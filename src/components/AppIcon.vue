<template>
  <image
    class="app-icon"
    :src="iconSrc"
    :style="{ width: pxSize, height: pxSize }"
    mode="aspectFit"
  />
</template>

<script setup>
import { computed } from 'vue'
import { iconPaths } from '../utils/iconPaths'

/**
 * 线性 SVG 图标组件（基于 Lucide）
 * 使用 data:image/svg+xml + <image> 渲染，兼容 H5 与微信小程序。
 * 小程序不支持内联 <svg>，因此用 data URI 方式。
 */
const props = defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 20 },
  color: { type: String, default: '#0f172a' }
})

const pxSize = computed(() => {
  const s = props.size
  return typeof s === 'number' ? s + 'px' : /^\d+$/.test(s) ? s + 'px' : s
})

const iconSrc = computed(() => {
  const inner = iconPaths[props.name] || iconPaths['file-text'] || ''
  // data URI 内的 svg 脱离 DOM，currentColor 无效，必须注入实际颜色
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="${props.color}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">${inner}</svg>`
  return 'data:image/svg+xml;utf8,' + encodeURIComponent(svg)
})
</script>

<style scoped>
.app-icon {
  display: inline-block;
  vertical-align: middle;
  flex-shrink: 0;
}
</style>
