# SN码唯一性问题修复说明

## 🐛 问题描述

**用户反馈：**
> 在 http://localhost/receiptOrderEdit 页面中，对两个不同的货物使用了相同的SN码（如都使用SN001），结果居然提交成功了！

**根本原因：**
1. ✅ 单个SN码录入时有验证（已完成）
2. ❌ **提交入库单/出库单时没有批量验证所有SN码**
3. ❌ **不同商品之间的SN码没有交叉验证**

## ✅ 解决方案

### 🔧 新增功能

#### 1. 提交时批量验证所有SN码

**验证时机：**
- 暂存入库单时
- 完成入库时
- 暂存出库单时
- 完成出库时

**验证内容：**
1. ✅ 检查本地重复（不同商品之间）
2. ✅ 检查数据库重复（与已有SN码）
3. ✅ 编辑时排除原有SN码

#### 2. 多层验证机制

```
用户点击"暂存"或"完成入库"
    ↓
表单基础验证
    ↓
SN码数量验证（每个商品的SN码数量=商品数量）
    ↓
⭐ 新增：批量验证所有SN码唯一性
    ├─ 本地检查：不同商品间不能用相同SN码
    └─ 后端检查：数据库中不能已存在
    ↓
验证通过 → 提交保存
验证失败 → 显示错误，阻止提交
```

## 📝 代码实现

### 1. 前端批量验证函数

```javascript
// 验证所有SN码的唯一性（包括不同商品之间）
const validateAllSnCodes = async () => {
  // 1. 收集所有SN码
  const allSnCodes = []
  const snCodeMap = new Map()
  
  for (const detail of form.value.details) {
    if (detail.snCodes && Array.isArray(detail.snCodes)) {
      for (const snCode of detail.snCodes) {
        // 2. 检查本地重复（不同商品之间）
        if (snCodeMap.has(snCode)) {
          return {
            valid: false,
            message: `SN码【${snCode}】重复！不同商品不能使用相同的SN码。
                     商品1：${snCodeMap.get(snCode)}，商品2：${detail.itemSku?.item?.itemName}`
          }
        }
        snCodeMap.set(snCode, detail.itemSku?.item?.itemName)
        allSnCodes.push(snCode)
      }
    }
  }
  
  // 3. 调用后端批量验证
  const validateRes = await validateSnBatchEnhanced(allSnCodes, originalSnCodes)
  
  if (validateRes.data.valid) {
    return { valid: true }
  } else {
    return {
      valid: false,
      message: validateRes.data.message
    }
  }
}
```

### 2. 保存时调用验证

```javascript
const doSave = async (receiptOrderStatus = 0) => {
  // ... 其他验证 ...
  
  // ⭐ 新增：验证所有SN码的唯一性
  const validationResult = await validateAllSnCodes();
  if (!validationResult.valid) {
    return ElMessage.error(validationResult.message);
  }
  
  // 验证通过，继续保存
  // ...
}
```

### 3. 新增API接口

```javascript
// frontend/src/api/wms/sn.js

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
```

## 📋 修改的文件

| 文件 | 修改内容 | 状态 |
|------|---------|------|
| `frontend/src/api/wms/sn.js` | 新增批量验证增强版API | ✅ 已完成 |
| `frontend/src/views/wms/order/receipt/edit.vue` | 添加validateAllSnCodes函数 | ✅ 已完成 |
| `frontend/src/views/wms/order/shipment/edit.vue` | 添加validateAllSnCodes函数 | ✅ 已完成 |

## 🎯 验证场景

### 场景1：不同商品使用相同SN码（现在会被拒绝）

**操作：**
1. 创建入库单
2. 添加商品A，数量=1，录入SN码：`TEST001`
3. 添加商品B，数量=1，录入SN码：`TEST001`（相同）
4. 点击"暂存"或"完成入库"

**结果：**
- ❌ 之前：提交成功（BUG）
- ✅ 现在：提示"SN码【TEST001】重复！不同商品不能使用相同的SN码。商品1：商品A，商品2：商品B"

### 场景2：使用其他订单的SN码（现在会被拒绝）

**操作：**
1. 订单A已有SN码：`SN001`
2. 创建新订单B
3. 添加商品，录入SN码：`SN001`
4. 点击"暂存"

**结果：**
- ❌ 之前：可能提交成功（BUG）
- ✅ 现在：提示"以下SN码已存在：SN001"

### 场景3：编辑订单，保留原SN码（应该成功）

**操作：**
1. 编辑已有订单（SN码：SN001, SN002）
2. 不修改SN码
3. 点击"暂存"

**结果：**
- ✅ 提交成功（原有SN码会被排除验证）

### 场景4：同一商品重复录入SN码（在录入时就被拒绝）

