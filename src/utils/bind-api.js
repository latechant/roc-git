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
import { textAvatar } from './avatar.js'

/** 设置节点文本（值为 undefined/null/'' 时跳过，保留原型默认） */
function setText(controller, id, value) {
  if (!controller.ui[id]) return
  if (value === undefined || value === null || value === '') return
  controller.ui[id].textContent = String(value)
}

/** 设置进度条填充宽度（0-100 → style.width；节点不存在跳过） */
function setProgress(controller, id, pct) {
  const node = controller.ui && controller.ui[id]
  if (!node) return
  const n = Number(pct)
  node.style.width = (Number.isFinite(n) ? Math.max(0, Math.min(100, n)) : 0) + '%'
}

/** 设置节点文字头像：首字 + 稳定底色（节点不存在则跳过） */
function setAvatar(controller, id, name) {
  if (!controller.ui[id]) return
  const a = textAvatar(name)
  if (!a.char) return
  controller.ui[id].textContent = a.char
  try {
    controller.ui[id].style.background = a.color
  } catch (e) { /* 节点 style 不可写时仅保留首字 */ }
}

/** 隐藏节点：设置 removed=true，模板 v-if="!ui.nX.removed" 即不渲染 */
function hideNode(controller, id) {
  if (!controller.ui[id]) return
  controller.ui[id].removed = true
}

/** 空状态提示：浅灰小字（比标题小，不抢视觉） */
function setEmptyState(controller, id, text) {
  if (!controller.ui[id]) return
  controller.ui[id].textContent = String(text)
  try {
    const s = controller.ui[id].style
    s.color = '#bbb'
    s.fontSize = '13px'
    s.textAlign = 'center'
    s.padding = '16px 0'
  } catch (e) { /* style 不可写时仅保留文本 */ }
}

/**
 * 显式清空节点文本：置为空字符串 ''。
 * 模板中 v-if="ui.nX.textContent !== undefined" 在 '' 时为 true，
 * 从而显示空文本而非 v-else 里的原型硬编码假数据。节点不存在则跳过。
 */
function clearText(controller, id) {
  if (!controller.ui[id]) return
  controller.ui[id].textContent = ''
}

/** 批量清空一组业务数据节点（UI 静态文案节点请勿传入） */
function clearNodes(controller, ids) {
  ids.forEach((id) => clearText(controller, id))
}

/** 安全执行一个异步加载任务，失败不阻塞页面渲染 */
function safe(fn) {
  return fn().catch((e) => {
    console.warn('[bind-api] 数据加载失败（保留原型数据）:', e && e.message)
  })
}

/** 检查登录状态，未登录则跳转登录页。返回 true=已登录，false=未登录已跳转 */
async function requireLogin() {
  const user = await api.ensureLogin()
  if (!user) {
    uni.reLaunch({ url: '/pages/login/login' })
    return false
  }
  return true
}

// ==================== 首页 ====================
// 节点映射（home.vue）：
//   n5   昵称
//   n14/n17/n20  顶部统计数字（进行中/待审核/临期）
//   n42  当前项目-阶段/名称   n47 当前项目进度   n41 阶段文案   n43 课程
//   n60  第2个项目名称       n65 进度            n59 阶段文案   n61 课程
//   n77  第3个项目名称       n82 进度            n76 阶段文案   n78 课程
//   n52/n53/n54/n55 项目1成员与资料数   n70/n71 项目2成员   n87/n88 项目3成员
//   n103/n104/n106 研讨记录1   n112/n113/n115 研讨记录2
//   n120/n126/n127/n128 临期任务1   n133/n134/n135 临期任务2
//   n145/n146 待审核1   n152/n153 待审核2   n162/n163 归档
// 静态标题不清：n32 当前项目 / n96 研讨记录 / n119 临期任务 / n138 待审核 /
//               n157 归档 / n169 首页 / n172 消息 / n176 我的
const HOME_CLEAR_IDS = [
  'n4', 'n5', 'n14', 'n17', 'n20',
  'n41', 'n42', 'n43', 'n47', 'n52', 'n53', 'n54', 'n55',
  'n59', 'n60', 'n61', 'n65', 'n70', 'n71', 'n72',
  'n76', 'n77', 'n78', 'n82', 'n87', 'n88',
  'n103', 'n104', 'n106', 'n112', 'n113', 'n115',
  'n120', 'n126', 'n127', 'n128', 'n133', 'n134', 'n135',
  'n145', 'n146', 'n152', 'n153',
  'n162', 'n163'
]

