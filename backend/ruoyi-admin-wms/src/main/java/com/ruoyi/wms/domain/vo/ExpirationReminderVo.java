package com.ruoyi.wms.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 到期提醒视图对象
 *
 * @author zcc
 * @date 2024-11-23
 */
@Data
public class ExpirationReminderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 库存详情ID
     */
    private Long id;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 商品编码
     */
    private String itemCode;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 规格编码
     */
    private String skuCode;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 库区ID
     */
    private Long areaId;

    /**
     * 库区名称
     */
    private String areaName;

    /**
     * 批号
     */
    private String batchNo;

    /**
     * 生产日期
     */
    private LocalDateTime productionDate;

    /**
     * 过期时间
     */
    private LocalDateTime expirationDate;

    /**
     * 剩余数量
     */
    private Integer remainQuantity;

    /**
     * 距离过期的天数
     */
    private Long daysToExpire;
}

