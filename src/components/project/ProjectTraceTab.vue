<!--
  项目详情 - 溯源 Tab 组件（操作记录时间线）
  功能：展示项目操作记录，时间线形式
  Props: projectId
-->
<template>
  <view class="trace-tab">
    <scroll-view class="trace-list" scroll-y :show-scrollbar="false" @scrolltolower="onLoadMore">
      <view v-if="loading && activities.length === 0" class="loading-state">
        <text>加载中...</text>
      </view>

      <view v-else-if="activities.length === 0" class="empty-state">
        <app-icon name="scroll-text" :size="42" color="#cbd5e1" />
        <text class="empty-state__text">暂无操作记录</text>
      </view>

      <view v-else class="timeline">
        <view
          v-for="(item, idx) in activities"
          :key="item.activityId || item.id || idx"
          class="timeline-item"
        >
          <!-- 时间线左侧 -->
          <view class="timeline-item__line">
            <view class="timeline-item__dot" :class="'timeline-item__dot--' + activityType(item).type">
              <app-icon :name="activityType(item).icon" :size="14" :color="activityType(item).color" />
            </view>
            <view v-if="idx < activities.length - 1" class="timeline-item__bar"></view>
          </view>

          <!-- 内容 -->
          <view class="timeline-item__content">
            <view class="timeline-item__header">
              <text class="timeline-item__title">{{ activityTitle(item) }}</text>
              <text class="timeline-item__time">{{ formatTime(item.createdAt) }}</text>
            </view>
            <text v-if="item.targetName" class="timeline-item__desc">
              {{ item.targetName }}
            </text>
            <view v-if="item.userName" class="timeline-item__operator">
              <text class="timeline-item__op-label">操作人：</text>
              <text class="timeline-item__op-name">{{ item.userName }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="activities.length > 0 && loading" class="load-more">
        <text>加载更多...</text>
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '../../utils/api.js'

const props = defineProps({
  projectId: { type: Number, required: true },
})

const activities = ref([])
const loading = ref(false)
const pageNum = ref(1)
const hasMore = ref(true)
const PAGE_SIZE = 30

/** 活动类型映射（图标 + 颜色），按 type 字段分类 */
function activityType(item) {
  const type = item.type || ''
  const map = {
    file:    { icon: 'file-text', color: '#0284c7', type: 'file' },
    task:    { icon: 'clipboard-list', color: '#d97706', type: 'warning' },
    stage:   { icon: 'flag', color: '#2563eb', type: 'primary' },
    member:  { icon: 'user', color: '#64748b', type: 'default' },
  }
  return map[type] || { icon: 'file-edit', color: '#64748b', type: 'default' }
}

/** 活动标题（优先用 action 字段，否则按 type 兜底） */
function activityTitle(item) {
  if (item.action) return item.action
  const type = item.type || ''
  const map = {
    file: '文件操作',
    task: '任务操作',
    stage: '阶段操作',
    member: '成员操作',
  }
  return map[type] || '操作记录'
}

/** 时间格式化 */
function formatTime(t) {
  if (!t) return ''
  const str = String(t)
  // 已经是 YYYY-MM-DD HH:mm:ss 格式，去掉秒
  if (str.length >= 16) return str.slice(5, 16)
  return str.slice(0, 16)
}

/** 加载操作记录 */
async function loadActivities(reset = false) {
  if (loading.value) return
  if (reset) {
    pageNum.value = 1
    hasMore.value = true
    activities.value = []
  }
  if (!hasMore.value) return

  loading.value = true
  try {
    const res = await api.projectActivities(props.projectId, {
      pageNum: pageNum.value,
      pageSize: PAGE_SIZE,
    })
    const list = (res && (res.list || res.records || res.activities)) || (Array.isArray(res) ? res : [])

    if (reset) {
      activities.value = list
    } else {
      activities.value = [...activities.value, ...list]
    }

    if (list.length < PAGE_SIZE) {
      hasMore.value = false
    } else {
      pageNum.value += 1
    }
  } catch (e) {
    console.warn('[trace-tab] 加载操作记录失败:', e)
  } finally {
    loading.value = false
  }
}

function refresh() {
  loadActivities(true)
}
defineExpose({ refresh })

function onLoadMore() {
  if (hasMore.value && !loading.value) {
    loadActivities(false)
  }
}

onMounted(() => {
  if (props.projectId) loadActivities(true)
})

watch(() => props.projectId, (pid) => {
  if (pid) loadActivities(true)
})
</script>

<style scoped>
.trace-tab {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.trace-list {
  flex: 1;
  overflow: hidden;
  padding: 14px 16px;
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
.load-more {
  padding: 16px 0;
  text-align: center;
  font-size: 12px;
  color: #94a3b8;
}

.bottom-spacing {
  height: 30px;
}

/* 时间线 */
.timeline {
  display: flex;
  flex-direction: column;
}

.timeline-item {
  display: flex;
  gap: 12px;
}

.timeline-item__line {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 28px;
  flex-shrink: 0;
}

.timeline-item__dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.timeline-item__dot--default { background: #f1f5f9; }
.timeline-item__dot--primary { background: #dbeafe; }
.timeline-item__dot--success { background: #d1fae5; }
.timeline-item__dot--danger  { background: #fee2e2; }
.timeline-item__dot--warning { background: #fef3c7; }
.timeline-item__dot--file    { background: #e0f2fe; }

.timeline-item__icon {
  font-size: 12px;
}

.timeline-item__bar {
  width: 2px;
  flex: 1;
  background: #e2e8f0;
  margin: 4px 0;
  min-height: 20px;
}

.timeline-item__content {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 12px;
  padding: 12px 14px;
  border: 1px solid #f1f5f9;
  margin-bottom: 10px;
}

.timeline-item__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}

.timeline-item__title {
  font-size: 13px;
  font-weight: 600;
  color: #0f172a;
  flex: 1;
}

.timeline-item__time {
  font-size: 11px;
  color: #94a3b8;
  flex-shrink: 0;
  white-space: nowrap;
}

.timeline-item__desc {
  display: block;
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  word-break: break-all;
}

.timeline-item__operator {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #f8fafc;
}

.timeline-item__op-label {
  font-size: 11px;
  color: #94a3b8;
}

.timeline-item__op-name {
  font-size: 11px;
  color: #475569;
  font-weight: 500;
}
</style>
