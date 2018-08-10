package com.thinkwin.auth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.mapper.db.SysOrganizationMapper;
import com.thinkwin.auth.service.DepartmentService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.vo.organizationVo.DepartmentVo;
import com.thinkwin.common.vo.organizationVo.OrganizationsVo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门实现
 * Created by Administrator on 2018/3/23 0023.
 */
@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {


    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    @Override
    public void getOrganizationListArrayList(List<SysOrganization> organizationList, List<DepartmentVo> departmentVos) {
        try {
             Example example = null;
            for (SysOrganization organization : organizationList) {
                example = new Example(SysOrganization.class);
                example.or().andEqualTo("id",organization.getParentId());
                DepartmentVo vo = new DepartmentVo();
                vo.setId(organization.getId());
                vo.setOrgName(organization.getOrgName());
                List<SysOrganization> OrganizationListById = sysOrganizationMapper.selectByExample(example);
                if (OrganizationListById.size() > 0) {
                    departmentVos.add(vo);
                    getOrganizationListArrayList(OrganizationListById, departmentVos);
                } else {
                    departmentVos.add(vo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getOrganizationInfo(String orgId, String searchKey, String type,String currentPage,String pageSize,String modular) {

        Map<String,Object> map = new HashMap<String,Object> ();
        int i=1;
        if("1".equals(modular)){
            map.put("sysOrganizations",getMailListAssembly(orgId,searchKey,type,currentPage, pageSize));
            map.put("total",getTotal(searchKey,orgId,type));
        }
        return map;
    }


    public static void main(String[] args) {

    }

    /**
     * 获取当前机构下级是否有子机构
     * @param organizations
     * @return
     */
    public List<OrganizationsVo> getOrganizationsVo(List<SysOrganization> organizations, List<DepartmentVo> departmentVos){

        List<OrganizationsVo> vos = new ArrayList<>();
        for(SysOrganization org : organizations){
            OrganizationsVo vo = new OrganizationsVo();
            vo.setDepartmentLevel(departmentVos);
            vo.setId(org.getId());
            vo.setOrgName(org.getOrgName());
            Example example = new Example(SysOrganization.class);
            example.or().andEqualTo("parentId",org.getId());
            List<SysOrganization> sys = this.sysOrganizationMapper.selectByExample(example);
            if(sys.size() != 0){
                vo.setLeaf(true);
            }else{
                vo.setLeaf(false);
            }
           vos.add(vo);
        }
        return vos;
    }

    /**
     * 获取上级所有路径
      * @param departmentVos
     * @return
     */
  public List<DepartmentVo> sortDepartmentVoList(List<DepartmentVo> departmentVos){

      List<DepartmentVo> vos = new ArrayList<DepartmentVo>();
      for(int i=departmentVos.size()-1; i>0;i--){
              vos.add(departmentVos.get(i));
      }
         return vos;
  }


   //获取通讯录组件
   public List<OrganizationsVo>  getMailListAssembly(String orgId,String searchKey,String type,String currentPage,String pageSize){

       List<DepartmentVo> departmentVos = new ArrayList<DepartmentVo>();
       OrganizationsVo organizationsVo = new OrganizationsVo();
       List<OrganizationsVo> vos = new ArrayList<OrganizationsVo>();
       List<SysOrganization> orgs = new ArrayList<SysOrganization>();

       //是否为搜索
       if(StringUtils.isBlank(searchKey)) {
           //是否为根节点
           if (!"1".equals(orgId) && "0".equals(type)) {
               SysOrganization org = this.sysOrganizationMapper.selectByPrimaryKey(orgId);
               orgs.add(org);
               getOrganizationListArrayList(orgs, departmentVos);
               departmentVos = sortDepartmentVoList(departmentVos);
               //获取同级的子机构
               if(currentPage != null){
                   PageHelper.startPage(Integer.parseInt(currentPage), Integer.parseInt(pageSize));
               }
               Example example = new Example(SysOrganization.class);
               example.orderBy("compositor");
               example.or().andEqualTo("parentId", org.getParentId());
               List<SysOrganization> organizations = this.sysOrganizationMapper.selectByExample(example);
               vos = getOrganizationsVo(organizations, departmentVos);
           } else {
               if(currentPage != null){
                   PageHelper.startPage(Integer.parseInt(currentPage), Integer.parseInt(pageSize));
               }
               //获取同级的子机构
               Example example = new Example(SysOrganization.class);
               example.orderBy("compositor");
               example.or().andEqualTo("parentId", orgId);
               orgs = this.sysOrganizationMapper.selectByExample(example);
               vos = searchMethod(orgs);
           }
       }else{
           if(currentPage != null){
               PageHelper.startPage(Integer.parseInt(currentPage), Integer.parseInt(pageSize));
           }
           //搜索关键字
           searchKey = "%" + searchKey + "%";
           List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectUserLikeConditionAll(searchKey);
          return searchMethod(sysOrganizations);
       }
      return vos;

   }


   public List<OrganizationsVo> searchMethod(List<SysOrganization> sysOrganizations){
       List<OrganizationsVo> vos = new ArrayList<OrganizationsVo>();
       if(null != sysOrganizations && sysOrganizations.size() > 0){
           //过滤根节点
            for(SysOrganization org : sysOrganizations){
                if(!"1".equals(org.getId())){
                    List<DepartmentVo> departmentVos = new ArrayList<DepartmentVo>();
                    List<SysOrganization> orgs = new ArrayList<SysOrganization>();
                    orgs.add(org);
                    //递归获取上级所有目录
                    getOrganizationListArrayList(orgs, departmentVos);
                    //获取上级路径
                    departmentVos = sortDepartmentVoList(departmentVos);
                    vos.add(getOrganizationsVo(org, departmentVos));
                }
            }
       }
        return vos;

   }
    /**
     * 获取当前机构下级是否有子机构不同机构
     * @param organizations
     * @return
     */
    public OrganizationsVo getOrganizationsVo(SysOrganization org, List<DepartmentVo> departmentVos){
            OrganizationsVo vo = new OrganizationsVo();
            vo.setDepartmentLevel(departmentVos);
            vo.setId(org.getId());
            vo.setOrgName(org.getOrgName());
            Example example = new Example(SysOrganization.class);
            example.or().andEqualTo("parentId",org.getId());
            List<SysOrganization> sys = this.sysOrganizationMapper.selectByExample(example);
            if(sys.size() != 0){
                vo.setLeaf(true);
            }else{
                vo.setLeaf(false);
            }
        return vo;
    }

    public Integer getTotal(String searchKey,String orgId,String type){

        List<SysOrganization> orgs = new ArrayList<SysOrganization>();
        int num = 0;
        if(StringUtils.isBlank(searchKey)) {
            //是否为根节点
            if (!"1".equals(orgId) && "0".equals(type)) {
                SysOrganization org = this.sysOrganizationMapper.selectByPrimaryKey(orgId);
                orgs.add(org);
                //获取同级的子机构
                Example example = new Example(SysOrganization.class);
                example.orderBy("compositor");
                example.or().andEqualTo("parentId", org.getParentId());
                List<SysOrganization> organizations = this.sysOrganizationMapper.selectByExample(example);
                num = organizations.size();
            } else {
                //获取同级的子机构
                Example example = new Example(SysOrganization.class);
                example.orderBy("compositor");
                example.or().andEqualTo("parentId", orgId);
                orgs = this.sysOrganizationMapper.selectByExample(example);
                num = orgs.size();
            }
        }else{
            //搜索关键字
            searchKey = "%" + searchKey + "%";
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectUserLikeConditionAll(searchKey);
            num = sysOrganizations.size();
        }

        return num;
    }


}
