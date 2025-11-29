-- ============================================
-- 创建库存预警测试数据
-- 为部分SKU设置最低库存，并确保有库存低于最低库存的情况
-- ============================================

-- 1. 为部分SKU设置最低库存（如果还没有设置）
-- 步骤1：先查询出要更新的SKU ID和对应的最低库存值（限制10条）
-- 执行这个查询，查看结果，确认要更新的数据
SELECT 
    inv.sku_id,
    GREATEST(ROUND(MAX(inv.quantity) * 1.5, 2), 10) as new_min_stock,
    MAX(inv.quantity) as current_quantity
FROM wms_inventory inv
INNER JOIN wms_item_sku s ON inv.sku_id = s.id
WHERE (s.min_stock IS NULL OR s.min_stock = 0)
  AND inv.quantity > 0
GROUP BY inv.sku_id
LIMIT 10;

-- 步骤2：根据步骤1的结果，手动更新（推荐方式）
-- 将下面的ID替换为步骤1查询出的实际SKU ID
-- UPDATE wms_item_sku 
-- SET min_stock = 150  -- 替换为步骤1中查询出的new_min_stock值
-- WHERE id = 1829398702011904001;  -- 替换为步骤1中查询出的sku_id

-- 或者使用批量更新（如果MySQL版本支持，且步骤1的结果不多）
-- 注意：需要根据步骤1的实际结果修改下面的值
/*
UPDATE wms_item_sku 
SET min_stock = CASE id
    WHEN 1829398702011904001 THEN 150
    WHEN 1829398702011904002 THEN 200
    -- 添加更多SKU...
    ELSE min_stock
END
WHERE id IN (1829398702011904001, 1829398702011904002);
*/

-- 方法2：简单方式 - 更新所有符合条件的SKU（不限制数量）
-- 注意：这会更新所有没有设置最低库存且有库存的SKU
UPDATE wms_item_sku sku
SET sku.min_stock = (
    SELECT GREATEST(ROUND(MAX(inv.quantity) * 1.5, 2), 10)
    FROM wms_inventory inv
    WHERE inv.sku_id = sku.id
      AND inv.quantity > 0
)
WHERE (sku.min_stock IS NULL OR sku.min_stock = 0)
  AND EXISTS (
      SELECT 1 
      FROM wms_inventory inv 
      WHERE inv.sku_id = sku.id 
        AND inv.quantity > 0
  );

-- 2. 或者手动为特定SKU设置最低库存（示例）
-- 假设某个SKU当前库存是100，设置最低库存为150，这样就会产生预警
-- UPDATE wms_item_sku 
-- SET min_stock = 150 
-- WHERE id = 1829398702011904001;  -- 替换为实际的SKU ID

-- 3. 验证预警数据
SELECT 
    item.item_name as '商品名称',
    sku.sku_name as '规格名称',
    inv.quantity as '当前库存',
    sku.min_stock as '最低库存',
    (inv.quantity - sku.min_stock) as '库存差额',
    CASE 
        WHEN inv.quantity < sku.min_stock THEN '预警'
        ELSE '正常'
    END as '状态'
FROM wms_inventory inv
INNER JOIN wms_item_sku sku ON inv.sku_id = sku.id
INNER JOIN wms_item item ON sku.item_id = item.id
WHERE sku.min_stock IS NOT NULL 
  AND sku.min_stock > 0
ORDER BY (inv.quantity - sku.min_stock) ASC
LIMIT 20;

