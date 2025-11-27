package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.bo.SerialNumberBo;
import com.ruoyi.wms.domain.vo.SerialNumberVo;
import com.ruoyi.wms.service.SerialNumberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 序列号/SN码管理
 *
 * @author ruoyi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/sn")
public class SerialNumberController extends BaseController {

    private final SerialNumberService serialNumberService;

    /**
     * 查询SN码列表
     */
    @GetMapping("/list")
    public TableDataInfo<SerialNumberVo> list(SerialNumberBo bo, PageQuery pageQuery) {
        return serialNumberService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出SN码列表
     */
    @Log(title = "SN码", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SerialNumberBo bo, HttpServletResponse response) {
        List<SerialNumberVo> list = serialNumberService.queryList(bo);
        ExcelUtil.exportExcel(list, "SN码", SerialNumberVo.class, response);
    }

    /**
     * 扫描录入（模拟扫描枪接口）
     */
    @Log(title = "SN码扫描", businessType = BusinessType.OTHER)
    @PostMapping("/scan")
    public R<String> scan() {
        String message = serialNumberService.scanSn();
        return R.ok(message);
    }

    /**
     * 直接输入SN码（验证接口）
     * 注意：此接口必须放在 @GetMapping("/{snCode}") 之前，避免路径冲突
     */
    @PostMapping("/input")
    public R<String> input(@RequestBody Map<String, String> params) {
        String snCode = params.get("snCode");
        if (snCode == null || snCode.trim().isEmpty()) {
            return R.fail("SN码不能为空");
        }
        String result = serialNumberService.inputSn(snCode.trim());
        if ("OK".equals(result)) {
            return R.ok("SN码验证通过");
        } else {
            return R.fail(result);
        }
    }

    /**
     * 获取SN码详细信息
     */
    @GetMapping(value = "/{snCode}")
    public R<SerialNumberVo> getInfo(@NotNull(message = "SN码不能为空")
                                     @PathVariable("snCode") String snCode) {
        return R.ok(serialNumberService.queryBySnCode(snCode));
    }

    /**
     * 新增SN码（直接输入）
     */
    @Log(title = "SN码", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SerialNumberBo bo) {
        return toAjax(serialNumberService.insertByBo(bo));
    }

    /**
     * 修改SN码
     */
    @Log(title = "SN码", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SerialNumberBo bo) {
        return toAjax(serialNumberService.updateByBo(bo));
    }

    /**
     * 删除SN码
     */
    @Log(title = "SN码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{snCodes}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String snCodes) {
        Long[] ids = Arrays.stream(snCodes.split(","))
            .map(code -> {
                SerialNumberVo vo = serialNumberService.queryBySnCode(code);
                return vo != null ? vo.getSnId() : null;
            })
            .filter(id -> id != null)
            .toArray(Long[]::new);
        return toAjax(serialNumberService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 批量验证SN码
     */
    @PostMapping("/validate/batch")
    public R<List<SerialNumberVo>> validateBatch(@RequestBody List<String> snCodes) {
        List<SerialNumberVo> result = serialNumberService.validateSnBatch(snCodes);
        return R.ok(result);
    }

    /**
     * 绑定SN码到物品
     */
    @Log(title = "SN码绑定", businessType = BusinessType.UPDATE)
    @PostMapping("/bind")
    public R<Void> bind(@RequestBody SerialNumberBo bo) {
        return toAjax(serialNumberService.bindSnToItem(bo));
    }

    /**
     * 解绑SN码
     */
    @Log(title = "SN码解绑", businessType = BusinessType.UPDATE)
    @PostMapping("/unbind/{snCode}")
    public R<Void> unbind(@PathVariable String snCode) {
        return toAjax(serialNumberService.unbindSn(snCode));
    }
}

