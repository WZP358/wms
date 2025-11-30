package com.ruoyi.wms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.wms.domain.vo.ExpirationReminderVo;
import com.ruoyi.wms.domain.vo.InventoryWarningVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存预警与到期提醒Mapper接口
 *
 * @author zcc
 * @date 2024-11-23
 */
public interface InventoryWarningMapper {

    /**
     * 查询库存预警列表（分页）
     *
     * @param page 分页对象
     * @param itemName 商品名称
     * @param itemCode 商品编号
     * @return 库存预警列表
     */
    Page<InventoryWarningVo> selectInventoryWarningPage(Page<InventoryWarningVo> page, @Param("itemName") String itemName, @Param("itemCode") String itemCode);

    /**
     * 查询库存预警列表
     *
     * @return 库存预警列表
     */
    List<InventoryWarningVo> selectInventoryWarningList();

    /**
     * 获取库存预警数量
     *
     * @return 库存预警数量
     */
    Long selectInventoryWarningCount();

    /**
     * 查询到期提醒列表（分页）
     *
     * @param page 分页对象
     * @param daysBeforeExpire 提前多少天提醒
     * @param itemName 商品名称
     * @param itemCode 商品编号
     * @return 到期提醒列表
     */
    Page<ExpirationReminderVo> selectExpirationReminderPage(Page<ExpirationReminderVo> page, @Param("daysBeforeExpire") Integer daysBeforeExpire, @Param("itemName") String itemName, @Param("itemCode") String itemCode);

    /**
     * 查询到期提醒列表
     *
     * @param daysBeforeExpire 提前多少天提醒
     * @return 到期提醒列表
     */
    List<ExpirationReminderVo> selectExpirationReminderList(@Param("daysBeforeExpire") Integer daysBeforeExpire);

    /**
     * 获取到期提醒数量
     *
     * @param daysBeforeExpire 提前多少天提醒
     * @return 到期提醒数量
     */
    Long selectExpirationReminderCount(@Param("daysBeforeExpire") Integer daysBeforeExpire);
}

