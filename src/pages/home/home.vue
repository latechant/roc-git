<!--
  首页（新架构版本）
  功能：顶部统计 / 当前项目列表 / 临期任务 / 待审核 / 归档 / 底部导航
  接入：userStore / groupStore / projectStore / messageStore
-->
<template>
  <view class="home-page">
    <!-- ========== 顶部 Header ========== -->
    <view class="home-header">
      <view class="home-header__left">
        <view class="home-header__icon-btn home-header__icon-btn--primary" @tap="onCreate">
          <text class="home-header__icon">+</text>
        </view>
        <text class="home-header__name">{{ userStore.nickname || '同学' }}</text>
      </view>
    </view>

    <!-- ========== 小组切换条 ========== -->
    <view class="group-switcher" @tap="onToggleGroupSwitcher">
      <view class="group-switcher__inner">
        <text class="group-switcher__icon">👥</text>
        <text class="group-switcher__name">{{ currentGroupName }}</text>
        <text class="group-switcher__arrow" :class="{ 'group-switcher__arrow--up': showGroupSwitcher }">▾</text>
      </view>
    </view>

    <!-- 小组下拉面板 -->
    <view v-if="showGroupSwitcher" class="group-dropdown-mask" @tap="showGroupSwitcher = false">
      <view class="group-dropdown" @tap.stop>
        <view v-if="!groupStore.groups.length" class="group-dropdown__empty">
          <text class="group-dropdown__empty-text">暂无小组</text>
        </view>
        <view v-else class="group-dropdown__list">
          <view
            v-for="g in groupStore.groups"
            :key="g.groupId"
            class="group-dropdown__item"
            :class="{ 'group-dropdown__item--active': g.groupId === groupStore.currentGroupId }"
            @tap="onSwitchGroup(g)"
          >
            <text class="group-dropdown__item-name">{{ g.name }}</text>
            <text v-if="g.groupId === groupStore.currentGroupId" class="group-dropdown__item-badge">当前</text>
          </view>
        </view>
        <view class="group-dropdown__footer">
          <text class="group-dropdown__manage" @tap="onOpenGroupManage">管理小组</text>
        </view>
      </view>
    </view>

    <!-- 可滚动内容区 -->
    <scroll-view class="home-scroll" scroll-y scroll-with-animation :scroll-into-view="scrollAnchor" :show-scrollbar="false" @scrolltolower="onLoadMore">

      <!-- ========== 统计卡片 ========== -->
      <view class="stats-row">
        <view class="stats-card stats-card--blue" hover-class="stats-card--pressed" @tap="scrollToBlock('projects')">
          <text class="stats-card__num">{{ stats.activeCount }}</text>
          <text class="stats-card__label">进行中</text>
          <text class="stats-card__arrow">›</text>
        </view>
        <view class="stats-card stats-card--orange" hover-class="stats-card--pressed" @tap="scrollToBlock('audit')">
          <text class="stats-card__num">{{ stats.pendingCount }}</text>
          <text class="stats-card__label">待审核</text>
          <text class="stats-card__arrow">›</text>
        </view>
        <view class="stats-card stats-card--red" hover-class="stats-card--pressed" @tap="scrollToBlock('tasks')">
          <text class="stats-card__num">{{ stats.dueCount }}</text>
          <text class="stats-card__label">临期</text>
          <text class="stats-card__arrow">›</text>
        </view>
      </view>

      <!-- ========== 当前项目 ========== -->
      <view class="section" id="block-projects">
        <view class="section__header">
          <text class="section__title">当前项目</text>
          <text v-if="activeProjects.length > 3" class="section__more" @tap="showProjectList = true">查看全部</text>
        </view>

        <!-- 项目卡片横向滚动 -->
        <scroll-view v-if="activeProjects.length" class="project-scroll" scroll-x :show-scrollbar="false">
          <view class="project-list">
            <view
              v-for="(p, idx) in activeProjects.slice(0, 3)"
              :key="p.projectId"
              class="project-card"
              @tap="onOpenProject(p)"
            >
              <view class="project-card__header">
                <text class="project-card__name">{{ p.name }}</text>
              </view>
              <view class="project-card__meta">
                <text class="project-card__stage">{{ p.currentStageName || '进行中' }}</text>
              </view>
              <view class="project-card__progress">
                <view class="project-card__progress-bar">
                  <view class="project-card__progress-fill" :style="{ width: (p.progress || 0) + '%' }"></view>
                </view>
                <text class="project-card__progress-text">{{ p.progress || 0 }}%</text>
              </view>
              <view class="project-card__members">
                <view class="project-card__avatar-list">
                  <MemberAvatar
                    v-for="(m, mi) in getProjectMembers(p, idx)"
                    :key="mi"
                    :name="m.nickname"
                    :size="'xs'"
                    :class="`member-avatar--offset-${mi}`"
                  />
                </view>
                <text class="project-card__member-count">{{ memberCount }}位成员</text>
              </view>
            </view>
          </view>
        </scroll-view>

        <!-- 空状态 -->
        <view v-else class="empty-state">
          <text class="empty-state__icon">📁</text>
          <text class="empty-state__text">暂无项目</text>
        </view>
      </view>

      <!-- ========== 临期任务 ========== -->
      <view class="section" id="block-tasks">
        <view class="section__header">
          <text class="section__title">临期任务</text>
          <text class="section__count">{{ dueTasks.length }}项</text>
        </view>

        <view v-if="dueTasks.length" class="task-list">
          <view
            v-for="(t, idx) in dueTasks.slice(0, 2)"
            :key="t.taskId || idx"
            class="task-item"
            @tap="onOpenTask(t)"
          >
            <view class="task-item__icon">⏰</view>
            <view class="task-item__content">
              <text class="task-item__name">{{ t.name }}</text>
              <text class="task-item__desc">{{ t.projectName || '' }} · 还剩 {{ t.daysLeft || 0 }}天</text>
            </view>
            <AppBadge type="danger" size="sm">{{ t.daysLeft }}天</AppBadge>
          </view>
        </view>

        <view v-else class="empty-state empty-state--sm">
          <text class="empty-state__text">暂无临期任务</text>
        </view>
      </view>

      <!-- ========== 待审核 ========== -->
      <view class="section" id="block-audit">
        <view class="section__header">
          <text class="section__title">待审核</text>
          <text v-if="pendingFiles.length" class="section__more" @tap="onGoAudit">全部</text>
        </view>

        <view v-if="pendingFiles.length" class="audit-list">
          <view
            v-for="(f, idx) in pendingFiles.slice(0, 2)"
            :key="f.fileId || idx"
            class="audit-item"
          >
            <view class="audit-item__icon">📄</view>
            <view class="audit-item__content">
              <text class="audit-item__name">{{ f.uploaderName || '' }} · {{ f.name || '' }}</text>
              <text class="audit-item__time">{{ formatTime(f.createdAt) }}</text>
            </view>
            <view class="audit-item__action" @tap.stop="onApprove(f)">
              <text class="audit-item__action-text">通过</text>
            </view>
          </view>
        </view>

        <view v-else class="empty-state empty-state--sm">
          <text class="empty-state__text">暂无待审核文件</text>
        </view>
      </view>

      <!-- ========== 归档 ========== -->
      <view class="section">
        <view class="section__header">
          <text class="section__title">归档</text>
        </view>

        <view v-if="archivedProjects.length" class="archive-item" @tap="onOpenProject(archivedProjects[0])">
          <view class="archive-item__icon">🏆</view>
          <view class="archive-item__content">
            <text class="archive-item__name">{{ archivedProjects[0].name }}</text>
            <text class="archive-item__desc">
              {{ archivedProjects[0].fileCount || 0 }} 份成果 · {{ archivedProjects[0].versionCount || 0 }} 版本
            </text>
          </view>
          <text class="archive-item__arrow">›</text>
        </view>

        <view v-else class="empty-state empty-state--sm">
          <text class="empty-state__text">暂无归档项目</text>
        </view>
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>

    <!-- ========== 底部 TabBar ========== -->
    <TabBar current="home" :unread-count="unreadCount" @change="onTabChange" />

    <!-- ========== 弹窗 ========== -->
    <GroupPopup :show="showGroupPopup" @close="showGroupPopup = false" @joined="onGroupJoined" />
    <ProjectListPopup
      :show="showProjectList"
      :projects="activeProjects"
      @close="showProjectList = false"
      @select="onSelectProject"
    />
  </view>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/index.js'
