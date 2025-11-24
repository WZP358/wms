-- 仓库地图配置表
DROP TABLE IF EXISTS `wms_warehouse_map`;
CREATE TABLE `wms_warehouse_map` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地图配置ID',
    `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
    `map_name` varchar(100) NOT NULL COMMENT '地图名称',
    `rows` int NOT NULL DEFAULT '10' COMMENT '地图行数',
    `cols` int NOT NULL DEFAULT '15' COMMENT '地图列数',
    `cell_size` int DEFAULT '50' COMMENT '格子大小(px)',
    `gap` int DEFAULT '5' COMMENT '格子间隙(px)',
    `map_config` text COMMENT '地图配置JSON',
    `areas_config` text COMMENT '库区配置JSON',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库地图配置表';

-- 路径记录表
DROP TABLE IF EXISTS `wms_path_record`;
CREATE TABLE `wms_path_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `map_id` bigint NOT NULL COMMENT '地图配置ID',
    `path_name` varchar(100) DEFAULT NULL COMMENT '路径名称',
    `target_areas` varchar(500) NOT NULL COMMENT '目标库区（JSON数组）',
    `path_result` text NOT NULL COMMENT '路径结果（JSON）',
    `total_distance` decimal(10, 2) DEFAULT NULL COMMENT '总距离',
    `algorithm_params` varchar(500) DEFAULT NULL COMMENT '算法参数（JSON）',
    `calculate_time` bigint DEFAULT NULL COMMENT '计算耗时（毫秒）',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_map_id` (`map_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径记录表';

-- 插入示例数据
INSERT INTO `wms_warehouse_map` (`id`, `warehouse_id`, `map_name`, `rows`, `cols`, `cell_size`, `gap`, `map_config`, `areas_config`, `remark`, `status`, `create_by`, `create_time`) 
VALUES 
(1, 1, '默认地图', 10, 15, 50, 5, NULL, NULL, '系统默认地图配置', '0', 'admin', NOW());

-- 菜单 SQL
-- 仓库地图菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('仓库地图', '2000', 6, 'map', 'wms/warehouseMap', 1, 0, 'C', '0', '0', 'wms:map:list', 'map', 'admin', NOW(), '', NULL, '仓库地图配置菜单');

-- 获取刚插入的菜单ID（假设为最新的ID）
SET @menu_id = LAST_INSERT_ID();

-- 仓库地图子菜单权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
('仓库地图查询', @menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'wms:map:query', '#', 'admin', NOW(), '', NULL, ''),
('仓库地图新增', @menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'wms:map:add', '#', 'admin', NOW(), '', NULL, ''),
('仓库地图修改', @menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'wms:map:edit', '#', 'admin', NOW(), '', NULL, ''),
('仓库地图删除', @menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'wms:map:remove', '#', 'admin', NOW(), '', NULL, '');

