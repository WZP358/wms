# SN码唯一性验证增强说明

## 📋 问题描述

原系统中SN码存在重复录入的问题，特别是在编辑入库单/出库单时，SN码的唯一性验证不够严格。

## ✅ 解决方案

### 1. 数据库层面保障

数据库已有唯一约束：
```sql
UNIQUE KEY `uk_sn_code` (`sn_code`)
```

这是最后一道防线，确保数据库级别的唯一性。

### 2. 后端API增强

#### 新增接口

**增强版验证接口：** `POST /wms/sn/input`

支持排除已有SN码（用于编辑场景）：

```java
{
  "snCode": "SN001",           // 要验证的SN码
  "excludeSnCodes": ["SN001", "SN002"]  // 排除列表（编辑时的已有SN码）
}
```

**响应：**
- 成功：`{ "code": 200, "msg": "SN码验证通过" }`
- 失败：`{ "code": 500, "msg": "SN码已存在，请勿重复录入" }`

#### 验证逻辑

```java
@Override
public String inputSnWithExclude(String snCode, List<String> excludeSnCodes) {
    // 1. 格式验证
    if (StringUtils.isBlank(snCode)) {
        return "SN码不能为空";
    }
    if (snCode.length() < 3 || snCode.length() > 50) {
        return "SN码长度应在3-50个字符之间";
    }
    
    // 2. 排除列表检查
    if (excludeSnCodes != null && excludeSnCodes.contains(snCode)) {
        return "OK";  // 在排除列表中，允许通过
    }
    
    // 3. 数据库唯一性检查
    if (baseMapper.exists(lqw.eq(SerialNumber::getSnCode, snCode))) {
        return "SN码已存在，请勿重复录入";
    }
    
    return "OK";
}
```

### 3. 前端组件增强

#### SnInputDialog组件

**更新的验证流程：**

```javascript
// 1. 本地检查（当前输入框中的SN码）
if (snCodes.value.includes(code)) {
  ElMessage.warning('SN码已存在，请勿重复录入')
  return
}

// 2. 调用后端API验证（实时验证）
const res = await inputSn(code, props.existingSnCodes)

// 3. 根据验证结果处理
if (res.code === 200) {
  snCodes.value.push(code)  // 验证通过，添加
} else {
  ElMessage.error(res.msg)  // 验证失败，显示错误
}
```

**关键特性：**
- ✅ 每次输入SN码都实时调用后端验证
- ✅ 支持扫描模式（自动聚焦）
- ✅ 支持编辑模式（排除已有SN码）
- ✅ 友好的错误提示

### 4. API接口更新

```javascript
// frontend/src/api/wms/sn.js

export function inputSn(snCode, excludeSnCodes = []) {
  return request({
    url: '/wms/sn/input',
    method: 'post',
    data: { 
      snCode,
      excludeSnCodes: excludeSnCodes || []
    }
  })
}
```

## 🎯 验证场景

### 场景1：新增入库单录入SN码

```
1. 用户点击"录入SN码"
2. 输入SN001
3. 前端调用: inputSn("SN001", [])
4. 后端检查: SN001不存在
5. 返回: 验证通过
6. SN001添加到列表
```

### 场景2：编辑入库单修改SN码

```
1. 入库单已有SN码: [SN001, SN002]
2. 用户删除SN002，重新录入SN003
3. 前端调用: inputSn("SN003", ["SN001", "SN002"])
4. 后端检查: SN003不存在且不在排除列表
5. 返回: 验证通过
6. SN003添加到列表
```

### 场景3：尝试录入重复SN码

```
1. 用户输入SN001
2. 前端调用: inputSn("SN001", [])
3. 后端检查: SN001已存在于数据库
4. 返回: SN码已存在，请勿重复录入
5. 前端显示错误提示
6. SN001未添加到列表
```

### 场景4：编辑时保留原有SN码

```
1. 入库单已有: [SN001, SN002]
2. 用户删除SN002后又想重新添加
3. 前端调用: inputSn("SN002", ["SN001", "SN002"])
4. 后端检查: SN002在排除列表中
5. 返回: 验证通过（因为是原有的SN码）
6. SN002重新添加到列表
```