import { useGroupStore } from '../../stores/index.js'
import { useMessageStore } from '../../stores/index.js'
import api from '../../utils/api.js'
import AppBadge from '../../components/AppBadge.vue'
import MemberAvatar from '../../components/MemberAvatar.vue'
import TabBar from '../../components/TabBar.vue'
import GroupPopup from '../../components/GroupPopup.vue'
import ProjectListPopup from '../../components/ProjectListPopup.vue'
import { formatDate } from '../../utils-new/format.js'

const userStore = useUserStore()
const groupStore = useGroupStore()
const messageStore = useMessageStore()

// ---------- 数据 ----------
const activeProjects = ref([])    // 进行中项目
const archivedProjects = ref([])  // 归档项目
const dueTasks = ref([])          // 临期任务
const pendingFiles = ref([])      // 待审核文件
const memberList = ref([])        // 小组成员
const loading = ref(false)

// 弹窗
const showGroupPopup = ref(false)
const showProjectList = ref(false)
const showGroupSwitcher = ref(false)

// ---------- 计算属性 ----------
const currentGroupName = computed(() => {
  if (!groupStore.currentGroupId) return '未加入小组'
  const g = groupStore.groups.find(x => x.groupId === groupStore.currentGroupId)
  return g ? g.name : '未加入小组'
})

