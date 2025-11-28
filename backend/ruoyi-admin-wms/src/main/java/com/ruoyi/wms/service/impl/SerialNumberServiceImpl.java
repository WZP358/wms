package com.ruoyi.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.SerialNumberBo;
import com.ruoyi.wms.domain.entity.SerialNumber;
import com.ruoyi.wms.domain.vo.SerialNumberVo;
import com.ruoyi.wms.mapper.SerialNumberMapper;
import com.ruoyi.wms.service.SerialNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 序列号/SN码Service业务层处理
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@Service
public class SerialNumberServiceImpl implements SerialNumberService {

    private final SerialNumberMapper baseMapper;

    /**
     * 查询SN码
     */
    @Override
    public SerialNumberVo queryById(Long snId) {
        return baseMapper.selectVoById(snId);
    }

    /**
     * 根据SN码查询
     */
    @Override
    public SerialNumberVo queryBySnCode(String snCode) {
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.eq(SerialNumber::getSnCode, snCode);
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 查询SN码列表
     */
    @Override
    public TableDataInfo<SerialNumberVo> queryPageList(SerialNumberBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SerialNumber> lqw = buildQueryWrapper(bo);
        Page<SerialNumberVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询SN码列表
     */
    @Override
    public List<SerialNumberVo> queryList(SerialNumberBo bo) {
        LambdaQueryWrapper<SerialNumber> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SerialNumber> buildQueryWrapper(SerialNumberBo bo) {
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getSnCode()), SerialNumber::getSnCode, bo.getSnCode());
        lqw.like(StringUtils.isNotBlank(bo.getItemName()), SerialNumber::getItemName, bo.getItemName());
        lqw.eq(bo.getItemId() != null, SerialNumber::getItemId, bo.getItemId());
        lqw.eq(StringUtils.isNotBlank(bo.getBindStatus()), SerialNumber::getBindStatus, bo.getBindStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getReceiptOrderNo()), SerialNumber::getReceiptOrderNo, bo.getReceiptOrderNo());
        lqw.eq(StringUtils.isNotBlank(bo.getShipmentOrderNo()), SerialNumber::getShipmentOrderNo, bo.getShipmentOrderNo());
        lqw.orderByDesc(SerialNumber::getCreateTime);
        return lqw;
    }

    /**
     * 新增SN码
     */
    @Override
    public Boolean insertByBo(SerialNumberBo bo) {
        SerialNumber add = BeanUtil.toBean(bo, SerialNumber.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setSnId(add.getSnId());
        }
        return flag;
    }

