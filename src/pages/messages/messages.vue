<!--
  消息页（新架构版本）
  功能：消息列表 / 时间桶分组 / 分类筛选 / 已读 / 删除 / 全部已读
  接入：messageStore
-->
<template>
  <view class="messages-page">
    <!-- ========== 顶部 Header ========== -->
    <view class="msg-header">
      <view class="msg-header__left">
        <text class="msg-header__title">协作消息</text>
        <text v-if="unreadTotal > 0" class="msg-header__count">{{ unreadTotal }} 条变更</text>
      </view>
      <view v-if="unreadTotal > 0" class="msg-header__action" @tap="onMarkAllRead">
        <text class="msg-header__action-text">全部已读</text>
      </view>
    </view>

    <!-- ========== 分类 Tab ========== -->
    <scroll-view class="msg-tabs" scroll-x :show-scrollbar="false">
      <view class="msg-tabs__inner">
        <view
          v-for="tab in tabs"
          :key="tab.key"
          class="msg-tab"
          :class="{ 'msg-tab--active': activeTab === tab.key }"
          @tap="onTabChange(tab.key)"
        >
          <text>{{ tab.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- ========== 消息列表 ========== -->
    <scroll-view class="msg-list" scroll-y :show-scrollbar="false" @scrolltolower="onLoadMore">

      <!-- 空状态 -->
      <view v-if="!loading && filteredMessages.length === 0" class="empty-state empty-state--lg">
        <text class="empty-state__icon">🔔</text>
        <text class="empty-state__text">{{ emptyText }}</text>
      </view>

      <!-- 时间桶 -->
      <template v-for="bucket in buckets" :key="bucket.label">
        <view class="msg-bucket">
          <text class="msg-bucket__label">{{ bucket.label }}</text>
        </view>

        <!-- 消息项 -->
        <view
          v-for="msg in bucket.items"
          :key="msg.messageId || msg.id"
          class="msg-item"
          :class="{ 'msg-item--unread': !msg.read }"
          @tap="onOpenMessage(msg)"
        >
          <view class="msg-item__icon-wrap">
            <text class="msg-item__icon">{{ msgIcon(msg.type) }}</text>
            <view v-if="!msg.read" class="msg-item__dot"></view>
          </view>
          <view class="msg-item__content">
            <view class="msg-item__header">
              <text class="msg-item__title">{{ msg.title || msg.content || '新消息' }}</text>
              <text class="msg-item__time">{{ formatMsgTime(msg.createdAt) }}</text>
            </view>
            <text class="msg-item__desc">{{ msg.description || msg.projectName || '' }}</text>
          </view>
          <view class="msg-item__delete" @tap.stop="onDelete(msg)">
            <text class="msg-item__delete-icon">✕</text>
          </view>
        </view>
      </template>

      <view class="bottom-spacing"></view>
    </scroll-view>

    <!-- ========== 底部 TabBar ========== -->
    <TabBar current="messages" :unread-count="unreadTotal" @change="onTabBarChange" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useMessageStore } from '../../stores/index.js'
import TabBar from '../../components/TabBar.vue'
import { groupByTimeBucket } from '../../utils-new/format.js'

const messageStore = useMessageStore()

// ---------- 数据 ----------
const activeTab = ref('all')
const loading = ref(false)

const tabs = [
  { key: 'all', label: '全部' },
  { key: 'review', label: '审核' },
  { key: 'deadline', label: '截止' },
  { key: 'version', label: '版本' },
]

// ---------- 计算属性 ----------
const unreadTotal = computed(() => messageStore.unreadCount.total)

/** 当前 Tab 过滤后的消息列表 */
const filteredMessages = computed(() => {
  const list = messageStore.list
  if (activeTab.value === 'all') return list
  return list.filter(m => msgTypeCategory(m.type) === activeTab.value)
})

/** 按时间桶分组 */
const buckets = computed(() => {
  return groupByTimeBucket(filteredMessages.value, 'createdAt')
})

/** 空状态文字 */
const emptyText = computed(() => {
  if (activeTab.value !== 'all') return '暂无此类型消息'
  return '暂无消息'
})

// ---------- 方法 ----------

/** 消息类型 → 图标 */
function msgIcon(type) {
  const map = {
    audit: '📝',
    review: '📝',
    deadline: '⏰',
    task: '⏰',
    version: '📄',
    file: '📄',
    mention: '👋',
    system: '🔔',
  }
  return map[type] || '🔔'
}

/** 消息类型 → Tab 分类映射 */
function msgTypeCategory(type) {
  const map = {
    audit: 'review',
    review: 'review',
    deadline: 'deadline',
    task: 'deadline',
    version: 'version',
    file: 'version',
  }
  return map[type] || 'other'
}

/** 格式化消息时间 */
function formatMsgTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return ''
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  const pad = (n) => String(n).padStart(2, '0')
  if (isToday) {
    return `${pad(d.getHours())}:${pad(d.getMinutes())}`
  }
  return `${d.getMonth() + 1}/${d.getDate()}`
}

