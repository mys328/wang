package com.thinkwin.web.controller;

import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.ordersVo.OrdersPersonVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * 类名: OrderAuthController </br>
 * 描述: 订单授权升级页面</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/22 </br>
 */
@Controller
@RequestMapping(value = "/orderauth", method = RequestMethod.POST)
public class OrderAuthController {

    @Resource
    UserService userService;
    @Resource
    SaasTenantService saasTenantCoreService;
    @Resource
    YuncmMeetingService yuncmMeetingService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    PricingConfigService pricingConfigService;

    /**
     * 方法名：personStatistics</br>
     * 描述：人员统计</br>
     * 参数：</br>
     * 返回值：</br>
     */
    @RequestMapping("/personstatistics")
    @ResponseBody
    public Object personStatistics() {
        //获取租户Id
        String tenantId = TenantContext.getTenantId();
        //获取用户总人数
        List<SysUser> sysUsers = userService.selectNotDimissionPerson();
        if (null != sysUsers && sysUsers.size() > 0) {
            int active = 0, notActive = 0;
            OrdersPersonVo ordersPersonVo = new OrdersPersonVo();
            for (SysUser sysUser : sysUsers) {
                if (sysUser.getStatus()==1) {
                    active++;
                } else {
                    notActive++;
                }
            }
            ordersPersonVo.setActivePersonNum(active);
            ordersPersonVo.setNotActivePersonNum(notActive);
            //查询人员上限
            SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
            if(null!=saasTenant){
                //免费用户读取定价配置
                if("0".equals(saasTenant.getTenantType())){
                    //获取定价配置
                    PricingConfigDto configDto = pricingConfigService.getPricingConfig();
                    //获取免费价格
                    List<CapacityConfig> configs = configDto.getFreeAccountConfig();
                    for(CapacityConfig config : configs){
                        //会议室数量
                        if("100".equals(config.getSku())){
                            saasTenant.setBuyRoomNumTotal(config.getQty());
                            saasTenant.setBasePackageRoomNum(0);
                        }
                        //储存空间
                        if("101".equals(config.getSku())){
                            saasTenant.setBuySpaceNumTotal(config.getQty());
                        }
                        //员工人数
                        if("102".equals(config.getSku())){
                            saasTenant.setExpectNumber(config.getQty());
                        }
                    }
                }
                Integer expectNumber = saasTenant.getExpectNumber();
                if(expectNumber>0){
                    ordersPersonVo.setTotalPersonNum(expectNumber);
                    ordersPersonVo.setResiduePersonNum(expectNumber-(active+notActive));
                }
                ordersPersonVo.setVersion(saasTenant.getTenantType());
                // String basePackageType = saasTenant.getTenantType();
                /*if(basePackageType.equals("1000")){
                    ordersPersonVo.setVersion("1");
                }else if(basePackageType.equals("2000")||basePackageType.equals("2001")||basePackageType.equals("2002")){
                    ordersPersonVo.setVersion("1");
                }else if(basePackageType.equals("2100")||basePackageType.equals("2101")||basePackageType.equals("2102")){
                    ordersPersonVo.setVersion("2");
                }else if(basePackageType.equals("2200")||basePackageType.equals("2201")||basePackageType.equals("2202")){
                    ordersPersonVo.setVersion("3");
                }*/
                if(null!=saasTenant.getBasePackageExpir()) {
                    ordersPersonVo.setValidity(DateUtils.addDays(DateUtils.truncate(saasTenant.getBasePackageExpir(), Calendar.DAY_OF_MONTH), -1).getTime());
                }else{
                    ordersPersonVo.setValidity((long)0);
                }
                ordersPersonVo = selectMeetingRoomAndSpace(ordersPersonVo,saasTenant);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), ordersPersonVo);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 方法名：selectMeetingRoomAndSpace</br>
     * 描述：查询会议室数量和空间</br>
     * 参数：ordersPersonVo 返回前端的统计Vo</br>
     * 返回值：</br>
     */
    public OrdersPersonVo selectMeetingRoomAndSpace(OrdersPersonVo ordersPersonVo,SaasTenant saasTenant) {
        Map<String,Object> map = new HashedMap();
        //获取租户Id
        String tenantId = TenantContext.getTenantId();
        //获取租户版本
        //全部会议室
        int meetingRoomAll = saasTenant.getBuyRoomNumTotal();// + saasTenant.getBasePackageRoomNum();
        //使用会议室
        int meetingRoomUse = this.yuncmMeetingService.selectTenantMeetingRoomCount().size();
        //剩余会议室
        int meetingRoomSurplus =  meetingRoomAll - meetingRoomUse;
        ordersPersonVo.setTotalRooms(meetingRoomAll +"");
        ordersPersonVo.setUsedRooms(meetingRoomUse +"");
        ordersPersonVo.setFreeRooms(meetingRoomSurplus +"");
        //总空间
        long baseSpace = 0;//saasTenant.getBasePackageSpaceNum() * 1024;
        Integer buySpaceNumTotal = saasTenant.getBuySpaceNumTotal();
        long buySpace = 0;
        if(null != buySpaceNumTotal){
            buySpace = buySpaceNumTotal * 1024;
        }
        long spaceAll = baseSpace + buySpace;
        //使用空间
        long spaceUse =Long.valueOf(this.fileUploadService.selectTenantuseSpace(tenantId)) / 1024;
        if(spaceUse <= 0){
            spaceUse = 1;
        }
        //剩余空间
        long spaceSurplus = spaceAll - spaceUse;
        ordersPersonVo.setTotalSpace(spaceAll +"");
        ordersPersonVo.setUsedSpace(spaceUse +"");
        ordersPersonVo.setFreeSpace(spaceSurplus +"");
        return ordersPersonVo;
    }

    /**
     * 方法名：selectVersionsInfo</br>
     * 描述：查询版本信息</br>
     * 参数：</br>
     * 返回值：</br>版本
     */
    @RequestMapping("/selectversionsinfo")
    @ResponseBody
    public Object selectVersionsInfo(){
        //获取tenantId
        String tenantId = TenantContext.getTenantId();
        //查询版本到期时间
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        if(null!=saasTenant){
            Date basePackageExpir = saasTenant.getBasePackageExpir();
            Map<String,Object> map = new HashMap<>();
            if(null!=basePackageExpir) {
                map.put("expirationTime", basePackageExpir.getTime());
            }else{
                map.put("expirationTime","");
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
    }

}
