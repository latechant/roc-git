/**
 * api.js - ROC-git 前端真实接口请求层
 *
 * 设计说明：
 *  - 基于 uni.request 封装，H5 与微信小程序双端通用。
 *  - baseURL 使用相对路径 '/api'，在 H5 部署时由 nginx 反向代理到后端 8080；
 *    小程序端可通过 manifest 配置或此处覆盖为完整域名。
 *  - 后端 JWT 拦截器要求除 health/config/login 外均需携带 token，
 *    本层在登录后自动把 token 存入缓存并在每次请求注入 Authorization。
 *  - 后端统一返回 { code, message, data }，code === 0 表示成功。
 */

// H5 用相对路径（nginx 反代到后端）；微信小程序无同源概念，必须用完整地址。
// 演示阶段走 HTTP，需在微信开发者工具勾选「不校验合法域名」；正式上线须改为已备案的 HTTPS 域名。
let BASE_URL = '/api'
// #ifdef MP-WEIXIN
BASE_URL = 'http://116.62.81.113/api'
// #endif

// 登录来源标识（后端据此决定是否调微信 code2session）
let LOGIN_SOURCE = 'h5'
// #ifdef MP-WEIXIN
LOGIN_SOURCE = 'mp-weixin'
// #endif
// 演示：前端默认登录所用昵称/头像（后端 login 接口会原样回显）
const DEMO_NICKNAME = '林知行'
const DEMO_AVATAR = 'https://cdn.rocgit.com/avatar/demo.png'

const TOKEN_KEY = 'rocgit_token'
const USER_KEY = 'rocgit_user'

// 正在跳转登录页时置位，避免多个并发 401 重复 reLaunch
let redirectingToLogin = false

function getToken() {
  try { return uni.getStorageSync(TOKEN_KEY) || '' } catch (e) { return '' }
}
function setToken(token) {
  try { uni.setStorageSync(TOKEN_KEY, token) } catch (e) { /* ignore */ }
}
function getCachedUser() {
  try { return uni.getStorageSync(USER_KEY) || null } catch (e) { return null }
}
function setCachedUser(user) {
  try { uni.setStorageSync(USER_KEY, user) } catch (e) { /* ignore */ }
}

/** 凭证失效：清理本地登录态并跳登录页（仅触发一次） */
function handleUnauthorized() {
  if (redirectingToLogin) return
  redirectingToLogin = true
  logout()
  uni.reLaunch({ url: '/pages/login/login' })
}

/**
 * 核心请求方法
 * @param {Object} opts { url, method, data, params, silent }
 * @returns {Promise<any>} 解析后的 data 字段；HTTP 失败或 code!=0 时 reject
 */
function request(opts = {}) {
  const { url, method = 'GET', data = null, params = null, silent = false } = opts

  let query = ''
  if (params && Object.keys(params).length) {
    const qs = Object.keys(params)
      .filter(k => params[k] !== undefined && params[k] !== null && params[k] !== '')
      .map(k => `${encodeURIComponent(k)}=${encodeURIComponent(params[k])}`)
      .join('&')
    if (qs) query = '?' + qs
  }

  return new Promise((resolve, reject) => {
    const urlPath = (BASE_URL + url).replace(/^https?:\/\/[^/]+/, '')
    // 这些接口本身不需要鉴权，不参与 401 跳转
    const isPublic = /\/(login|health|config)\b/.test(urlPath)
    uni.request({
      url: BASE_URL + url + query,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        Authorization: getToken() ? `Bearer ${getToken()}` : ''
      },
      success: (res) => {
        if (res.statusCode === 401 && !isPublic) {
          handleUnauthorized()
          reject(new Error('未登录或登录已过期'))
          return
        }
        if (res.statusCode >= 200 && res.statusCode < 300) {
          const body = res.data
          if (body && typeof body === 'object' && 'code' in body) {
            // 后端业务码表示未授权/凭证失效时同样清理登录态
            if (!isPublic && (body.code === 401 || body.code === 40101 || body.code === 1001)) {
              handleUnauthorized()
              reject(new Error(body.message || '未登录或登录已过期'))
              return
            }
            if (body.code === 0) {
              resolve(body.data)
            } else {
              const msg = body.message || '请求失败'
              if (!silent) uni.showToast({ title: String(msg), icon: 'none' })
              reject(new Error(msg))
            }
          } else {
            // 非标准结构，原样返回
            resolve(body)
          }
        } else {
          if (!silent) uni.showToast({ title: `请求失败(${res.statusCode})`, icon: 'none' })
          reject(new Error(`HTTP ${res.statusCode}`))
        }
      },
      fail: (err) => {
        if (!silent) uni.showToast({ title: '网络异常，请检查服务是否可用', icon: 'none' })
        reject(err)
      }
    })
  })
}

// ============ 认证 ============
/**
 * 微信登录（演示：前端直接调用后端 login 获取 token）
 * @returns {Promise<Object>} LoginVO { token, userId, nickname, ... }
 */
function login(payload = {}) {
  const body = {
    nickname: payload.nickname || DEMO_NICKNAME,
    avatarUrl: payload.avatarUrl || DEMO_AVATAR,
    code: payload.code || ('wx_user_' + Date.now()),
    source: payload.source || LOGIN_SOURCE
  }
  return request({ url: '/v1/user/login', method: 'POST', data: body, silent: true })
    .then(user => {
      if (user && user.token) {
        setToken(user.token)
        setCachedUser(user)
      }
      return user
    })
}