    /**
     * 修改SN码
     */
    @Override
    public Boolean updateByBo(SerialNumberBo bo) {
        SerialNumber update = BeanUtil.toBean(bo, SerialNumber.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SerialNumber entity) {
        // 检查SN码是否已存在
        if (entity.getSnId() == null) {
            LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
            lqw.eq(SerialNumber::getSnCode, entity.getSnCode());
            if (baseMapper.exists(lqw)) {
                throw new RuntimeException("SN码已存在: " + entity.getSnCode());
            }
        }
    }

    /**
     * 批量删除SN码
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 可以添加删除前的业务校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 模拟扫描枪扫描
     * 实际应用中，这个接口会连接硬件扫描枪
     */
    @Override
    public String scanSn() {
        // 模拟返回提示信息
        // 在实际应用中，这里应该是硬件扫描枪的接口调用
        return "请连接扫描枪";
    }

    /**
     * 直接输入SN码（验证并处理）
     * 验证SN码格式、是否重复等
     */
    @Override
    public String inputSn(String snCode) {
        return inputSnWithExclude(snCode, null);
    }

    /**
     * 直接输入SN码（验证并处理 - 增强版）
     * 验证SN码格式、是否重复等，支持排除某些SN码
     */
    @Override
    public String inputSnWithExclude(String snCode, List<String> excludeSnCodes) {
        if (StringUtils.isBlank(snCode)) {
            return "SN码不能为空";
        }
        
        // 验证SN码格式（可根据实际需求调整）
        if (snCode.length() < 3 || snCode.length() > 50) {
            return "SN码长度应在3-50个字符之间";
        }
        
        // 检查SN码是否已存在
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.eq(SerialNumber::getSnCode, snCode);
        
        // 如果有排除列表，排除这些SN码
        if (excludeSnCodes != null && !excludeSnCodes.isEmpty()) {
            // 如果要验证的SN码在排除列表中，直接通过
            if (excludeSnCodes.contains(snCode)) {
                return "OK";
            }
        }
        
        if (baseMapper.exists(lqw)) {
            return "SN码已存在，请勿重复录入";
        }
        
        // 验证通过
        return "OK";
    }

    /**
     * 批量验证SN码
     */
    @Override
    public List<SerialNumberVo> validateSnBatch(List<String> snCodes) {
        if (snCodes == null || snCodes.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.in(SerialNumber::getSnCode, snCodes);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 批量验证SN码（增强版）
     * 支持排除某些SN码（用于编辑场景）
     */
    @Override
    public Map<String, Object> validateSnBatchEnhanced(List<String> snCodes, List<String> excludeSnCodes) {
        Map<String, Object> result = new HashMap<>();
        
        if (snCodes == null || snCodes.isEmpty()) {
            result.put("valid", true);
            result.put("duplicates", new ArrayList<>());
            result.put("message", "验证通过");
            return result;
        }
        
        // 过滤掉要排除的SN码
        List<String> codesToCheck = snCodes;
        if (excludeSnCodes != null && !excludeSnCodes.isEmpty()) {
            codesToCheck = snCodes.stream()
                .filter(code -> !excludeSnCodes.contains(code))
                .collect(Collectors.toList());
        }
        
        if (codesToCheck.isEmpty()) {
            result.put("valid", true);
            result.put("duplicates", new ArrayList<>());
            result.put("message", "验证通过");
            return result;
        }
        
        // 检查这些SN码是否已存在
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.in(SerialNumber::getSnCode, codesToCheck);
        List<SerialNumberVo> existingSnCodes = baseMapper.selectVoList(lqw);
        
        if (existingSnCodes.isEmpty()) {
            result.put("valid", true);
            result.put("duplicates", new ArrayList<>());
            result.put("message", "所有SN码验证通过");
        } else {
            result.put("valid", false);
            result.put("duplicates", existingSnCodes.stream()
                .map(SerialNumberVo::getSnCode)
                .collect(Collectors.toList()));
            result.put("message", "以下SN码已存在：" + 
                existingSnCodes.stream()
                    .map(SerialNumberVo::getSnCode)
                    .collect(Collectors.joining(", ")));
        }
        
        return result;
    }

    /**
     * 绑定SN码到物品
     */
    @Override
    public Boolean bindSnToItem(SerialNumberBo bo) {
        SerialNumber entity = baseMapper.selectById(bo.getSnId());
        if (entity == null) {
            throw new RuntimeException("SN码不存在");
        }
        entity.setItemId(bo.getItemId());
        entity.setItemNo(bo.getItemNo());
        entity.setItemName(bo.getItemName());
        entity.setSpec(bo.getSpec());
        entity.setBindStatus("1");
        return baseMapper.updateById(entity) > 0;
    }

    /**
     * 解绑SN码
     */
    @Override
    public Boolean unbindSn(String snCode) {
        LambdaQueryWrapper<SerialNumber> lqw = Wrappers.lambdaQuery();
        lqw.eq(SerialNumber::getSnCode, snCode);
        SerialNumber entity = baseMapper.selectOne(lqw);
        if (entity == null) {
            throw new RuntimeException("SN码不存在");
        }
        entity.setItemId(null);
        entity.setItemNo(null);
        entity.setItemName(null);
        entity.setSpec(null);
        entity.setBindStatus("0");
        return baseMapper.updateById(entity) > 0;
    }
}

