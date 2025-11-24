package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;

import java.util.List;

/**
 * 仓库地图配置业务对象
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseMapBo extends BaseEntity {

    /**
     * 地图配置ID
     */
    @NotNull(message = "地图配置ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 仓库ID
     */
    @NotNull(message = "仓库ID不能为空", groups = {AddGroup.class})
    private Long warehouseId;

    /**
     * 地图名称
     */
    @NotNull(message = "地图名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String mapName;

    /**
     * 地图行数
     */
    @NotNull(message = "地图行数不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer rows;

    /**
     * 地图列数
     */
    @NotNull(message = "地图列数不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer cols;

    /**
     * 格子大小
     */
    private Integer cellSize;

    /**
     * 格子间隙
     */
    private Integer gap;

    /**
     * 地图配置JSON（包含格子和库区信息）
     */
    private String mapConfig;

    /**
     * 库区配置JSON
     */
    private String areasConfig;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}

