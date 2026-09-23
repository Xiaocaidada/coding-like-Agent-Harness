<template>
  <div id="app">
    <div class="container-fluid">
      <!-- 顶部导航栏 -->
      <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
          <a class="navbar-brand" href="#">
            <i class="bi bi-shop"></i> 文创市集管理系统
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
              <li class="nav-item">
                <router-link class="nav-link" to="/dashboard">
                  <i class="bi bi-speedometer2"></i> 仪表板
                </router-link>
              </li>
            </ul>
            <ul class="navbar-nav">
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                  <i class="bi bi-person-circle"></i> {{ userStore.user?.username || '用户' }}
                </a>
                <ul class="dropdown-menu">
                  <li><a class="dropdown-item" href="#" @click="logout"><i class="bi bi-box-arrow-right"></i> 退出登录</a></li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <div class="row">
        <!-- 侧边栏 -->
        <div class="col-md-2 sidebar">
          <div class="p-3">
            <h6 class="text-muted mb-3">系统管理</h6>
            <ul class="nav flex-column">
              <li class="nav-item">
                <router-link class="nav-link" to="/users">
                  <i class="bi bi-people"></i> 用户管理
                </router-link>
              </li>
            </ul>
            
            <h6 class="text-muted mb-3 mt-4">市集管理</h6>
            <ul class="nav flex-column">
              <li class="nav-item">
                <router-link class="nav-link" to="/market-events">
                  <i class="bi bi-calendar-event"></i> 市集活动
                </router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/booths">
                  <i class="bi bi-grid-3x3"></i> 摊位管理
                </router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/applications">
                  <i class="bi bi-clipboard-check"></i> 报名申请
                </router-link>
              </li>
            </ul>

            <h6 class="text-muted mb-3 mt-4">业务管理</h6>
            <ul class="nav flex-column">
              <li class="nav-item">
                <router-link class="nav-link" to="/orders">
                  <i class="bi bi-receipt"></i> 订单管理
                </router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/statistics">
                  <i class="bi bi-graph-up"></i> 客流统计
                </router-link>
              </li>
            </ul>
          </div>
        </div>

        <!-- 主要内容区域 -->
        <div class="col-md-10 main-content">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from './store/auth.js'
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as bootstrap from 'bootstrap'

const authStore = useAuthStore()
const router = useRouter()

const userStore = authStore

const logout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  // 检查登录状态
  if (!authStore.isAuthenticated) {
    router.push('/login')
  }
})
</script>

<style>
body {
  background-color: #f8f9fa;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link i {
  font-size: 14px;
}

.sidebar {
  min-height: calc(100vh - 56px);
}

.main-content {
  background-color: #fff;
  min-height: calc(100vh - 56px);
}
</style>