const stats = computed(() => ({
  activeCount: activeProjects.value.length,
  pendingCount: pendingFiles.value.length,
  dueCount: dueTasks.value.length,
}))

const memberCount = computed(() => memberList.value.length)

const unreadCount = computed(() => messageStore.unreadCount.total)

// ---------- 方法 ----------

/** 获取项目展示用的成员（取前3个） */
function getProjectMembers(project, idx) {
  // 首页所有项目共用小组成员列表，取前3个
  return memberList.value.slice(0, 3)
}

/** 格式化时间（HH:mm） */
function formatTime(dateStr) {
  if (!dateStr) return ''
  return String(dateStr).slice(11, 16)
}

/** 加载首页全部数据 */
async function loadData() {
  loading.value = true
  try {
    // 1. 小组列表（走 groupStore）
    await groupStore.fetchMyGroups().catch(() => {})
    const gid = groupStore.currentGroupId

    // 2. 项目列表
    let projects = []
    if (gid) {
      const res = await api.projectList(gid).catch(() => null)
      projects = Array.isArray(res) ? res : []
    }
    activeProjects.value = projects.filter(p => p.status === 'active')
    archivedProjects.value = projects.filter(p => p.status === 'completed' || p.status === 'archived')

    // 3. 小组成员
    if (gid) {
      await groupStore.fetchMembers(gid).catch(() => {})
      memberList.value = groupStore.members
    } else {
      memberList.value = []
    }

    // 4. 临期任务（7天内）
    const dueRes = await api.dueSoonTasks(7).catch(() => null)
    dueTasks.value = Array.isArray(dueRes) ? dueRes : (dueRes && dueRes.tasks) || []

    // 5. 待审核文件
    const pendingRes = await api.pendingAudit(1, 10).catch(() => null)
    pendingFiles.value = (pendingRes && pendingRes.list) || []

    // 6. 未读消息数
    messageStore.fetchUnreadCount().catch(() => {})
  } catch (e) {
    console.warn('[home] 加载失败:', e)
  } finally {
    loading.value = false
  }
}

// ---------- 交互 ----------

/** 统计卡点击：scroll-view 内平滑滚动到对应区块 */
const scrollAnchor = ref('')
function scrollToBlock(block) {
  // 先清空再赋值，保证同一目标可重复触发
  scrollAnchor.value = ''
  nextTick(() => { scrollAnchor.value = 'block-' + block })
}

/** 切换小组下拉面板 */
function onToggleGroupSwitcher() {
  if (!groupStore.groups.length) {
    // 无小组时直接打开 GroupPopup
    showGroupPopup.value = true
    return
  }
  showGroupSwitcher.value = !showGroupSwitcher.value
}

/** 切换当前小组 */
async function onSwitchGroup(g) {
  showGroupSwitcher.value = false
  if (g.groupId === groupStore.currentGroupId) return
  groupStore.setCurrentGroup(g.groupId)
  // 切换后重新加载项目和成员
  loadData()
}

/** 打开小组管理弹窗 */
function onOpenGroupManage() {
  showGroupSwitcher.value = false
  showGroupPopup.value = true
}

