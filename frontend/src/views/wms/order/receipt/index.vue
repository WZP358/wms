<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="100px">
        <el-form-item label="输入条形码" prop="barcodeInput">
          <el-input
            v-model="barcodeInput"
            placeholder="入库单号或出库单号"
            clearable
            @keyup.enter="handleBarcodeScan"
            style="width: 200px"
            prefix-icon="Search"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="success" icon="Aim" @click="handleScanButtonClick">扫描条形码</el-button>
        </el-form-item>
        <el-form-item label="入库状态" prop="receiptOrderStatus">
          <el-radio-group v-model="queryParams.receiptOrderStatus" @change="handleQuery">
            <el-radio-button
              :key="-2"
              :label="-2"
            >
              全部
            </el-radio-button>
            <el-radio-button
              v-for="item in wms_receipt_status"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="入库类型" prop="receiptOrderType">
          <el-radio-group v-model="queryParams.receiptOrderType" @change="handleQuery">
            <el-radio-button
              :key="-1"
              :label="-1"
            >
              全部
            </el-radio-button>
            <el-radio-button
              v-for="item in wms_receipt_type"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="入库单号" prop="receiptOrderNo">
          <el-input
            v-model="queryParams.receiptOrderNo"
            placeholder="请输入入库单号"
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
        <el-col :span="6"><span style="font-size: large">入库单</span></el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['wms:receipt:all']"
          >新增</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="receiptOrderList" border class="mt20"
                @expand-change="handleExpandExchange"
                :row-key="getRowKey"
                :expand-row-keys="expandedRowKeys"
                empty-text="暂无入库单"
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
            <div>单号：{{ row.receiptOrderNo }}</div>
            <div v-if="row.orderNo">订单号：{{ row.orderNo }}</div>
          </template>
        </el-table-column>
        <el-table-column label="入库类型" align="left" prop="receiptOrderType">
          <template #default="{ row }">
            <dict-tag :options="wms_receipt_type" :value="row.receiptOrderType" />
          </template>
        </el-table-column>
        <el-table-column label="供应商" align="left" prop="merchantId">
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
        <el-table-column label="入库状态" align="center" prop="receiptOrderStatus" width="120">
          <template #default="{ row }">
            <dict-tag :options="wms_receipt_status" :value="row.receiptOrderStatus" />
          </template>
        </el-table-column>
        <el-table-column label="数量/金额(元)" align="left">
          <template #default="{ row }">
            <div class="flex-space-between">
              <span>数量：</span>
              <el-statistic :value="Number(row.totalQuantity)" :precision="0"/>
            </div>
            <div class="flex-space-between" v-if="row.payableAmount || row.payableAmount === 0">
              <span>金额：</span>
              <el-statistic :value="Number(row.payableAmount)" :precision="2"/>
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
                :disabled="scope.row.receiptOrderStatus === 0"
                :content="'入库单【' + scope.row.receiptOrderNo + '】已' + (scope.row.receiptOrderStatus === 1 ? '入库' : '作废') + '，无法修改！' "
              >
                <template #reference>
                  <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['wms:receipt:all']" :disabled="[-1, 1].includes(scope.row.receiptOrderStatus)">修改</el-button>
                </template>
              </el-popover>
              <el-button link type="primary" @click="handleGoDetail(scope.row)" v-hasPermi="['wms:receipt:all']">{{ expandedRowKeys.includes(scope.row.id) ? '收起' : '查看' }}</el-button>
            </div>
            <div class="mt10">
              <el-popover
                placement="left"
                title="提示"
                :width="300"
                trigger="hover"
                :disabled="[-1, 0].includes(scope.row.receiptOrderStatus)"
                :content="'入库单【' + scope.row.receiptOrderNo + '】已入库，无法删除！' "
              >
                <template #reference>
                  <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['wms:receipt:all']" :disabled="scope.row.receiptOrderStatus === 1">删除</el-button>
                </template>
              </el-popover>
              <el-button link type="primary" @click="handlePrint(scope.row)" v-hasPermi="['wms:receipt:all']">打印</el-button>
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

