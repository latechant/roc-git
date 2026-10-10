/**
 * groupStore — 小组状态管理（Pinia）
 */
import { defineStore } from 'pinia'
import api from '../utils/api.js'

export const useGroupStore = defineStore('group', {
  state: () => ({
    groups: [],          // 我的小组列表
    loading: false,
    currentGroupId: null,
    members: [],         // 当前小组成员
    inviteCodeMap: {},   // { groupId: inviteCode }
  }),

  actions: {
    /** 获取我的小组列表 */
    async fetchMyGroups() {
      this.loading = true
      try {
        const list = await api.myGroups()
        this.groups = Array.isArray(list) ? list : []
        if (this.groups.length && !this.currentGroupId) {
          this.currentGroupId = this.groups[0].groupId
        }
        return this.groups
      } finally {
        this.loading = false
      }
    },

    /** 获取小组成员 */
    async fetchMembers(groupId) {
      const gid = groupId || this.currentGroupId
      if (!gid) return []
      const res = await api.groupMembers(gid)
      this.members = (res && res.members) || []
      return this.members
    },

    /** 创建小组 */
    async createGroup(name) {
      const res = await api.createGroup({ name })
      await this.fetchMyGroups()
      return res
    },

    /** 加入小组 */
    async joinGroup(inviteCode) {
      const res = await api.joinGroup({ inviteCode })
      await this.fetchMyGroups()
      return res
    },

    /** 获取/生成邀请码 */
    async getInviteCode(groupId) {
      const gid = groupId || this.currentGroupId
      if (!gid) return ''
      if (this.inviteCodeMap[gid]) return this.inviteCodeMap[gid]
      try {
        const res = await api.regenerateInviteCode(gid)
        const code = res?.inviteCode || res?.code || ''
        if (code) this.inviteCodeMap[gid] = code
        return code
      } catch (e) {
        console.warn('[groupStore] 获取邀请码失败:', e)
        return ''
      }
    },

    /** 重新生成邀请码 */
    async regenerateInviteCode(groupId) {
      const gid = groupId || this.currentGroupId
      if (!gid) return ''
      const res = await api.regenerateInviteCode(gid)
      const code = res?.inviteCode || res?.code || ''
      if (code) this.inviteCodeMap[gid] = code
      return code
    },

    /** 设置当前小组 */
    setCurrentGroup(groupId) {
      this.currentGroupId = groupId
      this.members = []
    },
  },
})

export default useGroupStore
