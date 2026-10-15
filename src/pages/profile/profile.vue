<!--
  个人中心页（新架构版本）
  功能：个人资料 / 贡献统计 / 功能入口 / 退出登录
  接入：userStore / groupStore / projectStore
-->
<template>
  <view class="profile-page">
    <!-- 可滚动内容区 -->
    <scroll-view class="profile-scroll" scroll-y :show-scrollbar="false">

      <!-- ========== 顶部资料卡 ========== -->
      <view class="profile-header">
        <view class="profile-header__bg"></view>
        <view class="profile-header__content">
          <view class="profile-avatar">
            <MemberAvatar :name="userStore.nickname || '?' " size="lg" />
          </view>
          <view class="profile-info">
            <view class="profile-info__row">
              <text class="profile-info__name">{{ userStore.nickname || '未设置昵称' }}</text>
              <text class="profile-info__edit" @tap="onEditProfile">编辑</text>
            </view>
            <text class="profile-info__sub">
              {{ profile.major || '课程协作' }} · {{ profile.projectCount || 0 }} 个项目
            </text>
          </view>
        </view>
      </view>

      <!-- ========== 贡献统计 ========== -->
      <view class="stats-card-wrap">
        <view class="stats-card2">
          <view class="stats-card2__item">
            <text class="stats-card2__num">{{ stats.uploadedFiles || 0 }}</text>
            <text class="stats-card2__label">上传文件</text>
          </view>
          <view class="stats-card2__divider"></view>
          <view class="stats-card2__item">
            <text class="stats-card2__num">{{ stats.approvedFiles || 0 }}</text>
            <text class="stats-card2__label">通过版本</text>
          </view>
          <view class="stats-card2__divider"></view>
          <view class="stats-card2__item">
            <text class="stats-card2__num">{{ stats.completedTasks || 0 }}</text>
            <text class="stats-card2__label">完成任务</text>
          </view>
        </view>
      </view>

      <!-- ========== 功能菜单 ========== -->
      <view class="menu-section">
        <text class="menu-section__title">我的项目</text>

        <view class="menu-item" @tap="onMyProjects">
          <view class="menu-item__icon">📁</view>
          <text class="menu-item__label">我的项目</text>
          <text class="menu-item__value">{{ projectCount }} 个进行中</text>
          <text class="menu-item__arrow">›</text>
        </view>

        <view class="menu-item" @tap="onActivityLog">
          <view class="menu-item__icon">📋</view>
          <text class="menu-item__label">操作记录</text>
          <text class="menu-item__arrow">›</text>
        </view>
      </view>

      <view class="menu-section">
        <text class="menu-section__title">归档与存储</text>

        <view class="menu-item" @tap="onArchive">
          <view class="menu-item__icon">🏆</view>
          <text class="menu-item__label">期末归档</text>
          <text class="menu-item__arrow">›</text>
        </view>

        <view class="menu-item" @tap="onStorage">
          <view class="menu-item__icon">💾</view>
          <text class="menu-item__label">存储与下载</text>
          <text class="menu-item__value">正常</text>
          <text class="menu-item__arrow">›</text>
        </view>
      </view>

      <view class="menu-section">
        <text class="menu-section__title">设置</text>

        <view class="menu-item">
          <view class="menu-item__icon">🔔</view>
          <text class="menu-item__label">消息通知</text>
          <view class="menu-switch" :class="{ 'menu-switch--on': notifyEnabled }" @tap="notifyEnabled = !notifyEnabled">
            <view class="menu-switch__dot"></view>
          </view>
        </view>

        <view class="menu-item">
          <view class="menu-item__icon">🔕</view>
          <text class="menu-item__label">免打扰模式</text>
          <view class="menu-switch" :class="{ 'menu-switch--on': dndEnabled }" @tap="dndEnabled = !dndEnabled">
            <view class="menu-switch__dot"></view>
          </view>
        </view>
      </view>

      <!-- ========== 退出登录 ========== -->
      <view class="logout-wrap">
        <view class="logout-btn" @tap="onLogout">
          <text class="logout-btn__text">退出登录</text>
        </view>
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>

    <!-- ========== 底部 TabBar ========== -->
    <TabBar current="profile" :unread-count="unreadCount" @change="onTabBarChange" />

    <!-- ========== 弹窗 ========== -->
    <ProjectListPopup
      :show="showProjectList"
      :projects="activeProjects"
      @close="showProjectList = false"
      @select="onSelectProject"
    />

    <!-- 归档确认弹窗 -->
    <view v-if="showArchiveModal" class="modal-mask" @tap="showArchiveModal = false">
      <view class="modal-card" @tap.stop>
        <text class="modal-title">确认归档？</text>
        <text class="modal-desc">归档后项目将进入只读状态，文件会打包保存。</text>
        <view class="modal-actions">
          <view class="modal-btn modal-btn--cancel" @tap="showArchiveModal = false">
            <text>取消</text>
          </view>
          <view class="modal-btn modal-btn--confirm" @tap="doArchive">
            <text>确认归档</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/index.js'
