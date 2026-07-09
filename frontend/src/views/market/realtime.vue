<template>
  <div class="market-screen">
    <section class="market-hero">
      <div>
        <span class="eyebrow">Copper Market Terminal</span>
        <h1>铜产业行情监控</h1>
        <p>按需加载单只铜产业股票的当天分时走势，低频刷新最新行情，避免页面本地伪造曲线和持续占用内存。</p>
      </div>
      <div class="market-status" :class="connectionState">
        <span class="pulse" />
        <strong>{{ statusText }}</strong>
        <small>{{ lastUpdatedText }}</small>
      </div>
    </section>

    <section class="control-panel">
      <div>
        <span>当前品种</span>
        <strong>{{ currentQuote?.name || selectedOption?.name }}</strong>
      </div>
      <el-select v-model="selectedSymbol" class="symbol-select" filterable @change="handleSymbolChange">
        <el-option
          v-for="item in copperStocks"
          :key="item.symbol"
          :label="`${item.name}（${item.symbol}）`"
          :value="item.symbol"
        />
      </el-select>
    </section>

    <section class="chart-panel">
      <header>
        <div>
          <span class="symbol">{{ currentQuote?.symbol || selectedSymbol }}</span>
          <h2>{{ currentQuote?.name || selectedOption?.name }} · 当天分时走势</h2>
        </div>
        <div class="price-summary" v-if="currentQuote">
          <strong>{{ formatPrice(currentQuote.price, currentQuote.currency) }}</strong>
          <em :class="currentQuote.change >= 0 ? 'up' : 'down'">
            {{ formatSigned(currentQuote.change) }} / {{ formatPercent(currentQuote.changePercent) }}
          </em>
        </div>
      </header>

      <svg class="line-chart" viewBox="0 0 960 320" preserveAspectRatio="none" role="img" aria-label="当天分时价格折线图">
        <defs>
          <linearGradient id="copperLine" x1="0" x2="1" y1="0" y2="0">
            <stop offset="0%" stop-color="#f59e0b" />
            <stop offset="100%" :stop-color="currentQuote && currentQuote.change < 0 ? '#ef4444' : '#22c55e'" />
          </linearGradient>
          <linearGradient id="chartFill" x1="0" x2="0" y1="0" y2="1">
            <stop offset="0%" stop-color="rgba(245, 158, 11, 0.28)" />
            <stop offset="100%" stop-color="rgba(245, 158, 11, 0)" />
          </linearGradient>
        </defs>
        <g class="grid-lines">
          <line v-for="line in 5" :key="line" x1="0" x2="960" :y1="line * 54" :y2="line * 54" />
        </g>
        <line
          v-if="previousCloseY"
          class="previous-close-line"
          x1="0"
          x2="960"
          :y1="previousCloseY"
          :y2="previousCloseY"
        />
        <polygon v-if="areaPoints" :points="areaPoints" fill="url(#chartFill)" />
        <polyline
          v-if="linePoints"
          :points="linePoints"
          fill="none"
          stroke="url(#copperLine)"
          stroke-width="5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        <polyline
          v-if="averageLinePoints"
          :points="averageLinePoints"
          fill="none"
          stroke="rgba(96, 165, 250, 0.78)"
          stroke-width="2.5"
          stroke-dasharray="8 8"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>

      <div class="chart-footer">
        <span>分时点：{{ trendPoints.length }}</span>
        <span>时间范围：{{ timeRangeText }}</span>
        <span>刷新间隔：{{ refreshIntervalMs / 1000 }} 秒</span>
        <span>数据源：腾讯报价 + 东方财富分时</span>
      </div>
    </section>

    <section class="detail-panel" v-if="currentQuote">
      <div class="detail-item">
        <span>开盘</span>
        <strong>{{ formatPrice(currentQuote.open, currentQuote.currency) }}</strong>
      </div>
      <div class="detail-item">
        <span>昨收</span>
        <strong>{{ formatPrice(currentQuote.previousClose, currentQuote.currency) }}</strong>
      </div>
      <div class="detail-item">
        <span>最高</span>
        <strong>{{ formatPrice(currentQuote.dayHigh, currentQuote.currency) }}</strong>
      </div>
      <div class="detail-item">
        <span>最低</span>
        <strong>{{ formatPrice(currentQuote.dayLow, currentQuote.currency) }}</strong>
      </div>
      <div class="detail-item">
        <span>成交量</span>
        <strong>{{ formatVolume(currentQuote.volume) }}</strong>
      </div>
      <div class="detail-item">
        <span>行情时间</span>
        <strong>{{ formatDateTime(currentQuote.quoteTime) }}</strong>
      </div>
    </section>

    <el-empty v-if="!loading && !currentQuote" description="暂无行情数据，请检查后端服务或腾讯行情接口" />
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'MarketRealtime' })

import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMarketQuotes, type MarketQuote, type MarketTrendPoint } from '@/api/market'