/** 点击加号（创建项目） */
async function onCreate() {
  if (groupStore.groups.length) {
    uni.navigateTo({ url: '/pages/create/create' })
  } else {
    uni.showToast({ title: '请先加入/创建小组', icon: 'none' })
    showGroupPopup.value = true
  }
}

/** 打开项目详情 */
function onOpenProject(p) {
  if (!p || !p.projectId) {
    uni.showToast({ title: '暂无项目', icon: 'none' })
    return
  }
  uni.setStorageSync('rocgit_current_project_id', p.projectId)
  uni.navigateTo({ url: '/pages/project/project' })
}

/** 选中项目列表弹窗中的项目 */
function onSelectProject(p) {
  showProjectList.value = false
  if (!p) return
  uni.setStorageSync('rocgit_current_project_id', p.projectId)
  uni.navigateTo({ url: '/pages/project/project' })
}

/** 小组创建/加入成功 */
function onGroupJoined() {
  showGroupPopup.value = false
  loadData()
}

/** 审核通过 */
function onApprove(file) {
  uni.showToast({ title: '已通过', icon: 'none' })
  // TODO: 接入真实审核接口
}

/** 跳审核页（暂跳项目详情） */
function onGoAudit() {
  if (activeProjects.value.length) {
    onOpenProject(activeProjects.value[0])
  }
}

/** 打开任务 */
function onOpenTask(t) {
  // 跳对应项目详情
  if (t.projectId) {
    uni.setStorageSync('rocgit_current_project_id', t.projectId)
    uni.navigateTo({ url: '/pages/project/project' })
  }
}

/** TabBar 切换 */
function onTabChange(key) {
  if (key === 'home') return
  if (key === 'messages') {
    uni.redirectTo({ url: '/pages/messages/messages' })
  } else if (key === 'profile') {
    uni.redirectTo({ url: '/pages/profile/profile' })
  }
}

/** 滚动到底部（预留上拉加载） */
function onLoadMore() {
  // 首页暂无分页需求
}

// ---------- 生命周期 ----------
onMounted(() => {
  loadData()
})

onShow(() => {
  // 每次页面显示时刷新数据
  loadData()
})
</script>

