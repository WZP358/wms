package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.vo.ExpirationReminderVo;
import com.ruoyi.wms.domain.vo.InventoryWarningVo;
import com.ruoyi.wms.service.InventoryWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存预警与到期提醒Controller
 *
 * @author zcc
 * @date 2024-11-23
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/inventoryWarning")
public class InventoryWarningController extends BaseController {

    private final InventoryWarningService inventoryWarningService;

    /**
     * 查询库存预警列表
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/warning/list")
    public TableDataInfo<InventoryWarningVo> queryInventoryWarningList(
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String itemCode,
            PageQuery pageQuery) {
        return inventoryWarningService.queryInventoryWarningList(itemName, itemCode, pageQuery);
    }

    /**
     * 查询库存预警列表（不分页）
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/warning/listAll")
    public R<List<InventoryWarningVo>> queryInventoryWarningListAll() {
        List<InventoryWarningVo> list = inventoryWarningService.queryInventoryWarningListAll();
        return R.ok(list);
    }

    /**
     * 获取库存预警数量
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/warning/count")
    public R<Long> getInventoryWarningCount() {
        Long count = inventoryWarningService.getInventoryWarningCount();
        return R.ok(count);
    }

    /**
     * 查询到期提醒列表
     *
     * @param daysBeforeExpire 提前多少天提醒（默认30天）
     * @param itemName 商品名称
     * @param itemCode 商品编号
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/expiration/list")
    public TableDataInfo<ExpirationReminderVo> queryExpirationReminderList(
            @RequestParam(required = false, defaultValue = "30") Integer daysBeforeExpire,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String itemCode,
            PageQuery pageQuery) {
        return inventoryWarningService.queryExpirationReminderList(daysBeforeExpire, itemName, itemCode, pageQuery);
    }

    /**
     * 查询到期提醒列表（不分页）
     *
     * @param daysBeforeExpire 提前多少天提醒（默认30天）
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/expiration/listAll")
    public R<List<ExpirationReminderVo>> queryExpirationReminderListAll(
            @RequestParam(required = false, defaultValue = "30") Integer daysBeforeExpire) {
        List<ExpirationReminderVo> list = inventoryWarningService.queryExpirationReminderList(daysBeforeExpire);
        return R.ok(list);
    }

    /**
     * 获取到期提醒数量
     *
     * @param daysBeforeExpire 提前多少天提醒（默认30天）
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/expiration/count")
    public R<Long> getExpirationReminderCount(
            @RequestParam(required = false, defaultValue = "30") Integer daysBeforeExpire) {
        Long count = inventoryWarningService.getExpirationReminderCount(daysBeforeExpire);
        return R.ok(count);
    }
}

