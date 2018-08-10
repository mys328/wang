package com.thinkwin.auth.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.db.*;

import java.util.List;
import java.util.Map;

/**
 * User: yinchunlei
 * Date: 2017/5/24.
 * Company: thinkwin
 */
public interface UserService {
    //添加新用户功能
    public boolean saveUser(SysUser user,List<String> roleIds);
    //添加注册新租户信息
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

    //删除用户的角色功能
    public boolean deleteUserRole(String userId,List<String> roleIds);

    //根据用户的ID修改用户的角色信息
    public boolean updateUserRole(String userId,List<String> roleIds);

    //根据用户的ID修改用户的角色信息
    public boolean updateUserRole(SysUserRole sysUserRole);

    //根据用户的主键ID获取用户的角色信息
    public List<SysRole> selectUserRole(String userId,Page page);

    //根据用户的主键ID获取用户的角色信息  返回角色Id
    public List<String> selectUserRole(String userId);

    //根据用户信息获取用户的角色信息  返回角色Id
    public List<String> selectUserRole(SysUser user);

    //根据用户的主键ID获取用户的信息功能
    public SysUser selectUserByUserId(String userId);

    //根据组织机构主键id获取该组织下所有用户
    public Map selectUserByOrgId(String orgId,BasePageEntity basePageEntity);
    //根据组织机构主键id获取该组织下所有用户(人员组件中预订会议时使用)
    public Map selectUserByOrgId(String orgId,BasePageEntity basePageEntity,String startTime,String endTime,String conferenceId);

    //组件搜索查询功能接口
    public Map searchLike(String searchParameter,BasePageEntity basePageEntity);
    //组件搜索查询功能接口(扩展)
    public Map searchLike(String searchParameter,BasePageEntity basePageEntity,String startTime,String endTime,String conferenceId);

    /**
     * 根据用户id和角色id判断该用户是否拥有该角色
     * @param userId
     * @param roleId
     * @return
     */
    public List<SysUserRole> getUserRoleByUserIdAndRoleId(String userId,String roleId);

    /**
     * 根据租户id获取租户信息
     * @param tenantId
     * @return
     */
    public SaasTenantInfo selectSaasTenantInfoByTenantId(String tenantId);

    /**
     * 添加子管理员功能
     * @param userId
     * @param sonList
     * @return
     */
    public Integer addSonManager(String userId,List<String> sonList);

    /**
     * 判断用户是否（主、子、会议室）管理员
     * @param userId
     * @return
     */
    public boolean getRequestIdentity(String userId);

    /**
     * 判断用户是否有操作人员权限
     * @param userId
     * @return
     */
    public boolean isHandlePeoplePermission(String userId);
    /**
     * 删除子管理员功能
     * @param sonUserId
     * @return
     */
    public Integer delSonManagerBySonUserId(String sonUserId);

    /**
     * 移动某些人员至xx下
     * @param parentId
     * @param moveUsserIds
     * @return
     */
    public Integer usersMove(String parentId,List<String> moveUsserIds);

    /**
     * 添加会议室管理员
     * @param userId
     * @param managerIds
     * @return
     */
    public Integer addBoardroomManager(String userId,List<String> managerIds);

    /**
     * 删除会议室管理员
     * @param userId
     * @param moveIds
     * @return
     */
    public Integer delBoardroomManager(String userId,List<String> moveIds);

    /**
     * 查询所有会议室管理员
     * @param userId
     * @return
     */
    public List<SysUser> selectBoardroomManager(String userId);

    /**
     * 添加会议室预订专员功能
     * @param userId
     * @param commissionerIds
     * @return
     */
    public Integer addBoardroomCommissioner(String userId,List<String> commissionerIds);

    /**
     * 删除会议室预订专员功能
     * @param userId
     * @param commissionerIds
     * @return
     */
    public Integer delBoardroomCommissioner(String userId,List<String> commissionerIds);

    /**
     * 查看所有会议室预订专员功能
     * @param userId
     * @return
     */
    public List<SysUser> selectBoardroomCommissioner(String userId);

    /**
     * 获取管理设置中人员集合
     * @return
     */
    public Map selectManagementSettingPersons();

