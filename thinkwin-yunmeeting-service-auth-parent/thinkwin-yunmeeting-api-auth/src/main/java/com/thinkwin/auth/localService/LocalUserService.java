package com.thinkwin.auth.localService;

import com.thinkwin.common.model.db.SysRole;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysUserRole;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/5/27.
 * Company: thinkwin
 */
public interface LocalUserService {
    //添加新用户功能
    public boolean saveUser(SysUser user,List<String> roleds,String typee);

    //根据用户名或手机号判断用户是否存在
    public boolean selectUserName(String userName);

    //根据用户的主键ID删除用户功能
    public boolean deleteUserByUserId(String userId);

    //根据用户的主键ID修改用户的信息
    public boolean updateUserByUserId(SysUser sysUser);

    //根据条件查询用户列表功能
    public List<SysUser> selectUser(SysUser sysUser);

    //用户角色的添加功能
    public boolean saveUserRole(String userId,List<String> roleIds);

    //获取所有角色列表
    public List<SysRole> findAllRoles();
    //用户角色的添加功能
    public boolean userRoleNumber(String userId,String roleId);

    /**
     * 根据用户的主键id获取用户拥有的角色id
     * @param userId
     * @return
     */
    public List<SysUserRole> selectUserRole(String userId);

    /**
     * 根据条件获取用户总数量
     * @return
     */
    public Integer selectUserTotalNum(Example example);

    /**
     * 根据用户的主键id获取用户角色功能
     * @param userId
     * @return
     */
    public List<SysUserRole> getCurrentUserRoleIds(String userId);

    /**
     * 根据用户的主键id获取用户信息
     * @return
     */
    public SysUser getUserInfoByUserId(String userId);
}
