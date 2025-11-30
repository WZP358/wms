package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存详情导出视图对象
 *
 * @author zcc
 * @date 2024-11-28
 */
@Data
@ExcelIgnoreUnannotated
public class InventoryDetailExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 仓库名称
     */
    @ExcelProperty(value = "仓库名称")
    private String warehouseName;

    /**
     * 库区名称
     */
    @ExcelProperty(value = "库区名称")
    private String areaName;

    /**
     * 商品编号
     */
    @ExcelProperty(value = "商品编号")
    private String itemCode;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String itemName;

    /**
     * 规格编号
     */
    @ExcelProperty(value = "规格编号")
    private String skuCode;

    /**
     * 规格名称
     */
    @ExcelProperty(value = "规格名称")
    private String skuName;

    /**
     * 库存数量
     */
    @ExcelProperty(value = "库存数量")
    private Integer quantity;

    /**
     * 批号
     */
    @ExcelProperty(value = "批号")
    private String batchNo;

    /**
     * 生产日期
     */
    @ExcelProperty(value = "生产日期")
    private LocalDateTime productionDate;

    /**
     * 过期日期
     */
    @ExcelProperty(value = "过期日期")
    private LocalDateTime expirationDate;

    /**
     * 入库日期
     */
    @ExcelProperty(value = "入库日期")
    private LocalDateTime createTime;

    /**
     * 金额
     */
    @ExcelProperty(value = "金额")
    private BigDecimal amount;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;
}

