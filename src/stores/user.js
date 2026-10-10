/**
 * userStore — 用户状态管理（Pinia）
 */
import { defineStore } from 'pinia'
import api from '../utils/api.js'

const TOKEN_KEY = 'rocgit_token'
const USER_KEY = 'rocgit_user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null, // { userId, nickname, avatarUrl, openid, isNewUser }
    loading: false,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    nickname: (state) => state.userInfo?.nickname || '',
    userId: (state) => state.userInfo?.userId || 0,
  },

  actions: {
    // 从本地存储恢复
    init() {
      try {
        const token = uni.getStorageSync(TOKEN_KEY)
        const user = uni.getStorageSync(USER_KEY)
        if (token) this.token = token
        if (user && typeof user === 'object') this.userInfo = user
      } catch (e) {
        // ignore
      }
    },

    /**
     * 登录
     * @param {Object} payload { nickname, avatarUrl, code }
     * @returns {Promise<Object>} 用户信息
     */
    async login(payload = {}) {
      this.loading = true
      try {
        const user = await api.login(payload)
        if (user && user.token) {
          this.token = user.token
          this.userInfo = user
          try {
            uni.setStorageSync(TOKEN_KEY, user.token)
            uni.setStorageSync(USER_KEY, user)
          } catch (e) { /* ignore */ }
        }
        return user
      } finally {
        this.loading = false
      }
    },

    /** 退出登录 */
    logout() {
      this.token = ''
      this.userInfo = null
      try {
        uni.removeStorageSync(TOKEN_KEY)
        uni.removeStorageSync(USER_KEY)
      } catch (e) { /* ignore */ }
    },

    /** 刷新用户资料 */
    async fetchProfile() {
      try {
        const profile = await api.getProfile()
        if (profile) {
          this.userInfo = { ...this.userInfo, ...profile }
          try { uni.setStorageSync(USER_KEY, this.userInfo) } catch (e) { /* ignore */ }
        }
        return profile
      } catch (e) {
        console.warn('[userStore] 刷新资料失败:', e)
        return null
      }
    },

    /**
     * 更新用户资料
     * @param {Object} data { nickname, avatarUrl, ... }
     */
    async updateProfile(data) {
      const res = await api.updateProfile(data)
      await this.fetchProfile()
      return res
    },
  },
})

// 初始化一次（模块加载时从本地存储恢复）
// 注意：uni-app 中 Pinia 在 app.use 之后才能使用，
// 所以 init() 在 main.js 中 app.use(pinia) 之后调用
export default useUserStore
