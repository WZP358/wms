package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.vo.ItemSkuVo;
import com.ruoyi.wms.domain.vo.ItemVo;
import com.ruoyi.wms.domain.vo.WarehouseVo;
import com.ruoyi.wms.domain.vo.AreaVo;
import com.ruoyi.wms.domain.vo.InventoryDetailExportVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.ruoyi.wms.domain.bo.InventoryDetailBo;
import com.ruoyi.wms.domain.entity.InventoryDetail;
import com.ruoyi.wms.domain.vo.InventoryDetailVo;
import com.ruoyi.wms.mapper.InventoryDetailMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 库存详情Service业务层处理
 *
 * @author zcc
 * @date 2024-07-22
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryDetailService extends ServiceImpl<InventoryDetailMapper, InventoryDetail> {

    private final InventoryDetailMapper inventoryDetailMapper;
    private final ItemSkuService itemSkuService;
    
    @Lazy
    @Autowired
    private WarehouseService warehouseService;
    
    @Lazy
    @Autowired
    private AreaService areaService;
    
    @Lazy
    @Autowired
    private ItemService itemService;

    /**
     * 查询库存详情
     */
    public InventoryDetailVo queryById(Long id){
        return inventoryDetailMapper.selectVoById(id);
    }

    /**
     * 查询库存详情列表
     */
    public TableDataInfo<InventoryDetailVo> queryPageList(InventoryDetailBo bo, PageQuery pageQuery) {
        if (bo.getDaysToExpires() != null) {
            LocalDateTime expirationStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            bo.setExpirationStartTime(expirationStartTime);
            LocalDateTime expirationEndTime = expirationStartTime.plusDays(bo.getDaysToExpires());
            bo.setExpirationEndTime(expirationEndTime);
        }
        // 调试日志
        log.info("查询库存明细 - warehouseId: {}, areaId: {}, remainQuantity过滤: true", 
            bo.getWarehouseId(), bo.getAreaId());
        log.info("查询参数详情 - itemName: {}, itemCode: {}, skuName: {}, skuCode: {}, batchNo: {}", 
            bo.getItemName(), bo.getItemCode(), bo.getSkuName(), bo.getSkuCode(), bo.getBatchNo());
        Page<InventoryDetailVo> result = inventoryDetailMapper.selectPageByBo(pageQuery.build(), bo);
        log.info("查询结果数量: {}, 当前页数据量: {}", result.getTotal(), result.getRecords().size());
        if (result.getRecords().size() > 0) {
            log.info("第一条数据 - id: {}, warehouseId: {}, areaId: {}, remainQuantity: {}", 
                result.getRecords().get(0).getId(), 
                result.getRecords().get(0).getWarehouseId(),
                result.getRecords().get(0).getAreaId(),
                result.getRecords().get(0).getRemainQuantity());
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询库存详情列表
     */
    public List<InventoryDetailVo> queryList(InventoryDetailBo bo) {
        List<InventoryDetailVo> vos = inventoryDetailMapper.selectListByBo(bo);
        if (CollUtil.isEmpty(vos)) {
            return vos;
        }
        Set<Long> skuIds = vos.stream().map(InventoryDetailVo::getSkuId).collect(Collectors.toSet());
        Map<Long, ItemSkuVo> itemSkuMap = itemSkuService.queryVosByIds(skuIds).stream().collect(Collectors.toMap(ItemSkuVo::getId, Function.identity()));
        vos.forEach(it -> it.setItemSku(itemSkuMap.get(it.getSkuId())));
        return vos;
    }

    private LambdaQueryWrapper<InventoryDetail> buildQueryWrapper(InventoryDetailBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<InventoryDetail> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getReceiptOrderId() != null, InventoryDetail::getReceiptOrderId, bo.getReceiptOrderId());
        lqw.eq(StringUtils.isNotBlank(bo.getReceiptOrderType()), InventoryDetail::getReceiptOrderType, bo.getReceiptOrderType());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), InventoryDetail::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getType() != null, InventoryDetail::getType, bo.getType());
        lqw.eq(bo.getSkuId() != null, InventoryDetail::getSkuId, bo.getSkuId());
        lqw.eq(bo.getWarehouseId() != null, InventoryDetail::getWarehouseId, bo.getWarehouseId());
        lqw.eq(bo.getAreaId() != null, InventoryDetail::getAreaId, bo.getAreaId());
        lqw.eq(bo.getQuantity() != null, InventoryDetail::getQuantity, bo.getQuantity());
        lqw.eq(bo.getExpirationDate() != null, InventoryDetail::getExpirationDate, bo.getExpirationDate());
        lqw.eq(bo.getAmount() != null, InventoryDetail::getAmount, bo.getAmount());
        lqw.eq(bo.getRemainQuantity() != null, InventoryDetail::getRemainQuantity, bo.getRemainQuantity());
        return lqw;
    }

    /**
     * 新增库存详情
     */
    public void insertByBo(InventoryDetailBo bo) {
        InventoryDetail add = MapstructUtils.convert(bo, InventoryDetail.class);
        inventoryDetailMapper.insert(add);
    }

    /**
     * 修改库存详情
     */
    public void updateByBo(InventoryDetailBo bo) {
        InventoryDetail update = MapstructUtils.convert(bo, InventoryDetail.class);
        inventoryDetailMapper.updateById(update);
    }

    /**
     * 批量删除库存详情
     */
    public void deleteByIds(Collection<Long> ids) {
        inventoryDetailMapper.deleteBatchIds(ids);
    }

    /**
     * 校验入库记录剩余数
     * @param inventoryDetailBoList
     */
    public void validateRemainQuantity(List<InventoryDetailBo> inventoryDetailBoList) {
        if (CollUtil.isEmpty(inventoryDetailBoList)) {
            return;
        }
        List<InventoryDetail> inventoryDetailList = inventoryDetailMapper.selectBatchIds(inventoryDetailBoList.stream().map(InventoryDetailBo::getId).toList());
        if (CollUtil.isEmpty(inventoryDetailList)) {
            throw new BaseException("库存不足");
        }
        Map<Long, Integer> remainQuantityMap = inventoryDetailList
            .stream()
            .collect(Collectors.toMap(InventoryDetail::getId, InventoryDetail::getRemainQuantity));
        boolean validResult = inventoryDetailBoList
            .stream()
            .anyMatch(inventoryDetailBo ->
                !remainQuantityMap.containsKey(inventoryDetailBo.getId())
                    || remainQuantityMap.get(inventoryDetailBo.getId()) < inventoryDetailBo.getShipmentQuantity()
            );
        if (validResult) {
            throw new BaseException("库存不足");
        }
    }

    public void clearDataWithZeroRemainQuantity() {
        LambdaQueryWrapper<InventoryDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(InventoryDetail::getRemainQuantity, 0);
        inventoryDetailMapper.delete(wrapper);
    }

    /**
     * 查询库存详情列表用于导出（包含仓库、库区、商品信息）
     */
    public List<InventoryDetailExportVo> queryListForExport(InventoryDetailBo bo) {
        // 处理过期时间查询
        if (bo.getDaysToExpires() != null) {
            LocalDateTime expirationStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            bo.setExpirationStartTime(expirationStartTime);
            LocalDateTime expirationEndTime = expirationStartTime.plusDays(bo.getDaysToExpires());
            bo.setExpirationEndTime(expirationEndTime);
        }
        
        // 查询库存详情列表
        List<InventoryDetailVo> vos = inventoryDetailMapper.selectListByBo(bo);
        if (CollUtil.isEmpty(vos)) {
            return new ArrayList<>();
        }
        
        // 获取所有SKU ID
        Set<Long> skuIds = vos.stream().map(InventoryDetailVo::getSkuId).collect(Collectors.toSet());
        Map<Long, ItemSkuVo> itemSkuMap = itemSkuService.queryVosByIds(skuIds).stream()
            .collect(Collectors.toMap(ItemSkuVo::getId, Function.identity()));
        
        // 获取所有商品ID
        Set<Long> itemIds = itemSkuMap.values().stream()
            .map(ItemSkuVo::getItemId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, ItemVo> itemMap = itemService.queryById(new ArrayList<>(itemIds)).stream()
            .collect(Collectors.toMap(ItemVo::getId, Function.identity()));
        
        // 获取所有仓库ID
        Set<Long> warehouseIds = vos.stream()
            .map(InventoryDetailVo::getWarehouseId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, WarehouseVo> warehouseMap = new HashMap<>();
        for (Long warehouseId : warehouseIds) {
            WarehouseVo warehouse = warehouseService.queryById(warehouseId);
            if (warehouse != null) {
                warehouseMap.put(warehouseId, warehouse);
            }
        }
        
        // 获取所有库区ID
        Set<Long> areaIds = vos.stream()
            .map(InventoryDetailVo::getAreaId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, AreaVo> areaMap = new HashMap<>();
        for (Long areaId : areaIds) {
            AreaVo area = areaService.queryById(areaId);
            if (area != null) {
                areaMap.put(areaId, area);
            }
        }
        
        // 转换为导出VO
        return vos.stream().map(vo -> {
            InventoryDetailExportVo exportVo = new InventoryDetailExportVo();
            
            // 设置仓库和库区信息
            WarehouseVo warehouse = warehouseMap.get(vo.getWarehouseId());
            exportVo.setWarehouseName(warehouse != null ? warehouse.getWarehouseName() : "");
            AreaVo area = areaMap.get(vo.getAreaId());
            exportVo.setAreaName(area != null ? area.getAreaName() : "");
            
            // 设置商品和规格信息
            ItemSkuVo itemSku = itemSkuMap.get(vo.getSkuId());
            if (itemSku != null) {
                exportVo.setSkuCode(itemSku.getSkuCode());
                exportVo.setSkuName(itemSku.getSkuName());
                
                ItemVo item = itemMap.get(itemSku.getItemId());
                if (item != null) {
                    exportVo.setItemCode(item.getItemCode());
                    exportVo.setItemName(item.getItemName());
                }
            }
            
            // 设置库存信息
            exportVo.setQuantity(vo.getRemainQuantity());
            exportVo.setBatchNo(vo.getBatchNo());
            exportVo.setProductionDate(vo.getProductionDate());
            exportVo.setExpirationDate(vo.getExpirationDate());
            exportVo.setCreateTime(vo.getCreateTime());
            exportVo.setAmount(vo.getAmount());
            exportVo.setRemark(vo.getRemark());
            
            return exportVo;
        }).collect(Collectors.toList());
    }
}
