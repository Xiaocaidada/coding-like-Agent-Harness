<template>
  <div class="container-fluid">
    <div class="row mb-4">
      <div class="col-md-12">
        <div class="d-flex justify-content-between align-items-center">
          <h2>摊位管理</h2>
          <button class="btn btn-primary" @click="showAddModal = true">
            <i class="bi bi-plus-circle"></i> 新增摊位
          </button>
          <button class="btn btn-success" @click="showBatchAddModal = true">
            <i class="bi bi-plus-square"></i> 批量新增
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
                <label class="form-label">摊位编号</label>
                <input type="text" class="form-control" v-model="filters.boothCode" @keyup.enter="handleSearch">
              </div>
              <div class="col-md-3">
                <label class="form-label">所在区域</label>
                <select class="form-select" v-model="filters.area">
                  <option value="">全部区域</option>
                  <option value="文创区">文创区</option>
                  <option value="美食区">美食区</option>
                  <option value="手工艺区">手工艺区</option>
                  <option value="文创区">文创区</option>
                </select>
              </div>
              <div class="col-md-3">
                <label class="form-label">摊位状态</label>
                <select class="form-select" v-model="filters.status">
                  <option value="">全部状态</option>
                  <option value="AVAILABLE">空闲</option>
                  <option value="ALLOCATED">已分配</option>
                  <option value="DISABLED">禁用</option>
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

    <!-- 统计卡片 -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card text-white bg-primary">
          <div class="card-body">
            <h5 class="card-title">总摊位数</h5>
            <h2>{{ statistics.totalBooths }}</h2>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-success">
          <div class="card-body">
            <h5 class="card-title">空闲摊位</h5>
            <h2>{{ statistics.availableBooths }}</h2>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-warning">
          <div class="card-body">
            <h5 class="card-title">已分配摊位</h5>
            <h2>{{ statistics.allocatedBooths }}</h2>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-danger">
          <div class="card-body">
            <h5 class="card-title">禁用摊位</h5>
            <h2>{{ statistics.disabledBooths }}</h2>
          </div>
        </div>
      </div>
    </div>

    <!-- 摊位列表 -->
    <div class="row">
      <div class="col-md-12">
        <div class="card">
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>摊位编号</th>
                    <th>所在区域</th>
                    <th>面积(㎡)</th>
                    <th>租金价格(元/天)</th>
                    <th>状态</th>
                    <th>分配给</th>
                    <th>创建时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="loading">
                    <td colspan="8" class="text-center">
                      <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                      </div>
                    </td>
                  </tr>
                  <tr v-else-if="booths.length === 0">
                    <td colspan="8" class="text-center text-muted">暂无数据</td>
                  </tr>
                  <tr v-else v-for="booth in booths" :key="booth.id">
                    <td>{{ booth.boothCode }}</td>
                    <td>{{ booth.area }}</td>
                    <td>{{ booth.areaSize }}</td>
                    <td>{{ booth.rentPrice }}</td>
                    <td>
                      <span class="badge" :class="getStatusBadgeClass(booth.status)">
                        {{ getStatusText(booth.status) }}
                      </span>
                    </td>
                    <td>{{ booth.vendorName || '-' }}</td>
                    <td>{{ formatDateTime(booth.createTime) }}</td>
                    <td>
                      <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-outline-primary" @click="editBooth(booth)">
                          <i class="bi bi-pencil"></i> 编辑
                        </button>
                        <button class="btn btn-sm btn-outline-info" @click="viewBooth(booth)" v-if="booth.status === 'AVAILABLE'">
                          <i class="bi bi-people"></i> 分配
                        </button>
                        <button class="btn btn-sm btn-outline-secondary" @click="viewBooth(booth)">
                          <i class="bi bi-eye"></i> 查看
                        </button>
                        <button class="btn btn-sm btn-outline-danger" @click="deleteBooth(booth.id)" v-if="booth.status === 'DISABLED'">
                          <i class="bi bi-trash"></i> 删除
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
                共 {{ total }} 条记录
              </div>
              <nav>
                <ul class="pagination">
                  <li class="page-item" :class="{ disabled: pagination.current === 1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(pagination.current - 1)">上一页</a>
                  </li>
                  <li class="page-item" v-for="page in computedPagination.pages" :key="page" 
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

    <!-- 单个添加摊位模态框 -->
    <div class="modal fade" :class="{ show: showAddModal }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">新增摊位</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveBooth">
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">摊位编号 *</label>
                    <input type="text" class="form-control" v-model="currentBooth.boothCode" required>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">所在区域 *</label>
                    <select class="form-select" v-model="currentBooth.area" required>
                      <option value="">请选择区域</option>
                      <option value="文创区">文创区</option>
                      <option value="美食区">美食区</option>
                      <option value="手工艺区">手工艺区</option>
                    </select>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">面积(㎡) *</label>
                    <input type="number" class="form-control" v-model="currentBooth.areaSize" min="1" required>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">租金价格(元/天) *</label>
                    <input type="number" class="form-control" v-model="currentBooth.rentPrice" min="0" step="0.01" required>
                  </div>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">摊位状态</label>
                <select class="form-select" v-model="currentBooth.status">
                  <option value="AVAILABLE">空闲</option>
                  <option value="ALLOCATED">已分配</option>
                  <option value="DISABLED">禁用</option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveBooth">保存</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 批量添加摊位模态框 -->
    <div class="modal fade" :class="{ show: showBatchAddModal }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">批量新增摊位</h5>
            <button type="button" class="btn-close" @click="closeBatchModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="batchAddBooths">
              <div class="mb-3">
                <label class="form-label">区域 *</label>
                <select class="form-select" v-model="batchBooths.area" required>
                  <option value="">请选择区域</option>
                  <option value="文创区">文创区</option>
                  <option value="美食区">美食区</option>
                  <option value="手工艺区">手工艺区</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">起始摊位编号 *</label>
                <input type="text" class="form-control" v-model="batchBooths.startCode" required>
              </div>
              <div class="mb-3">
                <label class="form-label">摊位数量 *</label>
                <input type="number" class="form-control" v-model="batchBooths.count" min="1" max="100" required>
              </div>
              <div class="mb-3">
                <label class="form-label">面积(㎡) *</label>
                <input type="number" class="form-control" v-model="batchBooths.areaSize" min="1" required>
              </div>
              <div class="mb-3">
                <label class="form-label">租金价格(元/天) *</label>
                <input type="number" class="form-control" v-model="batchBooths.rentPrice" min="0" step="0.01" required>
              </div>
              <div class="alert alert-info">
                <i class="bi bi-info-circle"></i>
                将自动生成摊位编号：{起始编号} 到 {起始编号 + 数量 - 1}
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeBatchModal">取消</button>
            <button type="button" class="btn btn-primary" @click="batchAddBooths">批量添加</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 摊位详情模态框 -->
    <div class="modal fade" :class="{ show: showViewModal }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">摊位详情</h5>
            <button type="button" class="btn-close" @click="closeViewModal"></button>
          </div>
          <div class="modal-body">
            <div v-if="selectedBooth">
              <div class="row mb-3">
                <div class="col-md-6">
                  <strong>摊位编号：</strong> {{ selectedBooth.boothCode }}
                </div>
                <div class="col-md-6">
                  <strong>摊位状态：</strong>
                  <span class="badge" :class="getStatusBadgeClass(selectedBooth.status)">
                    {{ getStatusText(selectedBooth.status) }}
                  </span>
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-md-6">
                  <strong>所在区域：</strong> {{ selectedBooth.area }}
                </div>
                <div class="col-md-6">
                  <strong>面积：</strong> {{ selectedBooth.areaSize }} ㎡
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-md-6">
                  <strong>租金价格：</strong> {{ selectedBooth.rentPrice }} 元/天
                </div>
                <div class="col-md-6">
                  <strong>创建时间：</strong> {{ formatDateTime(selectedBooth.createTime) }}
                </div>
              </div>
              <div class="row" v-if="selectedBooth.vendorName">
                <div class="col-md-6">
                  <strong>分配给：</strong> {{ selectedBooth.vendorName }}
                </div>
                <div class="col-md-6">
                  <strong>分配时间：</strong> {{ formatDateTime(selectedBooth.allocateTime) }}
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

    <!-- 摊位分配模态框 -->
    <div class="modal fade" :class="{ show: showAllocateModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">分配摊位</h5>
            <button type="button" class="btn-close" @click="closeAllocateModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="allocateBooth">
              <div class="mb-3">
                <label class="form-label">选择摊主 *</label>
                <select class="form-select" v-model="allocation.vendorId" required>
                  <option value="">请选择摊主</option>
                  <option v-for="vendor in vendors" :key="vendor.id" :value="vendor.id">
                    {{ vendor.realName }} ({{ vendor.phone }})
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">选择市集活动 *</label>
                <select class="form-select" v-model="allocation.marketEventId" required>
                  <option value="">请选择市集活动</option>
                  <option v-for="event in marketEvents" :key="event.id" :value="event.id">
                    {{ event.eventName }} ({{ formatDateTime(event.eventTime) }})
                  </option>
                </select>
              </div>
              <div class="alert alert-warning">
                <i class="bi bi-exclamation-triangle"></i>
                分配后将自动生成摊位租赁订单
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeAllocateModal">取消</button>
            <button type="button" class="btn btn-primary" @click="allocateBooth">分配</button>
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
const booths = ref([])
const total = ref(0)
const vendors = ref([])
const marketEvents = ref([])
const statistics = reactive({
  totalBooths: 0,
  availableBooths: 0,
  allocatedBooths: 0,
  disabledBooths: 0
})

