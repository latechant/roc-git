<!--
  项目详情 - 任务 Tab 组件
  功能：任务列表 / 筛选 / 创建 / 编辑 / 完成 / 转交 / 删除
  Props: projectId, stages（阶段列表，用于下拉选择）
  Emits: taskChange - 任务变更时通知父组件刷新
-->
<template>
  <view class="task-tab">
    <!-- 筛选 Tab -->
    <view class="filter-tabs">
      <view
        v-for="t in filterTabs"
        :key="t.key"
        class="filter-tab"
        :class="{ 'filter-tab--active': activeFilter === t.key }"
        @tap="activeFilter = t.key; loadTasks(true)"
      >
        <text class="filter-tab__text">{{ t.label }}</text>
        <text v-if="getCount(t.key) > 0" class="filter-tab__count">{{ getCount(t.key) }}</text>
      </view>
    </view>

    <!-- 任务列表 -->
    <scroll-view class="task-list" scroll-y :show-scrollbar="false" @scrolltolower="onLoadMore">
      <view v-if="loading && tasks.length === 0" class="loading-state">
        <text>加载中...</text>
      </view>

      <view v-else-if="filteredTasks.length === 0" class="empty-state">
        <app-icon name="check-circle" :size="42" color="#cbd5e1" />
        <text class="empty-state__text">{{ emptyText }}</text>
        <view v-if="activeFilter !== 'completed'" class="empty-state__btn" @tap="openCreate">
          <text>添加任务</text>
        </view>
      </view>

      <view v-else class="task-list__inner">
        <!-- 按阶段分组 -->
        <view v-for="group in groupedTasks" :key="group.stageId" class="task-group">
          <view class="task-group__header">
            <view class="task-group__dot" :class="stageStatusClass(group.status)"></view>
            <text class="task-group__name">{{ group.stageName || '未分组' }}</text>
            <text class="task-group__count">{{ group.tasks.length }} 项</text>
          </view>

          <view class="task-group__list">
            <view
              v-for="t in group.tasks"
              :key="t.taskId"
              class="task-card"
              :class="{ 'task-card--done': t.status === 'completed' }"
            >
              <view class="task-card__check" @tap.stop="onToggleComplete(t)">
                <view
                  class="task-card__check-inner"
                  :class="{ 'task-card__check-inner--on': t.status === 'completed' }"
                >
                  <app-icon v-if="t.status === 'completed'" name="check" :size="12" color="#fff" />
                </view>
              </view>

              <view class="task-card__main">
                <view class="task-card__title-row">
                  <text class="task-card__title">{{ t.name || '未命名任务' }}</text>
                  <view class="task-card__badge" :class="'task-card__badge--' + statusType(t.status)">
                    {{ statusText(t.status) }}
                  </view>
                </view>

                <view v-if="t.description" class="task-card__desc">
                  {{ t.description }}
                </view>

                <view class="task-card__meta">
                  <view v-if="t.deadline" class="task-card__deadline" :class="{ 'task-card__deadline--overdue': isOverdue(t) }">
                    <app-icon name="calendar" :size="12" :color="isOverdue(t) ? '#dc2626' : '#94a3b8'" />
                    <text>{{ formatDate(t.deadline) }}</text>
                  </view>
                  <view v-if="t.assigneeName" class="task-card__assignee">
                    <app-icon name="user" :size="12" color="#94a3b8" />
                    <text>{{ t.assigneeName }}</text>
                  </view>
                </view>
              </view>

              <view class="task-card__more" @tap.stop="onMore(t)">
                <text>⋯</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-if="tasks.length > 0 && loading" class="load-more">
        <text>加载更多...</text>
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>

    <!-- 添加任务悬浮按钮 -->
    <view class="fab-add" @tap="openCreate">
      <text class="fab-add__icon">+</text>
      <text class="fab-add__text">添加任务</text>
    </view>

    <!-- ========== 创建/编辑任务弹窗 ========== -->
    <view v-if="showForm" class="modal-mask" @tap="showForm = false">
      <view class="modal-sheet" @tap.stop>
        <view class="modal-sheet__header">
          <text class="modal-sheet__title">{{ editingTask ? '编辑任务' : '添加任务' }}</text>
          <view class="modal-sheet__close" @tap="showForm = false"><app-icon name="x" :size="18" color="#94a3b8" /></view>
        </view>
        <scroll-view class="sheet-body" scroll-y>
          <view class="form-item">
            <text class="form-item__label">任务名称 <text class="form-item__required">*</text></text>
            <input
              class="form-item__input"
              v-model="formData.name"
              placeholder="请输入任务名称"
              placeholder-class="form-item__ph"
            />
          </view>

          <view class="form-item">
            <text class="form-item__label">所属阶段</text>
            <picker mode="selector" :range="stageOptions" range-key="name" @change="onStagePick">
              <view class="form-item__picker">
                {{ pickedStageName || '请选择阶段' }}
                <text class="form-item__arrow">›</text>
              </view>
            </picker>
          </view>

          <view class="form-item">
            <text class="form-item__label">负责人</text>
            <picker mode="selector" :range="memberOptions" range-key="nickname" @change="onMemberPick">
              <view class="form-item__picker">
                {{ pickedMemberName || '请选择负责人' }}
                <text class="form-item__arrow">›</text>
              </view>
            </picker>
          </view>

          <view class="form-item">
            <text class="form-item__label">截止日期</text>
            <picker mode="date" :value="formData.deadline" @change="onDeadlineChange">
              <view class="form-item__picker">
                {{ formData.deadline || '请选择' }}
                <text class="form-item__arrow">›</text>
              </view>
            </picker>
          </view>

          <view class="form-item">
            <text class="form-item__label">任务描述</text>
            <textarea
              class="form-item__textarea"
              v-model="formData.description"
              placeholder="描述任务详情（可选）"
              placeholder-class="form-item__ph"
            />
          </view>

          <view class="sheet-actions">
            <view class="sheet-btn sheet-btn--ghost" @tap="showForm = false">
              <text>取消</text>
            </view>
            <view
              class="sheet-btn sheet-btn--primary"
              :class="{ 'sheet-btn--disabled': submitting }"
              @tap="submitForm"
            >
              <text>{{ submitting ? '提交中...' : (editingTask ? '保存修改' : '确认添加') }}</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- ========== 更多操作菜单 ========== -->
    <view v-if="showMoreMenu" class="modal-mask" @tap="showMoreMenu = false">
      <view class="action-sheet" @tap.stop>
        <view class="action-sheet__item" @tap="onEdit">
          <app-icon name="pencil" :size="16" color="#0f172a" />
          <text>编辑任务</text>
        </view>
        <view class="action-sheet__item" @tap="onTransfer">
          <app-icon name="arrow-right-left" :size="16" color="#0f172a" />
          <text>转交任务</text>
        </view>
        <view class="action-sheet__item action-sheet__item--danger" @tap="onDelete">
          <app-icon name="trash-2" :size="16" color="#dc2626" />
          <text>删除任务</text>
        </view>
        <view class="action-sheet__cancel" @tap="showMoreMenu = false">
          <text>取消</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import api from '../../utils/api.js'