export function loadHome(controller) {
  return safe(async () => {
    const ok = await requireLogin()
    if (!ok) return

    // 先清空本页所有业务数据节点：接口失败或数据为空时保持空态，不回退原型假数据
    clearNodes(controller, HOME_CLEAR_IDS)

    // 今日日期
    const now = new Date()
    const wd = ['周日','周一','周二','周三','周四','周五','周六'][now.getDay()]
    setText(controller, 'n4', `${wd}，${now.getMonth()+1}月${now.getDate()}日`)

    // 个人信息（昵称）
    const profile = await api.getProfile().catch(() => null)
    setText(controller, 'n5', profile && profile.nickname)

    // 我的小组 -> 取第一个小组
    const groups = await api.myGroups().catch(() => null)
    const gid = groups && groups.length ? groups[0].groupId : null
    const projects = gid ? await api.projectList(gid).catch(() => null) : null
    const list = projects || []
    // 小组项目列表挂到 controller，供首页"当前项目"跳转取 id
    controller.rocProjects = list

    // 顶部统计：进行中项目数 / 临期任务数 / 待审核文件数
    setText(controller, 'n14', String(list.filter(p => p.status === 'active').length))
    const dueTasks = await api.dueSoonTasks(7).catch(() => null)
    const dueArr = Array.isArray(dueTasks) ? dueTasks : (dueTasks && dueTasks.tasks) || []
    setText(controller, 'n20', String(dueArr.length))
    const pending = await api.pendingAudit(1, 10).catch(() => null)
    const pendingTotal = (pending && pending.total) || 0
    setText(controller, 'n17', String(pendingTotal))

    // 小组成员（仅有组时才查）
    const members = gid ? await api.groupMembers(gid).catch(() => null) : null
    const memberList = (members && members.members) || []
    const memberNames = memberList.map((m) => m.nickname)

    // 项目1：名称/进度/阶段文案/成员头像
    if (list.length) {
      const p0 = list[0]
      setText(controller, 'n42', p0.name)
      setText(controller, 'n41', `进度 ${p0.progress}%`)
      setProgress(controller, 'n49', p0.progress)
      setText(controller, 'n47', `${p0.progress}%`)
      setAvatar(controller, 'n52', memberNames[0])
      setAvatar(controller, 'n53', memberNames[1])
      setAvatar(controller, 'n54', memberNames[2])
      setText(controller, 'n55', `${members.total || memberNames.length}位成员`)
    } else {
      hideNode(controller, 'n33')
      hideNode(controller, 'n36')
      hideNode(controller, 'n89')
      hideNode(controller, 'n90')
      controller.ui.n34.className = ''
      controller.ui.n35.style.justifyContent = 'center'
      setEmptyState(controller, 'n35', '暂无项目')
    }

    // 项目2
    if (list.length > 1) {
      const p1 = list[1]
      setText(controller, 'n60', p1.name)
      setText(controller, 'n59', `进度 ${p1.progress}%`)
      setText(controller, 'n65', `${p1.progress}%`)
      setProgress(controller, 'n67', p1.progress)
      setAvatar(controller, 'n70', memberNames[0])
      setAvatar(controller, 'n71', memberNames[1])
    } else {
      hideNode(controller, 'n56')
    }

    // 项目3
    if (list.length > 2) {
      const p2 = list[2]
      setText(controller, 'n77', p2.name)
      setText(controller, 'n76', p2.status === 'active' ? '进行中' : '筹备中')
      setText(controller, 'n82', `${p2.progress}%`)
      setProgress(controller, 'n84', p2.progress)
      setAvatar(controller, 'n87', memberNames[0])
      setText(controller, 'n88', memberList[0] && memberList[0].role === 'leader' ? '组长' : '组员')
    } else {
      hideNode(controller, 'n73')
    }

    // 研讨记录（取第一个项目的研讨列表前2条）
    let dl = []
    if (list.length) {
      const pid0 = list[0].projectId
      const discussions = await api.discussionList(pid0, { pageNum: 1, pageSize: 5 }).catch(() => null)
      dl = (discussions && discussions.list) || (discussions && discussions.tasks) || []
    }
    if (dl.length) {
      const d0 = dl[0]
      setText(controller, 'n103', d0.title)
      setText(controller, 'n104', `${d0.particitantCount || ''}人 · ${(d0.discussionTime || '').slice(5, 10)}`)
      setText(controller, 'n106', `${d0.decisionCount || 0} 待落地`)
    } else {
      hideNode(controller, 'n97')
      hideNode(controller, 'n99')
      setEmptyState(controller, 'n98', '暂无研讨记录')
    }
    if (dl.length > 1) {
      const d1 = dl[1]
      setText(controller, 'n112', d1.title)
      setText(controller, 'n113', `${d1.particitantCount || ''}人 · ${(d1.discussionTime || '').slice(5, 10)}`)
      setText(controller, 'n115', '已全部落地')
    } else {
      hideNode(controller, 'n108')
    }

    // 临期任务（前2条）
    if (dueArr.length) {
      const t0 = dueArr[0]
      setText(controller, 'n126', t0.name)
      setText(controller, 'n127', `${t0.projectName || ''} · 还剩 ${t0.daysLeft || ''}天`)
      setText(controller, 'n128', `${t0.daysLeft}天`)
      setText(controller, 'n120', String(dueArr.length) + '项')
    } else {
      setText(controller, 'n120', '0 项')
      setEmptyState(controller, 'n121', '暂无临期任务')
    }
    if (dueArr.length > 1) {
      const t1 = dueArr[1]
      setText(controller, 'n133', t1.name)
      setText(controller, 'n134', `${t1.projectName || ''} · 还剩 ${t1.daysLeft || ''}天`)
      setText(controller, 'n135', `${t1.daysLeft}天`)
    } else {
      hideNode(controller, 'n129')
    }

    // 待审核文件（前2条）
    const pendingList = (pending && pending.list) || []
    if (pendingList.length) {
      const f0 = pendingList[0]
      setText(controller, 'n145', `${f0.uploaderName || ''} · ${f0.name || ''}`)
      setText(controller, 'n146', (f0.createdAt || '').slice(11, 16))
    } else {
      hideNode(controller, 'n139')
      hideNode(controller, 'n141')
      setEmptyState(controller, 'n140', '暂无待审核文件')
    }
    if (pendingList.length > 1) {
      const f1 = pendingList[1]
      setText(controller, 'n152', `${f1.uploaderName || ''} · ${f1.name || ''}`)
      setText(controller, 'n153', (f1.createdAt || '').slice(11, 16))
    } else {
      hideNode(controller, 'n148')
    }

    // 归档项目（取已完成项目第一个）
    const archived = list.filter(p => p.status === 'completed' || p.status === 'archived')
    if (archived.length) {
      const a0 = archived[0]
      setText(controller, 'n162', a0.name)
      setText(controller, 'n163', `${a0.fileCount || 0} 份成果 · ${a0.versionCount || 0} 版本`)
    } else {
      controller.ui.n158.className = ''
      controller.ui.n158.attrs.href = ''
      setEmptyState(controller, 'n158', '暂无归档项目')
    }

    // 未读消息角标
    const unread = await api.unreadCount().catch(() => null)
    if (unread && typeof unread.total === 'number' && unread.total > 0) {
      setText(controller, 'n173', String(unread.total))
    }
  })
}

