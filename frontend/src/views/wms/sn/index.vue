<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="SN码" prop="snCode">
        <el-input
          v-model="queryParams.snCode"
          placeholder="请输入SN码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物品名称" prop="itemName">
        <el-input
          v-model="queryParams.itemName"
          placeholder="请输入物品名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="绑定状态" prop="bindStatus">
        <el-select v-model="queryParams.bindStatus" placeholder="请选择绑定状态" clearable>
          <el-option label="未绑定" value="0" />
          <el-option label="已绑定" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
        >手动输入SN码</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Camera"
          @click="handleScan"
        >扫描录入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="snList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="SN码" align="center" prop="snCode" :show-overflow-tooltip="true" />
      <el-table-column label="物品编号" align="center" prop="itemNo" />
      <el-table-column label="物品名称" align="center" prop="itemName" />
      <el-table-column label="规格型号" align="center" prop="spec" />
      <el-table-column label="绑定状态" align="center" prop="bindStatus">
        <template #default="scope">
          <dict-tag :options="bind_status" :value="scope.row.bindStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="入库时间" align="center" prop="inboundTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.inboundTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改SN码对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="snRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="SN码" prop="snCode">
          <el-input v-model="form.snCode" placeholder="请输入SN码" :disabled="form.snId != null" />
        </el-form-item>
        <el-form-item label="物品信息" prop="itemId">
          <el-select 
            v-model="form.itemId" 
            filterable 
            placeholder="请选择物品"
            @change="handleItemChange"
          >
            <el-option
              v-for="item in itemList"
              :key="item.id"
              :label="`${item.itemNo} - ${item.itemName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物品编号" prop="itemNo">
          <el-input v-model="form.itemNo" disabled />
        </el-form-item>
        <el-form-item label="物品名称" prop="itemName">
          <el-input v-model="form.itemName" disabled />
        </el-form-item>
        <el-form-item label="规格型号" prop="spec">
          <el-input v-model="form.spec" disabled />
        </el-form-item>
        <el-form-item label="绑定状态" prop="bindStatus">
          <el-radio-group v-model="form.bindStatus">
            <el-radio label="0">未绑定</el-radio>
            <el-radio label="1">已绑定</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 扫描录入对话框 -->
    <el-dialog title="扫描录入" v-model="scanOpen" width="500px" append-to-body>
      <div class="scan-container">
        <el-alert
          title="请连接扫描枪"
          type="info"
          description="请确保扫描枪已正确连接到计算机，然后使用扫描枪扫描物品上的SN码"
          :closable="false"
          show-icon
          center
        />
        <div class="scan-status">
          <el-icon class="scan-icon" :size="80">
            <Camera />
          </el-icon>
          <p class="scan-text">等待扫描...</p>
        </div>
        <el-form ref="scanFormRef" :model="scanForm" label-width="100px" style="margin-top: 20px;">
          <el-form-item label="扫描输入框" prop="scanInput">
            <el-input
              v-model="scanForm.scanInput"
              placeholder="焦点在此输入框，扫描枪会自动输入"
              @keyup.enter="handleScanInput"
              ref="scanInputRef"
              autofocus
            />
          </el-form-item>
          <el-form-item label="已扫描列表">
            <el-tag
              v-for="(code, index) in scannedCodes"
              :key="index"
              closable
              @close="handleRemoveScanned(index)"
              style="margin-right: 10px; margin-bottom: 10px;"
            >
              {{ code }}
            </el-tag>
            <div v-if="scannedCodes.length === 0" style="color: #909399; font-size: 14px;">
              暂无扫描记录
            </div>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmScan" :disabled="scannedCodes.length === 0">
            确认提交（{{ scannedCodes.length }}）
          </el-button>
          <el-button @click="closeScan">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Sn">
import { listSn, getSn, delSn, addSn, updateSn, scanSn } from "@/api/wms/sn";
import { listItem } from "@/api/wms/item";

const { proxy } = getCurrentInstance();
const { bind_status } = proxy.useDict('bind_status');

