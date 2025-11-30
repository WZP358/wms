package com.ruoyi.wms.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.vo.ExpirationReminderVo;
import com.ruoyi.wms.domain.vo.InventoryWarningVo;

import java.util.List;

/**
 * 库存预警与到期提醒Service接口
 *
 * @author zcc
 * @date 2024-11-23
 */
public interface InventoryWarningService {

    /**
     * 查询库存预警列表
     *
     * @param itemName 商品名称
     * @param itemCode 商品编号
     * @param pageQuery 分页查询参数
     * @return 库存预警列表
     */
    TableDataInfo<InventoryWarningVo> queryInventoryWarningList(String itemName, String itemCode, PageQuery pageQuery);

    /**
     * 查询库存预警列表（不分页）
     *
     * @return 库存预警列表
     */
    List<InventoryWarningVo> queryInventoryWarningListAll();

    /**
     * 获取库存预警数量
     *
     * @return 库存预警数量
     */
    Long getInventoryWarningCount();

    /**
     * 查询到期提醒列表
     *
     * @param daysBeforeExpire 提前多少天提醒（默认30天）
     * @param itemName 商品名称
     * @param itemCode 商品编号
     * @param pageQuery 分页查询参数
     * @return 到期提醒列表
     */
    TableDataInfo<ExpirationReminderVo> queryExpirationReminderList(Integer daysBeforeExpire, String itemName, String itemCode, PageQuery pageQuery);

    /**
     * 查询到期提醒列表（不分页）
     *
     * @param daysBeforeExpire 提前多少天提醒（默认30天）
     * @return 到期提醒列表
     */
    List<ExpirationReminderVo> queryExpirationReminderList(Integer daysBeforeExpire);

    /**
     * 获取到期提醒数量
     *
     * @param daysBeforeExpire 提前多少天提醒（默认30天）
     * @return 到期提醒数量
     */
    Long getExpirationReminderCount(Integer daysBeforeExpire);
}