// ==================== 项目详情页 ====================
// 节点映射（project.vue）：头部 + 文件列表 + 作业阶段 + 任务分工 + 小组成员 +
//   溯源时间线 + 版本对比/多维筛选/标记闲置/批量清理等弹层里的业务数据。
// 静态标签不清：总体进度/文件/阶段/成员/溯源等列名与按钮、格式/状态/排序等
//   固定筛选选项词、上传资料/项目设置弹层的固定菜单项。
// ==================== 项目详情页：文件/阶段/任务动态行 ====================

// 文件列表原型假行（parent=n60 的 7 行 file-row）
const FILE_ROW_IDS = ['n61', 'n78', 'n95', 'n112', 'n124', 'n137', 'n154']

// 阶段原型行（n173=space-y-3 容器内，n174/n184/n224 是阶段行本体；n600/n235 是入口按钮保留）
const STAGE_ROW_IDS = ['n174', 'n184', 'n224']

// 阶段状态文案映射（后端 StageVO.status：pending/in_progress/completed）
function stageStatusText(status) {
  const map = {
    pending: ['bg-slate-100 text-slate-500', '待开始'],
    in_progress: ['bg-blue-50 text-blue-600', '进行中'],
    completed: ['bg-emerald-50 text-emerald-600', '已完成']
  }
  const m = map[status] || ['bg-slate-100 text-slate-500', status || '待开始']
  return { cls: m[0], text: m[1] }
}

