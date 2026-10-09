<template>
  <view v-if="show" class="project-popup-mask" @tap="emit('close')">
    <view class="project-popup-card" @tap.stop>
      <view class="project-popup-header">
        <text class="project-popup-title">{{ title }}</text>
        <text class="project-popup-close" @tap="emit('close')">✕</text>
      </view>

      <view v-if="!projects.length" class="project-popup-empty">
        <text class="project-popup-empty-text">暂无项目</text>
      </view>
      <view v-else class="project-popup-list">
        <view
          v-for="p in projects"
          :key="p.projectId"
          class="project-popup-item"
          @tap="emit('select', p)"
        >
          <view class="project-popup-item-main">
            <text class="project-popup-item-name">{{ p.name }}</text>
            <text class="project-popup-item-status">{{ p.status === 'active' ? '进行中' : (p.status === 'completed' || p.status === 'archived' ? '已归档' : '筹备中') }}</text>
          </view>
          <text class="project-popup-item-progress">进度 {{ p.progress || 0 }}%</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
defineProps({
  show: { type: Boolean, default: false },
  projects: { type: Array, default: () => [] },
  title: { type: String, default: '当前项目' }
})
const emit = defineEmits(['close', 'select'])
</script>

<style scoped>
.project-popup-mask {
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
.project-popup-card {
  width: 300px;
  max-height: 70vh;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-sizing: border-box;
  overflow-y: auto;
}
.project-popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.project-popup-title {
  font-size: 17px;
  font-weight: 600;
  color: #0f172a;
}
.project-popup-close {
  font-size: 16px;
  color: #94a3b8;
  padding: 4px;
}
.project-popup-empty {
  padding: 16px 0;
}
.project-popup-empty-text {
  font-size: 13px;
  color: #94a3b8;
}
.project-popup-list {
  display: flex;
  flex-direction: column;
}
.project-popup-item {
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}
.project-popup-item-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.project-popup-item-name {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}
.project-popup-item-status {
  font-size: 11px;
  color: #2563eb;
  background: #eff6ff;
  border-radius: 6px;
  padding: 2px 8px;
}
.project-popup-item-progress {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}
</style>
