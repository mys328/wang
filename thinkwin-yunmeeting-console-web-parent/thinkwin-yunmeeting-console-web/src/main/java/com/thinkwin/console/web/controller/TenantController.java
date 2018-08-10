package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.log.SysLogType;
import com.thinkwin.common.model.publish.PlatformProgramVersion;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.console.service.SaasRoleService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.goodscenter.service.ProductPackSkuService;
import com.thinkwin.log.service.SysLogTypeService;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.publish.service.PlatformProgramVersionSerevice;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.propertyeditors.ReaderEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * 租户管理
 */
@Controller
@RequestMapping("/tenant")
public class TenantController {

    @Resource
    SaasTenantService saasTenantService;

    @Resource
    ProductPackSkuService productPackSkuService;

    @Resource
    PricingConfigService pricingConfigService;

    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;

    @Resource
    private SaasRoleService saasRoleService;

    @Resource
    private TerminalLogService terminalLogService;

    @Resource
    private SysLogTypeService sysLogTypeService;

    /*
   * 查询租户分页
    * */
    @RequestMapping("/selectSaasTenantListByPage")
    @ResponseBody
    public Object selectSaasTenantListByPage(BasePageEntity basePageEntity, String content
            , @RequestParam(value = "option",required = false)String option) {
        SaasTenant saasTenant = new SaasTenant();
        saasTenant.setTenantName(content);

        //查询所有租户
        if(option!=null){
            saasTenant.setTerminalCount(1);
        }
        PageInfo<SaasTenant> saasTenantPageInfo = this.saasTenantService.selectSaasTenantListByPage(basePageEntity, saasTenant);
        List<SaasTenant> saasTenantList = saasTenantPageInfo.getList();

        //查询所有sku版本
        //List<ProductPackSku> productPackSkuList = this.productPackSkuService.selectProductPackSkuList();

        if (saasTenantList != null && saasTenantList.size() > 0) {
            for (SaasTenant saasTenant1 : saasTenantList) {
                if(null != saasTenant1){
                    if(StringUtils.isBlank(saasTenant1.getIsCustomizedTenant())){
                        saasTenant1.setIsCustomizedTenant("0");
                    }
                }
                if(saasTenant1.getBasePackageExpir() != null){
                    saasTenant1.setBasePackageExpir(DateUtils.addDays(DateUtils.truncate(saasTenant1.getBasePackageExpir(), Calendar.DAY_OF_MONTH), -1));
                }
                if("0".equals(saasTenant1.getTenantType())){
                    saasTenant1.setBasePackageType("免费版");
                    //获取定价配置
                    PricingConfigDto configDto = pricingConfigService.getPricingConfig();
                    //获取免费价格
                    List<CapacityConfig> configs = configDto.getFreeAccountConfig();
                    for(CapacityConfig config : configs){
                        //会议室数量
                        if("100".equals(config.getSku())){
                            saasTenant1.setBuyRoomNumTotal(config.getQty());
                            saasTenant1.setBasePackageRoomNum(0);
                        }
                        //储存空间
                        if("101".equals(config.getSku())){
                            saasTenant1.setBuySpaceNumTotal(config.getQty());
                        }
                        //员工人数
                        if("102".equals(config.getSku())){
                            saasTenant1.setExpectNumber(config.getQty());
                        }
                    }
                }else{
                    saasTenant1.setBasePackageType("专业版");
                }
            /*    String basePackageType = saasTenant1.getBasePackageType();
                for (ProductPackSku productPackSku : productPackSkuList) {
                    String sku = productPackSku.getSku();
                    String skuDesc = productPackSku.getSkuDesc();
                    if (basePackageType.equals(sku)) {
                        //替换版本信息
                        saasTenant1.setBasePackageType(skuDesc);
                        break;
                    }
                }*/
            }
        }

        if (saasTenantPageInfo.getList() != null && saasTenantPageInfo.getList().size() > 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),saasTenantPageInfo);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), saasTenantPageInfo, BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 菜单跳转到itenant页面
     */
    @RequestMapping("/gotoTenantPage")
    public String gotoTenantPage(String userId, Model model, String token) {
        return "console/tenant";
    }

    /**
     * 租户状态的修改
     * @return
     */
    @RequestMapping(value = "/updateIsInnerTest",method = RequestMethod.POST)
    @ResponseBody
    public Object updateTenantIsInnerTest(String tenantId){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        List<SaasRole> userRolesByUserId = saasRoleService.findUserRolesByUserId(userId);
        if(null == userRolesByUserId || userRolesByUserId.size() == 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        List list = new ArrayList();
        for (SaasRole sr:userRolesByUserId) {
            if(null != sr){
                String roleId = sr.getRoleId();
                if(StringUtils.isNotBlank(roleId)){
                    list.add(roleId);
                }
            }
        }
        boolean contains = list.contains("0");
        if(!contains){
           return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"权限不足");
        }
        if(StringUtils.isNotBlank(tenantId)){
            Map map = new HashMap<>();
            SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(tenantId);
            if(null != saasTenant){
                Integer status = saasTenant.getStatus();
                if(2==status){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"已解散的企业不可切换状态");
                }
                String isInnerTest = saasTenant.getIsInnerTest();//1:是  0：否
                SaasTenant st = new SaasTenant();
                if(StringUtils.isBlank(isInnerTest) || "0" .equals(isInnerTest) ){
                    st.setId(saasTenant.getId());
                    st.setIsInnerTest("1");
                }else if("1".equals(isInnerTest)){
                    st.setId(saasTenant.getId());
                    st.setIsInnerTest("0");
                }
                boolean b = saasTenantService.updateSaasTenantService(st);
                if(b){
                    RedisUtil.delRedisKeys(tenantId+"_*");
                    map.put("success", true);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数为空");
    }

    @Resource
    PlatformProgramVersionSerevice platformProgramVersionSerevice;

    /**
     * 租户是否定制状态的修改
     * @return
     */
    @RequestMapping(value = "/updateIsCustomizedTenant",method = RequestMethod.POST)
    @ResponseBody
    public Object updateIsCustomizedTenant(String tenantId){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        List<SaasRole> userRolesByUserId = saasRoleService.findUserRolesByUserId(userId);
        if(null == userRolesByUserId || userRolesByUserId.size() == 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        List list = new ArrayList();
        for (SaasRole sr:userRolesByUserId) {
            if(null != sr){
                String roleId = sr.getRoleId();
                if(StringUtils.isNotBlank(roleId)){
                    list.add(roleId);
                }
            }
        }
        boolean contains = list.contains("0");
        if(!contains){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"权限不足");
        }
        if(StringUtils.isNotBlank(tenantId)){
            Map map = new HashMap<>();
            SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(tenantId);
            if(null != saasTenant){
                Integer status = saasTenant.getStatus();
                if(2==status){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"已解散的企业不可切换状态");
                }
                //String isInnerTest = saasTenant.getIsInnerTest();//1:是  0：否
                String isCustomizedTenant = saasTenant.getIsCustomizedTenant();//是否定制租户 0：普通租户 1：定制租户
                SaasTenant st = new SaasTenant();
                if(StringUtils.isBlank(isCustomizedTenant) || "0" .equals(isCustomizedTenant) ){
                    st.setId(saasTenant.getId());
                   // st.setIsInnerTest("1");
                    st.setIsCustomizedTenant("1");
                }else if("1".equals(isCustomizedTenant)){
                    st.setId(saasTenant.getId());
                    st.setIsCustomizedTenant("0");
                }
                boolean b = saasTenantService.updateSaasTenantService(st);
                if(b){
                    List<PlatformProgramVersion> ppvs = platformProgramVersionSerevice.newSelectPlatformProgramVersion("1",tenantId);
                    if(null != ppvs && ppvs.size() > 0){
                        PlatformProgramVersion platformProgramVersion = ppvs.get(0);
                        if(null != platformProgramVersion){
                            PlatformProgramVersion platformProgramVersionNew = new PlatformProgramVersion();
                            platformProgramVersionNew.setId(platformProgramVersion.getId());
                            boolean b11 = platformProgramVersionSerevice.updatePlatformProgarmVersion(platformProgramVersionNew);
                            if(b11){
                                RedisUtil.delRedisKeys(tenantId+"_*");
                                RedisUtil.remove("isCustomizedTenantCache");
                                map.put("success", true);
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                            }
                        }
                    }else{
                     ///////////////////////////////////////////////////////
                        PlatformProgramVersion platformProgramVersion = new PlatformProgramVersion();
                        platformProgramVersion.setId(CreateUUIdUtil.Uuid());
                        platformProgramVersion.setVer(CreateUUIdUtil.Uuid());
                        platformProgramVersion.setCreatTime(new Date());
                        platformProgramVersion.setCustomTenantId(tenantId);
                        platformProgramVersion.setVersionUpdateBatch(CreateUUIdUtil.Uuid());
                        boolean bbb = platformProgramVersionSerevice.addPlatformProgramVersion(platformProgramVersion);
                        if(!bbb){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
                        }
                        RedisUtil.delRedisKeys(tenantId+"_*");
                        RedisUtil.remove("isCustomizedTenantCache");
                        map.put("success", true);
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                        //////////////////////////////////////////////////////
                    }
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"操作失败");
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数为空");
    }

    /**
     * 获取租户下的终端列表
     * @param tenantId 租户Id
     * @return
     */
    @RequestMapping("/getTerminalList")
    @ResponseBody
    public ResponseResult getTerminalList(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            TenantContext.setTenantId(tenantId);
            List<InfoReleaseTerminal> terminals = infoReleaseTerminalService.selectInfoReleaseTerminalByTenantId(tenantId);
            List<Map<String, Object>> list = new ArrayList<>();
            if (terminals != null && terminals.size() > 0) {
                for (InfoReleaseTerminal terminal : terminals) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("terminalId", terminal.getHardwareId());
                    map.put("terminalType", terminal.getTerminalTypeId());
                    map.put("registrationTime", terminal.getCreatTime());
                    list.add(map);
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), list);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数为空");
    }

    /**
     * 查看全部错误终端日志
     * @param word 搜索关键字
     * @param eventType 日志菜单类型
     * @return
     */
    @RequestMapping(value = "/getAllTerminalLog",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult getAllTerminalLog(BasePageEntity page,String word,String eventType){
        //日志类型
        List<SysLogType> typeList1 = new ArrayList<>();
        SysLogType log1 = new SysLogType();
        log1.setId("101");
        typeList1.add(this.sysLogTypeService.selectSysLogTypeList(log1).get(0));
        log1.setId("102");
        typeList1.add(this.sysLogTypeService.selectSysLogTypeList(log1).get(0));
        log1.setId("103");
        typeList1.add(this.sysLogTypeService.selectSysLogTypeList(log1).get(0));
        log1.setId("104");
        typeList1.add(this.sysLogTypeService.selectSysLogTypeList(log1).get(0));
        log1.setId("105");
        typeList1.add(this.sysLogTypeService.selectSysLogTypeList(log1).get(0));
        //根据一级菜单 获取二级菜单
        if (typeList1 != null && typeList1.size() > 0) {
            List<SysLogType> typeList12 = new ArrayList<>();
            for (SysLogType sysLogType : typeList1) {
                SysLogType sysLogType1 = new SysLogType();
                sysLogType1.setParentId(sysLogType.getId());
//                sysLogType1.setStatus(0);
                List<SysLogType> sysLogTypeList2 = this.sysLogTypeService.selectSysLogTypeList(sysLogType1);
                typeList12.addAll(sysLogTypeList2);
            }
            typeList1.addAll(typeList12);
        }
        PageInfo logs = terminalLogService.getAllTerminalLog(null,word, eventType,page);
        Map<String,Object> map = new HashMap<>();
        map.put("types", typeList1);
        if(logs!=null) {
            map.put("logs", logs.getList());
            map.put("pages", logs.getPages());
            map.put("pageNum", logs.getPageNum());
//            map.put("pageSize", logs.getPageSize());
            map.put("total", logs.getTotal());
        }else{
            map.put("logs", null);
            map.put("pages", 0);
            map.put("pageNum", null);
//            map.put("pageSize", 0);
            map.put("total", 0);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }

    /**
     * 查看某个租户错误终端日志
     * @param tenantId 租户Id
     * @return
     */
    @RequestMapping(value = "/getTenantTerminalLog",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult getTenantTerminalLog(BasePageEntity page,String tenantId,String word){
        if(StringUtils.isNotBlank(tenantId)) {
            PageInfo logs = terminalLogService.getAllTerminalLog(tenantId, word, null,page);
            Map<String,Object> map = new HashMap<>();
            SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(tenantId);
            String tenantName = "";
            if(saasTenant!=null){
                tenantName =saasTenant.getTenantName();
                map.put("tenantName",tenantName);
            }
            if(logs!=null) {
                map.put("logs", logs.getList());
                map.put("pages", logs.getPages());
                map.put("pageNum", logs.getPageNum());
                map.put("total", logs.getTotal());
            }else{
                map.put("logs", null);
                map.put("pages", 0);
                map.put("pageNum", null);
                map.put("total", 0);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数为空");
    }

    /**
     * 查看日志详细
     * @param logId 日志Id
     * @return
     */
    @RequestMapping(value = "/getTerminalLogDetail",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult getTerminalLogDetail(String logId){
        if(StringUtils.isNotBlank(logId)){
            String result = terminalLogService.getTerminalLogResult(logId);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), result);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"必填参数为空");
    }
}