// 任务状态徽章映射（后端 TaskVO.status：pending/in_progress/completed/abandoned）
function taskStatusText(status) {
  const map = {
    pending: ['text-slate-400', '待开始'],
    in_progress: ['text-blue-600', '进行中'],
    completed: ['text-emerald-600', '已完成'],
    abandoned: ['text-rose-400', '已放弃']
  }
  const m = map[status] || ['text-slate-400', status || '待开始']
  return { cls: m[0], text: m[1] }
}

// 任务行 HTML（嵌在阶段卡内，负责人首字头像 + 名称 + 截止 + 状态）
function buildTaskRowHtml(t) {
  const badge = taskStatusText(t.status)
  const av = (t.assigneeName || '?').slice(0, 1)
  return '<div class="flex items-center gap-2 bg-slate-50 rounded-xl px-3 py-2">' +
    '<span class="w-5 h-5 rounded-full bg-sky-400 text-[9px] font-bold text-white flex items-center justify-center shrink-0">' + av + '</span>' +
    '<div class="flex-1 min-w-0">' +
    '<p class="text-[12px] text-slate-900 truncate">' + (t.name || '未命名任务') + '</p>' +
    (t.deadline ? '<p class="text-[10px] text-slate-400">截止 ' + String(t.deadline).slice(5, 10) + '</p>' : '') +
    '</div>' +
    '<span class="text-[10px] ' + badge.cls + '">' + badge.text + '</span>' +
    '</div>'
}

// 构造阶段行 HTML（含进度条 + 内嵌任务列表；appendHtml 解析内联 style）
function buildStageRowHtml(s, index, tasks) {
  const no = (index < 9 ? '0' : '') + (index + 1)
  const badge = stageStatusText(s.status)
  const sp = s.progress != null ? s.progress : Math.round((s.completedTaskCount || 0) / ((s.taskCount || s.totalTaskCount) || 1) * 100)
  const range = [s.startDate, s.endDate].filter(Boolean).map(d => String(d).slice(5)).join(' ~ ')
  const taskHtml = (tasks && tasks.length)
    ? '<div class="mt-3 pt-3 border-t border-slate-100 space-y-2">' +
      '<p class="text-[12px] font-semibold text-slate-700">任务分工</p>' +
      tasks.map(t => buildTaskRowHtml(t)).join('') +
      '</div>'
    : ''
  return '<div class="bg-white rounded-2xl p-4 shadow-sm border border-slate-100">' +
    '<div class="flex items-center gap-3">' +
    '<span class="w-6 h-6 rounded-full bg-blue-50 text-[11px] font-semibold text-blue-600 flex items-center justify-center shrink-0">' + no + '</span>' +
    '<div class="flex-1 min-w-0">' +
    '<p class="text-[13px] font-medium text-slate-900">' + (s.name || '未命名阶段') + '</p>' +
    (range ? '<p class="text-[11px] text-slate-400 mt-0.5">' + range + '</p>' : '') +
    '</div>' +
    '<span class="px-2 py-0.5 rounded-full ' + badge.cls + ' text-[10px] font-semibold">' + badge.text + '</span>' +
    '</div>' +
    '<div class="mt-3 h-1.5 bg-slate-100 rounded-full">' +
    '<div class="h-full bg-blue-600 rounded-full" style="width:' + (sp || 0) + '%"></div>' +
    '</div>' +
    taskHtml +
    '</div>'
}

// 文件状态徽章映射（后端 FileVO.status：pending/approved/rejected）
function fileStatusBadge(status) {
  const map = {
    pending: ['bg-amber-50 text-amber-600', '待审核'],
    approved: ['bg-emerald-50 text-emerald-600', '已通过'],
    rejected: ['bg-rose-50 text-rose-600', '已驳回']
  }
  const m = map[status] || ['bg-slate-100 text-slate-500', status || '未知']
  return { cls: m[0], text: m[1] }
}

function formatFileSize(size) {
  if (!size && size !== 0) return ''
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(0) + ' KB'
  return (size / 1024 / 1024).toFixed(1) + ' MB'
}

function formatFileTime(t) {
  if (!t) return ''
  return String(t).slice(5, 10)
}

