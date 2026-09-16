<template>
  <view v-if="!node.removed" :class="classes(node.className)" :style="node.style">
    <template v-for="(child, index) in items" :key="child.id || index">
      <text v-if="child.text">{{ child.text }}</text>
      <input v-else-if="child.tag === 'input' && child.attrs.type !== 'date'" :class="classes(child.className)" class="original-input" :value="child.value" :placeholder="child.attrs.placeholder || ''" :maxlength="-1" @input="child.value = $event.detail.value" />
      <template v-else-if="child.tag === 'input' && child.attrs.type === 'date'">
        <!-- #ifdef H5 -->
        <NativeDate :class="classes(child.className)" v-model:value="child.value" />
        <!-- #endif -->
        <!-- #ifndef H5 -->
        <picker :class="classes(child.className)" class="original-date" mode="date" :value="child.value" @change="child.value = $event.detail.value"><view>{{ child.value ? child.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
        <!-- #endif -->
      </template>
      <view v-else :class="classes(child.className)" :style="child.style">
        <text v-for="(label, labelIndex) in child.children" :key="labelIndex">{{ label.text }}</text>
      </view>
    </template>
  </view>
</template>

<script setup>
import { computed, inject } from 'vue'
// #ifdef H5
import NativeDate from './NativeDate.vue'
// #endif
const props = defineProps({ nodeId: { type: String, required: true } })
const { ui, classes } = inject('pageController')
const node = computed(() => ui[props.nodeId])
// The original prototype adds one form row containing labels, inputs and dates.
const items = computed(() => node.value.children.map(ref => ref.id ? ui[ref.id] : ref))
</script>

<script>
export default { options: { virtualHost: true, styleIsolation: 'shared' } }
</script>
