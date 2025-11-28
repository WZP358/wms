-- 添加商品管理菜单
-- 菜单ID: 1813820131794837506
-- 父菜单: 基础资料 (1808758090157985794)
-- 路径: wms/basic/item/index

INSERT INTO `sys_menu` (
    `menu_id`,
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `query_param`,
    `is_frame`,
    `is_cache`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `update_by`,
    `update_time`,
    `remark`
) VALUES (
    1813820131794837506,
    '商品管理',
    1808758090157985794,
    4,
    'item',
    'wms/basic/item/index',
    NULL,
    0,
    0,
    'C',
    '1',
    '1',
    'wms:item:list',
    'documentation',
    'admin',
    NOW(),
    'admin',
    NOW(),
    '商品管理菜单'
) ON DUPLICATE KEY UPDATE
    `menu_name` = '商品管理',
    `parent_id` = 1808758090157985794,
    `order_num` = 4,
    `path` = 'item',
    `component` = 'wms/basic/item/index',
    `perms` = 'wms:item:list',
    `icon` = 'documentation',
    `update_by` = 'admin',
    `update_time` = NOW(),
    `remark` = '商品管理菜单';