<style scoped>
.home-page {
  width: 100%;
  height: 100vh;
  height: 100dvh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

/* ---------- 顶部 Header ---------- */
.home-header {
  padding: 48px 20px 16px;
  background: linear-gradient(180deg, #eff6ff 0%, #f8fafc 100%);
  display: flex;
  align-items: center;
}
.home-header__left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.home-header__name {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}
.home-header__icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
.home-header__icon-btn--primary {
  background: #2563eb;
}
.home-header__icon-btn--primary .home-header__icon {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}
.home-header__icon {
  font-size: 18px;
}

/* ---------- 小组切换条 ---------- */
.group-switcher {
  padding: 0 20px 12px;
  background: linear-gradient(180deg, #eff6ff 0%, #f8fafc 100%);
}
.group-switcher__inner {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #fff;
  border-radius: 20px;
  padding: 6px 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
.group-switcher__icon {
  font-size: 14px;
}
.group-switcher__name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.group-switcher__arrow {
  font-size: 12px;
  color: #64748b;
  transition: transform 0.2s;
}
.group-switcher__arrow--up {
  transform: rotate(180deg);
}

/* 小组下拉面板 */
.group-dropdown-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 900;
}
.group-dropdown {
  position: absolute;
  left: 20px;
  top: 100px;
  width: 220px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}
.group-dropdown__empty {
  padding: 20px;
  text-align: center;
}
.group-dropdown__empty-text {
  font-size: 13px;
  color: #94a3b8;
}
.group-dropdown__list {
  max-height: 240px;
  overflow-y: auto;
}
.group-dropdown__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
}
.group-dropdown__item:last-child {
  border-bottom: none;
}
.group-dropdown__item--active {
  background: #eff6ff;
}
.group-dropdown__item-name {
  font-size: 14px;
  color: #334155;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.group-dropdown__item-badge {
  font-size: 11px;
  color: #2563eb;
  background: #dbeafe;
  padding: 2px 8px;
  border-radius: 10px;
  margin-left: 8px;
  flex-shrink: 0;
}
.group-dropdown__footer {
  padding: 10px 16px;
  border-top: 1px solid #f1f5f9;
  text-align: center;
}
.group-dropdown__manage {
  font-size: 13px;
  color: #2563eb;
  font-weight: 500;
}

/* ---------- 滚动区 ---------- */
.home-scroll {
  flex: 1;
  overflow: hidden;
}

/* ---------- 统计卡片 ---------- */
.stats-row {
  display: flex;
  gap: 10px;
  padding: 0 20px 16px;
}
.stats-card {
  position: relative;
  flex: 1;
  border-radius: 14px;
  padding: 14px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.stats-card--blue { background: #dbeafe; }
.stats-card--orange { background: #fef3c7; }
.stats-card--red { background: #fee2e2; }
.stats-card--pressed {
  transform: scale(0.96);
  opacity: 0.9;
}
.stats-card__arrow {
  position: absolute;
  top: 4px;
  right: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #94a3b8;
}
.stats-card__num {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}
.stats-card__label {
  font-size: 12px;
  color: #64748b;
}

/* ---------- 通用 Section ---------- */
.section {
  padding: 0 20px 20px;
}
.section__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.section__title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}
.section__more {
  font-size: 13px;
  color: #2563eb;
}
.section__count {
  font-size: 13px;
  color: #94a3b8;
}

/* ---------- 项目卡片 ---------- */
.project-scroll {
  margin: 0 -20px;
  padding: 0 20px;
  white-space: nowrap;
}
.project-list {
  display: inline-flex;
  gap: 12px;
  padding-right: 20px;
}
.project-card {
  width: 220px;
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  border: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex-shrink: 0;
}
.project-card__name {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.project-card__stage {
  font-size: 12px;
  color: #2563eb;
  background: #dbeafe;
  padding: 2px 8px;
  border-radius: 6px;
  display: inline-block;
}
.project-card__progress {
  display: flex;
  align-items: center;
  gap: 8px;
}
.project-card__progress-bar {
  flex: 1;
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}
.project-card__progress-fill {
  height: 100%;
  background: #2563eb;
  border-radius: 3px;
  transition: width 0.3s;
}
.project-card__progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  min-width: 36px;
  text-align: right;
}
.project-card__members {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.project-card__avatar-list {
  display: flex;
  position: relative;
}
.member-avatar--offset-0 { z-index: 3; }
.member-avatar--offset-1 { margin-left: -8px; z-index: 2; }
.member-avatar--offset-2 { margin-left: -8px; z-index: 1; }
.project-card__member-count {
  font-size: 11px;
  color: #94a3b8;
}

/* ---------- 空状态 ---------- */
.empty-state {
  padding: 24px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  background: #fff;
  border-radius: 12px;
  border: 1px dashed #e2e8f0;
}
.empty-state--sm {
  padding: 16px 0;
}
.empty-state__icon {
  font-size: 32px;
  opacity: 0.5;
}
.empty-state--sm .empty-state__icon {
  font-size: 24px;
}
.empty-state__text {
  font-size: 13px;
  color: #94a3b8;
}

/* ---------- 临期任务 ---------- */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.task-item {
  background: #fff;
  border-radius: 12px;
  padding: 12px 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #f1f5f9;
}
.task-item__icon {
  font-size: 18px;
}
.task-item__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.task-item__name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.task-item__desc {
  font-size: 12px;
  color: #94a3b8;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ---------- 待审核 ---------- */
.audit-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.audit-item {
  background: #fff;
  border-radius: 12px;
  padding: 12px 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #f1f5f9;
}
.audit-item__icon {
  font-size: 18px;
}
.audit-item__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.audit-item__name {
  font-size: 13px;
  color: #334155;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.audit-item__time {
  font-size: 12px;
  color: #94a3b8;
}
.audit-item__action {
  padding: 6px 14px;
  background: #d1fae5;
  border-radius: 8px;
}
.audit-item__action-text {
  font-size: 12px;
  font-weight: 600;
  color: #059669;
}

/* ---------- 归档 ---------- */
.archive-item {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid #f1f5f9;
}
.archive-item__icon {
  font-size: 28px;
}
.archive-item__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}
.archive-item__name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.archive-item__desc {
  font-size: 12px;
  color: #94a3b8;
}
.archive-item__arrow {
  font-size: 20px;
  color: #cbd5e1;
}

/* ---------- 底部间距（避开 TabBar） ---------- */
.bottom-spacing {
  height: 80px;
}
</style>
