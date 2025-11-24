package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.bo.PathRecordBo;
import com.ruoyi.wms.domain.bo.WarehouseMapBo;
import com.ruoyi.wms.domain.vo.PathRecordVo;
import com.ruoyi.wms.domain.vo.WarehouseMapVo;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库地图配置控制器
 *
 * @author ruoyi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/map")
public class WarehouseMapController extends BaseController {

    // Service层暂时注释，等实现后再启用
    // private final WarehouseMapService warehouseMapService;
    // private final PathRecordService pathRecordService;

    /**
     * 查询仓库地图配置列表
     */
    @SaCheckPermission("wms:map:list")
    @GetMapping("/list")
    public TableDataInfo<WarehouseMapVo> list(WarehouseMapBo bo, PageQuery pageQuery) {
        // return warehouseMapService.queryPageList(bo, pageQuery);
        return TableDataInfo.build();
    }

    /**
     * 获取仓库地图配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("wms:map:list")
    @GetMapping("/config/{id}")
    public R<WarehouseMapVo> getConfig(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        // return R.ok(warehouseMapService.queryById(id));
        return R.ok();
    }

    /**
     * 获取默认地图配置
     */
    @SaCheckPermission("wms:map:list")
    @GetMapping("/config")
    public R<WarehouseMapVo> getDefaultConfig() {
        // 返回默认配置或最后一次保存的配置
        // return R.ok(warehouseMapService.getDefaultConfig());
        WarehouseMapVo vo = new WarehouseMapVo();
        vo.setRows(10);
        vo.setCols(15);
        vo.setCellSize(50);
        vo.setGap(5);
        vo.setMapName("默认地图");
        return R.ok(vo);
    }

    /**
     * 保存仓库地图配置
     */
    @SaCheckPermission("wms:map:edit")
    @Log(title = "仓库地图配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/config")
    public R<Void> saveConfig(@Validated(AddGroup.class) @RequestBody WarehouseMapBo bo) {
        // warehouseMapService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 修改仓库地图配置
     */
    @SaCheckPermission("wms:map:edit")
    @Log(title = "仓库地图配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/config")
    public R<Void> updateConfig(@Validated(EditGroup.class) @RequestBody WarehouseMapBo bo) {
        // warehouseMapService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 删除仓库地图配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("wms:map:remove")
    @Log(title = "仓库地图配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/config/{ids}")
    public R<Void> removeConfig(@NotNull(message = "主键不能为空") @PathVariable Long[] ids) {
        // warehouseMapService.deleteWithValidByIds(List.of(ids), true);
        return R.ok();
    }

    /**
     * 查询库区列表
     */
    @SaCheckPermission("wms:map:list")
    @GetMapping("/areas")
    public R<List<Object>> listAreas() {
        // return R.ok(warehouseMapService.listAreas());
        return R.ok();
    }

    /**
     * 保存库区配置
     */
    @SaCheckPermission("wms:map:edit")
    @Log(title = "库区配置", businessType = BusinessType.UPDATE)
    @PostMapping("/areas")
    public R<Void> saveAreas(@RequestBody String areasConfig) {
        // warehouseMapService.saveAreas(areasConfig);
        return R.ok();
    }

    /**
     * 查询路径记录历史
     */
    @SaCheckPermission("wms:map:list")
    @GetMapping("/path/history")
    public TableDataInfo<PathRecordVo> pathHistory(PathRecordBo bo, PageQuery pageQuery) {
        // return pathRecordService.queryPageList(bo, pageQuery);
        return TableDataInfo.build();
    }

    /**
     * 保存路径记录
     */
    @SaCheckPermission("wms:map:edit")
    @Log(title = "路径记录", businessType = BusinessType.INSERT)
    @PostMapping("/path/record")
    public R<Void> savePathRecord(@Validated(AddGroup.class) @RequestBody PathRecordBo bo) {
        // pathRecordService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 获取路径记录详情
     *
     * @param id 主键
     */
    @SaCheckPermission("wms:map:list")
    @GetMapping("/path/record/{id}")
    public R<PathRecordVo> getPathRecord(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        // return R.ok(pathRecordService.queryById(id));
        return R.ok();
    }

    /**
     * 删除路径记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("wms:map:remove")
    @Log(title = "路径记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/path/record/{ids}")
    public R<Void> removePathRecord(@NotNull(message = "主键不能为空") @PathVariable Long[] ids) {
        // pathRecordService.deleteWithValidByIds(List.of(ids), true);
        return R.ok();
    }
}

