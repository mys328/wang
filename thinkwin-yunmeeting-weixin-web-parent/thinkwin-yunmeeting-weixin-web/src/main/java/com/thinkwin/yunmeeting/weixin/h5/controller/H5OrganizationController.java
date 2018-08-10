package com.thinkwin.yunmeeting.weixin.h5.controller;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.vo.mobile.MobileOrgaVo;
import com.thinkwin.common.vo.mobile.MobileUserVo;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：
 * @author lining 2017/8/28
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/wechat/h5Orga")
public class H5OrganizationController {

    private final Logger log = LoggerFactory.getLogger(H5OrganizationController.class);

    //组织系统管理机构节点
    private static String MainOrgId ="1";

    @Resource
    private WxOAuth2Service wxOAuth2Service;
    @Resource
    private WxUserService wxUserService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private UserService userService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private FileUploadService fileUploadService;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private MeetingReserveService meetingReserveService;


    /**
     * 组织机构列表
     * @param request
     * @param response
     */
    @RequestMapping("/sysOrgaList")
    @ResponseBody
    public ResponseResult sysOrgaList(HttpServletRequest request, HttpServletResponse response,String orgaId,String tenantId) {
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> map=new HashMap<>();
        MobileOrgaVo mainOrgaVo=new MobileOrgaVo();
        List<MobileOrgaVo> lsRet = new ArrayList<MobileOrgaVo>();

        //租户为空
        if(StringUtils.isBlank(tenantId)){
            responseResult.setIfSuc(0);
            responseResult.setMsg("参数不正确");
            return responseResult;
        }

        try {
            //判断是否为根节点
            if(StringUtils.isBlank(orgaId)){
                orgaId=MainOrgId;
            }

            TenantContext.setTenantId(tenantId);
            SysOrganization orga=this.organizationService.findOrgaAndChildOrgaByOrgaId(orgaId);
            if(null==orga){
                responseResult.setIfSuc(0);
                responseResult.setMsg("参数不正确");
                return responseResult;
            }
            mainOrgaVo.setOrgaId(orga.getId());
            mainOrgaVo.setOrgaName(orga.getOrgName());
            List<SysOrganization> orgas=orga.getChildren();
            if(null!=orgas && orgas.size()>0){
            mainOrgaVo.setLeaf(true);
            mainOrgaVo.setChildCount(orgas.size());
                for(SysOrganization o:orgas){
                     List<SysOrganization> lists=this.organizationService.selectOrganiztions(o.getId());
                     MobileOrgaVo vo=new MobileOrgaVo();
                     vo.setOrgaId(o.getId());
                     vo.setOrgaName(o.getOrgName());
                     if(null!=lists && lists.size()>0){
                         vo.setLeaf(true);
                         vo.setChildCount(lists.size());
                     }
                    lsRet.add(vo) ;
                }
                mainOrgaVo.setChildren(lsRet);
            }
            map.put("mainOrga",mainOrgaVo);
            responseResult.setIfSuc(1);
            responseResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 人员列表
     * @param request
     * @param response
     */
    @RequestMapping("/sysUserList")
    @ResponseBody
    public ResponseResult meetingRoomList(HttpServletRequest request, HttpServletResponse response,String orgaId,String tenantId,String startTime,String endTime) {
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> map=new HashMap<>();
        MobileOrgaVo mainOrgaVo=new MobileOrgaVo();
        List<MobileUserVo> mainUserVos=new ArrayList<>();
        List<MobileOrgaVo> lsRet = new ArrayList<MobileOrgaVo>();

        //租户为空
        if(StringUtils.isBlank(tenantId)){
            responseResult.setIfSuc(0);
            responseResult.setMsg("参数不正确");
            return responseResult;
        }

        try {
            //判断是否为根节点
            if(StringUtils.isBlank(orgaId)){
                orgaId=MainOrgId;
            }
            TenantContext.setTenantId(tenantId);
            SysOrganization orga=this.organizationService.findOrgaAndChildOrgaByOrgaId(orgaId);
            if(null==orga){
                responseResult.setIfSuc(0);
                responseResult.setMsg("参数不正确");
                return responseResult;
            }
            mainOrgaVo.setOrgaId(orga.getId());
            mainOrgaVo.setOrgaName(orga.getOrgName());
            List<SysOrganization> orgas=orga.getChildren();
            if(null!=orgas && orgas.size()>0){
                List<SysUser> tempUsers=this.userService.selectAddressListStructure(orgaId);
                if(null!=tempUsers && tempUsers.size()>0){
                    mainOrgaVo.setChildCount(tempUsers.size());
                }

                for(SysOrganization o:orgas){
                    MobileOrgaVo vo=new MobileOrgaVo();
                    vo.setOrgaId(o.getId());
                    vo.setOrgaName(o.getOrgName());
                    List<SysUser> tempUsers2=this.userService.selectAddressListStructure(o.getId());
                    if(null!=tempUsers2 && tempUsers2.size()>0){
                        vo.setLeaf(true);
                        vo.setChildCount(tempUsers2.size());
                    }
                    lsRet.add(vo) ;
                }
                mainOrgaVo.setChildren(lsRet);
            }

            //添加用户列表******************************
            List<SysUser> userList=this.userService.findByOrgId(orgaId);
            for(SysUser u:userList){
                if(u.getStatus()!=89){
                    MobileUserVo userVo=new MobileUserVo();
                    userVo.setUserId(u.getId());
                    userVo.setUserName(u.getUserName());
                    userVo.setOrgaId(u.getOrgId());
                    userVo.setEmail(u.getEmail());
                    userVo.setPhoneNumber(u.getPhoneNumber());
                    String photo = u.getPhoto();
                    if (org.apache.commons.lang.StringUtils.isNotBlank(photo)) {
//                        Map<String,String> picMap=fileUploadService.selectFileCommon(photo);
                        Map<String, String> picMap = userService.getUploadInfo(photo);
                        if(null != picMap){
                            userVo.setHeadPicAddress(picMap.get("big"));
                        }

                    }

                    List<YunmeetingConference> ms=new ArrayList<>();
                    ms=this.meetingReserveService.findByUserByOccupy(startTime,endTime,u.getId());
                    userVo.setIsOccupy(0);
                    if(ms!=null && ms.size()>0){
                        userVo.setIsOccupy(1);
                    }
                    userVo.setSex(u.getSex());
                    userVo.setDepartment(u.getPosition());

                    mainUserVos.add(userVo);
                }

            }


            map.put("mainUsers",mainUserVos);
            map.put("mainOrga",mainOrgaVo);
            responseResult.setIfSuc(1);
            responseResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 获取人员信息
     * @param request
     * @param response
     * @param userId
     * @param tenantId
     * @return
     */
    @RequestMapping("/getSysUser")
    @ResponseBody
    public ResponseResult getSysUser(HttpServletRequest request, HttpServletResponse response,String userId,String tenantId,String openId) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser user = this.check(tenantId, openId, userId);
        if (null == user) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(user.getOpenId()) || StringUtils.isBlank(user.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }

        TenantContext.setTenantId("0");
        SaasTenant tenant =this.saasTenantService.selectSaasTenantServcie(tenantId);

       try{
        TenantContext.setTenantId(tenantId);
        SysUser sysUser=this.userService.selectUserByUserId(userId);

        if(null!=sysUser){

            String isAdminRoom="0";
            List<SysUserRole> userRoles=this.userService.getCurrentUserRoleIds(sysUser.getId());
            for(SysUserRole r: userRoles){
                if(r.getRoleId().equals("3")){
                    isAdminRoom="1";
                    break;
                }
            }
            String photo = sysUser.getPhoto();
            if (org.apache.commons.lang.StringUtils.isNotBlank(photo)) {
//                Map<String,String> picMap=fileUploadService.selectFileCommon(photo);
                Map<String, String> picMap = userService.getUploadInfo(photo);
//                sysUser.setPhoto((picMap.isEmpty()?null:picMap.get("big")));
                if(null != picMap){
                    sysUser.setPhoto(picMap.get("big"));
                    sysUser.setCentralgraph(picMap.get("in"));
                    sysUser.setThumbnails(picMap.get("small"));
                }
            }
            responseResult.setIfSuc(1);
            map.put("publish",tenant.getTenantType());
            map.put("openId",sysUser.getOpenId());
            map.put("tenantId",tenantId);
            map.put("sysUser",sysUser);
            map.put("isAdminRoom",isAdminRoom);
            responseResult.setData(map);
            responseResult.setMsg("认证成功");
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg("用户不是有效的企云会用户");
        }
        } catch (Exception e) {
        e.printStackTrace();
        }
        return responseResult;
        }


    //权限认证
    public SysUser check(String tenantId, String openId, String userId) {
        SysUser user = null;
//        if(StringUtils.isBlank(tenantId) && StringUtils.isBlank(openId) &&StringUtils.isBlank(userId)){
        if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(userId)) {
            TenantContext.setTenantId(tenantId);
            user = this.userService.authSysUser(null, openId, userId);
        }
        return user;
    }


}
