<!--
  项目详情 - 阶段 Tab 组件（新架构版本）
  功能：阶段列表 / 添加阶段 / 编辑阶段 / 删除阶段 / 推进阶段 / 任务展示
  Props: projectId
-->
<template>
  <view class="stage-tab">
    <!-- 阶段列表 -->
    <scroll-view class="stage-list" scroll-y :show-scrollbar="false" @scrolltolower="onLoadMore">
      <view v-if="loading && stages.length === 0" class="loading-state">
        <text>加载中...</text>
      </view>

      <view v-else-if="stages.length === 0" class="empty-state">
        <app-icon name="clipboard-list" :size="42" color="#cbd5e1" />
        <text class="empty-state__text">暂无阶段</text>
        <view class="empty-state__btn" @tap="showAdd = true">
          <text>添加阶段</text>
        </view>
      </view>

      <view v-else class="stage-list__inner">
        <view
          v-for="(s, idx) in stages"
          :key="s.stageId"
          class="stage-card"
          :class="stageCardClass(s)"
        >
          <!-- 阶段头部 -->
          <view class="stage-card__header">
            <view class="stage-card__no" :class="stageBadgeClass(s)">
              {{ formatNo(idx + 1) }}
            </view>
            <view class="stage-card__info">
              <view class="stage-card__name-row">
                <text class="stage-card__name" :class="stageNameClass(s)">{{ s.name || '未命名阶段' }}</text>
                <app-icon v-if="s.status === 'completed'" class="stage-card__done" name="check" :size="14" color="#16a34a" />
              </view>
              <text v-if="formatRange(s)" class="stage-card__range">{{ formatRange(s) }}</text>
            </view>
            <view class="stage-card__badge" :class="stageStatusBadge(s.status).cls">
              {{ stageStatusBadge(s.status).text }}
            </view>
          </view>

          <!-- 进度条 -->
          <view class="stage-card__progress">
            <view class="stage-card__progress-bar">
              <view class="stage-card__progress-fill" :style="{ width: getProgress(s) + '%' }"></view>
            </view>
          </view>

          <!-- 任务列表 -->
          <view v-if="getTasks(s.stageId).length" class="stage-card__tasks">
            <text class="stage-card__tasks-title">任务分工</text>
            <view class="task-list">
              <view
                v-for="t in getTasks(s.stageId)"
                :key="t.taskId"
                class="task-row"
                :class="{ 'task-row--done': t.status === 'completed' }"
              >
                <view
                  class="task-row__check"
                  :class="{ 'task-row__check--on': t.status === 'completed' }"
                  @tap.stop="onToggleTask(t)"
                >
                  <app-icon v-if="t.status === 'completed'" name="check" :size="12" color="#fff" />
                </view>
                <view class="task-row__main">
                  <text class="task-row__name">{{ t.name || '未命名任务' }}</text>
                  <view class="task-row__meta">
                    <text v-if="t.deadline" class="task-row__deadline">截止 {{ formatDeadline(t.deadline) }}</text>
                    <text v-if="t.assigneeName" class="task-row__assignee">{{ t.assigneeName }}</text>
                  </view>
                </view>
                <view class="task-row__badge" :class="'task-row__badge--' + taskStatusType(t.status)">
                  {{ taskStatusText(t.status) }}
                </view>
              </view>
            </view>
          </view>

          <!-- 操作按钮 -->
          <view class="stage-card__actions">
            <view
              v-if="s.status !== 'completed'"
              class="stage-btn stage-btn--primary"
              @tap="onAdvance(s)"
            >
              完成本阶段
            </view>
            <view class="stage-btn stage-btn--ghost" @tap="onEdit(s)">
              编辑
            </view>
            <view class="stage-btn stage-btn--danger" @tap="onDelete(s)">
              删除
            </view>
          </view>
        </view>
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>

    <!-- 添加阶段按钮（悬浮） -->
    <view v-if="stages.length > 0" class="fab-add" @tap="showAdd = true">
      <text class="fab-add__icon">+</text>
      <text class="fab-add__text">添加阶段</text>
    </view>

    <!-- ========== 添加阶段弹窗 ========== -->
    <view v-if="showAdd" class="modal-mask" @tap="showAdd = false">
      <view class="modal-sheet" @tap.stop>
        <view class="modal-sheet__header">
          <text class="modal-sheet__title">添加阶段</text>
          <view class="modal-sheet__close" @tap="showAdd = false"><app-icon name="x" :size="18" color="#94a3b8" /></view>
        </view>
        <view class="sheet-body">
          <view class="form-item">
            <text class="form-item__label">阶段名称</text>
            <input
              class="form-item__input"
              v-model="addForm.name"
              placeholder="请输入阶段名称"
              placeholder-class="form-item__ph"
            />
          </view>
          <view class="form-item">
            <text class="form-item__label">开始日期</text>
            <picker mode="date" @change="onAddStartChange" :value="addForm.startDate">
              <view class="form-item__picker">
                {{ addForm.startDate || '请选择' }}
                <text class="form-item__arrow">›</text>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-item__label">结束日期</text>
            <picker mode="date" @change="onAddEndChange" :value="addForm.endDate">
              <view class="form-item__picker">
                {{ addForm.endDate || '请选择' }}
                <text class="form-item__arrow">›</text>
              </view>
            </picker>
          </view>
          <view class="sheet-actions">
            <view class="sheet-btn sheet-btn--ghost" @tap="showAdd = false">
              <text>取消</text>
            </view>
            <view class="sheet-btn sheet-btn--primary" :class="{ 'sheet-btn--disabled': adding }" @tap="doAdd">
              <text>{{ adding ? '添加中...' : '确认添加' }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- ========== 编辑阶段弹窗 ========== -->
    <view v-if="showEdit" class="modal-mask" @tap="showEdit = false">
      <view class="modal-sheet" @tap.stop>
        <view class="modal-sheet__header">
          <text class="modal-sheet__title">编辑阶段</text>
          <view class="modal-sheet__close" @tap="showEdit = false"><app-icon name="x" :size="18" color="#94a3b8" /></view>
        </view>
        <view class="sheet-body">
          <view class="form-item">
            <text class="form-item__label">阶段名称</text>
            <input
              class="form-item__input"
              v-model="editForm.name"
              placeholder="请输入阶段名称"
              placeholder-class="form-item__ph"
            />
          </view>
          <view class="form-item">
            <text class="form-item__label">开始日期</text>
            <picker mode="date" @change="onEditStartChange" :value="editForm.startDate">
              <view class="form-item__picker">
                {{ editForm.startDate || '请选择' }}
                <text class="form-item__arrow">›</text>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-item__label">结束日期</text>
            <picker mode="date" @change="onEditEndChange" :value="editForm.endDate">
              <view class="form-item__picker">
                {{ editForm.endDate || '请选择' }}
                <text class="form-item__arrow">›</text>
              </view>
            </picker>
          </view>
          <view class="sheet-actions">
            <view class="sheet-btn sheet-btn--ghost" @tap="showEdit = false">
              <text>取消</text>
            </view>
            <view class="sheet-btn sheet-btn--primary" :class="{ 'sheet-btn--disabled': editing }" @tap="doEdit">
              <text>{{ editing ? '保存中...' : '保存修改' }}</text>
            </view>
          </view>
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
})

