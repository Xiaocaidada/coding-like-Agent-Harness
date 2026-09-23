import http from '../utils/http'

export const userApi = {
  // 获取用户列表
  getUsers(params) {
    return http.get('/users', { params })
  },
  
  // 获取用户详情
  getUserById(id) {
    return http.get(`/users/${id}`)
  },
  
  // 创建用户
  createUser(data) {
    return http.post('/users', data)
  },
  
  // 更新用户
  updateUser(id, data) {
    return http.put(`/users/${id}`, data)
  },
  
  // 删除用户
  deleteUser(id) {
    return http.delete(`/users/${id}`)
  },
  
  // 重置密码
  resetPassword(id) {
    return http.post(`/users/${id}/reset-password`)
  }
}

export default userApi