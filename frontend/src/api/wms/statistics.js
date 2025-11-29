import request from '@/utils/request'

// 获取出入库趋势分析
export function getInOutTrend(period) {
  return request({
    url: '/wms/statistics/trend',
    method: 'get',
    params: { period }
  })
}

// 获取热销商品TOP10
export function getHotProducts() {
  return request({
    url: '/wms/statistics/hotProducts',
    method: 'get'
  })
}

// 获取滞销商品预警
export function getSlowProducts() {
  return request({
    url: '/wms/statistics/slowProducts',
    method: 'get'
  })
}

// 获取供应商/客户分析
export function getMerchantAnalysis() {
  return request({
    url: '/wms/statistics/merchantAnalysis',
    method: 'get'
  })
}

// 获取库存需求预测
export function getInventoryForecast(days) {
  return request({
    url: '/wms/statistics/inventoryForecast',
    method: 'get',
    params: { days }
  })
}

// 获取各仓库/库区的空间利用率
export function getWarehouseAreaUtilization() {
  return request({
    url: '/wms/statistics/warehouseAreaUtilization',
    method: 'get'
  })
}

// 获取库位使用情况热力图
export function getLocationHeatmap() {
  return request({
    url: '/wms/statistics/locationHeatmap',
    method: 'get'
  })
}

// 获取出入库同比环比分析
export function getInOutYearOverYear(period) {
  return request({
    url: '/wms/statistics/inOutYearOverYear',
    method: 'get',
    params: { period }
  })
}

// 获取库存金额变化趋势
export function getInventoryAmountTrend(period) {
  return request({
    url: '/wms/statistics/inventoryAmountTrend',
    method: 'get',
    params: { period }
  })
}