const emit = defineEmits(['stageChange'])

// ---------- 数据 ----------
const stages = ref([])
const tasks = ref([]) // 全部任务
const loading = ref(false)

// 添加阶段
const showAdd = ref(false)
const adding = ref(false)
const addForm = ref({ name: '', startDate: '', endDate: '' })

// 编辑阶段
const showEdit = ref(false)
const editing = ref(false)
const editForm = ref({ stageId: 0, name: '', startDate: '', endDate: '' })

// ---------- 方法 ----------

/** 加载阶段列表 */
async function loadStages() {
  loading.value = true
  try {
    const res = await api.projectStages(props.projectId)
    stages.value = Array.isArray(res) ? res : ((res && (res.list || res.records)) || [])
  } catch (e) {
    console.warn('[stage-tab] 加载阶段失败:', e)
  } finally {
    loading.value = false
  }
}

/** 加载任务列表 */
async function loadTasks() {
  try {
    const res = await api.taskList(props.projectId, { pageNum: 1, pageSize: 50 })
    tasks.value = (res && (res.list || res.records || res.tasks)) || (Array.isArray(res) ? res : [])
  } catch (e) {
    console.warn('[stage-tab] 加载任务失败:', e)
  }
}

/** 加载全部 */
async function loadAll() {
  await Promise.all([loadStages(), loadTasks()])
}

