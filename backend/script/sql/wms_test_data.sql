-- ============================================
-- WMS系统测试数据生成脚本
-- 说明：为所有WMS业务表生成足够的测试数据（排除若依框架自带的sys_表）
-- 生成时间：2025-11-27
-- ============================================

-- 设置变量
SET @admin_user = 'admin';
SET @current_time = NOW(3);

-- 设置会话级别的字符集排序规则，避免排序规则冲突
SET collation_connection = 'utf8mb4_general_ci';
SET collation_database = 'utf8mb4_general_ci';
SET collation_server = 'utf8mb4_general_ci';

-- ============================================
-- 1. 仓库表 (wms_warehouse) - 补充数据
-- ============================================
INSERT INTO `wms_warehouse` (`warehouse_code`, `warehouse_name`, `remark`, `order_num`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('WH004', '北京中央仓库', '位于北京市朝阳区，主要存储电子产品', 3, @admin_user, @current_time, @admin_user, @current_time),
('WH005', '上海浦东仓库', '位于上海市浦东新区，主要存储日用品', 4, @admin_user, @current_time, @admin_user, @current_time),
('WH006', '广州白云仓库', '位于广州市白云区，主要存储服装', 5, @admin_user, @current_time, @admin_user, @current_time),
('WH007', '深圳南山仓库', '位于深圳市南山区，主要存储数码产品', 6, @admin_user, @current_time, @admin_user, @current_time),
('WH008', '成都武侯仓库', '位于成都市武侯区，主要存储食品', 7, @admin_user, @current_time, @admin_user, @current_time),
('WH009', '杭州西湖仓库', '位于杭州市西湖区，主要存储图书', 8, @admin_user, @current_time, @admin_user, @current_time),
('WH010', '武汉江汉仓库', '位于武汉市江汉区，主要存储医疗器械', 9, @admin_user, @current_time, @admin_user, @current_time);

-- ============================================
-- 2. 库区表 (wms_area) - 补充数据
-- ============================================
-- 为每个新仓库创建3-5个库区
INSERT INTO `wms_area` (`area_code`, `area_name`, `warehouse_id`, `remark`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
-- WH004的库区
('WH004.1', '库区1', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH004'), '属于北京中央仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH004.2', '库区2', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH004'), '属于北京中央仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH004.3', '库区3', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH004'), '属于北京中央仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH004.4', '库区4', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH004'), '属于北京中央仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
-- WH005的库区
('WH005.1', '库区1', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH005'), '属于上海浦东仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH005.2', '库区2', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH005'), '属于上海浦东仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH005.3', '库区3', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH005'), '属于上海浦东仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
-- WH006的库区
('WH006.1', '库区1', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH006'), '属于广州白云仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH006.2', '库区2', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH006'), '属于广州白云仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH006.3', '库区3', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH006'), '属于广州白云仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH006.4', '库区4', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH006'), '属于广州白云仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH006.5', '库区5', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH006'), '属于广州白云仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
-- WH007的库区
('WH007.1', '库区1', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH007'), '属于深圳南山仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH007.2', '库区2', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH007'), '属于深圳南山仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH007.3', '库区3', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH007'), '属于深圳南山仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
-- WH008的库区
('WH008.1', '库区1', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH008'), '属于成都武侯仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH008.2', '库区2', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH008'), '属于成都武侯仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH008.3', '库区3', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH008'), '属于成都武侯仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH008.4', '库区4', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH008'), '属于成都武侯仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
-- WH009的库区
('WH009.1', '库区1', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH009'), '属于杭州西湖仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH009.2', '库区2', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH009'), '属于杭州西湖仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH009.3', '库区3', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH009'), '属于杭州西湖仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
-- WH010的库区
('WH010.1', '库区1', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH010'), '属于武汉江汉仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH010.2', '库区2', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH010'), '属于武汉江汉仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH010.3', '库区3', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH010'), '属于武汉江汉仓库', 0, @admin_user, @current_time, @admin_user, @current_time),
('WH010.4', '库区4', (SELECT id FROM wms_warehouse WHERE warehouse_code = 'WH010'), '属于武汉江汉仓库', 0, @admin_user, @current_time, @admin_user, @current_time);

