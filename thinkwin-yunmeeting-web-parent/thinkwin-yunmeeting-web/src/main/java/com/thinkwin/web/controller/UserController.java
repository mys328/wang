package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.*;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.SHA1Util;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.SaasTenantInfoVo;
import com.thinkwin.common.vo.SysUserVo;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingReserveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/6/16.
 * Company: thinkwin
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private FileUploadService fileUploadService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private MeetingReserveService meetingReserveService;
    @Resource
    private DepartmentService departmentService;

    /**
     * 根据组织机构id获取该组织下的人员列表
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/sysUsersByOrgId")
    public ResponseResult selectUserByOrgId(String orgId,BasePageEntity basePageEntity,String startTime,String endTime,String conferenceId){
        if(null == orgId && "".equals(orgId)) {
            orgId = "0";
        }
        //此处需要考虑组织下还有子组织的问题？？？？？
        Map map = userService.selectUserByOrgId(orgId,basePageEntity,startTime,endTime,conferenceId);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 组件搜索功能
     * @param searchParameter
     * @return
     */
    @RequestMapping(value = "/search")
    public ResponseResult searchLike(String searchParameter,BasePageEntity basePageEntity,String startTime,String endTime,String conferenceId){
        Map map = userService.searchLike(searchParameter,basePageEntity,startTime,endTime,conferenceId);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 添加子管理员功能
     * @param sonList
     * @return
     */
    @RequestMapping("/addSonManager")
    public ResponseResult addSonAdministrators(String[] sonList){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"参数错误！租户id不能为空！");
        }
        if(null != sonList && sonList.length > 0 && sonList.length > 3){
            sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"子管理员数量超出上限!","", Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "子管理员数量超出上限！", false);
        }
        String userId = TenantContext.getUserInfo().getUserId();
        if(StringUtils.isNotBlank(userId)) {
            //根据用户的主键id和角色主键id判断该用户是否拥有某个角色(如果第二个参数roleId为空时，默认为1，超级管理员)
            List userRoleList = userService.getUserRoleByUserIdAndRoleId(userId,null);
            if(null == userRoleList){
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"必填参数为空","", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(),false);
            }
            if(null != sonList && sonList.length > 0){
                List<String> list = Arrays.asList(sonList);
                boolean contains = list.contains(userId);
                if(contains){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"主管理员不可设置为子管理员");
                }
                Integer num = userService.addSonManager(userId,list);
                if(null != num && num == 0){
                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                    String tenantType = "0";//0免费 1收费
                    if(null != saasTenant){
                        String tenantType1 = saasTenant.getTenantType();
                        if(StringUtils.isNotBlank(tenantType1)){
                            tenantType = tenantType1;
                        }
                    }
                    for (String sonAdminId:sonList) {
                        List<SysMenu> sysMenus = new ArrayList<>();
                        sysMenus = menuService.selectMenus(sonAdminId, "0", tenantType);
                        String s = JSON.toJSONString(sysMenus);
                        //把字符串存redis里面
                        RedisUtil.set(tenantId +"_Menus_" +  sonAdminId, s);
                        RedisUtil.expire(tenantId + "_Menus_" + sonAdminId, 1200);
                        RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + sonAdminId);
                        //RedisUtil.remove(tenantId+"_yunmeeting_token_"+sonAdminId);
                        RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+sonAdminId);
                        RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+sonAdminId);
                    }
                    //查询被添加人员的用户名
                    String content = "";
                    for(String userId1:sonList){
                        SysUser sysUser = userService.selectUserByUserId(userId1);
                        if(null!=sysUser){
                            content += sysUser.getUserName() +"、";
                        }
                    }
                    sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),TenantContext.getUserInfo().getUserName()+"授权子管理员："+content.substring(0,content.length()-1),"", Loglevel.info.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),true);
                }else if(null != num && num ==1){
                    sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"添加子管理员失败","", Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), false);
                }else if(null != num && num == 2){
                    sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"主管理员不能成为子管理员！","", Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "主管理员不能成为子管理员！", false);
                }else{
                    sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"子管理员数量超出上限!","", Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "子管理员数量超出上限！", false);
                }
            }
        }
        sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"超级管理人员userId不能为空!","", Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"超级管理人员userId不能为空!");
    }

    /**
     * 删除子管理员
     * @return
     */
    @RequestMapping("/delSonManager")
    public ResponseResult delSonManager(String userId,String sonUserId){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"参数错误!");
        }
        if(StringUtils.isBlank(userId)) {
            userId = TenantContext.getUserInfo().getUserId();
        }
        if(StringUtils.isNotBlank(userId)){
            if(StringUtils.isNotBlank(sonUserId)) {
                //判断该用户是否为超级管理员
                List userRoleList = userService.getUserRoleByUserIdAndRoleId(userId, null);
                if (null == userRoleList) {
                    sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"删除子管理员的权限不足!","", Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "你不是超级管理员！", false);
                }
                //执行删除子管理员
                Integer num = userService.delSonManagerBySonUserId(sonUserId);
                if(null != num && num == 0){
                    // TenantContext.setTenantId("0");

                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                    String tenantType = "0";//0免费 1收费
                    if(null != saasTenant){
                        String tenantType1 = saasTenant.getTenantType();
                        if(StringUtils.isNotBlank(tenantType1)){
                            tenantType = tenantType1;
                        }
                    }
                    List<SysMenu> sysMenus = new ArrayList<>();
                    sysMenus = menuService.selectMenus(sonUserId, null, tenantType);
                    String s = JSON.toJSONString(sysMenus);
                    //把字符串存redis里面
                    RedisUtil.set(tenantId + "_Menus_" + sonUserId, s);
                    RedisUtil.expire(tenantId +"_Menus_" +  sonUserId, 1200);
                    RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + sonUserId);
                    //RedisUtil.remove(tenantId+"_yunmeeting_token_"+sonUserId);
                    RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+sonUserId);
                    RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+sonUserId);
                    sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"删除子管理员成功!","", Loglevel.info.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),true);
                }else if(null != num && num ==1){
                    sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"删除子管理员失败!","", Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), false);
                }
            }
            sysLogService.createLog(BusinessType.companyOp.toString(), EventType.child_admin_settings.toString(),"要删除的子管理员主键id不能为空!","", Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"要删除的子管理员id不能为空!");
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"超级管理人员userId不能为空!");
    }

    /**
     * 移动某些人员到某处
     * @param userId
     * @param parentId
     * @param moveUserIds
     * @return
     * @throws Exception
     */
    @RequestMapping("/usersMove")
    @ResponseBody
    public ResponseResult usersMove(String userId, String parentId, String[] moveUserIds)throws Exception{
        if(StringUtils.isBlank(userId)){
            userId = TenantContext.getUserInfo().getUserId();
        }
        boolean isManager = userService.getRequestIdentity(userId);
        if(!isManager){
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(),"移动人员时权限不足!","", Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "你不是管理员！",false);
        }
        if(null != moveUserIds && moveUserIds.length > 0) {
            List<String> list = Arrays.asList(moveUserIds);
            //此处判断组织机构是否存在
            SysOrganization sysOrganization = organizationService.selectOrganiztionById(parentId);
            if(null != sysOrganization){
                Integer num = userService.usersMove(parentId, list);
                if (null != num && num == 1) {
                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), "移动人员成功!", "", Loglevel.info.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), true);
                } else if (null != num && num == 2) {
                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), "移动人员失败!", "", Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "人员移动失败！", false);
                } else {
                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), "移动人员时参数错误!", "", Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "人员父id不合法！", false);
                }
            }else {
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), "移动人员时组织机构参数错误!", "", Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "组织机构不存在！", false);
            }
        }else {
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(),"移动人员失败!","", Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
        }
    }

    @Resource
    private MenuService menuService;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private com.thinkwin.core.service.SaasTenantService saasTenantCoreService;
    /**
     * 添加会议室管理员功能
     * @return
     */
    @RequestMapping("/addboardroommanager")
    public ResponseResult addBoardroomManager(String userId,String[] managerIds){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_admin_settings.toString(),"添加会议室管理员时参数不足!","", Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
        }
        if(StringUtils.isBlank(userId)){
            userId = TenantContext.getUserInfo().getUserId();
        }
        //????添加会议室管理员时需不需要判断添加人的角色？如果需要判断时应该拥有什么角色
        if(null != managerIds && managerIds.length > 0){
            List<String> list = Arrays.asList(managerIds);
            Integer num = userService.addBoardroomManager(userId,list);
            if(null != num && num == 1 || "1".equals(num)){
                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                String tenantType = "0";//0免费 1收费
                if(null != saasTenant){
                    String tenantType1 = saasTenant.getTenantType();
                    if(StringUtils.isNotBlank(tenantType1)){
                        tenantType = tenantType1;
                    }
                }
                String content = "";
                for (String managerIIds:managerIds) {
                    List<SysMenu> sysMenus = new ArrayList<>();
                    sysMenus = menuService.selectMenus(managerIIds, null, tenantType);
                    String s = JSON.toJSONString(sysMenus);
                    //把字符串存redis里面
                    RedisUtil.set(tenantId +"_Menus_" +  managerIIds, s);
                    RedisUtil.expire(tenantId +"_Menus_" +  managerIIds, 1200);
                    RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + managerIIds);
                    if(!managerIIds.equals(userId)){
//                        RedisUtil.remove(tenantId+"_yunmeeting_token_"+managerIIds);
                        RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+managerIIds);
                        RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+managerIIds);
                    }
                    SysUser sysUser = userService.selectUserByUserId(managerIIds);
                    //查询设置成管理员的名称
                    if(null!=sysUser){
                        content += sysUser.getUserName()+"、";
                    }
                }
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_admin_settings.toString(),TenantContext.getUserInfo().getUserName()+"授权会议室管理员："+content.substring(0,content.length()-1),"", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else if(null != num && num == 2){
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_admin_settings.toString(),"添加会议室管理员失败!","", Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "添加会议室管理员失败！",false);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
    }

    /**
     * 删除会议室管理员
     * @return
     */
    @RequestMapping("/delboardroommanager")
    public ResponseResult delBoardroomManager(String userId ,String[] moveIds){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){

        }
        if(StringUtils.isBlank(userId)){
            userId = TenantContext.getUserInfo().getUserId();
        }
        //????添加会议室管理员时需不需要判断添加人的角色？如果需要判断时应该拥有什么角色
        if(null != moveIds && moveIds.length > 0){
            List<String> list = Arrays.asList(moveIds);
            Integer num = userService.delBoardroomManager(userId,list);
            if(null != num && num == 1){
                for (String boardroommanager:list) {
                    if(StringUtils.isNotBlank(boardroommanager)) {
                        RedisUtil.remove(tenantId + "_Menus_" + boardroommanager);
                        RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + boardroommanager);
                        if(!boardroommanager.equals(userId)){
//                            RedisUtil.remove(tenantId+"_yunmeeting_token_"+boardroommanager);
                            RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+boardroommanager);
                            RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+boardroommanager);
                        }
                    }
                }
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_admin_settings.toString(),"删除会议室管理员成功!","", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else if(null != num && num == 2){
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_admin_settings.toString(),"删除会议室管理员失败!","", Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "删除会议室管理员失败！",false);
            }
        }
        sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_admin_settings.toString(),"删除会议室管理员时参数不足!","", Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 查询会议室管理员功能
     * @return
     */
    @RequestMapping("/selectboardroommanager")
    public ResponseResult selectBoardroomManager(String userId){
        if(StringUtils.isBlank(userId)){
            userId = TenantContext.getUserInfo().getUserId();
        }
        if(StringUtils.isNotBlank(userId)) {
            //????添加会议室管理员时需不需要判断添加人的角色？如果需要判断时应该拥有什么角色
            List<SysUser> managers = userService.selectBoardroomManager(userId);
            if (null != managers && managers.size() > 0) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), managers);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 添加会议室预订专员功能
     * @param commissionerIds
     * @return
     */
    @RequestMapping("/addcommissioner")
    public ResponseResult addBoardroomCommissioner(String userId,String[] commissionerIds){
        if(StringUtils.isBlank(userId)){
            userId = TenantContext.getUserInfo().getUserId();
        }
        String tenantId = TenantContext.getTenantId();
        //????添加会议室管理员时需不需要判断添加人的角色？如果需要判断时应该拥有什么角色
        if(null != commissionerIds && commissionerIds.length > 0){
            List<String> list = Arrays.asList(commissionerIds);
            Integer num = userService.addBoardroomCommissioner(userId,list);
            if(null != num && num == 1){
                for (String commissionerid:list) {
                    RedisUtil.remove(tenantId + "_Menus_" + commissionerid);
                    RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + commissionerid);
                    if(!commissionerid.equals(userId)){
//                        RedisUtil.remove(tenantId+"_yunmeeting_token_"+commissionerid);
                        RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+commissionerid);
                        RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+commissionerid);
                    }
                }
                //查询名称
                String content = "";
                for(String userIds:commissionerIds){
                    SysUser sysUser = userService.selectUserByUserId(userIds);
                    if(null!=sysUser){
                        content += sysUser.getUserName()+"、";
                    }
                }
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_specialist_settings.toString(),TenantContext.getUserInfo().getUserName()+"授权会议室预定专员："+content.substring(0,content.length()-1),"", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else if(null != num && num == 2){
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_specialist_settings.toString(),"会议室预订专员添加失败!","", Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "添加会议室预订专员失败！",false);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
    }

    /**
     * 删除会议室预订专员功能接口
     * @param userId
     * @param commissionerIds
     * @return
     */
    @RequestMapping("/delcommissioner")
    public ResponseResult delBoardroomCommissioner(String userId,String[] commissionerIds){
        if(StringUtils.isBlank(userId)){
            userId = TenantContext.getUserInfo().getUserId();
        }
        String tenantId = TenantContext.getTenantId();
        //????添加会议室管理员时需不需要判断添加人的角色？如果需要判断时应该拥有什么角色
        if(null != commissionerIds && commissionerIds.length > 0){
            List<String> list = Arrays.asList(commissionerIds);
            Integer num = userService.delBoardroomCommissioner(userId,list);
            if(null != num && num == 1){
                for (String commissionerid:list) {
                    RedisUtil.remove(tenantId + "_Menus_" + commissionerid);
                    RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + commissionerid);
                    if(!commissionerid.equals(userId)){
//                        RedisUtil.remove(tenantId+"_yunmeeting_token_"+commissionerid);
                        RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+commissionerid);
                        RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+commissionerid);
                    }
                }
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_specialist_settings.toString(),"会议室预订专员删除成功!","", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else if(null != num && num == 2){
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_specialist_settings.toString(),"会议室预订专员删除失败!","", Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "删除会议室预订专员失败！",false);
            }
        }
        sysLogService.createLog(BusinessType.companyOp.toString(), EventType.meeting_room_specialist_settings.toString(),"会议室预订专员删除时参数不足!","", Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 会议室预订专员查询
     * @param userId
     * @return
     */
    @RequestMapping("/seachcommissioner")
    public ResponseResult selectBoardromCommissioner(String userId){
        //????添加会议室管理员时需不需要判断添加人的角色？如果需要判断时应该拥有什么角色
        List<SysUser> commissioners= userService.selectBoardroomCommissioner(userId);
        if(null != commissioners && commissioners.size() > 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),commissioners);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 管理设置中的人员集合
     * @return
     */
    @RequestMapping("/management_setting_persons")
    public ResponseResult managementSettings(String userId) {
        if(StringUtils.isBlank(userId)) {
            userId = TenantContext.getUserInfo().getUserId();
        }
        if (StringUtils.isNotBlank(userId)) {
            String tenantId = TenantContext.getTenantId();
            if(StringUtils.isBlank(tenantId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            if(null == saasTenant){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
            List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "1");
            if(null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0){
                Map map = userService.selectManagementSettingPersons();
                if(StringUtils.isBlank(saasTenant.getTerminalManagerPasswd())){
                    map.put("isDisplay",false);
                    map.put("terminalAdministrators",new ArrayList<>());
                }else{
                    map.put("isDisplay",true);
                }
                if (null != map) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), map, BusinessExceptionStatusEnum.DataNull.getCode());
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());

    }


    /**
     * 根据用户的主键id获取用户所在公司的信息
     * @return
     */
    @RequestMapping("/getUserTenantInfo")
    public ResponseResult getUserTenantInfo(){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = null;
        if(null != userInfo){
            userId = userInfo.getUserId();
        }
        String tenantId = userInfo.getTenantId();
        if(StringUtils.isNotBlank(userId)){
            SaasTenantInfoVo saasTenantInfoVo = new SaasTenantInfoVo();
                if (StringUtils.isNotBlank(tenantId)) {
                    String s1 = RedisUtil.get(tenantId+"_SaasTenantInfo");
                    if(StringUtils.isNotBlank(s1)){
                        saasTenantInfoVo = JSON.parseObject(s1, SaasTenantInfoVo.class);
                    }else {
                        SaasTenantInfo saasTenantInfo =saasTenantCoreService.selectSaasTenantInfoByTenantId(tenantId);
                        BeanUtils.copyProperties(saasTenantInfo,saasTenantInfoVo);
                        String companyLogo = saasTenantInfo.getCompanyLogo();
                        if (StringUtils.isNotBlank(companyLogo)) {
                            Map<String, String> logos = userService.getUploadInfo(companyLogo);
                            saasTenantInfoVo.setCompanyLogo(logos.get("primary"));
                            saasTenantInfoVo.setBigPicture(logos.get("big"));
                            saasTenantInfoVo.setInPicture(logos.get("in"));
                            saasTenantInfoVo.setSmallPicture(logos.get("small"));
                        }
                        String s = JSON.toJSONString(saasTenantInfoVo);
                        if(StringUtils.isNotBlank(s)) {
                            //把字符串存redis里面
                            RedisUtil.set(tenantId + "_SaasTenantInfo", s);
                        }
                    }
                }
            if (null != saasTenantInfoVo) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasTenantInfoVo);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), saasTenantInfoVo, BusinessExceptionStatusEnum.DataNull.getCode());

        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());

    }

    /**
     * 根据用户的主键id获取用户的信息
     * @return
     */
    @RequestMapping("/getSysUserInfoByUserId")
    public ResponseResult getSysUserByUserId() {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        if (null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)){
                SysUser sysUser = null;
                String s1 = RedisUtil.get(tenantId+"_yunmeeting_SysUserInfo_" + userId);
                SysUserVo sysUserVo = new SysUserVo();
                if(StringUtils.isNotBlank(s1)){
                    sysUserVo = JSON.parseObject(s1, SysUserVo.class);
                }else {
                    sysUser = userService.selectUserByUserId(userId);
                    if (null != sysUser) {
                        BeanUtils.copyProperties(sysUser,sysUserVo);
                        sysUserVo.setOpenId(sysUser.getOpenId());
                        String photo = sysUser.getPhoto();
                        if (StringUtils.isNotBlank(photo)) {
                            Map<String, String> photos = userService.getUploadInfo(photo);
                            if(null != photos) {
                                sysUserVo.setPhoto(photos.get("primary"));
                                sysUserVo.setBigPicture(photos.get("big"));
                                sysUserVo.setInPicture(photos.get("in"));
                                sysUserVo.setSmallPicture(photos.get("small"));
                            }
                        }
                        boolean isRole = getUserRoleIdsByUserId();
                        sysUserVo.setRole(isRole);
                        String s = JSON.toJSONString(sysUserVo);
                        //把字符串存redis里面
                        RedisUtil.set(tenantId+"_yunmeeting_SysUserInfo_" + userId, s);
                        RedisUtil.expire(tenantId+"_yunmeeting_SysUserInfo_" + userId, 1200);
                    }else{
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), sysUserVo, BusinessExceptionStatusEnum.DataNull.getCode());
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysUserVo);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),BusinessExceptionStatusEnum.Failure.getCode());
    }

    /**
     * 根据用户的状态查询用户功能接口
     * @return
     */
    @RequestMapping("/getUsersByStatus")
    public ResponseResult getUsersByUserStatus(Integer userStatus, BasePageEntity pageEntity,String orgId,String seachCondition){
        String userId = TenantContext.getUserInfo().getUserId();
        boolean handlePeoplePermission = userService.isHandlePeoplePermission(userId);
        if(handlePeoplePermission) {
            Map map = new HashMap();
            if (null != userStatus) {
                PageInfo pageInfo = userService.getUsersByUserStatus(userStatus,pageEntity,orgId,seachCondition);
                map.put("sysUser",pageInfo);
                //此处根据用户状态查用户数量
                Map map1 = userService.selectUserNumInfo3(orgId,seachCondition,userStatus);
                if(null != map1){
                    map.put("userNumInfo",map1);
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
    }

    /**
     * 可按员工姓名、全拼、手机号、邮箱等字段进行人员搜索
     * @return
     */
    @RequestMapping("/seachUserByCondition")
    public ResponseResult likeSeachUserByCondition(String parentId,String seachCondition,BasePageEntity pageEntity){
        Map map = new HashMap();

        PageInfo pageInfo = userService.likeSeachUserByCondition(parentId,seachCondition,pageEntity);
        map.put("sysUser",pageInfo);
        //此处根据用户状态查用户数量
        Map map1 = userService.selectUserNumInfo2(parentId,seachCondition);
        if(null != map1){
            map.put("userNumInfo",map1);
        }
        if(null != pageInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.DataNull.getDescription(),map,BusinessExceptionStatusEnum.DataNull.getCode());
    }

    @Resource
    private LoginRegisterService loginRegisterService;
    /**
     * 管理员登录控制台功能接口
     * @return
     */
    @RequestMapping("/administratorLoginConsole")
    public ResponseResult loginConsole(String password){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String userName = userInfo.getUserName();
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(password) || StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setUserId(userId);
        SaasUserWeb saasUserWeb1 = saasTenantCoreService.selectUserLoginInfo(saasUserWeb);
        if(null != saasUserWeb1){
            String password1 = saasUserWeb1.getPassword();
            if(StringUtils.isBlank(password1)){
                sysLogService.createLog(BusinessType.loginOp.toString(),EventType.console_login.toString(),userName + "登录控制台失败",null,Loglevel.error.toString());
                String data1 = "{\"code\":\"0\",\"url\":\"\"}";
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"密码不正确！",data1);
            }
            String password2 = SHA1Util.SHA1(password);
            if(password1.equals(password2)){
                sysLogService.createLog(BusinessType.loginOp.toString(),EventType.console_login.toString(),userName + "登录控制台成功",null,Loglevel.info.toString());
                String data =  "{\"code\":\"1\",\"msg\":\"登录成功！\",\"url\":\"/gotoConsolePage\"}";
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),data,BusinessExceptionStatusEnum.Success.getCode());
            }else{
                sysLogService.createLog(BusinessType.loginOp.toString(),EventType.console_login.toString(),userName + "登录控制台失败",null,Loglevel.error.toString());
                String data1 = "{\"code\":\"0\",\"url\":\"\"}";
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"密码不正确！",data1);
            }
        }
        sysLogService.createLog(BusinessType.loginOp.toString(),EventType.console_login.toString(),userName + "登录控制台失败！",null,Loglevel.error.toString());
        String data1 = "{\"code\":\"0\",\"url\":\"\"}";
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"用户id有误！",data1);
    }

    /**
     * 获取公司下的所有的用户
     * @return
     */
    @RequestMapping("/getSystemUsers")
    public ResponseResult getSystemUsers(BasePageEntity basePageEntity){
        Map map = userService.selectSystemUsers(basePageEntity);
        if(null != map){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.DataNull.getDescription(),map,BusinessExceptionStatusEnum.DataNull.getCode());
        }

    }

    /**
     * 获取通讯录结构数据
     * @return
     */
    @RequestMapping("/getAddressListStructure")
    public ResponseResult getAddressListStructure(String orgId,BasePageEntity basePageEntity){
        if(StringUtils.isBlank(orgId)) {
            orgId = "0";
        }
        Map map = userService.selectAddressListStructure(orgId,basePageEntity);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }


    /**
     * 获取用户的会议动态是否阅读标识
     * @return
     */
    @RequestMapping("/getUnreadMessage")
    public ResponseResult getUnreadMessage(){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        //1、查询七天前所有我参与的会议的信息
        Integer messageNum = meetingReserveService.selectUnreadMessage(userId);
        if(null != messageNum && messageNum != 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),messageNum);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),0);
    }


    /**
     * 获取当前用户最高角色id
     * @return
     */
    @RequestMapping("/getCurrentUserRoleId")
    public String getCurrentUserRoleId(){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo){
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)){
                List<SysUserRole> SysUserRoles = userService.getCurrentUserRoleIds(userId);
                if(null != SysUserRoles && SysUserRoles.size() > 0){
                    List list = new ArrayList();
                    for (SysUserRole sysUserRole:SysUserRoles) {
                        if(null != sysUserRole){
                            list.add(sysUserRole.getRoleId());
                        }
                    }
                    return Collections.min(list).toString();
                }
            }
        }
        return null;
    }

    /**
     * 获取部门中个状态下的用户数量
     * @param orgId
     * @return
     */
    @RequestMapping("getUserNumInfo")
    public ResponseResult selectUserNumInfo(String orgId){
        if(StringUtils.isBlank(orgId)){
            orgId = "0";
        }
        Map map1 = userService.getUserNumInfo1(orgId);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map1,BusinessExceptionStatusEnum.Success.getCode());
    }

    public boolean getUserRoleIdsByUserId(){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                List<String> list = userService.selectUserRole(userId);
                if(null != list && list.size() > 0){
                    for (String roleId:list) {
                        if("1".equals(roleId)||"2".equals(roleId)||"3".equals(roleId)||"5".equals(roleId)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    @RequestMapping("/findDepartment")
    public ResponseResult findDepartment(String orgId, String searchKey,String type,String currentPage,String pageSize,String modular){

          Map<String,Object> map = new HashMap<String,Object>();
        if(null == orgId || "".equals(orgId) || "0".equals(orgId)) {
            orgId = "1";
        }
        map = this.departmentService.getOrganizationInfo(orgId,searchKey,type,currentPage,pageSize,modular);

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
    }

    /**
     * 添加会议显示终端管理员功能接口
     * @return
     */
    @RequestMapping("/addTerminalAdministrator")
    public Object addTerminalAdministrator(String[] terminalAdminIds){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"权限不足");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"权限不足");
        }
        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "1");
        if(null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0){
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"权限不足");
        }
        if(null == terminalAdminIds || terminalAdminIds.length <= 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数有误");
        }
        if(terminalAdminIds.length > 3){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议显示终端管理员数量超出上限！");//6月26日 需求确定该提示语
        }