/** 对外暴露刷新方法 */
function refresh() {
  loadAll()
}
defineExpose({ refresh })

/** 按阶段ID取任务 */
function getTasks(stageId) {
  return tasks.value.filter(t => t.stageId === stageId || t.stageId == stageId)
}

/** 计算进度 */
function getProgress(s) {
  if (s.progress != null) return s.progress
  const total = s.taskCount || s.totalTaskCount || 0
  const done = s.completedTaskCount || 0
  if (!total) return 0
  return Math.round(done / total * 100)
}

/** 序号格式化 */
function formatNo(n) {
  return n < 10 ? '0' + n : '' + n
}

/** 日期范围（兼容 LocalDate 序列化为数组/字符串的情况） */
function formatRange(s) {
  const start = formatDate(s.startDate)
  const end = formatDate(s.endDate)
  if (!start && !end) return ''
  return [start, end].filter(Boolean).join(' ~ ')
}

/** 通用日期格式化（兼容数组 [2024,1,15] 和字符串 "2024-01-15"） */
function formatDate(d) {
  if (!d) return ''
  // 如果是数组 [year, month, day]
  if (Array.isArray(d) && d.length >= 3) {
    const y = d[0]
    const m = String(d[1]).padStart(2, '0')
    const day = String(d[2]).padStart(2, '0')
    return `${y}-${m}-${day}`.slice(5) // 返回 MM-DD
  }
  // 如果是对象（year/month/day）
  if (typeof d === 'object' && d.year != null) {
    const m = String(d.monthValue != null ? d.monthValue : d.month || 0).padStart(2, '0')
    const day = String(d.day || 0).padStart(2, '0')
    return `${d.year}-${m}-${day}`.slice(5)
  }
  // 字符串
  return String(d).slice(5, 10)
}

/** 状态徽章（后端 status: pending / in_progress / completed） */
function stageStatusBadge(status) {
  const map = {
    pending: { cls: 'badge--default', text: '未开始' },
    in_progress: { cls: 'badge--primary', text: '进行中' },
    completed: { cls: 'badge--success', text: '已完成' },
  }
  // 兼容旧值 active
  if (status === 'active') return map.in_progress
  return map[status] || { cls: 'badge--default', text: status || '未知' }
}

/** 任务状态文字（后端 status: pending / in_progress / completed） */
function taskStatusText(status) {
  const map = {
    pending: '待办',
    in_progress: '进行中',
    completed: '已完成',
  }
  return map[status] || (status || '待办')
}
function taskStatusType(status) {
  const map = {
    pending: 'default',
    in_progress: 'primary',
    completed: 'success',
  }
  return map[status] || 'default'
}

/** 任务截止日期格式化（兼容数组/字符串/对象） */
function formatDeadline(d) {
  if (!d) return ''
  if (Array.isArray(d) && d.length >= 3) {
    const m = String(d[1]).padStart(2, '0')
    const day = String(d[2]).padStart(2, '0')
    return `${m}-${day}`
  }
  if (typeof d === 'object' && d.year != null) {
    const m = String(d.monthValue != null ? d.monthValue : d.month || 0).padStart(2, '0')
    const day = String(d.day || 0).padStart(2, '0')
    return `${m}-${day}`
  }
  return String(d).slice(5, 10)
}

