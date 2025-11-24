# 修复 MapStruct 转换器错误

## 错误信息

```
cannot find converter from Warehouse to WarehouseVo
cannot find converter from Area to AreaVo
cannot find converter from Merchant to MerchantVo
```

## 原因

项目使用了 MapStruct Plus（linpeilie）自动映射工具，但映射器类没有正确生成或编译。

## 解决方案

### 方案1：重新编译项目（推荐）

```bash
# 进入后端目录
cd backend

# 清理并重新编译
mvn clean compile

# 或者完整重新构建
mvn clean install -DskipTests
```

### 方案2：IDE 重新构建

1. **IntelliJ IDEA**：
   - 点击菜单：`Build` → `Rebuild Project`
   - 或者：`Build` → `Build Project`
   - 等待编译完成

2. **Eclipse**：
   - 右键项目
   - 选择 `Maven` → `Update Project`
   - 勾选 `Force Update of Snapshots/Releases`
   - 点击 OK

### 方案3：删除 target 目录

```bash
# 进入后端目录
cd backend

# 删除所有 target 目录
find . -name "target" -type d -exec rm -rf {} +

# 重新编译
mvn clean install -DskipTests
```

### 方案4：检查依赖

确保 `pom.xml` 中有正确的 MapStruct Plus 依赖：

```xml
<dependency>
    <groupId>io.github.linpeilie</groupId>
    <artifactId>mapstruct-plus-spring-boot-starter</artifactId>
</dependency>
```

## 验证修复

### 1. 检查生成的类

编译后，应该在以下位置找到生成的映射器类：

```
backend/ruoyi-admin-wms/target/generated-sources/annotations/
  └── com/ruoyi/wms/domain/entity/
      ├── WarehouseToWarehouseVoMapperImpl.java
      ├── AreaToAreaVoMapperImpl.java
      └── MerchantToMerchantVoMapperImpl.java
```

### 2. 测试 API

启动后端服务后测试：

```bash
# 测试仓库列表API
curl http://localhost:8080/wms/warehouse/listNoPage

# 测试库区列表API
curl http://localhost:8080/wms/area/listNoPage?warehouseId=1
```

应该返回 JSON 数据而不是错误。

## 常见问题

### Q1: 编译后仍然报错？

**解决方案**：
1. 完全关闭 IDE
2. 删除所有 target 目录
3. 删除 `.idea` 目录（IntelliJ）或 `.settings` 目录（Eclipse）
4. 重新导入项目
5. 重新编译

### Q2: Maven 编译很慢？

**解决方案**：
```bash
# 跳过测试，只编译
mvn clean compile -DskipTests

# 使用多线程编译
mvn clean compile -T 4
```

### Q3: 仍然无法解决？

**临时解决方案**：

修改 Service 实现类，手动转换而不使用自动映射：

```java
@Override
public List<WarehouseVo> queryList(WarehouseBo bo) {
    List<Warehouse> list = baseMapper.selectList(/* ... */);
    
    // 手动转换
    return list.stream().map(warehouse -> {
        WarehouseVo vo = new WarehouseVo();
        vo.setId(warehouse.getId());
        vo.setWarehouseCode(warehouse.getWarehouseCode());
        vo.setWarehouseName(warehouse.getWarehouseName());
        // ... 其他字段
        return vo;
    }).collect(Collectors.toList());
}
```

## 前端独立运行

如果暂时无法修复后端，前端仍然可以独立运行：

1. 前端会自动检测API错误
2. 自动使用本地存储的数据
3. 创建默认的仓库和库区
4. 所有核心功能正常工作

## 预防措施

### 1. 使用 Maven Wrapper

```bash
# 使用项目自带的 Maven
./mvnw clean install
```

### 2. CI/CD 中配置

```yaml
# .gitlab-ci.yml 或 .github/workflows/build.yml
build:
  script:
    - mvn clean compile
    - mvn package -DskipTests
```

### 3. 定期清理

```bash
# 定期清理 target 目录
mvn clean

# 清理本地 Maven 仓库中的快照
mvn dependency:purge-local-repository
```

## 参考文档

- MapStruct Plus 文档：https://mapstruct.plus/
- Spring Boot 文档：https://docs.spring.io/spring-boot/docs/current/reference/html/
- Maven 编译插件：https://maven.apache.org/plugins/maven-compiler-plugin/

---

**注意**：编译完成后，记得重启后端服务！