// 构造文件行 HTML（DOM-adapter appendFragment 解析 → ui.n60.appended）
function buildFileRowHtml(f) {
  const badge = fileStatusBadge(f.status)
  const meta = [
    f.uploader && f.uploader.nickname,
    f.version ? 'v' + f.version : '',
    formatFileSize(f.size),
    f.stageName
  ].filter(Boolean).join(' · ')
  return '<div class="file-row bg-white rounded-2xl p-3.5 shadow-sm border border-slate-100" data-status="' + (f.status || '') + '">' +
    '<div class="flex items-center gap-3">' +
    '<span class="w-9 h-9 rounded-xl bg-sky-50 flex items-center justify-center shrink-0">📄</span>' +
    '<div class="flex-1 min-w-0">' +
    '<p class="text-[13px] font-medium text-slate-900 truncate">' + (f.name || '未命名文件') + '</p>' +
    '<p class="text-[11px] text-slate-400 mt-0.5">' + (meta || ' ') + '</p>' +
    '</div>' +
    '<span class="px-2 py-0.5 rounded-full ' + badge.cls + ' text-[10px] font-semibold">' + badge.text + '</span>' +
    '</div>' +
    '</div>'
}

const PROJECT_CLEAR_IDS = [
  // 头部：项目名/阶段/进度/成员头像/资料·待审·版本数
  'n6', 'n7', 'n13',
  'n18', 'n19', 'n20', 'n21',
  'n23', 'n24', 'n25',
  // 工具区计数
  'n40', 'n55',
  // 文件列表（名称/元信息/状态）
  'n68', 'n69', 'n70',
  'n85', 'n86', 'n87',
  'n102', 'n103', 'n104',
  'n119', 'n120', 'n121',
  'n129', 'n130', 'n131',
  'n144', 'n145', 'n146',
  'n158',
  'n166', 'n167',
  // 作业阶段（序号/名称/状态/时间/进度）
  'n177', 'n178', 'n182', 'n183',
  'n187', 'n188', 'n194', 'n195', 'n199',
  'n227', 'n228', 'n234',
  // 任务分工
  'n207', 'n208', 'n209',
  'n213', 'n214', 'n215',
  'n219', 'n220', 'n221',
  // 小组成员（头像/姓名/角色/徽章）
  'n261', 'n263', 'n264', 'n265',
  'n267', 'n269', 'n270', 'n271',
  'n273', 'n275', 'n276', 'n277',
  'n279', 'n281', 'n282',
  // 溯源时间线
  'n299', 'n300',
  'n308', 'n309', 'n310',
  'n318', 'n319',
  'n327', 'n328',
  'n335', 'n336',
  // 版本对比弹层
  'n346', 'n347', 'n350', 'n351', 'n352',
  'n357', 'n358', 'n362',
  // 多维筛选弹层里的阶段/上传者人名选项
  'n427', 'n428', 'n429',
  'n434', 'n436', 'n438',
  // 标记闲置弹层的文件预览
  'n464', 'n465',
  // 批量清理弹层标题（含项数）
  'n485'
]

export function loadProject(controller, projectId) {
  return safe(async () => {
    const ok = await requireLogin()
    if (!ok) return

    // 先清空业务数据节点，接口失败/无数据时保持空态
    clearNodes(controller, PROJECT_CLEAR_IDS)

    const detail = await api.projectDetail(projectId)
    if (detail) {
      setText(controller, 'n6', detail.name)
      setText(controller, 'n7', `${detail.currentStage || ''}`.trim())
      setText(controller, 'n13', `${detail.progress}%`)
      setProgress(controller, 'n15', detail.progress)
      setText(controller, 'n23', `${detail.fileCount}资料`)
      setText(controller, 'n24', `${detail.fileCount}待审`)
      setText(controller, 'n25', `${detail.completedTaskCount}版本`)
    }

    // 文件列表：隐藏原型假行 → 按真实文件动态渲染（appendFragment 追加进 n60.appended）
    FILE_ROW_IDS.forEach((id) => hideNode(controller, id))
    const files = await api.fileList({ projectId }).catch(() => null)
    const fileArr = (files && (files.list || files.records)) || (Array.isArray(files) ? files : [])
    const fileListEl = controller.adapters.document.getElementById('fileList')
    if (fileListEl && fileArr.length) {
      fileArr.forEach((f) => fileListEl.insertAdjacentHTML('beforeend', buildFileRowHtml(f)))
    }

    const stages = await api.projectStages(projectId).catch(() => null)
    const stageArr = Array.isArray(stages) ? stages : (stages && (stages.list || stages.records)) || []
    // 隐藏原型阶段行 → 按真实阶段动态渲染（阶段名/序号/状态/起止/进度条 + 内嵌任务列表）
    STAGE_ROW_IDS.forEach((id) => hideNode(controller, id))
    const taskRes = await api.taskList(projectId, { pageNum: 1, pageSize: 50 }).catch(() => null)
    const taskArr = (taskRes && (taskRes.list || taskRes.records || taskRes.tasks)) || (Array.isArray(taskRes) ? taskRes : [])
    if (stageArr.length) {
      const stageTaskMap = {}
      taskArr.forEach((t) => {
        const sid = t.stageId
        if (sid == null) return
        ;(stageTaskMap[sid] = stageTaskMap[sid] || []).push(t)
      })
      stageArr.forEach((s, i) => controller.appendHtml('n173', buildStageRowHtml(s, i, stageTaskMap[s.stageId])))
      // 作业阶段区标题：取第一阶段进度
      const s0 = stageArr[0]
      const sp = s0.progress != null ? s0.progress : Math.round((s0.completedTaskCount || 0) / ((s0.taskCount || s0.totalTaskCount) || 1) * 100)
      setText(controller, 'n199', sp + '%')
      setProgress(controller, 'n201', sp)
    }

    // 项目所属小组的成员文字头像（前 4 位：头像首字/姓名/角色）
    if (detail && detail.groupId) {
      const members = await api.groupMembers(detail.groupId).catch(() => null)
      if (members && members.members && members.members.length) {
        const slots = [
          { av: 'n261', nm: 'n263', role: 'n264' },
          { av: 'n267', nm: 'n269', role: 'n270' },
          { av: 'n273', nm: 'n275', role: 'n276' },
          { av: 'n279', nm: 'n281', role: 'n282' }
        ]
        members.members.slice(0, 4).forEach((m, i) => {
          setAvatar(controller, slots[i].av, m.nickname)
          setText(controller, slots[i].nm, m.nickname)
          setText(controller, slots[i].role, m.role === 'leader' ? '组长' : '组员')
        })
      }
    }
  })
}