function stageCardClass(s) {
  if (s.status === 'in_progress' || s.status === 'active') return 'stage-card--active'
  if (s.status === 'completed') return 'stage-card--done'
  return 'stage-card--pending'
}
function stageBadgeClass(s) {
  if (s.status === 'in_progress' || s.status === 'active') return 'no--active'
  if (s.status === 'completed') return 'no--done'
  return 'no--pending'
}
function stageNameClass(s) {
  if (s.status === 'in_progress' || s.status === 'active') return 'name--active'
  if (s.status === 'completed') return 'name--done'
  return 'name--pending'
}

// ---------- 交互 ----------

/** 推进阶段 */
function onAdvance(s) {
  uni.showModal({
    title: '确认完成？',
    content: `确定将「${s.name}」标记为完成？`,
    success: (res) => {
      if (res.confirm) {
        api.advanceStage(props.projectId, s.stageId).then(() => {
          uni.showToast({ title: '已完成', icon: 'success' })
          loadAll()
          emit('stageChange')
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '操作失败', icon: 'none' })
        })
      }
    }
  })
}

/** 编辑阶段 */
function onEdit(s) {
  editForm.value = {
    stageId: s.stageId,
    name: s.name || '',
    startDate: s.startDate ? String(s.startDate).slice(0, 10) : '',
    endDate: s.endDate ? String(s.endDate).slice(0, 10) : '',
  }
  showEdit.value = true
}

/** 删除阶段 */
function onDelete(s) {
  uni.showModal({
    title: '确认删除？',
    content: `删除阶段「${s.name}」，关联任务也会被删除`,
    success: (res) => {
      if (res.confirm) {
        api.deleteStage(props.projectId, s.stageId).then(() => {
          uni.showToast({ title: '已删除', icon: 'none' })
          loadAll()
          emit('stageChange')
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '删除失败', icon: 'none' })
        })
      }
    }
  })
}

/** 任务完成切换 */
function onToggleTask(t) {
  if (t.status === 'completed') {
    uni.showToast({ title: '暂不支持取消完成', icon: 'none' })
    return
  }
  api.completeTask(t.taskId).then(() => {
    uni.showToast({ title: '已完成', icon: 'success' })
    loadAll()
    emit('stageChange')
  }).catch(e => {
    uni.showToast({ title: (e && e.message) || '操作失败', icon: 'none' })
  })
}

/** 添加 - 日期选择 */
function onAddStartChange(e) {
  addForm.value.startDate = e.detail.value
}
function onAddEndChange(e) {
  addForm.value.endDate = e.detail.value
}

/** 编辑 - 日期选择 */
function onEditStartChange(e) {
  editForm.value.startDate = e.detail.value
}
function onEditEndChange(e) {
  editForm.value.endDate = e.detail.value
}

/** 确认添加 */
async function doAdd() {
  if (adding.value) return
  if (!addForm.value.name.trim()) {
    uni.showToast({ title: '请输入阶段名称', icon: 'none' })
    return
  }
  adding.value = true
  try {
    await api.createStage(props.projectId, {
      name: addForm.value.name.trim(),
      startDate: addForm.value.startDate || null,
      endDate: addForm.value.endDate || null,
    })
    uni.showToast({ title: '添加成功', icon: 'success' })
    showAdd.value = false
    addForm.value = { name: '', startDate: '', endDate: '' }
    loadAll()
    emit('stageChange')
  } catch (e) {
    uni.showToast({ title: (e && e.message) || '添加失败', icon: 'none' })
  } finally {
    adding.value = false
  }
}

/** 确认编辑 */
async function doEdit() {
  if (editing.value) return
  if (!editForm.value.name.trim()) {
    uni.showToast({ title: '请输入阶段名称', icon: 'none' })
    return
  }
  editing.value = true
  try {
    await api.updateStage(props.projectId, editForm.value.stageId, {
      name: editForm.value.name.trim(),
      startDate: editForm.value.startDate || null,
      endDate: editForm.value.endDate || null,
    })
    uni.showToast({ title: '修改成功', icon: 'success' })
    showEdit.value = false
    loadAll()
    emit('stageChange')
  } catch (e) {
    uni.showToast({ title: (e && e.message) || '修改失败', icon: 'none' })
  } finally {
    editing.value = false
  }
}

