<template>
  <div class="dashboard app-container">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-kicker">WMS Command Center</span>
        <h1>仓储运营总览</h1>
        <p>实时掌握商品、库存、入库与出库状态，快速定位低库存风险。</p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="$router.push('/inventory/inbound')">
          <el-icon><Download /></el-icon>新建入库
        </el-button>
        <el-button size="large" @click="$router.push('/product/list')">
          <el-icon><GoodsFilled /></el-icon>商品管理
        </el-button>
      </div>
      <div class="hero-orbit orbit-a" />
      <div class="hero-orbit orbit-b" />
    </section>

    <el-row :gutter="18" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(stat, idx) in statsCards" :key="stat.label">
        <el-card shadow="never" class="stat-card" :style="{ animationDelay: idx * 0.07 + 's' }">
          <div class="stat-topline">
            <div class="stat-icon" :class="stat.tone">
              <el-icon :size="24"><component :is="stat.icon" /></el-icon>
            </div>
            <span>{{ stat.badge }}</span>
          </div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-value">
            <span class="count-number">{{ animatedValues[stat.key] }}</span>
          </div>
          <div class="stat-trend">
            <span :class="stat.trendType">{{ stat.trend }}</span>
            <small>{{ stat.hint }}</small>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18" class="content-row">
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <div>
                <strong>近 7 日吞吐趋势</strong>
                <small>入库与出库数量对比</small>
              </div>
              <div class="chart-legend">
                <span><i class="legend-dot inbound" />入库</span>
                <span><i class="legend-dot outbound" />出库</span>
              </div>
            </div>
          </template>
          <div class="chart-container">
            <div class="fake-chart">
              <div v-for="(item, idx) in dashboardData.trendData" :key="idx" class="chart-bar-group">
                <div class="bar-pair">
                  <div class="bar inbound" :style="{ height: animatedBars[idx]?.inbound + '%' || '0%' }">
                    <span class="bar-tip">{{ item.inbound }}</span>
                  </div>
                  <div class="bar outbound" :style="{ height: animatedBars[idx]?.outbound + '%' || '0%' }">
                    <span class="bar-tip">{{ item.outbound }}</span>
                  </div>
                </div>
                <span class="bar-label">{{ item.date.slice(5) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="warn-card">
          <template #header>
            <div class="card-header">
              <div>
                <strong>库存预警</strong>
                <small>低于安全库存的商品</small>
              </div>
              <el-tag type="danger" size="small">{{ dashboardData.lowStockProducts.length }} 条</el-tag>
            </div>
          </template>
          <div v-if="dashboardData.lowStockProducts.length" class="warning-list">
            <div class="warning-item" v-for="item in dashboardData.lowStockProducts.slice(0, 5)" :key="item.sku">
              <div class="warning-main">
                <span>{{ item.name }}</span>
                <small>{{ item.sku }}</small>
              </div>
              <div class="warning-stock">
                <strong>{{ item.stockQuantity }}</strong>
                <span>/ {{ item.safeStock }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无库存预警" :image-size="88" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18" class="shortcut-row">
      <el-col :span="24">
        <el-card shadow="never" class="shortcut-card">
          <template #header>
            <div class="card-header">
              <div>
                <strong>高频操作</strong>
                <small>减少模块跳转成本</small>
              </div>
            </div>
          </template>
          <div class="shortcut-list">
            <div class="shortcut-item" v-for="shortcut in shortcuts" :key="shortcut.title" @click="$router.push(shortcut.path)">
              <div class="shortcut-icon" :class="shortcut.tone">
                <el-icon :size="22"><component :is="shortcut.icon" /></el-icon>
              </div>
              <div>
                <span>{{ shortcut.title }}</span>
                <small>{{ shortcut.desc }}</small>
              </div>
              <el-icon class="shortcut-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">

defineOptions({ name: 'Dashboard' })
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { getDashboardData } from '@/api/product'

interface TrendItem {
  date: string
  inbound: number
  outbound: number
}

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
  { key: 'productCount', label: '商品总数', icon: 'GoodsFilled', tone: 'blue', badge: 'SKU', trend: '+12%', trendType: 'up', hint: '较上周' },
  { key: 'totalStock', label: '库存总量', icon: 'Box', tone: 'green', badge: 'Stock', trend: '稳定', trendType: 'flat', hint: '库存水位' },
  { key: 'todayInbound', label: '今日入库', icon: 'Download', tone: 'amber', badge: 'Inbound', trend: '+8%', trendType: 'up', hint: '日内变化' },
  { key: 'todayOutbound', label: '今日出库', icon: 'Upload', tone: 'rose', badge: 'Outbound', trend: '-3%', trendType: 'down', hint: '日内变化' }
] as const

const shortcuts = [
  { title: '新增商品', desc: '录入 SKU 与安全库存', icon: 'CirclePlus', path: '/product/list', tone: 'blue' },
  { title: '入库操作', desc: '采购、退货、调拨入库', icon: 'Download', path: '/inventory/inbound', tone: 'green' },
  { title: '出库操作', desc: '销售、退供、调拨出库', icon: 'Upload', path: '/inventory/outbound', tone: 'amber' },
  { title: '库存查询', desc: '查看实时库存和预警', icon: 'Search', path: '/inventory/stock', tone: 'rose' }
]

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
    animateNumber('productCount', dashboardData.value.productCount, 650)
    animateNumber('totalStock', dashboardData.value.totalStock, 850)
    animateNumber('todayInbound', dashboardData.value.todayInbound, 520)
    animateNumber('todayOutbound', dashboardData.value.todayOutbound, 520)
    setTimeout(animateBars, 260)
  })
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.dashboard {
  .hero-panel {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
    min-height: 170px;
    margin-bottom: 18px;
    padding: 30px;
    overflow: hidden;
    border-radius: 30px;
    color: #fff;
    background:
      linear-gradient(135deg, rgba(15, 27, 45, 0.96), rgba(30, 64, 175, 0.92)),
      radial-gradient(circle at 78% 26%, rgba(20, 184, 166, 0.45), transparent 30%);
    box-shadow: 0 26px 70px rgba(15, 23, 42, 0.2);
  }

  .hero-copy {
    position: relative;
    z-index: 1;

    .hero-kicker {
      display: inline-flex;
      margin-bottom: 13px;
      padding: 7px 11px;
      color: rgba(226, 232, 240, 0.86);
      font-size: 12px;
      font-weight: 800;
      letter-spacing: 0.12em;
      text-transform: uppercase;
      border-radius: 999px;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.12);
    }

    h1 {
      margin: 0;
      font-size: clamp(28px, 4vw, 42px);
      letter-spacing: -0.04em;
    }

    p {
      max-width: 520px;
      margin: 12px 0 0;
      color: rgba(226, 232, 240, 0.72);
      line-height: 1.8;
    }
  }

  .hero-actions {
    position: relative;
    z-index: 1;
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .hero-orbit {
    position: absolute;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.12);
  }

  .orbit-a {
    width: 220px;
    height: 220px;
    right: -44px;
    top: -70px;

  }

  .orbit-b {
    width: 130px;
    height: 130px;
    right: 185px;
    bottom: -54px;

  }

  .stats-row,
  .content-row {
    margin-bottom: 18px;
  }

  .stat-card {
    min-height: 166px;
    animation: cardRise 0.48s $ease-out both;

    .stat-topline {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 18px;

      > span {
        color: $text-secondary;
        font-size: 12px;
        font-weight: 800;
      }
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      display: grid;
      place-items: center;
      color: #fff;
      border-radius: 17px;
      box-shadow: 0 14px 28px rgba(15, 23, 42, 0.1);

      &.blue { background: linear-gradient(135deg, #1d4ed8, #2563eb); }
      &.green { background: linear-gradient(135deg, #059669, #34d399); }
      &.amber { background: linear-gradient(135deg, #d97706, #fbbf24); }
      &.rose { background: linear-gradient(135deg, #e11d48, #fb7185); }
    }

    .stat-label {
      color: $text-secondary;
      font-size: 13px;
      font-weight: 700;
    }

    .stat-value {
      margin-top: 8px;
      color: $text-primary;
      font-size: 34px;
      font-weight: 900;
      letter-spacing: -0.04em;
      font-variant-numeric: tabular-nums;
    }

    .stat-trend {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 12px;
      font-size: 12px;

      span {
        padding: 4px 8px;
        border-radius: 999px;
        font-weight: 900;
      }

      .up { color: #047857; background: rgba(16, 185, 129, 0.12); }
      .down { color: #be123c; background: rgba(244, 63, 94, 0.1); }
      .flat { color: #1d4ed8; background: rgba(29, 78, 216, 0.1); }

      small {
        color: $text-secondary;
      }
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;

    strong {
      display: block;
      color: $text-primary;
      font-size: 16px;
      font-weight: 900;
    }

    small {
      display: block;
      margin-top: 6px;
      color: $text-secondary;
      font-size: 12px;
    }
  }

  .chart-legend {
    display: flex;
    align-items: center;
    gap: 14px;
    color: $text-secondary;
    font-size: 12px;
    font-weight: 700;

    span {
      display: inline-flex;
      align-items: center;
      gap: 6px;
    }

    .legend-dot {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 50%;

      &.inbound { background: #1d4ed8; }
      &.outbound { background: #16a34a; }
    }
  }

  .chart-container {
    padding: 10px 4px 4px;
  }

  .fake-chart {
    position: relative;
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    height: 260px;
    padding: 26px 14px 28px;
    border-radius: 22px;
    background:
      linear-gradient(rgba(148, 163, 184, 0.11) 1px, transparent 1px),
      linear-gradient(90deg, rgba(148, 163, 184, 0.08) 1px, transparent 1px),
      rgba(248, 250, 252, 0.76);
    background-size: 100% 52px, 52px 100%, auto;
  }

  .chart-bar-group {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    height: 100%;
  }

  .bar-pair {
    display: flex;
    align-items: flex-end;
    gap: 7px;
    flex: 1;
  }

  .bar {
    width: clamp(14px, 2vw, 24px);
    min-height: 8px;
    border-radius: 999px 999px 8px 8px;
    transition: height 0.7s cubic-bezier(0.34, 1.56, 0.64, 1);
    position: relative;

    &.inbound { background: linear-gradient(180deg, #3b82f6, #1d4ed8); }
    &.outbound { background: linear-gradient(180deg, #34d399, #059669); }

    .bar-tip {
      position: absolute;
      top: -24px;
      left: 50%;
      transform: translateX(-50%);
      opacity: 0;
      padding: 3px 6px;
      color: #fff;
      font-size: 11px;
      border-radius: 8px;
      background: rgba(15, 23, 42, 0.86);
      transition: opacity 0.2s ease;
    }

    &:hover .bar-tip {
      opacity: 1;
    }
  }

  .bar-label {
    color: $text-secondary;
    font-size: 11px;
    font-weight: 700;
  }

  .warning-list {
    display: grid;
    gap: 12px;
  }

  .warning-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 13px;
    border-radius: 18px;
    background: rgba(248, 250, 252, 0.82);
    border: 1px solid rgba(226, 232, 240, 0.8);
    transition: transform 0.2s $ease-out, background 0.2s ease;

    &:hover {
      transform: translateX(4px);
      background: rgba(255, 247, 237, 0.86);
    }
  }

  .warning-main {
    display: flex;
    flex-direction: column;
    min-width: 0;

    span {
      color: $text-primary;
      font-weight: 800;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    small {
      margin-top: 5px;
      color: $text-secondary;
    }
  }

  .warning-stock {
    flex-shrink: 0;
    color: #be123c;

    strong {
      font-size: 22px;
      font-weight: 900;
    }

    span {
      color: $text-secondary;
      font-size: 12px;
    }
  }

  .shortcut-list {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 14px;
  }

  .shortcut-item {
    display: flex;
    align-items: center;
    gap: 13px;
    min-height: 86px;
    padding: 16px;
    border-radius: 20px;
    cursor: pointer;
    background: rgba(248, 250, 252, 0.72);
    border: 1px solid rgba(226, 232, 240, 0.8);
    transition: transform 0.22s $ease-out, box-shadow 0.22s ease, background 0.22s ease;

    &:hover {
      transform: translateY(-2px);
      background: #fff;
      box-shadow: 0 16px 36px rgba(15, 23, 42, 0.09);

      .shortcut-arrow {
        transform: translateX(3px);
        opacity: 1;
      }
    }

    span {
      display: block;
      color: $text-primary;
      font-weight: 900;
    }

    small {
      display: block;
      margin-top: 6px;
      color: $text-secondary;
      font-size: 12px;
    }
  }

  .shortcut-icon {
    width: 46px;
    height: 46px;
    flex: 0 0 46px;
    display: grid;
    place-items: center;
    color: #fff;
    border-radius: 17px;

    &.blue { background: linear-gradient(135deg, #1d4ed8, #2563eb); }
    &.green { background: linear-gradient(135deg, #059669, #34d399); }
    &.amber { background: linear-gradient(135deg, #d97706, #fbbf24); }
    &.rose { background: linear-gradient(135deg, #e11d48, #fb7185); }
  }

  .shortcut-arrow {
    margin-left: auto;
    color: $text-secondary;
    opacity: 0.55;
    transition: transform 0.2s $ease-out, opacity 0.2s ease;
  }
}

@keyframes cardRise {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes orbitPulse {
  50% {
    transform: translate3d(-12px, 10px, 0) scale(1.05);
  }
}

@media (max-width: 1180px) {
  .dashboard .shortcut-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .dashboard {
    .hero-panel {
      align-items: flex-start;
      flex-direction: column;
      padding: 22px;
    }

    .shortcut-list {
      grid-template-columns: 1fr;
    }
  }
}
</style>
