package com.ruoyi.wms.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存预警视图对象
 *
 * @author zcc
 * @date 2024-11-23
 */
@Data
public class InventoryWarningVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 当前库存
     */
    private Integer currentQuantity;

    /**
     * 最低库存
     */
    private Integer minStock;

    /**
     * 库存差额（当前库存 - 最低库存）
     */
    private Integer stockDifference;
}

