package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;

/**
 * 路径记录业务对象
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PathRecordBo extends BaseEntity {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 地图配置ID
     */
    @NotNull(message = "地图配置ID不能为空", groups = {AddGroup.class})
    private Long mapId;

    /**
     * 路径名称
     */
    private String pathName;

    /**
     * 目标库区（JSON数组）
     */
    @NotNull(message = "目标库区不能为空", groups = {AddGroup.class})
    private String targetAreas;

    /**
     * 路径结果（JSON）
     */
    @NotNull(message = "路径结果不能为空", groups = {AddGroup.class})
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
}

