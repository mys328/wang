package com.thinkwin.console.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasPermission;

import java.util.List;

/**
 * 权限功能接口
 * User: wangxilei
 * Date: 2017/9/13
 * Company: thinkwin
 */
public interface SaasPermissionService {
    /**
     * 根据权限主键的id获取权限信息
     * @param permissionId 权限Id
     * @return
     */
    SaasPermission getSaasPermissionById(String permissionId);

    /**
     * 根据用户的主键ID获取用户的所有权限功能
     * @param userId 用户Id
     * @return
     */
    List<SaasPermission> selectSaasUserPermissionByUserId(String userId);

    /**
     * 添加新的权限信息
     * @param saasPermission 权限实体
     * @return
     */
    boolean saveSaasPermission(SaasPermission saasPermission);

    /**
     * 权限的批量删除功能
     * @param permissionIds 权限Id列表
     * @return
     */
    boolean deleteSaasPermission(List<String> permissionIds);

    /**
     * 根据权限的主键id删除相应的权限信息
     * @param permissionId 权限Id
     * @return
     */
    boolean deleteSaasPermissionById(String permissionId);

    /**
     * 权限的修改功能
     * @param permission 权限实体
     * @return
     */
    boolean updateSaasPermission(SaasPermission permission);

    /**
     * 带分页功能查询全部权限
     * @return
     */
    PageInfo<SaasPermission> selectSaasPermission(String condition, BasePageEntity page);

    /**
     * 查询权限信息
     * @param saasPermission 权限实体
     * @return
     */
    List<SaasPermission> getSaasPermission(SaasPermission saasPermission);
}
