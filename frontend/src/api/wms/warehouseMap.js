import request from '@/utils/request'

// 获取仓库列表
export function listWarehouses() {
  return request({
    url: '/wms/warehouse/listNoPage',
    method: 'get'
  })
}

// 根据仓库ID获取库区列表
export function listAreasByWarehouse(warehouseId) {
  return request({
    url: '/wms/area/listNoPage',
    method: 'get',
    params: { warehouseId }
  })
}

// 获取仓库地图配置
export function getWarehouseMap(warehouseId) {
  return request({
    url: '/wms/map/config',
    method: 'get',
    params: { warehouseId }
  })
}

// 保存仓库地图配置
export function saveWarehouseMap(data) {
  return request({
    url: '/wms/map/config',
    method: 'post',
    data: data
  })
}

// 获取库区列表（兼容旧接口）
export function listAreas() {
  return request({
    url: '/wms/map/areas',
    method: 'get'
  })
}

// 保存库区配置
export function saveAreas(data) {
  return request({
    url: '/wms/map/areas',
    method: 'post',
    data: data
  })
}

// 获取历史路径记录
export function getPathHistory(params) {
  return request({
    url: '/wms/map/path/history',
    method: 'get',
    params: params
  })
}

// 保存路径记录
export function savePathRecord(data) {
  return request({
    url: '/wms/map/path/record',
    method: 'post',
    data: data
  })
}