const props = defineProps({
  projectId: { type: Number, required: true },
  stages: { type: Array, default: () => [] },
  members: { type: Array, default: () => [] },
})

const emit = defineEmits(['taskChange'])

// ---------- 数据 ----------
const tasks = ref([])
const loading = ref(false)
const pageNum = ref(1)
const hasMore = ref(true)
const PAGE_SIZE = 50

const activeFilter = ref('all')
const filterTabs = [
  { key: 'all', label: '全部' },
  { key: 'pending', label: '待办' },
  { key: 'active', label: '进行中' },
  { key: 'completed', label: '已完成' },
]

// 创建/编辑表单
const showForm = ref(false)
const submitting = ref(false)
const editingTask = ref(null)
const formData = ref({
  name: '',
  stageId: null,
  assigneeId: null,
  deadline: '',
  description: '',
})
const pickedStageIdx = ref(-1)
const pickedMemberIdx = ref(-1)

// 更多菜单
const showMoreMenu = ref(false)
const currentTask = ref(null)

// ---------- 计算属性 ----------

const stageOptions = computed(() => props.stages.map(s => ({
  stageId: s.stageId,
  name: s.name || '未命名阶段',
})))

const memberOptions = computed(() => {
  if (props.members && props.members.length) return props.members
  // 如果没传 members，从任务里提取
  const map = {}
  tasks.value.forEach(t => {
    if (t.assigneeId && t.assigneeName) {
      map[t.assigneeId] = { userId: t.assigneeId, nickname: t.assigneeName }
    }
  })
  return Object.values(map)
})

