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
    // 这些接口本身不需要鉴权，不参与 401 跳转（share 公开访问页 /p/{token} 系列免登录）
    const isPublic = /\/(login|health|config|share\/p\/)\b/.test(urlPath)
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
function regenerateInviteCode(groupId) { return request({ url: `/v1/group/${groupId}/invite-code`, method: 'POST' }) }
function updateGroup(groupId, data) { return request({ url: `/v1/group/${groupId}`, method: 'PUT', data }) }
function leaveGroup(groupId) { return request({ url: `/v1/group/${groupId}/leave`, method: 'POST' }) }
function transferLeader(groupId, newLeaderId) { return request({ url: `/v1/group/${groupId}/transfer-leader`, method: 'POST', data: { newLeaderId } }) }

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
function completeProject(projectId) { return request({ url: '/v1/project/' + projectId + '/complete', method: 'POST' }) }
function archiveProject(projectId) { return request({ url: '/v1/project/' + projectId + '/archive', method: 'PUT' }) }
// 成果展示（achievement）
function achievementList(projectId) { return request({ url: '/v1/achievement/list', params: { projectId } }) }
function achievementCreate(data) { return request({ url: '/v1/achievement/create', method: 'POST', data }) }
function achievementUpdate(id, data) { return request({ url: '/v1/achievement/' + id, method: 'PUT', data }) }
function achievementDelete(id) { return request({ url: '/v1/achievement/' + id, method: 'DELETE' }) }
// 结项分享（share）
function shareCreate(data) { return request({ url: '/v1/share/create', method: 'POST', data }) }
function shareList(projectId) { return request({ url: '/v1/share/list', params: { projectId } }) }
function shareRegenerate(shareId) { return request({ url: '/v1/share/' + shareId + '/regenerate', method: 'POST' }) }
function shareOverview(token) { return request({ url: '/v1/share/p/' + token + '/overview', noAuth: true }) }
function shareTimeline(token) { return request({ url: '/v1/share/p/' + token + '/timeline', noAuth: true }) }
function shareFiles(token) { return request({ url: '/v1/share/p/' + token + '/files', noAuth: true }) }
// 任务转交
function transferTask(taskId, data) { return request({ url: '/v1/task/' + taskId + '/transfer', method: 'POST', data }) }
// 阶段推进：完成当前阶段并激活下一阶段（返回 { allCompleted, currentStageId }）
function advanceStage(projectId, stageId) { return request({ url: '/v1/project/' + projectId + '/stage/' + stageId + '/advance', method: 'POST' }) }
// 阶段操作（挂 /v1/project 下）
function createStage(projectId, data) { return request({ url: `/v1/project/${projectId}/stage`, method: 'POST', data }) }
function updateStage(projectId, stageId, data) { return request({ url: `/v1/project/${projectId}/stage/${stageId}`, method: 'PUT', data }) }
function deleteStage(projectId, stageId) { return request({ url: `/v1/project/${projectId}/stage/${stageId}`, method: 'DELETE' }) }

// ============ 任务 ============
function myTasks(params) { return request({ url: '/v1/task/my-tasks', params }) }
function taskList(projectId, params) { return request({ url: '/v1/task/list', params: Object.assign({ projectId }, params) }) }
function taskDetail(taskId) { return request({ url: `/v1/task/${taskId}` }) }
function dueSoonTasks(days) { return request({ url: '/v1/task/due-soon', params: { days } }) }
function createTask(data) { return request({ url: '/v1/task/create', method: 'POST', data }) }
function updateTask(taskId, data) { return request({ url: `/v1/task/${taskId}`, method: 'PUT', data }) }
function deleteTask(taskId) { return request({ url: `/v1/task/${taskId}`, method: 'DELETE' }) }
function completeTask(taskId, data) { return request({ url: `/v1/task/${taskId}/complete`, method: 'POST', data: data || {} }) }

// ============ 消息 ============
function unreadCount() { return request({ url: '/v1/message/unread-count' }) }
function messageList(params) { return request({ url: '/v1/message/list', params }) }
function readAll() { return request({ url: '/v1/message/read-all', method: 'POST' }) }
function readMessage(id) { return request({ url: '/v1/message/' + id + '/read', method: 'POST' }) }
function deleteMessage(id) { return request({ url: '/v1/message/' + id, method: 'DELETE' }) }

