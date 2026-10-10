<!--
  AppButton — 通用按钮组件
  Props:
    - type: 'primary' | 'secondary' | 'danger' | 'ghost'
    - size: 'sm' | 'md' | 'lg'
    - disabled: Boolean
    - loading: Boolean
    - block: Boolean  // 宽度 100%
-->
<template>
  <view
    class="app-btn"
    :class="[
      `app-btn--${type}`,
      `app-btn--${size}`,
      { 'app-btn--block': block, 'app-btn--disabled': disabled || loading }
    ]"
    @tap="onTap"
  >
    <text v-if="loading" class="app-btn__loading">●</text>
    <slot />
  </view>
</template>

<script setup>
const props = defineProps({
  type: { type: String, default: 'primary' },
  size: { type: String, default: 'md' },
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  block: { type: Boolean, default: false },
})
const emit = defineEmits(['tap'])

function onTap() {
  if (props.disabled || props.loading) return
  emit('tap')
}
</script>

<style scoped>
.app-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-radius: 12px;
  font-weight: 500;
  transition: opacity 0.2s;
}
.app-btn--block { width: 100%; }
.app-btn--disabled { opacity: 0.5; }

/* size */
.app-btn--sm { height: 32px; padding: 0 14px; font-size: 12px; border-radius: 8px; }
.app-btn--md { height: 40px; padding: 0 18px; font-size: 14px; }
.app-btn--lg { height: 48px; padding: 0 24px; font-size: 15px; border-radius: 14px; }

/* type */
.app-btn--primary { background: #2563eb; color: #fff; }
.app-btn--secondary { background: #f1f5f9; color: #334155; }
.app-btn--danger { background: #fee2e2; color: #dc2626; }
.app-btn--ghost { background: transparent; color: #64748b; border: 1px solid #e2e8f0; }

.app-btn__loading {
  display: inline-block;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
