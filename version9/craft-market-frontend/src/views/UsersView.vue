<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>
        <i class="bi bi-people"></i> 用户管理
      </h1>
      <button class="btn btn-primary" @click="showAddModal = true">
        <i class="bi bi-plus-circle"></i> 添加用户
      </button>
    </div>

    <!-- 搜索和过滤 -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row">
          <div class="col-md-3">
            <input 
              type="text" 
              class="form-control" 
              v-model="searchParams.username"
              placeholder="搜索用户名"
              @input="handleSearch"
            >
          </div>
          <div class="col-md-3">
            <select class="form-select" v-model="searchParams.role">
              <option value="">全部角色</option>
              <option value="ADMIN">管理员</option>
              <option value="VENDOR">摊主</option>
            </select>
          </div>
          <div class="col-md-3">
            <select class="form-select" v-model="searchParams.status">
              <option value="">全部状态</option>
              <option value="ACTIVE">正常</option>
              <option value="INACTIVE">停用</option>
            </select>
          </div>
          <div class="col-md-3">
            <button class="btn btn-outline-secondary" @click="resetSearch">
              <i class="bi bi-x-circle"></i> 重置
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-striped">
            <thead>
              <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>角色</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>
                  <span class="badge" :class="user.role === 'ADMIN' ? 'bg-danger' : 'bg-success'">
                    {{ user.role === 'ADMIN' ? '管理员' : '摊主' }}
                  </span>
                </td>
                <td>
                  <span class="badge" :class="user.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
                    {{ user.status === 'ACTIVE' ? '正常' : '停用' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createTime) }}</td>
                <td>
                  <div class="btn-group" role="group">
                    <button class="btn btn-sm btn-outline-primary" @click="editUser(user)">
                      <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-warning" @click="resetPassword(user.id)">
                      <i class="bi bi-key"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" @click="deleteUser(user.id)">
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 分页 -->
        <div class="d-flex justify-content-between align-items-center mt-3">
          <div>
            <span class="text-muted">共 {{ total }} 条记录</span>
          </div>
          <nav>
            <ul class="pagination">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <a class="page-link" href="#" @click="changePage(currentPage - 1)">上一页</a>
              </li>
              <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
                <a class="page-link" href="#" @click="changePage(page)">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <a class="page-link" href="#" @click="changePage(currentPage + 1)">下一页</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>

    <!-- 添加/编辑用户模态框 -->
    <div class="modal fade" :class="{ show: showAddModal || showEditModal }" :style="{ display: showAddModal || showEditModal ? 'block' : 'none' }">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? '编辑用户' : '添加用户' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveUser">
              <div class="mb-3">
                <label class="form-label">用户名</label>
                <input type="text" class="form-control" v-model="userForm.username" required>
              </div>
              <div class="mb-3">
                <label class="form-label">密码</label>
                <input type="password" class="form-control" v-model="userForm.password" :required="!isEditing">
              </div>
              <div class="mb-3">
                <label class="form-label">角色</label>
                <select class="form-select" v-model="userForm.role" required>
                  <option value="">请选择角色</option>
                  <option value="ADMIN">管理员</option>
                  <option value="VENDOR">摊主</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">状态</label>
                <select class="form-select" v-model="userForm.status" required>
                  <option value="">请选择状态</option>
                  <option value="ACTIVE">正常</option>
                  <option value="INACTIVE">停用</option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveUser" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 重置密码模态框 -->
    <div class="modal fade" :class="{ show: showResetModal }" :style="{ display: showResetModal ? 'block' : 'none' }">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">重置密码</h5>
            <button type="button" class="btn-close" @click="closeResetModal"></button>
          </div>
          <div class="modal-body">
            <p>确定要重置该用户的密码吗？新密码将通过邮件发送给用户。</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeResetModal">取消</button>
            <button type="button" class="btn btn-primary" @click="confirmResetPassword" :disabled="resetting">
              <span v-if="resetting" class="spinner-border spinner-border-sm me-2"></span>
              {{ resetting ? '重置中...' : '确认重置' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import userApi from '../api/users'

const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const totalPages = ref(1)

const showAddModal = ref(false)
const showEditModal = ref(false)
const showResetModal = ref(false)
const saving = ref(false)
const resetting = ref(false)
const isEditing = ref(false)
const resetUserId = ref(null)

const searchParams = reactive({
  username: '',
  role: '',
  status: ''
})

const userForm = reactive({
  id: null,
  username: '',
  password: '',
  role: 'VENDOR',
  status: 'ACTIVE'
})

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const fetchUsers = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: 10,
      ...searchParams
    }
    const response = await userApi.getUsers(params)
    users.value = response.data.content
    total.value = response.data.total
    totalPages.value = Math.ceil(total.value / 10)
  } catch (error) {
    console.error('Failed to fetch users:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const resetSearch = () => {
  searchParams.username = ''
  searchParams.role = ''
  searchParams.status = ''
  handleSearch()
}

const changePage = (page) => {
  currentPage.value = page
  fetchUsers()
}

const editUser = (user) => {
  isEditing.value = true
  userForm.id = user.id
  userForm.username = user.username
  userForm.role = user.role
  userForm.status = user.status
  showEditModal.value = true
}

const addUser = () => {
  isEditing.value = false
  userForm.id = null
  userForm.username = ''
  userForm.password = ''
  userForm.role = 'VENDOR'
  userForm.status = 'ACTIVE'
  showAddModal.value = true
}

const saveUser = async () => {
  try {
    saving.value = true
    
    if (isEditing.value) {
      await userApi.updateUser(userForm.id, userForm)
    } else {
      await userApi.createUser(userForm)
    }
    
    closeModal()
    fetchUsers()
  } catch (error) {
    console.error('Failed to save user:', error)
  } finally {
    saving.value = false
  }
}

const deleteUser = (id) => {
  if (confirm('确定要删除该用户吗？')) {
    userApi.deleteUser(id)
      .then(() => {
        fetchUsers()
      })
      .catch(error => {
        console.error('Failed to delete user:', error)
      })
  }
}

const resetPassword = (id) => {
  resetUserId.value = id
  showResetModal.value = true
}

const confirmResetPassword = async () => {
  try {
    resetting.value = true
    await userApi.resetPassword(resetUserId.value)
    closeResetModal()
    alert('密码重置成功！')
  } catch (error) {
    console.error('Failed to reset password:', error)
  } finally {
    resetting.value = false
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
}

const closeResetModal = () => {
  showResetModal.value = false
  resetUserId.value = null
}

onMounted(() => {
  fetchUsers()
})
</script>