import { useMessageStore } from '../../stores/index.js'
import api from '../../utils/api.js'
import MemberAvatar from '../../components/MemberAvatar.vue'
import TabBar from '../../components/TabBar.vue'
import ProjectListPopup from '../../components/ProjectListPopup.vue'

const userStore = useUserStore()
const messageStore = useMessageStore()

// ---------- 数据 ----------
const profile = ref({
  nickname: '',
  major: '',
  projectCount: 0,
})
const stats = ref({
  uploadedFiles: 0,
  approvedFiles: 0,
  completedTasks: 0,
})
const activeProjects = ref([])
const projectCount = computed(() => activeProjects.value.length)
const unreadCount = computed(() => messageStore.unreadCount.total)

// 开关
const notifyEnabled = ref(true)
const dndEnabled = ref(false)

// 弹窗
const showProjectList = ref(false)
const showArchiveModal = ref(false)

// ---------- 方法 ----------

/** 加载全部数据 */
async function loadData() {
  try {
    // 1. 个人资料
    const p = await api.getProfile().catch(() => null)
    if (p) {
      profile.value = {
        nickname: p.nickname || '',
        major: p.major || '课程协作',
        projectCount: p.projectCount || 0,
      }
    }

    // 2. 贡献统计
    const s = await api.contributionStats().catch(() => null)
    if (s) {
      stats.value = {
        uploadedFiles: s.uploadedFiles || 0,
        approvedFiles: s.approvedFiles || 0,
        completedTasks: s.completedTasks || 0,
      }
    }

    // 3. 进行中项目（供弹窗用）
    const groups = await api.myGroups().catch(() => null)
    const gid = groups && groups.length ? groups[0].groupId : null
    if (gid) {
      const projects = await api.projectList(gid).catch(() => null)
      activeProjects.value = (projects || []).filter(p => p.status === 'active')
    }

    // 4. 未读消息数
    messageStore.fetchUnreadCount().catch(() => {})
  } catch (e) {
    console.warn('[profile] 加载失败:', e)
  }
}

// ---------- 交互 ----------

/** 编辑资料 */
function onEditProfile() {
  uni.showToast({ title: '编辑资料', icon: 'none' })
}

/** 我的项目 */
function onMyProjects() {
  showProjectList.value = true
}

/** 操作记录 */
function onActivityLog() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

/** 期末归档 */
function onArchive() {
  showArchiveModal.value = true
}

/** 确认归档 */
function doArchive() {
  showArchiveModal.value = false
  uni.showToast({ title: '归档功能开发中', icon: 'none' })
  // TODO: 接入归档接口
}

/** 存储与下载 */
function onStorage() {
  uni.showToast({ title: '存储正常', icon: 'none' })
}

/** 选中项目（弹窗） */
function onSelectProject(p) {
  showProjectList.value = false
  if (!p) return
  uni.setStorageSync('rocgit_current_project_id', p.projectId)
  uni.navigateTo({ url: '/pages/project/project' })
}

/** 退出登录 */
function onLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}

