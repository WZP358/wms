package com.ruoyi.wms.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 统计分析Mapper接口
 *
 * @author zcc
 * @date 2024-12-01
 */
public interface StatisticsMapper {

    /**
     * 获取出入库趋势分析
     */
    List<Map<String, Object>> getInOutTrend(@Param("period") String period);

    /**
     * 获取热销商品TOP10
     */
    List<Map<String, Object>> getHotProducts(@Param("limit") Integer limit);

    /**
     * 获取滞销商品预警
     */
    List<Map<String, Object>> getSlowProducts();

    /**
     * 获取供应商/客户分析
     */
    List<Map<String, Object>> getMerchantAnalysis();

    /**
     * 获取库存需求预测
     */
    List<Map<String, Object>> getInventoryForecast(@Param("days") Integer days);

    /**
     * 获取各仓库/库区的空间利用率
     */
    List<Map<String, Object>> getWarehouseAreaUtilization();

    /**
     * 获取库位使用情况热力图数据
     */
    List<Map<String, Object>> getLocationHeatmap();

    /**
     * 获取出入库同比环比分析
     */
    List<Map<String, Object>> getInOutYearOverYear(@Param("period") String period);

    /**
     * 获取库存金额变化趋势
     */
    List<Map<String, Object>> getInventoryAmountTrend(@Param("period") String period);
}

