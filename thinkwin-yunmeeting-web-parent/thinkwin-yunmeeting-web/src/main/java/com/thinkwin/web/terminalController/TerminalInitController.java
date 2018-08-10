package com.thinkwin.web.terminalController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.TerminalDto;
import com.thinkwin.common.dto.publish.TerminalProgramDto;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.db.SysDictionary;
import com.thinkwin.common.model.log.TerminalLog;
import com.thinkwin.common.model.publish.PlatformClientVersionUpgradeRecorder;
import com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.TerminalInfoVo.TerminalInfoVo;
import com.thinkwin.common.vo.TerminalInfoVo.TerminalTenantVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.dictionary.service.DictionaryService;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.publish.service.PlatformClientVersionUpgradeRecorderService;
import com.thinkwin.publish.service.PlatformTenantTerminalMiddleService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.InfoProgramService;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import com.thinkwin.yuncm.service.TerminalService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User:wangxilei
 * Date:2018/5/17
 * Company:thinkwin
 */
@Controller
@RequestMapping("/terminalClient")
public class TerminalInitController {
    @Resource
    private InfoProgramService infoProgramService;
    @Resource
    private SaasTenantService saasTenantCoreService;
    @Resource
    private TerminalLogService terminalLogService;
    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    PlatformClientVersionUpgradeRecorderService platformClientVersionUpgradeRecorderService;
    @Resource
    PlatformTenantTerminalMiddleService platformTenantTerminalMiddleService;
    @Resource
    DictionaryService dictionaryService;
    @Resource
    private TerminalService terminalService;

    private static Logger logger = LoggerFactory.getLogger(TerminalInitController.class);

