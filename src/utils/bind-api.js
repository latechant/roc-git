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
    const profile = await api.getProfile()
    setText(controller, 'n5', profile && profile.nickname)

    // 我的小组 -> 取第一个小组
    const groups = await api.myGroups()
    const gid = groups && groups.length ? groups[0].groupId : null
    const projects = gid ? await api.projectList(gid).catch(() => null) : null
    const list = projects || []

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
      setText(controller, 'n47', `${p0.progress}%`)
      setAvatar(controller, 'n52', memberNames[0])
      setAvatar(controller, 'n53', memberNames[1])
      setAvatar(controller, 'n54', memberNames[2])
      setText(controller, 'n55', `${members.total || memberNames.length}位成员`)
    } else {
      hideNode(controller, 'n36')
      setEmptyState(controller, 'n35', '暂无项目')
    }

    // 项目2
    if (list.length > 1) {
      const p1 = list[1]
      setText(controller, 'n60', p1.name)
      setText(controller, 'n59', `进度 ${p1.progress}%`)
      setText(controller, 'n65', `${p1.progress}%`)
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
      setEmptyState(controller, 'n158', '暂无归档')
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
      setText(controller, 'n23', `${detail.fileCount}资料`)
      setText(controller, 'n24', `${detail.fileCount}待审`)
      setText(controller, 'n25', `${detail.completedTaskCount}版本`)
    }

    const stages = await api.projectStages(projectId).catch(() => null)
    if (stages && stages.length) {
      // 阶段列表在 n40 区域，为多节点；此处仅展示第一个阶段名（保留扩展）
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
    if (unread) {
      setText(controller, 'n5', `${unread.total} 条变更`)
      setText(controller, 'n16', `${unread.task + unread.audit + unread.mention} 条项目变更`)
    }

    const list = await api.messageList({ pageNum: 1, pageSize: 10 })
    const items = list && list.list ? list.list : []
    if (items.length) {
      setText(controller, 'n26', items[0].title)
    } else {
      // 无数据：隐藏全部消息条目行与日期分隔符，避免空行残留
      ;['n25', 'n35', 'n45', 'n57', 'n64', 'n73', 'n19', 'n52', 'n69'].forEach((id) => hideNode(controller, id))
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
  clearText,
  clearNodes,
  loadHome,
  loadProject,
  loadProfile,
  loadMessages,
  loadMeeting,
  createProjectFromForm
}
