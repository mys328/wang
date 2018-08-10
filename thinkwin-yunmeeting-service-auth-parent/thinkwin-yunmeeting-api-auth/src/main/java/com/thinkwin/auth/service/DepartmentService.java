package com.thinkwin.auth.service;

import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.vo.organizationVo.DepartmentVo;

import java.util.List;
import java.util.Map;

/**
 * 部门插件service
 */
public interface DepartmentService {


    /**
     * 递归查询上级机构
     * @param organizationList
     * @param organizations
     */
    public void getOrganizationListArrayList(List<SysOrganization> organizationList, List<DepartmentVo> departmentVos);

    /**
     * 获取部门信息
     * @param orgId 部门id
     * @param searchKey 搜索关键字
     * @param type 类型
     * @param pageEntity 分页
     * @return
     */
    public Map<String,Object> getOrganizationInfo(String orgId, String searchKey, String type,String currentPage,String pageSize,String modular);


}
