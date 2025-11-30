<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="库存预警" name="warning">
        <el-card>
          <el-form :model="warningQueryParams" :inline="true" label-width="90px" ref="warningQueryRef">
            <el-form-item label="商品名称" prop="itemName">
              <el-input v-model="warningQueryParams.itemName" clearable placeholder="商品名称" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item label="商品编号" prop="itemCode">
              <el-input v-model="warningQueryParams.itemCode" clearable placeholder="商品编号" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleWarningQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetWarningQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card class="mt20">
          <div class="mb8 flex-space-between">
            <div style="font-size: large">库存预警列表</div>
          </div>
          <el-table :data="warningList" border v-loading="warningLoading" empty-text="暂无预警数据">
            <el-table-column label="商品名称" prop="itemName" width="150" />
            <el-table-column label="商品编号" prop="itemCode" width="120" />
            <el-table-column label="规格名称" prop="skuName" width="120" />
            <el-table-column label="规格编号" prop="skuCode" width="120" />
            <el-table-column label="仓库" prop="warehouseName" width="120" />
            <el-table-column label="库区" prop="areaName" width="120" />
            <el-table-column label="当前库存" prop="currentQuantity" width="100" align="right">
              <template #default="{ row }">
                <span :style="{ color: row.currentQuantity < row.minStock ? 'red' : '' }">
                  {{ row.currentQuantity }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="最低库存" prop="minStock" width="100" align="right" />
            <el-table-column label="库存差额" prop="stockDifference" width="100" align="right">
              <template #default="{ row }">
                <span style="color: red">{{ row.stockDifference }}</span>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="warningTotal > 0"
            :total="warningTotal"
            v-model:page="warningQueryParams.pageNum"
            v-model:limit="warningQueryParams.pageSize"
            @pagination="getWarningList"
          />
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="到期提醒" name="expiration">
        <el-card>
          <el-form :model="expirationQueryParams" :inline="true" label-width="90px" ref="expirationQueryRef">
            <el-form-item label="提前天数" prop="daysBeforeExpire">
              <el-select v-model="expirationQueryParams.daysBeforeExpire" clearable style="width: 150px" @change="handleExpirationQuery">
                <el-option label="7天" :value="7" />
                <el-option label="15天" :value="15" />
                <el-option label="30天" :value="30" />
                <el-option label="60天" :value="60" />
                <el-option label="90天" :value="90" />
              </el-select>
            </el-form-item>
            <el-form-item label="商品名称" prop="itemName">
              <el-input v-model="expirationQueryParams.itemName" clearable placeholder="商品名称" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item label="商品编号" prop="itemCode">
              <el-input v-model="expirationQueryParams.itemCode" clearable placeholder="商品编号" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleExpirationQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetExpirationQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card class="mt20">
          <div class="mb8 flex-space-between">
            <div style="font-size: large">到期提醒列表</div>
          </div>
          <el-table :data="expirationList" border v-loading="expirationLoading" empty-text="暂无到期提醒">
            <el-table-column label="商品名称" prop="itemName" width="150" />
            <el-table-column label="商品编号" prop="itemCode" width="120" />
            <el-table-column label="规格名称" prop="skuName" width="120" />
            <el-table-column label="规格编号" prop="skuCode" width="120" />
            <el-table-column label="仓库" prop="warehouseName" width="120" />
            <el-table-column label="库区" prop="areaName" width="120" />
            <el-table-column label="批号" prop="batchNo" width="120" />
            <el-table-column label="生产日期" prop="productionDate" width="120">
              <template #default="{ row }">
                {{ formatDate(row.productionDate) }}
              </template>
            </el-table-column>
            <el-table-column label="过期时间" prop="expirationDate" width="120">
              <template #default="{ row }">
                <span :style="{ color: row.daysToExpire < 0 ? 'red' : row.daysToExpire <= 7 ? 'red' : row.daysToExpire <= 30 ? 'orange' : '' }">
                  {{ formatDate(row.expirationDate) }}
                  <el-tag v-if="row.daysToExpire < 0" type="danger" size="small" style="margin-left: 5px">已过期</el-tag>
                </span>
              </template>
            </el-table-column>
            <el-table-column label="剩余数量" prop="remainQuantity" width="100" align="right" />
            <el-table-column label="距离过期" prop="daysToExpire" width="120" align="right">
              <template #default="{ row }">
                <span v-if="row.daysToExpire < 0" style="color: red; font-weight: bold">
                  已过期 {{ Math.abs(row.daysToExpire) }}天
                </span>
                <span v-else :style="{ color: row.daysToExpire <= 7 ? 'red' : row.daysToExpire <= 30 ? 'orange' : '' }">
                  {{ row.daysToExpire }}天
                </span>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="expirationTotal > 0"
            :total="expirationTotal"
            v-model:page="expirationQueryParams.pageNum"
            v-model:limit="expirationQueryParams.pageSize"
            @pagination="getExpirationList"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="InventoryWarning">
import { listInventoryWarning, listExpirationReminder } from '@/api/wms/inventoryWarning'
import { useRoute } from 'vue-router'

const route = useRoute()

// 根据URL参数初始化activeTab
const activeTab = ref(route.query.tab || 'warning')
const warningLoading = ref(false)
const expirationLoading = ref(false)
const warningList = ref([])
const expirationList = ref([])
const warningTotal = ref(0)
const expirationTotal = ref(0)

const warningQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  itemName: null,
  itemCode: null
})

const expirationQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  daysBeforeExpire: 30,
  itemName: null,
  itemCode: null
})

/** 查询库存预警列表 */
function getWarningList() {
  warningLoading.value = true
  listInventoryWarning(warningQueryParams.value).then(response => {
    warningList.value = response.rows || []
    warningTotal.value = response.total || 0
    warningLoading.value = false
  }).catch(error => {
    console.error('获取库存预警列表失败:', error)
    warningList.value = []
    warningTotal.value = 0
    warningLoading.value = false
  })
}

/** 搜索按钮操作 */
function handleWarningQuery() {
  warningQueryParams.value.pageNum = 1
  getWarningList()
}

/** 重置按钮操作 */
function resetWarningQuery() {
  warningQueryParams.value = {
    pageNum: 1,
    pageSize: 10,
    itemName: null,
    itemCode: null
  }
  handleWarningQuery()
}

/** 查询到期提醒列表 */
function getExpirationList() {
  expirationLoading.value = true
  const params = {
    ...expirationQueryParams.value,
    daysBeforeExpire: expirationQueryParams.value.daysBeforeExpire || 30
  }
  listExpirationReminder(params).then(response => {
    expirationList.value = response.rows || []
    expirationTotal.value = response.total || 0
    expirationLoading.value = false
  }).catch(error => {
    console.error('获取到期提醒列表失败:', error)
    expirationList.value = []
    expirationTotal.value = 0
    expirationLoading.value = false
  })
}

/** 搜索按钮操作 */
function handleExpirationQuery() {
  expirationQueryParams.value.pageNum = 1
  getExpirationList()
}

/** 重置按钮操作 */
function resetExpirationQuery() {
  expirationQueryParams.value = {
    pageNum: 1,
    pageSize: 10,
    daysBeforeExpire: 30,
    itemName: null,
    itemCode: null
  }
  handleExpirationQuery()
}

/** Tab切换 */
function handleTabChange(tabName) {
  if (tabName === 'warning') {
    getWarningList()
  } else if (tabName === 'expiration') {
    getExpirationList()
  }
}

/** 格式化日期 */
function formatDate(date) {
  if (!date) return ''
  return date.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  // 根据URL参数加载对应tab的数据
  if (activeTab.value === 'warning') {
    getWarningList()
  } else if (activeTab.value === 'expiration') {
    getExpirationList()
  }
})

// 监听路由变化，当tab参数改变时更新activeTab并加载数据
watch(() => route.query.tab, (newTab) => {
  if (newTab && (newTab === 'warning' || newTab === 'expiration')) {
    activeTab.value = newTab
    if (newTab === 'warning') {
      getWarningList()
    } else if (newTab === 'expiration') {
      getExpirationList()
    }
  }
})
</script>

<style scoped>
.mt20 {
  margin-top: 20px;
}
.mb8 {
  margin-bottom: 8px;
}
.flex-space-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

