<template>
  <div class="app-container home">
    <el-row :gutter="20" class="mb20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><Warning /></el-icon> 库存预警
              </span>
              <el-button type="primary" link @click="goToWarning">
                查看详情 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="warning-content">
            <div class="warning-count">
              <span class="count-number">{{ warningCount }}</span>
              <span class="count-label">项预警</span>
            </div>
            <div class="warning-list" v-if="warningList.length > 0">
              <div v-for="(item, index) in warningList.slice(0, 5)" :key="index" class="warning-item">
                <span class="item-name">{{ item.itemName }} - {{ item.skuName }}</span>
                <span class="item-quantity" style="color: red">
                  库存: {{ item.currentQuantity }} / 最低: {{ item.minStock }}
                </span>
              </div>
            </div>
            <div v-else class="no-data">暂无库存预警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><Clock /></el-icon> 到期提醒
              </span>
              <el-button type="primary" link @click="goToExpiration">
                查看详情 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="expiration-content">
            <div class="expiration-count">
              <span class="count-number">{{ expirationCount }}</span>
              <span class="count-label">项提醒</span>
            </div>
            <div class="expiration-list" v-if="expirationList.length > 0">
              <div v-for="(item, index) in expirationList.slice(0, 5)" :key="index" class="expiration-item">
                <span class="item-name">{{ item.itemName }} - {{ item.skuName }}</span>
                <span class="item-date" :style="{ color: item.daysToExpire <= 7 ? 'red' : item.daysToExpire <= 30 ? 'orange' : '' }">
                  {{ formatDate(item.expirationDate) }} ({{ item.daysToExpire }}天后)
                </span>
              </div>
            </div>
            <div v-else class="no-data">暂无到期提醒</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 数据分析图表 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><DataAnalysis /></el-icon> 库存金额分析
              </span>
              <el-button type="primary" link @click="downloadChart('inventoryAmount')">
                <el-icon><Download /></el-icon> 下载
              </el-button>
            </div>
          </template>
          <div ref="inventoryAmountChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><TrendCharts /></el-icon> 库存周转率分析
              </span>
              <el-button type="primary" link @click="downloadChart('turnoverRate')">
                <el-icon><Download /></el-icon> 下载
              </el-button>
            </div>
          </template>
          <div ref="turnoverRateChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 出入库趋势分析 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><TrendCharts /></el-icon> 出入库趋势分析
              </span>
              <div style="display: flex; gap: 10px; align-items: center;">
                <el-select v-model="trendPeriod" @change="loadTrendData" style="width: 120px">
                  <el-option label="日" value="day" />
                  <el-option label="周" value="week" />
                  <el-option label="月" value="month" />
                  <el-option label="年" value="year" />
                </el-select>
                <el-button type="primary" link @click="downloadChart('trend')">
                  <el-icon><Download /></el-icon> 下载
                </el-button>
              </div>
            </div>
          </template>
          <div ref="trendChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热销商品和滞销商品 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><TrendCharts /></el-icon> 热销商品TOP10
              </span>
              <el-button type="primary" link @click="downloadChart('hotProducts')">
                <el-icon><Download /></el-icon> 下载
              </el-button>
            </div>
          </template>
          <div ref="hotProductsChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><Warning /></el-icon> 滞销商品预警
              </span>
              <el-button type="primary" link @click="downloadChart('slowProducts')">
                <el-icon><Download /></el-icon> 下载
              </el-button>
            </div>
          </template>
          <div ref="slowProductsChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 供应商/客户分析 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><DataAnalysis /></el-icon> 供应商/客户分析
              </span>
              <el-button type="primary" link @click="downloadChart('merchant')">
                <el-icon><Download /></el-icon> 下载
              </el-button>
            </div>
          </template>
          <div ref="merchantChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存需求预测 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-size: large;font-weight: bold">
                <el-icon><TrendCharts /></el-icon> 库存需求预测
              </span>
              <div style="display: flex; gap: 10px; align-items: center;">
                <el-select v-model="forecastDays" @change="loadForecastData" style="width: 120px">
                  <el-option label="7天" :value="7" />
                  <el-option label="30天" :value="30" />
                  <el-option label="90天" :value="90" />
                </el-select>
                <el-button type="primary" link @click="downloadChart('forecast')">
                  <el-icon><Download /></el-icon> 下载
                </el-button>
              </div>
            </div>
          </template>
          <div ref="forecastChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import { Warning, Clock, ArrowRight, DataAnalysis, TrendCharts, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getInventoryWarningCount, listInventoryWarningAll, getExpirationReminderCount, listExpirationReminderAll } from '@/api/wms/inventoryWarning'
