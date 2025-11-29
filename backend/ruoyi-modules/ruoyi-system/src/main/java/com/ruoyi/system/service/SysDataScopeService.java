package com.ruoyi.system.service;

import org.springframework.stereotype.Service;

/**
 * 数据权限 实现
 * <p>
 * 注意: 此Service内不允许调用标注`数据权限`注解的方法
 * 例如: deptMapper.selectList 此 selectList 方法标注了`数据权限`注解 会出现循环解析的问题
 * <p>
 * 由于已移除部门功能，相关方法返回null
 *
 * @author Lion Li
 */
@Service("sdss")
public class SysDataScopeService {

    /**
     * 获取角色自定义数据权限
     * 已移除部门功能，返回null
     *
     * @param roleId 角色ID
     * @return null
     */
    public String getRoleCustom(Long roleId) {
        return null;
    }

    /**
     * 获取部门及子部门数据权限
     * 已移除部门功能，返回null
     *
     * @param deptId 部门ID
     * @return null
     */
    public String getDeptAndChild(Long deptId) {
        return null;
    }

}
