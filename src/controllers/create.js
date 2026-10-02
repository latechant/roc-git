import data from '../data/create.json'
import { createPageController } from '../utils/page-controller.js'
import api from '../utils/api.js'
import { loadCreate, loadCreateMembers, createProjectFromForm } from '../utils/bind-api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = createPageController(data)
  const { document, location, setTimeout } = controller.adapters
  const ui = controller.ui

  // 接入真实接口：登录校验、清空假数据、拉小组下拉与成员池
  loadCreate(controller)

  function showToast(msg) {
    var t = document.getElementById('toast')
    t.textContent = msg
    t.classList.remove('hidden')
    setTimeout(function () { t.classList.add('hidden') }, 1800)
  }

  // 追加一行「成员姓名输入框」，供用户键入要加入项目的成员昵称
  function addMember() {
    document.getElementById('members').insertAdjacentHTML('beforeend',
      '<div class="flex items-center gap-1.5 px-3 py-2 bg-white border border-dashed border-slate-300 rounded-xl">' +
      '<input class="flex-1 text-[13px] text-slate-900 outline-none bg-transparent" placeholder="输入成员姓名">' +
      '</div>')
  }

  // 追加一行「阶段名输入 + 日期」
  var stageNo = 4
  function addStage() {
    var no = (stageNo < 10 ? '0' : '') + stageNo
    stageNo++
    document.getElementById('stages').insertAdjacentHTML('beforeend',
      '<div class="flex items-center gap-2 bg-white border border-slate-200 rounded-xl px-3 py-2.5">' +
      '<span class="text-[12px] font-medium text-slate-700 w-6">' + no + '</span>' +
      '<input class="flex-1 text-[13px] text-slate-900 outline-none bg-transparent" placeholder="阶段标题">' +
      '<input type="date" class="text-[11px] text-slate-400 bg-transparent outline-none">' +
      '</div>')
  }

  // 追加一行「任务名输入 + 日期」
  function addTask() {
    document.getElementById('tasks').insertAdjacentHTML('beforeend',
      '<div class="flex items-center gap-2 bg-white border border-slate-200 rounded-xl p-3">' +
      '<span class="w-6 h-6 rounded-full bg-blue-100 text-blue-700 font-bold flex items-center justify-center text-[10px]">任</span>' +
      '<input class="flex-1 text-[13px] text-slate-900 outline-none bg-transparent" placeholder="任务描述">' +
      '<input type="date" class="text-[11px] text-slate-400 bg-transparent outline-none">' +
      '</div>')
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
    const groupValue = ui.n30.value || ''
    if (groupValue.indexOf('暂无小组') >= 0) { showToast('请先加入/创建小组'); return }
    const group = (controller.rocGroups || []).find(function (g) { return g.name === groupValue })
    if (!group) { showToast('请先加入/创建小组'); return }
    const description = ui.n36.value || ''

    // 阶段收集：每行取非 date 输入框 value，非空则收（orderNum 连续递增）
    const stages = []
    collectRows('n56').forEach(function (row) {
      const nameInput = rowInputs(row).find(function (n) { return n.attrs.type !== 'date' })
      const v = nameInput ? (nameInput.value || '').trim() : ''
      if (v) stages.push({ name: v, orderNum: stages.length + 1 })
    })

    // 任务收集：非 date 输入框 = 任务名，date 输入框 = deadline
    const tasks = []
    collectRows('n73').forEach(function (row) {
      const inputs = rowInputs(row)
      const nameInput = inputs.find(function (n) { return n.attrs.type !== 'date' })
      const dateInput = inputs.find(function (n) { return n.attrs.type === 'date' })
      const v = nameInput ? (nameInput.value || '').trim() : ''
      if (v) tasks.push({ name: v, deadline: dateInput ? (dateInput.value || '') : '' })
    })

    // 成员收集：成员池全部作为项目成员；再叠加 addMember 追加行输入值（按昵称匹配）
    const pool = controller.rocMemberPool || []
    const members = pool.map(function (m) { return { userId: m.userId, role: m.role } })
    const extraNames = []
    ;(ui.n41.appended || []).forEach(function (id) {
      const row = ui[id]
      const nameInput = rowInputs(row).find(function (n) { return n.attrs.type !== 'date' })
      if (nameInput) {
        const v = (nameInput.value || '').trim()
        if (v) extraNames.push(v)
      }
    })
    for (let k = 0; k < extraNames.length; k++) {
      const nm = extraNames[k]
      const hit = pool.find(function (m) { return m.nickname === nm })
      if (!hit) { showToast('成员"' + nm + '"不在小组内，请选择小组成员'); return }
      members.push({ userId: hit.userId, role: 'member' })
    }

    try {
      showToast('正在创建项目…')
      const data = await createProjectFromForm({
        groupId: group.groupId,
        name: name,
        description: description,
        stages: stages,
        members: members
      })
      const projectId = data && data.projectId
      if (!projectId) throw new Error('创建失败：未返回项目 id')
      uni.setStorageSync('rocgit_current_project_id', projectId)

      // 逐个创建任务（assignee = 当前用户；任一失败即 toast 并停止后续）
      if (tasks.length) {
        const profile = await api.getProfile().catch(function () { return null })
        const assigneeId = profile && profile.userId
        for (let k = 0; k < tasks.length; k++) {
          const t = tasks[k]
          await api.createTask({
            projectId: projectId,
            name: t.name,
            assigneeId: assigneeId,
            deadline: fmtDeadline(t.deadline)
          })
        }
      }
      location.href = 'project.html'
    } catch (e) {
      showToast((e && e.message) || '创建失败')
    }
  }

  // picker 切换小组：更新选中值并按新小组重载成员区
  controller.onGroupChange = function (index) {
    const opts = ui.n30.options || []
    const name = opts[index]
    if (!name) return
    ui.n30.value = name
    const g = (controller.rocGroups || []).find(function (x) { return x.name === name })
    loadCreateMembers(controller, g ? g.groupId : null)
  }

  controller.setHandlers({
    n40: function (event) { addMember() },
    n55: function (event) { addStage() },
    n72: function (event) { addTask() },
    n105: function (event) { createProject() }
  })
  return controller
}