<script setup name="ReceiptOrder">
import { listReceiptOrder, getReceiptOrder, delReceiptOrder, addReceiptOrder, updateReceiptOrder } from "@/api/wms/receiptOrder";
import {getCurrentInstance, reactive, ref, toRefs} from "vue";
import {useWmsStore} from "../../../../store/modules/wms";
import {listByReceiptOrderId} from "@/api/wms/receiptOrderDetail";
import {ElMessageBox} from "element-plus";
import receiptPanel from "@/components/PrintTemplate/receipt-panel";
import { generateNo } from "@/utils/ruoyi";
import { listShipmentOrder, getShipmentOrder } from "@/api/wms/shipmentOrder";

const { proxy } = getCurrentInstance();
const { wms_receipt_status, wms_receipt_type } = proxy.useDict("wms_receipt_status", "wms_receipt_type");
const receiptOrderList = ref([]);
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
    receiptOrderNo: undefined,
    receiptOrderType: -1,
    merchantId: undefined,
    orderNo: undefined,
    payableAmount: undefined,
    receiptOrderStatus: -2,
  },
});

const { queryParams } = toRefs(data);

/** 查询入库单列表 */
function getList() {
  loading.value = true;
  const query = {...queryParams.value}
  if (query.receiptOrderStatus === -2) {
    query.receiptOrderStatus = null
  }
  if (query.receiptOrderType === -1) {
    query.receiptOrderType = null
  }
  listReceiptOrder(query).then(response => {
    receiptOrderList.value = response.rows;
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
  proxy.$router.push({ path: "/receiptOrderEdit" });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('确认删除入库单【' + row.receiptOrderNo + '】吗？').then(function() {
    loading.value = true;
    return delReceiptOrder(_ids);
  }).then(() => {
    loading.value = true;
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((e) => {
    if (e === 409) {
      return ElMessageBox.alert(
        '<div>入库单【' + row.receiptOrderNo + '】已入库，不能删除 ！</div><div>请联系管理员处理！</div>',
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
  proxy.$router.push({ path: "/receiptOrderEdit",  query: { id: row.id } });
}

function handleGoDetail(row) {
  const index = expandedRowKeys.value.indexOf(row.id)
  if (index !== -1) {
    // 收起
    expandedRowKeys.value.splice(index, 1)
  } else {
    // 展开
    expandedRowKeys.value.push(row.id)
    loadReceiptOrderDetail(row)
  }
}

/** 导出按钮操作 */
async function handlePrint(row) {
  const res = await getReceiptOrder(row.id)
  const receiptOrder = res.data
  let table = []
  if (receiptOrder.details?.length) {
    table = receiptOrder.details.map(detail => {
      // 格式化SN码显示：如果有SN码数组，用逗号分隔；否则显示"无"
      let snCodesDisplay = '无';
      if (detail.snCodes && Array.isArray(detail.snCodes) && detail.snCodes.length > 0) {
        snCodesDisplay = detail.snCodes.join(', ');
      }
      
      return {
        itemName: detail.itemSku.item.itemName,
        skuName: detail.itemSku.skuName,
        areaName: useWmsStore().areaMap.get(detail.areaId)?.areaName,
        batchNo: detail.batchNo,
        productionDate: proxy.parseTime(detail.productionDate, '{y}-{m}-{d}'),
        expirationDate: proxy.parseTime(detail.expirationDate, '{y}-{m}-{d}'),
        quantity: Number(detail.quantity).toFixed(0),
        snCodes: snCodesDisplay,
        amount: detail.amount
      }
    })
  }
  const printData = {
    receiptOrderNo: receiptOrder.receiptOrderNo,
    receiptOrderType: proxy.selectDictLabel(wms_receipt_type.value, receiptOrder.receiptOrderType),
    receiptOrderStatus: proxy.selectDictLabel(wms_receipt_status.value, receiptOrder.receiptOrderStatus),
    merchantName: useWmsStore().merchantMap.get(receiptOrder.merchantId)?.merchantName,
    orderNo: receiptOrder.orderNo,
    warehouseName: useWmsStore().warehouseMap.get(receiptOrder.warehouseId)?.warehouseName,
    areaName: useWmsStore().areaMap.get(receiptOrder.areaId)?.areaName,
    totalQuantity: Number(receiptOrder.totalQuantity).toFixed(0),
    payableAmount: ((receiptOrder.payableAmount || receiptOrder.payableAmount === 0) ? (receiptOrder.payableAmount + '元') : ''),
    createBy: receiptOrder.createBy,
    createTime: proxy.parseTime(receiptOrder.createTime, '{mm}-{dd} {hh}:{ii}'),
    updateBy: receiptOrder.updateBy,
    updateTime: proxy.parseTime(receiptOrder.updateTime, '{mm}-{dd} {hh}:{ii}'),
    remark: receiptOrder.remark,
    table
  }
  let printTemplate = new proxy.$hiprint.PrintTemplate({template: receiptPanel})
  printTemplate.print(printData, {}, {
    styleHandler: () => {
      let css = '<link href="https://cyl-press.oss-cn-shenzhen.aliyuncs.com/print-lock.css" media="print" rel="stylesheet">';
      console.info("css:", css)
      return css
    }
  })
}


function handleExpandExchange(value, expandedRows) {
  if (!ifExpand(expandedRows)) {
    return
  }
  expandedRowKeys.value = expandedRows.map(it => it.id)
  loadReceiptOrderDetail(value)
}

function loadReceiptOrderDetail(row) {
  const index = receiptOrderList.value.findIndex(it => it.id === row.id)
  detailLoading.value[index] = true
  listByReceiptOrderId(row.id).then(res => {
    if (res.data?.length) {
      const details = res.data.map(it => {
        return {
          ...it,
          warehouseName: useWmsStore().warehouseMap.get(it.warehouseId)?.warehouseName,
          areaName: useWmsStore().areaMap.get(it.areaId)?.areaName
        }
      })
      receiptOrderList.value[index].details = details
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
function handleScanButtonClick() {
  proxy.$modal.msgInfo("未连接扫描枪");
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
    // 判断是入库单号还是出库单号
    // 入库单号以RK开头，出库单号以CK开头
    const isReceiptOrder = scannedCode.startsWith('RK');
    const isShipmentOrder = scannedCode.startsWith('CK');
    
    if (isReceiptOrder) {
      // 查询入库单
      await handleReceiptOrderScan(scannedCode);
    } else if (isShipmentOrder) {
      // 查询出库单，用于转入库
      await handleShipmentOrderScanForReceipt(scannedCode);
    } else {
      // 尝试同时查询两种单据
      const receiptResponse = await listReceiptOrder({
        receiptOrderNo: scannedCode,
        pageNum: 1,
        pageSize: 10
      });
      
      if (receiptResponse.rows && receiptResponse.rows.length > 0) {
        await handleReceiptOrderScan(scannedCode);
      } else {
        const shipmentResponse = await listShipmentOrder({
          shipmentOrderNo: scannedCode,
          pageNum: 1,
          pageSize: 10
        });
        
        if (shipmentResponse.rows && shipmentResponse.rows.length > 0) {
          await handleShipmentOrderScanForReceipt(scannedCode);
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

/** 处理入库单扫描 */
async function handleReceiptOrderScan(scannedCode) {
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
      `找到入库单【${order.receiptOrderNo}】，请选择操作：`,
      '扫描成功',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '一键转出库',
        cancelButtonText: '查看入库单',
        type: 'info',
      }
    ).then(async () => {
      await handleConvertToShipment(order);
    }).catch((action) => {
      if (action === 'cancel') {
        handleViewReceiptOrder(order);
      }
    });
  } else {
    proxy.$modal.msgError(`未找到入库单号为【${scannedCode}】的入库单！`);
    barcodeInput.value = '';
    loading.value = false;
  }
}

/** 处理出库单扫描（用于转入库） */
async function handleShipmentOrderScanForReceipt(scannedCode) {
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
      `找到出库单【${order.shipmentOrderNo}】，是否一键转入库？`,
      '扫描成功',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '一键转入库',
        cancelButtonText: '取消',
        type: 'info',
      }
    ).then(async () => {
      await handleConvertShipmentToReceipt(order);
    }).catch(() => {
      // 用户取消
    });
  } else {
    proxy.$modal.msgError(`未找到出库单号为【${scannedCode}】的出库单！`);
    barcodeInput.value = '';
    loading.value = false;
  }
}

/** 出库单转入库 */
async function handleConvertShipmentToReceipt(shipmentOrder) {
  try {
    loading.value = true;
    
    if (shipmentOrder.shipmentOrderStatus !== 1) {
      proxy.$modal.msgWarning(`出库单【${shipmentOrder.shipmentOrderNo}】未出库，无法转入库！`);
      loading.value = false;
      return;
    }
    
    const res = await getShipmentOrder(shipmentOrder.id);
    const fullShipmentOrder = res.data;
    
    if (!fullShipmentOrder.details || fullShipmentOrder.details.length === 0) {
      proxy.$modal.msgError("出库单没有商品明细，无法转入库！");
      loading.value = false;
      return;
    }
    
    const receiptOrderNo = 'RK' + generateNo();
    
    const receiptData = {
      receiptOrderNo: receiptOrderNo,
      receiptOrderType: 11,
      warehouseId: fullShipmentOrder.warehouseId,
      areaId: fullShipmentOrder.areaId,
      merchantId: fullShipmentOrder.merchantId,
      orderNo: fullShipmentOrder.shipmentOrderNo,
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

/** 查看入库单 */
function handleViewReceiptOrder(order) {
  if (order.receiptOrderStatus === 1) {
    proxy.$modal.msgWarning(`入库单【${order.receiptOrderNo}】已入库，无法编辑！`);
    queryParams.value.receiptOrderNo = order.receiptOrderNo;
    handleQuery();
  } else if (order.receiptOrderStatus === -1) {
    proxy.$modal.msgWarning(`入库单【${order.receiptOrderNo}】已作废，无法编辑！`);
    queryParams.value.receiptOrderNo = order.receiptOrderNo;
    handleQuery();
  } else {
    proxy.$modal.msgSuccess(`正在跳转到入库单编辑页面...`);
    setTimeout(() => {
      proxy.$router.push({ path: "/receiptOrderEdit", query: { id: order.id } });
    }, 500);
  }
}

/** 一键转出库 - 根据入库单创建出库单 */
async function handleConvertToShipment(receiptOrder) {
  try {
    loading.value = true;
    
    // 检查入库单状态
    if (receiptOrder.receiptOrderStatus !== 1) {
      proxy.$modal.msgWarning(`入库单【${receiptOrder.receiptOrderNo}】未入库，无法转出库！`);
      loading.value = false;
      return;
    }
    
    // 获取入库单详细信息
    const res = await getReceiptOrder(receiptOrder.id);
    const fullReceiptOrder = res.data;
    
    if (!fullReceiptOrder.details || fullReceiptOrder.details.length === 0) {
      proxy.$modal.msgError("入库单没有商品明细，无法转出库！");
      loading.value = false;
      return;
    }
    
    // 生成出库单号
    const shipmentOrderNo = 'CK' + generateNo();
    
    // 构建出库单数据并跳转到出库单编辑页面
    const shipmentData = {
      shipmentOrderNo: shipmentOrderNo,
      shipmentOrderType: 22, // 销售出库
      warehouseId: fullReceiptOrder.warehouseId,
      areaId: fullReceiptOrder.areaId,
      merchantId: fullReceiptOrder.merchantId,
      orderNo: fullReceiptOrder.receiptOrderNo, // 关联原入库单号
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
    
    // 使用localStorage传递数据
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
