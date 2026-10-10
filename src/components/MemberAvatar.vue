<!--
  MemberAvatar — 成员头像（首字 + 背景色）
  Props:
    - name: String    // 姓名（取首字）
    - size: 'xs' | 'sm' | 'md' | 'lg'
    - src: String     // 头像 URL（优先显示图片）
    - role: String    // 角色：leader / member（可选，显示角标）
-->
<template>
  <view class="member-avatar" :class="[`member-avatar--${size}`, { 'member-avatar--leader': role === 'leader' }]" :style="bgStyle">
    <image v-if="src" :src="src" class="member-avatar__img" mode="aspectFill" />
    <text v-else class="member-avatar__text">{{ firstChar }}</text>
    <view v-if="role === 'leader'" class="member-avatar__badge">组</view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: { type: String, default: '?' },
  size: { type: String, default: 'md' },
  src: { type: String, default: '' },
  role: { type: String, default: '' }, // leader / member
})

const firstChar = computed(() => {
  const n = props.name || '?'
  return n.charAt(0).toUpperCase()
})

// 根据姓名生成稳定背景色
const colorPalette = [
  '#38bdf8', '#f87171', '#4ade80', '#fbbf24', '#a78bfa',
  '#fb7185', '#34d399', '#60a5fa', '#facc15', '#c084fc',
]
const bgStyle = computed(() => {
  if (props.src) return {}
  let hash = 0
  for (let i = 0; i < props.name.length; i++) {
    hash = props.name.charCodeAt(i) + ((hash << 5) - hash)
  }
  const idx = Math.abs(hash) % colorPalette.length
  return { background: colorPalette[idx] }
})
</script>

<style scoped>
.member-avatar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  color: #fff;
  font-weight: 600;
}
.member-avatar--xs { width: 20px; height: 20px; font-size: 9px; }
.member-avatar--sm { width: 28px; height: 28px; font-size: 11px; }
.member-avatar--md { width: 36px; height: 36px; font-size: 14px; }
.member-avatar--lg { width: 48px; height: 48px; font-size: 18px; }

.member-avatar__img { width: 100%; height: 100%; }

.member-avatar__badge {
  position: absolute;
  right: -2px;
  bottom: -2px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #f59e0b;
  color: #fff;
  font-size: 8px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1.5px solid #fff;
}
</style>
