package com.ruoyi.wms.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 仓库地图配置视图对象
 *
 * @author ruoyi
 */
@Data
public class WarehouseMapVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地图配置ID
     */
    private Long id;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 地图名称
     */
    private String mapName;

    /**
     * 地图行数
     */
    private Integer rows;

    /**
     * 地图列数
     */
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
     * 地图配置JSON
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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}