import { getInventoryAmount, getInventoryTurnoverRate } from '@/api/wms/inventory'
import { getInOutTrend, getHotProducts, getSlowProducts, getMerchantAnalysis, getInventoryForecast } from '@/api/wms/statistics'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()
const warningCount = ref(0)
const expirationCount = ref(0)
const warningList = ref([])
const expirationList = ref([])
const inventoryAmountChart = ref(null)
const turnoverRateChart = ref(null)
const trendChart = ref(null)
const hotProductsChart = ref(null)
const slowProductsChart = ref(null)
const merchantChart = ref(null)
const forecastChart = ref(null)
const trendPeriod = ref('day')
const forecastDays = ref(30)

// 保存图表实例
const inventoryAmountChartInstance = ref(null)
const turnoverRateChartInstance = ref(null)
const trendChartInstance = ref(null)
const hotProductsChartInstance = ref(null)
const slowProductsChartInstance = ref(null)
const merchantChartInstance = ref(null)
const forecastChartInstance = ref(null)

/** 获取库存预警数据 */
function getWarningData() {
  getInventoryWarningCount().then(response => {
    warningCount.value = response.data || 0
  })
  listInventoryWarningAll().then(response => {
    warningList.value = response.data || []
  })
}

/** 获取到期提醒数据 */
function getExpirationData() {
  getExpirationReminderCount(30).then(response => {
    expirationCount.value = response.data || 0
  })
  listExpirationReminderAll(30).then(response => {
    expirationList.value = response.data || []
  })
}

/** 跳转到库存预警页面 */
function goToWarning() {
  router.push({ path: '/wms/warning', query: { tab: 'warning' } })
}

/** 跳转到到期提醒页面 */
function goToExpiration() {
  router.push({ path: '/wms/warning', query: { tab: 'expiration' } })
}

/** 格式化日期 */
function formatDate(date) {
  if (!date) return ''
  return date.replace('T', ' ').substring(0, 10)
}

/** 获取库存金额数据并渲染图表 */
function getInventoryAmountData() {
  getInventoryAmount().then(response => {
    const amount = response.data || 0
    // 如果图表已存在，先销毁
    const existingChart = echarts.getInstanceByDom(inventoryAmountChart.value)
    if (existingChart) {
      existingChart.dispose()
      inventoryAmountChartInstance.value = null
    }
    const chartInstance = echarts.init(inventoryAmountChart.value, 'macarons')
    inventoryAmountChartInstance.value = chartInstance
    chartInstance.setOption({
      title: {
        text: '实时库存金额',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} 元'
      },
      series: [
        {
          name: '库存金额',
          type: 'gauge',
          center: ['50%', '60%'],
          startAngle: 200,
          endAngle: -20,
          min: 0,
          max: Math.max(amount * 2, 100000),
          splitNumber: 8,
          axisLine: {
            lineStyle: {
              width: 6,
              color: [
                [0.3, '#67e0e3'],
                [0.7, '#37a2da'],
                [1, '#fd666d']
              ]
            }
          },
          pointer: {
            itemStyle: {
              color: 'inherit'
            }
          },
          axisTick: {
            distance: -30,
            length: 8,
            lineStyle: {
              color: '#fff',
              width: 2
            }
          },
          splitLine: {
            distance: -30,
            length: 14,
            lineStyle: {
              color: '#fff',
              width: 3
            }
          },
          axisLabel: {
            color: '#464646',
            distance: -20,
            fontSize: 12
          },
          detail: {
            valueAnimation: true,
            formatter: function(value) {
              return Math.round(value).toLocaleString() + ' 元'
            },
            color: 'inherit',
            fontSize: 20,
            fontWeight: 'bold',
            offsetCenter: [0, '20%']
          },
          data: [
            {
              value: amount
            }
          ]
        }
      ]
    })
    
    // 响应式调整
    window.addEventListener('resize', () => {
      chartInstance.resize()
    })
  })
}

