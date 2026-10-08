<template>
  <view v-if="!node.removed" :class="classes(node.className)" :style="node.style">
    <template v-for="(child, index) in items" :key="child.id || index">
      <text v-if="child.text">{{ child.text }}</text>
      <input v-else-if="child.tag === 'input' && child.attrs.type !== 'date'" :class="classes(child.className)" class="original-input" :value="child.value" :placeholder="child.attrs.placeholder || ''" :maxlength="-1" @input="child.value = $event.detail.value" />
      <template v-else-if="child.tag === 'input' && child.attrs.type === 'date'">
        <NativeDate :class="classes(child.className)" v-model:value="child.value" />
      </template>
      <template v-else-if="child.tag === 'picker'">
        <picker :class="classes(child.className)" class="original-select" mode="selector" :range="child.className.includes('stage-pick') ? stageNames : memberNames" :value="child.value" @change="child.value = $event.detail.value"><view>{{ child.className.includes('stage-pick') ? (stageNames[child.value] || '所属阶段') : (memberNames[child.value] || memberNames[0]) }}</view></picker>
      </template>
      <!-- 动态行：无条件原生 div（不经过 uni-view 属性过滤，data-* 正常落 DOM，事件走 page-controller 的 document click 委托） -->
      <div v-else :class="classes(child.className)" :style="child.style"
        :data-action="child.attrs && child.attrs['data-action']"
        :data-stage-id="child.attrs && child.attrs['data-stage-id']"
        :data-task-id="child.attrs && child.attrs['data-task-id']"
        :data-file-id="child.attrs && child.attrs['data-file-id']"
        :data-version="child.attrs && child.attrs['data-version']"
        :data-id="child.attrs && child.attrs['data-id']"
        :data-pid="child.attrs && child.attrs['data-pid']"
        :data-cat="child.attrs && child.attrs['data-cat']">
        <template v-for="(sub, si) in child.children" :key="sub.id || si">
          <NativeFragment v-if="sub.id && ui[sub.id]" :node-id="sub.id" />
          <text v-else>{{ sub.text }}</text>
        </template>
      </div>
    </template>
    <text v-if="removable" class="stage-del" @tap="pc.removeRow(props.nodeId, props.nodeId)">×</text>
  </view>
</template>

<script setup>
import { computed, inject } from 'vue'
import NativeDate from './NativeDate.vue'
const props = defineProps({ nodeId: { type: String, required: true }, removable: { type: Boolean, default: false } })
const pc = inject('pageController')
const { ui, classes } = pc
const node = computed(() => ui[props.nodeId])
// The original prototype adds one form row containing labels, inputs and dates.
const items = computed(() => node.value.children.map(ref => ref.id ? ui[ref.id] : ref))
// 负责人选择器数据：从成员池映射昵称；池为空时兜底当前用户昵称
const memberNames = computed(() => {
  const pool = (pc.rocMemberPool || [])
  const names = pool.map(m => m.nickname || '')
  if (!names.length && pc.currentUserNickname) names.push(pc.currentUserNickname)
  return names
})
// 所属阶段选择器数据：追加任务行的阶段选项（创建页已添加阶段名）
const stageNames = computed(() => (pc.stageNames || []))
</script>

<script>
export default { options: { virtualHost: true, styleIsolation: 'shared' } }
</script>