// ==================== 个人中心 ====================
// 节点映射（profile.vue）：
//   n7 头像首字  n9 昵称  n10 副标题(专业·项目数)  n12/n13 角色徽章
//   n18 文件数  n21 版本数  n24 待审数  n27 归档数
//   n34/n35 待审提醒文案  n44 进行中项目数  n56 批量归档提示
//   n71 指导小组数  n78 过程评价提示
//   归档弹窗：n119/n120、n125/n126、n131/n132、n137/n138 四个归档项
//            n145/n148/n151/n154/n160 归档包含统计  n161 确认归档按钮
// 静态标签不清：n2 个人空间 / n3 我的 / n19 文件 / n22 版本 / n25 待审 /
//   n28 归档 / n38 我的项目 / n43 进行中项目 / n49 操作记录 / n55 期末归档 /
//   n61 存储与下载 / n64 教学管理 / n70 我指导的小组 / n77 过程性评价 /
//   n81 通知 / n85/n86/n91/n92 通知渠道说明 / n96 退出登录 / 底部导航
const PROFILE_CLEAR_IDS = [
  'n7', 'n9', 'n10', 'n12', 'n13',
  'n18', 'n21', 'n24', 'n27',
  'n34', 'n35', 'n44', 'n56', 'n71', 'n78',
  'n119', 'n120', 'n125', 'n126', 'n131', 'n132', 'n137', 'n138',
  'n145', 'n148', 'n151', 'n154', 'n160', 'n161'
]