-- 为现有仓库补充库区
INSERT INTO `wms_area` (`area_code`, `area_name`, `warehouse_id`, `remark`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('1.4', '库区4', 1992782506943336450, '补充库区', 0, @admin_user, @current_time, @admin_user, @current_time),
('1.5', '库区5', 1992782506943336450, '补充库区', 0, @admin_user, @current_time, @admin_user, @current_time),
('2.4', '库区4', 1992782642801037314, '补充库区', 0, @admin_user, @current_time, @admin_user, @current_time),
('2.5', '库区5', 1992782642801037314, '补充库区', 0, @admin_user, @current_time, @admin_user, @current_time),
('3.4', '库区4', 1992782691492712450, '补充库区', 0, @admin_user, @current_time, @admin_user, @current_time),
('3.5', '库区5', 1992782691492712450, '补充库区', 0, @admin_user, @current_time, @admin_user, @current_time);

-- ============================================
-- 3. 往来单位表 (wms_merchant) - 补充数据
-- ============================================
INSERT INTO `wms_merchant` (`merchant_code`, `merchant_name`, `merchant_type`, `merchant_level`, `bank_name`, `bank_account`, `address`, `mobile`, `tel`, `contact_person`, `email`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('M0004', '北京科技有限公司', 1, 'A', '中国工商银行', '6222021234567890123', '北京市海淀区中关村大街1号', '13800138004', '010-12345678', '张经理', 'zhang@example.com', '主要供应商', @admin_user, @current_time, @admin_user, @current_time),
('M0005', '上海贸易有限公司', 2, 'A', '中国建设银行', '6222021234567890124', '上海市黄浦区南京东路100号', '13800138005', '021-12345678', '李经理', 'li@example.com', '主要客户', @admin_user, @current_time, @admin_user, @current_time),
('M0006', '广州物流有限公司', 3, 'B', '中国农业银行', '6222021234567890125', '广州市天河区天河路200号', '13800138006', '020-12345678', '王经理', 'wang@example.com', '物流合作伙伴', @admin_user, @current_time, @admin_user, @current_time),
('M0007', '深圳电子有限公司', 1, 'A', '招商银行', '6222021234567890126', '深圳市南山区科技园', '13800138007', '0755-12345678', '赵经理', 'zhao@example.com', '电子产品供应商', @admin_user, @current_time, @admin_user, @current_time),
('M0008', '成都食品有限公司', 1, 'B', '中国银行', '6222021234567890127', '成都市锦江区春熙路50号', '13800138008', '028-12345678', '刘经理', 'liu@example.com', '食品供应商', @admin_user, @current_time, @admin_user, @current_time),
('M0009', '杭州服装有限公司', 2, 'A', '交通银行', '6222021234567890128', '杭州市上城区解放路88号', '13800138009', '0571-12345678', '陈经理', 'chen@example.com', '服装客户', @admin_user, @current_time, @admin_user, @current_time),
('M0010', '武汉医疗有限公司', 1, 'A', '民生银行', '6222021234567890129', '武汉市武昌区中南路99号', '13800138010', '027-12345678', '周经理', 'zhou@example.com', '医疗器械供应商', @admin_user, @current_time, @admin_user, @current_time),
('M0011', '西安建材有限公司', 1, 'B', '浦发银行', '6222021234567890130', '西安市雁塔区小寨东路1号', '13800138011', '029-12345678', '吴经理', 'wu@example.com', '建材供应商', @admin_user, @current_time, @admin_user, @current_time),
('M0012', '南京化工有限公司', 1, 'A', '光大银行', '6222021234567890131', '南京市鼓楼区中山路200号', '13800138012', '025-12345678', '郑经理', 'zheng@example.com', '化工产品供应商', @admin_user, @current_time, @admin_user, @current_time),
('M0013', '重庆机械有限公司', 2, 'B', '华夏银行', '6222021234567890132', '重庆市渝中区解放碑1号', '13800138013', '023-12345678', '孙经理', 'sun@example.com', '机械设备客户', @admin_user, @current_time, @admin_user, @current_time);

-- ============================================
-- 4. 商品品牌表 (wms_item_brand) - 补充数据
-- ============================================
INSERT INTO `wms_item_brand` (`brand_name`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('联想', @admin_user, @current_time, @admin_user, @current_time),
('戴尔', @admin_user, @current_time, @admin_user, @current_time),
('三星', @admin_user, @current_time, @admin_user, @current_time),
('OPPO', @admin_user, @current_time, @admin_user, @current_time),
('vivo', @admin_user, @current_time, @admin_user, @current_time),
('美的', @admin_user, @current_time, @admin_user, @current_time),
('格力', @admin_user, @current_time, @admin_user, @current_time),
('海尔', @admin_user, @current_time, @admin_user, @current_time),
('海信', @admin_user, @current_time, @admin_user, @current_time),
('TCL', @admin_user, @current_time, @admin_user, @current_time);

-- ============================================
-- 5. 物料类型表 (wms_item_category) - 补充数据
-- ============================================
INSERT INTO `wms_item_category` (`parent_id`, `category_name`, `order_num`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
(0, '办公用品', 7, '1', @admin_user, @current_time, @admin_user, @current_time),
(0, '汽车用品', 8, '1', @admin_user, @current_time, @admin_user, @current_time),
(0, '运动用品', 9, '1', @admin_user, @current_time, @admin_user, @current_time),
(0, '美妆用品', 10, '1', @admin_user, @current_time, @admin_user, @current_time),
(0, '母婴用品', 11, '1', @admin_user, @current_time, @admin_user, @current_time),
(1828405743737016322, '洗衣机', 2, '1', @admin_user, @current_time, @admin_user, @current_time),
(1828405743737016322, '空调', 3, '1', @admin_user, @current_time, @admin_user, @current_time),
(1829397860466749441, '蔬菜', 2, '1', @admin_user, @current_time, @admin_user, @current_time),
(1829397860466749441, '肉类', 3, '1', @admin_user, @current_time, @admin_user, @current_time);

-- ============================================
-- 6. 物料表 (wms_item) - 补充数据
-- ============================================
INSERT INTO `wms_item` (`item_code`, `item_name`, `item_category`, `unit`, `item_brand`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('ITEM0011', '联想ThinkPad X1', '1828365043024695297', '台', (SELECT id FROM wms_item_brand WHERE brand_name = '联想' LIMIT 1), '商务笔记本电脑', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0012', '戴尔XPS 13', '1828365043024695297', '台', (SELECT id FROM wms_item_brand WHERE brand_name = '戴尔' LIMIT 1), '超薄笔记本电脑', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0013', '三星Galaxy S24', '1828364988754595841', '台', (SELECT id FROM wms_item_brand WHERE brand_name = '三星' LIMIT 1), '智能手机', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0014', 'OPPO Find X7', '1828364988754595841', '台', (SELECT id FROM wms_item_brand WHERE brand_name = 'OPPO' LIMIT 1), '旗舰手机', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0015', '美的空调KFR-35', '1828405743737016322', '台', (SELECT id FROM wms_item_brand WHERE brand_name = '美的' LIMIT 1), '1.5匹空调', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0016', '格力空调KFR-50', '1828405743737016322', '台', (SELECT id FROM wms_item_brand WHERE brand_name = '格力' LIMIT 1), '2匹空调', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0017', '海尔洗衣机XQG100', '1828405743737016322', '台', (SELECT id FROM wms_item_brand WHERE brand_name = '海尔' LIMIT 1), '10公斤洗衣机', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0018', '海信电视65E8K', '1828405743737016322', '台', (SELECT id FROM wms_item_brand WHERE brand_name = '海信' LIMIT 1), '65寸4K电视', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0019', 'TCL电视75Q10G', '1828405743737016322', '台', (SELECT id FROM wms_item_brand WHERE brand_name = 'TCL' LIMIT 1), '75寸QLED电视', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0020', 'vivo X100', '1828364988754595841', '台', (SELECT id FROM wms_item_brand WHERE brand_name = 'vivo' LIMIT 1), '拍照手机', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0021', '办公椅', (SELECT id FROM wms_item_category WHERE category_name = '办公用品' LIMIT 1), '把', NULL, '人体工学办公椅', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0022', '打印机纸A4', (SELECT id FROM wms_item_category WHERE category_name = '办公用品' LIMIT 1), '包', NULL, '500张/包', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0023', '汽车轮胎', (SELECT id FROM wms_item_category WHERE category_name = '汽车用品' LIMIT 1), '条', NULL, '205/55R16', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0024', '汽车机油', (SELECT id FROM wms_item_category WHERE category_name = '汽车用品' LIMIT 1), '升', NULL, '5W-30全合成', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0025', '跑步机', (SELECT id FROM wms_item_category WHERE category_name = '运动用品' LIMIT 1), '台', NULL, '家用跑步机', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0026', '哑铃', (SELECT id FROM wms_item_category WHERE category_name = '运动用品' LIMIT 1), '对', NULL, '可调节哑铃', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0027', '口红', (SELECT id FROM wms_item_category WHERE category_name = '美妆用品' LIMIT 1), '支', NULL, '正红色', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0028', '面膜', (SELECT id FROM wms_item_category WHERE category_name = '美妆用品' LIMIT 1), '片', NULL, '补水面膜', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0029', '婴儿奶粉', (SELECT id FROM wms_item_category WHERE category_name = '母婴用品' LIMIT 1), '罐', NULL, '1段奶粉', @admin_user, @current_time, @admin_user, @current_time),
('ITEM0030', '纸尿裤', (SELECT id FROM wms_item_category WHERE category_name = '母婴用品' LIMIT 1), '包', NULL, 'L码', @admin_user, @current_time, @admin_user, @current_time);

-- ============================================
-- 7. SKU信息表 (wms_item_sku) - 补充数据
-- ============================================
-- 为每个物料创建2-3个SKU
INSERT INTO `wms_item_sku` (`sku_name`, `item_id`, `barcode`, `sku_code`, `length`, `width`, `height`, `gross_weight`, `net_weight`, `cost_price`, `selling_price`, `min_stock`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    CASE 
        WHEN i.item_name LIKE '%手机%' THEN CONCAT('颜色-', spec.name)
        WHEN i.item_name LIKE '%电脑%' THEN CONCAT('配置-', spec.name)
        WHEN i.item_name LIKE '%电视%' THEN CONCAT('尺寸-', spec.name)
        ELSE CONCAT('规格-', spec.name)
    END as sku_name,
    i.id as item_id,
    CONCAT('BC', LPAD(i.id, 10, '0'), LPAD(spec.id, 3, '0')) as barcode,
    CONCAT('SKU', LPAD(i.id, 6, '0'), LPAD(spec.id, 3, '0')) as sku_code,
    ROUND(20 + RAND() * 50, 1) as length,
    ROUND(10 + RAND() * 30, 1) as width,
    ROUND(5 + RAND() * 20, 1) as height,
    ROUND(0.5 + RAND() * 5, 3) as gross_weight,
    ROUND(0.3 + RAND() * 4, 3) as net_weight,
    ROUND(100 + RAND() * 5000, 2) as cost_price,
    ROUND(150 + RAND() * 6000, 2) as selling_price,
    ROUND(10 + RAND() * 100, 2) as min_stock,
    @admin_user,
    @current_time,
    @admin_user,
    @current_time
FROM wms_item i
CROSS JOIN (
    SELECT '黑色' as name, 1 as id UNION SELECT '白色', 2 UNION SELECT '银色', 3 UNION SELECT '金色', 4 UNION SELECT '蓝色', 5
) spec
WHERE i.item_code >= 'ITEM0011'
LIMIT 50;

-- ============================================
-- 8. 入库单表 (wms_receipt_order) - 补充数据
-- ============================================
-- 生成30条入库单数据
INSERT INTO `wms_receipt_order` (`receipt_order_no`, `receipt_order_type`, `merchant_id`, `order_no`, `total_quantity`, `payable_amount`, `receipt_order_status`, `warehouse_id`, `area_id`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    CONCAT('RK', DATE_FORMAT(DATE_SUB(@current_time, INTERVAL n.id DAY), '%y%m%d'), LPAD(n.id, 3, '0')) as receipt_order_no,
    FLOOR(1 + RAND() * 5) as receipt_order_type,
    (SELECT id FROM wms_merchant WHERE merchant_type = 1 ORDER BY RAND() LIMIT 1) as merchant_id,
    CONCAT('PO', DATE_FORMAT(DATE_SUB(@current_time, INTERVAL n.id DAY), '%Y%m%d'), LPAD(n.id, 4, '0')) as order_no,
    ROUND(10 + RAND() * 500, 2) as total_quantity,
    ROUND(1000 + RAND() * 50000, 2) as payable_amount,
    CASE WHEN RAND() > 0.3 THEN 1 ELSE 0 END as receipt_order_status,
    (SELECT id FROM wms_warehouse ORDER BY RAND() LIMIT 1) as warehouse_id,
    (SELECT id FROM wms_area ORDER BY RAND() LIMIT 1) as area_id,
    CONCAT('测试入库单', n.id) as remark,
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY),
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY)
FROM (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30
) n;

-- ============================================
-- 9. 入库单详情表 (wms_receipt_order_detail) - 补充数据
-- ============================================
-- 为每个入库单生成2-5条详情
INSERT INTO `wms_receipt_order_detail` (`receipt_order_id`, `sku_id`, `quantity`, `amount`, `batch_no`, `production_date`, `expiration_date`, `remark`, `warehouse_id`, `area_id`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    ro.id as receipt_order_id,
    (SELECT id FROM wms_item_sku ORDER BY RAND() LIMIT 1) as sku_id,
    ROUND(1 + RAND() * 100, 2) as quantity,
    ROUND(50 + RAND() * 5000, 2) as amount,
    CONCAT('BATCH', DATE_FORMAT(ro.create_time, '%Y%m%d'), LPAD(FLOOR(1 + RAND() * 999), 3, '0')) as batch_no,
    DATE_SUB(ro.create_time, INTERVAL FLOOR(30 + RAND() * 180) DAY) as production_date,
    DATE_ADD(ro.create_time, INTERVAL FLOOR(180 + RAND() * 365) DAY) as expiration_date,
    CONCAT('入库详情', detail.id) as remark,
    ro.warehouse_id,
    ro.area_id,
    @admin_user,
    ro.create_time,
    @admin_user,
    ro.create_time
FROM wms_receipt_order ro
CROSS JOIN (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
) detail
WHERE ro.receipt_order_no COLLATE utf8mb4_general_ci LIKE 'RK%'
LIMIT 100;

-- ============================================
-- 10. 出库单表 (wms_shipment_order) - 补充数据
-- ============================================
-- 生成30条出库单数据
INSERT INTO `wms_shipment_order` (`shipment_order_no`, `shipment_order_type`, `order_no`, `merchant_id`, `receivable_amount`, `total_quantity`, `shipment_order_status`, `warehouse_id`, `area_id`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    CONCAT('CK', DATE_FORMAT(DATE_SUB(@current_time, INTERVAL n.id DAY), '%y%m%d'), LPAD(n.id, 3, '0')) as shipment_order_no,
    FLOOR(1 + RAND() * 5) as shipment_order_type,
    CONCAT('SO', DATE_FORMAT(DATE_SUB(@current_time, INTERVAL n.id DAY), '%Y%m%d'), LPAD(n.id, 4, '0')) as order_no,
    (SELECT id FROM wms_merchant WHERE merchant_type = 2 ORDER BY RAND() LIMIT 1) as merchant_id,
    ROUND(500 + RAND() * 30000, 2) as receivable_amount,
    ROUND(5 + RAND() * 200, 2) as total_quantity,
    CASE WHEN RAND() > 0.2 THEN 1 ELSE 0 END as shipment_order_status,
    (SELECT id FROM wms_warehouse ORDER BY RAND() LIMIT 1) as warehouse_id,
    (SELECT id FROM wms_area ORDER BY RAND() LIMIT 1) as area_id,
    CONCAT('测试出库单', n.id) as remark,
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY),
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY)
FROM (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30
) n;

-- ============================================
-- 11. 出库单详情表 (wms_shipment_order_detail) - 补充数据
-- ============================================
-- 为每个出库单生成2-4条详情
INSERT INTO `wms_shipment_order_detail` (`shipment_order_id`, `sku_id`, `quantity`, `amount`, `warehouse_id`, `area_id`, `batch_no`, `production_date`, `expiration_date`, `inventory_detail_id`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    so.id as shipment_order_id,
    (SELECT id FROM wms_item_sku ORDER BY RAND() LIMIT 1) as sku_id,
    ROUND(1 + RAND() * 50, 2) as quantity,
    ROUND(30 + RAND() * 3000, 2) as amount,
    so.warehouse_id,
    so.area_id,
    CONCAT('BATCH', DATE_FORMAT(so.create_time, '%Y%m%d'), LPAD(FLOOR(1 + RAND() * 999), 3, '0')) as batch_no,
    DATE_SUB(so.create_time, INTERVAL FLOOR(30 + RAND() * 180) DAY) as production_date,
    DATE_ADD(so.create_time, INTERVAL FLOOR(180 + RAND() * 365) DAY) as expiration_date,
    (SELECT id FROM wms_inventory_detail ORDER BY RAND() LIMIT 1) as inventory_detail_id,
    CONCAT('出库详情', detail.id) as remark,
    @admin_user,
    so.create_time,
    @admin_user,
    so.create_time
FROM wms_shipment_order so
CROSS JOIN (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
) detail
WHERE so.shipment_order_no COLLATE utf8mb4_general_ci LIKE 'CK%'
LIMIT 90;

-- ============================================
-- 12. 移库单表 (wms_movement_order) - 补充数据
-- ============================================
-- 生成20条移库单数据
INSERT INTO `wms_movement_order` (`movement_order_no`, `source_warehouse_id`, `source_area_id`, `target_warehouse_id`, `target_area_id`, `movement_order_status`, `total_quantity`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    CONCAT('YK', DATE_FORMAT(DATE_SUB(@current_time, INTERVAL n.id DAY), '%y%m%d'), LPAD(n.id, 3, '0')) as movement_order_no,
    (SELECT id FROM wms_warehouse ORDER BY RAND() LIMIT 1) as source_warehouse_id,
    (SELECT id FROM wms_area ORDER BY RAND() LIMIT 1) as source_area_id,
    (SELECT id FROM wms_warehouse ORDER BY RAND() LIMIT 1) as target_warehouse_id,
    (SELECT id FROM wms_area ORDER BY RAND() LIMIT 1) as target_area_id,
    CASE WHEN RAND() > 0.3 THEN 1 ELSE 0 END as movement_order_status,
    ROUND(10 + RAND() * 200, 2) as total_quantity,
    CONCAT('测试移库单', n.id) as remark,
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY),
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY)
FROM (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) n;

-- ============================================
-- 13. 移库单详情表 (wms_movement_order_detail) - 补充数据
-- ============================================
-- 为每个移库单生成2-4条详情
INSERT INTO `wms_movement_order_detail` (`movement_order_id`, `sku_id`, `quantity`, `remark`, `batch_no`, `production_date`, `expiration_date`, `source_warehouse_id`, `source_area_id`, `target_warehouse_id`, `target_area_id`, `inventory_detail_id`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    mo.id as movement_order_id,
    (SELECT id FROM wms_item_sku ORDER BY RAND() LIMIT 1) as sku_id,
    ROUND(5 + RAND() * 50, 2) as quantity,
    CONCAT('移库详情', detail.id) as remark,
    CONCAT('BATCH', DATE_FORMAT(mo.create_time, '%Y%m%d'), LPAD(FLOOR(1 + RAND() * 999), 3, '0')) as batch_no,
    DATE_SUB(mo.create_time, INTERVAL FLOOR(30 + RAND() * 180) DAY) as production_date,
    DATE_ADD(mo.create_time, INTERVAL FLOOR(180 + RAND() * 365) DAY) as expiration_date,
    mo.source_warehouse_id,
    mo.source_area_id,
    mo.target_warehouse_id,
    mo.target_area_id,
    (SELECT id FROM wms_inventory_detail ORDER BY RAND() LIMIT 1) as inventory_detail_id,
    @admin_user,
    mo.create_time,
    @admin_user,
    mo.create_time
FROM wms_movement_order mo
CROSS JOIN (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
) detail
WHERE mo.movement_order_no COLLATE utf8mb4_general_ci LIKE 'YK%'
LIMIT 60;

-- ============================================
-- 14. 盘点单表 (wms_check_order) - 补充数据
-- ============================================
-- 生成20条盘点单数据
INSERT INTO `wms_check_order` (`check_order_no`, `check_order_status`, `check_order_total`, `warehouse_id`, `area_id`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    CONCAT('PD', DATE_FORMAT(DATE_SUB(@current_time, INTERVAL n.id DAY), '%y%m%d'), LPAD(n.id, 3, '0')) as check_order_no,
    CASE 
        WHEN RAND() > 0.7 THEN 1
        WHEN RAND() > 0.9 THEN -1
        ELSE 0
    END as check_order_status,
    ROUND(-50 + RAND() * 100, 2) as check_order_total,
    (SELECT id FROM wms_warehouse ORDER BY RAND() LIMIT 1) as warehouse_id,
    (SELECT id FROM wms_area ORDER BY RAND() LIMIT 1) as area_id,
    CONCAT('测试盘点单', n.id) as remark,
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY),
    @admin_user,
    DATE_SUB(@current_time, INTERVAL n.id DAY)
FROM (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) n;

-- ============================================
-- 15. 盘点单详情表 (wms_check_order_detail) - 补充数据
-- ============================================
-- 为每个盘点单生成3-6条详情
INSERT INTO `wms_check_order_detail` (`check_order_id`, `sku_id`, `quantity`, `check_quantity`, `warehouse_id`, `area_id`, `batch_no`, `production_date`, `expiration_date`, `receipt_time`, `inventory_detail_id`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    co.id as check_order_id,
    (SELECT id FROM wms_item_sku ORDER BY RAND() LIMIT 1) as sku_id,
    ROUND(10 + RAND() * 100, 2) as quantity,
    ROUND(10 + RAND() * 100 + (RAND() - 0.5) * 10, 2) as check_quantity,
    co.warehouse_id,
    co.area_id,
    CONCAT('BATCH', DATE_FORMAT(co.create_time, '%Y%m%d'), LPAD(FLOOR(1 + RAND() * 999), 3, '0')) as batch_no,
    DATE_SUB(co.create_time, INTERVAL FLOOR(30 + RAND() * 180) DAY) as production_date,
    DATE_ADD(co.create_time, INTERVAL FLOOR(180 + RAND() * 365) DAY) as expiration_date,
    DATE_SUB(co.create_time, INTERVAL FLOOR(1 + RAND() * 30) DAY) as receipt_time,
    (SELECT id FROM wms_inventory_detail ORDER BY RAND() LIMIT 1) as inventory_detail_id,
    CONCAT('盘点详情', detail.id) as remark,
    @admin_user,
    co.create_time,
    @admin_user,
    co.create_time
FROM wms_check_order co
CROSS JOIN (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
) detail
WHERE co.check_order_no COLLATE utf8mb4_general_ci LIKE 'PD%'
LIMIT 100;

-- ============================================
-- 16. 库存表 (wms_inventory) - 补充数据
-- ============================================
-- 为每个SKU在不同仓库/库区生成库存记录
INSERT INTO `wms_inventory` (`sku_id`, `warehouse_id`, `area_id`, `quantity`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT DISTINCT
    sku.id as sku_id,
    w.id as warehouse_id,
    a.id as area_id,
    ROUND(100 + RAND() * 1000, 2) as quantity,
    CONCAT('库存记录-', sku.sku_name) as remark,
    @admin_user,
    @current_time,
    @admin_user,
    @current_time
FROM wms_item_sku sku
CROSS JOIN wms_warehouse w
CROSS JOIN wms_area a
WHERE NOT EXISTS (
    SELECT 1 FROM wms_inventory inv 
    WHERE inv.sku_id = sku.id 
    AND inv.warehouse_id = w.id 
    AND inv.area_id = a.id
)
LIMIT 200;

-- ============================================
-- 17. 库存详情表 (wms_inventory_detail) - 补充数据
-- ============================================
-- 基于入库单生成库存详情
INSERT INTO `wms_inventory_detail` (`receipt_order_id`, `receipt_order_type`, `order_no`, `type`, `sku_id`, `warehouse_id`, `area_id`, `quantity`, `batch_no`, `production_date`, `expiration_date`, `amount`, `remark`, `remain_quantity`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT 
    rod.receipt_order_id,
    ro.receipt_order_type,
    ro.receipt_order_no as order_no,
    1 as type,
    rod.sku_id,
    rod.warehouse_id,
    rod.area_id,
    rod.quantity,
    rod.batch_no,
    rod.production_date,
    rod.expiration_date,
    rod.amount,
    CONCAT('库存详情-', ro.receipt_order_no) as remark,
    rod.quantity as remain_quantity,
    @admin_user,
    rod.create_time,
    @admin_user,
    rod.create_time
FROM wms_receipt_order_detail rod
INNER JOIN wms_receipt_order ro ON rod.receipt_order_id = ro.id
WHERE NOT EXISTS (
    SELECT 1 FROM wms_inventory_detail invd 
    WHERE invd.receipt_order_id = rod.receipt_order_id 
    AND invd.sku_id = rod.sku_id
    AND invd.batch_no COLLATE utf8mb4_general_ci = rod.batch_no COLLATE utf8mb4_general_ci
)
LIMIT 100;

-- ============================================
-- 18. 库存历史表 (wms_inventory_history) - 补充数据
-- ============================================
-- 基于入库单、出库单、移库单生成历史记录
INSERT INTO `wms_inventory_history` (`warehouse_id`, `area_id`, `sku_id`, `quantity`, `batch_no`, `production_date`, `expiration_date`, `amount`, `remark`, `order_id`, `order_no`, `order_type`, `create_time`)
SELECT 
    rod.warehouse_id,
    rod.area_id,
    rod.sku_id,
    rod.quantity,
    rod.batch_no,
    rod.production_date,
    rod.expiration_date,
    rod.amount,
    CONCAT('入库历史-', ro.receipt_order_no) as remark,
    ro.id as order_id,
    ro.receipt_order_no as order_no,
    1 as order_type,
    ro.create_time
FROM wms_receipt_order_detail rod
INNER JOIN wms_receipt_order ro ON rod.receipt_order_id = ro.id
WHERE NOT EXISTS (
    SELECT 1 FROM wms_inventory_history ih 
    WHERE ih.order_id = ro.id 
    AND ih.order_type = 1
)
LIMIT 50;

-- 出库历史
INSERT INTO `wms_inventory_history` (`warehouse_id`, `area_id`, `sku_id`, `quantity`, `batch_no`, `production_date`, `expiration_date`, `amount`, `remark`, `order_id`, `order_no`, `order_type`, `create_time`)
SELECT 
    sod.warehouse_id,
    sod.area_id,
    sod.sku_id,
    -sod.quantity as quantity,
    sod.batch_no,
    sod.production_date,
    sod.expiration_date,
    sod.amount,
    CONCAT('出库历史-', so.shipment_order_no) as remark,
    so.id as order_id,
    so.shipment_order_no as order_no,
    2 as order_type,
    so.create_time
FROM wms_shipment_order_detail sod
INNER JOIN wms_shipment_order so ON sod.shipment_order_id = so.id
WHERE NOT EXISTS (
    SELECT 1 FROM wms_inventory_history ih 
    WHERE ih.order_id = so.id 
    AND ih.order_type = 2
)
LIMIT 30;

-- 移库历史（源仓库减少）
INSERT INTO `wms_inventory_history` (`warehouse_id`, `area_id`, `sku_id`, `quantity`, `batch_no`, `production_date`, `expiration_date`, `amount`, `remark`, `order_id`, `order_no`, `order_type`, `create_time`)
SELECT 
    mov_detail.source_warehouse_id as warehouse_id,
    mov_detail.source_area_id as area_id,
    mov_detail.sku_id,
    -mov_detail.quantity as quantity,
    mov_detail.batch_no,
    mov_detail.production_date,
    mov_detail.expiration_date,
    NULL as amount,
    CONCAT('移库历史-', mo.movement_order_no, '-源') as remark,
    mo.id as order_id,
    mo.movement_order_no as order_no,
    3 as order_type,
    mo.create_time
FROM wms_movement_order_detail mov_detail
INNER JOIN wms_movement_order mo ON mov_detail.movement_order_id = mo.id
WHERE NOT EXISTS (
    SELECT 1 FROM wms_inventory_history ih 
    WHERE ih.order_id = mo.id 
    AND ih.order_type = 3
    AND ih.warehouse_id = mov_detail.source_warehouse_id
)
LIMIT 20;

-- 移库历史（目标仓库增加）
INSERT INTO `wms_inventory_history` (`warehouse_id`, `area_id`, `sku_id`, `quantity`, `batch_no`, `production_date`, `expiration_date`, `amount`, `remark`, `order_id`, `order_no`, `order_type`, `create_time`)
SELECT 
    mov_detail.target_warehouse_id as warehouse_id,
    mov_detail.target_area_id as area_id,
    mov_detail.sku_id,
    mov_detail.quantity,
    mov_detail.batch_no,
    mov_detail.production_date,
    mov_detail.expiration_date,
    NULL as amount,
    CONCAT('移库历史-', mo.movement_order_no, '-目标') as remark,
    mo.id as order_id,
    mo.movement_order_no as order_no,
    3 as order_type,
    mo.create_time
FROM wms_movement_order_detail mov_detail
INNER JOIN wms_movement_order mo ON mov_detail.movement_order_id = mo.id
WHERE NOT EXISTS (
    SELECT 1 FROM wms_inventory_history ih 
    WHERE ih.order_id = mo.id 
    AND ih.order_type = 3
    AND ih.warehouse_id = mov_detail.target_warehouse_id
)
LIMIT 20;

-- ============================================
-- 19. SN码表 (wms_serial_number) - 补充数据
-- ============================================
-- 为需要SN码的商品生成序列号（使用时间戳+item_id+sku_id+n.id确保唯一性）
INSERT INTO `wms_serial_number` (`sn_code`, `item_id`, `item_no`, `item_name`, `spec`, `bind_status`, `inbound_time`, `receipt_order_no`, `shipment_order_no`, `warehouse_id`, `rack_no`, `create_time`, `create_by`, `update_time`, `update_by`, `remark`)
SELECT 
    CONCAT('SN', DATE_FORMAT(@current_time, '%Y%m%d'), LPAD(
        ((i.id % 10000) * 10000 + (sku.id % 10000) + (n.id * 100) + (UNIX_TIMESTAMP(@current_time) % 100)) % 100000000,
        8, '0')) as sn_code,
    i.id as item_id,
    i.item_code as item_no,
    i.item_name,
    sku.sku_name as spec,
    CASE WHEN RAND() > 0.7 THEN '1' ELSE '0' END as bind_status,
    DATE_SUB(@current_time, INTERVAL FLOOR(RAND() * 30) DAY) as inbound_time,
    CASE WHEN RAND() > 0.5 THEN (SELECT receipt_order_no FROM wms_receipt_order ORDER BY RAND() LIMIT 1) ELSE NULL END as receipt_order_no,
    CASE WHEN RAND() > 0.8 THEN (SELECT shipment_order_no FROM wms_shipment_order ORDER BY RAND() LIMIT 1) ELSE NULL END as shipment_order_no,
    (SELECT id FROM wms_warehouse ORDER BY RAND() LIMIT 1) as warehouse_id,
    CONCAT('RACK', LPAD(FLOOR(1 + RAND() * 100), 3, '0')) as rack_no,
    @current_time,
    @admin_user,
    @current_time,
    @admin_user,
    CONCAT('SN码-', i.item_name) as remark
FROM wms_item i
INNER JOIN wms_item_sku sku ON sku.item_id = i.id
CROSS JOIN (
    SELECT 1 as id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30
    UNION SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40
    UNION SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
) n
WHERE (i.item_name COLLATE utf8mb4_general_ci LIKE '%手机%' OR i.item_name COLLATE utf8mb4_general_ci LIKE '%电脑%' OR i.item_name COLLATE utf8mb4_general_ci LIKE '%电视%')
AND NOT EXISTS (
    SELECT 1 FROM wms_serial_number sn 
    WHERE sn.sn_code = CONCAT('SN', DATE_FORMAT(@current_time, '%Y%m%d'), LPAD(
        ((i.id % 10000) * 10000 + (sku.id % 10000) + (n.id * 100) + (UNIX_TIMESTAMP(@current_time) % 100)) % 100000000,
        8, '0'))
)
LIMIT 200;

-- ============================================
-- 数据生成完成
-- ============================================
SELECT 'WMS测试数据生成完成！' as message;

