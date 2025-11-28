package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 序列号/SN码视图对象 wms_serial_number
 *
 * @author ruoyi
 */
@Data
@ExcelIgnoreUnannotated
public class SerialNumberVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "ID")
    private Long snId;

    /**
     * SN码/序列号
     */
    @ExcelProperty(value = "SN码")
    private String snCode;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 物品编号
     */
    @ExcelProperty(value = "物品编号")
    private String itemNo;

    /**
     * 物品名称
     */
    @ExcelProperty(value = "物品名称")
    private String itemName;

    /**
     * 规格型号
     */
    @ExcelProperty(value = "规格型号")
    private String spec;

    /**
     * 绑定状态
     */
    @ExcelProperty(value = "绑定状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "bind_status")
    private String bindStatus;

    /**
     * 入库时间
     */
    @ExcelProperty(value = "入库时间")
    private Date inboundTime;

    /**
     * 入库单号
     */
    @ExcelProperty(value = "入库单号")
    private String receiptOrderNo;

    /**
     * 出库单号
     */
    @ExcelProperty(value = "出库单号")
    private String shipmentOrderNo;

    /**
     * 当前位置（仓库ID）
     */
    private Long warehouseId;

    /**
     * 当前位置（货架编号）
     */
    @ExcelProperty(value = "货架编号")
    private String rackNo;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;
}