    /**
     * 获取终端节目列表
     * @param terminalId 终端Id
     * @return
     */
    @RequestMapping(value = "/getAllProgram",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult getAllProgram(String terminalId,String tenantId){
        if(StringUtils.isNotBlank(terminalId)&&StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
            List<String> terminalIds = new ArrayList<>();
            terminalIds.add(terminalId);
            List<TerminalProgramDto> list = infoProgramService.selectProgramByTerminalIds(terminalIds);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), list);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 获取租户详情（包括管理密码、授权是否过期）
     * @param tenantId 租户Id
     * @return
     */
    @RequestMapping(value = "/getTenantInfo",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult getTenantInfo(String tenantId){
        if(StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            if(saasTenant!=null){
                TerminalTenantVo vo = new TerminalTenantVo();
                vo.setId(saasTenant.getId());
                vo.setContacts(saasTenant.getContacts());
                vo.setContactsTel(saasTenant.getContactsTel());
                vo.setTenantCode(saasTenant.getTenantCode());
                vo.setTenantName(saasTenant.getTenantName());
                vo.setBasePackageStart(saasTenant.getBasePackageStart());
                vo.setBasePackageExpir(saasTenant.getBasePackageExpir());
                if("0".equals(saasTenant.getTenantType())) {
                    vo.setBasePackageType("免费版");
                }else {
                    vo.setBasePackageType("专业版");
                }
                vo.setTerminalManagerPasswd(saasTenant.getTerminalManagerPasswd());
                vo.setAuthor(1);
                if(saasTenant.getBasePackageExpir()!=null){
                    if(new Date().getTime()>saasTenant.getBasePackageExpir().getTime()){
                        vo.setAuthor(0);
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), vo);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 判断授权是否过期
     * @param tenantId 租户Id
     * @return
     */
    @RequestMapping(value = "/getExpiredState",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult getExpiredState(String tenantId){
        if(StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            if(saasTenant!=null){
                int author = 1;
                if(saasTenant.getBasePackageExpir()!=null){
                    if(new Date().getTime()>saasTenant.getBasePackageExpir().getTime()){
                        author = 0;
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), author);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 验证管理员密码
     * @param password 密码（密文）
     * @return
     */
    @RequestMapping(value = "/verifyAdminPassword",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult verifyAdminPassword(String tenantId,String password){
        if(StringUtils.isNotBlank(password)){
            TenantContext.setTenantId(tenantId);
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            if (saasTenant != null) {
                int ok = 0;
                if (saasTenant.getTerminalManagerPasswd()!=null&&saasTenant.getTerminalManagerPasswd().equals(password)) {
                    ok = 1;
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), ok);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 终端上传错误日志
     * @param data 日志对象json
     * @return
     */
    @RequestMapping(value = "/addLogByTerminal",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult addLogByTerminal(String data){
        if(StringUtils.isNotBlank(data)){
            TerminalLog log = JSON.parseObject(data, TerminalLog.class);
            if(log!=null){
                terminalLogService.addTerminalLog(log);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 获取终端详情
     * @return
     */
    @RequestMapping(value = "/getTerminalDetail")
    @ResponseBody
    public Object getTerminalDetail(@RequestParam("terminalId")String terminalId) {
        if(StringUtils.isNotBlank(terminalId)) {
            TerminalDto dto = terminalService.getTerminalById4Mobile(terminalId);
//            TerminalDto vo = new TerminalDto();
//            if(dto!=null){
//                vo.setBackgroundUrl(dto.getBackgroundUrl());
//            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), dto);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 方法名：perfectTerminalInfo</br>
     * 描述：解密终端成功后调用的完善终端信息接口</br>
     * 参数：[terminalInfoVo]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/perfectTerminalInfo" ,method = RequestMethod.POST)
    @ResponseBody
    public Object perfectTerminalInfo(String terminalInfoVo){
        TerminalInfoVo terminalInfoVo1 = JSON.parseObject(terminalInfoVo, TerminalInfoVo.class);
        // String terminalInfoVo1 = request.getParameter("terminalInfoVo");
        if(null != terminalInfoVo1){
            //TerminalInfoVo terminalInfoVo = new TerminalInfoVo();
            String tenantId = terminalInfoVo1.getTenantId();
            String hardwareId = terminalInfoVo1.getHardwareId();
            if(StringUtils.isNotBlank(hardwareId) && StringUtils.isNotBlank(tenantId)){
                TenantContext.setTenantId(tenantId);
                InfoReleaseTerminal infoReleaseTerminal = infoReleaseTerminalService.selectInfoReleaseTerminalByHardwareId(hardwareId);
                if(null != infoReleaseTerminal){
                    TenantUserVo userInfo = TenantContext.getUserInfo();
                    if(null != userInfo){
                        String userId = userInfo.getUserId();
                        if(StringUtils.isNotBlank(userId)){
                            infoReleaseTerminal.setCreater(userId);
                        }
                    }
                    //处理终端信息Vo的数据
                    infoReleaseTerminal = handleInfoReleaseTerminalVoParam(terminalInfoVo1,infoReleaseTerminal);
                    if(null != infoReleaseTerminal){
                        boolean b = infoReleaseTerminalService.updateInfoReleaseTerminal(infoReleaseTerminal);
                        if(b){
                            //维护租户终端关系表的终端名称字段
                            if(StringUtils.isNotBlank(infoReleaseTerminal.getTerminalName())){
                                PlatformTenantTerminalMiddle platformTenantTerminalMiddle = new PlatformTenantTerminalMiddle();
                                platformTenantTerminalMiddle.setId(infoReleaseTerminal.getId());
                                platformTenantTerminalMiddle.setTerminalName(infoReleaseTerminal.getTerminalName());
                                platformTenantTerminalMiddleService.updatePTenantTerminalMByEntity(platformTenantTerminalMiddle);
                            }
                            //增加版本日志信息
                            PlatformClientVersionUpgradeRecorder pCVURecorder = new PlatformClientVersionUpgradeRecorder();
                            //查询租户信息
                            String tenantType = "";
                            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                            if(null != saasTenant){
                                pCVURecorder.setPhoneNumber(saasTenant.getContactsTel());
                                pCVURecorder.setTenantName(saasTenant.getTenantName());
                                pCVURecorder.setTenantNamePinyin(PingYinUtil.getPingYin(saasTenant.getTenantName()));
                                pCVURecorder.setUserName(saasTenant.getContacts());
                                tenantType = saasTenant.getIsInnerTest();
                            }
                            logger.info("租户库用户类型是否是内测用户======================"+tenantType);
                            String currClientVer = infoReleaseTerminal.getCurrClientVer();
                            //查询版本日志是否存在
                            boolean plCliVerUpgradeRecorder = platformClientVersionUpgradeRecorderService.findPlCliVerUpgradeRecorder(tenantId, currClientVer, hardwareId);
                            logger.info("版本日志======================"+plCliVerUpgradeRecorder);
                            if(!plCliVerUpgradeRecorder) {
                                logger.info("====================增加版本日志======================");
                                pCVURecorder.setClientVersionId(infoReleaseTerminal.getCurrClientVer());
                                pCVURecorder.setCreateTime(new Date());
                                pCVURecorder.setCurrentVer(currClientVer);
                                pCVURecorder.setHardwareId(hardwareId);
                                pCVURecorder.setId(CreateUUIdUtil.Uuid());
                                pCVURecorder.setStatus("1");
                                pCVURecorder.setTenantId(infoReleaseTerminal.getTenantId());
                                platformClientVersionUpgradeRecorderService.add(pCVURecorder);
                            }
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,"完善成功", BusinessExceptionStatusEnum.Success.getCode());
                        }else{
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.SysError.getDescription(),BusinessExceptionStatusEnum.SysError.getCode());
                        }
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：handleInfoReleaseTerminalParam</br>
     * 描述：处理终端信息vo参数</br>
     * 参数：[terminalInfoVo]</br>
     * 返回值：com.thinkwin.common.model.db.InfoReleaseTerminal</br>
     */
    public InfoReleaseTerminal handleInfoReleaseTerminalVoParam(TerminalInfoVo terminalInfoVo,InfoReleaseTerminal infoReleaseTerminal) {
        try {
            if (null != terminalInfoVo) {
                String id = infoReleaseTerminal.getId();
                infoReleaseTerminal = new InfoReleaseTerminal();
                infoReleaseTerminal.setId(id);
                if (StringUtils.isNotBlank(terminalInfoVo.getTenantId()))
                    infoReleaseTerminal.setTenantId(terminalInfoVo.getTenantId());

                if (StringUtils.isNotBlank(terminalInfoVo.getHardwareId()))
                    infoReleaseTerminal.setHardwareId(terminalInfoVo.getHardwareId());

                if (StringUtils.isNotBlank(terminalInfoVo.getTerminalName()))
                    infoReleaseTerminal.setTerminalName(/*new String(*/terminalInfoVo.getTerminalName()/*.getBytes("ISO-8859-1"), "UTF-8")*/);

                if (StringUtils.isNotBlank(terminalInfoVo.getTerminalTypeId()))
                    infoReleaseTerminal.setTerminalTypeId(terminalInfoVo.getTerminalTypeId());

                String terminalOs = "";
                if (StringUtils.isNotBlank(terminalInfoVo.getTerminalOs())) {
                    terminalOs = terminalInfoVo.getTerminalOs().toUpperCase();
                    String dictCode = "DEVICE_TYPE_" + terminalOs;
                    String s = handleDeviceDict(dictCode);
                    if (StringUtils.isNotBlank(s)) {
                        infoReleaseTerminal.setTerminalOs(s);
                    }
                }
                if (StringUtils.isNotBlank(terminalInfoVo.getInitOsVer())) {
                    String initOsVer = terminalInfoVo.getInitOsVer();
                    if (initOsVer.indexOf(".") != -1) {
                        initOsVer = initOsVer.substring(0, initOsVer.lastIndexOf("."));
                        if (StringUtils.isNotBlank(terminalOs)) {
                            String dictCode = terminalOs + "_" + initOsVer;
                            String s = handleDeviceDict(dictCode);
                            if (StringUtils.isNotBlank(s)) {
                                infoReleaseTerminal.setInitOsVer(s);
                            }
                        }
                    }
                }
                if (StringUtils.isNotBlank(terminalInfoVo.getCurrOsVer())) {
                    String currOsVer = terminalInfoVo.getCurrOsVer();
                    if (currOsVer.indexOf(".") != -1) {
                        currOsVer = currOsVer.substring(0, currOsVer.lastIndexOf("."));
                        if (StringUtils.isNotBlank(terminalOs)) {
                            String dictCode = terminalOs + "_" + currOsVer;
                            String s = handleDeviceDict(dictCode);
                            if (StringUtils.isNotBlank(s)) {
                                infoReleaseTerminal.setCurrOsVer(s);
                            }
                        }
                    }
                }

                if (StringUtils.isNotBlank(terminalInfoVo.getInitClientVer()))
                    infoReleaseTerminal.setInitClientVer(terminalInfoVo.getInitClientVer());

                if (StringUtils.isNotBlank(terminalInfoVo.getCurrClientVer()))
                    infoReleaseTerminal.setCurrClientVer(terminalInfoVo.getCurrClientVer());

                if (StringUtils.isNotBlank(terminalInfoVo.getResolutionRatio())) {
                    String resolutionRatio = terminalInfoVo.getResolutionRatio();
                    if(resolutionRatio.indexOf("*") != -1){
                        resolutionRatio = resolutionRatio.replace("*","×");
                    }
                    infoReleaseTerminal.setResolutionRatio(resolutionRatio);
                }
                if (null != terminalInfoVo.getTerminalBrightness())
                    infoReleaseTerminal.setTerminalBrightness(terminalInfoVo.getTerminalBrightness());

                if (null != terminalInfoVo.getTerminalVolume())
                    infoReleaseTerminal.setTerminalVolume(terminalInfoVo.getTerminalVolume());

                if (StringUtils.isNotBlank(terminalInfoVo.getStatus()))
                    infoReleaseTerminal.setStatus(terminalInfoVo.getStatus());

                return infoReleaseTerminal;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String handleDeviceDict(String dictCode){
        SysDictionary sysDictionary = dictionaryService.selectByIdSysDictionary(dictCode);
        if(null != sysDictionary){
            return sysDictionary.getDictId();
        }else{
            return null;
        }
    }
}
