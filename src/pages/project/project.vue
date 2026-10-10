<!--
  项目详情页（新架构版本 - 迁移中）
  文件 Tab 已完成迁移，其他 Tab 迁移中
-->
<template>
  <view class="project-page">
    <!-- ========== 顶部导航栏 ========== -->
    <view class="project-nav">
      <view class="project-nav__back" @tap="onBack">
        <text class="project-nav__back-icon">‹</text>
      </view>
      <text class="project-nav__title">{{ project.name || '加载中...' }}</text>
      <view class="project-nav__more" @tap="toggleMore">
        <text class="project-nav__more-icon">⋯</text>
      </view>
    </view>

    <!-- ========== 项目信息头 ========== -->
    <view class="project-header">
      <view class="project-header__info">
        <text class="project-header__stage">{{ project.currentStage || '进行中' }}</text>
        <view class="project-header__progress">
          <view class="project-header__progress-bar">
            <view class="project-header__progress-fill" :style="{ width: (project.progress || 0) + '%' }"></view>
          </view>
          <text class="project-header__progress-text">{{ project.progress || 0 }}%</text>
        </view>
      </view>
      <view class="project-header__stats">
        <view class="stat-pill">
          <text class="stat-pill__num">{{ project.fileCount || 0 }}</text>
          <text class="stat-pill__label">资料</text>
        </view>
        <view class="stat-pill">
          <text class="stat-pill__num">{{ pendingCount }}</text>
          <text class="stat-pill__label">待审</text>
        </view>
        <view class="stat-pill">
          <text class="stat-pill__num">{{ project.completedTaskCount || 0 }}</text>
          <text class="stat-pill__label">任务</text>
        </view>
      </view>
    </view>

    <!-- ========== Tab 切换 ========== -->
    <view class="tab-bar">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-bar__item"
        :class="{ 'tab-bar__item--active': activeTab === tab.key }"
        @tap="activeTab = tab.key"
      >
        <text>{{ tab.label }}</text>
        <view v-if="activeTab === tab.key" class="tab-bar__indicator"></view>
      </view>
    </view>

    <!-- ========== Tab 内容 ========== -->
    <view class="tab-content">
      <!-- 文件 Tab（已迁移） -->
      <ProjectFileTab
        v-if="activeTab === 'files' && projectId"
        :project-id="projectId"
        ref="fileTabRef"
      />

      <!-- 阶段 Tab（已迁移） -->
      <ProjectStageTab
        v-else-if="activeTab === 'stages' && projectId"
        :project-id="projectId"
        ref="stageTabRef"
        @stage-change="onStageChange"
      />

      <!-- 任务 Tab（已迁移） -->
      <ProjectTaskTab
        v-else-if="activeTab === 'tasks' && projectId"
        :project-id="projectId"
        :stages="stages"
        ref="taskTabRef"
        @task-change="onTaskChange"
      />

      <!-- 成员 Tab（已迁移） -->
      <ProjectMemberTab
        v-else-if="activeTab === 'members' && projectInfo.groupId"
        :group-id="projectInfo.groupId"
        ref="memberTabRef"
      />

      <!-- 溯源 Tab（已迁移） -->
      <ProjectTraceTab
        v-else-if="activeTab === 'activity' && projectId"
        :project-id="projectId"
        ref="traceTabRef"
      />
    </view>

    <!-- ========== 更多操作弹窗 ========== -->
    <view v-if="showMore" class="modal-mask" @tap="showMore = false">
      <view class="modal-sheet modal-sheet--sm" @tap.stop>
        <view class="menu-list">
          <view class="menu-list__item" @tap="onRename">
            <text>✏️ 重命名</text>
          </view>
          <view class="menu-list__item" @tap="onAchievement">
            <text>🏆 成果管理</text>
          </view>
          <view class="menu-list__item" @tap="onShare">
            <text>🔗 分享链接</text>
          </view>
          <view class="menu-list__item menu-list__item--danger" @tap="onArchive">
            <text>📦 项目归档</text>
          </view>
        </view>
        <view class="menu-list__cancel" @tap="showMore = false">
          <text>取消</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import api from '../../utils/api.js'
