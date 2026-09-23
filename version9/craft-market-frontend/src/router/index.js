import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      redirect: '/dashboard',
      meta: { requiresAuth: true }
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'Users',
      component: () => import('../views/UsersView.vue'),
      meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
      path: '/market-events',
      name: 'MarketEvents',
      component: () => import('../views/MarketEventsView.vue'),
      meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
      path: '/booths',
      name: 'Booths',
      component: () => import('../views/BoothsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/applications',
      name: 'Applications',
      component: () => import('../views/ApplicationsView.vue'),
      meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
      path: '/orders',
      name: 'Orders',
      component: () => import('../views/OrdersView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/statistics',
      name: 'Statistics',
      component: () => import('../views/StatisticsView.vue'),
      meta: { requiresAuth: true, role: 'ADMIN' }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }
  
  // 检查角色权限
  if (to.meta.role && authStore.user?.role !== to.meta.role) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router