**操作：**
1. 商品A数量=2
2. 录入SN001
3. 再次录入SN001

**结果：**
- ✅ 立即提示"SN码已存在，请勿重复录入"

## 🔒 完整的防护机制

| 检查点 | 验证内容 | 触发时机 | 位置 |
|--------|---------|---------|------|
| 1️⃣ | 本地单个商品内不重复 | 录入SN码时 | SnInputDialog组件 |
| 2️⃣ | 数据库不重复 | 录入SN码时 | 后端API `/wms/sn/input` |
| 3️⃣ | 本地不同商品间不重复 | 提交订单时 | validateAllSnCodes函数 |
| 4️⃣ | 批量数据库验证 | 提交订单时 | 后端API `/wms/sn/validate/batch/enhanced` |
| 5️⃣ | 数据库唯一约束 | 保存到数据库时 | UNIQUE KEY `uk_sn_code` |

## 🧪 测试步骤

### ⭐ 关键测试：不同商品使用相同SN码

1. **刷新浏览器**（Ctrl + Shift + R）
2. 进入"入库单管理" → 点击"新增"
3. 填写基本信息
4. **添加第一个商品**：
   - 选择商品A
   - 数量设为 1
   - 点击"录入SN码"
   - 输入：`DUPLICATE001`
   - 点击"添加"，点击"确定"
5. **添加第二个商品**：
   - 选择商品B
   - 数量设为 1
   - 点击"录入SN码"
   - 输入：`DUPLICATE001`（相同！）
   - 点击"添加"，点击"确定"
6. **尝试提交**：
   - 点击"暂存"按钮
7. ✅ **验证点**：应该弹出错误提示：
   ```
   SN码【DUPLICATE001】重复！不同商品不能使用相同的SN码。
   商品1：商品A，商品2：商品B
   ```
8. ✅ **验证点**：订单不能提交成功

### 本地验证（立即生效，无需后端）

**立即可测试的功能：**
1. ✅ 不同商品之间的SN码重复检查（本地JavaScript）
2. ✅ 友好的错误提示
3. ✅ 阻止提交

**需要后端支持的功能：**
1. ⏳ 检查数据库中已存在的SN码（需要后端API）
2. ⏳ 批量验证接口（需要重启后端）

## 📊 错误提示示例

### 本地重复检查（立即生效）

```
❌ SN码【TEST001】重复！不同商品不能使用相同的SN码。
商品1：iPhone 15 Pro，商品2：MacBook Pro
```

### 数据库重复检查（需要后端）

```
❌ 以下SN码已存在：TEST001, TEST002
```

### 同时存在多个问题

```
❌ SN码【TEST001】重复！不同商品不能使用相同的SN码。
商品1：商品A，商品2：商品B

或

❌ 以下SN码已存在：TEST001
```

## 🎯 立即测试

### 快速验证（无需后端重启）

**测试本地验证：**
1. 刷新浏览器（Ctrl + Shift + R）
2. 打开入库单编辑页面
3. 添加2个商品，都使用相同的SN码
4. 点击"暂存"
5. ✅ 应该立即提示错误（本地JavaScript检查）
6. ✅ 无法提交成功

这个验证是**纯前端的**，不依赖后端，**立即生效**！

### 完整验证（需要后端支持）

等后端成功运行后：
1. 测试跨订单SN码重复检查
2. 测试数据库级别的唯一性
3. 测试编辑时排除原SN码

## 💡 关键改进

| 改进项 | 之前 | 现在 |
|--------|------|------|
| 不同商品SN码检查 | ❌ 无 | ✅ 本地+后端双重检查 |
| 提交时批量验证 | ❌ 无 | ✅ 每次提交都验证 |
| 错误提示 | ❌ 无 | ✅ 详细指出重复位置 |
| 编辑场景支持 | ❌ 无 | ✅ 智能排除原SN码 |

## 🎉 修复完成

✅ **关键问题已修复：**
- 不同商品不能使用相同的SN码
- 提交前强制验证所有SN码
- 友好的错误提示
- 阻止提交重复SN码

✅ **立即生效（无需后端）：**
- 本地JavaScript检查已生效
- 刷新浏览器即可测试

✅ **完整功能（需要后端）：**
- 数据库级别的唯一性检查
- 跨订单SN码重复验证
- 批量验证增强接口

## 📞 测试确认

请立即测试：

1. **刷新浏览器**（Ctrl + Shift + R）
2. 创建新入库单
3. 添加2个不同商品
4. 为两个商品都输入相同的SN码（如：`TEST001`）
5. 点击"暂存"
6. ✅ **应该提示错误，无法提交**

如果还能提交成功，请告诉我具体操作步骤！

---

**修复日期：** 2025-11-28

**状态：** ✅ 已完成

**测试：** ⏳ 待用户确认

