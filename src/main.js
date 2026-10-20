import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { useUserStore, useProjectStore } from './stores'
import AppIcon from './components/AppIcon.vue'

export function createApp() {
  const app = createSSRApp(App)

  // 安装 Pinia
  const pinia = createPinia()
  app.use(pinia)

  // 全局注册线性 SVG 图标组件
  app.component('AppIcon', AppIcon)

  // Pinia 安装完成后，恢复本地存储的状态
  const userStore = useUserStore()
  userStore.init()

  const projectStore = useProjectStore()
  projectStore.restoreCurrentId()

  return { app }
}
