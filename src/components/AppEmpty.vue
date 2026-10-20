<!--
  AppEmpty — 空状态占位
  Props:
    - text: String  // 提示文字
    - icon: String  // 线性图标名（AppIcon name）
    - size: 'sm' | 'md' | 'lg'
-->
<template>
  <view class="app-empty" :class="`app-empty--${size}`">
    <app-icon class="app-empty__icon" :name="icon" :size="iconSize" color="#cbd5e1" />
    <text class="app-empty__text">{{ text }}</text>
    <slot name="action" />
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  text: { type: String, default: '暂无数据' },
  icon: { type: String, default: 'inbox' },
  size: { type: String, default: 'md' },
})

const sizeMap = { sm: 28, md: 40, lg: 56 }
const iconSize = computed(() => sizeMap[props.size] || 40)
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
  margin-bottom: 12px;
  opacity: 0.8;
}
.app-empty--sm .app-empty__icon { margin-bottom: 8px; }
.app-empty--lg .app-empty__icon { margin-bottom: 16px; }

.app-empty__text {
  font-size: 13px;
  color: #94a3b8;
}
.app-empty--sm .app-empty__text { font-size: 12px; }
.app-empty--lg .app-empty__text { font-size: 14px; }
</style>
