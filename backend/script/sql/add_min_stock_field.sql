-- 为wms_item_sku表添加min_stock字段（最低库存）
ALTER TABLE `wms_item_sku` 
ADD COLUMN `min_stock` decimal(20, 2) NULL DEFAULT NULL COMMENT '最低库存' AFTER `selling_price`;

