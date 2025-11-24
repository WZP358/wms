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
  </div>
</template>

<script setup name="Index">
import { Warning, Clock, ArrowRight } from '@element-plus/icons-vue'
import { getInventoryWarningCount, listInventoryWarningAll, getExpirationReminderCount, listExpirationReminderAll } from '@/api/wms/inventoryWarning'
import { useRouter } from 'vue-router'

const router = useRouter()
const warningCount = ref(0)
const expirationCount = ref(0)
const warningList = ref([])
const expirationList = ref([])

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

onMounted(() => {
  getWarningData()
  getExpirationData()
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

