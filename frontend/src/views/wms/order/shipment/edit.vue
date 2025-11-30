<template>
  <div>
    <div class="receipt-order-edit-wrapper app-container" style="margin-bottom: 60px" v-loading="loading">
      <el-card header="出库单基本信息">
        <el-form label-width="108px" :model="form" ref="shipmentForm" :rules="rules">
          <el-row :gutter="24">
            <el-col :span="11">
              <el-form-item label="出库单号" prop="shipmentOrderNo">
                <el-input class="w200" v-model="form.shipmentOrderNo" placeholder="出库单号"
                          :disabled="form.id"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="仓库" prop="warehouseId">
                <el-select v-model="form.warehouseId" placeholder="请选择仓库" @change="handleChangeWarehouse"
                           filterable>
                  <el-option v-for="item in useWmsStore().warehouseList" :key="item.id" :label="item.warehouseName"
                             :value="item.id"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="库区" prop="areaId">
                <el-select v-model="form.areaId" placeholder="请选择库区" :disabled="!form.warehouseId" clearable
                           filterable @change="handleChangeArea" style="width: 100%!important;">
                  <el-option v-for="item in useWmsStore().areaList.filter(it => it.warehouseId === form.warehouseId)"
                             :key="item.id" :label="item.areaName" :value="item.id"/>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="11">
              <el-form-item label="出库类型" prop="shipmentOrderType">
                <el-radio-group v-model="form.shipmentOrderType">
                  <el-radio-button
                    v-for="item in wms_shipment_type"
                    :key="item.value"
                    :label="item.value"
                  >{{ item.label }}
                  </el-radio-button
                  >
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="客户" prop="merchantId">
                <el-select v-model="form.merchantId" placeholder="请选择客户" clearable filterable>
                  <el-option v-for="item in useWmsStore().merchantList" :key="item.id" :label="item.merchantName"
                             :value="item.id"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="订单号" prop="orderNo">
                <el-input v-model="form.orderNo" placeholder="请输入订单号"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="11">
              <el-form-item label="备注" prop="remark">
                <el-input
                  v-model="form.remark"
                  placeholder="备注...100个字符以内"
                  rows="4"
                  maxlength="100"
                  type="textarea"
                  show-word-limit="show-word-limit"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <div style="display: flex;align-items: start">
                <el-form-item label="金额" prop="receivableAmount">
                  <el-input-number v-model="form.receivableAmount" :precision="2" :min="0"></el-input-number>
                </el-form-item>
                <el-button link type="primary" @click="handleAutoCalc" class="ml20" style="line-height: 32px">自动计算
                </el-button>
              </div>
            </el-col>
            <el-col :span="6">
              <el-form-item label="数量" prop="totalQuantity">
                <el-input-number v-model="form.totalQuantity" :controls="false" :precision="0"
                                 :disabled="true"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
      <el-card header="商品明细" class="mt10">
        <div class="receipt-order-content">
          <div class="flex-space-between mb8">
            <div>
            </div>
            <div>
              <el-button type="success" plain size="default" @click="handleExportExcel" icon="Download"
                         :disabled="!form.details || form.details.length === 0" style="margin-right: 10px;">
                导出Excel
              </el-button>
              <el-popover
                placement="left"
                title="提示"
                :width="200"
                trigger="hover"
                :disabled="form.warehouseId"
                content="请先选择仓库！"
              >
                <template #reference>
                  <el-button type="primary" plain="plain" size="default" @click="showAddItem" icon="Plus"
                             :disabled="!form.warehouseId">添加商品
                  </el-button>
                </template>
              </el-popover>
            </div>
          </div>
          <el-table :data="form.details" border empty-text="暂无商品明细">
            <el-table-column label="商品信息" prop="itemSku.itemName">
              <template #default="{ row }">
                <div>{{
                    row.itemSku.item.itemName + (row.itemSku.item.itemCode ? ('(' + row.itemSku.item.itemCode + ')') : '')
                  }}
                </div>
                <div v-if="row.itemSku.item.itemBrand">
                  品牌：{{ useWmsStore().itemBrandMap.get(row.itemSku.item.itemBrand).brandName }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="规格信息">
              <template #default="{ row }">
                <div>{{ row.itemSku.skuName }}</div>
                <div v-if="row.itemSku.barcode">条码：{{row.itemSku.barcode}}</div>
              </template>
            </el-table-column>
            <el-table-column label="库区" prop="areaName" width="200"/>
            <el-table-column label="批号" prop="batchNo" />
            <el-table-column label="生产日期" prop="productionDate">
              <template #default="{ row }">
                <div v-if="row.productionDate">{{ row.productionDate.substring(0, 10) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="过期日期" prop="expirationDate">
              <template #default="{ row }">
                <div v-if="row.expirationDate">{{ row.expirationDate.substring(0, 10) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="剩余库存" prop="remainQuantity" align="right" width="150">
              <template #default="{ row }">
                <el-statistic :value="Number(row.remainQuantity)" :precision="0"/>
              </template>
            </el-table-column>
            <el-table-column label="出库数量" prop="quantity" width="180">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.quantity"
                  placeholder="出库数量"
                  :min="1"
                  :precision="0"
                  :max="scope.row.remainQuantity"
                  @change="handleChangeQuantity"
                ></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="价格" prop="amount" width="180">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.amount"
                  placeholder="价格"
                  :precision="2"
                  :min="0"
                  :max="2147483647"
                ></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="SN码" width="150">
              <template #default="scope">
                <div v-if="scope.row.snCodes && scope.row.snCodes.length > 0">
                  <el-tag type="success" size="small">{{ scope.row.snCodes.length }}个</el-tag>
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click="handleEditSn(scope.row, scope.$index)"
                    style="margin-left: 5px;"
                  >
                    查看/编辑
                  </el-button>
                </div>
                <el-button
                  v-else
                  link
                  type="primary"
                  size="small"
                  @click="handleEditSn(scope.row, scope.$index)"
                >
                  录入SN码
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="right" fixed="right">
              <template #default="scope">
                <el-button icon="Delete" type="danger" plain size="small"
                           @click="handleDeleteDetail(scope.row, scope.$index)" link>删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
      <InventoryDetailSelect
        ref="inventorySelectRef"
        :model-value="inventorySelectShow"
        @handleOkClick="handleOkClick"
        @handleCancelClick="inventorySelectShow = false"
        :size="'90%'"
        :select-warehouse-disable="false"
        :select-area-disable="!!form?.areaId"
        :warehouse-id="form.warehouseId"
        :area-id="form.areaId"
        :selected-inventory="selectedInventory"
      />
      <SnInputDialog
        v-model="snDialogShow"
        :title="snDialogTitle"
        :item-info="currentSnItem"
        :quantity="currentSnQuantity"
        :existing-sn-codes="currentSnCodes"
        mode="scan"
        @confirm="handleSnConfirm"
      />
    </div>
    <div class="footer-global">
      <div class="btn-box">
        <div>
          <el-button @click="doShipment" type="primary" class="ml10">完成出库</el-button>
          <el-button @click="updateToInvalid" type="danger" v-if="form.id">作废</el-button>
        </div>
        <div>
          <el-button @click="save" type="primary">暂存</el-button>
          <el-button @click="cancel" class="mr10">取消</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="ShipmentOrderEdit">
import {computed, getCurrentInstance, onMounted, reactive, ref, toRef, toRefs, watch} from "vue";
import {addShipmentOrder, getShipmentOrder, updateShipmentOrder, shipment} from "@/api/wms/shipmentOrder";
import {delShipmentOrderDetail} from "@/api/wms/shipmentOrderDetail";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";
import {useWmsStore} from '@/store/modules/wms'
import {numSub, generateNo} from '@/utils/ruoyi'
import InventoryDetailSelect from "@/views/components/InventoryDetailSelect.vue";
import SnInputDialog from "@/components/SnInputDialog.vue";
import { validateSnBatchEnhanced } from '@/api/wms/sn';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

const {proxy} = getCurrentInstance();
const {wms_shipment_type} = proxy.useDict("wms_shipment_type");

const mode = ref(true) // 强制开启一物一码/SN模式
const loading = ref(false)
const snDialogShow = ref(false)
const currentSnItem = ref({})
const currentSnQuantity = ref(1)
const currentSnCodes = ref([])
const currentSnIndex = ref(-1)
const snDialogTitle = ref('录入SN码')
const initFormData = {
  id: undefined,
  shipmentOrderNo: undefined,
  shipmentOrderType: "2",
  merchantId: undefined,
  orderNo: undefined,
  receivableAmount: undefined,
  shipmentOrderStatus: 0,
  remark: undefined,
  warehouseId: undefined,
  areaId: undefined,
  totalQuantity: 0,
  details: [],
}
const inventorySelectRef = ref(null)
const selectedInventory = ref([])
const data = reactive({
  form: {...initFormData},
  rules: {
    shipmentOrderNo: [
      {required: true, message: "出库单号不能为空", trigger: "blur"}
    ],
    shipmentOrderType: [
      {required: true, message: "出库类型不能为空", trigger: "change"}
    ],
    warehouseId: [
      {required: true, message: "请选择仓库", trigger: ['blur', 'change']}
    ],
  }
});
const {form, rules} = toRefs(data);
const cancel = async () => {
  await proxy?.$modal.confirm('确认取消编辑出库单吗？');
  close()
}
const close = () => {
  const obj = {path: "/shipmentOrder"};
  proxy?.$tab.closeOpenPage(obj);
}
const inventorySelectShow = ref(false)

// 选择商品 start
const showAddItem = () => {
  inventorySelectRef.value.getList()
  inventorySelectShow.value = true
}
// 选择成功
const handleOkClick = (item) => {
  inventorySelectShow.value = false
  selectedInventory.value = [...item]
  item.forEach(it => {
    if (!form.value.details.find(detail => detail.inventoryDetailId === it.id)) {
      form.value.details.push(
        {
          itemSku: {
            ...it.itemSku,
            item: it.item
          },
          skuId: it.skuId,
          amount: undefined,
          quantity: undefined,
          remainQuantity: it.remainQuantity,
          batchNo: it.batchNo,
          productionDate: it.productionDate,
          expirationDate: it.expirationDate,
          warehouseId: form.value.warehouseId,
          areaId: form.value.areaId ?? it.areaId,
          inventoryDetailId: it.id,
          areaName: useWmsStore().areaMap.get(form.value.areaId ?? it.areaId)?.areaName,
          snCodes: []
        })
    }
  })
}

// SN码相关
const handleEditSn = (row, index) => {
  currentSnItem.value = {
    itemName: row.itemSku?.item?.itemName || '',
    skuName: row.itemSku?.skuName || ''
  }
  currentSnQuantity.value = row.quantity || 1
  currentSnCodes.value = row.snCodes || []
  currentSnIndex.value = index
  snDialogTitle.value = `录入SN码 - ${currentSnItem.value.itemName}`
  snDialogShow.value = true
}

const handleSnConfirm = (snCodes) => {
  if (currentSnIndex.value >= 0 && currentSnIndex.value < form.value.details.length) {
    form.value.details[currentSnIndex.value].snCodes = snCodes
    ElMessage.success(`已录入 ${snCodes.length} 个SN码`)
  }
  currentSnIndex.value = -1
}

const getPlaceAndSkuKey = (row) => {
  return row.warehouseId + '_' + row.areaId + '_' + row.skuId
}
// 选择商品 end

// 验证所有SN码的唯一性（包括不同商品之间）
const validateAllSnCodes = async () => {
  // 收集所有SN码
  const allSnCodes = []
  const snCodeMap = new Map() // 用于检查本地重复
  
  for (const detail of form.value.details) {
    if (detail.snCodes && Array.isArray(detail.snCodes)) {
      for (const snCode of detail.snCodes) {
        // 检查本地是否重复（不同商品之间）
        if (snCodeMap.has(snCode)) {
          return {
            valid: false,
            message: `SN码【${snCode}】重复！不同商品不能使用相同的SN码。商品1：${snCodeMap.get(snCode)}，商品2：${detail.itemSku?.item?.itemName}`
          }
        }
        snCodeMap.set(snCode, detail.itemSku?.item?.itemName)
        allSnCodes.push(snCode)
      }
    }
  }
  
  if (allSnCodes.length === 0) {
    return { valid: true }
  }
  
  // 调用后端批量验证（检查数据库中是否已存在）
  try {
    // 编辑时，需要排除原有的SN码
    const originalSnCodes = []
    if (form.value.id) {
      // 获取原始数据中的所有SN码
      const res = await getShipmentOrder(form.value.id)
      const originalOrder = res.data
      if (originalOrder.details) {
        originalOrder.details.forEach(detail => {
          if (detail.snCodes && Array.isArray(detail.snCodes)) {
            originalSnCodes.push(...detail.snCodes)
          }
        })
      }
    }
    
    const validateRes = await validateSnBatchEnhanced(allSnCodes, originalSnCodes)
    
    if (validateRes.code === 200 && validateRes.data) {
      if (validateRes.data.valid) {
        return { valid: true }
      } else {
        return {
          valid: false,
          message: validateRes.data.message || '存在重复的SN码，请检查！'
        }
      }
    } else {
      // 如果接口不可用，至少保证本地不重复
      console.warn('批量验证接口调用失败，使用本地验证')
      return { valid: true }
    }
  } catch (error) {
    console.error('批量验证SN码失败:', error)
    // 验证接口出错时，至少保证本地不重复
    return { valid: true }
  }
}

// 初始化receipt-order-form ref
const shipmentForm = ref()

const save = async () => {
  await proxy?.$modal.confirm('确认暂存出库单吗？');
  doSave()
}

const doSave = async (shipmentOrderStatus = 0) => {
  //验证shipmentForm表单
  const valid = await shipmentForm.value?.validate().catch(() => false);
  if (!valid) {
    return ElMessage.error('请填写必填项')
  }
  
  let details = []
  if (form.value.details?.length) {
    const invalidQuantityList = form.value.details.filter(it => !it.quantity)
    if (invalidQuantityList?.length) {
      return ElMessage.error('请选择数量')
    }
    // SN模式校验（强制开启）
    const invalidSnList = form.value.details.filter(it => {
      return !it.snCodes || it.snCodes.length !== it.quantity
    })
    if (invalidSnList?.length) {
      return ElMessage.error('SN模式下，请为每个商品录入完整的SN码（数量需与SN码数量一致）')
    }
    
    // ⭐ 新增：验证所有SN码的唯一性
    const validationResult = await validateAllSnCodes();
    if (!validationResult.valid) {
      return ElMessage.error(validationResult.message);
    }
      // 构建参数
      details = form.value.details.map(it => {
        const detail = {
          id: it.id,
          receiptOrderId: form.value.id,
          skuId: it.skuId,
          amount: it.amount,
          quantity: it.quantity,
          batchNo: it.batchNo,
          productionDate: it.productionDate,
          expirationDate: it.expirationDate,
          inventoryDetailId: it.inventoryDetailId,
          warehouseId: form.value.warehouseId,
          areaId: it.areaId
        }
        // SN模式下添加SN码（强制开启）
        if (it.snCodes) {
          detail.snCodes = it.snCodes
        }
        return detail
      })
    }


    //('提交前校验',form.value)
    const params = {
      id: form.value.id,
      shipmentOrderNo: form.value.shipmentOrderNo,
      shipmentOrderType: form.value.shipmentOrderType,
      shipmentOrderStatus,
      merchantId: form.value.merchantId,
      orderNo: form.value.orderNo,
      remark: form.value.remark,
      receivableAmount: form.value.receivableAmount,
      totalQuantity: form.value.totalQuantity,
      warehouseId: form.value.warehouseId,
      areaId: form.value.areaId,
      details: details
    }
    if (params.id) {
      updateShipmentOrder(params).then((res) => {
        if (res.code === 200) {
          ElMessage.success(res.msg)
          close()
        } else {
          ElMessage.error(res.msg)
        }
      })
    } else {
      addShipmentOrder(params).then((res) => {
        if (res.code === 200) {
          ElMessage.success(res.msg)
          close()
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
}


const updateToInvalid = async () => {
  await proxy?.$modal.confirm('确认作废出库单吗？');
  doSave(-1)
}

const doShipment = async () => {
  await proxy?.$modal.confirm('确认出库吗？');
  
  const valid = await shipmentForm.value?.validate().catch(() => false);
  if (!valid) {
    return ElMessage.error('请填写必填项')
  }
  
  if (!form.value.details?.length) {
    return ElMessage.error('请选择商品')
  }
  
  const invalidQuantityList = form.value.details.filter(it => !it.quantity)
  if (invalidQuantityList?.length) {
    return ElMessage.error('请选择出库数量')
  }
  
  // SN模式校验（强制开启）
  const invalidSnList = form.value.details.filter(it => {
    return !it.snCodes || it.snCodes.length !== it.quantity
  })
  if (invalidSnList?.length) {
    return ElMessage.error('SN模式下，请为每个商品录入完整的SN码（数量需与SN码数量一致）')
  }
  
  // ⭐ 新增：验证所有SN码的唯一性
  const validationResult = await validateAllSnCodes();
  if (!validationResult.valid) {
    return ElMessage.error(validationResult.message);
  }
    // 构建参数
    const details = form.value.details.map(it => {
      const detail = {
        id: it.id,
        receiptOrderId: form.value.id,
        skuId: it.skuId,
        amount: it.amount,
        quantity: it.quantity,
        batchNo: it.batchNo,
        productionDate: it.productionDate,
        expirationDate: it.expirationDate,
        inventoryDetailId: it.inventoryDetailId,
        warehouseId: form.value.warehouseId,
        areaId: it.areaId
      }
      // SN模式下添加SN码（强制开启）
      if (it.snCodes) {
        detail.snCodes = it.snCodes
      }
      return detail
    })

    //('提交前校验',form.value)
    const params = {
      id: form.value.id,
      shipmentOrderNo: form.value.shipmentOrderNo,
      shipmentOrderType: form.value.shipmentOrderType,
      merchantId: form.value.merchantId,
      orderNo: form.value.orderNo,
      remark: form.value.remark,
      receivableAmount: form.value.receivableAmount,
      totalQuantity: form.value.totalQuantity,
      warehouseId: form.value.warehouseId,
      areaId: form.value.areaId,
      details: details
    }
    loading.value = true
    shipment(params).then((res) => {
      if (res.code === 200) {
        ElMessage.success('出库成功')
        close()
      } else {
        ElMessage.error(res.msg)
      }
    }).finally(()=>{
      loading.value = false
    })
}

const route = useRoute();
onMounted(() => {
  const id = route.query && route.query.id;
  const fromConvert = route.query && route.query.fromConvert;
  
  if (id) {
    loadDetail(id)
  } else if (fromConvert === 'receipt') {
    // 从入库单转换而来
    loadConvertData();
  } else {
    form.value.shipmentOrderNo = 'CK' + generateNo()
  }
})

// 加载转换数据
const loadConvertData = () => {
  const convertData = localStorage.getItem('convertShipmentData');
  if (convertData) {
    try {
      const data = JSON.parse(convertData);
      form.value = {
        ...form.value,
        ...data
      };
      // 设置仓库和库区
      if (inventorySelectRef.value && form.value.warehouseId) {
        inventorySelectRef.value.setWarehouseIdAndAreaId(form.value.warehouseId, form.value.areaId);
      }
      // 清除localStorage数据
      localStorage.removeItem('convertShipmentData');
      proxy.$modal.msgSuccess("已自动填充入库单商品信息，请核对后提交！");
    } catch (error) {
      console.error('加载转换数据失败:', error);
      proxy.$modal.msgError("加载数据失败！");
    }
  }
}


// 获取入库单详情
const loadDetail = (id) => {
  loading.value = true
  getShipmentOrder(id).then((response) => {
    if (response.data.details?.length) {
      response.data.details.forEach(detail => {
        detail.areaName = useWmsStore().areaMap.get(detail.areaId)?.areaName
      })
      selectedInventory.value = response.data.details.map(it => {
        return {
          id: it.inventoryDetailId,
          areaId: it.areaId
        }
      })
    }
    form.value = {...response.data}
    inventorySelectRef.value.setWarehouseIdAndAreaId(form.value.warehouseId, form.value.areaId)
    Promise.resolve();
  }).then(() => {
  }).finally(() => {
    loading.value = false
  })
}

const handleChangeWarehouse = (e) => {
  form.value.areaId = undefined
  form.value.details = []
  inventorySelectRef.value.setWarehouseIdAndAreaId(form.value.warehouseId, form.value.areaId)
}

const handleChangeArea = (e) => {
  inventorySelectRef.value.setWarehouseIdAndAreaId(form.value.warehouseId, form.value.areaId)
  form.value.details = form.value.details.filter(it => it.areaId === e)
  selectedInventory.value = selectedInventory.value.filter(selected => selected.areaId === e)
}

const handleChangeQuantity = () => {
  let sum = 0
  form.value.details.forEach(it => {
    if (it.quantity) {
      sum += Number(it.quantity)
    }
  })
  form.value.totalQuantity = sum
}

const handleAutoCalc = () => {
  let sum = undefined
  form.value.details.forEach(it => {
    if (it.amount >= 0) {
      if (!sum) {
        sum = 0
      }
      sum = numSub(sum, -Number(it.amount))
    }
  })
  form.value.receivableAmount = sum
}

// 导出Excel
const handleExportExcel = () => {
  if (!form.value.details || form.value.details.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  
  // 获取仓库和库区名称
  const warehouseName = useWmsStore().warehouseList.find(w => w.id === form.value.warehouseId)?.warehouseName || '未知仓库'
  const areaName = form.value.areaId ? (useWmsStore().areaMap.get(form.value.areaId)?.areaName || '未知库区') : '全部库区'
  
  // 准备导出数据
  const exportData = form.value.details.map((row, index) => {
    const itemName = row.itemSku?.item?.itemName || ''
    const itemCode = row.itemSku?.item?.itemCode || ''
    const brandName = row.itemSku?.item?.itemBrand ? (useWmsStore().itemBrandMap.get(row.itemSku.item.itemBrand)?.brandName || '') : ''
    const skuName = row.itemSku?.skuName || ''
    const barcode = row.itemSku?.barcode || ''
    
    return {
      '序号': index + 1,
      '商品名称': itemName,
      '商品编码': itemCode,
      '品牌': brandName,
      '规格名称': skuName,
      '条码': barcode,
      '库区': row.areaName || '',
      '批号': row.batchNo || '',
      '生产日期': row.productionDate ? row.productionDate.substring(0, 10) : '',
      '过期日期': row.expirationDate ? row.expirationDate.substring(0, 10) : '',
      '剩余库存': row.remainQuantity || 0,
      '出库数量': row.quantity || 0,
      '价格': row.amount || 0,
      'SN码数量': row.snCodes?.length || 0,
      'SN码': row.snCodes?.join(', ') || ''
    }
  })
  
  // 创建工作簿
  const wb = XLSX.utils.book_new()
  const ws = XLSX.utils.json_to_sheet(exportData)
  
  // 设置列宽
  const colWidths = [
    { wch: 6 },   // 序号
    { wch: 20 },  // 商品名称
    { wch: 15 },  // 商品编码
    { wch: 12 },  // 品牌
    { wch: 20 },  // 规格名称
    { wch: 15 },  // 条码
    { wch: 15 },  // 库区
    { wch: 15 },  // 批号
    { wch: 12 },  // 生产日期
    { wch: 12 },  // 过期日期
    { wch: 12 },  // 剩余库存
    { wch: 12 },  // 出库数量
    { wch: 12 },  // 价格
    { wch: 12 },  // SN码数量
    { wch: 30 }   // SN码
  ]
  ws['!cols'] = colWidths
  
  // 添加工作表
  XLSX.utils.book_append_sheet(wb, ws, '商品明细')
  
  // 生成文件名
  const fileName = `出库单_${warehouseName}_${areaName}_${new Date().getTime()}.xlsx`
  
  // 导出文件
  const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
  saveAs(new Blob([wbout], { type: 'application/octet-stream' }), fileName)
  
  ElMessage.success('导出成功')
}

const handleDeleteDetail = (row, index) => {
  if (row.id) {
    proxy.$modal.confirm('确认删除本条商品明细吗？如确认会立即执行！').then(function () {
      return delShipmentOrderDetail(row.id);
    }).then(() => {
      form.value.details.splice(index, 1)
      proxy.$modal.msgSuccess("删除成功");
    })
  } else {
    form.value.details.splice(index, 1)
  }
  const indexOfSelected = selectedInventory.value.findIndex(it => it.id === row.inventoryDetailId)
  selectedInventory.value.splice(indexOfSelected, 1)
}
</script>

<style lang="scss" scoped>
@import "@/assets/styles/variables.module";

.btn-box {
  width: calc(100% - #{$base-sidebar-width});
  display: flex;
  align-items: center;
  justify-content: space-between;
  float: right;
}

.el-statistic__content {
  font-size: 14px;
}
</style>
