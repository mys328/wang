package com.thinkwin.auth.mapper.db;

import com.thinkwin.common.model.db.SysUser;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SysUserMapper extends Mapper<SysUser> {
    /**
     * 根据条件模糊查询用户
     * @param searchParameter
     * @return
     */
    List<SysUser> selectUserLickCondition(String searchParameter);

    /**
     * 根据条件模糊查询用户
     * @param searchParameter
     * @return
     */
    List<SysUser> selectUserLickConditionNew(String searchParameter);

    /**
     * 可按员工姓名、全拼、手机号、邮箱等字段进行人员搜索
     * @param map
     * @return
     */
    List<SysUser> likeSeachUsersByCondition(Map map);

    /**
     * 根据组织机构id获取用户信息
     * @return
     */
    List<SysUser> likeSeachUsersByConditionByOrgIds(Map map);

    /**
     * 获取除离职外的所有员工数量
     * @param map
     * @return
     */
    Integer selectUserCount(Map map);

    /**
     * 根据条件查询用户
     * @return
     */
    List<SysUser> batchQuery(List list);

    /**
     * 根据用户状态获取用户数量
     * @param map
     * @return
     */
    Integer selectUserCountByStatus(Map map);

    /**
     * 根据用户的 状态和组织机构主键id获取用户列表功能
     * @param map
     * @return
     */
    List<SysUser> selectUserByUserStatus(Map map);

    /**
     * 根据用户的组织机构id、查询条件和用户状态查询数据
     * @param map
     * @return
     */
    Integer selectUserCountSeach(Map map);

    /**
     * 查询当前企业下除离职和删除人员外的总人数
     * @return
     */
    Integer selectUserTotalCount();

    /**
     * 根据需要查询用户的常用字段
     * @param map
     * @return
     */
    List<SysUser> findByUserMap(Map map);

    /**
     * 获取所有用户的id集合
     * @return
     */
    List<SysUser> getUserIds();


}