const showAddModal = ref(false)
const showBatchAddModal = ref(false)
const showViewModal = ref(false)
const showAllocateModal = ref(false)
const selectedBooth = ref(null)

const filters = reactive({
  boothCode: '',
  area: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
  pages: []
})

const currentBooth = reactive({
  id: null,
  boothCode: '',
  area: '',
  areaSize: null,
  rentPrice: null,
  status: 'AVAILABLE'
})

const batchBooths = reactive({
  area: '',
  startCode: '',
  count: 1,
  areaSize: null,
  rentPrice: null
})

const allocation = reactive({
  boothId: null,
  vendorId: null,
  marketEventId: null
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
  loadBooths()
  loadVendors()
  loadMarketEvents()
  loadStatistics()
})

// 加载摊位列表
const loadBooths = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    
    if (filters.boothCode) params.boothCode = filters.boothCode
    if (filters.area) params.area = filters.area
    if (filters.status) params.status = filters.status
    
    const response = await http.get('/api/booths', { params })
    booths.value = response.data.records
    total.value = response.data.total
    pagination.total = Math.ceil(total.value / pagination.size)
  } catch (error) {
    console.error('加载摊位列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载摊主列表
const loadVendors = async () => {
  try {
    const response = await http.get('/api/vendors')
    vendors.value = response.data.records
  } catch (error) {
    console.error('加载摊主列表失败:', error)
  }
}

// 加载市集活动列表
const loadMarketEvents = async () => {
  try {
    const response = await http.get('/api/market-events')
    marketEvents.value = response.data.records.filter(event => event.status === 'NOT_STARTED')
  } catch (error) {
    console.error('加载市集活动列表失败:', error)
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    const response = await http.get('/api/booths/statistics')
    Object.assign(statistics, response.data)
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadBooths()
}

// 重置筛选
const resetFilters = () => {
  filters.boothCode = ''
  filters.area = ''
  filters.status = ''
  handleSearch()
}

// 改变页码
const changePage = (page) => {
  if (page >= 1 && page <= pagination.total) {
    pagination.current = page
    loadBooths()
  }
}

// 新增摊位
const addBooth = () => {
  currentBooth.id = null
  currentBooth.boothCode = ''
  currentBooth.area = ''
  currentBooth.areaSize = null
  currentBooth.rentPrice = null
  currentBooth.status = 'AVAILABLE'
  showAddModal.value = true
}

// 编辑摊位
const editBooth = (booth) => {
  Object.assign(currentBooth, booth)
  showAddModal.value = true
}

// 查看摊位
const viewBooth = (booth) => {
  selectedBooth.value = booth
  if (booth.status === 'AVAILABLE') {
    allocation.boothId = booth.id
    showAllocateModal.value = true
  } else {
    showViewModal.value = true
  }
}

// 关闭模态框
const closeModal = () => {
  showAddModal.value = false
}

const closeBatchModal = () => {
  showBatchAddModal.value = false
}

const closeViewModal = () => {
  showViewModal.value = false
  selectedBooth.value = null
}

const closeAllocateModal = () => {
  showAllocateModal.value = false
  allocation.boothId = null
  allocation.vendorId = null
  allocation.marketEventId = null
}

// 保存摊位
const saveBooth = async () => {
  try {
    if (currentBooth.id) {
      await http.put(`/api/booths/${currentBooth.id}`, currentBooth)
    } else {
      await http.post('/api/booths', currentBooth)
    }
    closeModal()
    loadBooths()
    loadStatistics()
  } catch (error) {
    console.error('保存摊位失败:', error)
  }
}

// 删除摊位
const deleteBooth = async (id) => {
  if (confirm('确定要删除该摊位吗？')) {
    try {
      await http.delete(`/api/booths/${id}`)
      loadBooths()
      loadStatistics()
    } catch (error) {
      console.error('删除摊位失败:', error)
    }
  }
}

// 批量添加摊位
const batchAddBooths = async () => {
  try {
    const booths = []
    for (let i = 0; i < batchBooths.count; i++) {
      const boothCode = batchBooths.startCode.padStart(3, '0') + (i + 1)
      booths.push({
        boothCode: boothCode,
        area: batchBooths.area,
        areaSize: batchBooths.areaSize,
        rentPrice: batchBooths.rentPrice,
        status: 'AVAILABLE'
      })
    }
    
    await http.post('/api/booths/batch', { booths })
    closeBatchModal()
    loadBooths()
    loadStatistics()
  } catch (error) {
    console.error('批量添加摊位失败:', error)
  }
}

// 分配摊位
const allocateBooth = async () => {
  try {
    await http.post('/api/booths/allocate', allocation)
    closeAllocateModal()
    loadBooths()
    loadStatistics()
  } catch (error) {
    console.error('分配摊位失败:', error)
  }
}

// 获取状态徽章样式
const getStatusBadgeClass = (status) => {
  const classes = {
    'AVAILABLE': 'bg-success',
    'ALLOCATED': 'bg-warning',
    'DISABLED': 'bg-danger'
  }
  return classes[status] || 'bg-secondary'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'AVAILABLE': '空闲',
    'ALLOCATED': '已分配',
    'DISABLED': '禁用'
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
  addBooth,
  editBooth,
  viewBooth
})
</script>

<style scoped>
.badge {
  font-size: 0.875rem;
}
</style>