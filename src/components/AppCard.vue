<!--
  AppCard — 通用卡片容器
  Props:
    - hover: Boolean  // 点击态阴影
    - padding: 'sm' | 'md' | 'lg' | 'none'
-->
<template>
  <view
    class="app-card"
    :class="[`app-card--p-${padding}`, { 'app-card--hover': hover }]"
    @tap="onTap"
  >
    <slot />
  </view>
</template>

<script setup>
const props = defineProps({
  hover: { type: Boolean, default: false },
  padding: { type: String, default: 'md' }, // sm / md / lg / none
})
const emit = defineEmits(['tap'])

function onTap() {
  emit('tap')
}
</script>

<style scoped>
.app-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  border: 1px solid #f1f5f9;
}
.app-card--hover:active { transform: scale(0.98); }

.app-card--p-sm { padding: 10px 12px; }
.app-card--p-md { padding: 14px 16px; }
.app-card--p-lg { padding: 20px 22px; }
.app-card--p-none { padding: 0; }
</style>
