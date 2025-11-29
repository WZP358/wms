package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 统计分析
 *
 * @author zcc
 * @date 2024-12-01
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/statistics")
public class StatisticsController extends BaseController {

    private final StatisticsService statisticsService;

    /**
     * 获取出入库趋势分析
     * @param period 时间周期：day/week/month/year
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/trend")
    public R<Map<String, Object>> getInOutTrend(@RequestParam(defaultValue = "day") String period) {
        return R.ok(statisticsService.getInOutTrend(period));
    }

    /**
     * 获取热销商品TOP10
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/hotProducts")
    public R<Map<String, Object>> getHotProducts() {
        return R.ok(statisticsService.getHotProducts());
    }

    /**
     * 获取滞销商品预警
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/slowProducts")
    public R<Map<String, Object>> getSlowProducts() {
        return R.ok(statisticsService.getSlowProducts());
    }

    /**
     * 获取供应商/客户分析
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/merchantAnalysis")
    public R<Map<String, Object>> getMerchantAnalysis() {
        return R.ok(statisticsService.getMerchantAnalysis());
    }

    /**
     * 获取库存需求预测
     * @param days 预测天数：7/30/90
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/inventoryForecast")
    public R<Map<String, Object>> getInventoryForecast(@RequestParam(defaultValue = "30") Integer days) {
        return R.ok(statisticsService.getInventoryForecast(days));
    }
}

