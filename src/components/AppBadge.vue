<!--
  AppBadge — 状态徽章 / 标签
  Props:
    - type: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
    - size: 'sm' | 'md'
    - text: String   // 徽章文字（也可通过 slot）
-->
<template>
  <view class="app-badge" :class="[`app-badge--${type}`, `app-badge--${size}`]">
    <slot>{{ text }}</slot>
  </view>
</template>

<script setup>
defineProps({
  type: { type: String, default: 'default' },
  size: { type: String, default: 'sm' },
  text: { type: String, default: '' },
})
</script>

<style scoped>
.app-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  font-weight: 600;
  line-height: 1;
}
.app-badge--sm { padding: 3px 8px; font-size: 10px; }
.app-badge--md { padding: 4px 10px; font-size: 12px; }

.app-badge--primary { background: #dbeafe; color: #2563eb; }
.app-badge--success { background: #d1fae5; color: #059669; }
.app-badge--warning { background: #fef3c7; color: #d97706; }
.app-badge--danger { background: #fee2e2; color: #dc2626; }
.app-badge--info { background: #ede9fe; color: #7c3aed; }
.app-badge--default { background: #f1f5f9; color: #64748b; }
</style>