/** 获取库存周转率数据并渲染图表 */
function getTurnoverRateData() {
  getInventoryTurnoverRate().then(response => {
    const rate = response.data || 0
    // 如果图表已存在，先销毁
    const existingChart = echarts.getInstanceByDom(turnoverRateChart.value)
    if (existingChart) {
      existingChart.dispose()
      turnoverRateChartInstance.value = null
    }
    const chartInstance = echarts.init(turnoverRateChart.value, 'macarons')
    turnoverRateChartInstance.value = chartInstance
    chartInstance.setOption({
      title: {
        text: '实时库存周转率',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}'
      },
      series: [
        {
          name: '库存周转率',
          type: 'gauge',
          center: ['50%', '60%'],
          startAngle: 200,
          endAngle: -20,
          min: 0,
          max: 5,
          splitNumber: 10,
          axisLine: {
            lineStyle: {
              width: 6,
              color: [
                [0.2, '#91c7ae'],
                [0.4, '#37a2da'],
                [0.6, '#fd666d'],
                [1, '#fc8452']
              ]
            }
          },
          pointer: {
            itemStyle: {
              color: 'inherit'
            }
          },
          axisTick: {
            distance: -30,
            length: 8,
            lineStyle: {
              color: '#fff',
              width: 2
            }
          },
          splitLine: {
            distance: -30,
            length: 14,
            lineStyle: {
              color: '#fff',
              width: 3
            }
          },
          axisLabel: {
            color: '#464646',
            distance: -20,
            fontSize: 12
          },
          detail: {
            valueAnimation: true,
            formatter: function(value) {
              return value.toFixed(2)
            },
            color: 'inherit',
            fontSize: 20,
            fontWeight: 'bold',
            offsetCenter: [0, '20%']
          },
          data: [
            {
              value: rate
            }
          ]
        }
      ]
    })
    
    // 响应式调整
    window.addEventListener('resize', () => {
      chartInstance.resize()
    })
  })
}

/** 加载出入库趋势数据 */
function loadTrendData() {
  getInOutTrend(trendPeriod.value).then(response => {
    const data = response.data || {}
    // 如果图表已存在，先销毁
    const existingChart = echarts.getInstanceByDom(trendChart.value)
    if (existingChart) {
      existingChart.dispose()
      trendChartInstance.value = null
    }
    const chartInstance = echarts.init(trendChart.value, 'macarons')
    trendChartInstance.value = chartInstance
    chartInstance.setOption({
      title: {
        text: '出入库趋势分析',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['入库金额', '出库金额', '入库数量', '出库数量'],
        top: 30
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.dates || []
      },
      yAxis: [
        {
          type: 'value',
          name: '金额(元)',
          position: 'left'
        },
        {
          type: 'value',
          name: '数量',
          position: 'right'
        }
      ],
      series: [
        {
          name: '入库金额',
          type: 'line',
          data: (data.receiptAmounts || []).map(v => Number(v)),
          smooth: true
        },
        {
          name: '出库金额',
          type: 'line',
          data: (data.shipmentAmounts || []).map(v => Number(v)),
          smooth: true
        },
        {
          name: '入库数量',
          type: 'line',
          yAxisIndex: 1,
          data: (data.receiptQuantities || []).map(v => Number(v)),
          smooth: true
        },
        {
          name: '出库数量',
          type: 'line',
          yAxisIndex: 1,
          data: (data.shipmentQuantities || []).map(v => Number(v)),
          smooth: true
        }
      ]
    })
    window.addEventListener('resize', () => chartInstance.resize())
  })
}