/** 上拉加载更多 */
function onLoadMore() {
  // TODO: 分页
}

// ---------- 生命周期 ----------
onMounted(() => {
  if (props.projectId) loadAll()
})

watch(() => props.projectId, (newId) => {
  if (newId) loadAll()
})
</script>

<style scoped>
.stage-tab {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  position: relative;
}

/* ---------- 列表 ---------- */
.stage-list {
  flex: 1;
  overflow: hidden;
  padding: 14px 16px;
}
.stage-list__inner {
  display: flex;
  flex-direction: column;
  gap: 12px;
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

.bottom-spacing {
  height: 90px;
}

/* ---------- 阶段卡片 ---------- */
.stage-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}
.stage-card--active {
  border-color: #93c5fd;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}
.stage-card--pending {
  opacity: 0.8;
}
.stage-card--done {
  border-color: #f1f5f9;
}

.stage-card__header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.stage-card__no {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
}
.no--pending { background: #f1f5f9; color: #94a3b8; }
.no--active { background: #2563eb; color: #fff; }
.no--done { background: #10b981; color: #fff; }

.stage-card__info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.stage-card__name-row {
  display: flex;
  align-items: center;
  gap: 6px;
}
.stage-card__name {
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.name--pending { color: #64748b; }
.name--active { color: #1e3a8a; }
.name--done { color: #0f172a; }
.stage-card__done {
  font-size: 12px;
  color: #10b981;
  font-weight: 700;
}
.stage-card__range {
  font-size: 11px;
  color: #94a3b8;
}

.stage-card__badge {
  padding: 3px 8px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 600;
  flex-shrink: 0;
}
.badge--default { background: #f1f5f9; color: #64748b; }
.badge--primary { background: #dbeafe; color: #2563eb; }
.badge--success { background: #d1fae5; color: #059669; }

/* 进度条 */
.stage-card__progress {
  margin-top: 12px;
}
.stage-card__progress-bar {
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}
.stage-card__progress-fill {
  height: 100%;
  background: #2563eb;
  border-radius: 3px;
  transition: width 0.3s;
}

/* 任务列表 */
.stage-card__tasks {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}
.stage-card__tasks-title {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
}
.task-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.task-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
}
.task-row--done .task-row__name {
  text-decoration: line-through;
  color: #94a3b8;
}
.task-row__check {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #fff;
  flex-shrink: 0;
}
.task-row__check--on {
  background: #10b981;
  border-color: #10b981;
}
.task-row__main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.task-row__name {
  font-size: 13px;
  color: #334155;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.task-row__meta {
  display: flex;
  align-items: center;
  gap: 8px;
}
.task-row__deadline {
  font-size: 11px;
  color: #f59e0b;
}
.task-row__assignee {
  font-size: 11px;
  color: #94a3b8;
}
.task-row__badge {
  padding: 2px 6px;
  border-radius: 6px;
  font-size: 10px;
  font-weight: 500;
  flex-shrink: 0;
}
.task-row__badge--default { background: #f1f5f9; color: #64748b; }
.task-row__badge--primary { background: #dbeafe; color: #2563eb; }
.task-row__badge--success { background: #d1fae5; color: #059669; }

/* 操作按钮 */
.stage-card__actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f8fafc;
}
.stage-btn {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
}
.stage-btn--primary {
  background: #2563eb;
  color: #fff;
}
.stage-btn--ghost {
  background: #f1f5f9;
  color: #64748b;
}
.stage-btn--danger {
  background: #fee2e2;
  color: #dc2626;
}

/* ---------- 悬浮添加按钮 ---------- */
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
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.form-item__label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
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
.form-item__arrow {
  font-size: 16px;
  color: #94a3b8;
}

.sheet-actions {
  display: flex;
  gap: 10px;
  margin-top: 4px;
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
</style>
