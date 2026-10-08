import { reactive } from 'vue'
import data from '../data/create.json'
import { createPageController } from '../utils/page-controller.js'
import api from '../utils/api.js'
import { loadCreate, createProjectFromForm } from '../utils/bind-api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  // controller 整体包一层 reactive：仅 ui 原生响应式，step/goStep 等需模板感知的状态由此驱动
  const controller = reactive(createPageController(data))
  const { document, location, setTimeout } = controller.adapters
  const ui = controller.ui

  // 四步向导：1 基础 → 2 阶段 → 3 分工(成员+任务) → 4 规则
  controller.step = 1

  // 同步步骤条激活态：圆点(n10/n13/n16/n19) + 标签(n9/n12/n15/n18)，className 幂等切换
  const DOT_ACTIVE = ['bg-blue-600', 'text-white']
  const DOT_IDLE = ['bg-slate-200', 'text-slate-500']
  const LABEL_ACTIVE = ['text-blue-600', 'font-semibold']
  const LABEL_IDLE = ['text-slate-400']
  function retokenize(el, strip, target) {
    var tokens = String(el.className || '').split(/\s+/).filter(function (t) { return t && strip.indexOf(t) < 0 })
    el.className = tokens.concat(target).join(' ')
  }
  function applyStepBar(active) {
    const dots = ['n10', 'n13', 'n16', 'n19']
    const labels = ['n9', 'n12', 'n15', 'n18']
    dots.forEach(function (id, i) {
      retokenize(ui[id], DOT_ACTIVE.concat(DOT_IDLE), (i + 1) === active ? DOT_ACTIVE : DOT_IDLE)
    })
    labels.forEach(function (id, i) {
      retokenize(ui[id], LABEL_ACTIVE.concat(LABEL_IDLE), (i + 1) === active ? LABEL_ACTIVE : LABEL_IDLE)
    })
  }
  function goStep(n) {
    if (n < 1 || n > 4) return
    controller.step = n
    applyStepBar(n)
    if (n === 4) ui.n105.textContent = '创建项目'
  }
  function nextStep() {
    const step = controller.step
    if (step === 1) {
      const name = (ui.n27.value || '').trim()
      if (!name) { showToast('请输入项目名称'); return }
      const group = (controller.rocGroups || [])[0]
      if (!group) { showToast('请先加入/创建小组'); return }
    }
    goStep(step + 1)
  }
  function prevStep() { goStep(controller.step - 1) }
  controller.goStep = goStep
  controller.nextStep = nextStep
  controller.prevStep = prevStep
  applyStepBar(1)

  // 接入真实接口：登录校验、清空假数据、拉小组下拉与成员池
  loadCreate(controller)
  // 记录当前用户昵称：成员池为空时负责人选择器兜底显示当前用户
  api.getProfile().then(function (profile) {
    if (profile && profile.nickname) controller.currentUserNickname = profile.nickname
  }).catch(function () {})

  function showToast(msg) {
    var t = document.getElementById('toast')
    t.textContent = msg
    t.classList.remove('hidden')
    setTimeout(function () { t.classList.add('hidden') }, 1800)
  }

  // 追加一行「阶段名输入 + 日期」；title 传入则预填（模板用），否则留空待填
  var stageNo = 4
  function addStage(title) {
    var no = (stageNo < 10 ? '0' : '') + stageNo
    stageNo++
    var titleInput = title ? 'value="' + title + '"' : 'placeholder="阶段标题"'
    document.getElementById('stages').insertAdjacentHTML('beforeend',
      '<div class="flex items-center gap-2 bg-white border border-slate-200 rounded-xl px-3 py-2.5">' +
      '<span class="text-[12px] font-medium text-slate-700 w-6">' + no + '</span>' +
      '<input class="flex-1 text-[13px] text-slate-900 outline-none bg-transparent" ' + titleInput + '>' +
      '<input type="date" class="text-[11px] text-slate-400 bg-transparent outline-none">' +
      '</div>')
  }

  // 阶段模板：一键填充常见阶段流程（小白引导）
  var STAGE_TEMPLATES = {
    course: ['需求分析', '方案设计', '编码实现', '单元测试', '联调验收', '答辩提交'],
    research: ['选题与文献调研', '数据收集', '分析论证', '报告撰写', '修改提交'],
    agile: ['迭代规划', '需求澄清', '开发', '评审', '复盘']
  }
  // 应用模板：清空现有阶段行 → 重置序号 → 逐条预填模板阶段
  controller.applyStageTemplate = function (key) {
    var names = STAGE_TEMPLATES[key]
    if (!names) return
    collectRows('n56').forEach(function (r) { r.removed = true })
    stageNo = 1
    names.forEach(function (t) { addStage(t) })
    showToast('已套用「' + (key === 'course' ? '课程项目' : key === 'research' ? '调研报告' : '敏捷迭代') + '」模板')
  }

  // 删除一行阶段：标记 removed（模板 v-if 不渲染，收集时 collectRows 自动跳过）
  controller.removeRow = function (parentId, rowId) { ui[rowId].removed = true }

  // 点选/取消成员 chip：切换选中态（selected 响应式，模板即时反馈）
  controller.toggleMember = function (m) {
    if (m) m.selected = !m.selected
  }

  // 追加一行「负责人 + 任务名输入 + 日期」；title 传入则预填（模板用），否则留空待填
  function addTask(title) {
    var taskInput = title ? 'value="' + title + '"' : 'placeholder="任务描述"'
    document.getElementById('tasks').insertAdjacentHTML('beforeend',
      '<div class="flex items-center gap-2 bg-white border border-slate-200 rounded-xl p-3">' +
      '<picker mode="selector" value="0" class="text-[11px] text-slate-400 bg-transparent outline-none">0</picker>' +
      '<input class="flex-1 text-[13px] text-slate-900 outline-none bg-transparent" ' + taskInput + '>' +
      '<input type="date" class="text-[11px] text-slate-400 bg-transparent outline-none">' +
      '</div>')
  }

  // 任务模板：一键批量预填常用任务（追加而非替换，可多次叠加）
  var TASK_TEMPLATES = {
    dev: ['需求分析', '系统设计', '编码实现', '单元测试', '集成联调', '文档整理'],
    report: ['资料收集', '初稿撰写', '小组评审', '修改定稿', '答辩准备']
  }
  // 应用任务模板：追加语义——不清空、不重置，逐条预填模板任务
  controller.applyTaskTemplate = function (key) {
    var names = TASK_TEMPLATES[key]
    if (!names) return
    names.forEach(function (t) { addTask(t) })
    showToast('已添加「' + (key === 'dev' ? '通用开发' : '报告答辩') + '」任务模板')
  }

  // 取某行内所有 input 节点（原型行 children={id} 引用；追加行同理）
  function rowInputs(rowNode) {
    const inputs = []
    ;(rowNode.children || []).forEach(function (ref) {
      if (!ref.id) return
      const n = ui[ref.id]
      if (n && n.tag === 'input') inputs.push(n)
    })
    return inputs
  }
  // 收集容器内全部行：原型行（未 removed）+ 追加行
  function collectRows(containerId) {
    const c = ui[containerId]
    const rows = []
    ;(c.children || []).forEach(function (ref) {
      if (ref.id && !ui[ref.id].removed) rows.push(ui[ref.id])
    })
    ;(c.appended || []).forEach(function (id) { rows.push(ui[id]) })
    return rows
  }

  // deadline：有日期取该日 23:59:59；无日期默认 now+7 天（YYYY-MM-DD HH:mm:ss）
  function fmtDeadline(dateStr) {
    if (dateStr && String(dateStr).length >= 10) return String(dateStr).slice(0, 10) + ' 23:59:59'
    const d = new Date(Date.now() + 7 * 24 * 3600 * 1000)
    const p = function (x) { return String(x).padStart(2, '0') }
    return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) + ' 23:59:59'
  }

  async function createProject() {
    // 基础信息
    const name = (ui.n27.value || '').trim()
    if (!name) { showToast('请输入项目名称'); return }
    const group = (controller.rocGroups || [])[0]
    if (!group) { showToast('请先加入/创建小组'); return }
    const description = ui.n36.value || ''

    // 阶段收集：每行取非 date 输入框 value，非空则收（orderNum 连续递增）
    const stages = []
    collectRows('n56').forEach(function (row) {
      const nameInput = rowInputs(row).find(function (n) { return n.attrs.type !== 'date' })
      const v = nameInput ? (nameInput.value || '').trim() : ''
      if (v) stages.push({ name: v, orderNum: stages.length + 1 })
    })

    // 任务收集：非 date 输入框 = 任务名，date 输入框 = deadline；picker = 负责人（昵称索引）
    const pool = controller.rocMemberPool || []
    const tasks = []
    collectRows('n73').forEach(function (row) {
      const inputs = rowInputs(row)
      const nameInput = inputs.find(function (n) { return n.attrs.type !== 'date' })
      const dateInput = inputs.find(function (n) { return n.attrs.type === 'date' })
      const v = nameInput ? (nameInput.value || '').trim() : ''
      if (!v) return
      // 负责人：行内 picker 的 value 为池昵称索引；无 picker（旧原型/兜底）默认当前用户
      let assigneeId = null
      const picker = (row.children || []).map(ref => ref.id ? ui[ref.id] : null).find(n => n && n.tag === 'picker')
      if (picker && pool[picker.value] != null) assigneeId = pool[picker.value].userId
      tasks.push({ name: v, deadline: dateInput ? (dateInput.value || '') : '', assigneeId: assigneeId })
    })

    // 成员收集：只收点选中的成员（默认全不选，点选加入）；不选任何人也允许创建
    const members = []
    ;(controller.rocMemberPool || []).forEach(function (m) {
      if (m.selected) members.push({ userId: m.userId, role: m.role })
    })

    try {
      showToast('正在创建项目…')
      // 规则收集：挂单审核（n86）+ 截止提醒（n91）两个开关
      const auditOn = !!ui.n86.checked
      const remindOn = !!ui.n91.checked
      const data = await createProjectFromForm({
        groupId: group.groupId,
        name: name,
        description: description,
        stages: stages,
        members: members,
        auditRule: { requireAudit: auditOn, auditorRole: 'leader' },
        reminderRule: { taskReminderDays: remindOn ? [3, 1] : [] }
      })
      const projectId = data && data.projectId
      if (!projectId) throw new Error('创建失败：未返回项目 id')
      uni.setStorageSync('rocgit_current_project_id', projectId)

      // 逐个创建任务（负责人=所选成员；未选/兜底回退当前用户；任一失败即 toast 并停止后续）
      if (tasks.length) {
        const profile = await api.getProfile().catch(function () { return null })
        const currentUserId = profile && profile.userId
        for (let k = 0; k < tasks.length; k++) {
          const t = tasks[k]
          await api.createTask({
            projectId: projectId,
            name: t.name,
            assigneeId: t.assigneeId || currentUserId,
            deadline: fmtDeadline(t.deadline)
          })
        }
      }
      location.href = 'project.html'
    } catch (e) {
      showToast((e && e.message) || '创建失败')
    }
  }

  // 默认归属第一个小组：小组下拉已移除，createProject() 直接取 rocGroups[0]

  controller.setHandlers({
    n55: function (event) { addStage() },
    n72: function (event) { addTask() },
    n105: function (event) { createProject() }
  })
  return controller
}
