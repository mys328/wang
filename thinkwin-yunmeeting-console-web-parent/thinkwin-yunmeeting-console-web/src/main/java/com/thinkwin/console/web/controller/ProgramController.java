package com.thinkwin.console.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.model.publish.PlatformProgram;
import com.thinkwin.common.model.publish.PlatformProgramTenantMiddle;
import com.thinkwin.common.model.publish.PlatformProgramVersion;
import com.thinkwin.common.model.publish.PlatformProgrameLabel;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.JsonData;
import com.thinkwin.common.vo.SaasTenantInfoVo;
import com.thinkwin.common.vo.consoleVo.PlatformProgramVersionVo;
import com.thinkwin.common.vo.consoleVo.PlatformProgrameLabelVo;
import com.thinkwin.common.vo.consoleVo.PlatformProgrameVo;
import com.thinkwin.common.vo.programe.CustomizingTenantVo;
import com.thinkwin.console.service.SaasRoleService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.publish.service.*;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.thinkwin.common.utils.FileOperateUtil.FILEDIR;
import static com.thinkwin.common.utils.FileOperateUtil.mkDir;
import static com.thinkwin.common.utils.ZipUtil.unZip;

/**
 * 节目管理controller层
 * User: yinchunlei
 * Date: 2018/4/19.
 * Company: thinkwin
 */
@Controller
public class ProgramController {

    private static final Logger log = LoggerFactory.getLogger(ProgramController.class);

    @Resource
    PlatformProgrameLabelService platformProgrameLabelService;

    @Resource
    PlatformProgrameService platformProgrameService;
    @Resource
    PlatformProgramVersionSerevice platformProgramVersionSerevice;

    @Resource
    PlatformLabelProgramMiddleService platformLabelProgramMiddleService;

    @Resource
    FileUploadService fileUploadService;

    @Resource
    SaasRoleService saasRoleService;

    @Resource
    SaasTenantService saasTenantService;

    @Resource
    PlatformProgramComponentsMiddleService platformProgramComponentsMiddleService;

    @Resource
    PlatformProgramTenantMiddleService platformProgramTenantMiddleService;



