<!--
  登录页
  功能：昵称输入 + 登录 + JWT 存储 + 跳转首页
  小程序端：微信一键登录（uni.login 真实 code，昵称可用 type=nickname 一键填入，可空）
  接入：userStore + AppButton 组件
-->
<template>
  <view class="login-screen">
    <view class="login-card">
      <view class="login-logo">📚</view>
      <view class="login-title">ROC-git</view>
      <view class="login-subtitle">课程项目协作平台</view>

      <view class="input-wrap">
        <!-- #ifdef MP-WEIXIN -->
        <input
          class="nickname-input"
          type="nickname"
          v-model="nickname"
          placeholder="点这里一键填入微信昵称（可选）"
          maxlength="20"
          :disabled="userStore.loading"
        />
        <!-- #endif -->
        <!-- #ifdef H5 -->
        <input
          class="nickname-input"
          v-model="nickname"
          placeholder="请输入昵称"
          maxlength="20"
          confirm-type="done"
          :disabled="userStore.loading"
          @confirm="handleLogin"
        />
        <!-- #endif -->
      </view>

      <AppButton
        type="primary"
        size="lg"
        block
        :loading="userStore.loading"
        :disabled="userStore.loading"
        @tap="handleLogin"
      >
        <!-- #ifdef MP-WEIXIN -->
        {{ userStore.loading ? '登录中...' : '微信一键登录' }}
        <!-- #endif -->
        <!-- #ifdef H5 -->
        {{ userStore.loading ? '进入中...' : '进入平台' }}
        <!-- #endif -->
      </AppButton>

      <view class="login-tip">
        <text class="login-tip__text">
          登录即表示同意
          <text class="login-tip__link">《用户协议》</text>
          和
          <text class="login-tip__link">《隐私政策》</text>
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/index.js'
import AppButton from '../../components/AppButton.vue'

const userStore = useUserStore()
const nickname = ref('')

onLoad(() => {
  // 已有完整登录态 → 预填昵称
  // 只有 token 没有用户信息 → 清理残留
  if (userStore.isLoggedIn && userStore.userInfo) {
    nickname.value = userStore.userInfo.nickname || ''
  } else if (userStore.token && !userStore.userInfo) {
    userStore.logout()
  }
})

/**
 * 获取登录凭证 code
 *  - 微信小程序：uni.login 换 code
 *  - H5：用设备 ID 生成演示 code
 */
function fetchLoginCode() {
  return new Promise((resolve, reject) => {
    // #ifdef MP-WEIXIN
    uni.login({
      provider: 'weixin',
      success: (res) => {
        if (res && res.code) {
          resolve(res.code)
        } else {
          reject(new Error('微信登录凭证获取失败'))
        }
      },
      fail: () => reject(new Error('微信登录失败，请检查网络或 AppID 配置'))
    })
    // #endif

    // #ifdef H5
    let deviceId = ''
    try { deviceId = uni.getStorageSync('rocgit_device_id') || '' } catch (e) { /* ignore */ }
    if (!deviceId) {
      deviceId = 'dev_' + Date.now() + '_' + Math.random().toString(36).slice(2, 8)
      try { uni.setStorageSync('rocgit_device_id', deviceId) } catch (e) { /* ignore */ }
    }
    resolve('wx_user_' + deviceId)
    // #endif
  })
}

async function handleLogin() {
  let name = (nickname.value || '').trim()
  // #ifdef H5
  if (!name) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  // #endif
  // #ifdef MP-WEIXIN
  // 一键登录：昵称可空，空则用固定默认名；老用户已预填原昵称，不会被覆盖
  if (!name) name = '微信用户'
  // #endif

  try {
    const code = await fetchLoginCode()
    await userStore.login({ nickname: name, code })
    uni.reLaunch({ url: '/pages/home/home' })
  } catch (e) {
    uni.showToast({
      title: (e && e.message) || '登录失败，请重试',
      icon: 'none'
    })
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
  background: #f8fafc;
}

.login-card {
  width: 80%;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-logo {
  font-size: 56px;
  margin-bottom: 16px;
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
  margin-bottom: 40px;
}

.input-wrap {
  width: 100%;
  margin-bottom: 20px;
}

.nickname-input {
  width: 100%;
  height: 48px;
  padding: 0 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #ffffff;
  font-size: 16px;
  color: #1e293b;
  outline: none;
  box-sizing: border-box;
}
.nickname-input:focus {
  border-color: #2563eb;
}

.login-tip {
  margin-top: 20px;
}
.login-tip__text {
  font-size: 12px;
  color: #94a3b8;
}
.login-tip__link {
  color: #2563eb;
}
</style>
