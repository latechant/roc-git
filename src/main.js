import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { useUserStore, useProjectStore } from './stores'

export function createApp() {
  const app = createSSRApp(App)

  // 安装 Pinia
  const pinia = createPinia()
  app.use(pinia)

  // Pinia 安装完成后，恢复本地存储的状态
  const userStore = useUserStore()
  userStore.init()

  const projectStore = useProjectStore()
  projectStore.restoreCurrentId()

  return { app }
}