const pickedStageName = computed(() => {
  if (pickedStageIdx.value >= 0 && stageOptions.value[pickedStageIdx.value]) {
    return stageOptions.value[pickedStageIdx.value].name
  }
  return ''
})

const pickedMemberName = computed(() => {
  if (pickedMemberIdx.value >= 0 && memberOptions.value[pickedMemberIdx.value]) {
    return memberOptions.value[pickedMemberIdx.value].nickname
  }
  return ''
})

const filteredTasks = computed(() => {
  if (activeFilter.value === 'all') return tasks.value
  return tasks.value.filter(t => t.status === activeFilter.value)
})

const groupedTasks = computed(() => {
  const map = {}
  const stageMap = {}
  props.stages.forEach(s => {
    stageMap[s.stageId] = s
  })

  filteredTasks.value.forEach(t => {
    const sid = t.stageId || 0
    if (!map[sid]) {
      const stage = stageMap[sid]
      map[sid] = {
        stageId: sid,
        stageName: stage ? stage.name : (t.stageName || '未分组'),
        status: stage ? stage.status : 'pending',
        tasks: [],
      }
    }
    map[sid].tasks.push(t)
  })

  // 按阶段顺序排列
  const result = []
  props.stages.forEach(s => {
    if (map[s.stageId]) result.push(map[s.stageId])
  })
  // 加上未分组的
  Object.keys(map).forEach(k => {
    if (!props.stages.find(s => String(s.stageId) === String(k))) {
      result.push(map[k])
    }
  })
  return result
})

const emptyText = computed(() => {
  const map = {
    all: '暂无任务，点击下方按钮添加',
    pending: '暂无待办任务',
    active: '暂无进行中任务',
    completed: '暂无已完成任务',
  }
  return map[activeFilter.value] || '暂无数据'
})

// ---------- 方法 ----------

/** 加载任务列表 */
async function loadTasks(reset = false) {
  if (loading.value) return
  if (reset) {
    pageNum.value = 1
    hasMore.value = true
    tasks.value = []
  }
  if (!hasMore.value) return

  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: PAGE_SIZE }
    if (activeFilter.value !== 'all') {
      params.status = activeFilter.value
    }
    const res = await api.taskList(props.projectId, params)
    const list = (res && (res.list || res.records || res.tasks)) || (Array.isArray(res) ? res : [])

    if (reset) {
      tasks.value = list
    } else {
      tasks.value = [...tasks.value, ...list]
    }

    if (list.length < PAGE_SIZE) {
      hasMore.value = false
    } else {
      pageNum.value += 1
    }
  } catch (e) {
    console.warn('[task-tab] 加载任务失败:', e)
  } finally {
    loading.value = false
  }
}

function refresh() {
  loadTasks(true)
}
defineExpose({ refresh })

/** 各状态计数 */
function getCount(key) {
  if (key === 'all') return tasks.value.length
  return tasks.value.filter(t => t.status === key).length
}

/** 状态文字（后端 status: pending / in_progress / completed） */
function statusText(status) {
  const map = { pending: '待办', in_progress: '进行中', completed: '已完成' }
  return map[status] || (status || '待办')
}
function statusType(status) {
  const map = { pending: 'default', in_progress: 'primary', completed: 'success' }
  return map[status] || 'default'
}