const snList = ref([]);
const itemList = ref([]);
const open = ref(false);
const scanOpen = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const scannedCodes = ref([]);

const data = reactive({
  form: {},
  scanForm: {
    scanInput: ''
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    snCode: null,
    itemName: null,
    bindStatus: null,
  },
  rules: {
    snCode: [
      { required: true, message: "SN码不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, scanForm, rules } = toRefs(data);

/** 查询SN码列表 */
function getList() {
  loading.value = true;
  listSn(queryParams.value).then(response => {
    snList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 查询物品列表 */
function getItemList() {
  listItem({ pageNum: 1, pageSize: 1000 }).then(response => {
    itemList.value = response.rows;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    snId: null,
    snCode: null,
    itemId: null,
    itemNo: null,
    itemName: null,
    spec: null,
    bindStatus: "0",
    inboundTime: null,
    remark: null
  };
  proxy.resetForm("snRef");
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

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.snId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "手动输入SN码";
  getItemList();
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const snCode = row.snCode || ids.value[0];
  getSn(snCode).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改SN码";
    getItemList();
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["snRef"].validate(valid => {
    if (valid) {
      if (form.value.snId != null) {
        updateSn(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addSn(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const snCodes = row.snCode || ids.value.join(",");
  proxy.$modal.confirm('是否确认删除SN码编号为"' + snCodes + '"的数据项？').then(function() {
    return delSn(snCodes);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 扫描按钮操作 */
function handleScan() {
  scanOpen.value = true;
  scannedCodes.value = [];
  scanForm.value.scanInput = '';
  // 延迟聚焦，确保对话框已完全打开
  nextTick(() => {
    proxy.$refs.scanInputRef?.focus();
  });
}

/** 处理扫描输入 */
function handleScanInput() {
  const input = scanForm.value.scanInput.trim();
  if (input) {
    // 检查是否已存在
    if (!scannedCodes.value.includes(input)) {
      scannedCodes.value.push(input);
      proxy.$modal.msgSuccess(`成功扫描: ${input}`);
    } else {
      proxy.$modal.msgWarning(`SN码已存在: ${input}`);
    }
    // 清空输入框，准备下次扫描
    scanForm.value.scanInput = '';
  }
}

/** 移除已扫描的码 */
function handleRemoveScanned(index) {
  scannedCodes.value.splice(index, 1);
}

/** 确认扫描提交 */
function confirmScan() {
  if (scannedCodes.value.length === 0) {
    proxy.$modal.msgWarning("请先扫描SN码");
    return;
  }
  
  // 这里可以批量提交到后端
  const batchData = scannedCodes.value.map(code => ({
    snCode: code,
    bindStatus: "0"
  }));
  
  // 模拟批量提交（实际应该调用批量接口）
  let successCount = 0;
  const promises = batchData.map(item => {
    return addSn(item).then(() => {
      successCount++;
    }).catch(error => {
      console.error(`添加 ${item.snCode} 失败:`, error);
    });
  });
  
  Promise.all(promises).then(() => {
    proxy.$modal.msgSuccess(`成功添加 ${successCount} 条SN码记录`);
    closeScan();
    getList();
  });
}

/** 关闭扫描对话框 */
function closeScan() {
  scanOpen.value = false;
  scannedCodes.value = [];
  scanForm.value.scanInput = '';
}

/** 物品选择改变 */
function handleItemChange(itemId) {
  const selectedItem = itemList.value.find(item => item.id === itemId);
  if (selectedItem) {
    form.value.itemNo = selectedItem.itemNo;
    form.value.itemName = selectedItem.itemName;
    form.value.spec = selectedItem.spec;
  }
}

getList();
</script>

<style scoped>
.scan-container {
  padding: 20px;
}

.scan-status {
  text-align: center;
  padding: 40px 0;
}

.scan-icon {
  color: #409EFF;
  margin-bottom: 20px;
}

.scan-text {
  font-size: 18px;
  color: #606266;
  margin: 0;
}
</style>