    /**
     * 添加新的标签
     * @param platformProgrameLabelName
     * @param platformProgrameLabelId
     * @param platformProgrameIds
     * @return
     */
    @RequestMapping(value = "/createdPlatformProgrameLabel",method = RequestMethod.POST)
    @ResponseBody
    public Object createdPlatformProgrameLabel(String platformProgrameLabelName,String platformProgrameLabelId, String[] platformProgrameIds){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap<>();
        List platformProgrameIdss = new ArrayList<>();
        String newLabelId = null;
        if(null != platformProgrameIds && platformProgrameIds.length > 0) {
            platformProgrameIdss = Arrays.asList(platformProgrameIds);
        }
        if(StringUtils.isNotBlank(platformProgrameLabelName) && null != platformProgrameIdss && platformProgrameIdss.size() > 0 && StringUtils.isBlank(platformProgrameLabelId)){
        //该方法内是标签名不为空节目主键id集合不为空，标签id为空（创建标签并关联相对应的节目集合）
            //判断节目名称是否存在
            PlatformProgrameLabel ppl = new PlatformProgrameLabel();
            ppl.setLabelName(platformProgrameLabelName);
            List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelService.selectPlatformProgrameLabel(ppl);
            if(null != platformProgrameLabels && platformProgrameLabels.size() > 0){
                //newLabelId = platformProgrameLabels.get(0).getId();
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "标签名称已存在");
            }else{
                PlatformProgrameLabel platformProgrameLabel = new PlatformProgrameLabel();
                platformProgrameLabel.setLabelName(platformProgrameLabelName);
                newLabelId = platformProgrameLabelService.createdPlatformProgrameLabel(platformProgrameLabel);
                if(StringUtils.isBlank(newLabelId)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "标签创建失败");
                }
            }
            boolean b = platformProgrameService.programListAddLabel(platformProgrameIdss, newLabelId);
            if(b){
               // RedisUtil.delRedisKeys("platform_info_*");
                map.put("success",true);
                map.put("newLabelId",newLabelId);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
            }
        }else if(StringUtils.isNotBlank(platformProgrameLabelId) && null != platformProgrameIdss && platformProgrameIdss.size() > 0 && StringUtils.isBlank(platformProgrameLabelName)){
        //该方法内是标签id不为空标签名为空节目主键不为空（为节目添加标签功能）
            boolean b = platformProgrameService.programListAddLabel(platformProgrameIdss, platformProgrameLabelId);
            if(b){
                //RedisUtil.delRedisKeys("platform_info_*");
                map.put("success",true);
                map.put("newLabelId",newLabelId);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
            }
        }else if(StringUtils.isNotBlank(platformProgrameLabelName) && (null == platformProgrameIdss || platformProgrameIdss.size() == 0 )&& StringUtils.isBlank(platformProgrameLabelId)){
            //该方法内的功能是创建新的标签功能
            //判断节目名称是否存在
            PlatformProgrameLabel ppl = new PlatformProgrameLabel();
            ppl.setLabelName(platformProgrameLabelName);
            List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelService.selectPlatformProgrameLabel(ppl);
            if(null != platformProgrameLabels && platformProgrameLabels.size() > 0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "标签名称已存在");
            }
            PlatformProgrameLabel platformProgrameLabel = new PlatformProgrameLabel();
            platformProgrameLabel.setLabelName(platformProgrameLabelName);
            newLabelId = platformProgrameLabelService.createdPlatformProgrameLabel(platformProgrameLabel);
            if(StringUtils.isBlank(newLabelId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "标签创建失败");
            }
            //RedisUtil.delRedisKeys("platform_info_*");
            map.put("success",true);
            map.put("newLabelId",newLabelId);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误！！！");
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
    }

    /**
     * 标签删除功能（单一删除和批量删除合并为一个）
     * @param platformProgrameLabeIds
     * @return
     */
    @RequestMapping(value = "/delPlatformProgrameLabels",method = RequestMethod.POST)
    @ResponseBody
    public Object delPlatformProgrameLabels(String[] platformProgrameLabeIds){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        List<String> platformProgrameLabeIdss = new ArrayList<>();
        if(null != platformProgrameLabeIds && platformProgrameLabeIds.length > 0) {
            platformProgrameLabeIdss = Arrays.asList(platformProgrameLabeIds);
        if(null != platformProgrameLabeIdss && platformProgrameLabeIdss.size() > 0){
            if(platformProgrameLabeIdss.size() > 1){
                return delPlatformProgrameLabelss(platformProgrameLabeIdss);
            }else{
                return delPlatformProgrameLabelById(platformProgrameLabeIdss);
            }
        }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误！！！");
    }

    /**
     * 批量删除标签
     * @return
     */
    public Object delPlatformProgrameLabelss(List platformProgrameLabeIdss){
        Map map = new HashMap();
        boolean b = platformProgrameLabelService.delPlatformProgrameLabel(platformProgrameLabeIdss);
        if(b){
            //RedisUtil.delRedisKeys("platform_info_*");
            map.put("success",true);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
        }else{
            map.put("success",false);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Failure.getDescription(),map);
        }
    }

    /**
     * 标签单一删除
     * @return
     */
    public Object delPlatformProgrameLabelById(List<String> platformProgrameLabeIds){
        String platformProgrameLabelId = platformProgrameLabeIds.get(0);
        PlatformProgrameLabel platformProgrameLabel = platformProgrameLabelService.selectPlatformProgrameLabelByLabelId(platformProgrameLabelId);
        if(null == platformProgrameLabel){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"数据库记录已被他人修改，请刷新重试");
        }
        Map map = new HashMap();
        boolean b = platformProgrameLabelService.delPlatformProgrameLabel(platformProgrameLabeIds);
        if(b){
            //RedisUtil.delRedisKeys("platform_info_*");
            map.put("success",true);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
        }else{
            map.put("success",false);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Failure.getDescription(),map);
        }
    }

    /**
     * 根据节目的主键id获取节目详情
     * @param platformProgrameId
     * @return
     */
    @RequestMapping(value = "/selPlatformProgrameById",method = RequestMethod.POST)
    @ResponseBody
    public Object selPlatformProgrameById(String platformProgrameId){
    if(StringUtils.isNotBlank(platformProgrameId)){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        PlatformProgrameVo platformProgrameVo = platformProgrameService.selectPlatformProgramById(platformProgrameId);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),platformProgrameVo);
    }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误！！！");
    }

    /**
     * 标签修改功能
     * @param platformProgrameLabelId
     * @param platformProgrameLabelName
     * @return
     */
    @RequestMapping(value = "/updatePlatformProgrameLabel",method = RequestMethod.POST)
    @ResponseBody
    public Object updatePlatformProgrameLabel(String platformProgrameLabelId,String platformProgrameLabelName,String ver ){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        if(StringUtils.isNotBlank(platformProgrameLabelId) && StringUtils.isNotBlank(platformProgrameLabelName) && StringUtils.isNotBlank(ver)){
            PlatformProgrameLabel platformProgrameLabel = platformProgrameLabelService.selectPlatformProgrameLabelByLabelId(platformProgrameLabelId);
            if(null != platformProgrameLabel){
                String labelStatus = platformProgrameLabel.getLabelStatus();
                String ver1 = platformProgrameLabel.getVer();
                if(StringUtils.isBlank(ver1)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                }
                if(!ver.equals(ver1)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                }
                PlatformProgrameLabel platformProgrameLabell = new PlatformProgrameLabel();
                platformProgrameLabell.setLabelName(platformProgrameLabelName);
                List<PlatformProgrameLabel> platformProgrameLabells = platformProgrameLabelService.selectPlatformProgrameLabel(platformProgrameLabell);
                if(null != platformProgrameLabells && platformProgrameLabells.size() > 0){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "标签名已存在");
                }
                platformProgrameLabel.setLabelName(platformProgrameLabelName);
                boolean b = platformProgrameLabelService.updatePlatformProgrameLabel(platformProgrameLabel);
                if(b){
                    if(!"0".equals(labelStatus)) {
                        platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("3");
                        RedisUtil.delRedisKeys("platform_info_*");
                    }
                    map.put("success",true);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
                }else {
                    map.put("success",false);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Failure.getDescription(),map);
                }
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误！！！");
    }

    /**
     * 根据标签主键id获取标签详情
     * @param platformProgrameLabeId
     * @return
     */
    @RequestMapping(value = "/selPlatformProgrameLabelById",method = RequestMethod.POST)
    @ResponseBody
    public Object selInfoProgrameLabelById(String platformProgrameLabeId){
        if(StringUtils.isNotBlank(platformProgrameLabeId)){
            TenantUserVo userInfo = TenantContext.getUserInfo();
            if(null == userInfo) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            String userId = userInfo.getUserId();
            if(StringUtils.isBlank(userId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            boolean c = checkingUserRoles(userId,"信息发布管理员");
            if(!c){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
            }
            PlatformProgrameLabel platformProgrameLabel = platformProgrameLabelService.selectPlatformProgrameLabelByLabelId(platformProgrameLabeId);
            PlatformProgrameLabelVo platformProgrameLabelVo = new PlatformProgrameLabelVo();
            BeanUtils.copyProperties(platformProgrameLabel,platformProgrameLabelVo);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),platformProgrameLabelVo);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误！！！");
    }

    /**
     * 查询标签功能接口功能说明(页面初始化接口)
     * @param platformProgrameLabelId
     * @return
     */
    @RequestMapping(value = "/selPlatformProgrameLabels",method = RequestMethod.POST)
    @ResponseBody
    public Object selPlatformProgrameLabels(String platformProgrameLabelId , BasePageEntity pageEntity,String type){
        if(StringUtils.isNotBlank(type) && !"0".equals(type) && !"1".equals(type)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
        }
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        if(StringUtils.isBlank(type)){
            type="0";
        }
        if(StringUtils.isBlank(type) || "0".equals(type)) {
        List platformProgrameLabes = new ArrayList();
            List<PlatformProgrameLabel> platformProgrameLabels1 = new ArrayList<>();
            if(StringUtils.isBlank(platformProgrameLabelId) || "0".equals(platformProgrameLabelId)){
                platformProgrameLabels1 = platformProgrameLabelService.selectPlatformProgrameLabels();
        }else{
            platformProgrameLabels1 = platformProgrameLabelService.selectPlatformProgrameLabelsNew(platformProgrameLabelId);
        }
        if(null != platformProgrameLabels1 && platformProgrameLabels1.size() > 0){
            List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelService.selectPlatformProgrameLabels();
            for (PlatformProgrameLabel platformProgrameLabel:platformProgrameLabels) {
                Integer integer = platformProgrameLabelService.selectPlatformProgramNumByLabelId(platformProgrameLabel.getId());
                PlatformProgrameLabelVo platformProgrameLabelVo = new PlatformProgrameLabelVo();
                BeanUtils.copyProperties(platformProgrameLabel,platformProgrameLabelVo);//前边复制到后边
                platformProgrameLabelVo.setPlatformProgrameNum(integer);
                platformProgrameLabes.add(platformProgrameLabelVo);
            }
        }
        map.put("platformProgrameLabes",platformProgrameLabes);
        List platformProgrames = new ArrayList();
        List<PlatformProgram> platformPrograms = new ArrayList<>();
        if(StringUtils.isBlank(platformProgrameLabelId) || "0".equals(platformProgrameLabelId)){
            platformPrograms= platformProgrameService.selectAllPlatformProgram(pageEntity,type);
        }else{
            platformPrograms= platformProgrameService.selectPlatformProgramByLabelId(platformProgrameLabelId,pageEntity,type);
        }
        if(null != platformPrograms && platformPrograms.size() > 0){
            for (PlatformProgram platformProgram:platformPrograms) {
                if(null != platformProgram) {
                    PlatformProgrameVo platformProgrameVo = platformProgrameService.selectPlatformProgramByType(platformProgram.getId(),type);
                    platformProgrames.add(platformProgrameVo);
                }
            }
        }
        map.put("platformProgrames",new PageInfo<>(platformProgrames));
        Integer platformProgrameTotalNum = platformProgrameService.selectPlatformProgramTotalNum(type);
            //int size = platformProgrames.size();
            map.put("platformProgrameTotalNum",platformProgrameTotalNum);//节目的总数量
        List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.selectPlatformProgramVersion();
        String nowVersionNum = null;
        String ver = null;
        String batchOperationState = platformProgrameService.getBatchOperationState(type);
        if(null != ppvs && ppvs.size() > 0){
            PlatformProgramVersion platformProgramVersion = ppvs.get(0);
            if(null != platformProgramVersion){
                String ver1 = platformProgramVersion.getVer();
                if(StringUtils.isNotBlank(ver1)){
                    ver = ver1;
                }
                String programVersionNum = platformProgramVersion.getProgramVersionNum();
                if(StringUtils.isNotBlank(programVersionNum)){
                    nowVersionNum = programVersionNum;
                }
            }
        }else{
            PlatformProgramVersion platformProgramVersion = new PlatformProgramVersion();
            platformProgramVersion.setId(CreateUUIdUtil.Uuid());
            ver = CreateUUIdUtil.Uuid();
            platformProgramVersion.setVer(ver);
            platformProgramVersion.setCreatTime(new Date());
            boolean b = platformProgramVersionSerevice.addPlatformProgramVersion(platformProgramVersion);
            if(!b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
            }
        }
        map.put("nowVersionNum",nowVersionNum);
        map.put("ver",ver);
        map.put("batchOperationState",batchOperationState);
        }else if(StringUtils.isNotBlank(type) && "1".equals(type)){
            List platformProgrameLabes = new ArrayList();
            List<PlatformProgramTenantMiddle> tenantIds1 = new ArrayList<>();
            if(StringUtils.isBlank(platformProgrameLabelId) || "0".equals(platformProgrameLabelId)){
                tenantIds1 = platformProgrameService.selectPlatformProgramTenantMiddles();
            }else{
                tenantIds1 = platformProgrameService.selectPlatformProgramTenantMiddle(platformProgrameLabelId);
            }
            //获取租户与定制节目关联关系
            //List<PlatformProgramTenantMiddle> tenantIds = platformProgrameService.selectPlatformProgramTenantMiddle(platformProgrameLabelId);
            if(null != tenantIds1 && tenantIds1.size()>0){
                List<String> list  = new ArrayList();
                List<PlatformProgramTenantMiddle> tenantIds = platformProgrameService.selectPlatformProgramTenantMiddles();
                for (PlatformProgramTenantMiddle pptm:tenantIds) {
                    if(null != pptm){
                        String tenantId = pptm.getTenantId();
                        if(StringUtils.isNotBlank(tenantId)){
                            boolean contains = list.contains(tenantId);
                            if(!contains){
                                list.add(tenantId);
                            }
                        }
                    }
                }
                if(null != list && list.size()> 0){
                    for (String tt:list) {
                        Integer platformNum = platformProgrameService.selectpPlatformProgramTenantMiddleNumByTenantId(tt);
                        PlatformProgrameLabelVo platformProgrameLabelVo = null;
                        ////////////////////////////////////////////////////////////////////
                        String s1 = RedisUtil.get(tt+"_SaasTenantInfo");
                        if(StringUtils.isNotBlank(s1)){
                            SaasTenantInfoVo saasTenantInfoVo = JSON.parseObject(s1, SaasTenantInfoVo.class);
                            if(null != saasTenantInfoVo) {
                                platformProgrameLabelVo = new PlatformProgrameLabelVo();
                                platformProgrameLabelVo.setId(saasTenantInfoVo.getTenantId());
                                platformProgrameLabelVo.setLabelName(saasTenantInfoVo.getTenantName());
                            }
                        }else {
                            SaasTenantInfo saasTenantInfo = saasTenantService.selectSaasTenantInfoByTenantId(tt);
                            if(null != saasTenantInfo) {
                                platformProgrameLabelVo = new PlatformProgrameLabelVo();
                                platformProgrameLabelVo.setId(saasTenantInfo.getTenantId());
                                platformProgrameLabelVo.setLabelName(saasTenantInfo.getTenantName());
                            }
                        }
                        ///////////////////////////////////////////////////////////////////
                        if(null != platformProgrameLabelVo){
                            platformProgrameLabelVo.setPlatformProgrameNum(platformNum);
                            platformProgrameLabes.add(platformProgrameLabelVo);
                        }
                    }
                }
            }
            map.put("platformProgrameLabes",platformProgrameLabes);
            List platformProgrames = new ArrayList();
            List<PlatformProgram> platformPrograms = new ArrayList<>();
            if(StringUtils.isBlank(platformProgrameLabelId) || "0".equals(platformProgrameLabelId)){
                platformPrograms= platformProgrameService.selectAllPlatformProgram(pageEntity,type);
            }else{
                platformPrograms= platformProgrameService.selectPlatformProgramByLabelId(platformProgrameLabelId,pageEntity,type);
            }
            if(null != platformPrograms && platformPrograms.size() > 0){
                for (PlatformProgram platformProgram:platformPrograms) {
                    if(null != platformProgram) {
                        PlatformProgrameVo platformProgrameVo = platformProgrameService.selectPlatformProgramByType(platformProgram.getId(),type);
                        platformProgrames.add(platformProgrameVo);
                    }
                }
            }
            map.put("platformProgrames",new PageInfo<>(platformProgrames));
            Integer platformProgrameTotalNum =  platformProgrameService.selectPlatformProgramTotalNum(type);
            map.put("platformProgrameTotalNum",platformProgrameTotalNum);//节目的总数量
            List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.selectPlatformProgramVersion();
            String nowVersionNum = null;
            String ver = null;
            String batchOperationState = platformProgrameService.getBatchOperationState(type);
            if(null != ppvs && ppvs.size() > 0){
                PlatformProgramVersion platformProgramVersion = ppvs.get(0);
                if(null != platformProgramVersion){
                    String ver1 = platformProgramVersion.getVer();
                    if(StringUtils.isNotBlank(ver1)){
                        ver = ver1;
                    }
                    String programVersionNum = platformProgramVersion.getProgramVersionNum();
                    if(StringUtils.isNotBlank(programVersionNum)){
                        nowVersionNum = programVersionNum;
                    }
                }
            }else{
                PlatformProgramVersion platformProgramVersion = new PlatformProgramVersion();
                platformProgramVersion.setId(CreateUUIdUtil.Uuid());
                ver = CreateUUIdUtil.Uuid();
                platformProgramVersion.setVer(ver);
                platformProgramVersion.setCreatTime(new Date());
                boolean b = platformProgramVersionSerevice.addPlatformProgramVersion(platformProgramVersion);
                if(!b){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
                }
            }
            map.put("nowVersionNum",nowVersionNum);
            map.put("ver",ver);
            map.put("batchOperationState",batchOperationState);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 删除单个节目和单个标签的关联关系功能
     * @param platformProgrameId
     * @param platformProgrameLabelId
     * @return
     */
    @RequestMapping(value = "/delPlatformProgrameAndLabelmiddle",method = RequestMethod.POST)
    @ResponseBody
    public Object delPlatformProgrameAndLabelmiddle (String platformProgrameId,String platformProgrameLabelId ){
    Map map = new HashMap();
        if(StringUtils.isNotBlank(platformProgrameId) && StringUtils.isNotBlank(platformProgrameLabelId)){
            TenantUserVo userInfo = TenantContext.getUserInfo();
            if(null == userInfo) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            String userId = userInfo.getUserId();
            if(StringUtils.isBlank(userId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            boolean c = checkingUserRoles(userId,"信息发布管理员");
            if(!c){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
            }
           // List<PlatformLabelProgramMiddle> pflpm = platformLabelProgramMiddleService.getPlatformLabeProgramMiddleBylabelIdAndProgrameId(platformProgrameId,platformProgrameLabelId);
            List list = new ArrayList();
            List list1 = new ArrayList();
            list.add(platformProgrameId);
            list1.add(platformProgrameLabelId);
            boolean b = platformLabelProgramMiddleService.delPlatformLabelProgramMiddle(list, list1);
            if(b) {
                //RedisUtil.delRedisKeys("platform_info_*");
                map.put("success", true);
            }else{
                map.put("success", false);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误！！！");
    }

    /**
     * 组件中标签查询功能
     * @return
     */
    @RequestMapping(value = "/selAssemblyPlatformProgrameLabels",method = RequestMethod.POST)
    @ResponseBody
    public Object selAssemblyPlatformProgrameLabes(){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        List list = new ArrayList();
        String platformProgrameLabelTotalNum = null;
        List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelService.selectPlatformProgrameLabels();
        if(null != platformProgrameLabels && platformProgrameLabels.size() > 0){
            for (PlatformProgrameLabel platformProgrameLabel:platformProgrameLabels) {
                if(null != platformProgrameLabel){
                    PlatformProgrameLabelVo pplv = new PlatformProgrameLabelVo();
                    BeanUtils.copyProperties(platformProgrameLabel,pplv);
                    list.add(pplv);
                }
            }
            int size = platformProgrameLabels.size();
            platformProgrameLabelTotalNum = String.valueOf(size);
        }
        map.put("list",list);
        map.put("platformProgrameLabelTotalNum",platformProgrameLabelTotalNum);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 获取当前版本号功能接口
     * @return
     */
    @RequestMapping(value = "/getNowProgramVersion",method = RequestMethod.POST)
    @ResponseBody
    public Object getNowProgramVersion(String type){
        Map map = new HashMap();
        String programVersionNum = null;
        String versionUpdateBatch = null;
        List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.selectPlatformProgramVersionNew();
        String batchOperationState = platformProgrameService.getBatchOperationState(type);
        if(null != ppvs && ppvs.size() > 0){
            PlatformProgramVersion platformProgramVersion = ppvs.get(0);
            if(null != platformProgramVersion){
                programVersionNum = platformProgramVersion.getProgramVersionNum();
                versionUpdateBatch = platformProgramVersion.getVersionUpdateBatch();
            }

        }
        map.put("programVersionNum",programVersionNum);
        map.put("versionUpdateBatch",versionUpdateBatch);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 获取版本信息列表功能接口(带分页)
     * @return
     */
    @RequestMapping(value = "/getProgramVersionList",method = RequestMethod.POST)
    @ResponseBody
    public Object getProgramVersionList(BasePageEntity basePageEntity){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        List list = new ArrayList();
        String programVersionTotalNum = "0";
        List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.selectPlatformProgramVersionNew();//不带分页
        if(null != ppvs && ppvs.size() > 0){
            for (PlatformProgramVersion platformProgramVersion:ppvs) {
                if(null != platformProgramVersion){
                    PlatformProgramVersionVo platformProgramVersionVo = new PlatformProgramVersionVo();
                    BeanUtils.copyProperties(platformProgramVersion,platformProgramVersionVo);
                    list.add(platformProgramVersionVo);
                }
            }
        }
        int size = ppvs.size();
        programVersionTotalNum = String.valueOf(size);
        map.put("programVersionTotalNum",programVersionTotalNum);
        map.put("list",list);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 节目同步功能
     * @param oldProgramVersionNum
     * @param nextProgramVersionNum
     * @param ver
     * @param publishNote
     * @return
     */
    @RequestMapping(value = "/platformProgrameSynchronization",method = RequestMethod.POST)
    @ResponseBody
    public Object platformProgrameSynchronization(String oldProgramVersionNum, String nextProgramVersionNum,String ver,String publishNote,String type){
        if(StringUtils.isNotBlank(type) && !"0".equals(type) && !"1".equals(type)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
        }
        if (/*StringUtils.isNotBlank(nextProgramVersionNum) && */StringUtils.isNotBlank(ver) /*&& StringUtils.isNotBlank(publishNote)*/){
            TenantUserVo userInfo = TenantContext.getUserInfo();
            if(null == userInfo) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            String userId = userInfo.getUserId();
            if(StringUtils.isBlank(userId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            boolean c = checkingUserRoles(userId,"信息发布管理员");
            if(!c){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
            }
            String programVersionNum = null;
            Map map = new HashMap();
            List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.newSelectPlatformProgramVersion("0",null);
            if(null != ppvs && ppvs.size() > 0){  //在同步必须通过内测功能才能执行同步功能的情况下不回去状况
                PlatformProgramVersion platformProgramVersion = ppvs.get(0);
                if(null != platformProgramVersion){
                    programVersionNum = platformProgramVersion.getProgramVersionNum();
                    String ver1 = platformProgramVersion.getVer();
                    if(StringUtils.isBlank(ver1)){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                    }
                    if(!ver.equals(ver1)){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                    }
                    //此处校验版本的生成规则(需要考虑内测是版本号会空的时候的问题)
                    PlatformProgramVersion platformProgramVersionNew = new PlatformProgramVersion();
                    boolean b = false;
                    if(StringUtils.isBlank(type)){
                        type = "0";
                    }
                    if(StringUtils.isBlank(type) || "0".equals(type)) {
                        if (StringUtils.isNotBlank(oldProgramVersionNum) && StringUtils.isNotBlank(programVersionNum)) {
                            double d = Double.parseDouble(programVersionNum);
                            String newProgramVersionNum = d + 1 + "";
                            if (!nextProgramVersionNum.equals(newProgramVersionNum)) {
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "版本号错误");
                            }
                            platformProgramVersionNew.setProgramVersionNum(nextProgramVersionNum);
                            platformProgramVersionNew.setPublishNote(publishNote);
                            platformProgramVersionNew.setPublishTime(new Date());
                            b = platformProgramVersionSerevice.newAddPlatformProgramVersion(platformProgramVersionNew);
                        } else if (StringUtils.isBlank(programVersionNum)) {
                            double d = Double.parseDouble("0.0");
                            String newProgramVersionNum = d + 1 + "";
                            if (!nextProgramVersionNum.equals(newProgramVersionNum)) {
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "版本号错误");
                            }
                            //如果版本号符合生成规则就向下执行否则返回操作失败提示
                            platformProgramVersionNew.setId(platformProgramVersion.getId());
                            platformProgramVersionNew.setProgramVersionNum(nextProgramVersionNum);
                            platformProgramVersionNew.setPublishNote(publishNote);
                            platformProgramVersionNew.setCreatTime(new Date());
                            platformProgramVersionNew.setPublishTime(new Date());
                            b = platformProgramVersionSerevice.updatePlatformProgarmVersion(platformProgramVersionNew);
                        }
                    }else if(StringUtils.isNotBlank(type) && "1".equals(type)){
                        b=true;
                    }
                    if (b) {
                        boolean bb = platformProgrameService.platformProgrameSynchronization(nextProgramVersionNum,type);
                        if(!bb){
                            map.put("success", false);
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                        }
                        RedisUtil.delRedisKeys("platform_info_*");
                        map.put("success", true);
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                    } else {
                        map.put("success", false);
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                    }
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误！！！");
    }


    /**
     * 节目进行内测功能接口
     * @return
     */
    @RequestMapping(value = "/platformProgrameBeta")
    @ResponseBody
    public Object platformProgrameBeta(String ver,String type){
        if(StringUtils.isNotBlank(type) && !"0".equals(type) && !"1".equals(type)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
        }
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.selectPlatformProgramVersion();
                if(null != ppvs && ppvs.size() > 0 ) {
                    if(StringUtils.isBlank(ver)){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
                    }
                    PlatformProgramVersion platformProgramVersion = ppvs.get(0);
                    if (null != platformProgramVersion) {
                        String ver1 = platformProgramVersion.getVer();
                        if(!ver.equals(ver1)){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                        }
                        platformProgramVersion.setId(platformProgramVersion.getId());
                        boolean b = platformProgramVersionSerevice.updatePlatformProgarmVersion(platformProgramVersion);
                        if(!b){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
                        }
                    }
                }else{
                    PlatformProgramVersion newPlatformPrograme = new PlatformProgramVersion();
                    boolean b = platformProgramVersionSerevice.addPlatformProgramVersion(newPlatformPrograme);
                    if(!b){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
                    }
                }
                if(StringUtils.isBlank(type)){
                    type = "0";
                }
                boolean b = platformProgramVersionSerevice.platformProgrameBeta(type);
                if(b) {
                    RedisUtil.delRedisKeys("platform_info_*");
                    map.put("success", true);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                }else{
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
                }
    }


    /**
     * 标签排序功能
     * @param platformProgrameLabelId
     * @param sortPlatformProgrameLabelId
     * @param sortType 被移动的标签排序类型 1 参考标签的上方 2 参考标签的下方 3移动到参考标签的目录下
     * @return
     */
    @RequestMapping(value = "/platformProgrameLabelsort",method = RequestMethod.POST)
    @ResponseBody
    public Object platformProgrameLabelSort(String platformProgrameLabelId,String sortPlatformProgrameLabelId,String sortType){
        if(StringUtils.isNotBlank(platformProgrameLabelId)&&StringUtils.isNotBlank(sortPlatformProgrameLabelId)&&StringUtils.isNotBlank(sortType)){
            TenantUserVo userInfo = TenantContext.getUserInfo();
            if(null == userInfo) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            String userId = userInfo.getUserId();
            if(StringUtils.isBlank(userId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            boolean c = checkingUserRoles(userId,"信息发布管理员");
            if(!c){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
            }
            Map map = new HashMap();
            PlatformProgrameLabel platformProgrameLabel = platformProgrameLabelService.selectPlatformProgrameLabelByLabelId(platformProgrameLabelId);
            if(null == platformProgrameLabel){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
            }
            PlatformProgrameLabel platformProgrameLabel1 = platformProgrameLabelService.selectPlatformProgrameLabelByLabelId(sortPlatformProgrameLabelId);
            if(null == platformProgrameLabel1){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
            }
            boolean b = platformProgrameLabelService.platformProgrameLabelSort(platformProgrameLabelId,sortPlatformProgrameLabelId,sortType);
            if(b){
                RedisUtil.delRedisKeys("platform_info_*");
                map.put("success", true);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            }else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
    }


    /**
     * 批量删除节目功能接口(和单个删除功能合并为一个接口)
     * @param platformProgrameIds
     * @return
     */
    @RequestMapping(value = "/delPlatformProgrames",method = RequestMethod.POST)
    @ResponseBody
    public Object delPlatformPrograme(HttpServletRequest request,String[]  platformProgrameIds){
        if(null != platformProgrameIds && platformProgrameIds.length > 0) {
            TenantUserVo userInfo = TenantContext.getUserInfo();
            if(null == userInfo) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            String userId = userInfo.getUserId();
            if(StringUtils.isBlank(userId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            boolean c = checkingUserRoles(userId,"信息发布管理员");
            if(!c){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
            }
            String path = request.getSession().getServletContext()
                    .getRealPath("/");
            FILEDIR = path + "template" + File.separator + "test";
            Map map = new HashMap();
            List<String> platformProgrameIdss = new ArrayList<>();
            if (null != platformProgrameIds && platformProgrameIds.length > 0) {
                platformProgrameIdss = Arrays.asList(platformProgrameIds);
            }
            boolean cc = false;
            boolean bb = false;
            if (null != platformProgrameIdss && platformProgrameIdss.size() > 0) {
                for (String pPId:platformProgrameIdss) {
                    if(StringUtils.isNotBlank(pPId)){
                        PlatformProgrameVo platformProgrameVo = platformProgrameService.selectPlatformProgramById(pPId);
                        if(null == platformProgrameVo){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                        }
                        String programStatus = platformProgrameVo.getProgramStatus();
                        if(StringUtils.isNotBlank(programStatus)){
                            if("2".equals(programStatus)){
                                cc = true;
                            }
                            if(!"0".equals(programStatus)){
                                bb = true;
                            }
                        }
                    }
                }
            boolean b = platformProgrameService.delPlatformPrograms(platformProgrameIdss, FILEDIR);
            if (b) {
                if(cc) {
                    platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("3");
                }
                if(bb){
                    RedisUtil.delRedisKeys("platform_info_*");
                }
                map.put("success", true);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            }
        }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
    }

    /**
     * 修改节目版本管理内容
     * @param programVersionId
     * @param publishNote
     * @return
     */
    @RequestMapping(value = "/updateProgramVersion",method = RequestMethod.POST)
    @ResponseBody
    public Object updateProgramVersion(String programVersionId, String publishNote,String ver){
        if(StringUtils.isNotBlank(programVersionId)&& StringUtils.isNotBlank(publishNote)&&StringUtils.isNotBlank(ver)) {
            TenantUserVo userInfo = TenantContext.getUserInfo();
            if(null == userInfo) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            String userId = userInfo.getUserId();
            if(StringUtils.isBlank(userId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
            }
            boolean c = checkingUserRoles(userId,"信息发布管理员");
            if(!c){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
            }
            PlatformProgramVersion platformProgramVersion = platformProgramVersionSerevice.selectPlatformProgramVersionById(programVersionId);
            if(null == platformProgramVersion){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
            String ver1 = platformProgramVersion.getVer();
            if(StringUtils.isBlank(ver1)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
            if(!ver.equals(ver1)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
            Map map = new HashMap();
            PlatformProgramVersion ppv = new PlatformProgramVersion();
            ppv.setId(programVersionId);
            ppv.setPublishNote(publishNote);
            boolean b = platformProgramVersionSerevice.updatePlatformProgarmVersion(ppv);
            if(b) {
                //RedisUtil.delRedisKeys("platform_info_*");
                map.put("success", true);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
    }

    /**
     * 节目修改功能
     * @return
     */
    @RequestMapping(value = "/updatePlatformProgrames",method = RequestMethod.POST)
    @ResponseBody
    public Object updatePlatformPrograme(MultipartFile file,String platformProgrameId,String programeName,String ver) throws IOException {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作有误");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        boolean cc = false;
        if(StringUtils.isNotBlank(platformProgrameId)) {
            PlatformProgrameVo platformProgrameVo = platformProgrameService.selectPlatformProgramById(platformProgrameId);
            if (null != platformProgrameVo) {
                String programStatus1 = platformProgrameVo.getProgramStatus();
                if("2".equals(programStatus1)){
                   cc=true;
                }
                String ver1 = platformProgrameVo.getVer();
                if(StringUtils.isNotBlank(ver)){
                    if(!ver1.equals(ver)){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                    }
                }
                Map map = new HashMap();
            Map mmp = new HashMap();//该值用来获取上传服务的返回值
            PlatformProgram platformProgram = new PlatformProgram();
            if (file != null) {
                String ext = "png";
                Map<String, String> map1 = new HashMap<>();
                map1.put("big", "300_225");
                map1.put("in", "240_240");
                map1.put("small", "60_45");
                List<FastdfsVo> vos = FileUploadUtil.fileUpload(map1, file.getBytes(), ext);
                FileVo fileVo = fileUploadService.insertFileUpload("3", userId, null, null, ext, vos);
                if (null != fileVo) {
                    boolean b = platformProgrameService.updateBizImageRecorderByBizId(userId, fileVo, platformProgrameId, "1");
                    if (!b) {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
                    }
                }
            }
            if (StringUtils.isNotBlank(programeName)) {
                List<PlatformProgram> platformPrograms = platformProgrameService.selectPlatformProgramsByName(programeName);
                if (null != platformPrograms && platformPrograms.size() > 0) {
                    for (PlatformProgram platformProgram1 : platformPrograms) {
                        if (null != platformProgram1) {
                            String id = platformProgram1.getId();
                            if (!platformProgrameId.equals(id)) {
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "节目名称已存在");
                            }
                        }
                    }
                }
            }
            boolean b = false;
                String programName = platformProgrameVo.getProgramName();
            if(StringUtils.isNotBlank(programName) && programName.equals(programeName) && null == file){
                  b = true;
            } else {
                platformProgram.setId(platformProgrameId);
                platformProgram.setProgramName(programeName);
                String programVersionNum = platformProgrameVo.getProgramVersionNum();
                String programStatus = platformProgrameVo.getProgramStatus();
                if(StringUtils.isNotBlank(programVersionNum) && !"0".equals(programStatus) && !"2".equals(programStatus)){
                    platformProgram.setProgramStatus("0");
                }else{
                    platformProgram.setProgramStatus(programStatus);
                }
                b = platformProgrameService.updatePlatformProgram(platformProgram);
            }
            if (b) {
                RedisUtil.delRedisKeys("platform_info_*");
//                if(cc){
//                    //2018/6/14   需求定为节目只有在同步时才会更新正式版本的内容
//                   RedisUtil.remove("platform_info_platformProgrames_2");
//                   RedisUtil.remove("platform_info_bizImageRecorders");
//                   RedisUtil.remove("platform_info_platformLabelProgramMiddles");
//                }
                map.put("success", true);
            } else {
                map.put("success", false);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "节目已被删除");
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
    }


    /**
     * 条件查询节目功能接口
     * @param seachKey
     * @param platformProgrameLabeId
     * @return
     */
    @RequestMapping(value = "/selPlatformProgrames",method = RequestMethod.POST)
    @ResponseBody
    public Object selPlatformProgrames(String seachKey,String platformProgrameLabeId,BasePageEntity pageEntity,String type){
        if(StringUtils.isNotBlank(type) && !"0".equals(type) && !"1".equals(type)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
        }
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        List list = new ArrayList();
        if(StringUtils.isBlank(type)){
            type = "0";
        }
        if(StringUtils.isNotBlank(platformProgrameLabeId) && !"0".equals(platformProgrameLabeId)) {
            if("0".equals(type)) {
                list = platformProgrameLabelService.selectPlatformProgrameIdsByLabelId(platformProgrameLabeId);
            }else if("1".equals(type)){
                list = platformProgrameLabelService.selectPlatformProgrameIdsByLabelType(platformProgrameLabeId);
            }
            if (null == list || list.size() <= 0) {
                map.put("list",new ArrayList<>());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            }
        }
        List<PlatformProgram> list1 = platformProgrameService.selectAllPlatformProgramBySeachKey(seachKey,list,pageEntity,type);
        Integer integer = platformProgrameService.selectPlatformProgramTotalNumBySeachKey(seachKey,list,type);
          List platformProgrames = new ArrayList();
        if(null != list1 && list1.size() > 0){
            for (PlatformProgram platformProgram:list1) {
                if(null != platformProgram) {
                    PlatformProgrameVo platformProgrameVo = platformProgrameService.selectPlatformProgramById(platformProgram.getId());
                    platformProgrames.add(platformProgrameVo);
                }
            }
        }

          map.put("list",platformProgrames);
          map.put("platformProgrameTotalNum",integer);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }


    /**
     *  节目预览功能接口
     * @return
     */
    @RequestMapping(value = "/platformProgramePreview",method = RequestMethod.POST)
    @ResponseBody
    public Object platformProgramePreview(HttpServletRequest request,String platformProgrameId,String ver){
        String path = request.getSession().getServletContext()
                .getRealPath("/");
        String s1 = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map map = new HashMap();
        if(StringUtils.isNotBlank(platformProgrameId) && StringUtils.isNotBlank(ver)) {
            PlatformProgrameVo platformProgrameVo = platformProgrameService.selectPlatformProgramById(platformProgrameId);
            if(null == platformProgrameVo){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
            String ver1 = platformProgrameVo.getVer();
            if(StringUtils.isBlank(ver1)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
            if(!ver.equals(ver1)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
            BizImageRecorder bir = platformProgrameService.getBizImageRecorderByBizId(platformProgrameId);
            if (null  == bir) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "还没有上传视频文件");
            }
            String imageId = bir.getImageId();
            if(StringUtils.isBlank(imageId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
            }
            if(StringUtils.isNotBlank(platformProgrameVo.getProgramVersionNum()) && "1".equals(platformProgrameVo.getProgramStatus())) {
                FILEDIR = path + "template"+File.separator+"formal"+File.separator+imageId;
            }else{
                FILEDIR = path + "template"+File.separator+"test"+File.separator+imageId;
            }
            File filePath = new File(FILEDIR);
            String newPath = FILEDIR + File.separator ;
            if(filePath.exists()){
                String f1 = FILEDIR;
                File file11 = new File(f1);
                String[] test3=file11.list();
                for(int i=0;i<test3.length;i++) {
                    //需要创建解压后的文件存储路径
                    System.out.println(test3[i]);
                    FILEDIR+=File.separator+test3[i];
                    if(!"template".equals(test3[i])) {
                        File file4 = new File(f1+File.separator+test3[i]);
                        String[] test5 = file4.list();
                        for (int j=0;j < test5.length;j++) {
                            if(!"template".equals(test5[j])) {
                                String prefix123 = test5[j].substring(test5[j].lastIndexOf("."));
                                if (".html".equals(prefix123)) {
                                    FILEDIR += File.separator + test5[j];
                                }
                            }
                        }
                    }
                }
                String sysAttachmentUrl = null;
                if(FILEDIR.indexOf("template")!=-1) {
                    sysAttachmentUrl = FILEDIR.substring(FILEDIR.lastIndexOf("template"));
                }
                if(StringUtils.isNotBlank(sysAttachmentUrl) && sysAttachmentUrl.indexOf(".html") ==-1){
                    String s = path + File.separator + sysAttachmentUrl;
                    clearFiles(s);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "获取预览文件失败，请重试");
                }
                map.put("sysAttachmentUrl", s1+File.separator+sysAttachmentUrl);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            }else if (!filePath.exists()) {
                mkDir(filePath);
            }
            SysAttachment sysAttachment = fileUploadService.selectByidFile(imageId);
            String fileName = FileUploadUtil.downloadFile(newPath,sysAttachment);/*fileUploadService.downloadFile(imageId, newPath);*/
            if(StringUtils.isBlank(fileName)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
            }
            String s = newPath + fileName;
            unZip(s);
            clearFiles(s);
            String f = FILEDIR;
            File file1 = new File(f);
            String[] test=file1.list();
            String sysAttachmentUrl = s.substring(s.lastIndexOf("template"));
            sysAttachmentUrl = sysAttachmentUrl.substring(0,sysAttachmentUrl.lastIndexOf("."));
            for(int i=0;i<test.length;i++) {
                //需要创建解压后的文件存储路径
                System.out.println(test[i]);
                if(!"template".equals(test[i])) {
                    File file2 = new File(f+File.separator+test[i]);
                    String[] test2 = file2.list();
                    for (int j=0;j < test2.length;j++) {
                        if(!"template".equals(test2[j])) {
                            String prefix12 = test2[j].substring(test2[j].lastIndexOf("."));
                            if (".html".equals(prefix12)) {
                                sysAttachmentUrl += File.separator + test2[j];
                            }
                        }
                    }
                }
            }
            map.put("sysAttachmentUrl", s1+File.separator+sysAttachmentUrl);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "必填参数有误");
        }

        @Resource
    PlatformProgramComponentsService platformProgramComponentsService;
    /**
     *  节目添加功能接口
     * @return
     */
    @RequestMapping(value = "/addPlatformPrograme",method = RequestMethod.POST)
    @ResponseBody
    public Object addPlatformPrograme(HttpServletRequest request,MultipartFile file, String platformProgrameId,String ver,String tenantId) throws IOException {
        String path1 = request.getSession().getServletContext()
                .getRealPath("/");
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        PlatformProgrameVo platformProgrameVo = null;
        String imageId = null;
        if(StringUtils.isBlank(platformProgrameId)){
            platformProgrameId = CreateUUIdUtil.Uuid();
        }else{
            platformProgrameVo = platformProgrameService.selectPlatformProgramById(platformProgrameId);
            if(null != platformProgrameVo){
                String ver1 = platformProgrameVo.getVer();
                if(StringUtils.isBlank(ver1)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                }
                if(!ver.equals(ver1)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
                }
                String programVersionNum = platformProgrameVo.getProgramVersionNum();
                if(StringUtils.isNotBlank(programVersionNum)){
                        return  ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "正式版本下的节目不可修改视频文件");
                }
                BizImageRecorder bir = platformProgrameService.getBizImageRecorderByBizId(platformProgrameId);
                if (null  != bir) {
                    imageId = bir.getImageId();
                }
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据库记录已被他人修改，请刷新重试");
            }
        }
        PlatformProgram platformProgram = new PlatformProgram();
        platformProgram.setId(platformProgrameId);
        Properties properties = new Properties();
        try {
            InputStream is = ProgramController.class.getClassLoader().getResourceAsStream("service.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
        }
        String basePath = properties.getProperty("file.outputPath");
        String picture = FileOperateUtil.uploads(file, "template");
        String path = basePath + File.separator + picture;
        String prefix=path.substring(0,path.lastIndexOf("."));
        if (file.getSize() != 0 && !"".equals(file.getOriginalFilename())) {
            String fn = file.getOriginalFilename();
            String platformProgrameName = fn.substring(0,fn.lastIndexOf("."));
            List<PlatformProgram> platformPrograms = platformProgrameService.selectPlatformProgramsByName(platformProgrameName);
            if(null != platformPrograms && platformPrograms.size() > 0){
                clearFiles(basePath + File.separator + "template");
                PlatformProgram platformProgram1 = platformPrograms.get(0);
                if(null != platformProgram1){
                    String id = platformProgram1.getId();
                    if(StringUtils.isNotBlank(id)){
                        if(!id.equals(platformProgrameId)){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "节目名称已存在");
                        }
                    }
                }

            }
            platformProgram.setProgramName(platformProgrameName);
        }else{
            clearFiles(basePath + File.separator + "template");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请选择上传文件");
        }
        Map map = new HashMap();
            unZip(path);
            String f = prefix;
            File file1 = new File(f);
            String[] test=file1.list();
            if(null != test && test.length > 0) {
                List<String> strings = Arrays.asList(test);
                if(null != strings && strings.size() > 0) {
                List llst = new ArrayList();
                    for (String ss:strings) {
                        if(StringUtils.isNotBlank(ss) && !"template".equals(ss) &&(ss.contains(".jpg")||ss.contains(".html") || ss.contains(".json"))){
                            String ext = ss.substring(ss.lastIndexOf("."));
                            llst.add(ext);
                        }
                    }
                    boolean contains = llst.contains(".jpg");
                    if(!contains){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"上传失败，请检查节目包内容");
                    }
                    boolean contains1 = llst.contains(".html");
                    if(!contains1){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"上传失败，请检查节目包内容");
                    }
                    boolean contains2 = llst.contains(".json");
                    if(!contains2){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"上传失败，请检查节目包内容");
                    }
                }
            }
        for(int i=0;i<test.length;i++) {
                if(!"template".equals(test[i])){
                System.out.println(test[i]);
                String prefix1 = test[i].substring(test[i].lastIndexOf("."));
                if (".jpg".equals(prefix1)) {
                    String s = prefix + File.separator + test[i];
                    byte[] bytes = File2byteUtil.File2byte(s);
                    String ext = "png";
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("big", "300_225");
                    map1.put("in", "240_240");
                    map1.put("small", "60_45");
                    List<FastdfsVo> vos = FileUploadUtil.fileUpload(map1,bytes,ext);
                    FileVo fileVo = fileUploadService.insertFileUpload("3", userId, null, null, ext,vos);
                    if (null != fileVo) {
                        boolean b = platformProgrameService.updateBizImageRecorderByBizId(userId, fileVo, platformProgrameId, "1");
                        if(!b){
                            clearFiles(basePath + File.separator + "template");
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
                        }
                    }
                }
                    if (".json".equals(prefix1)) {
                        String s = f + File.separator +  test[i];
                        String s1 = new JsonUtil().ReadFile(s);
                        JsonData jsonData = new JsonData();
                        if(StringUtils.isNotBlank(s1)){
                            jsonData = JSON.parseObject(s1, JsonData.class);
                        }
                        String types = jsonData.getTypes();
                        if(StringUtils.isNotBlank(types)){
                            String[] split = types.split(",");
                       if(null != split && split.length > 0){
                           List<String> stringss = Arrays.asList(split);
                           if(null != stringss && stringss.size() > 0){
                               for (String ttt:stringss) {
                                if(StringUtils.isNotBlank(ttt)){
                                    //该处需要添加判断组件code是否合法的校验
                                    //.....
                                    boolean b = platformProgramComponentsService.selectPlatformProgramComponentsByComponentsCode(ttt);
                                    if(b) {
                                        platformProgramComponentsMiddleService.addPlatformProgramComponentsMiddle(platformProgrameId, ttt);
                                    }else{
                                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "上传失败，请检查节目包内容");//ertre
                                    }
                                }
                               }
                           }
                       }
                        }
                    }
            }
            }
        if(file != null) {
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            List<FastdfsVo> vos = FileUploadUtil.fileUpload(null,file.getBytes(),ext);
            FileVo fileVo = fileUploadService.insertFileUpload("3",userId, null,null,ext,vos);
            if(null != fileVo){
                boolean b = platformProgrameService.updateBizImageRecorderByBizId(userId, fileVo, platformProgrameId, "2");
                if(!b){
                    clearFiles(basePath + File.separator + "template");
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作失败");
                }
                platformProgram.setSysAttachmentId(fileVo.getId());
            }
        }
        clearFiles(basePath + File.separator + "template");
        boolean b = false;
        String programStatus = "0";
        if(null == platformProgrameVo){
            platformProgram.setCreater(userId);
            platformProgram.setProgramStatus("0");
            if(StringUtils.isNotBlank(tenantId)){
                platformProgram.setProgramType("1");
            }else{
                platformProgram.setProgramType("0");
            }
            b = platformProgrameService.addPlatformProgram(platformProgram);
        }else {
            programStatus = platformProgrameVo.getProgramStatus();
            if(!"1".equals(programStatus)){
                platformProgram.setProgramStatus(programStatus);
            }else {
                platformProgram.setProgramStatus("0");
            }
            String programType = platformProgrameVo.getProgramType();
            if(StringUtils.isBlank(programType)){
                platformProgram.setProgramType("0");
            }else{
                platformProgram.setProgramType(programType);
            }
            b = platformProgrameService.updatePlatformProgram(platformProgram);
            if(b){
                if(StringUtils.isNotBlank(imageId)){
                    String filePath = path1 + "template"+File.separator+"test"+File.separator+imageId;
                    clearFiles(filePath);
                }
            }
        }
        if(b){
            if(StringUtils.isNotBlank(tenantId)) {
                platformProgramTenantMiddleService.addPlatformProgramTenantMiddle(platformProgrameId, tenantId);
            }
            if(StringUtils.isNotBlank(platformProgrameId) && !"0".equals(programStatus)) {
                platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("3");
                RedisUtil.delRedisKeys("platform_info_*");
            }
        map.put("success", true);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
       }else {
            map.put("success", false);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
    }

    //删除文件和目录
    private void clearFiles(String workspaceRootPath) {
        File file = new File(workspaceRootPath);
        if (file.exists()) {
            deleteFile(file);
        }
    }

    //删除文件和目录
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }


    /**
     *
     * 校验用户角色（实时加载角色信息）
     * @param userId
     * @param roleName
     * @return
     */
    public boolean checkingUserRoles(String userId,String roleName){
        List<SaasRole> userRolesByUserId = saasRoleService.findUserRolesByUserId(userId);
        if(null != userRolesByUserId && userRolesByUserId.size() > 0){
            List list = new ArrayList();
            for (SaasRole sr:userRolesByUserId) {
                if(null != sr){
                    String roleName1 = sr.getRoleName();
                    if(StringUtils.isNotBlank(roleName1)){
                        list.add(roleName1);
                    }
                }
            }
            if(null != list && list.size() > 0) {
                if (list.contains("系统管理员") || list.contains("信息发布管理员")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 校验用户的权限功能（节目管理部分使用）
     * @return
     */
    @RequestMapping("/checkUserRole")
    @ResponseBody
    public Object checkUserRole(){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "操作成功","/gotoVersionsLogPage",null);
        }
    }

    /**
     *  获取定制节目上传时的租户数据集功能接口
     * @param seachKey
     * @return
     */
    @RequestMapping(value = "/getAllTenants")
    @ResponseBody
    public Object getAllTenants(String seachKey,BasePageEntity basePageEntity){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请重新登录");
        }
        boolean c = checkingUserRoles(userId,"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        if(StringUtils.isNotBlank(seachKey)){
            List<CustomizingTenantVo> list1 = new ArrayList();
            String isCustomizedTenantCache = RedisUtil.get("isCustomizedTenantCache");
            if(StringUtils.isNotBlank(isCustomizedTenantCache)){
                list1 = JSON.parseArray(isCustomizedTenantCache, CustomizingTenantVo.class);
            }else{
                list1 = platformProgrameService.getAllTenants();
                if(null != list1 && list1.size() > 0){
                    String s = JSON.toJSONString(list1);
                    if(StringUtils.isNotBlank(s)){
                        RedisUtil.set("isCustomizedTenantCache",s);
                    }
                }
            }
            List list = new ArrayList();
            if(null != list1 && list1.size() > 0){
                for (CustomizingTenantVo stv:list1) {
                    if(null != stv){
                        String tenantName = stv.getTenantName();
                        if(StringUtils.isNotBlank(tenantName)){
                            boolean contains = tenantName.contains(seachKey);
                            if(contains){
                                list.add(stv);
                            }
                        }
                    }
                }
            }
           // basePageEntity = null;//由于当前需求定为不加分页功能但是分页对象中有默认值所以要在此处给置为null便于后面的扩展
            Map map = new HashMap();
            //List<CustomizingTenantVo> list = platformProgrameService.getAllTenants(seachKey,basePageEntity);
            map.put("list",list);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请选择节目制定租户");
    }





}
