<template>
  <el-dialog
    :title="title"
    v-model="visible"
    width="600px"
    append-to-body
    @close="handleClose"
  >
    <div class="sn-input-container">
      <el-alert
        v-if="mode === 'scan'"
        title="扫描录入模式"
        type="info"
        description="请使用扫描枪扫描SN码，扫描枪会自动输入并按回车确认"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      />
      <el-form label-width="100px">
        <el-form-item label="商品信息">
          <div>
            <div>{{ itemInfo.itemName }}</div>
            <div v-if="itemInfo.skuName" style="color: #909399; font-size: 12px;">规格：{{ itemInfo.skuName }}</div>
          </div>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number :model-value="quantity" :min="1" :precision="0" :disabled="true" />
          <span style="margin-left: 10px; color: #909399;">需要录入 {{ quantity }} 个SN码</span>
        </el-form-item>
        <el-form-item label="SN码录入">
          <div style="width: 100%;">
            <div style="display: flex; gap: 8px; align-items: center;">
              <el-input
                v-model="currentSnCode"
                placeholder="请输入SN码（直接输入会自动验证）"
                @keyup.enter="handleAddSn"
                ref="snInputRef"
                clearable
                style="flex: 1;"
              >
                <template #append>
                  <el-button @click="handleAddSn" :disabled="!currentSnCode || isAdding" :loading="isAdding">
                    {{ isAdding ? '验证中...' : '添加' }}
                  </el-button>
                </template>
              </el-input>
              <el-button 
                type="primary" 
                @click="handleScan" 
                :loading="isScanning"
                style="min-width: 80px;"
              >
                <el-icon v-if="!isScanning" style="margin-right: 4px;"><Camera /></el-icon>
                <span>{{ isScanning ? '扫描中...' : '扫描' }}</span>
              </el-button>
            </div>
            <div style="margin-top: 10px; color: #909399; font-size: 12px;">
              已录入：{{ snCodes.length }} / {{ quantity }}
              <span style="margin-left: 10px; color: #409eff;">提示：直接输入SN码会自动调用验证接口，点击"扫描"按钮会提示连接扫描枪</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="已录入SN码">
          <div style="max-height: 300px; overflow-y: auto; border: 1px solid #dcdfe6; border-radius: 4px; padding: 10px;">
            <el-tag
              v-for="(code, index) in snCodes"
              :key="index"
              closable
              @close="handleRemoveSn(index)"
              style="margin-right: 8px; margin-bottom: 8px;"
              type="success"
            >
              {{ code }}
            </el-tag>
            <div v-if="snCodes.length === 0" style="color: #909399; text-align: center; padding: 20px;">
              暂无SN码，请开始录入
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="snCodes.length !== quantity">
          确 定 ({{ snCodes.length }}/{{ quantity }})
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
import { scanSn, inputSn } from '@/api/wms/sn'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '录入SN码'
  },
  itemInfo: {
    type: Object,
    default: () => ({})
  },
  quantity: {
    type: Number,
    default: 1
  },
  existingSnCodes: {
    type: Array,
    default: () => []
  },
  mode: {
    type: String,
    default: 'manual' // manual 或 scan
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = ref(false)
const currentSnCode = ref('')
const snCodes = ref([])
const snInputRef = ref(null)
const isAdding = ref(false)
const isScanning = ref(false)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    snCodes.value = [...props.existingSnCodes]
    currentSnCode.value = ''
    nextTick(() => {
      snInputRef.value?.focus()
    })
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 处理扫描按钮点击
const handleScan = async () => {
  isScanning.value = true
  try {
    const res = await scanSn()
    console.log('扫描接口返回:', res)
    if (res.code === 200) {
      // 显示"请连接扫描枪"提示
      ElMessage.warning(res.data || res.msg || '请连接扫描枪')
    } else {
      ElMessage.error(res.msg || '扫描失败')
    }
  } catch (error) {
    console.error('扫描接口调用失败:', error)
    ElMessage.error('扫描接口调用失败: ' + (error.message || '未知错误'))
  } finally {
    isScanning.value = false
  }
}

// 处理添加SN码
const handleAddSn = async () => {
  const code = currentSnCode.value?.trim()
  if (!code) {
    ElMessage.warning('请输入SN码')
    return
  }
  
  // 检查本地是否重复
  if (snCodes.value.includes(code)) {
    ElMessage.warning('SN码已存在，请勿重复录入')
    currentSnCode.value = ''
    // 扫描模式下，重复时也要聚焦
    if (props.mode === 'scan') {
      nextTick(() => {
        snInputRef.value?.focus()
      })
    }
    return
  }
  
  if (snCodes.value.length >= props.quantity) {
    ElMessage.warning(`最多只能录入 ${props.quantity} 个SN码`)
    return
  }
  
  // 调用后端接口验证SN码（直接输入接口）
  isAdding.value = true
  try {
    console.log('调用直接输入接口，SN码:', code)
    const res = await inputSn(code)
    console.log('直接输入接口返回:', res)
    if (res && res.code === 200) {
      // 验证通过，添加到列表
      snCodes.value.push(code)
      currentSnCode.value = ''
      
      // 如果是扫描模式，自动聚焦输入框
      if (props.mode === 'scan') {
        nextTick(() => {
          snInputRef.value?.focus()
          // 如果已录入完成，给出提示
          if (snCodes.value.length === props.quantity) {
            ElMessage.success(`已录入完成 ${props.quantity} 个SN码，请点击确定按钮`)
          }
        })
      } else {
        ElMessage.success(`已添加SN码: ${code}（已通过验证）`)
      }
    } else {
      // 验证失败，显示错误信息
      const errorMsg = res?.msg || res?.data || 'SN码验证失败'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('直接输入接口调用失败:', error)
    // 如果接口不存在或网络错误，先允许本地添加（开发阶段）
    if (error.response && error.response.status === 404) {
      ElMessage.warning('接口未找到，使用本地验证模式')
      // 本地验证通过，添加到列表
      snCodes.value.push(code)
      currentSnCode.value = ''
      ElMessage.success(`已添加SN码: ${code}`)
    } else {
      const errorMsg = error?.response?.data?.msg || error?.message || 'SN码验证接口调用失败'
      ElMessage.error(errorMsg)
    }
  } finally {
    isAdding.value = false
  }
}

const handleRemoveSn = (index) => {
  snCodes.value.splice(index, 1)
  // 扫描模式下，删除后自动聚焦输入框
  if (props.mode === 'scan') {
    nextTick(() => {
      snInputRef.value?.focus()
    })
  }
}

const handleConfirm = () => {
  if (snCodes.value.length !== props.quantity) {
    ElMessage.warning(`请录入完整的 ${props.quantity} 个SN码`)
    return
  }
  
  emit('confirm', [...snCodes.value])
  handleClose()
}

const handleClose = () => {
  visible.value = false
  snCodes.value = []
  currentSnCode.value = ''
}
</script>

<style scoped>
.sn-input-container {
  padding: 10px 0;
}
</style>