/** 加载消息 */
async function loadData() {
  loading.value = true
  try {
    await messageStore.fetchList()
    await messageStore.fetchUnreadCount()
  } catch (e) {
    console.warn('[messages] 加载失败:', e)
  } finally {
    loading.value = false
  }
}

// ---------- 交互 ----------

/** Tab 切换 */
function onTabChange(key) {
  activeTab.value = key
}

/** 全部已读 */
async function onMarkAllRead() {
  try {
    await messageStore.readAll()
    uni.showToast({ title: '已全部标记已读', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

/** 消息类型 → 项目详情 Tab 映射 */
function msgTargetTab(type) {
  const map = {
    audit: 'files',
    review: 'files',
    version: 'files',
    file: 'files',
    deadline: 'tasks',
    task: 'tasks',
  }
  return map[type] || ''
}

/** 打开消息 */
async function onOpenMessage(msg) {
  // 标记已读
  const id = msg.messageId || msg.id
  if (id && !msg.read) {
    messageStore.readMessage(id).catch(() => {})
  }
  // 跳对应项目详情并定位到对应 Tab
  const pid = msg.projectId
  if (pid) {
    uni.setStorageSync('rocgit_current_project_id', Number(pid))
    const tab = msgTargetTab(msg.type)
    uni.navigateTo({ url: '/pages/project/project' + (tab ? `?tab=${tab}` : '') })
  }
}

/** 删除消息 */
async function onDelete(msg) {
  const id = msg.messageId || msg.id
  if (!id) return
  try {
    await messageStore.deleteMessage(id)
    uni.showToast({ title: '已删除', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: '删除失败', icon: 'none' })
  }
}

/** TabBar 切换 */
function onTabBarChange(key) {
  if (key === 'messages') return
  if (key === 'home') {
    uni.redirectTo({ url: '/pages/home/home' })
  } else if (key === 'profile') {
    uni.redirectTo({ url: '/pages/profile/profile' })
  }
}

/** 上拉加载更多（预留） */
function onLoadMore() {
  // TODO: 分页加载
}

// ---------- 生命周期 ----------
onMounted(() => {
  loadData()
})

onShow(() => {
  loadData()
})
</script>

<style scoped>
.messages-page {
  width: 100%;
  height: 100vh;
  height: 100dvh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

/* ---------- 顶部 Header ---------- */
.msg-header {
  padding: 48px 20px 12px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f1f5f9;
}
.msg-header__left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.msg-header__title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}
.msg-header__count {
  font-size: 12px;
  color: #64748b;
}
.msg-header__action {
  padding: 6px 14px;
  background: #dbeafe;
  border-radius: 8px;
}
.msg-header__action-text {
  font-size: 13px;
  font-weight: 500;
  color: #2563eb;
}

/* ---------- 分类 Tab ---------- */
.msg-tabs {
  background: #fff;
  white-space: nowrap;
  border-bottom: 1px solid #f1f5f9;
}
.msg-tabs__inner {
  display: inline-flex;
  padding: 10px 16px;
  gap: 8px;
}
.msg-tab {
  padding: 6px 14px;
  border-radius: 999px;
  background: #f1f5f9;
  font-size: 12px;
  font-weight: 500;
  color: #64748b;
  flex-shrink: 0;
}
.msg-tab--active {
  background: #2563eb;
  color: #fff;
}

/* ---------- 消息列表 ---------- */
.msg-list {
  flex: 1;
  overflow: hidden;
}

/* 时间桶 */
.msg-bucket {
  padding: 16px 20px 8px;
}
.msg-bucket__label {
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
}

/* 消息项 */
.msg-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #f8fafc;
  position: relative;
}
.msg-item--unread {
  background: #f8fafc;
}
.msg-item__icon-wrap {
  position: relative;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.msg-item__icon {
  font-size: 18px;
}
.msg-item__dot {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #ef4444;
  border: 2px solid #fff;
}
.msg-item__content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.msg-item__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.msg-item__title {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
.msg-item__time {
  font-size: 11px;
  color: #94a3b8;
  flex-shrink: 0;
}
.msg-item__desc {
  font-size: 12px;
  color: #64748b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.msg-item__delete {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  opacity: 0.4;
}
.msg-item__delete-icon {
  font-size: 14px;
  color: #94a3b8;
}

/* ---------- 空状态 ---------- */
.empty-state {
  padding: 60px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.empty-state--lg {
  padding-top: 100px;
}
.empty-state__icon {
  font-size: 48px;
  opacity: 0.4;
}
.empty-state__text {
  font-size: 14px;
  color: #94a3b8;
}

/* ---------- 底部间距 ---------- */
.bottom-spacing {
  height: 80px;
}
</style>
