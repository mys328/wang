package com.thinkwin.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.PermissionService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.log.SysLog;
import com.thinkwin.common.model.log.SysLogType;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.log.service.SysLogTypeService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Log日志基本操作
 */
@Controller
@RequestMapping(value = "/log")
public class LogController {

    private final Logger log = LoggerFactory.getLogger(LogController.class);
    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private SysLogTypeService sysLogTypeService;

    @Autowired
    UserService userService;
    @Resource
    PermissionService permissionService;

    @RequestMapping("/operationLog")
    public String operationLog(HttpServletRequest request) throws Exception {
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "op_logs/operationLog";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
      //  return "op_logs/operationLog";
    }

    /*
    * 查询日志分页
     * */
    @RequestMapping("/selectSysLogListByPage")
    @ResponseBody
    public Object selectSysLogListByPage(BasePageEntity basePageEntity, String businesstype, String eventtype, String content) {
        SysLog sysLog = new SysLog();
        sysLog.setEventtype(eventtype);
        sysLog.setContent(content);

        //放置businesstype类型  sql操作为in
        List<String> businesstypes = new ArrayList<String>();

        if (StringUtil.isEmpty(businesstype)) {
            //获得人员
            String userId = TenantContext.getUserInfo().getUserId();

            //获得角色
            List<String> stringList = userService.selectUserRole(userId);

            //日志类型
            List<SysLogType> sysLogTypeList = new ArrayList<>();
            //获得与角色相关的一级菜单
            for (String roleId : stringList) {
                sysLogTypeList = this.sysLogTypeService.selectSysLogTypeListByRoleId(roleId);
            }
            if (sysLogTypeList != null && stringList.size() > 0) {
                for (SysLogType sysLogType : sysLogTypeList) {
                    businesstypes.add(sysLogType.getType());
                }
            }
            sysLog.setBusinesstypes(businesstypes);
        } else {
            businesstypes.add(businesstype);
            sysLog.setBusinesstypes(businesstypes);
        }

        PageInfo<SysLog> sysLogTypeList = this.sysLogService.selectSysLogListByPage(basePageEntity, sysLog);
        if (sysLogTypeList.getList() != null && sysLogTypeList.getList().size() > 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysLogTypeList);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), sysLogTypeList, BusinessExceptionStatusEnum.DataNull.getCode());
    }


    /*
    * 查询单个日志
     * */
    @RequestMapping("/selectSysLogById")
    @ResponseBody
    public Object selectSysLogById(String id) {
        SysLog sysLog = this.sysLogService.selectSysLogById(id);
        if (sysLog != null) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysLog);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), sysLog, BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /*
      * 清空日志(假删除)
    * */
    @RequestMapping("/deleteSysLogList")
    @ResponseBody
    public Object deleteSysLogList(String businesstype, String eventtype) {
        SysLog sysLog = new SysLog();
        sysLog.setEventtype(eventtype);

        //放置businesstype类型  sql操作为in
        List<String> businesstypes = new ArrayList<String>();

        if (StringUtil.isEmpty(businesstype)) {
            //获得人员
            String userId = TenantContext.getUserInfo().getUserId();

            //获得角色
            List<String> stringList = userService.selectUserRole(userId);

            //日志类型
            List<SysLogType> sysLogTypeList = new ArrayList<>();
            //获得与角色相关的一级菜单
            for (String roleId : stringList) {
                sysLogTypeList = this.sysLogTypeService.selectSysLogTypeListByRoleId(roleId);
            }
            if (sysLogTypeList != null && stringList.size() > 0) {
                for (SysLogType sysLogType : sysLogTypeList) {
                    businesstypes.add(sysLogType.getType());
                }
            }
            sysLog.setBusinesstypes(businesstypes);
        } else {
            businesstypes.add(businesstype);
            sysLog.setBusinesstypes(businesstypes);
        }

        boolean success = this.sysLogService.updateSysLogList(sysLog);
        if (success) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /*
    * 查询日志类型
    * */
    @RequestMapping("/selectSysLogTypeList")
    @ResponseBody
    public Object selectSysLogTypeList() {
        //获得人员
        String userId = TenantContext.getUserInfo().getUserId();

        //获得角色
        List<String> stringList = userService.selectUserRole(userId);

        //日志类型
        List<SysLogType> sysLogTypeList = new ArrayList<>();

        HashSet hs=new HashSet<>();
        //获得与角色相关的一级菜单
        for (String roleId : stringList) {
            List<SysLogType> sysLogTypeList1 = this.sysLogTypeService.selectSysLogTypeListByRoleId(roleId);
            for(SysLogType sysLogType:sysLogTypeList1){
                if(!hs.contains(sysLogType.getId())){
                    sysLogTypeList.add(sysLogType);
                    hs.add(sysLogType.getId());
                }
            }
        }

        //根据一级菜单 获取二级菜单
        if (sysLogTypeList != null && stringList.size() > 0) {
            List<SysLogType> sysLogTypeList1 = new ArrayList<>();
            for (SysLogType sysLogType : sysLogTypeList) {
                SysLogType sysLogType1 = new SysLogType();
                sysLogType1.setParentId(sysLogType.getId());
                sysLogType1.setStatus(0);
                List<SysLogType> sysLogTypeList2 = this.sysLogTypeService.selectSysLogTypeList(sysLogType1);
                sysLogTypeList1.addAll(sysLogTypeList2);
            }
            sysLogTypeList.addAll(sysLogTypeList1);
        }

        //返回菜单树最终数据
        if (sysLogTypeList != null && sysLogTypeList.size() > 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysLogTypeList);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), sysLogTypeList, BusinessExceptionStatusEnum.DataNull.getCode());
    }

}