/** 确保已有有效 token；无 token 时返回 null（不再自动登录） */
function ensureLogin() {
  if (getToken()) return Promise.resolve(getCachedUser() || { token: getToken() })
  return Promise.resolve(null)
}

/** 是否已登录 */
function isLoggedIn() {
  return !!getToken()
}

/** 退出登录，清除 token 和用户缓存 */
function logout() {
  try { uni.removeStorageSync(TOKEN_KEY) } catch (e) { /* ignore */ }
  try { uni.removeStorageSync(USER_KEY) } catch (e) { /* ignore */ }
}

// ============ 通用 ============
function health() { return request({ url: '/v1/health', silent: true }) }
function getConfig() { return request({ url: '/v1/config', silent: true }) }
function upload() { return request({ url: '/v1/upload', method: 'POST', silent: true }) }

// ============ 用户 ============
function getProfile() { return request({ url: '/v1/user/profile' }) }
function updateProfile(data) { return request({ url: '/v1/user/profile', method: 'PUT', data }) }
function loginHistory() { return request({ url: '/v1/user/login-history' }) }
function contributionStats() { return request({ url: '/v1/user/contribution-stats' }) }

// ============ 小组 ============
function myGroups() { return request({ url: '/v1/group/my-list' }) }
function groupDetail(groupId) { return request({ url: `/v1/group/${groupId}` }) }
function groupStats(groupId) { return request({ url: `/v1/group/${groupId}/stats` }) }
function groupMembers(groupId, params) { return request({ url: `/v1/group/${groupId}/members`, params }) }
function groupMemberStats(groupId) { return request({ url: `/v1/group/${groupId}/member-stats` }) }
function createGroup(data) { return request({ url: '/v1/group/create', method: 'POST', data }) }
function joinGroup(data) { return request({ url: '/v1/group/join', method: 'POST', data }) }
function validateInviteCode(data) { return request({ url: '/v1/group/validate-invite-code', method: 'POST', data }) }

// ============ 项目 ============
function projectList(groupId) { return request({ url: '/v1/project/list', params: { groupId } }) }
function projectDetail(projectId) { return request({ url: `/v1/project/${projectId}` }) }
function projectProgress(projectId) { return request({ url: `/v1/project/${projectId}/progress` }) }
function projectActivities(projectId, params) { return request({ url: `/v1/project/${projectId}/activities`, params }) }
function projectTaskStats(projectId) { return request({ url: `/v1/project/${projectId}/task-stats` }) }
function projectStages(projectId) { return request({ url: `/v1/project/${projectId}/stage` }) }
function projectMembers(projectId) { return request({ url: `/v1/project/${projectId}/members` }) }
function projectTemplateList() { return request({ url: '/v1/project/template/list' }) }
function createProject(data) { return request({ url: '/v1/project/create', method: 'POST', data }) }

// ============ 任务 ============
function myTasks(params) { return request({ url: '/v1/task/my-tasks', params }) }
function taskList(projectId, params) { return request({ url: '/v1/task/list', params: Object.assign({ projectId }, params) }) }
function taskDetail(taskId) { return request({ url: `/v1/task/${taskId}` }) }
function dueSoonTasks(days) { return request({ url: '/v1/task/due-soon', params: { days } }) }
function createTask(data) { return request({ url: '/v1/task/create', method: 'POST', data }) }
function completeTask(taskId, data) { return request({ url: `/v1/task/${taskId}/complete`, method: 'POST', data: data || {} }) }

// ============ 消息 ============
function unreadCount() { return request({ url: '/v1/message/unread-count' }) }
function messageList(params) { return request({ url: '/v1/message/list', params }) }
function readAll() { return request({ url: '/v1/message/read-all', method: 'POST' }) }

// ============ 文件 ============
function fileList(params) { return request({ url: '/v1/file/list', params }) }
function fileDetail(fileId) { return request({ url: `/v1/file/${fileId}` }) }
function pendingAudit(pageNum, pageSize) { return request({ url: '/v1/file/pending-audit', params: { pageNum, pageSize } }) }

// ============ 研讨/决策/公告（扩展，接口已预留） ============
function discussionList(projectId, params) { return request({ url: '/v1/discussion/list', params: Object.assign({ projectId }, params) }) }
function decisionList(projectId, params) { return request({ url: '/v1/decision/list', params: Object.assign({ projectId }, params) }) }
function announcementList(params) { return request({ url: '/v1/announcement/list', params }) }

export default {
  BASE_URL,
  getToken,
  setToken,
  getCachedUser,
  request,
  login,
  ensureLogin,
  isLoggedIn,
  logout,
  health,
  getConfig,
  upload,
  getProfile,
  updateProfile,
  loginHistory,
  contributionStats,
  myGroups,
  groupDetail,
  groupStats,
  groupMembers,
  groupMemberStats,
  createGroup,
  joinGroup,
  validateInviteCode,
  projectList,
  projectDetail,
  projectProgress,
  projectActivities,
  projectTaskStats,
  projectStages,
  projectMembers,
  projectTemplateList,
  createProject,
  myTasks,
  taskList,
  taskDetail,
  dueSoonTasks,
  createTask,
  completeTask,
  unreadCount,
  messageList,
  readAll,
  discussionList,
  decisionList,
  announcementList,
  fileList,
  fileDetail,
  pendingAudit
}
