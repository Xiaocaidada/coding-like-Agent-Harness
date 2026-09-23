import http from '../utils/http'

export const authApi = {
  // 用户登录
  login(data) {
    return http.post('/auth/login', data)
  },
  
  // 获取用户信息
  getUserInfo() {
    return http.get('/auth/user')
  },
  
  // 更新用户信息
  updateUserInfo(data) {
    return http.put('/auth/user', data)
  },
  
  // 修改密码
  changePassword(data) {
    return http.post('/auth/change-password', data)
  }
}

export default authApi