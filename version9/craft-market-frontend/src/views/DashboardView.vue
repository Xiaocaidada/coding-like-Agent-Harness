<template>
  <div>
    <h1 class="mb-4">
      <i class="bi bi-speedometer2"></i> 仪表板
    </h1>

    <!-- 统计卡片 -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card text-white bg-primary">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <h6 class="card-title">市集活动</h6>
                <h3>{{ statistics.marketEvents }}</h3>
              </div>
              <div>
                <i class="bi bi-calendar-event" style="font-size: 2rem;"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card text-white bg-success">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <h6 class="card-title">可用摊位</h6>
                <h3>{{ statistics.availableBooths }}</h3>
              </div>
              <div>
                <i class="bi bi-grid-3x3" style="font-size: 2rem;"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card text-white bg-warning">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <h6 class="card-title">报名申请</h6>
                <h3>{{ statistics.pendingApplications }}</h3>
              </div>
              <div>
                <i class="bi bi-clipboard-check" style="font-size: 2rem;"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card text-white bg-info">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <h6 class="card-title">今日客流</h6>
                <h3>{{ statistics.todayVisitors }}</h3>
              </div>
              <div>
                <i class="bi bi-people" style="font-size: 2rem;"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">
              <i class="bi bi-clock"></i> 最近活动
            </h5>
          </div>
          <div class="card-body">
            <div class="list-group" v-if="recentActivities.length">
              <div v-for="activity in recentActivities" :key="activity.id" class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 class="mb-1">{{ activity.title }}</h6>
                    <p class="mb-1 text-muted">{{ activity.description }}</p>
                    <small class="text-muted">{{ formatDate(activity.startTime) }}</small>
                  </div>
                  <span class="badge bg-success" v-if="activity.status === 'ACTIVE'">进行中</span>
                  <span class="badge bg-secondary" v-else>已结束</span>
                </div>
              </div>
            </div>
            <div v-else class="text-muted text-center">
              暂无最近活动
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">
              <i class="bi bi-bar-chart"></i> 本月收入统计
            </h5>
          </div>
          <div class="card-body">
            <div class="text-center">
              <h3 class="text-primary">¥{{ monthlyRevenue }}</h3>
              <p class="text-muted mb-0">比上月增长 12.5%</p>
            </div>
            <div class="mt-3">
              <canvas id="revenueChart" height="150"></canvas>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 待办事项 -->
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">
              <i class="bi bi-check-circle"></i> 待办事项
            </h5>
          </div>
          <div class="card-body">
            <div class="list-group">
              <div class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 class="mb-1">审核新报名申请</h6>
                    <small class="text-muted">3个申请需要处理</small>
                  </div>
                  <button class="btn btn-sm btn-outline-primary">查看</button>
                </div>
              </div>
              <div class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 class="mb-1">安排下周摊位分配</h6>
                    <small class="text-muted">截止时间：明天18:00</small>
                  </div>
                  <button class="btn btn-sm btn-outline-primary">处理</button>
                </div>
              </div>
              <div class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 class="mb-1">更新市集活动信息</h6>
                    <small class="text-muted">2个活动需要更新详情</small>
                  </div>
                  <button class="btn btn-sm btn-outline-primary">编辑</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '../utils/http'

const statistics = ref({
  marketEvents: 12,
  availableBooths: 45,
  pendingApplications: 8,
  todayVisitors: 234
})

const recentActivities = ref([])
const monthlyRevenue = ref(45680)

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const fetchRecentActivities = async () => {
  try {
    const response = await http.get('/market-events/recent')
    recentActivities.value = response.data
  } catch (error) {
    console.error('Failed to fetch recent activities:', error)
  }
}

const initChart = () => {
  // 这里可以初始化图表库，如Chart.js
  // 暂时使用占位图
  const ctx = document.getElementById('revenueChart')
  if (ctx) {
    // 实际项目中可以使用Chart.js等图表库
    ctx.getContext('2d').fillText('收入趋势图', 50, 75)
  }
}

onMounted(() => {
  fetchRecentActivities()
  initChart()
})
</script>