const copperStocks = [
  { symbol: 'sh600362', name: '江西铜业' },
  { symbol: 'sz000630', name: '铜陵有色' },
  { symbol: 'sz000878', name: '云南铜业' },
  { symbol: 'sh601899', name: '紫金矿业' },
  { symbol: 'sh603993', name: '洛阳钼业' },
  { symbol: 'sh601168', name: '西部矿业' },
  { symbol: 'sz000737', name: '北方铜业' },
  { symbol: 'sh601212', name: '白银有色' },
  { symbol: 'sz002203', name: '海亮股份' },
  { symbol: 'sh601609', name: '金田股份' }
]

const refreshIntervalMs = 45000

const selectedSymbol = ref(copperStocks[0].symbol)
const currentQuote = ref<MarketQuote | null>(null)
const trendPoints = ref<MarketTrendPoint[]>([])
const loading = ref(false)
const lastUpdated = ref<Date | null>(null)
const connectionState = ref<'idle' | 'live' | 'error'>('idle')
let timer: number | undefined
let inFlight = false

const selectedOption = computed(() => copperStocks.find((item) => item.symbol === selectedSymbol.value))
const statusText = computed(() => {
  if (connectionState.value === 'live') return '历史分时 + 低频刷新'
  if (connectionState.value === 'error') return '行情源异常'
  return '等待刷新'
})
const lastUpdatedText = computed(() => lastUpdated.value ? `更新于 ${lastUpdated.value.toLocaleTimeString()}` : '尚未更新')

const chartMetrics = computed(() => {
  const values = trendPoints.value
    .flatMap((point) => [point.price, point.averagePrice])
    .filter((value) => Number.isFinite(value) && value > 0)
  if (currentQuote.value?.previousClose) {
    values.push(currentQuote.value.previousClose)
  }
  if (values.length === 0) return { min: 0, range: 1 }
  const min = Math.min(...values)
  const max = Math.max(...values)
  const padding = Math.max((max - min) * 0.12, max * 0.002)
  return {
    min: min - padding,
    range: max - min + padding * 2 || 1
  }
})

const buildChartPoints = (field: 'price' | 'averagePrice') => {
  const points = trendPoints.value
  if (points.length === 0) return ''
  if (points.length === 1) return `0,160 960,160`
  return points.map((point, index) => {
    const value = point[field]
    if (!Number.isFinite(value) || value <= 0) return ''
    const x = (index / (points.length - 1)) * 960
    const y = 282 - ((value - chartMetrics.value.min) / chartMetrics.value.range) * 244
    return `${x.toFixed(1)},${y.toFixed(1)}`
  }).filter(Boolean).join(' ')
}

const linePoints = computed(() => buildChartPoints('price'))
const averageLinePoints = computed(() => buildChartPoints('averagePrice'))
const areaPoints = computed(() => linePoints.value ? `0,320 ${linePoints.value} 960,320` : '')
const previousCloseY = computed(() => {
  const value = currentQuote.value?.previousClose
  if (!value || !Number.isFinite(value)) return ''
  return (282 - ((value - chartMetrics.value.min) / chartMetrics.value.range) * 244).toFixed(1)
})
const timeRangeText = computed(() => {
  const first = trendPoints.value[0]?.time
  const last = trendPoints.value[trendPoints.value.length - 1]?.time
  if (!first || !last) return '--'
  return `${formatTrendTime(first)} - ${formatTrendTime(last)}`
})

const loadQuote = async () => {
  if (inFlight || document.hidden) return
  inFlight = true
  loading.value = true
  try {
    const res = await getMarketQuotes([selectedSymbol.value])
    const quote = res.data?.[0] || null
    currentQuote.value = quote
    if (quote) {
      trendPoints.value = quote.trendPoints?.filter((point) => Number.isFinite(point.price) && point.price > 0) || []
      lastUpdated.value = new Date()
      connectionState.value = trendPoints.value.length > 0 ? 'live' : 'error'
      if (trendPoints.value.length === 0) {
        ElMessage.warning('已获取当前报价，但分时历史数据为空')
      }
    } else {
      connectionState.value = 'error'
    }
  } catch (error: any) {
    connectionState.value = 'error'
    ElMessage.error(error?.message || '行情数据获取失败')
  } finally {
    loading.value = false
    inFlight = false
  }
}

const handleSymbolChange = () => {
  currentQuote.value = null
  trendPoints.value = []
  lastUpdated.value = null
  startPolling()
}

const startPolling = () => {
  stopPolling()
  loadQuote()
  timer = window.setInterval(loadQuote, refreshIntervalMs)
}

const stopPolling = () => {
  if (timer) {
    window.clearInterval(timer)
    timer = undefined
  }
}

const handleVisibilityChange = () => {
  if (document.hidden) {
    stopPolling()
  } else {
    startPolling()
  }
}

const formatPrice = (value: number, currency = 'CNY') => {
  if (!Number.isFinite(value)) return '--'
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: currency || 'CNY',
    maximumFractionDigits: 2
  }).format(value)
}

