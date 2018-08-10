package com.thinkwin.console.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.console.SaasUserRole;

import java.util.List;

/**
 * 用户角色接口
 * User: wangxilei
 * Date: 2017/9/13
 * Company: thinkwin
 */
public interface SaasUserRoleService {
    /**
     * 获取用户的角色列表
     * @param userId 用户Id
     * @return
     */
    List<SaasRole> findSaasUserRolesByUserId(String userId);
    /**
     * 获取角色的用户列表
     * @param roleId 角色Id
     * @return
     */
    List<SaasUser> findSaasUsersByRoleId(String roleId);

    /**
     * 用户角色的添加功能
     * @param userId 用户Id
     * @param roleIds 角色Id列表
     * @return
     */
    boolean saveSaasUserRole(String userId,List<String> roleIds);

    /**
     * 初始化用户角色
     * @param userId 用户Id
     * @param roleIds 角色Id列表
     * @return
     */
    boolean addSaasUserRole(String userId,List<String> roleIds);

    /**
     * 删除用户的角色功能
     * @param userId 用户Id
     * @param roleIds 角色Id列表
     * @return
     */
    boolean deleteSaasUserRole(String userId,List<String> roleIds);

    /**
     * 清除用户角色
     * @param userId 用户Id
     * @return
     */
    boolean deleteSaasUserRole(String userId);

    /**
     * 根据用户的主键ID获取用户的角色列表信息
     * @param userId 用户Id
     * @param page 分页实体类
     * @return
     */
    List<SaasRole> selectSaasUserRole(String userId, BasePageEntity page);

    /**
     * 根据用户的主键ID获取用户的角色Id列表
     * @param userId 用户Id
     * @return
     */
    List<String> selectSaasUserRoleIds(String userId);

    /**
     * 根据用户的主键id获取用户拥有的角色信息
     * @param userId
     * @return
     */
    List<SaasUserRole> selectSaasUserRole(String userId);

    /**
     * 根据用户id和角色id判断该用户是否拥有该角色
     * @param userId 用户Id
     * @param roleId 角色Id
     * @return
     */
    List<SaasUserRole> getSaasUserRoleByUserIdAndRoleId(String userId, String roleId);
}