/** 加载热销商品数据 */
function loadHotProductsData() {
  getHotProducts().then(response => {
    const data = response.data || {}
    // 如果图表已存在，先销毁
    const existingChart = echarts.getInstanceByDom(hotProductsChart.value)
    if (existingChart) {
      existingChart.dispose()
      hotProductsChartInstance.value = null
    }
    const chartInstance = echarts.init(hotProductsChart.value, 'macarons')
    hotProductsChartInstance.value = chartInstance
    chartInstance.setOption({
      title: {
        text: '热销商品TOP10',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: (data.productNames || []).map(name => name.length > 15 ? name.substring(0, 15) + '...' : name),
        inverse: true
      },
      series: [
        {
          name: '出库数量',
          type: 'bar',
          data: (data.quantities || []).map(v => Number(v))
        }
      ]
    })
    window.addEventListener('resize', () => chartInstance.resize())
  })
}

/** 加载滞销商品数据 */
function loadSlowProductsData() {
  getSlowProducts().then(response => {
    const data = response.data || {}
    // 如果图表已存在，先销毁
    const existingChart = echarts.getInstanceByDom(slowProductsChart.value)
    if (existingChart) {
      existingChart.dispose()
      slowProductsChartInstance.value = null
    }
    const chartInstance = echarts.init(slowProductsChart.value, 'macarons')
    slowProductsChartInstance.value = chartInstance
    chartInstance.setOption({
      title: {
        text: '滞销商品预警',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        formatter: function(params) {
          return params[0].name + '<br/>' +
                 '库存数量: ' + params[0].value + '<br/>' +
                 '未销售天数: ' + params[1].value + '天'
        }
      },
      legend: {
        data: ['库存数量', '未销售天数'],
        top: 30
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: (data.productNames || []).map(name => name.length > 10 ? name.substring(0, 10) + '...' : name),
        axisLabel: { rotate: 45 }
      },
      yAxis: [
        {
          type: 'value',
          name: '数量',
          position: 'left'
        },
        {
          type: 'value',
          name: '天数',
          position: 'right'
        }
      ],
      series: [
        {
          name: '库存数量',
          type: 'bar',
          data: (data.quantities || []).map(v => Number(v))
        },
        {
          name: '未销售天数',
          type: 'line',
          yAxisIndex: 1,
          data: (data.daysNoSale || []).map(v => Number(v))
        }
      ]
    })
    window.addEventListener('resize', () => chartInstance.resize())
  })
}

