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
}

