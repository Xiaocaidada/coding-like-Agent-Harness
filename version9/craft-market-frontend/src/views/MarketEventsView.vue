<template>
  <div class="container-fluid">
    <div class="row mb-4">
      <div class="col-md-12">
        <div class="d-flex justify-content-between align-items-center">
          <h2>市场活动管理</h2>
          <button class="btn btn-primary" @click="showAddModal = true">
            <i class="bi bi-plus-circle"></i> 新建活动
          </button>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="row mb-4">
      <div class="col-md-12">
        <div class="card">
          <div class="card-body">
            <div class="row">
              <div class="col-md-3">
                <label class="form-label">活动名称</label>
                <input type="text" class="form-control" v-model="filters.eventName" @keyup.enter="handleSearch">
              </div>
              <div class="col-md-3">
                <label class="form-label">活动状态</label>
                <select class="form-select" v-model="filters.status">
                  <option value="">全部状态</option>
                  <option value="NOT_STARTED">未开始</option>
                  <option value="IN_PROGRESS">进行中</option>
                  <option value="ENDED">已结束</option>
                </select>
              </div>
              <div class="col-md-3">
                <label class="form-label">&nbsp;</label>
                <div class="d-flex gap-2">
                  <button class="btn btn-primary" @click="handleSearch">搜索</button>
                  <button class="btn btn-outline-secondary" @click="resetFilters">重置</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动列表 -->
    <div class="row">
      <div class="col-md-12">
        <div class="card">
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>活动名称</th>
                    <th>活动时间</th>
                    <th>活动地点</th>
                    <th>状态</th>
                    <th>创建时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="loading">
                    <td colspan="6" class="text-center">
                      <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                      </div>
                    </td>
                  </tr>
                  <tr v-else-if="marketEvents.length === 0">
                    <td colspan="6" class="text-center text-muted">暂无数据</td>
                  </tr>
                  <tr v-else v-for="event in marketEvents" :key="event.id">
                    <td>{{ event.eventName }}</td>
                    <td>{{ formatDateTime(event.eventTime) }}</td>
                    <td>{{ event.location }}</td>
                    <td>
                      <span class="badge" :class="getStatusBadgeClass(event.status)">
                        {{ getStatusText(event.status) }}
                      </span>
                    </td>
                    <td>{{ formatDateTime(event.createTime) }}</td>
                    <td>
                      <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-outline-primary" @click="editEvent(event)">
                          <i class="bi bi-pencil"></i> 编辑
                        </button>
                        <button class="btn btn-sm btn-outline-secondary" @click="viewEvent(event)">
                          <i class="bi bi-eye"></i> 查看
                        </button>
                        <div class="btn-group" role="group">
                          <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">
                            操作
                          </button>
                          <ul class="dropdown-menu">
                            <li v-if="event.status === 'NOT_STARTED'">
                              <a class="dropdown-item" href="#" @click="publishEvent(event.id)">发布活动</a>
                            </li>
                            <li v-if="event.status === 'IN_PROGRESS'">
                              <a class="dropdown-item" href="#" @click="cancelEvent(event.id)">取消活动</a>
                            </li>
                          </ul>
                        </div>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- 分页 -->
            <div class="d-flex justify-content-between align-items-center mt-3">
              <div>
                共 {{ total }} 条记录
              </div>
              <nav>
                <ul class="pagination">
                  <li class="page-item" :class="{ disabled: pagination.current === 1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(pagination.current - 1)">上一页</a>
                  </li>
                  <li class="page-item" v-for="page in pagination.pages" :key="page" 
                      :class="{ active: page === pagination.current }">
                    <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
                  </li>
                  <li class="page-item" :class="{ disabled: pagination.current === pagination.total }">
                    <a class="page-link" href="#" @click.prevent="changePage(pagination.current + 1)">下一页</a>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑活动模态框 -->
    <div class="modal fade" :class="{ show: showAddModal }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? '编辑活动' : '新建活动' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveEvent">
              <div class="mb-3">
                <label class="form-label">活动名称 *</label>
                <input type="text" class="form-control" v-model="currentEvent.eventName" required>
              </div>
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">活动开始时间 *</label>
                    <input type="datetime-local" class="form-control" v-model="currentEvent.eventTime" required>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">活动地点 *</label>
                    <input type="text" class="form-control" v-model="currentEvent.location" required>
                  </div>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">活动简介 *</label>
                <textarea class="form-control" v-model="currentEvent.description" rows="4" required></textarea>
              </div>
              <div class="mb-3">
                <label class="form-label">海报图片</label>
                <input type="file" class="form-control" @change="handlePosterUpload">
                <div class="mt-2" v-if="currentEvent.posterUrl">
                  <img :src="currentEvent.posterUrl" alt="海报预览" class="img-thumbnail" style="max-width: 200px;">
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">活动状态</label>
                <select class="form-select" v-model="currentEvent.status">
                  <option value="NOT_STARTED">未开始</option>
                  <option value="IN_PROGRESS">进行中</option>
                  <option value="ENDED">已结束</option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveEvent">保存</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动详情模态框 -->
    <div class="modal fade" :class="{ show: showViewModal }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">活动详情</h5>
            <button type="button" class="btn-close" @click="closeViewModal"></button>
          </div>
          <div class="modal-body">
            <div v-if="selectedEvent">
              <div class="row mb-3">
                <div class="col-md-6">
                  <strong>活动名称：</strong> {{ selectedEvent.eventName }}
                </div>
                <div class="col-md-6">
                  <strong>活动状态：</strong>
                  <span class="badge" :class="getStatusBadgeClass(selectedEvent.status)">
                    {{ getStatusText(selectedEvent.status) }}
                  </span>
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-md-6">
                  <strong>活动时间：</strong> {{ formatDateTime(selectedEvent.eventTime) }}
                </div>
                <div class="col-md-6">
                  <strong>活动地点：</strong> {{ selectedEvent.location }}
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-12">
                  <strong>活动简介：</strong>
                  <p>{{ selectedEvent.description }}</p>
                </div>
              </div>
              <div class="row" v-if="selectedEvent.posterUrl">
                <div class="col-12">
                  <strong>海报图片：</strong>
                  <img :src="selectedEvent.posterUrl" alt="活动海报" class="img-thumbnail mt-2" style="max-width: 300px;">
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeViewModal">关闭</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import http from '../utils/http'

