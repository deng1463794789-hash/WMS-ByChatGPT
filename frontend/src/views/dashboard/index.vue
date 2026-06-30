<template>
  <div class="dashboard">
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(stat, idx) in statsCards" :key="stat.label">
        <el-card shadow="hover" class="stat-card" :style="{ animationDelay: idx * 0.08 + 's' }">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.bgColor }">
              <el-icon :size="28" :color="stat.iconColor"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-value">
                <span class="count-number">{{ animatedValues[stat.key] }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>入库/出库趋势</span>
              <div class="chart-legend">
                <span class="legend-dot inbound"></span>入库
                <span class="legend-dot outbound"></span>出库
              </div>
            </div>
          </template>
          <div class="chart-container">
            <div class="fake-chart" ref="chartRef">
              <div v-for="(item, idx) in dashboardData.trendData" :key="idx" class="chart-bar-group">
                <div class="bar inbound" :style="{ height: animatedBars[idx]?.inbound + '%' || '0%' }">
                  <span class="bar-tip">{{ item.inbound }}</span>
                </div>
                <div class="bar outbound" :style="{ height: animatedBars[idx]?.outbound + '%' || '0%' }">
                  <span class="bar-tip">{{ item.outbound }}</span>
                </div>
                <span class="bar-label">{{ item.date.slice(5) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" class="warn-card">
          <template #header>
            <div class="card-header">
              <span>库存预警</span>
              <el-tag type="danger" size="small">{{ dashboardData.lowStockProducts.length }}条</el-tag>
            </div>
          </template>
          <el-table :data="dashboardData.lowStockProducts" size="small" max-height="300" :show-header="true">
            <el-table-column prop="name" label="商品名称" show-overflow-tooltip />
            <el-table-column prop="sku" label="商品编码" width="100" />
            <el-table-column prop="stockQuantity" label="当前库存" width="80" align="center">
              <template #default="{ row }">
                <el-tag type="danger" size="small">{{ row.stockQuantity }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="safeStock" label="安全库存" width="80" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="shortcut-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="shortcut-list">
            <div class="shortcut-item" @click="$router.push('/product/list')">
              <div class="shortcut-icon"><el-icon :size="22"><CirclePlus /></el-icon></div>
              <span>新增商品</span>
            </div>
            <div class="shortcut-item" @click="$router.push('/inventory/inbound')">
              <div class="shortcut-icon"><el-icon :size="22"><Download /></el-icon></div>
              <span>入库操作</span>
            </div>
            <div class="shortcut-item" @click="$router.push('/inventory/outbound')">
              <div class="shortcut-icon"><el-icon :size="22"><Upload /></el-icon></div>
              <span>出库操作</span>
            </div>
            <div class="shortcut-item" @click="$router.push('/inventory/stock')">
              <div class="shortcut-icon"><el-icon :size="22"><Search /></el-icon></div>
              <span>库存查询</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { getDashboardData } from '@/api/product'

interface TrendItem {
  date: string
  inbound: number
  outbound: number
}

const chartRef = ref<HTMLElement>()

const dashboardData = ref({
  productCount: 0,
  totalStock: 0,
  todayInbound: 0,
  todayOutbound: 0,
  lowStockProducts: [] as Array<{ name: string; sku: string; stockQuantity: number; safeStock: number }>,
  trendData: [] as TrendItem[]
})

const animatedValues = reactive({
  productCount: 0,
  totalStock: 0,
  todayInbound: 0,
  todayOutbound: 0
})

const animatedBars = ref<Array<{ inbound: number; outbound: number }>>([])

const statsCards = [
  { key: 'productCount', label: '商品总数', icon: 'GoodsFilled', iconColor: '#409EFF', bgColor: '#e6f7ff' },
  { key: 'totalStock', label: '库存总量', icon: 'Box', iconColor: '#67C23A', bgColor: '#f0f9eb' },
  { key: 'todayInbound', label: '今日入库', icon: 'Download', iconColor: '#E6A23C', bgColor: '#fdf6ec' },
  { key: 'todayOutbound', label: '今日出库', icon: 'Upload', iconColor: '#F56C6C', bgColor: '#fef0f0' }
] as const

const maxTrend = computed(() => {
  let max = 1
  dashboardData.value.trendData.forEach((item: TrendItem) => {
    if (item.inbound > max) max = item.inbound
    if (item.outbound > max) max = item.outbound
  })
  return max
})

const animateNumber = (key: keyof typeof animatedValues, target: number, duration = 800) => {
  const start = animatedValues[key]
  const startTime = performance.now()
  const step = (currentTime: number) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    animatedValues[key] = Math.round(start + (target - start) * eased)
    if (progress < 1) {
      requestAnimationFrame(step)
    }
  }
  requestAnimationFrame(step)
}

const animateBars = () => {
  const trendData = dashboardData.value.trendData
  animatedBars.value = trendData.map(() => ({ inbound: 0, outbound: 0 }))
  nextTick(() => {
    requestAnimationFrame(() => {
      animatedBars.value = trendData.map((item) => ({
        inbound: (item.inbound / maxTrend.value) * 100,
        outbound: (item.outbound / maxTrend.value) * 100
      }))
    })
  })
}

onMounted(async () => {
  try {
    const res = await getDashboardData()
    if (res.data) {
      dashboardData.value = res.data
    }
  } catch (e) {
    dashboardData.value = {
      productCount: 128,
      totalStock: 5680,
      todayInbound: 23,
      todayOutbound: 41,
      lowStockProducts: [
        { name: '螺丝刀套装', sku: 'SCREW-001', stockQuantity: 5, safeStock: 20 },
        { name: '电工胶带', sku: 'TAPE-003', stockQuantity: 8, safeStock: 30 },
        { name: '防护手套', sku: 'GLOVE-002', stockQuantity: 12, safeStock: 50 }
      ],
      trendData: [
        { date: '2026-05-21', inbound: 12, outbound: 8 },
        { date: '2026-05-22', inbound: 18, outbound: 15 },
        { date: '2026-05-23', inbound: 8, outbound: 22 },
        { date: '2026-05-24', inbound: 25, outbound: 10 },
        { date: '2026-05-25', inbound: 15, outbound: 30 },
        { date: '2026-05-26', inbound: 30, outbound: 18 },
        { date: '2026-05-27', inbound: 22, outbound: 25 }
      ]
    }
  }
  nextTick(() => {
    animateNumber('productCount', dashboardData.value.productCount, 600)
    animateNumber('totalStock', dashboardData.value.totalStock, 800)
    animateNumber('todayInbound', dashboardData.value.todayInbound, 400)
    animateNumber('todayOutbound', dashboardData.value.todayOutbound, 400)
    setTimeout(animateBars, 300)
  })
})
</script>

<style scoped lang="scss">
.dashboard {
  .stats-row {
    margin-bottom: 16px;
  }

  .stat-card {
    cursor: pointer;
    transition: all 0.3s ease;
    animation: cardSlideUp 0.5s ease-out both;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1) !important;
    }

    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: transform 0.3s ease;
      }

      .stat-info {
        .stat-label {
          font-size: 13px;
          color: #909399;
          margin-bottom: 4px;
        }

        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
          font-variant-numeric: tabular-nums;
        }
      }
    }

    &:hover .stat-icon {
      transform: scale(1.1);
    }
  }

  .content-row {
    margin-bottom: 16px;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: 500;
  }

  .chart-legend {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 12px;
    font-weight: 400;
    color: #909399;

    .legend-dot {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 2px;
      margin-right: 4px;

      &.inbound { background: #409EFF; }
      &.outbound { background: #67C23A; }
    }
  }

  .chart-container {
    padding: 10px 0;
  }

  .fake-chart {
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    height: 200px;
    padding: 20px 10px 0;
    border-bottom: 2px solid #e8e8e8;
    margin: 0 10px;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: 10px;
      top: 20px;
      bottom: 0;
      width: 2px;
      background: #e8e8e8;
    }

    .chart-bar-group {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
      position: relative;
      z-index: 1;

      .bar {
        width: 22px;
        border-radius: 4px 4px 0 0;
        transition: height 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
        position: relative;

        &.inbound { background: linear-gradient(180deg, #66b1ff, #409EFF); }
        &.outbound { background: linear-gradient(180deg, #85ce61, #67C23A); }

        .bar-tip {
          position: absolute;
          top: -20px;
          left: 50%;
          transform: translateX(-50%);
          font-size: 10px;
          color: #909399;
          white-space: nowrap;
          opacity: 0;
          transition: opacity 0.3s ease 0.3s;
        }
      }

      &:hover .bar .bar-tip {
        opacity: 1;
      }

      .bar-label {
        font-size: 10px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }

  .shortcut-list {
    display: flex;
    gap: 16px;

    .shortcut-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      padding: 16px 28px;
      border-radius: 10px;
      transition: all 0.3s ease;
      color: #606266;
      flex: 1;

      .shortcut-icon {
        width: 44px;
        height: 44px;
        border-radius: 12px;
        background: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease;
      }

      &:hover {
        background: #ecf5ff;
        color: #409EFF;

        .shortcut-icon {
          background: #409EFF;
          color: #fff;
        }
      }

      span {
        font-size: 13px;
        font-weight: 500;
      }
    }
  }

  :deep(.el-card__body) {
    padding-top: 0;
  }

  :deep(.el-table) {
    --el-table-border-color: #ebeef5;
  }
}

@keyframes cardSlideUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
