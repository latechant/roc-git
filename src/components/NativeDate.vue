
<template>
  <!-- #ifdef H5 -->
  <input
    type="date"
    class="original-native-date"
    :value="value"
    @input="handleH5Input"
  />
  <!-- #endif -->

  <!-- #ifdef MP-WEIXIN -->
  <picker
    mode="date"
    :value="value"
    @change="handlePick"
  >
    <view class="original-native-date">
      {{ value || '选择日期' }}
    </view>
  </picker>
  <!-- #endif -->
</template>

<script setup>
defineProps({
  value: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:value'])

// 网页端
function handleH5Input(e) {
  const value = e.detail?.value ?? e.target?.value ?? ''
  emit('update:value', value)
}

// 微信小程序端
function handlePick(e) {
  emit('update:value', e.detail.value)
}
</script>
