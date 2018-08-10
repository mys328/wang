package com.thinkwin.auth.mapper.db;

import com.thinkwin.common.model.db.SysOrganization;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysOrganizationMapper extends Mapper<SysOrganization> {

        /**
         * 根据条件模糊查询组织机构
         * @param searchParameter
         * @return
         */
        List<SysOrganization> selectUserLikeCondition(String searchParameter);

        /**
         * 模糊查询所有
         * @param searchParameter
         * @return
         */
        List<SysOrganization> selectUserLikeConditionAll(String searchParameter);

        /**
         * 根据组织机构父id获取相关数据
         * @return
         */
        List<SysOrganization> selectOrgByParentId(String parentId);

        /**
         * 排序查询组织机构数据
         * @return
         */
        List<SysOrganization> selectAllSysOrganiztionsAndOrderBy(String parentId);

        /**
         * 根据部门名称查询部门信息
         * @param orgName
         * @return
         */
       List<SysOrganization> selectOrganizationByName(String orgName);

        /**
         * 查询orgaId有多少子节点
         * @param orgaId
         * @return
         */
        public Integer getChildrenCount(String orgaId);

    /**
     * 查询除父id不等于0外的所有部门
     * @return
     */
    public List<SysOrganization> selectOrganizationNum();

        }