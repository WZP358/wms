-- ============================================
-- 库存预警诊断SQL
-- 用于检查为什么库存预警显示为0
-- ============================================

-- 1. 检查有多少SKU设置了最低库存
SELECT 
    COUNT(*) as total_sku,
    COUNT(min_stock) as sku_with_min_stock,
    COUNT(CASE WHEN min_stock > 0 THEN 1 END) as sku_with_min_stock_gt_zero,
    COUNT(CASE WHEN min_stock IS NULL OR min_stock = 0 THEN 1 END) as sku_without_min_stock
FROM wms_item_sku;

-- 2. 检查库存和最低库存的对比情况
SELECT 
    item.item_name as '商品名称',
    sku.sku_name as '规格名称',
    sku.min_stock as '最低库存',
    inv.quantity as '当前库存',
    CASE 
        WHEN sku.min_stock IS NULL OR sku.min_stock = 0 THEN '未设置最低库存'
        WHEN inv.quantity < sku.min_stock THEN '库存不足（预警）'
        ELSE '库存充足'
    END as '状态',
    wh.warehouse_name as '仓库',
    area.area_name as '库区'
FROM wms_inventory inv
INNER JOIN wms_item_sku sku ON inv.sku_id = sku.id
INNER JOIN wms_item item ON sku.item_id = item.id
LEFT JOIN wms_warehouse wh ON inv.warehouse_id = wh.id
LEFT JOIN wms_area area ON inv.area_id = area.id
ORDER BY 
    CASE 
        WHEN sku.min_stock IS NULL OR sku.min_stock = 0 THEN 1
        WHEN inv.quantity < sku.min_stock THEN 0
        ELSE 2
    END,
    item.item_name,
    sku.sku_name
LIMIT 50;

-- 3. 统计应该预警的数量（按查询条件）
SELECT 
    COUNT(DISTINCT CONCAT(inv.sku_id, '-', inv.warehouse_id, '-', inv.area_id)) as warning_count
FROM wms_inventory inv
INNER JOIN wms_item_sku sku ON inv.sku_id = sku.id
WHERE sku.min_stock IS NOT NULL 
  AND sku.min_stock > 0 
  AND inv.quantity < sku.min_stock;

-- 4. 查看所有应该预警的明细
SELECT 
    item.item_name as '商品名称',
    item.item_code as '商品编号',
    sku.sku_name as '规格名称',
    sku.sku_code as '规格编号',
    wh.warehouse_name as '仓库',
    area.area_name as '库区',
    inv.quantity as '当前库存',
    sku.min_stock as '最低库存',
    (inv.quantity - sku.min_stock) as '库存差额'
FROM wms_inventory inv
INNER JOIN wms_item_sku sku ON inv.sku_id = sku.id
INNER JOIN wms_item item ON sku.item_id = item.id
LEFT JOIN wms_warehouse wh ON inv.warehouse_id = wh.id
LEFT JOIN wms_area area ON inv.area_id = area.id
WHERE sku.min_stock IS NOT NULL 
  AND sku.min_stock > 0 
  AND inv.quantity < sku.min_stock
ORDER BY (inv.quantity - sku.min_stock) ASC, item.item_name, sku.sku_name;

-- 5. 建议：为没有设置最低库存的SKU设置一个默认值（可选，仅用于测试）
-- 注意：这个SQL只是示例，实际使用时请根据业务需求修改
/*
UPDATE wms_item_sku 
SET min_stock = 10 
WHERE min_stock IS NULL OR min_stock = 0
LIMIT 10;  -- 先更新10条测试
*/

