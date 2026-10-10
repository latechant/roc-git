/**
 * projectStore — 项目状态管理（Pinia）
 */
import { defineStore } from 'pinia'
import api from '../utils/api.js'

const STORAGE_KEY = 'rocgit_current_project_id'

export const useProjectStore = defineStore('project', {
  state: () => ({
    projects: [],          // 当前小组的项目列表
    currentProjectId: null,
    currentProject: null,  // 当前项目详情
    stages: [],            // 当前项目阶段
    files: [],             // 当前项目文件
    tasks: [],             // 当前项目任务
    members: [],           // 当前项目成员
    activities: [],        // 操作日志（溯源）
    loading: false,
  }),

  actions: {
    // 从本地存储恢复当前项目 ID
    restoreCurrentId() {
      try {
        const id = uni.getStorageSync(STORAGE_KEY)
        if (id) this.currentProjectId = Number(id)
      } catch (e) { /* ignore */ }
    },

    saveCurrentId() {
      try {
        uni.setStorageSync(STORAGE_KEY, this.currentProjectId || '')
      } catch (e) { /* ignore */ }
    },

    /** 获取项目列表（按当前小组） */
    async fetchProjectList(groupId) {
      if (!groupId) return []
      this.loading = true
      try {
        const list = await api.projectList(groupId)
        this.projects = Array.isArray(list) ? list : []
        return this.projects
      } finally {
        this.loading = false
      }
    },

    /** 获取项目详情 */
    async fetchProjectDetail(projectId) {
      const pid = projectId || this.currentProjectId
      if (!pid) return null
      this.loading = true
      try {
        const detail = await api.projectDetail(pid)
        this.currentProject = detail
        this.currentProjectId = pid
        this.saveCurrentId()
        return detail
      } finally {
        this.loading = false
      }
    },

    /** 获取阶段列表 */
    async fetchStages(projectId) {
      const pid = projectId || this.currentProjectId
      if (!pid) return []
      const list = await api.projectStages(pid)
      this.stages = Array.isArray(list) ? list : []
      return this.stages
    },

    /** 获取文件列表 */
    async fetchFiles(projectId, params = {}) {
      const pid = projectId || this.currentProjectId
      if (!pid) return []
      const res = await api.fileList({ projectId: pid, ...params })
      this.files = (res?.list || res?.records || (Array.isArray(res) && res)) || []
      return this.files
    },

    /** 获取任务列表 */
    async fetchTasks(projectId, params = {}) {
      const pid = projectId || this.currentProjectId
      if (!pid) return []
      const res = await api.taskList(pid, { pageNum: 1, pageSize: 50, ...params })
      this.tasks = (res?.list || res?.records || res?.tasks || (Array.isArray(res) && res)) || []
      return this.tasks
    },

    /** 获取项目成员 */
    async fetchProjectMembers(projectId) {
      const pid = projectId || this.currentProjectId
      if (!pid) return []
      const res = await api.projectMembers(pid)
      this.members = (res?.members || (Array.isArray(res) && res)) || []
      return this.members
    },

    /** 获取操作日志（溯源） */
    async fetchActivities(projectId, params = {}) {
      const pid = projectId || this.currentProjectId
      if (!pid) return []
      const res = await api.projectActivities(pid, { pageNum: 1, pageSize: 50, ...params })
      this.activities = (res?.list || res?.records || (Array.isArray(res) && res)) || []
      return this.activities
    },

    /** 设置当前项目 */
    setCurrentProject(projectId) {
      this.currentProjectId = projectId
      this.currentProject = null
      this.stages = []
      this.files = []
      this.tasks = []
      this.members = []
      this.activities = []
      this.saveCurrentId()
    },

    /** 创建项目 */
    async createProject(data) {
      const res = await api.createProject(data)
      return res
    },

    /** 刷新当前项目全部数据（操作后调用） */
    async refreshAll(projectId) {
      const pid = projectId || this.currentProjectId
      if (!pid) return
      await Promise.all([
        this.fetchProjectDetail(pid),
        this.fetchStages(pid),
        this.fetchFiles(pid),
        this.fetchTasks(pid),
      ]).catch(() => {})
    },
  },
})

export default useProjectStore