//        List<SysUserRole> terminalAdmins = userService.getTerminalAdministratorsByRoleId("5");//会议显示管理员的角色主键id固定为5
//        if(null == terminalAdmins){
//            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数有误");
//        }
//        if(terminalAdmins.size()+terminalAdminIds.length > 3){
//            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议显示终端管理员数量超出上限！");//6月26日 需求确定该提示语
//        }
        List<String> terminalAdminIdss = new ArrayList();
        if(null != terminalAdminIds && terminalAdminIds.length > 0){
            terminalAdminIdss = Arrays.asList(terminalAdminIds);
        }
        if(null != terminalAdminIdss && terminalAdminIdss.size() > 0) {
            boolean contains = terminalAdminIdss.contains(userId);
            if(contains){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"主管理员不可设置为会议显示终端管理员");
            }
            String tenantId = TenantContext.getTenantId();
            String addTerminalAdminSuc =  userService.addTerminalAdministrator(userId,terminalAdminIdss);
           if("1".equals(addTerminalAdminSuc)){
               for (String terminalAdminIdd:terminalAdminIdss) {
                   RedisUtil.remove(tenantId + "_Menus_" + terminalAdminIdd);
                   RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + terminalAdminIdd);
//                   RedisUtil.remove(tenantId+"_yunmeeting_token_"+terminalAdminIdd);
                   RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+terminalAdminIdd);
                   RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+terminalAdminIdd);
               }
               return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),null,BusinessExceptionStatusEnum.Success.getCode());
           }
        }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Failure.getDescription(),null,BusinessExceptionStatusEnum.Failure.getCode());

    }

    /**
     * 删除会议显示终端管理员功能接口
     * @return
     */
    @RequestMapping("/delTerminalAdministrator")
    public Object delTerminalAdministrator(String[] terminalAdminIds){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"权限不足");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"权限不足");
        }
        if(null == terminalAdminIds || terminalAdminIds.length <= 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数有误");
        }
        List<String> terminalAdminIdss = new ArrayList();
        if(null != terminalAdminIds && terminalAdminIds.length > 0){
            terminalAdminIdss = Arrays.asList(terminalAdminIds);
        }
        if(null != terminalAdminIdss && terminalAdminIdss.size() > 0) {
            String tenantId = TenantContext.getTenantId();
            String delTerminalAdminSuc =  userService.delTerminalAdministrator(terminalAdminIdss);
            if("1".equals(delTerminalAdminSuc)){
                for (String terminalAdminIdd:terminalAdminIdss) {
                    RedisUtil.remove(tenantId + "_Menus_" + terminalAdminIdd);
                    RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + terminalAdminIdd);
//                    RedisUtil.remove(tenantId+"_yunmeeting_token_"+terminalAdminIdd);
                    RedisUtil.remove(tenantId+"_yunmeeting_WEB_token_"+terminalAdminIdd);
                    RedisUtil.remove(tenantId+"_yunmeeting_APP_token_"+terminalAdminIdd);
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),null,BusinessExceptionStatusEnum.Success.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Failure.getDescription(),null,BusinessExceptionStatusEnum.Failure.getCode());

    }










}
