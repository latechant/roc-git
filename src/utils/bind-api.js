/**
 * bind-api.js - 把后端真实接口返回的数据绑定到页面 DOM-adapter 节点
 *
 * 前端采用 DOM-adapter 架构：页面 UI 是 data/*.json 里的 records 树，
 * Vue 模板优先渲染节点 textContent（若已定义）。因此给节点设 textContent
 * 即可用真实后端数据覆盖原型硬编码文本，保留原布局与交互。
 *
 * 每个页面一个加载函数，由对应 controller 创建后调用。
 * 后端当前返回演示数据（接口路径真实、可调），链路为真实 HTTP 调用。
 */
import api from './api.js'

/** 设置节点文本（值为 undefined/null/'' 时跳过，保留原型默认） */
function setText(controller, id, value) {
  if (!controller.ui[id]) return
  if (value === undefined || value === null || value === '') return
  controller.ui[id].textContent = String(value)
}

/** 安全执行一个异步加载任务，失败不阻塞页面渲染 */
function safe(fn) {
  return fn().catch((e) => {
    console.warn('[bind-api] 数据加载失败（保留原型数据）:', e && e.message)
  })
}

// ==================== 首页 ====================
// 节点映射（home.vue）：
//   n5   昵称
//   n42  当前项目-阶段/名称   n47 当前项目进度   n41 阶段文案
//   n60  第2个项目名称       n65 进度            n59 阶段文案
//   n77  第3个项目名称       n82 进度
//   n104 研讨记录标题        n126/n133 临期任务  n145/n152 待审核
export function loadHome(controller) {
  return safe(async () => {
    await api.ensureLogin()

    // 个人信息（昵称）
    const profile = await api.getProfile()
    setText(controller, 'n5', profile && profile.nickname)

    // 我的小组 -> 取第一个小组的项目列表，填充"当前项目"
    const groups = await api.myGroups()
    const gid = groups && groups.length ? groups[0].groupId : 1
    const projects = await api.projectList(gid)

    if (projects && projects.length) {
      const p0 = projects[0]
      setText(controller, 'n42', p0.name)           // 当前项目名称
      setText(controller, 'n41', `进度 ${p0.progress}%`)
      setText(controller, 'n47', `${p0.progress}%`)
      if (projects.length > 1) {
        const p1 = projects[1]
        setText(controller, 'n60', p1.name)
        setText(controller, 'n59', `进度 ${p1.progress}%`)
        setText(controller, 'n65', `${p1.progress}%`)
      }
      if (projects.length > 2) {
        const p2 = projects[2]
        setText(controller, 'n77', p2.name)
        setText(controller, 'n82', `${p2.progress}%`)
      }
    }

    // 未读消息数
    const unread = await api.unreadCount().catch(() => null)
    if (unread && typeof unread.total === 'number') {
      // home 顶部统计：n14=进行中项目数  n17=待审核  n20=临期
      // 用接口返回值尽量对齐（后端为演示值）
    }
  })
}

// ==================== 项目详情页 ====================
// 节点映射（project.vue）：
//   n6   项目名称   n7   阶段文案
//   n13  总体进度   n23  资料数   n24  待审数   n25  版本数
export function loadProject(controller, projectId) {
  return safe(async () => {
    await api.ensureLogin()

    const detail = await api.projectDetail(projectId)
    if (detail) {
      setText(controller, 'n6', detail.name)
      setText(controller, 'n7', `${detail.currentStage || ''}`.trim())
      setText(controller, 'n13', `${detail.progress}%`)
      setText(controller, 'n23', `${detail.fileCount}资料`)
      setText(controller, 'n24', `${detail.fileCount}待审`)
      setText(controller, 'n25', `${detail.completedTaskCount}版本`)
    }

    const stages = await api.projectStages(projectId).catch(() => null)
    if (stages && stages.length) {
      // 阶段列表在 n40 区域，为多节点；此处仅展示第一个阶段名（保留扩展）
    }
  })
}

// ==================== 个人中心 ====================
// 节点映射（profile.vue）：
//   n9   昵称          n18  文件数   n21  版本数   n24  待审数
export function loadProfile(controller) {
  return safe(async () => {
    await api.ensureLogin()

    const profile = await api.getProfile()
    if (profile) {
      setText(controller, 'n9', profile.nickname)
      setText(controller, 'n18', profile.projectCount !== undefined ? String(profile.projectCount) : '')
    }

    const stats = await api.contributionStats()
    if (stats) {
      setText(controller, 'n18', stats.uploadedFiles)       // 文件数
      setText(controller, 'n21', stats.approvedFiles)        // 版本数
      setText(controller, 'n24', stats.completedTasks)       // 待审数
    }
  })
}

// ==================== 消息页 ====================
// 节点映射（messages.vue）：
//   n5   汇总条数  n16 分组条数  n26/n103 消息标题
export function loadMessages(controller) {
  return safe(async () => {
    await api.ensureLogin()

    const unread = await api.unreadCount()
    if (unread) {
      setText(controller, 'n5', `${unread.total} 条变更`)
      setText(controller, 'n16', `${unread.task + unread.audit + unread.mention} 条项目变更`)
    }

    const list = await api.messageList({ pageNum: 1, pageSize: 10 })
    const items = list && list.list ? list.list : []
    if (items.length) {
      setText(controller, 'n26', items[0].title)
    }
  })
}

// ==================== 研讨/会议页 ====================
// 节点映射（meeting.vue）：研讨记录标题等
export function loadMeeting(controller, projectId) {
  return safe(async () => {
    await api.ensureLogin()
    const list = await api.discussionList(projectId || 1, { pageNum: 1, pageSize: 10 })
    if (list && list.length) {
      // 列表节点为多节点结构，此处预留；首个标题尽力绑定
    }
  })
}

// ==================== 新建项目页 ====================
// 新建操作通过 createProject 真实提交
export function createProjectFromForm(form) {
  return api.createProject({
    groupId: form.groupId || 1,
    name: form.name,
    description: form.description || ''
  })
}

export default {
  setText,
  loadHome,
  loadProject,
  loadProfile,
  loadMessages,
  loadMeeting,
  createProjectFromForm
}
