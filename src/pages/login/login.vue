<template>
  <view class="login-screen">
    <view class="login-card">
      <view class="login-title">ROC-git</view>
      <view class="login-subtitle">课程项目协作平台</view>

      <view class="input-wrap">
        <input
          class="nickname-input"
          v-model="nickname"
          placeholder="请输入昵称"
          maxlength="20"
          confirm-type="done"
          @confirm="onLogin"
        />
      </view>

      <button class="login-btn" :disabled="loading" @tap="onLogin">
        {{ loading ? '登录中...' : '登 录' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import api from '../../utils/api.js'

const nickname = ref('')
const loading = ref(false)

// 获取或生成设备唯一 ID
function getDeviceId() {
  let id = ''
  try {
    id = uni.getStorageSync('rocgit_device_id') || ''
  } catch (e) { /* ignore */ }
  if (!id) {
    id = 'dev_' + Date.now() + '_' + Math.random().toString(36).slice(2, 8)
    try { uni.setStorageSync('rocgit_device_id', id) } catch (e) { /* ignore */ }
  }
  return id
}

onLoad(() => {
  // 已有 token 直接进首页
  const token = api.getToken()
  if (token) {
    uni.reLaunch({ url: '/pages/home/home' })
  }
})

async function onLogin() {
  const name = (nickname.value || '').trim()
  if (!name) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const deviceId = getDeviceId()
    const code = 'wx_user_' + deviceId
    await api.login({ nickname: name, code })
    uni.reLaunch({ url: '/pages/home/home' })
  } catch (e) {
    // api.login 内部 silent=true 不弹 toast，这里手动提示
    uni.showToast({ title: (e && e.message) || '登录失败，请重试', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-screen {
  width: 100%;
  height: 100vh;
  height: 100dvh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #F8FAFC;
}
.login-card {
  width: 80%;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}
.login-subtitle {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 48px;
}
.input-wrap {
  width: 100%;
  margin-bottom: 24px;
}
.nickname-input {
  width: 100%;
  height: 48px;
  padding: 0 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #ffffff;
  font-size: 16px;
  color: #1e293b;
  outline: none;
}
.login-btn {
  width: 100%;
  height: 48px;
  line-height: 48px;
  border: none;
  border-radius: 8px;
  background: #2563eb;
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
}
.login-btn::after {
  border: none;
}
.login-btn[disabled] {
  opacity: 0.6;
}
</style>
