package com.ruoyi.wms.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 路径记录视图对象
 *
 * @author ruoyi
 */
@Data
public class PathRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 地图配置ID
     */
    private Long mapId;

    /**
     * 地图名称
     */
    private String mapName;

    /**
     * 路径名称
     */
    private String pathName;

    /**
     * 目标库区（JSON数组）
     */
    private String targetAreas;

    /**
     * 路径结果（JSON）
     */
    private String pathResult;

    /**
     * 总距离
     */
    private Double totalDistance;

    /**
     * 算法参数（JSON）
     */
    private String algorithmParams;

    /**
     * 计算耗时（毫秒）
     */
    private Long calculateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;
}

