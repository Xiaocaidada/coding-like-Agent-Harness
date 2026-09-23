<template>
  <div class="container-fluid">
    <div class="row justify-content-center align-items-center min-vh-100">
      <div class="col-md-6 col-lg-4">
        <div class="card shadow">
          <div class="card-body p-5">
            <div class="text-center mb-4">
              <i class="bi bi-shop text-primary" style="font-size: 3rem;"></i>
              <h2 class="mt-3">文创市集管理系统</h2>
              <p class="text-muted">请登录您的账户</p>
            </div>

            <div class="alert alert-danger" v-if="error" style="display: none;" ref="errorAlert">
              <i class="bi bi-exclamation-triangle"></i> {{ error }}
            </div>

            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label for="username" class="form-label">
                  <i class="bi bi-person"></i> 用户名
                </label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="username" 
                  v-model="formData.username"
                  required
                  placeholder="请输入用户名"
                >
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">
                  <i class="bi bi-lock"></i> 密码
                </label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password" 
                  v-model="formData.password"
                  required
                  placeholder="请输入密码"
                >
              </div>

              <div class="d-grid">
                <button 
                  type="submit" 
                  class="btn btn-primary btn-lg"
                  :disabled="loading"
                >
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  {{ loading ? '登录中...' : '登录' }}
                </button>
              </div>
            </form>

            <div class="text-center mt-4">
              <p class="text-muted">
                &copy; 2024 文创市集管理系统
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const authStore = useAuthStore()

const formData = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref('')
const errorAlert = ref(null)

const handleLogin = async () => {
  try {
    // 隐藏错误信息
    error.value = ''
    if (errorAlert.value) {
      errorAlert.value.style.display = 'none'
    }

    loading.value = true

    // 调用登录API
    await authStore.login(formData.username, formData.password)
    
    // 登录成功，跳转到仪表板
    router.push('/dashboard')
  } catch (err) {
    error.value = err.message || '登录失败，请检查用户名和密码'
    if (errorAlert.value) {
      errorAlert.value.style.display = 'block'
    }
    console.error('Login error:', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.min-vh-100 {
  min-height: 100vh;
}

.card {
  border: none;
  border-radius: 15px;
}

.form-control {
  border-radius: 8px;
  border: 1px solid #dee2e6;
  padding: 10px 15px;
}

.form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

.btn-primary {
  border-radius: 8px;
  padding: 12px;
  font-weight: 500;
}
</style>