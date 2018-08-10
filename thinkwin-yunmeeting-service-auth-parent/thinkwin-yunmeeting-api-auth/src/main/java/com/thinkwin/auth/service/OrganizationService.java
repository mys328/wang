package com.thinkwin.auth.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysOrganization;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/6/8.
 * Company: thinkwin
 */
public interface OrganizationService {

    /**
     *
     * 添加新的组织机构功能
     * @param sysOrganization
     * @return
     */
    public boolean saveOrganization(SysOrganization sysOrganization);
    /**
     *
     * 添加新的组织机构功能(导入使用)
     * @return
     */
    public boolean saveOrganizationNew(SysOrganization sysOrg);

    /**
     * 添加新的组织机构并返回新组织机构的主键id
     * @param sysOrganization
     * @return
     */
    public String saveOrganizationReturnString(SysOrganization sysOrganization);

    /**
     * 根据组织机构id删除相关组织机构信息
     * @param organizationId
     * @return
     */
    public int deleteOrganizationById(String organizationId);

    /**
     * 修改组织机构信息功能
     * @param sysOrganization
     * @return
     */
    public boolean updateOrganization(SysOrganization sysOrganization);

    /**
     * 根据组织机构主键id获取相关信息
     * @param organiztionId
     * @return
     */
    public SysOrganization selectOrganiztionById(String organiztionId);

    /**
     * 根据组织机构名称查询组织机构信息
     * @param organiztionName
     * @return
     */
    public List<SysOrganization> selectOrganiztionByName(String organiztionName);

    /**
     * 根据部门名称和父id查询组织机构信息
     * @return
     */
    public List<SysOrganization> selectOrganiztionByNameAndParentId(String deparNamee,String parentId);

    /**
     * 根据父id获取所有的同级下的组织机构
     * @param parentId
     * @return
     */
    public List<String> selectOrganiztionByParentId(String parentId);


    /**
     *查询所有组织机构信息
     */

    public List<SysOrganization> selectOrganiztions(String parentId);

    /**
     *查询所有组织机构信息
     */

    public List<SysOrganization> selectOrganiztions();
    /**
     * 查询所有组织机构信息(带分页)
     * @param pageEntity
     * @return
     */
    public PageInfo selectOrganiztionsByPage(BasePageEntity pageEntity);

    /**
     * 组织机构移动功能
     * @param parentId
     * @param moveOrgIds
     * @return
     */
    public Integer organiztionMove(String parentId,List<String> moveOrgIds);

    /**
     * 组织机构移动功能
     * @param parentId
     * @param moveType
     * @return
     */
    public Integer organiztionMove(String parentId,String moveOrgId,String moveType);


    /**
     * 根据orgaId 查询SysOrganization及其子节点信息
     * @param orgaId
     * @return
     */
    public SysOrganization findOrgaAndChildOrgaByOrgaId(String orgaId);

    /**
     * 查询orgaId有多少子节点
     * @param orgaId
     * @return
     */
    public Integer getChildrenCount(String orgaId);

    /**
     * 获取所有组织机构数量功能
     * @return
     */
    public Integer getOrganizationNum();
}
