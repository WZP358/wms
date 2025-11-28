package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 序列号/SN码业务对象 wms_serial_number
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SerialNumberBo extends BaseEntity {

    /**
     * 主键ID
     */
    private Long snId;

    /**
     * SN码/序列号
     */
    @NotBlank(message = "SN码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String snCode;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 物品编号
     */
    private String itemNo;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 绑定状态 0-未绑定 1-已绑定
     */
    private String bindStatus;

    /**
     * 入库时间
     */
    private Date inboundTime;

    /**
     * 入库单号
     */
    private String receiptOrderNo;

    /**
     * 出库单号
     */
    private String shipmentOrderNo;

    /**
     * 当前位置（仓库ID）
     */
    private Long warehouseId;

    /**
     * 当前位置（货架编号）
     */
    private String rackNo;

    /**
     * 备注
     */
    private String remark;
}