/** 阶段状态样式（后端 status: pending / in_progress / completed） */
function stageStatusClass(status) {
  const map = { pending: 'dot--gray', in_progress: 'dot--blue', completed: 'dot--green' }
  return map[status] || 'dot--gray'
}

/** 日期格式化（兼容 LocalDate/LocalDateTime 序列化为数组/对象/字符串） */
function formatDate(d) {
  if (!d) return ''
  // 数组 [year, month, day, ...]
  if (Array.isArray(d) && d.length >= 3) {
    const y = d[0]
    const m = String(d[1]).padStart(2, '0')
    const day = String(d[2]).padStart(2, '0')
    return `${y}-${m}-${day}`
  }
  // 对象 { year, monthValue, day } 或 { year, month, day }
  if (typeof d === 'object' && d.year != null) {
    const m = String(d.monthValue != null ? d.monthValue : d.month || 0).padStart(2, '0')
    const day = String(d.day || 0).padStart(2, '0')
    return `${d.year}-${m}-${day}`
  }
  // 字符串
  return String(d).slice(0, 10)
}

/** 是否逾期 */
function isOverdue(t) {
  if (!t.deadline || t.status === 'completed') return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const dlStr = formatDate(t.deadline)
  const dl = new Date(dlStr.replace(/-/g, '/'))
  return dl < today
}

// ---------- 交互 ----------

/** 打开创建 */
function openCreate() {
  editingTask.value = null
  formData.value = { name: '', stageId: null, assigneeId: null, deadline: '', description: '' }
  pickedStageIdx.value = -1
  pickedMemberIdx.value = -1
  showForm.value = true
}

/** 阶段选择 */
function onStagePick(e) {
  const idx = e.detail.value
  pickedStageIdx.value = idx
  const opt = stageOptions.value[idx]
  if (opt) formData.value.stageId = opt.stageId
}

/** 成员选择 */
function onMemberPick(e) {
  const idx = e.detail.value
  pickedMemberIdx.value = idx
  const opt = memberOptions.value[idx]
  if (opt) {
    formData.value.assigneeId = opt.userId || opt.assigneeId
  }
}

/** 截止日期 */
function onDeadlineChange(e) {
  formData.value.deadline = e.detail.value
}