// ============ 文件 ============
function fileList(params) { return request({ url: '/v1/file/list', params }) }
function fileDetail(fileId) { return request({ url: `/v1/file/${fileId}` }) }
function pendingAudit(pageNum, pageSize) { return request({ url: '/v1/file/pending-audit', params: { pageNum, pageSize } }) }
// 文件上传（勿复用上方 upload()：那是 /v1/upload 图片通用接口）。H5 用原生 FormData + XHR，微信端用 uni.uploadFile。
// params: { projectId, stageId(可空), remark(可空) }
function uploadFile(file, params = {}) {
  const token = getToken()
  // #ifdef H5
  return new Promise((resolve, reject) => {
    const fd = new FormData()
    fd.append('file', file)
    if (params.projectId != null) fd.append('projectId', params.projectId)
    if (params.stageId != null) fd.append('stageId', params.stageId)
    if (params.remark) fd.append('remark', params.remark)
    const xhr = new XMLHttpRequest()
    xhr.open('POST', BASE_URL + '/v1/file/upload')
    if (token) xhr.setRequestHeader('Authorization', 'Bearer ' + token)
    xhr.onload = function () {
      let body = null
      try { body = JSON.parse(xhr.responseText) } catch (e) { /* ignore */ }
      if (xhr.status >= 200 && xhr.status < 300 && body && body.code === 0) resolve(body.data)
      else reject(new Error((body && body.message) || ('上传失败 HTTP ' + xhr.status)))
    }
    xhr.onerror = function () { reject(new Error('网络错误，上传失败')) }
    xhr.send(fd)
  })
  // #endif
  // #ifndef H5
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: BASE_URL + '/v1/file/upload',
      filePath: file,
      name: 'file',
      header: token ? { Authorization: 'Bearer ' + token } : {},
      formData: {
        projectId: params.projectId,
        stageId: params.stageId,
        remark: params.remark
      },
      success: (res) => {
        try {
          const body = JSON.parse(res.data)
          if (body.code === 0) resolve(body.data)
          else reject(new Error(body.message || '上传失败'))
        } catch (e) { reject(new Error('上传失败')) }
      },
      fail: (err) => reject(new Error((err && err.errMsg) || '上传失败'))
    })
  })
  // #endif
}
// 文件操作（/v1/file）
function downloadFile(fileId) { return request({ url: `/v1/file/${fileId}/download` }) }
function previewFile(fileId) { return request({ url: `/v1/file/${fileId}/preview` }) }
function moveFile(fileId, targetStageId) { return request({ url: `/v1/file/${fileId}/move`, method: 'PUT', data: { targetStageId } }) }
function renameFile(fileId, name) { return request({ url: `/v1/file/${fileId}/rename`, method: 'PUT', data: { name } }) }
function favoriteFile(fileId) { return request({ url: `/v1/file/${fileId}/favorite`, method: 'POST' }) }
function batchDeleteFile(fileIds) { return request({ url: '/v1/file/batch-delete', method: 'DELETE', data: { fileIds } }) }
function batchMoveFile(fileIds, targetStageId) { return request({ url: '/v1/file/batch-move', method: 'PUT', data: { fileIds, targetStageId } }) }
// 版本（/v1/version）
function versionList(fileId) { return request({ url: '/v1/version/list', params: { fileId } }) }
function versionRollback(fileId, targetVersion) { return request({ url: `/v1/version/${fileId}/rollback`, method: 'POST', data: { targetVersion } }) }
// 审核通过（无请求体）
function approveFile(fileId) { return request({ url: `/v1/file/${fileId}/approve`, method: 'POST' }) }
// 审核驳回（需原因，后端 @Valid RejectFileRequest）
function rejectFile(fileId, reason) { return request({ url: `/v1/file/${fileId}/reject`, method: 'POST', data: { reason } }) }
// 重新提交（被驳回后换新文件重传；multipart，字段 file + remark）
function resubmitFile(fileId, file, remark = '') {
  const token = getToken()
  // #ifdef H5
  return new Promise((resolve, reject) => {
    const fd = new FormData()
    fd.append('file', file)
    if (remark) fd.append('remark', remark)
    const xhr = new XMLHttpRequest()
    xhr.open('POST', BASE_URL + `/v1/file/${fileId}/resubmit`)
    if (token) xhr.setRequestHeader('Authorization', 'Bearer ' + token)
    xhr.onload = function () {
      let body = null
      try { body = JSON.parse(xhr.responseText) } catch (e) { /* ignore */ }
      if (xhr.status >= 200 && xhr.status < 300 && body && body.code === 0) resolve(body.data)
      else reject(new Error((body && body.message) || ('重传失败 HTTP ' + xhr.status)))
    }
    xhr.onerror = function () { reject(new Error('网络错误，重传失败')) }
    xhr.send(fd)
  })
  // #endif
  // #ifndef H5
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: BASE_URL + `/v1/file/${fileId}/resubmit`,
      filePath: file,
      name: 'file',
      header: token ? { Authorization: 'Bearer ' + token } : {},
      formData: { remark },
      success: (res) => {
        try {
          const body = JSON.parse(res.data)
          if (body.code === 0) resolve(body.data)
          else reject(new Error(body.message || '重传失败'))
        } catch (e) { reject(new Error('重传失败')) }
      },
      fail: (err) => reject(new Error((err && err.errMsg) || '重传失败'))
    })
  })
  // #endif
}

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
  regenerateInviteCode,
  updateGroup,
  leaveGroup,
  transferLeader,
  projectList,
  projectDetail,
  projectProgress,
  projectActivities,
  projectTaskStats,
  projectStages,
  projectMembers,
  projectTemplateList,
  createProject,
  completeProject,
  archiveProject,
  achievementList,
  achievementCreate,
  achievementUpdate,
  achievementDelete,
  shareCreate,
  shareList,
  shareRegenerate,
  shareOverview,
  shareTimeline,
  shareFiles,
  transferTask,
  advanceStage,
  createStage,
  updateStage,
  deleteStage,
  myTasks,
  taskList,
  taskDetail,
  dueSoonTasks,
  createTask,
  updateTask,
  deleteTask,
  completeTask,
  unreadCount,
  messageList,
  readAll,
  readMessage,
  deleteMessage,
  discussionList,
  decisionList,
  announcementList,
  fileList,
  fileDetail,
  pendingAudit,
  uploadFile,
  downloadFile,
  previewFile,
  approveFile,
  rejectFile,
  resubmitFile,
  moveFile,
  renameFile,
  favoriteFile,
  batchDeleteFile,
  batchMoveFile,
  versionList,
  versionRollback
}
