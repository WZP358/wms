import request from '@/utils/request'

// 查询SN码列表
export function listSn(query) {
  return request({
    url: '/wms/sn/list',
    method: 'get',
    params: query
  })
}

// 查询SN码详细
export function getSn(snCode) {
  return request({
    url: '/wms/sn/' + snCode,
    method: 'get'
  })
}

// 新增SN码（直接输入）
export function addSn(data) {
  return request({
    url: '/wms/sn',
    method: 'post',
    data: data
  })
}

// 修改SN码
export function updateSn(data) {
  return request({
    url: '/wms/sn',
    method: 'put',
    data: data
  })
}

// 删除SN码
export function delSn(snCodes) {
  return request({
    url: '/wms/sn/' + snCodes,
    method: 'delete'
  })
}

// 扫描SN码（模拟扫描枪接口）
export function scanSn() {
  return request({
    url: '/wms/sn/scan',
    method: 'post'
  })
}

// 直接输入SN码（验证接口 - 增强版）
export function inputSn(snCode, excludeSnCodes = []) {
  return request({
    url: '/wms/sn/input',
    method: 'post',
    data: { 
      snCode,
      excludeSnCodes: excludeSnCodes || []
    }
  })
}

// 批量验证SN码
export function validateSnBatch(snCodes) {
  return request({
    url: '/wms/sn/validate/batch',
    method: 'post',
    data: snCodes
  })
}

// 批量验证SN码（增强版）
export function validateSnBatchEnhanced(snCodes, excludeSnCodes = []) {
  return request({
    url: '/wms/sn/validate/batch/enhanced',
    method: 'post',
    data: {
      snCodes,
      excludeSnCodes: excludeSnCodes || []
    }
  })
}

// 绑定SN码到物品
export function bindSnToItem(data) {
  return request({
    url: '/wms/sn/bind',
    method: 'post',
    data: data
  })
}

// 解绑SN码
export function unbindSn(snCode) {
  return request({
    url: '/wms/sn/unbind/' + snCode,
    method: 'post'
  })
}

