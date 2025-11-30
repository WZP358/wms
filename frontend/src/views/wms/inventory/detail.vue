<template>
  <div class="app-container">
    <el-card>

      <el-form :model="queryParams" :inline="true" label-width="90px" ref="queryRef">
        <el-form-item label="维度 " prop="itemId" class="col4">
          <el-radio-group v-model="queryType" @change="handleSortTypeChange">
            <el-radio-button label="warehouse">仓库库区</el-radio-button>
            <el-radio-button label="item">商品</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId" class="col4">
          <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" @change="handleChangeWarehouse"
                     filterable clearable style="width:100%;">
            <el-option v-for="item in useWmsStore().warehouseList" :key="item.id" :label="item.warehouseName"
                       :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="库区" prop="areaId" class="col4">
          <el-select v-model="queryParams.areaId" placeholder="请选择库区" :disabled="!queryParams.warehouseId || queryParams.type == 1" clearable
                     filterable style="width:100%;" @change="handleChangeArea">
            <el-option v-for="item in useWmsStore().areaList.filter(it => it.warehouseId === queryParams.warehouseId)"
                       :key="item.id" :label="item.areaName" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item class="col4"  label="商品名称" prop="itemName">
          <el-input v-model="queryParams.itemName" clearable placeholder="商品名称"></el-input>
        </el-form-item>
        <el-form-item class="col4"  label="商品编号" prop="itemCode">
          <el-input class="w200" v-model="queryParams.itemCode" clearable placeholder="商品编号"></el-input>
        </el-form-item>
        <el-form-item class="col4"  label="规格名称" prop="skuName">
          <el-input class="w200" v-model="queryParams.skuName" clearable placeholder="规格名称"></el-input>
        </el-form-item>
        <el-form-item class="col4"  label="规格编号" prop="skuCode">
          <el-input class="w200" v-model="queryParams.skuCode" clearable placeholder="规格编号"></el-input>
        </el-form-item>
        <el-form-item class="col4"  label="批号" prop="batchNo">
          <el-input class="w200" v-model="queryParams.batchNo" clearable placeholder="批号"></el-input>
        </el-form-item>
        <el-form-item class="col4"  label="过期" prop="daysToExpires">
          <el-select v-model="queryParams.daysToExpires" clearable style="width:100%;">
            <el-option label="30天内" :value="30">30天内</el-option>
            <el-option label="60天内" :value="60">60天内</el-option>
            <el-option label="90天内" :value="90">90天内</el-option>
            <el-option label="120天内" :value="120">120天内</el-option>
            <el-option label="180天内" :value="180">180天内</el-option>
            <el-option label="365天内" :value="365">365天内</el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="col4"  label="入库日期" prop="createTimeRange">
          <el-date-picker
            v-model="queryParams.createTimeRange"
            type="daterange"
            range-separator="至"
            value-format="YYYY-MM-DD HH:mm:ss"
            format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item class="col4" style="margin-left: 32px">
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="mt20">
    <div class="mb8 flex-space-between">
      <div style="font-size: large">库存明细</div>
      <el-button
        type="primary"
        plain
        icon="Download"
        @click="handleExport"
        v-hasPermi="['wms:inventoryDetail:all']"
      >导出Excel</el-button>
    </div>
    <el-table :data="inventoryDetailList" border :span-method="spanMethod"
              cell-class-name="vertical-top-cell" v-loading="loading" empty-text="暂无库存">
      <template v-if="queryType === 'warehouse'">
        <el-table-column label="仓库" prop="warehouseId">
          <template #default="{ row }">
            <div>{{ useWmsStore().warehouseMap.get(row.warehouseId)?.warehouseName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="库区" prop="areaId">
          <template #default="{ row }">
            <div>{{ useWmsStore().areaMap.get(row.areaId)?.areaName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品信息" prop="areaIdAndItemId">
          <template #default="{ row }">
            <div>{{ row.item.itemName }}</div>
            <div v-if="row.item.itemCode">商品编号：{{ row.item.itemCode }}</div>
          </template>
        </el-table-column>
        <el-table-column label="规格信息" prop="areaIdAndSkuId">
          <template #default="{ row }">
            <div>{{ row.itemSku.skuName }}</div>
            <div v-if="row.itemSku.skuCode">规格编号：{{ row.itemSku.skuCode }}</div>
          </template>
        </el-table-column>
      </template>
      <template v-else>
        <el-table-column label="商品信息" prop="itemId">
          <template #default="{ row }">
            <div>{{ row.item.itemName }}</div>
            <div v-if="row.item.itemCode">商品编号：{{ row.item.itemCode }}</div>
          </template>
        </el-table-column>
        <el-table-column label="规格信息" prop="skuId">
          <template #default="{ row }">
            <div>{{ row.itemSku.skuName }}</div>
            <div v-if="row.itemSku.skuCode">规格编号：{{ row.itemSku.skuCode }}</div>
          </template>
        </el-table-column>
        <el-table-column label="仓库" prop="skuIdAndWarehouseId">
          <template #default="{row}">
            <div>{{ useWmsStore().warehouseMap.get(row.warehouseId)?.warehouseName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="所属库区" prop="skuIdAndAreaId">
          <template #default="{ row }">
            <div>{{ useWmsStore().areaMap.get(row.areaId)?.areaName }}</div>
          </template>
        </el-table-column>
      </template>
      <el-table-column label="入库日期" align="left" prop="createTime" width="140">
        <template #default="{ row }">
          <div>{{ parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') }}</div>
        </template>
      </el-table-column>
      <el-table-column label="库存" prop="remainQuantity" align="right">
        <template #default="{ row }">
          <el-statistic :value="Number(row.remainQuantity)" :precision="0"/>
        </template>
      </el-table-column>
      <el-table-column label="批号" align="left" prop="batchNo"/>
      <el-table-column label="生产日期/过期日期" align="left" width="180">
        <template #default="{ row }">
          <div v-if="row.productionDate">生产日期：{{ parseTime(row.productionDate, '{y}-{m}-{d}') }}</div>
          <div v-if="row.expirationDate">过期日期：{{ parseTime(row.expirationDate, '{y}-{m}-{d}') }}</div>
        </template>
      </el-table-column>
    </el-table>
      <el-row>
        <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
          class="mr10"
        />
      </el-row>
  </el-card>
  </div>
</template>

<script setup name="InventoryDetail">
import {useWmsStore} from "@/store/modules/wms";
import {computed, getCurrentInstance, onMounted, reactive, ref} from "vue";
import {listInventoryDetail} from "@/api/wms/inventoryDetail";
import {getRowspanMethod} from "@/utils/getRowSpanMethod";
import {ElForm} from "element-plus";
import {parseTime} from "../../../utils/ruoyi";

const {proxy} = getCurrentInstance();
const spanMethod = computed(() => getRowspanMethod(inventoryDetailList.value, rowSpanArray.value))
const queryType = ref("warehouse");
const inventoryDetailList = ref([]);
const loading = ref(false);
const rowSpanArray = ref(['warehouseId', 'areaId', 'areaIdAndItemId', 'areaIdAndSkuId'])
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  itemName: '',
  itemCode: '',
  skuName: '',
  skuCode: '',
  areaId: undefined,
  warehouseId: undefined,
  warehouseName: undefined,
  areaName: undefined,
  batchNo: '',
  daysToExpires: undefined
});
const total = ref(0);

const handleChangeWarehouse = () => {
  queryParams.value.areaId = undefined
  queryParams.value.areaName = undefined
  // 根据选中的仓库ID获取仓库名称
  if (queryParams.value.warehouseId) {
    const warehouse = useWmsStore().warehouseList.find(w => w.id === queryParams.value.warehouseId)
    queryParams.value.warehouseName = warehouse ? warehouse.warehouseName : undefined
  } else {
    queryParams.value.warehouseName = undefined
  }
}

const handleChangeArea = () => {
  // 根据选中的库区ID获取库区名称
  if (queryParams.value.areaId) {
    const area = useWmsStore().areaList.find(a => a.id === queryParams.value.areaId)
    queryParams.value.areaName = area ? area.areaName : undefined
  } else {
    queryParams.value.areaName = undefined
  }
}

onMounted(() => {
  getList();
})

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryType.value = "warehouse"
  proxy.resetForm("queryRef");
  handleQuery();
}

const handleSortTypeChange = (e) => {
  if (e === "warehouse") {
    queryParams.value.areaId = undefined
    queryParams.value.areaName = undefined
    rowSpanArray.value = ['warehouseId', 'areaId', 'areaIdAndItemId', 'areaIdAndSkuId']
  } else if (e === "item") {
    rowSpanArray.value = ['itemId', 'skuId','skuIdAndWarehouseId','skuIdAndAreaId']
  }
  queryParams.value.pageNum = 1;
  getList()
}

const getList = () => {
  const query = {...queryParams.value}
  if (query.createTimeRange) {
    query.createStartTime = query.createTimeRange[0]
    query.createEndTime = query.createTimeRange[1]
  }
  // 清理 undefined 和空字符串值，但保留数字 0
  Object.keys(query).forEach(key => {
    const value = query[key]
    if (value === undefined || value === null || (typeof value === 'string' && value === '')) {
      delete query[key]
    }
  })
  // 删除 createTimeRange，因为已经转换为 createStartTime 和 createEndTime
  delete query.createTimeRange
  // 如果使用名称查询，则清除ID（避免冲突）
  if (query.warehouseName) {
    delete query.warehouseId
  }
  if (query.areaName) {
    delete query.areaId
  }
  // 调试：打印查询参数
  console.log('查询参数:', JSON.stringify(query, null, 2))
  loading.value = true
  listInventoryDetail(query).then((res) => {
    console.log('查询结果:', res)
    console.log('返回数据行数:', res.rows?.length || 0)
    console.log('总记录数:', res.total || 0)
    if (res.rows && res.rows.length > 0) {
      console.log('第一条数据:', res.rows[0])
    }
    inventoryDetailList.value = res.rows;
    total.value = res.total;
    inventoryDetailList.value.forEach(it => {
      if (queryType.value == "warehouse") {
        it.areaIdAndItemId = it.areaId + '-' + it.item.id
        it.areaIdAndSkuId = it.areaId + '-' + it.itemSku.id
      } else if (queryType.value == "item") {
        it.itemId = it.item.id
        it.skuIdAndWarehouseId= it.itemSku.id + '-' + it.warehouseId
        it.skuIdAndAreaId= it.itemSku.id + '-' + it.areaId
      }
    })
  }).catch(err => {
    console.error('查询失败:', err)
  }).finally(() => loading.value = false);
}

/** 导出按钮操作 */
function handleExport() {
  const query = {...queryParams.value}
  if (query.createTimeRange) {
    query.createStartTime = query.createTimeRange[0]
    query.createEndTime = query.createTimeRange[1]
  }
  // 删除分页参数
  delete query.pageNum
  delete query.pageSize
  delete query.createTimeRange
  proxy.download('wms/inventoryDetail/export', query, `仓库库区商品数据_${new Date().getTime()}.xlsx`)
}
</script>
<style scoped lang="scss">

</style>
