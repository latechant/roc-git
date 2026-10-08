import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    host: '127.0.0.1',
    port: 5173,
    proxy: {
      // 本地调试：/api 代理到已部署后端（如本机起后端则改为 http://127.0.0.1:8080）
      '/api': { target: 'http://116.62.81.113:8080', changeOrigin: true }
    }
  }
})