/** 提交表单 */
async function submitForm() {
  if (submitting.value) return
  if (!formData.value.name.trim()) {
    uni.showToast({ title: '请输入任务名称', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const data = {
      projectId: props.projectId,
      name: formData.value.name.trim(),
      stageId: formData.value.stageId,
      assigneeId: formData.value.assigneeId,
      deadline: formData.value.deadline || null,
      description: formData.value.description || '',
    }
    if (editingTask.value) {
      await api.updateTask(editingTask.value.taskId, data)
      uni.showToast({ title: '修改成功', icon: 'success' })
    } else {
      await api.createTask(data)
      uni.showToast({ title: '添加成功', icon: 'success' })
    }
    showForm.value = false
    loadTasks(true)
    emit('taskChange')
  } catch (e) {
    uni.showToast({ title: (e && e.message) || '操作失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

/** 完成切换 */
function onToggleComplete(t) {
  if (t.status === 'completed') {
    uni.showToast({ title: '暂不支持取消完成', icon: 'none' })
    return
  }
  uni.showModal({
    title: '确认完成？',
    content: `确定将任务「${t.name}」标记为完成？`,
    success: (res) => {
      if (res.confirm) {
        api.completeTask(t.taskId).then(() => {
          uni.showToast({ title: '已完成', icon: 'success' })
          loadTasks(true)
          emit('taskChange')
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '操作失败', icon: 'none' })
        })
      }
    }
  })
}

/** 更多操作 */
function onMore(t) {
  currentTask.value = t
  showMoreMenu.value = true
}

/** 编辑 */
function onEdit() {
  showMoreMenu.value = false
  const t = currentTask.value
  if (!t) return
  editingTask.value = t
  formData.value = {
    name: t.name || '',
    stageId: t.stageId || null,
    assigneeId: t.assigneeId || null,
    deadline: t.deadline ? String(t.deadline).slice(0, 10) : '',
    description: t.description || '',
  }
  // 回显选中索引
  const sIdx = stageOptions.value.findIndex(s => String(s.stageId) === String(t.stageId))
  pickedStageIdx.value = sIdx
  const mIdx = memberOptions.value.findIndex(m =>
    String(m.userId || m.assigneeId) === String(t.assigneeId)
  )
  pickedMemberIdx.value = mIdx
  showForm.value = true
}

/** 转交 */
function onTransfer() {
  showMoreMenu.value = false
  const t = currentTask.value
  if (!t) return
  // 简化版：弹出 actionSheet 选成员
  const names = memberOptions.value.map(m => m.nickname)
  if (!names.length) {
    uni.showToast({ title: '暂无可选成员', icon: 'none' })
    return
  }
  uni.showActionSheet({
    itemList: names,
    success: (res) => {
      const m = memberOptions.value[res.tapIndex]
      const uid = m.userId || m.assigneeId
      api.transferTask(t.taskId, { assigneeId: uid }).then(() => {
        uni.showToast({ title: '转交成功', icon: 'success' })
        loadTasks(true)
        emit('taskChange')
      }).catch(e => {
        uni.showToast({ title: (e && e.message) || '转交失败', icon: 'none' })
      })
    }
  })
}

/** 删除 */
function onDelete() {
  showMoreMenu.value = false
  const t = currentTask.value
  if (!t) return
  uni.showModal({
    title: '确认删除？',
    content: `删除任务「${t.name}」？`,
    confirmColor: '#dc2626',
    success: (res) => {
      if (res.confirm) {
        api.deleteTask(t.taskId).then(() => {
          uni.showToast({ title: '已删除', icon: 'none' })
          loadTasks(true)
          emit('taskChange')
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '删除失败', icon: 'none' })
        })
      }
    }
  })
}

/** 上拉加载 */
function onLoadMore() {
  if (hasMore.value && !loading.value) {
    loadTasks(false)
  }
}

// ---------- 生命周期 ----------
onMounted(() => {
  if (props.projectId) loadTasks(true)
})

watch(() => props.projectId, (pid) => {
  if (pid) loadTasks(true)
})

watch(() => props.stages, () => {
  // 阶段变化时重新分组（不用重新加载）
}, { deep: true })
</script>

<style scoped>
.task-tab {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  position: relative;
}

/* 筛选 Tab */
.filter-tabs {
  display: flex;
  padding: 10px 16px 0;
  gap: 6px;
  flex-shrink: 0;
}
.filter-tab {
  padding: 6px 14px;
  border-radius: 999px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  gap: 4px;
}
.filter-tab--active {
  background: #2563eb;
}
.filter-tab__text {
  font-size: 12px;
  font-weight: 500;
  color: #64748b;
}
.filter-tab--active .filter-tab__text {
  color: #fff;
}
.filter-tab__count {
  font-size: 10px;
  font-weight: 600;
  color: #94a3b8;
  background: #e2e8f0;
  padding: 1px 6px;
  border-radius: 8px;
  min-width: 16px;
  text-align: center;
}
.filter-tab--active .filter-tab__count {
  color: #2563eb;
  background: #fff;
}

/* 任务列表 */
.task-list {
  flex: 1;
  overflow: hidden;
  padding: 12px 16px;
}
.task-list__inner {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 加载/空状态 */
.loading-state {
  padding: 60px 0;
  text-align: center;
  font-size: 13px;
  color: #94a3b8;
}
.empty-state {
  padding: 60px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.empty-state__icon {
  font-size: 42px;
  opacity: 0.4;
}
.empty-state__text {
  font-size: 13px;
  color: #94a3b8;
}
.empty-state__btn {
  margin-top: 8px;
  padding: 8px 20px;
  background: #2563eb;
  color: #fff;
  border-radius: 10px;
  font-size: 13px;
}
.load-more {
  padding: 16px 0;
  text-align: center;
  font-size: 12px;
  color: #94a3b8;
}

.bottom-spacing {
  height: 90px;
}

/* 任务分组 */
.task-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.task-group__header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 2px;
}
.task-group__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.dot--gray { background: #94a3b8; }
.dot--blue { background: #2563eb; }
.dot--green { background: #10b981; }
.task-group__name {
  flex: 1;
  font-size: 12px;
  font-weight: 600;
  color: #475569;
}
.task-group__count {
  font-size: 11px;
  color: #94a3b8;
}

.task-group__list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 任务卡片 */
.task-card {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #f1f5f9;
}
.task-card--done {
  opacity: 0.7;
}
.task-card--done .task-card__title {
  text-decoration: line-through;
  color: #94a3b8;
}

.task-card__check {
  padding: 2px;
  flex-shrink: 0;
  margin-top: 1px;
}
.task-card__check-inner {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #fff;
}
.task-card__check-inner--on {
  background: #10b981;
  border-color: #10b981;
}

.task-card__main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.task-card__title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}
.task-card__title {
  flex: 1;
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.4;
}
.task-card__badge {
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 500;
  flex-shrink: 0;
}
.task-card__badge--default { background: #f1f5f9; color: #64748b; }
.task-card__badge--primary { background: #dbeafe; color: #2563eb; }
.task-card__badge--success { background: #d1fae5; color: #059669; }

.task-card__desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.task-card__meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.task-card__deadline {
  font-size: 11px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}
.task-card__deadline--overdue {
  color: #dc2626;
  font-weight: 500;
}
.task-card__assignee {
  font-size: 11px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.task-card__more {
  padding: 4px 8px;
  font-size: 18px;
  color: #94a3b8;
  font-weight: 600;
  flex-shrink: 0;
}

/* 悬浮添加按钮 */
.fab-add {
  position: absolute;
  right: 20px;
  bottom: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #2563eb;
  color: #fff;
  border-radius: 999px;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  z-index: 10;
}
.fab-add__icon {
  font-size: 16px;
  font-weight: 600;
}
.fab-add__text {
  font-size: 13px;
  font-weight: 500;
}

/* ---------- 通用弹窗 ---------- */
.modal-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 999;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.modal-sheet {
  width: 100%;
  max-height: 85vh;
  background: #fff;
  border-radius: 20px 20px 0 0;
  display: flex;
  flex-direction: column;
}
.modal-sheet__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  flex-shrink: 0;
}
.modal-sheet__title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}
.modal-sheet__close {
  font-size: 16px;
  color: #94a3b8;
  padding: 4px;
}

.sheet-body {
  padding: 20px;
  flex: 1;
}
.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}
.form-item__label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}
.form-item__required {
  color: #dc2626;
}
.form-item__input {
  height: 42px;
  padding: 0 14px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #0f172a;
}
.form-item__ph {
  color: #94a3b8;
}
.form-item__picker {
  height: 42px;
  padding: 0 14px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #0f172a;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.form-item__picker:empty::before {
  content: '请选择';
  color: #94a3b8;
}
.form-item__arrow {
  font-size: 16px;
  color: #94a3b8;
}
.form-item__textarea {
  min-height: 80px;
  padding: 10px 14px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #0f172a;
  line-height: 1.5;
}

.sheet-actions {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}
.sheet-btn {
  flex: 1;
  padding: 12px;
  border-radius: 10px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
}
.sheet-btn--ghost {
  background: #f1f5f9;
  color: #64748b;
}
.sheet-btn--primary {
  background: #2563eb;
  color: #fff;
}
.sheet-btn--disabled {
  opacity: 0.6;
}

/* Action Sheet */
.action-sheet {
  width: 100%;
  background: #f8fafc;
  border-radius: 16px 16px 0 0;
  padding: 10px;
}
.action-sheet__item {
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  font-size: 15px;
  color: #0f172a;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.action-sheet__item--danger {
  color: #dc2626;
}
.action-sheet__cancel {
  padding: 16px;
  text-align: center;
  background: #fff;
  border-radius: 12px;
  font-size: 15px;
  color: #64748b;
  margin-top: 4px;
}
</style>