## 📊 验证流程图

```
用户输入SN码
    ↓
前端本地检查（是否在当前列表）
    ↓ 不重复
调用后端API验证
    ↓
后端检查格式
    ↓ 格式正确
检查是否在排除列表
    ↓ 不在排除列表
查询数据库
    ↓
是否已存在？
    ├─ 是 → 返回失败 → 前端显示错误
    └─ 否 → 返回成功 → 添加到列表
```

## 🔒 多层防护

| 层级 | 验证位置 | 验证内容 | 优先级 |
|------|---------|---------|--------|
| 1 | 前端本地 | 当前输入列表内不重复 | 最快 |
| 2 | 前端API | 实时调用后端验证 | 快速 |
| 3 | 后端逻辑 | 数据库查询验证 | 可靠 |
| 4 | 数据库约束 | UNIQUE KEY | 最终 |

## ⚙️ 配置说明

### 后端配置

**文件位置：**
- Controller: `SerialNumberController.java`
- Service: `SerialNumberService.java`
- ServiceImpl: `SerialNumberServiceImpl.java`

**关键方法：**
```java
// 验证单个SN码（支持排除列表）
String inputSnWithExclude(String snCode, List<String> excludeSnCodes)

// 批量验证SN码（增强版）
Map<String, Object> validateSnBatchEnhanced(
    List<String> snCodes, 
    List<String> excludeSnCodes
)
```

### 前端配置

**组件位置：**
- `frontend/src/components/SnInputDialog.vue`

**API位置：**
- `frontend/src/api/wms/sn.js`

**使用方式：**
```vue
<sn-input-dialog
  v-model="showSnDialog"
  :quantity="row.quantity"
  :existing-sn-codes="row.snCodes || []"
  @confirm="handleSnConfirm"
/>
```

## 🧪 测试用例

### 测试1：新增时重复验证

1. 创建新入库单
2. 添加商品，数量=2
3. 录入SN001（成功）
4. 再次录入SN001（失败，提示"SN码已存在"）
5. ✅ 验证通过

### 测试2：编辑时保留原SN码

1. 编辑已有入库单（已有SN001）
2. 删除SN001
3. 重新录入SN001（成功，因为在排除列表）
4. ✅ 验证通过

### 测试3：编辑时录入其他订单的SN码

1. 订单A已有SN001
2. 编辑订单B
3. 尝试录入SN001（失败，提示"已存在"）
4. ✅ 验证通过

### 测试4：扫描模式快速录入

1. 使用扫描枪模式
2. 快速扫描SN001、SN002、SN003
3. 每个SN码都实时验证
4. ✅ 验证通过

## 🐛 常见问题

### Q: 为什么需要排除列表？

A: 在编辑入库单/出库单时，原有的SN码应该被允许重新录入。例如用户删除后反悔，需要重新添加。

### Q: 验证会不会影响性能？

A: 
- 前端本地检查：毫秒级
- 后端API验证：通常<100ms
- 数据库查询：有索引，很快
- 总体影响：可接受

### Q: 扫描枪模式如何工作？

A: 扫描枪会自动输入SN码并按回车，系统捕获回车事件，自动验证并添加到列表，然后自动聚焦等待下一次扫描。

### Q: 数据库约束冲突怎么办？

A: 如果前端验证被绕过，数据库约束会作为最后防线，返回错误。前端应正确处理这个错误。

## 📝 更新历史

- **2025-11-28:** 
  - 增强SN码实时验证API接口
  - 支持排除列表（编辑场景）
  - 完善SnInputDialog组件验证逻辑
  - 添加详细文档

## ✅ 验证结果

- [x] 新增时重复SN码被拒绝
- [x] 编辑时可保留原SN码
- [x] 编辑时不能录入其他订单的SN码
- [x] 扫描模式快速录入正常
- [x] 数据库层面唯一约束生效
- [x] 错误提示友好清晰

---

**状态：** ✅ 已完成并测试

**测试日期：** 2025-11-28

**负责人：** AI Assistant

