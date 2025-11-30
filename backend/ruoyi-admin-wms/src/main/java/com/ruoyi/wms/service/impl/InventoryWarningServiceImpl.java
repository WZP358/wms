package com.ruoyi.wms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.vo.ExpirationReminderVo;
import com.ruoyi.wms.domain.vo.InventoryWarningVo;
import com.ruoyi.wms.mapper.InventoryWarningMapper;
import com.ruoyi.wms.service.InventoryWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库存预警与到期提醒Service业务层处理
 *
 * @author zcc
 * @date 2024-11-23
 */
@RequiredArgsConstructor
@Service
public class InventoryWarningServiceImpl implements InventoryWarningService {

    private final InventoryWarningMapper inventoryWarningMapper;

    /**
     * 查询库存预警列表
     */
    @Override
    public TableDataInfo<InventoryWarningVo> queryInventoryWarningList(String itemName, String itemCode, PageQuery pageQuery) {
        Page<InventoryWarningVo> page = inventoryWarningMapper.selectInventoryWarningPage(pageQuery.build(), itemName, itemCode);
        return TableDataInfo.build(page);
    }

    /**
     * 查询库存预警列表（不分页）
     */
    @Override
    public List<InventoryWarningVo> queryInventoryWarningListAll() {
        return inventoryWarningMapper.selectInventoryWarningList();
    }

    /**
     * 获取库存预警数量
     */
    @Override
    public Long getInventoryWarningCount() {
        return inventoryWarningMapper.selectInventoryWarningCount();
    }

    /**
     * 查询到期提醒列表
     */
    @Override
    public TableDataInfo<ExpirationReminderVo> queryExpirationReminderList(Integer daysBeforeExpire, String itemName, String itemCode, PageQuery pageQuery) {
        if (daysBeforeExpire == null || daysBeforeExpire <= 0) {
            daysBeforeExpire = 30; // 默认30天
        }
        Page<ExpirationReminderVo> page = inventoryWarningMapper.selectExpirationReminderPage(pageQuery.build(), daysBeforeExpire, itemName, itemCode);
        return TableDataInfo.build(page);
    }

    /**
     * 查询到期提醒列表（不分页）
     */
    @Override
    public List<ExpirationReminderVo> queryExpirationReminderList(Integer daysBeforeExpire) {
        if (daysBeforeExpire == null || daysBeforeExpire <= 0) {
            daysBeforeExpire = 30; // 默认30天
        }
        return inventoryWarningMapper.selectExpirationReminderList(daysBeforeExpire);
    }

    /**
     * 获取到期提醒数量
     */
    @Override
    public Long getExpirationReminderCount(Integer daysBeforeExpire) {
        if (daysBeforeExpire == null || daysBeforeExpire <= 0) {
            daysBeforeExpire = 30; // 默认30天
        }
        return inventoryWarningMapper.selectExpirationReminderCount(daysBeforeExpire);
    }
}

