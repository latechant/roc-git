<!--
  TabBar — 底部导航栏
  Props:
    - current: String   // 当前选中的 tab key (home/messages/profile)
    - unreadCount: Number  // 消息未读数
  Events:
    - change(tabKey)
-->
<template>
  <view class="tab-bar">
    <view
      v-for="item in tabs"
      :key="item.key"
      class="tab-bar__item"
      :class="{ 'tab-bar__item--active': current === item.key }"
      @tap="onTap(item.key)"
    >
      <view class="tab-bar__icon-wrap">
        <text class="tab-bar__icon">{{ item.icon }}</text>
        <view v-if="item.key === 'messages' && unreadCount > 0" class="tab-bar__badge">
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </view>
      </view>
      <text class="tab-bar__label">{{ item.label }}</text>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  current: { type: String, default: 'home' },
  unreadCount: { type: Number, default: 0 },
})
const emit = defineEmits(['change'])

const tabs = [
  { key: 'home', label: '首页', icon: '🏠' },
  { key: 'messages', label: '消息', icon: '🔔' },
  { key: 'profile', label: '我的', icon: '👤' },
]

function onTap(key) {
  if (key === props.current) return
  emit('change', key)
}
</script>

<style scoped>
.tab-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60px;
  background: #fff;
  border-top: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 100;
}
.tab-bar__item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
}
.tab-bar__icon-wrap {
  position: relative;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tab-bar__icon { font-size: 20px; opacity: 0.6; }
.tab-bar__item--active .tab-bar__icon { opacity: 1; }
.tab-bar__label {
  font-size: 11px;
  color: #94a3b8;
}
.tab-bar__item--active .tab-bar__label { color: #2563eb; font-weight: 500; }

.tab-bar__badge {
  position: absolute;
  top: -2px;
  right: -6px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 8px;
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}
</style>
