/**
 * stores 统一出口
 *
 * 用法：
 *   import { useUserStore } from '@/stores'
 *   const userStore = useUserStore()
 *   console.log(userStore.token, userStore.nickname)
 *   await userStore.login(...)
 */
export { useUserStore } from './user.js'
export { useGroupStore } from './group.js'
export { useProjectStore } from './project.js'
export { useMessageStore } from './message.js'