import ProjectFileTab from '../../components/project/ProjectFileTab.vue'
import ProjectStageTab from '../../components/project/ProjectStageTab.vue'
import ProjectTaskTab from '../../components/project/ProjectTaskTab.vue'
import ProjectMemberTab from '../../components/project/ProjectMemberTab.vue'
import ProjectTraceTab from '../../components/project/ProjectTraceTab.vue'

// ---------- 数据 ----------
const projectId = ref(0)
const project = ref({
  name: '',
  currentStage: '',
  progress: 0,
  fileCount: 0,
  completedTaskCount: 0,
  groupId: 0,
})
const activeTab = ref('files')
const showMore = ref(false)
const fileTabRef = ref(null)
const stageTabRef = ref(null)
const memberTabRef = ref(null)
const traceTabRef = ref(null)
const taskTabRef = ref(null)

const tabs = [
  { key: 'files', label: '文件' },
  { key: 'stages', label: '阶段' },
  { key: 'tasks', label: '任务' },
  { key: 'members', label: '成员' },
  { key: 'activity', label: '溯源' },
]

const pendingCount = ref(0)
const stages = ref([])

// ---------- 方法 ----------

/** 加载项目详情 */
async function loadProject() {
  try {
    const pid = projectId.value
    if (!pid) return
    const [detail, stagesRes] = await Promise.all([
      api.projectDetail(pid),
      api.projectStages(pid).catch(() => null),
    ])
    if (detail) {
      project.value = {
        name: detail.name || '',
        currentStage: detail.currentStage || '',
        progress: detail.progress || 0,
        fileCount: detail.fileCount || 0,
        completedTaskCount: detail.completedTaskCount || 0,
        groupId: detail.groupId || 0,
      }
    }
    // 阶段列表（供任务Tab使用）
    if (stagesRes) {
      stages.value = Array.isArray(stagesRes)
        ? stagesRes
        : ((stagesRes && (stagesRes.list || stagesRes.records)) || [])
    }
    // 统计待审数量
    const files = await api.fileList({ projectId: pid, status: 'pending' }).catch(() => null)
    const list = (files && (files.list || files.records)) || (Array.isArray(files) ? files : [])
    pendingCount.value = list.length
  } catch (e) {
    console.warn('[project] 加载详情失败:', e)
  }
}

/** 返回 */
function onBack() {
  uni.navigateBack({ fail: () => uni.redirectTo({ url: '/pages/home/home' }) })
}

/** 更多菜单 */
function toggleMore() {
  showMore.value = !showMore.value
}

/** 重命名 */
function onRename() {
  showMore.value = false
  uni.showModal({
    title: '重命名项目',
    editable: true,
    placeholderText: project.value.name,
    success: (res) => {
      if (res.confirm && res.content) {
        // TODO: 接入重命名接口
        uni.showToast({ title: '重命名功能开发中', icon: 'none' })
      }
    }
  })
}

/** 成果管理 */
function onAchievement() {
  showMore.value = false
  uni.showToast({ title: '成果管理开发中', icon: 'none' })
}

/** 分享链接 */
function onShare() {
  showMore.value = false
  // 生成分享链接
  api.shareCreate({ projectId: projectId.value, type: 'project' }).then(res => {
    const token = res && (res.token || res.shareToken || res.shareId)
    if (!token) {
      uni.showToast({ title: '生成链接失败', icon: 'none' })
      return
    }
    // #ifdef H5
    const link = `${location.origin}/#/pages/share/share?token=${token}`
    uni.setClipboardData({
      data: link,
      success: () => uni.showToast({ title: '链接已复制', icon: 'none' })
    })
    // #endif
    // #ifndef H5
    uni.setClipboardData({
      data: token,
      success: () => uni.showToast({ title: '分享码已复制', icon: 'none' })
    })
    // #endif
  }).catch(() => {
    uni.showToast({ title: '生成分享链接失败', icon: 'none' })
  })
}

/** 项目归档 */
function onArchive() {
  showMore.value = false
  uni.showModal({
    title: '确认归档？',
    content: '归档后项目进入只读状态，文件将打包保存',
    success: (res) => {
      if (res.confirm) {
        api.archiveProject(projectId.value).then(() => {
          uni.showToast({ title: '已归档', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 1500)
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '归档失败', icon: 'none' })
        })
      }
    }
  })
}