const router = useRouter()
const loading = ref(false)
const marketEvents = ref([])
const total = ref(0)
const isEditing = ref(false)
const showAddModal = ref(false)
const showViewModal = ref(false)
const selectedEvent = ref(null)

const filters = reactive({
  eventName: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
  pages: []
})

const currentEvent = reactive({
  id: null,
  eventName: '',
  eventTime: '',
  location: '',
  description: '',
  posterUrl: '',
  status: 'NOT_STARTED'
})

// 计算分页页码
const computedPagination = computed(() => {
  const pages = []
  const maxVisiblePages = 5
  let startPage = Math.max(1, pagination.current - Math.floor(maxVisiblePages / 2))
  let endPage = Math.min(pagination.total, startPage + maxVisiblePages - 1)
  
  if (endPage - startPage < maxVisiblePages - 1) {
    startPage = Math.max(1, endPage - maxVisiblePages + 1)
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i)
  }
  
  return pages
})

// 初始化
onMounted(() => {
  loadMarketEvents()
})

// 加载活动列表
const loadMarketEvents = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    
    if (filters.eventName) params.eventName = filters.eventName
    if (filters.status) params.status = filters.status
    
    const response = await http.get('/api/market-events', { params })
    marketEvents.value = response.data.records
    total.value = response.data.total
    pagination.total = Math.ceil(total.value / pagination.size)
  } catch (error) {
    console.error('加载活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadMarketEvents()
}

// 重置筛选
const resetFilters = () => {
  filters.eventName = ''
  filters.status = ''
  handleSearch()
}

// 改变页码
const changePage = (page) => {
  if (page >= 1 && page <= pagination.total) {
    pagination.current = page
    loadMarketEvents()
  }
}

// 新建活动
const addEvent = () => {
  isEditing.value = false
  currentEvent.id = null
  currentEvent.eventName = ''
  currentEvent.eventTime = ''
  currentEvent.location = ''
  currentEvent.description = ''
  currentEvent.posterUrl = ''
  currentEvent.status = 'NOT_STARTED'
  showAddModal.value = true
}

// 编辑活动
const editEvent = (event) => {
  isEditing.value = true
  Object.assign(currentEvent, event)
  showAddModal.value = true
}

// 查看活动
const viewEvent = (event) => {
  selectedEvent.value = event
  showViewModal.value = true
}

// 关闭模态框
const closeModal = () => {
  showAddModal.value = false
  isEditing.value = false
}

const closeViewModal = () => {
  showViewModal.value = false
  selectedEvent.value = null
}

// 保存活动
const saveEvent = async () => {
  try {
    if (isEditing.value) {
      await http.put(`/api/market-events/${currentEvent.id}`, currentEvent)
    } else {
      await http.post('/api/market-events', currentEvent)
    }
    closeModal()
    loadMarketEvents()
  } catch (error) {
    console.error('保存活动失败:', error)
  }
}

// 发布活动
const publishEvent = async (eventId) => {
  try {
    await http.put(`/api/market-events/${eventId}/publish`)
    loadMarketEvents()
  } catch (error) {
    console.error('发布活动失败:', error)
  }
}

// 取消活动
const cancelEvent = async (eventId) => {
  try {
    await http.put(`/api/market-events/${eventId}/cancel`)
    loadMarketEvents()
  } catch (error) {
    console.error('取消活动失败:', error)
  }
}

// 处理海报上传
const handlePosterUpload = async (event) => {
  const file = event.target.files[0]
  if (file) {
    // 这里应该实现文件上传逻辑
    console.log('上传文件:', file)
    // currentEvent.posterUrl = URL.createObjectURL(file) // 临时显示
  }
}

// 获取状态徽章样式
const getStatusBadgeClass = (status) => {
  const classes = {
    'NOT_STARTED': 'bg-secondary',
    'IN_PROGRESS': 'bg-success',
    'ENDED': 'bg-danger'
  }
  return classes[status] || 'bg-secondary'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '进行中',
    'ENDED': '已结束'
  }
  return texts[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 暴露方法给模板
defineExpose({
  addEvent,
  editEvent,
  viewEvent
})
</script>

<style scoped>
.badge {
  font-size: 0.875rem;
}
</style>