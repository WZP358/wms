import request from '@/utils/request'

// 查询库存预警列表
export function listInventoryWarning(query) {
  return request({
    url: '/wms/inventoryWarning/warning/list',
    method: 'get',
    params: query
  })
}

// 查询库存预警列表（不分页）
export function listInventoryWarningAll() {
  return request({
    url: '/wms/inventoryWarning/warning/listAll',
    method: 'get'
  })
}

// 获取库存预警数量
export function getInventoryWarningCount() {
  return request({
    url: '/wms/inventoryWarning/warning/count',
    method: 'get'
  })
}

// 查询到期提醒列表
export function listExpirationReminder(query) {
  return request({
    url: '/wms/inventoryWarning/expiration/list',
    method: 'get',
    params: query
  })
}

// 查询到期提醒列表（不分页）
export function listExpirationReminderAll(daysBeforeExpire) {
  return request({
    url: '/wms/inventoryWarning/expiration/listAll',
    method: 'get',
    params: { daysBeforeExpire }
  })
}

// 获取到期提醒数量
export function getExpirationReminderCount(daysBeforeExpire) {
  return request({
    url: '/wms/inventoryWarning/expiration/count',
    method: 'get',
    params: { daysBeforeExpire }
  })
}

