package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.vo.SerialNumberVo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * 序列号/SN码对象 wms_serial_number
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_serial_number")
@AutoMapper(target = SerialNumberVo.class)
public class SerialNumber extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "sn_id")
    private Long snId;

    /**
     * SN码/序列号
     */
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

