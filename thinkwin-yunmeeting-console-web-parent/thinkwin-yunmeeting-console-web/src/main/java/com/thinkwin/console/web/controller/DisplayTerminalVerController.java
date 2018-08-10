package com.thinkwin.console.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.VersionRecode;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.db.SysDictionary;
import com.thinkwin.common.model.publish.PlatformInfoClientVersionLib;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.console.constant.ConsoleConstant;
import com.thinkwin.console.service.SaasRoleService;
import com.thinkwin.dictionary.service.DictionaryService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.publish.service.PlatformClientVersionUpgradeRecorderService;
import com.thinkwin.publish.service.PlatformInfoClientVersionLibService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

/*
 * 类说明：会议显示管理-终端版本管理对外接口
 * @author lining 2018/4/8
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/displayTerminalVer")
public class DisplayTerminalVerController {

    private final Logger log = LoggerFactory.getLogger(DisplayTerminalVerController.class);

    @Resource
    FileUploadService fileUploadService;

    @Resource
    SaasRoleService saasRoleService;

    @Resource
    public PlatformInfoClientVersionLibService platformInfoClientVersionLibService;

    @Resource
    PlatformClientVersionUpgradeRecorderService platformClientVersionUpgradeRecorderService;
    @Resource
    DictionaryService dictionaryService;

    /**
     * 终端版本管理
     * @param hide 1:显示当前内测及正式版本;0:不显示
     * @param searchKey 搜索关键字（文件名称、版本编号）
     * @return
     */
    @RequestMapping("/versionList")
    @ResponseBody
    public ResponseResult versionList(String hide,String searchKey,BasePageEntity basePageEntity,String userId){
        ResponseResult responseResult=new ResponseResult();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        Map<String,Object> map=this.platformInfoClientVersionLibService.getData(searchKey,hide,basePageEntity);
        responseResult.setData(map);
        responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
        return responseResult;
    }

    /**
     * 设置/取消内测
     * @param id
     * @param verStatus
     * @return
     */
    @RequestMapping("/setVersion")
    @ResponseBody
    public ResponseResult setVersion(String id,String verStatus,BasePageEntity basePageEntity){
        ResponseResult responseResult=new ResponseResult();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }

        PlatformInfoClientVersionLib versionLib=this.platformInfoClientVersionLibService.getId(id);
        if(versionLib!=null){
            //设置内测
            if(verStatus.equals("1")){
                PlatformInfoClientVersionLib nc=this.platformInfoClientVersionLibService.findTerminalVersionByVerType("1");
                if(nc==null){
                    if(versionLib.getVerStatus().equals("0")){
                        versionLib.setVerStatus("1");
                        versionLib.setModifyTime(new Date());
                        this.platformInfoClientVersionLibService.setTerminalVersion(versionLib);
                        responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
                        responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerOperate1.getDescription());
                    }else{
                        responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                        responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerErr2.getDescription());

                  }
                }else{
                    responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                    responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerErr3.getDescription());
                }
            }else{
                    //取消内测
                    if(versionLib.getVerStatus().equals("1")){
                        versionLib.setVerStatus("0");
                        versionLib.setModifier(userVo.getUserName());
                        versionLib.setModifyTime(new Date());
                        this.platformInfoClientVersionLibService.setTerminalVersion(versionLib);
                        responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
                        responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerOperate2.getDescription());
                    }else{
                        responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                        responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerErr2.getDescription());
                    }
            }
        }else {
            responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
            responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerNull.getDescription());
        }

        Map<String,Object> map=this.platformInfoClientVersionLibService.getData(null,ConsoleConstant.TerVerHIDE,basePageEntity);
        responseResult.setData(map);
        return responseResult;
    }


    /**
     *  发布版本
     * @param id 版本Id
     * @param releaseStatus 状态（0无、1发布）
     * @return
     */
    @RequestMapping("/releaseVersion")
    @ResponseBody
    public ResponseResult releaseVersion(String id,String releaseStatus,BasePageEntity basePageEntity){
        ResponseResult responseResult=new ResponseResult();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }
        PlatformInfoClientVersionLib versionLib=this.platformInfoClientVersionLibService.getId(id);
        if(versionLib.getVerStatus().equals("1")) {
            PlatformInfoClientVersionLib now = this.platformInfoClientVersionLibService.findTerminalVersionByVerType("2");
           //第一次上传终端版本
            if(null==now){
                //设置发布状态
                versionLib.setVerStatus("2");
                versionLib.setReasleStatus("1");
                versionLib.setModifier(userVo.getUserName());
                versionLib.setModifyTime(new Date());
                this.platformInfoClientVersionLibService.setTerminalVersion(versionLib);
                responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerOperate4.getDescription());
            }else if (versionLib.getCode() > now.getCode() || (versionLib.getCode() == now.getCode() && versionLib.getVerNum().equals(now.getVerNum()))) {
                //将当前版本置为历史版本
                now.setVerStatus("3");
                now.setReasleStatus("2");
                now.setModifier(userVo.getUserName());
                now.setModifyTime(new Date());
                this.platformInfoClientVersionLibService.setTerminalVersion(now);

                //设置发布状态
                versionLib.setVerStatus("2");
                versionLib.setReasleStatus("1");
                versionLib.setModifier(userVo.getUserName());
                versionLib.setModifyTime(new Date());
                this.platformInfoClientVersionLibService.setTerminalVersion(versionLib);
                responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerOperate4.getDescription());
            } else {
                responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerLow.getDescription());
            }
        }else{
            responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
            responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerErr2.getDescription());
        }
        Map<String,Object> map=this.platformInfoClientVersionLibService.getData(null,ConsoleConstant.TerVerHIDE,basePageEntity);
        responseResult.setData(map);
        return responseResult;
    }

    /**
     * 删除内测版本
     * @param id 版本Id
     * @return
     */
    @RequestMapping("/delVersion")
    @ResponseBody
    public ResponseResult delVersion(String id,BasePageEntity basePageEntity){
        ResponseResult responseResult=new ResponseResult();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }

        PlatformInfoClientVersionLib versionLib=this.platformInfoClientVersionLibService.getId(id);
        if(versionLib!=null){
            if(versionLib.getReasleStatus().equals("0")){
                this.platformInfoClientVersionLibService.delTerminalVersion(id);
                responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerOperate3.getDescription());
            }else{
                responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerErr1.getDescription());
            }
        }else{
            responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
            responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerNull.getDescription());
        }

        Map<String,Object> map=this.platformInfoClientVersionLibService.getData(null,"1",basePageEntity);
        responseResult.setData(map);
        return responseResult;
    }

    /**
     * 版本更新情况
     * @param id 版本Id
     * @param searchKey
     * @return
     */
    @RequestMapping("/getVerUpdateInfo")
    @ResponseBody
    public ResponseResult getVerUpdateInfo(String id,String searchKey,BasePageEntity basePageEntity){
        ResponseResult responseResult=new ResponseResult();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }

        try{
            Map<String,Object> map=this.platformClientVersionUpgradeRecorderService.findByClientVersionIdAndTenantName(id,searchKey,basePageEntity);
            responseResult.setData(map);
        }catch(Exception e){
            responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
        }
        responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
        return responseResult;
    }

    /**
     * 上传版本文件
     * @param verfile
     * @return
     */
    @RequestMapping(value = "/uploadVerFile",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult uploadVerFile(MultipartFile file,String userId,BasePageEntity basePageEntity){
        ResponseResult responseResult=new ResponseResult();

        PlatformInfoClientVersionLib versionLib=new PlatformInfoClientVersionLib();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }

        //code:1000:正常，1001:文件为空，1002:文件格式不对，1003:版本信息异常，1004:版本号低
        int FLAG=1000;

        try{
            Properties properties = new Properties();
            try {
                InputStream is = ProgramController.class.getClassLoader().getResourceAsStream("service.properties");
                properties.load(is);
            } catch (Exception e) {
                e.getStackTrace();
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }

            String basePath = properties.getProperty("file.outputPath");
            String versionFile = FileOperateUtil.uploads((MultipartFile)file, "template");
            String filePath = basePath + File.separator + versionFile; //本地临时上传路径

            String verfileName = file.getOriginalFilename();
            String ext=null;


        //检查上传文件是否为空
        if(file != null) {
            //1.检查APK格式
            ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            if (ConsoleConstant.TerVerEXT.equalsIgnoreCase(ext)) {
                    //2.解析终端版本信息并校验信息、与当前最新版本比较高低
                    long fileSize=file.getSize();
                    int apkIndex = versionFile.indexOf(".apk");
                    String apkName=versionFile.substring(0,apkIndex);

                    //解压apk压缩包
                     ApkFileUtil.unZip(basePath+File.separator+versionFile);//解压

                    //读取终端版本信息并校验版本号
                    String str = ApkFileUtil.readTxt(basePath+File.separator+apkName+File.separator+"assets"+File.separator+"update.txt");
                    //File dir = new File(basePath+File.separator+apkName);
                    //ApkFileUtil.deleteDir(dir);
                    String fileName = null;
                    String pkgNmae = null;
                    String version = null;
                    String code=null;
                    String projectInfo = null;
                    String terminalCategory = null;
                    String versionDetail = null;
                    String[] arry = str.split(",");
                    for(int i = 0; i < arry.length;i++){
                        String[] st;
                        if(arry[i].contains(":")){//英文符
                            st = arry[i].split(":");
                        }else{
                            st = arry[i].split("：");//中文符
                        }
                        String tempStr = st[0].trim();
                        String contentTemp = "";
                        if(st.length > 1){
                            contentTemp = st[1];
                        }
                        if(StringUtils.isBlank(tempStr) || StringUtils.isBlank(contentTemp)){
                            continue;
                        }
                        contentTemp = contentTemp.trim();
                        tempStr = tempStr.toLowerCase();
                        String f1="file_name";
                        if(tempStr.indexOf(f1)>-1){
                            fileName = contentTemp;
                        }
                        if("pkg_name".equals(tempStr)){
                            pkgNmae = contentTemp;
                        }
                        if("version".equals(tempStr)){
                            version = contentTemp;
                            if(version.contains("v")){
                                version.replace("v", "");
                            }
                        }
                        if("code".equals(tempStr) ){
                            code = contentTemp.intern();
                        }
                        if("project_info".equals(tempStr) ){
                            projectInfo = contentTemp;
                        }
                        if("terminal_type".equals(tempStr)){
                            terminalCategory = contentTemp;
                        }
                        if("version_detail".equals(st[0])){
                            versionDetail = contentTemp;
                        }
                    }

                //APK大小
                String size ="0B";
                DecimalFormat df = new DecimalFormat("#.00");
                if (fileSize < 1024) {
                    size = fileSize + "B";
                } else if (fileSize < 1048576) {
                    size = df.format(fileSize / 1024) + "K";
                } else if (fileSize < 1073741824) {
                    size = df.format(fileSize / 1048576) + "M";
                }


                if(version.indexOf("-") > 0){
                        version = version.substring(0, version.indexOf("-"));
                    }
                    String temp = version.replace(".","");
                    Integer versionNum = Integer.valueOf(temp);

                    //校验解析内容
                    if(StringUtils.isBlank(fileName) ||StringUtils.isBlank(pkgNmae)|| StringUtils.isBlank(version) || StringUtils.isBlank(code) ||StringUtils.isBlank(projectInfo) ||StringUtils.isBlank(terminalCategory) || StringUtils.isBlank(versionDetail)){
                        FLAG=1003;
                    }

                    //校验版本号
                    int number=(code!=null)?Integer.parseInt(code):0;
                    if(FLAG==1000){
                        PlatformInfoClientVersionLib nowVersion=this.platformInfoClientVersionLibService.getTerminalVersion("2");
                        if(nowVersion!=null){
                            if(nowVersion.getCode()>number || (number==nowVersion.getCode() && version.equals(nowVersion.getVerNum())) ){
                                FLAG=1004;
                            }
                        }
                    }

                    String UUID = CreateUUIdUtil.Uuid();
                    versionLib.setId(UUID);
                    versionLib.setVersionTitle(fileName);
                    versionLib.setCode(number);
                    versionLib.setChangeRecode(versionDetail);
                    versionLib.setReasleStatus("0");
                    versionLib.setVerStatus("0");
//                    String type= "TERMINAL_TYPE_"+terminalCategory.toUpperCase();
//                    SysDictionary sysDictionary=this.dictionaryService.selectByIdSysDictionary(type);
//                    versionLib.setTerminalType(null!=sysDictionary?sysDictionary.getDictName():terminalCategory);
                    versionLib.setTerminalType(terminalCategory);
                    versionLib.setVerNum(version);
                    versionLib.setSize(size);
                    versionLib.setCreater(userVo.getUserName());
                    versionLib.setChangeNum(0);
                    versionLib.setCreatTime(new Date());
                    versionLib.setModifier(userId);
                    versionLib.setModifyTime(new Date());
            } else {
                FLAG=1002;
            }
        }else{
            FLAG=1001;
        }

        //解析校验结果
        switch(FLAG){
            case 1001:
                responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                responseResult.setMsg(BusinessExceptionStatusEnum.NotExistFile.getDescription());
                break;
            case 1002:
                responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerExtErr.getDescription());
                break;
            case 1003:
                responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerAnalysisErr.getDescription());
                break;
            case 1004:
                responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerLow.getDescription());
                break;
            default:
                break;
        }

            if(FLAG==1000){
                //3.上传终端版本
                byte[] bytes=ApkFileUtil.fileToBytes(filePath);
                List<FastdfsVo> vos = FileUploadUtil.fileUpload(null,bytes,ext);
                FileVo fileVo = fileUploadService.insertFileUpload( ConsoleConstant.source, userId, null, null, ext,vos);
                //4.保存终端版本信息
                versionLib.setSysAttachmentId(fileVo.getId());
                versionLib.setSysAttachmentUrl(fileVo.getPrimary());
                this.platformInfoClientVersionLibService.addTerminalVersion(versionLib);
                responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
                responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerSuccess.getDescription());
            }
            Map<String,Object> map=this.platformInfoClientVersionLibService.getData(null,"1",basePageEntity);
            responseResult.setData(map);
        }catch(Exception e){
            responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
            responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerFail.getDescription());
            responseResult.setCode("-1");
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 全部更新记录
     * @return
     */
    @RequestMapping("/getAllRecode")
    @ResponseBody
    public ResponseResult getAllRecode(){
        ResponseResult responseResult=new ResponseResult();
        Map<String,Object> map=new HashMap<>();
        List<VersionRecode> recodes=new ArrayList<>();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }

        List<PlatformInfoClientVersionLib> list=this.platformInfoClientVersionLibService.getReleaseAll(null,"0");
        for(PlatformInfoClientVersionLib v:list){
            VersionRecode recode=new VersionRecode();
            recode.setTitle(v.getVerNum());
            recode.setRecode(v.getChangeRecode());
            recodes.add(recode);
        }
        map.put("minVerList",recodes);

        responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
        responseResult.setData(map);
        return responseResult;
    }

    /**
     * 显示终端版本详情
     * @return
     */
    @RequestMapping("/getVersion")
    @ResponseBody
    public ResponseResult getVersion(String id){
        ResponseResult responseResult=new ResponseResult();

        TenantUserVo userVo = TenantContext.getUserInfo();
        boolean c = checkingUserRoles(userVo.getUserId(),"信息发布管理员");
        if(!c){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(10, "您没有权限，请联系平台管理员");
        }

        PlatformInfoClientVersionLib versionLib=this.platformInfoClientVersionLibService.getId(id);
        if(versionLib!=null){
            responseResult.setData(versionLib);
        }else{
            responseResult.setIfSuc(ConsoleConstant.IfSuc.FAIL);
            responseResult.setMsg(BusinessExceptionStatusEnum.TerminalVerNull.getDescription());
            return responseResult;
        }
        responseResult.setIfSuc(ConsoleConstant.IfSuc.SUCCESS);
        return responseResult;
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


}
