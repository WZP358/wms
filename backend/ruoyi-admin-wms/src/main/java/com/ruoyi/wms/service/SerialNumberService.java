package com.ruoyi.wms.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.SerialNumberBo;
import com.ruoyi.wms.domain.vo.SerialNumberVo;

import java.util.Collection;
import java.util.List;

/**
 * 序列号/SN码Service接口
 *
 * @author ruoyi
 */
public interface SerialNumberService {

    /**
     * 查询SN码
     */
    SerialNumberVo queryById(Long snId);

    /**
     * 根据SN码查询
     */
    SerialNumberVo queryBySnCode(String snCode);

    /**
     * 查询SN码列表
     */
    TableDataInfo<SerialNumberVo> queryPageList(SerialNumberBo bo, PageQuery pageQuery);

    /**
     * 查询SN码列表
     */
    List<SerialNumberVo> queryList(SerialNumberBo bo);

    /**
     * 新增SN码
     */
    Boolean insertByBo(SerialNumberBo bo);

    /**
     * 修改SN码
     */
    Boolean updateByBo(SerialNumberBo bo);

    /**
     * 校验并批量删除SN码信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 模拟扫描枪扫描
     */
    String scanSn();

    /**
     * 直接输入SN码（验证并处理）
     * @param snCode SN码
     * @return 验证结果信息
     */
    String inputSn(String snCode);

    /**
     * 直接输入SN码（验证并处理 - 增强版）
     * @param snCode SN码
     * @param excludeSnCodes 要排除的SN码列表（用于编辑场景）
     * @return 验证结果信息
     */
    String inputSnWithExclude(String snCode, List<String> excludeSnCodes);

    /**
     * 批量验证SN码
     */
    List<SerialNumberVo> validateSnBatch(List<String> snCodes);

    /**
     * 批量验证SN码（增强版）
     * @param snCodes 要验证的SN码列表
     * @param excludeSnCodes 要排除的SN码列表
     * @return 验证结果
     */
    java.util.Map<String, Object> validateSnBatchEnhanced(List<String> snCodes, List<String> excludeSnCodes);

    /**
     * 绑定SN码到物品
     */
    Boolean bindSnToItem(SerialNumberBo bo);

    /**
     * 解绑SN码
     */
    Boolean unbindSn(String snCode);
}

