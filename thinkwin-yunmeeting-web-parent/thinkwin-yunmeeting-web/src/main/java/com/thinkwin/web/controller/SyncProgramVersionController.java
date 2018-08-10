package com.thinkwin.web.controller;

import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.VersionRecode;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.model.publish.PlatformInfoClientVersionLib;
import com.thinkwin.common.model.publish.PlatformProgramVersion;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.FileUploadUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.SyncProVo;
import com.thinkwin.common.vo.consoleVo.PlatformProgramVersionVo;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.publish.service.PlatformInfoClientVersionLibService;
import com.thinkwin.publish.service.PlatformProgramVersionSerevice;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.BizImageRecorderService;
import com.thinkwin.yuncm.service.SyncProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明：会议显示管理-终端版本管理对外接口
 * @author lining 2018/4/8
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/syncProgramVer")
public class SyncProgramVersionController {


    private final Logger log = LoggerFactory.getLogger(SyncProgramVersionController.class);

    /**
     * 1表示内测用户
     */
    private final String InnerTest="1";

    @Resource
    private com.thinkwin.core.service.SaasTenantService saasTenantCoreService;

    @Resource
    SyncProgramService syncProgramService;

    @Resource
    BizImageRecorderService bizImageRecorderService;

    @Resource
    FileUploadService fileUploadService;

    @Resource
    PlatformInfoClientVersionLibService platformInfoClientVersionLibService;

    @Resource
    PlatformProgramVersionSerevice platformProgramVersionSerevice;

    /**
     * 跳转到versionsLog页面
     *
     * @return
     */
    @RequestMapping("/gotoVersionsLogPage")
    public String gotoVersionsLogPage() {
        return "publish_terminal/versionsLog";
    }

    /**
     * 跳转到更新故障排除页面  前端要求增加跳转页面  2018/07/23  weining
     *
     * @return
     */
    @RequestMapping("/gototroublePage")
    public String gotoTroublePage() {
        return "publish_terminal/troubleRemoval";
    }


    /**
     * 检查节目版本
     * @param tenantId 租户ID
     * @return
     */
    @RequestMapping("/checkProgramVer")
    @ResponseBody
    public ResponseResult checkProgramVer(String tenantId){
        ResponseResult responseResult=new ResponseResult();

        //1.根据租户ID获取租户信息
        SaasTenant saasTenant=saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());

        //租户没有终端，则不需要更新节目
        Integer terminalCount=saasTenant.getTerminalCount();
        if(null==terminalCount || terminalCount<=0){
            responseResult.setIfSuc(1);
            responseResult.setCode("1000");
            return responseResult;
        }

        //当前租户类型1正式，0内测
        int tenantType=1;
        if(null!=saasTenant.getIsInnerTest() && saasTenant.getIsInnerTest().equals(InnerTest)){
             tenantType=0;
        }
        SyncProVo syncProVo=this.syncProgramService.checkVersion(TenantContext.getTenantId(),tenantType);
        responseResult.setIfSuc(1);
        responseResult.setMsg(syncProVo.getDescribe());
        responseResult.setCode(syncProVo.code+"");
        return responseResult;
    }

    /**
     * 更新节目
     * @param tenantId 租户ID
     * @return
     */
    @RequestMapping("/upgradeProgramVer")
    @ResponseBody
    public ResponseResult upgradeProgramVer(HttpServletRequest request, String tenantId){
        ResponseResult responseResult=new ResponseResult();

        String path = request.getSession().getServletContext().getRealPath("/");
        String s1 = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        try{

        //当前租户类型1正式，0内测
        int tenantType=1;
        //1.根据租户ID获取租户信息
        SaasTenant saasTenant=saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
        if(null!=saasTenant.getIsInnerTest() && saasTenant.getIsInnerTest().equals(InnerTest)){
            tenantType=0;
        }

        // 1000 无需同步节目
         int syncStatus=1000;
        //检查是否需要更新节目
        SyncProVo syncProVo=this.syncProgramService.checkVersion(TenantContext.getTenantId(),tenantType);
        SyncProVo syncProVo1=null;
        if(syncProVo.getCode()!=syncStatus){
            //更新节目
            syncProVo1=this.syncProgramService.updateProgramVer(TenantContext.getTenantId(),tenantType,syncProVo.code);
            if(null!=syncProVo1){
                if(syncProVo1.getCode()==1){

                    //同步平台节目包到租户平台
                    File filePath=null;
                    List<BizImageRecorder> local=this.bizImageRecorderService.findByType("2");
                    for(BizImageRecorder biz:local){
                        String fileDir = path + "template"+File.separator+biz.getImageId();
                        String newPath = fileDir + File.separator ;
                        filePath=new File(fileDir);
                        if(!filePath.exists()){
                           SysAttachment sysAttachment=this.fileUploadService.selectByidFile(biz.getImageId());
                            boolean f=FileUploadUtil.syncProgramFile(newPath,filePath,sysAttachment);
                            if(!f){
                                log.info("同步节目文件出错，请检查！节目id:"+biz.getId());
                            }
                        }
                    }

                    responseResult.setIfSuc(1);
                    responseResult.setMsg(syncProVo1.getDescribe());
                    responseResult.setCode(syncProVo1.getCode()+"");
                }else{
                    responseResult.setIfSuc(0);
                    responseResult.setMsg(syncProVo1.getDescribe());
                }
            }else{
                responseResult.setIfSuc(0);
                responseResult.setMsg(syncProVo1.getDescribe());
                responseResult.setCode(syncProVo1.getCode()+"");
            }
        }else{
            responseResult.setIfSuc(1);
            responseResult.setMsg(syncProVo1.getDescribe());
        }
        }catch (Exception e){
            responseResult.setIfSuc(0);
            responseResult.setMsg("节目更新失败，请重试");

        }
        return responseResult;
    }

    /**
     * 全部终端版本更新记录
     * @return
     */
    @RequestMapping("/getAllRecode")
    @ResponseBody
    public ResponseResult getAllRecode(){
        ResponseResult responseResult=new ResponseResult();
        Map<String,Object> map=new HashMap<>();
        List<VersionRecode> recodes=new ArrayList<>();
        List<PlatformInfoClientVersionLib> list=this.platformInfoClientVersionLibService.getReleaseAll(null,"0");
        for(PlatformInfoClientVersionLib v:list){
            VersionRecode recode=new VersionRecode();
            recode.setTitle(v.getVerNum());
            recode.setRecode(v.getChangeRecode());
            recodes.add(recode);
        }
        map.put("minVerList",recodes);

        responseResult.setIfSuc(1);
        responseResult.setData(map);
        return responseResult;
    }


    /**
     * 获取版本信息列表功能接口(带分页)
     * @return
     */
    @RequestMapping(value = "/getProgramVersionList",method = RequestMethod.POST)
    @ResponseBody
    public Object getProgramVersionList(BasePageEntity basePageEntity){
        Map map = new HashMap();
        List list = new ArrayList();
        String programVersionTotalNum = "0";
        //不带分页
        List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.selectPlatformProgramVersionNew();
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


}
