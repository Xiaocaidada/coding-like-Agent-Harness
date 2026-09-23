import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import App from './App.vue'
import routes from './router/index.js'

// 创建路由器
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 创建状态管理
const pinia = createPinia()

// 创建应用实例
const app = createApp(App)

// 使用插件
app.use(router)
app.use(pinia)

// 挂载应用
app.mount('#app')