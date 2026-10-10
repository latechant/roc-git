/**
 * messageStore — 消息通知状态管理（Pinia）
 */
import { defineStore } from 'pinia'
import api from '../utils/api.js'

export const useMessageStore = defineStore('message', {
  state: () => ({
    list: [],
    unreadCount: {
      total: 0,
      task: 0,
      audit: 0,
      mention: 0,
      system: 0,
    },
    loading: false,
  }),

  actions: {
    /** 获取未读数 */
    async fetchUnreadCount() {
      try {
        const res = await api.unreadCount()
        if (res && typeof res.total === 'number') {
          this.unreadCount = {
            total: res.total || 0,
            task: res.task || 0,
            audit: res.audit || 0,
            mention: res.mention || 0,
            system: res.system || 0,
          }
        }
        return this.unreadCount
      } catch (e) {
        console.warn('[messageStore] 获取未读数失败:', e)
        return this.unreadCount
      }
    },

    /** 获取消息列表 */
    async fetchList(params = {}) {
      this.loading = true
      try {
        const res = await api.messageList({ pageNum: 1, pageSize: 50, ...params })
        this.list = (res?.list || res?.records || (Array.isArray(res) && res)) || []
        return this.list
      } finally {
        this.loading = false
      }
    },

    /** 全部已读 */
    async readAll() {
      await api.readAll()
      this.unreadCount.total = 0
      this.unreadCount.task = 0
      this.unreadCount.audit = 0
      this.unreadCount.mention = 0
      this.unreadCount.system = 0
      this.list.forEach(m => { if (!m.read) m.read = true })
    },

    /** 单条已读 */
    async readMessage(id) {
      try {
        await api.readMessage(id)
        const msg = this.list.find(m => m.messageId === id || m.id === id)
        if (msg) msg.read = true
        if (this.unreadCount.total > 0) this.unreadCount.total--
      } catch (e) {
        console.warn('[messageStore] 标记已读失败:', e)
      }
    },

    /** 删除消息 */
    async deleteMessage(id) {
      await api.deleteMessage(id)
      this.list = this.list.filter(m => m.messageId !== id && m.id !== id)
    },
  },
})

export default useMessageStore
