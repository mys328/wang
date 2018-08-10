package com.thinkwin.console.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;

import java.util.List;

/**
 * 角色接口
 */
public interface SaasRoleService {
    /**
     * 获取所有角色列表
     * @param condition 条件
     * @return
     */
    PageInfo<SaasRole> findAllSaasRoles(String condition, BasePageEntity page);

    /*
    * 根据用户id 查询角色列表
    * */
    public List<SaasRole> findUserRolesByUserId(String userId);

    /**
     * 添加角色功能接口
     * @param saasRole 角色对象
     * @return
     */
    boolean saveSaasRole(SaasRole saasRole);

    /**
     * 根据角色主键ID删除角色功能接口
     * @param roleId 角色Id
     * @return
     */
    boolean deleteSaasRole(String roleId);

    /**
     * 修改角色的功能
     * @param saasRole 角色对象
     * @return
     */
    boolean updateSaasRole(SaasRole saasRole);

    /**
     * 查询所有的角色功能（带分页【未添加】）
     * @param page 分页实体类
     * @return
     */
    List<SaasRole> selectSaasRoles(BasePageEntity page);

    /**
     * 根据角色主键ID获取角色详细信息
     * @param roleId 角色Id
     * @return
     */
    SaasRole selectSaasRoleById(String roleId);

    /**
     * 根据条件获取角色详细信息
     * @param saasRole 角色对象
     * @return
     */
    List<SaasRole> selectSaasRole(SaasRole saasRole);
}
