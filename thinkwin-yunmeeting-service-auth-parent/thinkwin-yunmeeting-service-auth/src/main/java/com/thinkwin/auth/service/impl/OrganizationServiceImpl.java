package com.thinkwin.auth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.mapper.db.SysOrganizationMapper;
import com.thinkwin.auth.mapper.db.SysUserMapper;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PingYinUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/6/8.
 * Company: thinkwin
 */
@Service("organizationService")
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    //添加新的组织机构功能
    public boolean saveOrganization(SysOrganization sysOrganization){
        if(null != sysOrganization){
           String parentIdd = sysOrganization.getParentId();
           if(StringUtils.isBlank(parentIdd)){
             parentIdd = "0";
           }
            SysOrganization sysOrganization1 = new SysOrganization();
            sysOrganization1.setParentId(parentIdd);
            List<SysOrganization> sysOrganizationList = sysOrganizationMapper.select(sysOrganization1);
            ////////////////////////////////////////注释部分为添加新的组织机构时放在最后位置////////////////////////////////////////
            Integer comp = 0;
            if(null != sysOrganizationList && sysOrganizationList.size() > 0){
                List<Integer> list = new ArrayList();
                for (SysOrganization sysOrg:sysOrganizationList) {
                    Integer compo = sysOrg.getCompositor();
                    if(null != compo){
                        list.add(compo);
                    }
                }
                comp = Collections.max(list);
            }
            sysOrganization.setCompositor(comp+1);
            /////////////////////////////////////////注释部分为添加新的组织机构时放在最前位置////////////////////////////////////////////////
//            if(null != sysOrganizationList && sysOrganizationList.size() > 0){
//                for (SysOrganization sysOrg:sysOrganizationList) {
//                    SysOrganization sysOrganizationn = new SysOrganization();
//                    sysOrganizationn.setId(sysOrg.getId());
//                    sysOrganizationn.setCompositor(sysOrg.getCompositor()+1);
//                    int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganizationn);
//                    if(i!=1){
//                        return false;
//                    }
//                }
//            }
            String id = sysOrganization.getId();
            if(StringUtils.isBlank(id)) {
                id = CreateUUIdUtil.Uuid();
            }
           // sysOrganization.setCompositor(1);
            sysOrganization.setId(id);
            String orgName = sysOrganization.getOrgName();
            if(null != orgName && !"".equals(orgName)){
                String orgNamePinYin = PingYinUtil.getPingYin(orgName);
                if(null != orgNamePinYin && !"".equals(orgNamePinYin)){
                    sysOrganization.setOrgNamePinyin(orgNamePinYin);
                }
            }
            if(StringUtils.isBlank(parentIdd)){
                sysOrganization.setParentId("0");
            }else{
                sysOrganization.setParentId(parentIdd);
            }
            sysOrganization.setStatus(1);
            sysOrganization.setCreateTime(new Date());
            int num = sysOrganizationMapper.insertSelective(sysOrganization);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * 添加新的组织机构功能(导入使用)
     * @return
     */
    public boolean saveOrganizationNew(SysOrganization sysOrg11){
        if(null != sysOrg11){
            String parentIdd = sysOrg11.getParentId();
            if(StringUtils.isBlank(parentIdd)){
                parentIdd = "0";
            }
            SysOrganization sysOrganization1 = new SysOrganization();
            sysOrganization1.setParentId(parentIdd);
            List<SysOrganization> sysOrganizationList = sysOrganizationMapper.select(sysOrganization1);
            Integer comp = 0;
            if(null != sysOrganizationList && sysOrganizationList.size() > 0){
                List<Integer> list = new ArrayList();
                for (SysOrganization sysOrg:sysOrganizationList) {
                    Integer compo = sysOrg.getCompositor();
                    if(null != compo){
                        list.add(compo);
                    }
                }
                comp = Collections.max(list);
            }
            sysOrg11.setCompositor(comp+1);
            String id = sysOrg11.getId();
            if(StringUtils.isBlank(id)) {
                id = CreateUUIdUtil.Uuid();
            }
            sysOrg11.setId(id);
            String orgName = sysOrg11.getOrgName();
            if(null != orgName && !"".equals(orgName)){
                String orgNamePinYin = PingYinUtil.getPingYin(orgName);
                if(null != orgNamePinYin && !"".equals(orgNamePinYin)){
                    sysOrg11.setOrgNamePinyin(orgNamePinYin);
                }
            }
            if(StringUtils.isBlank(parentIdd)){
                sysOrg11.setParentId("0");
            }else{
                sysOrg11.setParentId(parentIdd);
            }
            sysOrg11.setCreateTime(new Date());
            int num = sysOrganizationMapper.insertSelective(sysOrg11);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 添加新的组织机构并返回新组织机构的主键id
     * @param sysOrganization
     * @return
     */
    public String saveOrganizationReturnString(SysOrganization sysOrganization){
        if(null != sysOrganization){
            String parentIdd = sysOrganization.getParentId();
            if(StringUtils.isBlank(parentIdd)){
                parentIdd = "0";
            }
            SysOrganization sysOrganization1 = new SysOrganization();
            sysOrganization1.setParentId(parentIdd);
            List<SysOrganization> sysOrganizationList = sysOrganizationMapper.select(sysOrganization1);
            List<Integer> lllist = new ArrayList();
            if(null != sysOrganizationList && sysOrganizationList.size() > 0){
                for (SysOrganization sysOrg:sysOrganizationList) {
                    ///////////////////////////注释掉部分为新建部门在最上方////////////////////////////////////////
//                    SysOrganization sysOrganizationn = new SysOrganization();
//                    sysOrganizationn.setId(sysOrg.getId());
//                    sysOrganizationn.setCompositor(sysOrg.getCompositor()+1);
//                    int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganizationn);
//                    if(i!=1){
//                        return null;
//                    }
                    if(null != sysOrg){
                        if(null != sysOrg.getCompositor() && sysOrg.getCompositor()> 0) {
                            lllist.add(sysOrg.getCompositor());
                        }
                    }
                }
            }
            String organizationId = CreateUUIdUtil.Uuid();
            if(lllist!=null && lllist.size() > 0){
                sysOrganization.setCompositor(Collections.max(lllist)+1);
            }else {
                sysOrganization.setCompositor(1);
            }
            sysOrganization.setId(organizationId);
            String orgName = sysOrganization.getOrgName();
            if(null != orgName && !"".equals(orgName)){
                String orgNamePinYin = PingYinUtil.getPingYin(orgName);
                if(null != orgNamePinYin && !"".equals(orgNamePinYin)){
                    sysOrganization.setOrgNamePinyin(orgNamePinYin);
                }
            }
            if(StringUtils.isBlank(parentIdd)){
                sysOrganization.setParentId("0");
            }else{
                sysOrganization.setParentId(parentIdd);
            }
            sysOrganization.setCreateTime(new Date());
            int num = sysOrganizationMapper.insertSelective(sysOrganization);
            if(num > 0){
                return organizationId;
            }
        }
        return null;
    }
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 根据组织机构id删除相关组织机构信息
     * @param organizationId
     * @return
     */
    public int deleteOrganizationById(String organizationId){
        if(StringUtils.isNotBlank(organizationId)) {
            SysOrganization sysOrganization1 = new SysOrganization();
            sysOrganization1.setParentId(organizationId);
            List<SysOrganization> select = sysOrganizationMapper.select(sysOrganization1);
            if(null != select && select.size() > 0){
                return 2;
            }
            /*SysUser sysUser = new SysUser();
            sysUser.setOrgId(organizationId);
            List<SysUser> sysUsers = sysUserMapper.select(sysUser);*/
            Example example = new Example(SysUser.class);
            example.createCriteria().andEqualTo("orgId",organizationId).andNotEqualTo("status",89);
            List<SysUser> sysUsers = sysUserMapper.selectByExample(example);
             if(null == sysUsers||sysUsers.size() == 0){
            SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(organizationId);
            String parentId = sysOrganization.getParentId();
            Integer compositor = sysOrganization.getCompositor();
            int num = sysOrganizationMapper.deleteByPrimaryKey(organizationId);
            if (num > 0) {
                if (StringUtils.isNotBlank(parentId)) {
                    SysOrganization sysOrganizationn = new SysOrganization();
                    sysOrganizationn.setParentId(parentId);
                    List<SysOrganization> sysOrganizations = sysOrganizationMapper.select(sysOrganizationn);
                    if (null != sysOrganizations && sysOrganizations.size() > 0) {
                        for (SysOrganization sysOrg : sysOrganizations) {
                            Integer compositor1 = sysOrg.getCompositor();
                            if (compositor1 > compositor) {
                                Integer integer = updateOrganizationCompositorDown(sysOrg.getId(), compositor1);
                                if (integer < 1) {
                                    return 0;
                                }
                            }
                        }
                    }
                }
                return 1;
            }
        }else {
                 return 2;
             }
        }
        return 0;
    }

    //修改组织机构信息功能
    public boolean updateOrganization(SysOrganization sysOrganization){
        if(null != sysOrganization){
            String organizationId = sysOrganization.getId();
            if(null != organizationId && !"".equals(organizationId)){
                String orgName = sysOrganization.getOrgName();
                if(StringUtils.isNotBlank(orgName)){
                    String orgNamePinyin = PingYinUtil.getPingYin(orgName);
                    if(null != orgNamePinyin && !"".equals(orgNamePinyin)){
                        sysOrganization.setOrgNamePinyin(orgNamePinyin);
                    }
                }
                sysOrganization.setModifyTime(new Date());
                int num = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganization);
                if(num > 0){
                    return true;
                }
            }
        }
        return false;
    }

    //根据组织机构主键id获取相关信息
    public SysOrganization selectOrganiztionById(String organiztionId){
        if(null != organiztionId && !"".equals(organiztionId)){
            return sysOrganizationMapper.selectByPrimaryKey(organiztionId);
        }
        return null;
    }
    /**
     * 根据组织机构名称查询组织机构信息
     * @param organiztionName
     * @return
     */
    public List<SysOrganization> selectOrganiztionByName(String organiztionName){
        if(null != organiztionName && !"".equals(organiztionName)){
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectOrganizationByName(organiztionName);
            return sysOrganizations;
        }
        return null;
    }

    /**
     * 根据部门名称和父id查询组织机构信息
     * @return
     */
    public List<SysOrganization> selectOrganiztionByNameAndParentId(String deparNamee,String parentId){
        if(null != deparNamee && !"".equals(deparNamee)){
            Example example = new Example(SysOrganization.class);
            example.createCriteria().andEqualTo("parentId",parentId).andEqualTo("orgName",deparNamee);
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectByExample(example);
            return sysOrganizations;
        }
        return null;
    }
    /**
     * 根据父id获取所有的同级下的组织机构
     * @param parentId
     * @return
     */
    public List<String> selectOrganiztionByParentId(String parentId){
        List list = new ArrayList();
        SysOrganization sysOrganization = new SysOrganization();
        sysOrganization.setParentId(parentId);
        List<SysOrganization> orgs = sysOrganizationMapper.select(sysOrganization);
        if(null != orgs && orgs.size() > 0){
            for (SysOrganization sysOrg:orgs) {
                if(null != sysOrg){
                    Integer compositor = sysOrg.getCompositor();
                    if(null != compositor && compositor != 0) {
                        list.add(compositor);
                    }
                }
            }
        }
        return list;
    }
    //根据组织机构id查询所有组织机构信息（加排序功能)
    public List<SysOrganization> selectOrganiztions(String parentId){
        List<SysOrganization> list = sysOrganizationMapper.selectAllSysOrganiztionsAndOrderBy(parentId);
         return list;
    }
    //查询所有组织机构信息
    public List<SysOrganization> selectOrganiztions(){
        List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectAll();
        return sysOrganizations;
    }

    //查询所有组织机构信息(带分页)
    public PageInfo selectOrganiztionsByPage(BasePageEntity pageEntity){
        PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectAll();
       return new PageInfo<>(sysOrganizations);//将查询的信息封装到pageinfo中
    }

    /**
     * 组织机构移动功能
     * @param parentId
     * @param moveOrgIds
     * @return
     */
    public Integer organiztionMove(String parentId,List<String> moveOrgIds){
        boolean status;
        if(null != moveOrgIds && moveOrgIds.size() > 0) {
            if(null != parentId && !"".equals(parentId)){
                if(!"0".equals(parentId)){
                    SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(parentId);
                    if(null != sysOrganization){
                        status = getStatus(parentId,moveOrgIds);
                        if(status) {
                            //表示移动成功
                            return 1;
                        }else {
                            //表示移动失败
                            return 2;
                        }
                    }else {
                        //表示父id不合法
                        return 0;
                    }
                }
                status = getStatus(parentId,moveOrgIds);
                if(status) {
                    return 1;
                }else {
                    return 2;
                }
            }else{
                status = getStatus("0",moveOrgIds);
                if(status) {
                    return 1;
                }else {
                    return 2;
                }
            }
        }
        return 2;
    }

    /**
     * 组织机构移动功能(新)
     * @param parentId
     * @param moveType//移动的类型 1：移动至parentId的上方 2：移动至parentId的子级 3：移动至parentId的下方
     * @return
     */
    public Integer organiztionMove(String parentId,String moveOrgId,String moveType){
        if(StringUtils.isNotBlank(moveOrgId) && StringUtils.isNotBlank(moveType) && StringUtils.isNotBlank(parentId)) {
                //该移动功能支持三种类型的移动  1：移动至parentId上方的情况
                if("1".equals(moveType)) {
                   return organiztionMoveBefore(parentId,moveOrgId);
                    //2：移动至parentId的子级情况下
                }else if ("2".equals(moveType)) {
                   return organiztionMoveChildren(parentId,moveOrgId);
                    //3：移动至parentId下方的情况
                }else {
                   return organiztionMoveAfter(parentId,moveOrgId);
                }
        }
        return 0;
    }


//    /**
//     * 把某些组织移动至某个组织机构的前方
//     * @param parentId
//     * @param moveOrgId
//     * @return 返回值 1：成功 2：失败 0：参数有误
//     */
//    public Integer organiztionMoveBefore(String parentId,String moveOrgId){
//        if(StringUtils.isNotBlank(parentId) && StringUtils.isNotBlank(moveOrgId)){
//            SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(parentId);//参考机构id
//            SysOrganization sysOrganization3 = sysOrganizationMapper.selectByPrimaryKey(moveOrgId);//移动机构id
//            if(null != sysOrganization){
//                Integer compositor = sysOrganization.getCompositor();//参考机构id的排序号
//                if(null != compositor && compositor != 0){
//                    String parentId1 = sysOrganization.getParentId();
//                    SysOrganization sysOrganization1 = new SysOrganization();
//                    sysOrganization1.setParentId(parentId1);
//                    List<SysOrganization> sysOrganizations = sysOrganizationMapper.select(sysOrganization1);
//                    Integer newCompositor = sysOrganization3.getCompositor();//移动机构id的排序号
//                    if(null != sysOrganizations && sysOrganizations.size() > 0){
//                        for (SysOrganization sysOrganizationn:sysOrganizations) {
//                            Integer compositor1 = sysOrganizationn.getCompositor();//循环出的机构id排序号
//                            if(newCompositor > compositor) {//移动机构id的排序号和参考机构id的排序号比较
//                                if (compositor1 >= compositor && compositor1 < newCompositor) {
//                                    Integer integer = updateOrganizationCompositor(sysOrganizationn.getId(), compositor1);
//                                    if (null != integer && integer == 2) {
//                                        return 2;
//                                    }
//                                }
//                            }else{
//                                if(compositor1 <= compositor && compositor1 > newCompositor){//循环出的机构id排序号和参考机构id的排序号做比较
//                                    Integer integer = updateOrganizationCompositorDown(sysOrganizationn.getId(), compositor1);
//                                    if (null != integer && integer == 2) {
//                                        return 2;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    SysOrganization sysOrganization2 = new SysOrganization();
//                    sysOrganization2.setId(moveOrgId);
//                    sysOrganization2.setParentId(parentId1);
//                    sysOrganization2.setCompositor(compositor);
//                    int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganization2);
//                    if(i != 1){
//                        return 2;
//                    }else {
//                        return 1;
//                    }
//
//                }
//            }
//        }
//        return 0;
//    }

    /**
     * 把某些组织移动至某个组织机构的前方
     * @param parentId
     * @param moveOrgId
     * @return 返回值 1：成功 2：失败 0：参数有误
     */
    public Integer organiztionMoveBefore(String parentId,String moveOrgId){
        if(StringUtils.isNotBlank(parentId) && StringUtils.isNotBlank(moveOrgId)){
            SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(parentId);//参考机构id
            if(null != sysOrganization){
                Integer compositor = sysOrganization.getCompositor();//参考机构id的排序号
                String parentId1 = sysOrganization.getParentId();//参考机构的父id
                if(null != compositor && StringUtils.isNotBlank(parentId1)){
                    List<SysOrganization> sysOrganizations = this.selectOrganiztions(parentId1);
                    if(null != sysOrganizations && sysOrganizations.size() > 0){
                        for (SysOrganization sysOrg:sysOrganizations) {
                            if(null != sysOrg){
                                Integer compositor1 = sysOrg.getCompositor();
                                if(compositor1>=compositor) {
                                    SysOrganization sysOoRg = new SysOrganization();
                                    sysOoRg.setId(sysOrg.getId());
                                    sysOoRg.setCompositor(compositor1+1);
                                    sysOoRg.setModifyTime(new Date());
                                    int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOoRg);
                                    if (i < 1) {
                                        return 2;
                                    }
                                }
                            }
                        }
                    }
                }
                SysOrganization sysOorg = new SysOrganization();
                sysOorg.setId(moveOrgId);
                sysOorg.setParentId(parentId1);
                sysOorg.setCompositor(compositor);
                sysOorg.setModifyTime(new Date());
                int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOorg);
                if(i<1){
                    return 2;
                }else {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * 把某些组织移动为某个组织机构的子级
     * @param parentId
     * @param moveOrgId
     * @return 返回值 1：成功 2：失败 0：参数有误
     */
    public Integer organiztionMoveChildren(String parentId,String moveOrgId){
        if(StringUtils.isNotBlank(parentId) && StringUtils.isNotBlank(moveOrgId)) {
            if(!"0".equals(parentId)){
            SysOrganization sysOrganization = new SysOrganization();
            sysOrganization.setParentId(parentId);
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.select(sysOrganization);
            SysOrganization sysOrganization2 = sysOrganizationMapper.selectByPrimaryKey(moveOrgId);
            List<Integer> list = new ArrayList();
            if (null != sysOrganizations && sysOrganizations.size() > 0) {
                for (SysOrganization sysOrg : sysOrganizations) {
                    Integer compositor = sysOrg.getCompositor();
                    if (null != compositor) {
                        list.add(compositor);
                    }
                }
            } else {
                list.add(0);
            }
            Integer max = Collections.max(list);
            SysOrganization sysOrganizationn = new SysOrganization();
            sysOrganizationn.setId(moveOrgId);
            sysOrganizationn.setParentId(parentId);
            sysOrganizationn.setCompositor(max + 1);
            int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganizationn);
            if (i != 1) {
                return 2;
            } else {
                if (null != sysOrganization2) {
                    Integer compositor = sysOrganization2.getCompositor();
                    String parentId1 = sysOrganization2.getParentId();
                    if (StringUtils.isNotBlank(parentId1)) {
                        SysOrganization sysOrganization1 = new SysOrganization();
                        sysOrganization1.setParentId(parentId1);
                        List<SysOrganization> sysOrganizationList = sysOrganizationMapper.select(sysOrganization1);
                        if (null != sysOrganizationList && sysOrganizationList.size() > 0) {
                            for (SysOrganization sysOrg : sysOrganizationList) {
                                Integer compositor1 = sysOrg.getCompositor();
                                if (compositor1 > compositor) {
                                    SysOrganization sysOrganization3 = new SysOrganization();
                                    sysOrganization3.setId(sysOrg.getId());
                                    sysOrganization3.setCompositor(compositor1 - 1);
                                    sysOrganization3.setModifyTime(new Date());
                                    int i1 = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganization3);
                                    if (i1 != 1) {
                                        return 2;
                                    }
                                }
                            }
                        }
                    }
                }
                return 1;
            }
        }
        return 3;
        }
        return 0;
    }

//    /**
//     * 移动某些组织至某个组织机构的下方
//     * @param parentId
//     * @param moveOrgId
//     * @return 返回值 1：成功 2：失败 0：参数有误
//     */
//    public Integer organiztionMoveAfter(String parentId,String moveOrgId){
//        if(StringUtils.isNotBlank(parentId) && StringUtils.isNotBlank(moveOrgId)){
//            SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(parentId);
//            SysOrganization sysOrganization3 = sysOrganizationMapper.selectByPrimaryKey(moveOrgId);
//            if(null != sysOrganization){
//                Integer compositor = sysOrganization.getCompositor();//参考机构id排序号
//                if(null != compositor && compositor != 0){
//                    String parentId1 = sysOrganization.getParentId();
//                    SysOrganization sysOrganization1 = new SysOrganization();
//                    sysOrganization1.setParentId(parentId1);
//                    List<SysOrganization> sysOrganizations = sysOrganizationMapper.select(sysOrganization1);
//                    Integer newCompositor = sysOrganization.getCompositor();//移动的组织机构id的排序号
//                    SysOrganization sysOrganization2 = new SysOrganization();
//                    if(null != sysOrganizations && sysOrganizations.size() > 0){
//                        for (SysOrganization sysOrganizationn:sysOrganizations) {
//                            Integer compositor1 = sysOrganizationn.getCompositor();
//                            if(newCompositor > compositor){//移动机构id的排序号和参考机构id的排序号比较
//                            if (compositor1 > compositor && compositor1 < newCompositor) {
//                                Integer integer = updateOrganizationCompositor(sysOrganizationn.getId(), compositor1);
//                                if (null != integer && integer == 2) {
//                                    return 2;
//                                }
//                            }
//                        }else{
//                                if(compositor1 <= compositor && compositor1 > newCompositor){
//                                    Integer integer = updateOrganizationCompositorDown(sysOrganizationn.getId(), compositor1);
//                                    if (null != integer && integer == 2) {
//                                        return 2;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    sysOrganization2.setId(moveOrgId);
//                    sysOrganization2.setParentId(parentId1);
//                    if(newCompositor > compositor){
//                        sysOrganization2.setCompositor(compositor+1);
//                    }else {
//                        sysOrganization2.setCompositor(compositor);
//                    }
//                    int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganization2);
//                    if(i != 1){
//                        return 2;
//                    }else {
//                        return 1;
//                    }
//
//                }
//            }
//        }
//        return 0;
//    }

    /**
     * 移动某些组织至某个组织机构的下方
     * @param parentId
     * @param moveOrgId
     * @return 返回值 1：成功 2：失败 0：参数有误
     */
    public Integer organiztionMoveAfter(String parentId,String moveOrgId){
        if(StringUtils.isNotBlank(parentId) && StringUtils.isNotBlank(moveOrgId)){
            SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(parentId);//参考机构id
            if(null != sysOrganization){
                Integer compositor = sysOrganization.getCompositor();//参考机构id的排序号
                String parentId1 = sysOrganization.getParentId();//参考机构的父id
                if(null != compositor && StringUtils.isNotBlank(parentId1)){
                    List<SysOrganization> sysOrganizations = this.selectOrganiztions(parentId1);
                    if(null != sysOrganizations && sysOrganizations.size() > 0){
                        for (SysOrganization sysOrg:sysOrganizations) {
                            if(null != sysOrg){
                                Integer compositor1 = sysOrg.getCompositor();
                                if(compositor1>compositor) {
                                    SysOrganization sysOoRg = new SysOrganization();
                                    sysOoRg.setId(sysOrg.getId());
                                    sysOoRg.setCompositor(compositor1+1);
                                    sysOoRg.setModifyTime(new Date());
                                    int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOoRg);
                                    if (i < 1) {
                                        return 2;
                                    }
                                }
                            }
                        }
                    }
                }
                SysOrganization sysOorg = new SysOrganization();
                sysOorg.setId(moveOrgId);
                sysOorg.setParentId(parentId1);
                if(null != compositor) {
                    sysOorg.setCompositor(compositor + 1);
                } else {
                    sysOorg.setCompositor(1);
                }
                sysOorg.setModifyTime(new Date());
                int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOorg);
                if(i<1){
                    return 2;
                }else {
                    return 1;
                }
            }
        }
        return 0;
    }
    /**
     * 根据组织机构主键id修改组织机构排序(从大到小的向上移动)
     * @param organizationId
     * @param compositor
     * @return
     */
    public Integer updateOrganizationCompositor(String organizationId,Integer compositor){
        SysOrganization sysOrganization = new SysOrganization();
        sysOrganization.setId(organizationId);
        sysOrganization.setModifyTime(new Date());
        sysOrganization.setCompositor(compositor+1);
        int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganization);
        if(i != 1){
            return 2;
        }else{
            return 1;
        }
    }

    /**
     * 根据组织机构主键id修改组织机构排序(从小到大的向上移动)
     * @param organizationId
     * @param compositor
     * @return
     */
    public Integer updateOrganizationCompositorDown(String organizationId,Integer compositor){
        SysOrganization sysOrganization = new SysOrganization();
        sysOrganization.setId(organizationId);
        sysOrganization.setModifyTime(new Date());
        sysOrganization.setCompositor(compositor-1);
        int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganization);
        if(i != 1){
            return 2;
        }else{
            return 1;
        }
    }
    /**
     * 修改某个活某些组织机构移动后的父id
     * @param parentId
     * @param moveOrgIds
     * @return
     */
    public boolean getStatus(String parentId,List<String> moveOrgIds){
        boolean status = false;
        for (String orgId : moveOrgIds) {
            SysOrganization sysOrganization = new SysOrganization();
            sysOrganization.setId(orgId);
            sysOrganization.setParentId(parentId);
            int i = sysOrganizationMapper.updateByPrimaryKeySelective(sysOrganization);
            if(i != 1){
                return false;
            }else {
                status = true;
            }
        }
        return status;
    }


    /**
     * 根据orgaId 查询SysOrganization及其子节点信息
     *
     * @param orgaId
     * @return
     */
    @Override
    public SysOrganization findOrgaAndChildOrgaByOrgaId(String orgaId) {

        SysOrganization orga=null;
        if (StringUtils.isEmpty(orgaId)) {
            orga=this.selectOrganiztionById("1");
        }else{
            orga=this.selectOrganiztionById(orgaId);
        }
        if(null!=orga){
            List<SysOrganization> lsRet = this.selectOrganiztions(orga.getId());
//            for (SysOrganization orga2 : lsRet) {
//                    Integer childCont=this.getChildrenCount(orga2.getId());
//                    if(null!=childCont && childCont>0){
//                        orga.setChildren();
//                    }
//            }
            orga.setChildren(lsRet);
        }
        return orga;
    }

    /**
     * 查询orgaId有多少子节点
     *
     * @param orgaId
     * @return
     */
    @Override
    public Integer getChildrenCount(String orgaId) {
        List<SysOrganization> list=this.selectOrganiztions(orgaId);
        return (null!=list && list.size()>0)?list.size():0;
    }

    /**
     * 获取所有组织机构数量功能
     * @return
     */
    public Integer getOrganizationNum(){
        int orgNum = 0;
//        Example example = new Example(SysOrganization.class);
//        example.createCriteria().andNotEqualTo("parentId",0);/*.andNotEqualTo("status",2)*//*该状态需要等待后期维护后再打开注释运行*/
       // return sysOrganizationMapper.selectCountByExample(example);
        List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectOrganizationNum();
        if(null != sysOrganizations && sysOrganizations.size() > 0){
            orgNum += sysOrganizations.size();
        }
        return orgNum;
    }
}