/** 加载供应商/客户分析数据 */
function loadMerchantData() {
  getMerchantAnalysis().then(response => {
    const data = response.data || {}
    // 如果图表已存在，先销毁
    const existingChart = echarts.getInstanceByDom(merchantChart.value)
    if (existingChart) {
      existingChart.dispose()
      merchantChartInstance.value = null
    }
    const chartInstance = echarts.init(merchantChart.value, 'macarons')
    merchantChartInstance.value = chartInstance
    chartInstance.setOption({
      title: {
        text: '供应商/客户分析',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      legend: {
        data: ['到货及时率(%)', '退货率(%)'],
        top: 30
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: (data.merchantNames || []).map(name => name.length > 10 ? name.substring(0, 10) + '...' : name),
        axisLabel: { rotate: 45 }
      },
      yAxis: {
        type: 'value',
        name: '百分比(%)',
        max: 100
      },
      series: [
        {
          name: '到货及时率(%)',
          type: 'bar',
          data: (data.onTimeRates || []).map(v => Number(v))
        },
        {
          name: '退货率(%)',
          type: 'bar',
          data: (data.returnRates || []).map(v => Number(v))
        }
      ]
    })
    window.addEventListener('resize', () => chartInstance.resize())
  })
}

/** 加载库存需求预测数据 */
function loadForecastData() {
  getInventoryForecast(forecastDays.value).then(response => {
    const data = response.data || {}
    // 如果图表已存在，先销毁
    const existingChart = echarts.getInstanceByDom(forecastChart.value)
    if (existingChart) {
      existingChart.dispose()
      forecastChartInstance.value = null
    }
    const chartInstance = echarts.init(forecastChart.value, 'macarons')
    forecastChartInstance.value = chartInstance
    chartInstance.setOption({
      title: {
        text: `未来${forecastDays.value}天库存需求预测`,
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      legend: {
        data: ['当前库存', '预测需求', '建议补货'],
        top: 30
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: (data.productNames || []).map(name => name.length > 10 ? name.substring(0, 10) + '...' : name),
        axisLabel: { rotate: 45 }
      },
      yAxis: {
        type: 'value',
        name: '数量'
      },
      series: [
        {
          name: '当前库存',
          type: 'bar',
          data: (data.currentInventory || []).map(v => Number(v))
        },
        {
          name: '预测需求',
          type: 'bar',
          data: (data.forecastDemand || []).map(v => Number(v))
        },
        {
          name: '建议补货',
          type: 'bar',
          data: (data.suggestedOrder || []).map(v => Number(v))
        }
      ]
    })
    window.addEventListener('resize', () => chartInstance.resize())
  })
}

/** 下载图表 */
function downloadChart(chartType) {
  let chartInstance = null
  let fileName = ''
  
  switch (chartType) {
    case 'inventoryAmount':
      chartInstance = inventoryAmountChartInstance.value
      fileName = '库存金额分析'
      break
    case 'turnoverRate':
      chartInstance = turnoverRateChartInstance.value
      fileName = '库存周转率分析'
      break
    case 'trend':
      chartInstance = trendChartInstance.value
      fileName = '出入库趋势分析'
      break
    case 'hotProducts':
      chartInstance = hotProductsChartInstance.value
      fileName = '热销商品TOP10'
      break
    case 'slowProducts':
      chartInstance = slowProductsChartInstance.value
      fileName = '滞销商品预警'
      break
    case 'merchant':
      chartInstance = merchantChartInstance.value
      fileName = '供应商客户分析'
      break
    case 'forecast':
      chartInstance = forecastChartInstance.value
      fileName = `库存需求预测_${forecastDays.value}天`
      break
    default:
      return
  }
  
  if (!chartInstance) {
    ElMessage.warning('图表尚未加载完成，请稍后再试')
    return
  }
  
  try {
    // 获取图表的 base64 图片数据
    const url = chartInstance.getDataURL({
      type: 'png',
      pixelRatio: 2,
      backgroundColor: '#fff'
    })
    
    // 创建下载链接
    const link = document.createElement('a')
    link.href = url
    link.download = `${fileName}_${new Date().getTime()}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('图表下载成功')
  } catch (error) {
    console.error('下载图表失败:', error)
    ElMessage.error('下载图表失败，请稍后再试')
  }
}

onMounted(() => {
  getWarningData()
  getExpirationData()
  // 延迟加载图表，确保DOM已渲染
  nextTick(() => {
    getInventoryAmountData()
    getTurnoverRateData()
    loadTrendData()
    loadHotProductsData()
    loadSlowProductsData()
    loadMerchantData()
    loadForecastData()
  })
})
</script>

<style scoped lang="scss">
.home {
  .mb20 {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .warning-content,
  .expiration-content {
    min-height: 200px;
  }

  .warning-count,
  .expiration-count {
    text-align: center;
    margin-bottom: 20px;
    padding: 20px 0;
    border-bottom: 1px solid #eee;

    .count-number {
      font-size: 48px;
      font-weight: bold;
      color: #409eff;
      margin-right: 10px;
    }

    .count-label {
      font-size: 16px;
      color: #666;
    }
  }

  .warning-list,
  .expiration-list {
    .warning-item,
    .expiration-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .item-name {
        flex: 1;
        font-size: 14px;
        color: #333;
      }

      .item-quantity,
      .item-date {
        font-size: 13px;
        color: #666;
        margin-left: 10px;
      }
    }
  }

  .no-data {
    text-align: center;
    color: #999;
    padding: 40px 0;
    font-size: 14px;
  }
}
</style>

