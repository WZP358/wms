<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="100px">
        <el-form-item label="输入条形码" prop="barcodeInput">
          <el-input
            v-model="barcodeInput"
            placeholder="出库单号或入库单号"
            clearable
            @keyup.enter="handleBarcodeScan"
            style="width: 200px"
            prefix-icon="Search"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="success" icon="Aim" @click="handleScanButtonClick">扫描条形码</el-button>
        </el-form-item>
        <el-form-item label="出库状态" prop="shipmentOrderStatus">
          <el-radio-group v-model="queryParams.shipmentOrderStatus" @change="handleQuery">
            <el-radio-button
              :key="-2"
              :label="-2"
            >
              全部
            </el-radio-button>
            <el-radio-button
              v-for="item in wms_shipment_status"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出库类型" prop="shipmentOrderType">
          <el-radio-group v-model="queryParams.shipmentOrderType" @change="handleQuery">
            <el-radio-button
              :key="-1"
              :label="-1"
            >
              全部
            </el-radio-button>
            <el-radio-button
              v-for="item in wms_shipment_type"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出库单号" prop="shipmentOrderNo">
          <el-input
            v-model="queryParams.shipmentOrderNo"
            placeholder="请输入出库单号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="订单号" prop="orderNo">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt20">

      <el-row :gutter="10" class="mb8" type="flex" justify="space-between">
        <el-col :span="6"><span style="font-size: large">出库单</span></el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['wms:shipment:all']"
          >新增</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="shipmentOrderList" border class="mt20"
                @expand-change="handleExpandExchange"
                :row-key="getRowKey"
                :expand-row-keys="expandedRowKeys"
                empty-text="暂无出库单"
                cell-class-name="vertical-top-cell"
      >
        <el-table-column type="expand">
          <template #default="props">
            <div style="padding: 0 50px 20px 50px">
              <h3>商品明细</h3>
              <el-table :data="props.row.details" v-loading="detailLoading[props.$index]" empty-text="暂无商品明细">
                <el-table-column label="商品名称">
                  <template #default="{ row }">
                    <div>{{ row?.itemSku?.item?.itemName }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="规格名称">
                  <template #default="{ row }">
                    <div>{{ row?.itemSku?.skuName }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="库区" prop="areaName"/>
                <el-table-column label="数量" prop="quantity" align="right">
                  <template #default="{ row }">
                    <el-statistic :value="Number(row.quantity)" :precision="0"/>
                  </template>
                </el-table-column>
                <el-table-column label="价格(元)" align="right">
                  <template #default="{ row }">
                    <el-statistic :precision="2" :value="row.amount? Number(row.amount):'-'"/>
                  </template>
                </el-table-column>
                <el-table-column label="批号" prop="batchNo" />
                <el-table-column label="SN码" prop="snCodes" width="200">
                  <template #default="{ row }">
                    <div v-if="row.snCodes && row.snCodes.length > 0">
                      <div v-for="(snCode, index) in row.snCodes" :key="index">{{ snCode }}</div>
                    </div>
                    <div v-else>无</div>
                  </template>
                </el-table-column>
                <el-table-column label="生产日期" prop="productionDate">
                  <template #default="{ row }">
                    <div>{{ parseTime(row.productionDate, '{y}-{m}-{d}') }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="过期日期" prop="expirationDate">
                  <template #default="{ row }">
                    <div>{{ parseTime(row.expirationDate, '{y}-{m}-{d}') }}</div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单号/订单号" align="left">
          <template #default="{ row }">
            <div>单号：{{ row.shipmentOrderNo }}</div>
            <div v-if="row.orderNo">订单号：{{ row.orderNo }}</div>
          </template>
        </el-table-column>
        <el-table-column label="出库类型" align="left" prop="shipmentOrderType">
          <template #default="{ row }">
            <dict-tag :options="wms_shipment_type" :value="row.shipmentOrderType" />
          </template>
        </el-table-column>
        <el-table-column label="客户" align="left" prop="merchantId">
          <template #default="{ row }">
            <div>{{ useWmsStore().merchantMap.get(row.merchantId)?.merchantName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="仓库/库区" align="left" width="200">
          <template #default="{ row }">
            <div>仓库：{{ useWmsStore().warehouseMap.get(row.warehouseId)?.warehouseName }}</div>
            <div v-if="row.areaId">库区：{{ useWmsStore().areaMap.get(row.areaId)?.areaName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="出库状态" align="center" prop="shipmentOrderStatus" width="120">
          <template #default="{ row }">
            <dict-tag :options="wms_shipment_status" :value="row.shipmentOrderStatus" />
          </template>
        </el-table-column>
        <el-table-column label="数量/金额(元)" align="left">
          <template #default="{ row }">
            <div class="flex-space-between">
              <span>数量：</span>
              <el-statistic :value="Number(row.totalQuantity)" :precision="0"/>
            </div>
            <div class="flex-space-between" v-if="row.receivableAmount || row.receivableAmount === 0">
              <span>金额：</span>
              <el-statistic :value="Number(row.receivableAmount)" :precision="2"/>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建/操作" align="left">
          <template #default="{ row }">
            <div>创建：{{ row.createBy }}</div>
            <div v-if="row.updateBy">操作：{{ row.updateBy }}</div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间/操作时间" align="left" width="200">
          <template #default="{ row }">
            <div>创建：{{ parseTime(row.createTime, '{mm}-{dd} {hh}:{ii}') }}</div>
            <div>操作：{{ parseTime(row.updateTime, '{mm}-{dd} {hh}:{ii}') }}</div>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" />
        <el-table-column label="操作" align="right" class-name="small-padding fixed-width" width="120">
          <template #default="scope">
            <div>
              <el-popover
                placement="left"
                title="提示"
                :width="300"
                trigger="hover"
                :disabled="scope.row.shipmentOrderStatus === 0"
                :content="'出库单【' + scope.row.shipmentOrderNo + '】已' + (scope.row.shipmentOrderStatus === 1 ? '出库' : '作废') + '，无法修改！' "
              >
                <template #reference>
                  <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['wms:shipment:all']" :disabled="[-1, 1].includes(scope.row.shipmentOrderStatus)">修改</el-button>
                </template>
              </el-popover>
              <el-button link type="primary" @click="handleGoDetail(scope.row)" v-hasPermi="['wms:shipment:all']">{{ expandedRowKeys.includes(scope.row.id) ? '收起' : '查看' }}</el-button>
            </div>
            <div class="mt10">
              <el-popover
                placement="left"
                title="提示"
                :width="300"
                trigger="hover"
                :disabled="[-1, 0].includes(scope.row.shipmentOrderStatus)"
                :content="'出库单【' + scope.row.shipmentOrderNo + '】已出库，无法删除！' "
              >
                <template #reference>
                  <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['wms:shipment:all']" :disabled="scope.row.shipmentOrderStatus === 1">删除</el-button>
                </template>
              </el-popover>
              <el-button link type="primary" @click="handlePrint(scope.row)" v-hasPermi="['wms:shipment:all']">打印</el-button>
            </div>
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
        />
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="ShipmentOrder">
import {listShipmentOrder, delShipmentOrder, getShipmentOrder} from "@/api/wms/shipmentOrder";
import {listByShipmentOrderId} from "@/api/wms/shipmentOrderDetail";
import {getCurrentInstance, reactive, ref, toRefs} from "vue";
import {useWmsStore} from "../../../../store/modules/wms";
import {ElMessageBox} from "element-plus";
import shipmentPanel from "@/components/PrintTemplate/shipment-panel";
import { generateNo } from "@/utils/ruoyi";
import { listReceiptOrder, getReceiptOrder } from "@/api/wms/receiptOrder";
import { scanSn } from "@/api/wms/sn";

const { proxy } = getCurrentInstance();
const { wms_shipment_status, wms_shipment_type} = proxy.useDict("wms_shipment_status", "wms_shipment_type");
const shipmentOrderList = ref([]);
const open = ref(false);
const buttonLoading = ref(false);
const loading = ref(true);
const ids = ref([]);
const total = ref(0);
const title = ref("");
// 当前展开集合
const expandedRowKeys = ref([])
// 商品明细table的loading状态集合
const detailLoading = ref([])
// 条形码输入
const barcodeInput = ref('')
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    shipmentOrderNo: undefined,
    shipmentOrderType: -1,
    merchantId: undefined,
    orderNo: undefined,
    receivableAmount: undefined,
    shipmentOrderStatus: -2,
  },
});

const { queryParams } = toRefs(data);

/** 查询入库单列表 */
function getList() {
  loading.value = true;
  const query = {...queryParams.value}
  if (query.shipmentOrderStatus === -2) {
    query.shipmentOrderStatus = null
  }
  if (query.shipmentOrderType === -1) {
    query.shipmentOrderType = null
  }
  listShipmentOrder(query).then(response => {
    shipmentOrderList.value = response.rows;
    total.value = response.total;
    for (let i = 0; i < total; i++) {
      detailLoading.value.push(false)
    }
    expandedRowKeys.value = []
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  proxy.$router.push({ path: "/shipmentOrderEdit" });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('确认删除出库单【' + row.shipmentOrderNo + '】吗？').then(function() {
    loading.value = true;
    return delShipmentOrder(_ids);
  }).then(() => {
    loading.value = true;
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((e) => {
    if (e === 409) {
      return ElMessageBox.alert(
        '<div>出库单【' + row.shipmentOrderNo + '】已出库，不能删除 ！</div><div>请联系管理员处理！</div>',
        '系统提示',
        {
          dangerouslyUseHTMLString: true,
        }
      )
    }
  }).finally(() => {
    loading.value = false;
  });
}

function handleUpdate(row) {
  proxy.$router.push({ path: "/shipmentOrderEdit",  query: { id: row.id } });
}

function handleGoDetail(row) {
  const index = expandedRowKeys.value.indexOf(row.id)
  if (index !== -1) {
    // 收起
    expandedRowKeys.value.splice(index, 1)
  } else {
    // 展开
    expandedRowKeys.value.push(row.id)
    loadShipmentOrderDetail(row)
  }
}

/** 导出按钮操作 */
async function handlePrint(row) {
  const res = await getShipmentOrder(row.id)
  const shipmentOrder = res.data
  let table = []
  if (shipmentOrder.details?.length) {
    table = shipmentOrder.details.map(detail => {
      // 格式化SN码显示：如果有SN码数组，用逗号分隔；否则显示"无"
      let snCodesDisplay = '无';
      if (detail.snCodes && Array.isArray(detail.snCodes) && detail.snCodes.length > 0) {
        snCodesDisplay = detail.snCodes.join(', ');
      }
      
      return {
        itemName: detail.itemSku.item.itemName,
        skuName: detail.itemSku.skuName,
        areaName: useWmsStore().areaMap.get(detail.areaId)?.areaName,
        quantity: Number(detail.quantity).toFixed(0),
        snCodes: snCodesDisplay,
        batchNo: detail.batchNo,
        productionDate: proxy.parseTime(detail.productionDate, '{y}-{m}-{d}'),
        expirationDate: proxy.parseTime(detail.expirationDate, '{y}-{m}-{d}'),
        amount: detail.amount
      }
    })
  }
  const printData = {
    shipmentOrderNo: shipmentOrder.shipmentOrderNo,
    shipmentOrderType: proxy.selectDictLabel(wms_shipment_type.value, shipmentOrder.shipmentOrderType),
    shipmentOrderStatus: proxy.selectDictLabel(wms_shipment_status.value, shipmentOrder.shipmentOrderStatus),
    merchantName: useWmsStore().merchantMap.get(shipmentOrder.merchantId)?.merchantName,
    orderNo: shipmentOrder.orderNo,
    warehouseName: useWmsStore().warehouseMap.get(shipmentOrder.warehouseId)?.warehouseName,
    areaName: useWmsStore().areaMap.get(shipmentOrder.areaId)?.areaName,
    totalQuantity: Number(shipmentOrder.totalQuantity).toFixed(0),
    receivableAmount: ((shipmentOrder.receivableAmount || shipmentOrder.receivableAmount === 0) ? (shipmentOrder.receivableAmount + '元') : ''),
    createBy: shipmentOrder.createBy,
    createTime: proxy.parseTime(shipmentOrder.createTime, '{mm}-{dd} {hh}:{ii}'),
    updateBy: shipmentOrder.updateBy,
    updateTime: proxy.parseTime(shipmentOrder.updateTime, '{mm}-{dd} {hh}:{ii}'),
    remark: shipmentOrder.remark,
    table
  }
  let printTemplate = new proxy.$hiprint.PrintTemplate({template: shipmentPanel})
  printTemplate.print(printData, {}, {
    styleHandler: () => {
      let css = '<link href="https://cyl-press.oss-cn-shenzhen.aliyuncs.com/print-lock.css" media="print" rel="stylesheet">';
      return css
    }
  })
}


function handleExpandExchange(value, expandedRows) {
  if (!ifExpand(expandedRows)) {
    return
  }
  expandedRowKeys.value = expandedRows.map(it => it.id)
  loadShipmentOrderDetail(value)
}

function loadShipmentOrderDetail(row) {
  const index = shipmentOrderList.value.findIndex(it => it.id === row.id)
  detailLoading.value[index] = true
  listByShipmentOrderId(row.id).then(res => {
    if (res.data?.length) {
      const details = res.data.map(it => {
        return {
          ...it,
          warehouseName: useWmsStore().warehouseMap.get(it.warehouseId)?.warehouseName,
          areaName: useWmsStore().areaMap.get(it.areaId)?.areaName
        }
      })
      shipmentOrderList.value[index].details = details
    }
  }).finally(() => {
    detailLoading.value[index] = false
  })
}

function ifExpand(expandedRows) {
  if (expandedRows.length < expandedRowKeys.value.length) {
    expandedRowKeys.value = expandedRows.map(it => it.id)

    return false;
  }

  return true
}

function getRowKey(row) {
  return row.id
}

/** 扫描条形码按钮点击处理 */
async function handleScanButtonClick() {
  try {
    const res = await scanSn();
    if (res.code === 200) {
      // 未连接扫描枪，弹出提示框
      ElMessageBox.alert(
        res.data || res.msg || '未连接扫描枪',
        '提示',
        {
          type: 'warning',
          confirmButtonText: '确定'
        }
      );
    } else {
      ElMessageBox.alert(
        res.msg || '扫描枪检测失败',
        '提示',
        {
          type: 'error',
          confirmButtonText: '确定'
        }
      );
    }
  } catch (error) {
    console.error('扫描枪检测失败:', error);
    ElMessageBox.alert(
      '扫描枪检测失败，请重试',
      '提示',
      {
        type: 'error',
        confirmButtonText: '确定'
      }
    );
  }
}

/** 条形码扫描处理 */
async function handleBarcodeScan() {
  if (!barcodeInput.value || !barcodeInput.value.trim()) {
    proxy.$modal.msgWarning("请输入或扫描单据条形码");
    return;
  }
  
  const scannedCode = barcodeInput.value.trim();
  loading.value = true;
  
  try {
    // 判断是出库单号还是入库单号
    // 出库单号以CK开头，入库单号以RK开头
    const isShipmentOrder = scannedCode.startsWith('CK');
    const isReceiptOrder = scannedCode.startsWith('RK');
    
    if (isShipmentOrder) {
      // 查询出库单
      await handleShipmentOrderScan(scannedCode);
    } else if (isReceiptOrder) {
      // 查询入库单，用于转出库
      await handleReceiptOrderScanForShipment(scannedCode);
    } else {
      // 尝试同时查询两种单据
      const shipmentResponse = await listShipmentOrder({
        shipmentOrderNo: scannedCode,
        pageNum: 1,
        pageSize: 10
      });
      
      if (shipmentResponse.rows && shipmentResponse.rows.length > 0) {
        await handleShipmentOrderScan(scannedCode);
      } else {
        const receiptResponse = await listReceiptOrder({
          receiptOrderNo: scannedCode,
          pageNum: 1,
          pageSize: 10
        });
        
        if (receiptResponse.rows && receiptResponse.rows.length > 0) {
          await handleReceiptOrderScanForShipment(scannedCode);
        } else {
          proxy.$modal.msgError(`未找到单号为【${scannedCode}】的单据！`);
          barcodeInput.value = '';
          loading.value = false;
        }
      }
    }
  } catch (error) {
    console.error('查询单据失败:', error);
    proxy.$modal.msgError("查询单据失败，请重试！");
    barcodeInput.value = '';
    loading.value = false;
  }
}

/** 处理出库单扫描 */
async function handleShipmentOrderScan(scannedCode) {
  const response = await listShipmentOrder({
    shipmentOrderNo: scannedCode,
    pageNum: 1,
    pageSize: 10
  });
  
  if (response.rows && response.rows.length > 0) {
    const order = response.rows[0];
    barcodeInput.value = '';
    loading.value = false;
    
    ElMessageBox.confirm(
      `找到出库单【${order.shipmentOrderNo}】，请选择操作：`,
      '扫描成功',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '一键转入库',
        cancelButtonText: '查看出库单',
        type: 'info',
      }
    ).then(async () => {
      await handleConvertToReceipt(order);
    }).catch((action) => {
      if (action === 'cancel') {
        handleViewShipmentOrder(order);
      }
    });
  } else {
    proxy.$modal.msgError(`未找到出库单号为【${scannedCode}】的出库单！`);
    barcodeInput.value = '';
    loading.value = false;
  }
}

/** 处理入库单扫描（用于转出库） */
async function handleReceiptOrderScanForShipment(scannedCode) {
  const response = await listReceiptOrder({
    receiptOrderNo: scannedCode,
    pageNum: 1,
    pageSize: 10
  });
  
  if (response.rows && response.rows.length > 0) {
    const order = response.rows[0];
    barcodeInput.value = '';
    loading.value = false;
    
    ElMessageBox.confirm(
      `找到入库单【${order.receiptOrderNo}】，是否一键转出库？`,
      '扫描成功',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '一键转出库',
        cancelButtonText: '取消',
        type: 'info',
      }
    ).then(async () => {
      await handleConvertReceiptToShipment(order);
    }).catch(() => {
      // 用户取消
    });
  } else {
    proxy.$modal.msgError(`未找到入库单号为【${scannedCode}】的入库单！`);
    barcodeInput.value = '';
    loading.value = false;
  }
}

/** 入库单转出库 */
async function handleConvertReceiptToShipment(receiptOrder) {
  try {
    loading.value = true;
    
    if (receiptOrder.receiptOrderStatus !== 1) {
      proxy.$modal.msgWarning(`入库单【${receiptOrder.receiptOrderNo}】未入库，无法转出库！`);
      loading.value = false;
      return;
    }
    
    const res = await getReceiptOrder(receiptOrder.id);
    const fullReceiptOrder = res.data;
    
    if (!fullReceiptOrder.details || fullReceiptOrder.details.length === 0) {
      proxy.$modal.msgError("入库单没有商品明细，无法转出库！");
      loading.value = false;
      return;
    }
    
    const shipmentOrderNo = 'CK' + generateNo();
    
    const shipmentData = {
      shipmentOrderNo: shipmentOrderNo,
      shipmentOrderType: 22,
      warehouseId: fullReceiptOrder.warehouseId,
      areaId: fullReceiptOrder.areaId,
      merchantId: fullReceiptOrder.merchantId,
      orderNo: fullReceiptOrder.receiptOrderNo,
      remark: `根据入库单【${fullReceiptOrder.receiptOrderNo}】创建`,
      details: fullReceiptOrder.details.map(detail => ({
        itemSkuId: detail.itemSku.id,
        itemSku: detail.itemSku,
        areaId: detail.areaId,
        quantity: detail.quantity,
        amount: detail.amount,
        batchNo: detail.batchNo,
        productionDate: detail.productionDate,
        expirationDate: detail.expirationDate,
      }))
    };
    
    loading.value = false;
    proxy.$modal.msgSuccess("正在跳转到出库单编辑页面...");
    
    localStorage.setItem('convertShipmentData', JSON.stringify(shipmentData));
    
    setTimeout(() => {
      proxy.$router.push({ 
        path: "/shipmentOrderEdit",
        query: { fromConvert: 'receipt' }
      });
    }, 500);
    
  } catch (error) {
    console.error('转出库失败:', error);
    proxy.$modal.msgError("转出库失败，请重试！");
    loading.value = false;
  }
}

/** 查看出库单 */
function handleViewShipmentOrder(order) {
  if (order.shipmentOrderStatus === 1) {
    proxy.$modal.msgWarning(`出库单【${order.shipmentOrderNo}】已出库，无法编辑！`);
    queryParams.value.shipmentOrderNo = order.shipmentOrderNo;
    handleQuery();
  } else if (order.shipmentOrderStatus === -1) {
    proxy.$modal.msgWarning(`出库单【${order.shipmentOrderNo}】已作废，无法编辑！`);
    queryParams.value.shipmentOrderNo = order.shipmentOrderNo;
    handleQuery();
  } else {
    proxy.$modal.msgSuccess(`正在跳转到出库单编辑页面...`);
    setTimeout(() => {
      proxy.$router.push({ path: "/shipmentOrderEdit", query: { id: order.id } });
    }, 500);
  }
}

/** 一键转入库 - 根据出库单创建入库单 */
async function handleConvertToReceipt(shipmentOrder) {
  try {
    loading.value = true;
    
    // 检查出库单状态
    if (shipmentOrder.shipmentOrderStatus !== 1) {
      proxy.$modal.msgWarning(`出库单【${shipmentOrder.shipmentOrderNo}】未出库，无法转入库！`);
      loading.value = false;
      return;
    }
    
    // 获取出库单详细信息
    const res = await getShipmentOrder(shipmentOrder.id);
    const fullShipmentOrder = res.data;
    
    if (!fullShipmentOrder.details || fullShipmentOrder.details.length === 0) {
      proxy.$modal.msgError("出库单没有商品明细，无法转入库！");
      loading.value = false;
      return;
    }
    
    // 生成入库单号
    const receiptOrderNo = 'RK' + generateNo();
    
    // 构建入库单数据并跳转到入库单编辑页面
    const receiptData = {
      receiptOrderNo: receiptOrderNo,
      receiptOrderType: 11, // 退货入库
      warehouseId: fullShipmentOrder.warehouseId,
      areaId: fullShipmentOrder.areaId,
      merchantId: fullShipmentOrder.merchantId,
      orderNo: fullShipmentOrder.shipmentOrderNo, // 关联原出库单号
      remark: `根据出库单【${fullShipmentOrder.shipmentOrderNo}】创建`,
      details: fullShipmentOrder.details.map(detail => ({
        itemSkuId: detail.itemSku.id,
        itemSku: detail.itemSku,
        areaId: detail.areaId,
        quantity: detail.quantity,
        amount: detail.amount,
        batchNo: detail.batchNo,
        productionDate: detail.productionDate,
        expirationDate: detail.expirationDate,
      }))
    };
    
    loading.value = false;
    proxy.$modal.msgSuccess("正在跳转到入库单编辑页面...");
    
    // 使用localStorage传递数据
    localStorage.setItem('convertReceiptData', JSON.stringify(receiptData));
    
    setTimeout(() => {
      proxy.$router.push({ 
        path: "/receiptOrderEdit",
        query: { fromConvert: 'shipment' }
      });
    }, 500);
    
  } catch (error) {
    console.error('转入库失败:', error);
    proxy.$modal.msgError("转入库失败，请重试！");
    loading.value = false;
  }
}

getList();
</script>
<style lang="scss">
.el-statistic__content {
  font-size: 14px;
}
.el-table .vertical-top-cell {
  vertical-align: top
}
</style>
