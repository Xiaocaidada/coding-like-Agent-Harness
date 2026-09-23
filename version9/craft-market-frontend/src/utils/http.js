import axios from 'axios'
import { useAuthStore } from '../store/auth'

// 创建axios实例
const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
http.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.token
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  (response) => {
    const data = response.data
    
    // 处理统一响应格式
    if (data.code === 200) {
      return data
    } else {
      // 业务错误
      throw new Error(data.message || '操作失败')
    }
  },
  (error) => {
    const authStore = useAuthStore()
    
    if (error.response) {
      const { status } = error.response
      
      switch (status) {
        case 401:
          authStore.logout()
          throw new Error('登录已过期，请重新登录')
        case 403:
          throw new Error('权限不足')
        case 404:
          throw new Error('资源不存在')
        case 500:
          throw new Error('服务器内部错误')
        default:
          throw new Error(error.response.data.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      throw new Error('请求超时')
    } else {
      throw new Error('网络错误')
    }
  }
)

export default http