/** TabBar 切换 */
function onTabBarChange(key) {
  if (key === 'profile') return
  if (key === 'home') {
    uni.redirectTo({ url: '/pages/home/home' })
  } else if (key === 'messages') {
    uni.redirectTo({ url: '/pages/messages/messages' })
  }
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
.profile-page {
  width: 100%;
  height: 100vh;
  height: 100dvh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

.profile-scroll {
  flex: 1;
  overflow: hidden;
}

/* ---------- 顶部资料卡 ---------- */
.profile-header {
  position: relative;
  padding: 60px 20px 30px;
}
.profile-header__bg {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  height: 140px;
  background: linear-gradient(180deg, #dbeafe 0%, #f8fafc 100%);
}
.profile-header__content {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
}
.profile-avatar {
  flex-shrink: 0;
}
.profile-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.profile-info__row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.profile-info__name {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}
.profile-info__edit {
  font-size: 12px;
  color: #2563eb;
  padding: 3px 10px;
  background: rgba(37, 99, 235, 0.1);
  border-radius: 6px;
}
.profile-info__sub {
  font-size: 13px;
  color: #64748b;
}

/* ---------- 贡献统计 ---------- */
.stats-card-wrap {
  padding: 0 20px 20px;
}
.stats-card2 {
  background: #fff;
  border-radius: 16px;
  padding: 18px 0;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  border: 1px solid #f1f5f9;
}
.stats-card2__item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.stats-card2__num {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}
.stats-card2__label {
  font-size: 12px;
  color: #64748b;
}
.stats-card2__divider {
  width: 1px;
  height: 30px;
  background: #f1f5f9;
}

/* ---------- 菜单区 ---------- */
.menu-section {
  padding: 0 20px 16px;
}
.menu-section__title {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  margin-bottom: 8px;
  padding: 0 4px;
}
.menu-item {
  background: #fff;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 1px;
  border: 1px solid #f1f5f9;
  border-bottom: none;
}
.menu-item:first-child {
  border-radius: 12px 12px 0 0;
}
.menu-item:last-child {
  border-radius: 0 0 12px 12px;
  border-bottom: 1px solid #f1f5f9;
}
.menu-item:only-child {
  border-radius: 12px;
}
.menu-item__icon {
  font-size: 20px;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}
.menu-item__label {
  flex: 1;
  font-size: 14px;
  color: #1e293b;
}
.menu-item__value {
  font-size: 12px;
  color: #94a3b8;
}
.menu-item__arrow {
  font-size: 18px;
  color: #cbd5e1;
  flex-shrink: 0;
}

/* 开关按钮 */
.menu-switch {
  width: 44px;
  height: 24px;
  border-radius: 12px;
  background: #cbd5e1;
  position: relative;
  transition: background 0.2s;
  flex-shrink: 0;
}
.menu-switch--on {
  background: #2563eb;
}
.menu-switch__dot {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #fff;
  transition: left 0.2s;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}
.menu-switch--on .menu-switch__dot {
  left: 22px;
}

/* ---------- 退出登录 ---------- */
.logout-wrap {
  padding: 20px;
}
.logout-btn {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  text-align: center;
  border: 1px solid #f1f5f9;
}
.logout-btn__text {
  font-size: 15px;
  font-weight: 500;
  color: #ef4444;
}

/* ---------- 底部间距 ---------- */
.bottom-spacing {
  height: 80px;
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
  align-items: center;
  justify-content: center;
}
.modal-card {
  width: 280px;
  background: #fff;
  border-radius: 16px;
  padding: 24px 20px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.modal-title {
  font-size: 17px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 10px;
}
.modal-desc {
  font-size: 13px;
  color: #64748b;
  text-align: center;
  margin-bottom: 20px;
  line-height: 1.5;
}
.modal-actions {
  display: flex;
  gap: 10px;
  width: 100%;
}
.modal-btn {
  flex: 1;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
}
.modal-btn--cancel {
  background: #f1f5f9;
  color: #64748b;
}
.modal-btn--confirm {
  background: #2563eb;
  color: #fff;
}
</style>