const formatSigned = (value: number) => `${value >= 0 ? '+' : ''}${value.toFixed(2)}`
const formatPercent = (value: number) => `${value >= 0 ? '+' : ''}${value.toFixed(2)}%`
const formatVolume = (value: number) => {
  if (!Number.isFinite(value)) return '--'
  if (value >= 100000000) return `${(value / 100000000).toFixed(2)}亿手`
  if (value >= 10000) return `${(value / 10000).toFixed(2)}万手`
  return `${value}手`
}
const formatDateTime = (value: string) => value ? new Date(value).toLocaleString() : '--'
const formatTrendTime = (value: string) => value.split(' ')[1]?.slice(0, 5) || value

onMounted(() => {
  document.addEventListener('visibilitychange', handleVisibilityChange)
  startPolling()
})

onBeforeUnmount(() => {
  stopPolling()
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped lang="scss">
.market-screen {
  min-height: 100%;
  color: #dbeafe;
  padding-bottom: 12px;
}

.market-hero,
.control-panel,
.chart-panel,
.detail-panel {
  border: 1px solid rgba(148, 163, 184, 0.16);
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.14);
}

.market-hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  padding: 28px;
  overflow: hidden;
  border-radius: 30px;
  background:
    radial-gradient(circle at 82% 18%, rgba(245, 158, 11, 0.25), transparent 30%),
    linear-gradient(135deg, #07111f 0%, #1f2937 48%, #431407 100%);
}

.market-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0.14;
  background-image:
    linear-gradient(90deg, rgba(255,255,255,0.18) 1px, transparent 1px),
    linear-gradient(rgba(255,255,255,0.16) 1px, transparent 1px);
  background-size: 32px 32px;
}

.market-hero > * {
  position: relative;
  z-index: 1;
}

.eyebrow,
.symbol {
  color: #fbbf24;
  font-weight: 900;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.market-hero h1 {
  margin: 10px 0 8px;
  color: #f8fafc;
  font-size: clamp(30px, 4vw, 48px);
  letter-spacing: -0.05em;
}

.market-hero p {
  margin: 0;
  max-width: 700px;
  color: rgba(219, 234, 254, 0.72);
  line-height: 1.8;
}

.market-status {
  min-width: 168px;
  padding: 16px;
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.52);
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.market-status strong,
.market-status small {
  display: block;
}

.market-status small {
  margin-top: 7px;
  color: rgba(219, 234, 254, 0.62);
}

.pulse {
  display: inline-block;
  width: 9px;
  height: 9px;
  margin-right: 8px;
  border-radius: 50%;
  background: #94a3b8;
}

.market-status.live .pulse {
  background: #22c55e;
  box-shadow: 0 0 0 7px rgba(34, 197, 94, 0.12);
}

.market-status.error .pulse {
  background: #ef4444;
  box-shadow: 0 0 0 7px rgba(239, 68, 68, 0.12);
}

.control-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin: 18px 0;
  padding: 18px 20px;
  border-radius: 24px;
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.94), rgba(40, 31, 20, 0.9));
}

.control-panel span {
  display: block;
  color: rgba(219, 234, 254, 0.62);
  font-size: 12px;
}

.control-panel strong {
  display: block;
  margin-top: 6px;
  color: #f8fafc;
  font-size: 22px;
}

.symbol-select {
  width: min(360px, 100%);
}

.chart-panel {
  padding: 24px;
  border-radius: 30px;
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.96), rgba(18, 29, 44, 0.94));
}

.chart-panel header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.chart-panel h2 {
  margin: 6px 0 0;
  color: rgba(219, 234, 254, 0.72);
  font-size: 14px;
}

.price-summary {
  text-align: right;
}

.price-summary strong {
  display: block;
  color: #f8fafc;
  font-size: clamp(28px, 4vw, 44px);
  letter-spacing: -0.05em;
}

.price-summary em {
  display: block;
  margin-top: 6px;
  font-style: normal;
  font-weight: 900;
}

.line-chart {
  width: 100%;
  height: 340px;
  border-radius: 22px;
  background:
    radial-gradient(circle at 14% 10%, rgba(245, 158, 11, 0.14), transparent 30%),
    rgba(2, 6, 23, 0.32);
}

.grid-lines line {
  stroke: rgba(148, 163, 184, 0.14);
  stroke-width: 1;
}

.previous-close-line {
  stroke: rgba(248, 250, 252, 0.38);
  stroke-width: 1.4;
  stroke-dasharray: 7 7;
}

.chart-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
  color: rgba(219, 234, 254, 0.58);
  font-size: 12px;
}

.detail-panel {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
  padding: 18px;
  border-radius: 26px;
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.94), rgba(40, 31, 20, 0.9));
}

.detail-item {
  padding: 14px;
  border-radius: 18px;
  background: rgba(2, 6, 23, 0.24);
}

.detail-item span {
  color: rgba(219, 234, 254, 0.58);
  font-size: 12px;
}

.detail-item strong {
  display: block;
  margin-top: 8px;
  color: #f8fafc;
  font-size: 16px;
}

.up { color: #22c55e; }
.down { color: #ef4444; }

@media (max-width: 1180px) {
  .detail-panel {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .market-hero,
  .control-panel,
  .chart-panel header,
  .chart-footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .price-summary {
    text-align: left;
  }

  .detail-panel {
    grid-template-columns: 1fr;
  }
}
</style>