export function loadProfile(controller) {
  return safe(async () => {
    const ok = await requireLogin()
    if (!ok) return

    // 先清空业务数据节点，接口失败/无数据时保持空态
    clearNodes(controller, PROFILE_CLEAR_IDS)

    const profile = await api.getProfile()
    if (profile) {
      setAvatar(controller, 'n7', profile.nickname)          // 头像首字
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
//   n5 汇总条数  n16 本周分组条数  n17 分类统计
//   消息项1(待审核)：n26 标题  n27 状态标签  n28 描述  n29 时间·项目
//   消息项2(临期)：n36 标题  n37 状态标签  n38 描述  n39 时间·项目
//   消息项3(成果)：n46 标题  n47 版本标签  n48 描述  n49 时间·项目
//   消息项4(研讨,昨天)：n58 标题  n59 描述  n60 时间·项目
//   消息项5(系统提醒,昨天)：n65 标题  n66 描述  n67 时间
//   消息项6(更早)：n74 标题  n75 描述  n76 时间·项目
// 静态标签不清：n4 消息 / n6 全部已读 / n8~n12 分类按钮 /
//   n19 今天 / n52 昨天 / n69 更早 等日期分隔符 / 底部导航
const MESSAGES_CLEAR_IDS = [
  'n5', 'n16', 'n17',
  'n26', 'n27', 'n28', 'n29',
  'n36', 'n37', 'n38', 'n39',
  'n46', 'n47', 'n48', 'n49',
  'n58', 'n59', 'n60',
  'n65', 'n66', 'n67',
  'n74', 'n75', 'n76'
]

export function loadMessages(controller) {
  return safe(async () => {
    const ok = await requireLogin()
    if (!ok) return

    // 先清空业务数据节点，接口失败/无数据时保持空态
    clearNodes(controller, MESSAGES_CLEAR_IDS)

    const unread = await api.unreadCount()
    const total = unread && typeof unread.total === 'number' ? unread.total : 0
    if (total > 0) {
      setText(controller, 'n5', `${total} 条变更`)
      setText(controller, 'n16', `${unread.task + unread.audit + unread.mention} 条项目变更`)
    } else {
      // 0 条变更：隐藏标题下的汇总条数，避免 "0 条变更" 挂出
      hideNode(controller, 'n5')
    }

    const list = await api.messageList({ pageNum: 1, pageSize: 10 })
    const items = list && list.list ? list.list : []
    if (items.length) {
      setText(controller, 'n26', items[0].title)
    } else {
      // 无数据空态：隐藏统计卡(n14)、三个分组容器(n18/n51/n68)及其全部卡片本体，
      // 在滚动容器内用 setEmptyState 显示浅灰提示
      ;['n14', 'n18', 'n51', 'n68', 'n21', 'n31', 'n41', 'n54', 'n61', 'n70'].forEach((id) => hideNode(controller, id))
      setEmptyState(controller, 'n77', '暂无消息，去完成第一个项目吧')
    }
  })
}

// ==================== 研讨/会议页 ====================
// 节点映射（meeting.vue）：三份研讨记录 + 未落地事项 + 新建记录表单里的人名 chip
//   n7   项目副标题（如"社区调研报告 · 3 次"）
//   记录1(第3次)：n21 标题  n22 时间  n23 参与人数  n27 议题  n30 最终决策
//                 n36/n37/n38 参与人首字  n44/n47/n50 已落地更新
//   记录2(第2次)：n56 标题  n57 时间  n58 参与人数  n62 议题  n65 最终决策
//                 n71/n72/n73 参与人首字  n79/n82/n85 已落地更新
//   记录3(第1次)：n91 标题  n92 时间  n93 参与人数  n97 议题  n100 最终决策
//                 n102/n103/n104/n105 参与人首字
//   未落地事项：n113/n114、n120/n121
//   新建记录表单已选参与人 chip：n153/n155/n157
// 静态标签不清：n6 研讨记录 / n11 全部记录 / n12 结论待落地 /
//   n26/n61/n96 研讨议题 / n29/n64/n99 最终决策 / n34/n69/n101 参与人员 /
//   n40/n75 已落地更新 / n107 提示文案 / n126 新建研讨记录按钮 /
//   n130~n169 新建表单标签与按钮
const MEETING_CLEAR_IDS = [
  'n7',
  'n21', 'n22', 'n23', 'n27', 'n30', 'n36', 'n37', 'n38', 'n44', 'n47', 'n50',
  'n56', 'n57', 'n58', 'n62', 'n65', 'n71', 'n72', 'n73', 'n79', 'n82', 'n85',
  'n91', 'n92', 'n93', 'n97', 'n100', 'n102', 'n103', 'n104', 'n105',
  'n113', 'n114', 'n120', 'n121',
  'n153', 'n155', 'n157'
]

export function loadMeeting(controller, projectId) {
  return safe(async () => {
    const ok = await requireLogin()
    if (!ok) return

    // 先清空业务数据节点，接口失败/无数据时保持空态
    clearNodes(controller, MEETING_CLEAR_IDS)

    const list = await api.discussionList(projectId || 1, { pageNum: 1, pageSize: 10 })
    if (list && list.length) {
      // 列表节点为多节点结构，此处预留；首个标题尽力绑定
    } else {
      // 无数据：隐藏研讨记录行与未落地事项行，避免空行残留
      ;['n20', 'n55', 'n90', 'n112', 'n119'].forEach((id) => hideNode(controller, id))
    }
  })
}

// ==================== 新建项目页 ====================
// 节点映射（create.vue）：
//   n27 项目名输入(value)  n36 项目说明 textarea(value)  n30 picker(小组下拉 options/value)
//   n41#members 成员容器：原型假行 n42/n46/n49；真实成员 chip 追加进 ui.n41.appended
//   n56#stages 阶段容器：原型行 n57/n61/n65（行内 span 序号 + input 名 + input[type=date]）
//   n73#tasks 任务容器：原型行 n74/n78（行内 span 头像 + input 名 + input[type=date]）
//   n105 创建按钮  n106#toast
// 成员文案节点（仅文本，用 clearText 清空）：n43/n44/n45/n47/n48/n50/n51
const CREATE_MEMBER_TEXT_IDS = ['n43', 'n44', 'n45', 'n47', 'n48', 'n50', 'n51']
// 原型 3 个假成员行（整行隐藏）
const CREATE_MEMBER_ROW_IDS = ['n42', 'n46', 'n49']
// 原型阶段/任务假输入框（清 value）
const CREATE_INPUT_IDS = ['n27', 'n36', 'n59', 'n63', 'n67', 'n76', 'n80', 'n77', 'n81']

/** 清空输入框/文本域的 value（节点不存在则跳过） */
function clearInputValue(controller, id) {
  if (controller.ui[id]) controller.ui[id].value = ''
}

// 成员头像底色轮换（Tailwind 类；追加 HTML 的 inline style 不被 adapter 解析，故用 class）
const MEMBER_AVATAR_CLS = [
  'bg-sky-400 text-sky-900',
  'bg-rose-400 text-rose-900',
  'bg-emerald-400 text-emerald-900',
  'bg-amber-400 text-amber-900',
  'bg-violet-400 text-violet-900'
]
function escapeChipText(s) {
  return String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

/**
 * 加载某小组的成员区（loadCreate 首次加载 与 picker 切换小组 复用）：
 *  - 清空此前追加的成员行（含切换小组时的旧 chip）
 *  - 隐藏原型 3 个假成员行
 *  - 按原型 chips 样式追加真实成员（头像首字 + 昵称 + 角色 组长/组员）
 *  - 成员池 [{ userId, nickname, role }] 写入 controller.rocMemberPool 供提交时映射 userId
 */
export function loadCreateMembers(controller, groupId) {
  controller.rocMemberPool = []
  // 清空旧追加行（appended 为空则 NativeFragment 不再渲染旧 chip）
  if (controller.ui.n41) controller.ui.n41.appended = []
  CREATE_MEMBER_ROW_IDS.forEach((id) => hideNode(controller, id))
  if (!groupId) return Promise.resolve()

  return api.groupMembers(groupId).then((res) => {
    const list = (res && res.members) || []
    // 成员 chip 改为可点选：默认全不选，点选加入（selected 响应式，模板 v-for 渲染）
    controller.rocMemberPool = list.map((m) => ({
      userId: m.userId,
      nickname: m.nickname,
      role: m.role,
      selected: false
    }))
  }).catch((e) => {
    console.warn('[bind-api] 小组成员加载失败:', e && e.message)
  })
}

/**
 * 新建项目页加载：登录校验 → 清空原型假数据 → 拉我的小组填下拉 → 加载首个小组成员。
 * 小组列表写入 controller.rocGroups，供提交时按下拉选中名反查 groupId。
 */
export function loadCreate(controller) {
  return safe(async () => {
    const ok = await requireLogin()
    if (!ok) return

    controller.rocGroups = []
    controller.rocMemberPool = []

    // 清空原型假数据：输入框 value + 成员文案节点
    CREATE_INPUT_IDS.forEach((id) => clearInputValue(controller, id))
    clearNodes(controller, CREATE_MEMBER_TEXT_IDS)

    // 我的小组 → 下拉
    const groups = await api.myGroups().catch(() => null)
    const list = Array.isArray(groups) ? groups : []
    controller.rocGroups = list
    if (list.length) {
      controller.ui.n30.options = list.map((g) => g.name)
      controller.ui.n30.value = list[0].name
      await loadCreateMembers(controller, list[0].groupId)
    } else {
      controller.ui.n30.options = []
      controller.ui.n30.value = '暂无小组，请先创建/加入小组'
      CREATE_MEMBER_ROW_IDS.forEach((id) => hideNode(controller, id))
    }
  })
}

/**
 * 新建项目真实提交：{ groupId, name, description, stages:[{name,orderNum}], members:[{userId,role}] }
 */
export function createProjectFromForm(form) {
  return api.createProject({
    groupId: form.groupId,
    name: form.name,
    description: form.description || '',
    stages: form.stages || [],
    members: form.members || [],
    auditRule: form.auditRule,
    reminderRule: form.reminderRule
  })
}

export default {
  setText,
  clearText,
  clearNodes,
  loadHome,
  loadProject,
  loadProfile,
  loadMessages,
  loadMeeting,
  loadCreate,
  loadCreateMembers,
  createProjectFromForm
}
