<!--
  AppEmpty — 空状态占位
  Props:
    - text: String  // 提示文字
    - icon: String  // emoji 或文字图标
    - size: 'sm' | 'md' | 'lg'
-->
<template>
  <view class="app-empty" :class="`app-empty--${size}`">
    <text class="app-empty__icon">{{ icon }}</text>
    <text class="app-empty__text">{{ text }}</text>
    <slot name="action" />
  </view>
</template>

<script setup>
defineProps({
  text: { type: String, default: '暂无数据' },
  icon: { type: String, default: '📭' },
  size: { type: String, default: 'md' },
})
</script>

<style scoped>
.app-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
.app-empty--sm { padding: 20px 10px; }
.app-empty--lg { padding: 60px 20px; }

.app-empty__icon {
  font-size: 40px;
  margin-bottom: 12px;
  opacity: 0.5;
}
.app-empty--sm .app-empty__icon { font-size: 28px; margin-bottom: 8px; }
.app-empty--lg .app-empty__icon { font-size: 56px; margin-bottom: 16px; }

.app-empty__text {
  font-size: 13px;
  color: #94a3b8;
}
.app-empty--sm .app-empty__text { font-size: 12px; }
.app-empty--lg .app-empty__text { font-size: 14px; }
</style>