    /**
     * 根据人员的状态查询用户功能（带分页功能）
     * @param userStatus
     * @param pageEntity
     * @return
     */
    public PageInfo getUsersByUserStatus(Integer userStatus, BasePageEntity pageEntity,String orgId,String seachCondition);

    /**
     * 可按员工姓名、全拼、手机号、邮箱等字段进行人员搜索
     * @param parentId
     * @param seachCondition
     * @param pageEntity
     * @return
     */
    public PageInfo likeSeachUserByCondition(String parentId,String seachCondition,BasePageEntity pageEntity);

    /**
     * 获取系统所有的用户
     * @return
     */
    public Map selectSystemUsers(BasePageEntity basePageEntity);

    /**
     * 获取通讯录结构数据(带分页)
     * @param orgId
     * @param basePageEntity
     * @return
     */
    public Map selectAddressListStructure(String orgId,BasePageEntity basePageEntity);
    /**
     * 获取通讯录结构数据(不带分页)
     * @param orgId
     * @return
     */
    public List<SysUser> selectAddressListStructure(String orgId);

    /**
     * 获取用户所在的组织机构目录功能
     * @param orgId
     * @return
     */
    public String getUserCataLog(String orgId,List<SysOrganization> orgList);

    /**
     * 递归获取组织机构树
     * @param menuList
     * @param parentId
     * @return
     */
    // public JSONArray treeMenuList(JSONArray menuList, String parentId);

    /**
     * 根据组织机构主键id和查询参数获取用户数量信息
     * @param parentId
     * @param seachCondition
     * @return
     */

    public Map selectUserNumInfo2(String parentId,String seachCondition);

    /**
     * 根据用户的查询条件和状态获取符合条件的总数
     * @param orgId
     * @param seachCondition
     * @param userStatus
     * @return
     */
    public Map selectUserNumInfo3(String orgId,String seachCondition,Integer userStatus);

    /**
     * 查询当前企业下除离职和删除人员外的总人数
     * @return
     */
    public Integer selectUserTotalNum();

    /**
     * 方法名：selectNotDimissionPersonNum</br>
     * 描述：查询非离职人员</br>
     * 参数：</br>
     * 返回值：</br>
     */
    public List<SysUser> selectNotDimissionPerson();

    /**
     * 查询关注的OpenId的用户
     * @param openId
     * @param isSubscribe
     * @return
     */
    public SysUser findByOpenId(String openId,String isSubscribe);

    /**
     * 查询当前组织机构下的用户
     * @param orgId
     * @return
     */
    public List<SysUser> findByOrgId(String orgId);

    /**
     * 微信检查用户授权
     * @param tenantId
     * @param openId
     * @param userId
     * @return
     */
    public SysUser authSysUser(String tenantId,String openId,String userId);


    /**
     * 根据用户的主键id获取
     * @param userId
     * @return
     */
    public List<SysUserRole> getCurrentUserRoleIds(String userId);

    /**
     * 获取所有的组织机构并排序
     * @return
     */
    public List selectAllOrganizationAndOrderBy(String parentId);

    /**
     * 获取部门中个状态下的用户数量
     * @param orgId
     * @return
     */
    public Map getUserNumInfo1(String orgId);

    public List<SysUser> getUsersBybatchQuery(List<String> orgIds);

    /**
     * 存储本地图片信息功能
     * @param userId
     * @return
     */
    public boolean saveImageUrl(String userId,Map map,String imageId);

    public Map<String, String> getUploadInfo(String photo);

    /**
     * 根据图片id删除相关信息
     * @param companyLogo1
     * @return
     */
    public boolean deleteImageUrl(String companyLogo1);

    //查询租户下所有非离职人员
    public List<SysUser> findALL();

    /**
     * 根据角色主键id获取所有的关联关系功能
     * @param roleId
     * @return
     */
    public List<SysUserRole> getTerminalAdministratorsByRoleId(String roleId);


    /**
     * 添加会议显示终端管理员功能
     * @param userId
     * @param terminalAdminIdss
     * @return
     */
    public String addTerminalAdministrator(String userId,List<String> terminalAdminIdss);

    /**
     * 删除会议显示终端管理员功能
     * @param terminalAdminIds
     * @return
     */
    public String delTerminalAdministrator(List<String> terminalAdminIds);

}
