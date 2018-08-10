package com.thinkwin.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.SysOrganizationVo;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 组织机构对外接口controller层
 * User: yinchunlei
 * Date: 2017/6/8.
 * Company: thinkwin
 */
@Controller
//@RequestMapping("/setting")
public class OrganizationController{
    @Resource
    private OrganizationService organizationService;
    @Resource
    private SysLogService sysLogService;

    /**
     * 添加新的组织机构功能
     * @param sysOrganization
     */
    @RequestMapping(value = "/saveOrganization",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult addOrganization(SysOrganization sysOrganization){
        if(null != sysOrganization) {
            String parentId = sysOrganization.getParentId();
            if(StringUtils.isBlank(parentId)){
                parentId = "1";
            }
            String orgName = sysOrganization.getOrgName();
            if(StringUtils.isNotBlank(orgName)) {
                List<SysOrganization> sysOrganizations = organizationService.selectOrganiztionByNameAndParentId(orgName, parentId);
                if(null != sysOrganizations && sysOrganizations.size() > 0){
                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_depart.toString(),"部门创建失败","部门名称已存在",Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"部门名称已存在！",false);
                }
            }else{
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_depart.toString(),"部门创建失败","部门名称不能为空",Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"部门名称不能为空！",false);
            }
            //boolean status = organizationService.saveOrganization(sysOrganization);
            String organizationId = organizationService.saveOrganizationReturnString(sysOrganization);
            //if(status) {
            if(StringUtils.isNotBlank(organizationId)){
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_depart.toString(),orgName+"创建成功","", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),organizationId);
            }
        }
        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_depart.toString(),"部门创建失败",BusinessExceptionStatusEnum.Failure.getDescription(),Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),false);
    }
    /**
    *   根据组织机构id删除相关信息
    */

    @RequestMapping("/deleteOrganizationById")
    @ResponseBody
    public ResponseResult deleteOrganizationById(String organizationId){
        if(null != organizationId && !"".equals(organizationId)){
            SysOrganization sysOrganization = organizationService.selectOrganiztionById(organizationId);
            int status = organizationService.deleteOrganizationById(organizationId);
            if(status == 1) {
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.del_depart.toString(),sysOrganization.getOrgName()+"已删除","",Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else if(status == 2){
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.del_depart.toString(),sysOrganization.getOrgName()+"删除失败",BusinessExceptionStatusEnum.HaveSon.getDescription(),Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.HaveSon.getDescription(),false,BusinessExceptionStatusEnum.HaveSon.getCode());
            }
        }
        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.del_depart.toString(),"部门删除失败",BusinessExceptionStatusEnum.Failure.getDescription(),Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),false);
    }

    /**
     * 组织机构修改功能
     * @param sysOrganization
     * @return
     */
    @RequestMapping( "/updateOrganiztion")
    @ResponseBody
    public ResponseResult updateOrganiztion(SysOrganization sysOrganization){
        if(null != sysOrganization) {
            String orgId = sysOrganization.getId();
            if(StringUtils.isNotBlank(orgId)) {
                SysOrganization sysOrganization1 = organizationService.selectOrganiztionById(orgId);
                if(null != sysOrganization1) {
                    String orgName = sysOrganization.getOrgName();
                    String orgName1 = sysOrganization1.getOrgName();//数据库中已存在的名称
                    if(StringUtils.isNotBlank(orgName1)) {
                        if (!orgName.equals(orgName1)) {
                            String parentId = sysOrganization1.getParentId();
                            if(StringUtils.isBlank(parentId)){
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"参数错误！该组织机构父id不能为空！");
                            }
                            List<SysOrganization> sysOrganizations = organizationService.selectOrganiztionByNameAndParentId(orgName, parentId);
                            if(null != sysOrganizations && sysOrganizations.size() > 0){
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"该名称已存在！");
                            }
                        }
                    }
                    boolean b = organizationService.updateOrganization(sysOrganization);
                    if (b) {
                        if (!orgName.equals(orgName1)) {
                            SysUser sysUser = new SysUser();
                            sysUser.setOrgId(orgId);
                            List<SysUser> sysUsers = userService.selectUser(sysUser);
                            if(null != sysUsers && sysUsers.size() > 0){
                                for (SysUser sysU:sysUsers) {
                                    if(null != sysU) {
                                        SysUser ssysU = new SysUser();
                                        ssysU.setId(sysU.getId());
                                        ssysU.setOrgName(orgName);
                                        userService.updateUserByUserId(ssysU);
                                    }
                                }
                            }
                        }
                        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_depart.toString(), "部门名称修改为"+orgName, "", Loglevel.info.toString());
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), true);
                    }
                }
            }
        }
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_depart.toString(), "部门名称修改失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), false);

    }

    /**
     * 根据组织机构ID查询组织机构信息
     * @param organiztionId
     * @return
     */
    @RequestMapping("/selectOrganiztionById")
    @ResponseBody
    public ResponseResult selectOrganiztionById(String organiztionId){
        if(null != organiztionId && !"".equals(organiztionId)) {
            SysOrganization sysOrganization = organizationService.selectOrganiztionById(organiztionId);
            if (null != sysOrganization) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysOrganization);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(),sysOrganization,BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
    }

    /**
     * 根据组织机构名称查询组织机构信息
     * @param organiztionName
     * @return
     */
    @RequestMapping("/selectOrganiztionByName")
    @ResponseBody
    public ResponseResult selectOrganiztionByName(String organiztionName){
        if(null != organiztionName && !"".equals(organiztionName)) {
            List<SysOrganization> sysOrganizations = organizationService.selectOrganiztionByName(organiztionName);
            if (null != sysOrganizations) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysOrganizations);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(),sysOrganizations,BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
    }

    /**
     * 查询所有组织机构信息(该路径取出的值有排序功能)
     * @return
     */
    @RequestMapping("/selectOrganiztions")
    @ResponseBody
    public ResponseResult selectOrganiztions(String parentId){
        if(StringUtils.isBlank(parentId)){
            parentId = "0";
        }
        //该方法是根据父id获取第一级组织机构功能并排序
        List<SysOrganization> sysOrganizations = organizationService.selectOrganiztions(parentId);
       // List<SysOrganization> sysOrganizations = organizationService.selectOrganiztions();
        if(null != sysOrganizations && sysOrganizations.size() > 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),sysOrganizations);
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.DataNull.getDescription(), sysOrganizations,BusinessExceptionStatusEnum.DataNull.getCode());
        }
    }

    /**
     * 带分页功能查询组织机构信息
     * @param pageEntity
     * @return
     */
    @RequestMapping("/selectOrganiztionsByPage")
    @ResponseBody
    public ResponseResult selectOrganiztionsByPage(BasePageEntity pageEntity){
       PageInfo pageInfo = organizationService.selectOrganiztionsByPage(pageEntity);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), pageInfo);
        }

    @Resource
    private UserService userService;
    /**
     * 移动某些组织机构到某处
     * @param parentId
     * @param moveOrgId
     * @Param moveType//移动的类型 1：移动至parentId的上方 2：移动到parentId的子级 3：移动到parentId的下方
     * @return
     */
    @RequestMapping("/organiztionMove")
    @ResponseBody
    public ResponseResult organiztionMove(String parentId, String moveOrgId,String moveType)throws Exception{
        String userId = TenantContext.getUserInfo().getUserId();
        boolean isManager = userService.getRequestIdentity(userId);
        if(!isManager){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(),BusinessExceptionStatusEnum.PermissionDenied.getCode());
        }
        if(StringUtils.isNoneBlank(moveOrgId)){
            if(StringUtils.isBlank(parentId)){
                parentId = "1";
            }
            SysOrganization sysOrganization = organizationService.selectOrganiztionById(moveOrgId);
            if(null == sysOrganization){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"被移动部门不能为空！");
            }
            String orgName = sysOrganization.getOrgName();
            String parentId2 = sysOrganization.getParentId();
            if(StringUtils.isBlank(orgName)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"部门名称不能为空！");
            }
            if("2".equals(moveType)) {
                List<SysOrganization> sysOrganizations = organizationService.selectOrganiztionByNameAndParentId(orgName, parentId);
                if (null != sysOrganizations && sysOrganizations.size() > 0) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "该组织下已经存在相同名称部门！");
                }
            }else{
                SysOrganization sysOrganization111 = organizationService.selectOrganiztionById(parentId);
                if(null == sysOrganization111){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
                }
                String parentId1 = sysOrganization111.getParentId();
                if(StringUtils.isBlank(parentId1)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
                }
                if(!parentId1.equals(parentId2)) {
                    List<SysOrganization> sysOrganizations = organizationService.selectOrganiztionByNameAndParentId(orgName, parentId1);
                    if (null != sysOrganizations && sysOrganizations.size() > 0) {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "该组织下已经存在相同名称部门！");
                    }
                }
            }
            Integer num  = organizationService.organiztionMove(parentId,moveOrgId,moveType);
            if(null != num && num == 1){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else if(null != num && num == 2){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(),BusinessExceptionStatusEnum.Failure.getCode());
            }else if(null !=  num && num == 3){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.ClassError.getDescription(),BusinessExceptionStatusEnum.ClassError.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
    }
//    public static void main(String[] args){
//        List list = new ArrayList();
//        list.add(0);
//        Comparable max = Collections.max(list);
//        int i = (int)max + 1;
//        System.out.println("i ："+i);
//    }


}
