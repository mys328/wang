package com.thinkwin.publish.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.publish.PlatformClientVersionUpgradeRecorder;
import com.thinkwin.common.model.publish.PlatformInfoClientVersionLib;
import com.thinkwin.publish.mapper.PlatformClientVersionUpgradeRecorderMapper;
import com.thinkwin.publish.service.PlatformClientVersionUpgradeRecorderService;
import com.thinkwin.publish.service.PlatformInfoClientVersionLibService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：
 * @author lining 2018/5/23
 * @version 1.0
 *
 */
@Service("platformClientVersionUpgradeRecorderService")
public class PlatformClientVersionUpgradeRecorderServiceImpl implements PlatformClientVersionUpgradeRecorderService {

    @Autowired
    PlatformClientVersionUpgradeRecorderMapper platformClientVersionUpgradeRecorderMapper;
    @Resource
    public PlatformInfoClientVersionLibService platformInfoClientVersionLibService;
    @Resource
    private com.thinkwin.core.service.SaasTenantService saasTenantCoreService;

    /**
     * 1表示内测用户
     */
    private final String InnerTest="1";
    /**
     * 添加终端升级日志
     *
     * @param upgradeRecorder 日志
     * @return
     */
    @Override
    public boolean add(PlatformClientVersionUpgradeRecorder upgradeRecorder) {
       int f=0;
//        //1.根据租户ID获取租户信息
//        SaasTenant saasTenant=saasTenantCoreService.selectSaasTenantServcie(upgradeRecorder.getTenantId());
//        //当前租户类型2正式，1内测
//        String tenantType="2";
//        if(null!=saasTenant.getIsInnerTest() && saasTenant.getIsInnerTest().equals(InnerTest)){
//            tenantType="1";
//        }
       if(null!=upgradeRecorder.getCurrentVer()){
           PlatformInfoClientVersionLib lib=this.platformInfoClientVersionLibService.findByVerNum(upgradeRecorder.getCurrentVer(),null);
          if(null!=lib) {
              upgradeRecorder.setClientVersionId(lib.getId());
              f = this.platformClientVersionUpgradeRecorderMapper.insert(upgradeRecorder);
          }
       }
       return (f>0)?true:false;
    }

    /**
     * 查询终端版本升级日志
     * status 1升级成功0失败
     * @param clientVerId 终端版本id
     * @param searchKey  租户名称
     * @return
     */
    @Override
    public Map findByClientVersionIdAndTenantName(String clientVerId, String searchKey, BasePageEntity basePageEntity) {
        Map<String,Object> map=new HashMap<>();

        Map<String,String>  paramMap=new HashMap();
        if(StringUtils.isNotBlank(clientVerId)){
            paramMap.put("clientVersionId",clientVerId);
        }
        if(StringUtils.isNotBlank(searchKey)){
            paramMap.put("searchKey",searchKey);
        }
            paramMap.put("status","1");


        //分页显示终端列表
        PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
        List<PlatformClientVersionUpgradeRecorder> list=this.platformClientVersionUpgradeRecorderMapper.findByClientVersionIdAndTenantName(paramMap);
        PageInfo pageInfo = new PageInfo<>(list);
        map.put("verTerminalList",pageInfo);
        return map;
    }


     /**
     * 根据租户主键id删除终端版本升级日志
     * @param tenantId
     * @return
     */
     public boolean delPlatformClientVersionUpgradeRecorderByTenantId(String tenantId){
         if(StringUtils.isNotBlank(tenantId)){
             Example example = new Example(PlatformClientVersionUpgradeRecorder.class);
             example.createCriteria().andEqualTo("tenantId",tenantId);
             int i = platformClientVersionUpgradeRecorderMapper.deleteByExample(example);
             if(i>=0){
                 return true;
             }
         }
         return false;
     }
    private static Logger logger = LoggerFactory.getLogger(PlatformClientVersionUpgradeRecorderServiceImpl.class);
    @Override
    public boolean findPlCliVerUpgradeRecorder(String tenantId, String clientVersionId, String hardwareId) {
        if(null!=clientVersionId) {
            /*//当前租户类型2正式，1内测
            String tenantType="2";
            if(innerTest.equals("1")){
                tenantType="1";
            }*/
           /* logger.info("用户类型======================"+tenantType);
            PlatformInfoClientVersionLib lib = this.platformInfoClientVersionLibService.findByVerNum(clientVersionId, tenantType);
            logger.info("查询PlatformInfoClientVersionLib的条件======================clientVersionId="+clientVersionId +"**tenantType="+ tenantType);
            if (null != lib) {*/
                //clientVersionId = lib.getId();
                Example example = new Example(PlatformClientVersionUpgradeRecorder.class);
                example.createCriteria().andEqualTo("tenantId",tenantId)
                        //.andEqualTo("clientVersionId",clientVersionId)
                        .andEqualTo("hardwareId",hardwareId);
                logger.info("查询platformClientVersionUpgradeRecorders的条件======================tenantId="+tenantId +"**clientVersionId="+ clientVersionId+"**hardwareId="+hardwareId);
                List<PlatformClientVersionUpgradeRecorder> platformClientVersionUpgradeRecorders = platformClientVersionUpgradeRecorderMapper.selectByExample(example);
                if(platformClientVersionUpgradeRecorders.size()>0){
                    return true;
                }
            //}
        }
        return false;
    }
}
