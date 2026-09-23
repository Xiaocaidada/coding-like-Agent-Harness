import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '../utils/http'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // 登录
  const login = async (username, password) => {
    try {
      const response = await http.post('/auth/login', {
        username,
        password
      })
      
      token.value = response.data.token
      user.value = response.data.user
      
      // 保存token到本地存储
      localStorage.setItem('token', token.value)
      
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await http.get('/auth/user')
      user.value = response.data
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 更新用户信息
  const updateUserInfo = async (userData) => {
    try {
      const response = await http.put('/auth/user', userData)
      user.value = response.data
      return response.data
    } catch (error) {
      throw error
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    login,
    logout,
    getUserInfo,
    updateUserInfo
  }
})