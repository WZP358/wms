package com.ruoyi.wms.service;

import com.ruoyi.wms.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * 统计分析Service
 *
 * @author zcc
 * @date 2024-12-01
 */
@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final StatisticsMapper statisticsMapper;

    /**
     * 获取出入库趋势分析
     */
    public Map<String, Object> getInOutTrend(String period) {
        List<Map<String, Object>> data = statisticsMapper.getInOutTrend(period);
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> receiptAmounts = new ArrayList<>();
        List<BigDecimal> shipmentAmounts = new ArrayList<>();
        List<BigDecimal> receiptQuantities = new ArrayList<>();
        List<BigDecimal> shipmentQuantities = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            dates.add((String) item.get("date"));
            receiptAmounts.add((BigDecimal) item.getOrDefault("receiptAmount", BigDecimal.ZERO));
            shipmentAmounts.add((BigDecimal) item.getOrDefault("shipmentAmount", BigDecimal.ZERO));
            receiptQuantities.add((BigDecimal) item.getOrDefault("receiptQuantity", BigDecimal.ZERO));
            shipmentQuantities.add((BigDecimal) item.getOrDefault("shipmentQuantity", BigDecimal.ZERO));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("receiptAmounts", receiptAmounts);
        result.put("shipmentAmounts", shipmentAmounts);
        result.put("receiptQuantities", receiptQuantities);
        result.put("shipmentQuantities", shipmentQuantities);
        return result;
    }

    /**
     * 获取热销商品TOP10
     */
    public Map<String, Object> getHotProducts() {
        List<Map<String, Object>> data = statisticsMapper.getHotProducts(10);
        
        List<String> productNames = new ArrayList<>();
        List<BigDecimal> quantities = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            productNames.add((String) item.get("productName"));
            quantities.add((BigDecimal) item.getOrDefault("quantity", BigDecimal.ZERO));
            amounts.add((BigDecimal) item.getOrDefault("amount", BigDecimal.ZERO));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("productNames", productNames);
        result.put("quantities", quantities);
        result.put("amounts", amounts);
        return result;
    }

    /**
     * 获取滞销商品预警
     */
    public Map<String, Object> getSlowProducts() {
        List<Map<String, Object>> data = statisticsMapper.getSlowProducts();
        
        List<String> productNames = new ArrayList<>();
        List<BigDecimal> quantities = new ArrayList<>();
        List<Integer> daysNoSale = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            productNames.add((String) item.get("productName"));
            quantities.add((BigDecimal) item.getOrDefault("quantity", BigDecimal.ZERO));
            daysNoSale.add(((Number) item.getOrDefault("daysNoSale", 0)).intValue());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("productNames", productNames);
        result.put("quantities", quantities);
        result.put("daysNoSale", daysNoSale);
        return result;
    }

    /**
     * 获取供应商/客户分析
     */
    public Map<String, Object> getMerchantAnalysis() {
        List<Map<String, Object>> data = statisticsMapper.getMerchantAnalysis();
        
        List<String> merchantNames = new ArrayList<>();
        List<BigDecimal> onTimeRates = new ArrayList<>();
        List<BigDecimal> returnRates = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            merchantNames.add((String) item.get("merchantName"));
            BigDecimal onTimeRate = (BigDecimal) item.getOrDefault("onTimeRate", BigDecimal.ZERO);
            BigDecimal returnRate = (BigDecimal) item.getOrDefault("returnRate", BigDecimal.ZERO);
            onTimeRates.add(onTimeRate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
            returnRates.add(returnRate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("merchantNames", merchantNames);
        result.put("onTimeRates", onTimeRates);
        result.put("returnRates", returnRates);
        return result;
    }

    /**
     * 获取库存需求预测
     */
    public Map<String, Object> getInventoryForecast(Integer days) {
        List<Map<String, Object>> data = statisticsMapper.getInventoryForecast(days);
        
        List<String> productNames = new ArrayList<>();
        List<BigDecimal> currentInventory = new ArrayList<>();
        List<BigDecimal> forecastDemand = new ArrayList<>();
        List<BigDecimal> suggestedOrder = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            productNames.add((String) item.get("productName"));
            currentInventory.add((BigDecimal) item.getOrDefault("currentInventory", BigDecimal.ZERO));
            forecastDemand.add((BigDecimal) item.getOrDefault("forecastDemand", BigDecimal.ZERO));
            suggestedOrder.add((BigDecimal) item.getOrDefault("suggestedOrder", BigDecimal.ZERO));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("productNames", productNames);
        result.put("currentInventory", currentInventory);
        result.put("forecastDemand", forecastDemand);
        result.put("suggestedOrder", suggestedOrder);
        return result;
    }

    /**
     * 获取各仓库/库区的空间利用率
     */
    public Map<String, Object> getWarehouseAreaUtilization() {
        List<Map<String, Object>> data = statisticsMapper.getWarehouseAreaUtilization();
        
        List<String> names = new ArrayList<>();
        List<BigDecimal> utilizationRates = new ArrayList<>();
        List<BigDecimal> usedAmounts = new ArrayList<>();
        List<Long> warehouseIds = new ArrayList<>();
        List<Long> areaIds = new ArrayList<>();
        List<String> warehouseNames = new ArrayList<>();
        List<String> areaNames = new ArrayList<>();
        
        // 计算总金额，用于计算相对利用率
        BigDecimal totalAmount = data.stream()
            .map(item -> (BigDecimal) item.getOrDefault("usedAmount", BigDecimal.ZERO))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal maxAmount = data.stream()
            .map(item -> (BigDecimal) item.getOrDefault("usedAmount", BigDecimal.ZERO))
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ONE);
        
        for (Map<String, Object> item : data) {
            names.add((String) item.get("name"));
            BigDecimal usedAmount = (BigDecimal) item.getOrDefault("usedAmount", BigDecimal.ZERO);
            usedAmounts.add(usedAmount);
            
            warehouseIds.add(((Number) item.getOrDefault("warehouseId", 0L)).longValue());
            areaIds.add(((Number) item.getOrDefault("areaId", 0L)).longValue());
            warehouseNames.add((String) item.getOrDefault("warehouseName", ""));
            areaNames.add((String) item.getOrDefault("areaName", ""));
            
            // 基于最大金额计算相对利用率（0-100%）
            BigDecimal rate = maxAmount.compareTo(BigDecimal.ZERO) > 0 
                ? usedAmount.divide(maxAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;
            
            utilizationRates.add(rate.setScale(2, RoundingMode.HALF_UP));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("names", names);
        result.put("utilizationRates", utilizationRates);
        result.put("usedAmounts", usedAmounts);
        result.put("warehouseIds", warehouseIds);
        result.put("areaIds", areaIds);
        result.put("warehouseNames", warehouseNames);
        result.put("areaNames", areaNames);
        return result;
    }

    /**
     * 获取库位使用情况热力图数据
     */
    public Map<String, Object> getLocationHeatmap() {
        List<Map<String, Object>> data = statisticsMapper.getLocationHeatmap();
        
        List<String> locations = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        List<Long> warehouseIds = new ArrayList<>();
        List<Long> areaIds = new ArrayList<>();
        List<String> warehouseNames = new ArrayList<>();
        List<String> areaNames = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            locations.add((String) item.get("location"));
            values.add((BigDecimal) item.getOrDefault("amount", BigDecimal.ZERO));
            warehouseIds.add(((Number) item.getOrDefault("warehouseId", 0L)).longValue());
            areaIds.add(((Number) item.getOrDefault("areaId", 0L)).longValue());
            warehouseNames.add((String) item.getOrDefault("warehouseName", ""));
            areaNames.add((String) item.getOrDefault("areaName", ""));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("locations", locations);
        result.put("values", values);
        result.put("warehouseIds", warehouseIds);
        result.put("areaIds", areaIds);
        result.put("warehouseNames", warehouseNames);
        result.put("areaNames", areaNames);
        return result;
    }

    /**
     * 获取出入库同比环比分析
     */
    public Map<String, Object> getInOutYearOverYear(String period) {
        List<Map<String, Object>> data = statisticsMapper.getInOutYearOverYear(period);
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> currentReceiptAmounts = new ArrayList<>();
        List<BigDecimal> lastReceiptAmounts = new ArrayList<>();
        List<BigDecimal> currentShipmentAmounts = new ArrayList<>();
        List<BigDecimal> lastShipmentAmounts = new ArrayList<>();
        List<BigDecimal> receiptGrowthRates = new ArrayList<>();
        List<BigDecimal> shipmentGrowthRates = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            dates.add((String) item.get("date"));
            BigDecimal currentReceipt = (BigDecimal) item.getOrDefault("currentReceiptAmount", BigDecimal.ZERO);
            BigDecimal lastReceipt = (BigDecimal) item.getOrDefault("lastReceiptAmount", BigDecimal.ZERO);
            BigDecimal currentShipment = (BigDecimal) item.getOrDefault("currentShipmentAmount", BigDecimal.ZERO);
            BigDecimal lastShipment = (BigDecimal) item.getOrDefault("lastShipmentAmount", BigDecimal.ZERO);
            
            currentReceiptAmounts.add(currentReceipt);
            lastReceiptAmounts.add(lastReceipt);
            currentShipmentAmounts.add(currentShipment);
            lastShipmentAmounts.add(lastShipment);
            
            // 计算增长率
            BigDecimal receiptGrowth = lastReceipt.compareTo(BigDecimal.ZERO) > 0
                ? currentReceipt.subtract(lastReceipt).divide(lastReceipt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;
            BigDecimal shipmentGrowth = lastShipment.compareTo(BigDecimal.ZERO) > 0
                ? currentShipment.subtract(lastShipment).divide(lastShipment, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;
            
            receiptGrowthRates.add(receiptGrowth.setScale(2, RoundingMode.HALF_UP));
            shipmentGrowthRates.add(shipmentGrowth.setScale(2, RoundingMode.HALF_UP));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("currentReceiptAmounts", currentReceiptAmounts);
        result.put("lastReceiptAmounts", lastReceiptAmounts);
        result.put("currentShipmentAmounts", currentShipmentAmounts);
        result.put("lastShipmentAmounts", lastShipmentAmounts);
        result.put("receiptGrowthRates", receiptGrowthRates);
        result.put("shipmentGrowthRates", shipmentGrowthRates);
        return result;
    }

    /**
     * 获取库存金额变化趋势
     */
    public Map<String, Object> getInventoryAmountTrend(String period) {
        List<Map<String, Object>> data = statisticsMapper.getInventoryAmountTrend(period);
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            dates.add((String) item.get("date"));
            amounts.add((BigDecimal) item.getOrDefault("amount", BigDecimal.ZERO));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("amounts", amounts);
        return result;
    }
}