/** 阶段变化时刷新项目详情 */
function onStageChange() {
  loadProject()
}

/** 任务变化时刷新 */
function onTaskChange() {
  loadProject()
}

// ---------- 生命周期 ----------
onMounted(() => {
  const pid = uni.getStorageSync('rocgit_current_project_id')
  if (pid) {
    projectId.value = Number(pid)
    loadProject()
  } else {
    uni.showToast({ title: '未找到项目', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1500)
  }
})

onShow(() => {
  if (projectId.value) {
    loadProject()
    if (fileTabRef.value && fileTabRef.value.refresh) {
      fileTabRef.value.refresh()
    }
    if (stageTabRef.value && stageTabRef.value.refresh) {
      stageTabRef.value.refresh()
    }
    if (memberTabRef.value && memberTabRef.value.refresh) {
      memberTabRef.value.refresh()
    }
    if (traceTabRef.value && traceTabRef.value.refresh) {
      traceTabRef.value.refresh()
    }
    if (taskTabRef.value && taskTabRef.value.refresh) {
      taskTabRef.value.refresh()
    }
  }
})
</script>

<style scoped>
.project-page {
  width: 100%;
  height: 100vh;
  height: 100dvh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

/* ---------- 顶部导航 ---------- */
.project-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 44px 12px 10px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}
.project-nav__back {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.project-nav__back-icon {
  font-size: 28px;
  color: #334155;
  font-weight: 300;
}
.project-nav__title {
  flex: 1;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 10px;
}
.project-nav__more {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.project-nav__more-icon {
  font-size: 20px;
  color: #64748b;
}

/* ---------- 项目信息头 ---------- */
.project-header {
  background: #fff;
  padding: 14px 20px 18px;
  border-bottom: 1px solid #f1f5f9;
}
.project-header__info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}
.project-header__stage {
  font-size: 12px;
  color: #2563eb;
  background: #dbeafe;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 500;
  flex-shrink: 0;
}
.project-header__progress {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}
.project-header__progress-bar {
  flex: 1;
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}
.project-header__progress-fill {
  height: 100%;
  background: #2563eb;
  border-radius: 3px;
  transition: width 0.3s;
}
.project-header__progress-text {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  min-width: 38px;
  text-align: right;
}
.project-header__stats {
  display: flex;
  gap: 10px;
}
.stat-pill {
  flex: 1;
  background: #f8fafc;
  border-radius: 10px;
  padding: 10px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.stat-pill__num {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}
.stat-pill__label {
  font-size: 11px;
  color: #94a3b8;
}

/* ---------- Tab 切换 ---------- */
.tab-bar {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}
.tab-bar__item {
  flex: 1;
  padding: 12px 0;
  text-align: center;
  font-size: 13px;
  color: #64748b;
  position: relative;
}
.tab-bar__item--active {
  color: #2563eb;
  font-weight: 600;
}
.tab-bar__indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background: #2563eb;
  border-radius: 2px;
}

/* ---------- Tab 内容 ---------- */
.tab-content {
  flex: 1;
  overflow: hidden;
}

/* 占位样式 */
.tab-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 60px 20px;
}
.tab-placeholder__icon {
  font-size: 48px;
  opacity: 0.4;
}
.tab-placeholder__title {
  font-size: 16px;
  font-weight: 600;
  color: #334155;
}
.tab-placeholder__desc {
  font-size: 13px;
  color: #94a3b8;
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
  max-height: 80vh;
  background: #fff;
  border-radius: 20px 20px 0 0;
  display: flex;
  flex-direction: column;
}
.modal-sheet--sm {
  max-height: 60vh;
}

/* 菜单列表 */
.menu-list {
  padding: 8px 0;
}
.menu-list__item {
  padding: 14px 20px;
  font-size: 15px;
  color: #0f172a;
  text-align: center;
  border-bottom: 1px solid #f8fafc;
}
.menu-list__item--danger {
  color: #dc2626;
}
.menu-list__cancel {
  padding: 14px 20px;
  font-size: 15px;
  color: #64748b;
  text-align: center;
  background: #f8fafc;
}
</style>
