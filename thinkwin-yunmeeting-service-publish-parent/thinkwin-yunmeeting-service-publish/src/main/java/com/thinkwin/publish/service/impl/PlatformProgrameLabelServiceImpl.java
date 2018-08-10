package com.thinkwin.publish.service.impl;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.model.publish.*;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.publish.mapper.*;
import com.thinkwin.publish.service.PlatformProgramComponentsService;
import com.thinkwin.publish.service.PlatformProgramVersionSerevice;
import com.thinkwin.publish.service.PlatformProgrameLabelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 标签部分接口实现类层
 * User: yinchunlei
 * Date: 2018/4/23
 * Company: thinkwin
 */
@Service("platformProgrameLabelService")
public class PlatformProgrameLabelServiceImpl implements PlatformProgrameLabelService {

    @Autowired
    PlatformProgrameLabelMapper platformProgrameLabelMapper;
    @Autowired
    PlatformLabelProgramMiddleMapper platformLabelProgramMiddleMapper;
    @Autowired
    PlatformProgramMapper platformProgramMapper;

    @Autowired
    SaasTenantService saasTenantServcie;
    @Autowired
    PlatformProgramVersionSerevice platformProgramVersionSerevice;
    /**
     * 创建新的标签功能
     * @param platformProgrameLabel
     * @return
     */
    public String createdPlatformProgrameLabel(PlatformProgrameLabel platformProgrameLabel){
        if (null != platformProgrameLabel){
            String labelName = platformProgrameLabel.getLabelName();
            if(StringUtils.isNotBlank(labelName)){
                String labelId = platformProgrameLabel.getId();
                if(StringUtils.isBlank(labelId)){
                    labelId = CreateUUIdUtil.Uuid();
                    platformProgrameLabel.setId(labelId);
                }
                Integer sort = getLabelSort();
                if(null != sort && sort >0){
                    platformProgrameLabel.setLabelSort(sort);
                }
                platformProgrameLabel.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                platformProgrameLabel.setLabelStatus("2");
                platformProgrameLabel.setRecorderStatus("1");
                platformProgrameLabel.setCreatTime(new Date());
                platformProgrameLabel.setVer(CreateUUIdUtil.Uuid());
                int i = platformProgrameLabelMapper.insertSelective(platformProgrameLabel);
                if(i > 0){
                    //该处需要考虑redis中缓存的数据;标签在redis中缓存的数据key为：platformProgrameLabel_1，草稿；
                    // platformProgrameLabel_2，内测；platformProgrameLabel_3，正式；
                    //删除redis中缓存的key值
                    //labelVariableMaintenance();
                    platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("1");//1:只修改批次号 2：只修改ver字段
                    return labelId;
                }
            }
            return null;
        }
        return null;
    }

    /**
     * 获取新添加标签的排序号
     * @return
     */
    public Integer getLabelSort(){
        List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelMapper.selectAll();
        List list = new ArrayList();
        if(null != platformProgrameLabels && platformProgrameLabels.size() > 0){
            for (PlatformProgrameLabel platformProgrameLabel:platformProgrameLabels) {
                if(null != platformProgrameLabel){
                    list.add(platformProgrameLabel.getLabelSort());
                }
            }
        }
        if(null != list && list.size() > 0){
            return (int)Collections.max(list)+1;
        }else{
            return 1;
        }
    }

    /**
     * 删除标签功能接口
     * @return
     */
    public boolean delPlatformProgrameLabel (List<String> platformProgrameLabelIds){
        if(null != platformProgrameLabelIds && platformProgrameLabelIds.size() > 0) {
            for (String platformProgrameLabelId:platformProgrameLabelIds) {
                if (StringUtils.isNotBlank(platformProgrameLabelId)) {
                    PlatformProgrameLabel platformProgrameLabel = platformProgrameLabelMapper.selectByPrimaryKey(platformProgrameLabelId);
                    if (null != platformProgrameLabel) {
                        int i = platformProgrameLabelMapper.deleteByPrimaryKey(platformProgrameLabelId);
                        if (i > 0) {
                            Integer labelSort = platformProgrameLabel.getLabelSort();
                            if (null != labelSort && labelSort > 0) {
                                boolean b = labelDelSort(labelSort);
                                if (b) {
                                    //标签删除成功后，执行接触标签和相关节目的关联状态
                                    boolean b1 = delBindingRelationshipByLabelId(platformProgrameLabelId);
                                    if (!b1) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //删除redis中缓存的key值
            //labelVariableMaintenance();
            platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("1");//1:只修改批次号 2：只修改ver字段
            return true;
        }
        return false;
    }

    /**
     * 删除标签时的排序功能
     * @return
     */
    public boolean labelDelSort(Integer labelNum){
        if(null != labelNum) {
            Example example = new Example(PlatformProgrameLabel.class);
            example.createCriteria().andGreaterThan("labelSort", labelNum);
            List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelMapper.selectByExample(example);
            if (null != platformProgrameLabels && platformProgrameLabels.size() > 0) {
                for (PlatformProgrameLabel platformProgrameLabel : platformProgrameLabels) {
                    if (null != platformProgrameLabel) {
                        Integer labelSort = platformProgrameLabel.getLabelSort();
                        if (null != labelSort && labelSort > 1) {
                            PlatformProgrameLabel platformProgrameLabel1 = new PlatformProgrameLabel();
                            platformProgrameLabel1.setId(platformProgrameLabel.getId());
                            platformProgrameLabel1.setLabelSort(labelSort - 1);
                            platformProgrameLabel1.setModifyTime(new Date());
                            platformProgrameLabelMapper.updateByPrimaryKeySelective(platformProgrameLabel1);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 根据标签id删除相对应的标签关系表
     * @param platformProgrameLabelId
     * @return
     */
    public boolean delBindingRelationshipByLabelId(String platformProgrameLabelId){
        if(StringUtils.isNotBlank(platformProgrameLabelId)){
            Example example = new Example(PlatformLabelProgramMiddle.class);
            example.createCriteria().andEqualTo("programLabelId",platformProgrameLabelId);
            platformLabelProgramMiddleMapper.deleteByExample(example);
            return true;
        }
        return false;
    }

    /**
     * 修改标签功能接口
     * @return
     */
    public boolean updatePlatformProgrameLabel (PlatformProgrameLabel platformProgrameLabel){
        if(null != platformProgrameLabel){
                PlatformProgrameLabel platformProgrameLabel1 = new PlatformProgrameLabel();
                platformProgrameLabel1.setId(platformProgrameLabel.getId());
                String labelStatus = platformProgrameLabel.getLabelStatus();
                if(StringUtils.isNotBlank(labelStatus)){
                    platformProgrameLabel1.setLabelStatus(labelStatus);
                }else{
                    platformProgrameLabel1.setLabelStatus("2");
                }
                String labelName = platformProgrameLabel.getLabelName();
                if(StringUtils.isNotBlank(labelName)){
                    platformProgrameLabel1.setLabelName(labelName);
                }
                Integer labelSort = platformProgrameLabel.getLabelSort();
                if(null != labelSort){
                    platformProgrameLabel1.setLabelSort(labelSort);
                }
                platformProgrameLabel1.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                platformProgrameLabel1.setModifyTime(new Date());
                platformProgrameLabel1.setVer(CreateUUIdUtil.Uuid());
                int i = platformProgrameLabelMapper.updateByPrimaryKeySelective(platformProgrameLabel1);
                if (i >0){
                    //删除redis中缓存的key值
                    //labelVariableMaintenance();
                    platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("1");//1:只修改批次号 2：只修改ver字段
                    return true;
                }
            }
        return false;
    }

    /**
     * 查询标签功能接口
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabel (PlatformProgrameLabel platformProgrameLabel){
    if(null != platformProgrameLabel){
        Example example = new Example(PlatformProgrameLabel.class);
        Example.Criteria criteria = example.createCriteria();
        String platformProgrameLabelId = platformProgrameLabel.getId();
        if(StringUtils.isNotBlank(platformProgrameLabelId)){
            criteria.andEqualTo("id",platformProgrameLabelId);
        }
        String labelName = platformProgrameLabel.getLabelName();
        if(StringUtils.isNotBlank(labelName)){
            criteria.andEqualTo("labelName",labelName);
        }
        String labelStatus = platformProgrameLabel.getLabelStatus();
        if(StringUtils.isNotBlank(labelStatus)){
            criteria.andEqualTo("labelStatus",labelStatus);
        }
        return platformProgrameLabelMapper.selectByExample(example);
    }else {
        return platformProgrameLabelMapper.selectAll();
    }
    }

    /**
     * 根据主键id查询标签功能接口
     * @return
     */
    public PlatformProgrameLabel selectPlatformProgrameLabelByLabelId (String platformProgrameLabelId){
        if(StringUtils.isNotBlank(platformProgrameLabelId)){
            return platformProgrameLabelMapper.selectByPrimaryKey(platformProgrameLabelId);
        }
        return null;
    }
    /**
     * 标签变动时缓存数据的维护功能方法
     */
    public void labelVariableMaintenance(){
        //该处需要考虑redis中缓存的数据;标签在redis中缓存的数据key为：platformProgrameLabel_1，草稿；
        // platformProgrameLabel_2，内测；platformProgrameLabel_3，正式；
        //删除redis中缓存的key值
        List<String> keys = RedisUtil.keys("platform_ProgrameLabel_*");
        if(null != keys && keys.size() > 0){
            for (String key:keys) {
                if(StringUtils.isNotBlank(key)){
                    RedisUtil.remove(key);
                }
            }
        }
    }

    /**
     * 根据标签id获取有该标签的所有节目数量功能接口
     * @return
     */
    public Integer selectPlatformProgramNumByLabelId(String labelId){
        Integer i = 0;
        if(StringUtils.isNotBlank(labelId)){
            Example example = new Example(PlatformLabelProgramMiddle.class);
            example.createCriteria().andEqualTo("programLabelId",labelId);
            List<PlatformLabelProgramMiddle> platformLabelProgramMiddles = platformLabelProgramMiddleMapper.selectByExample(example);
            if(null != platformLabelProgramMiddles && platformLabelProgramMiddles.size() > 0){
                for (PlatformLabelProgramMiddle pbpm:platformLabelProgramMiddles) {
                    if(null != pbpm){
                        String programId = pbpm.getProgramId();
                        if(StringUtils.isNotBlank(programId)){
                            Example example1 = new Example(PlatformProgram.class);
                            example1.createCriteria().andEqualTo("id",programId).andEqualTo("recorderStatus",1);
                            List<PlatformProgram> platformProgram = platformProgramMapper.selectByExample(example1);
                            if(null != platformProgram && platformProgram.size() > 0){
                                i += 1;
                            }
                        }
                    }
                }
            }
        }
        return i;
    }
    @Autowired
    BizImageRecorderMapper bizImageRecorderMapper;
    @Autowired
    PlatformProgramComponentsService platformProgramComponentsService;

    /**
     * 根据租户id获取相关的节目、标签、节目标签关系列表功能接口
     * @return
     */
    public Map selectTenantDateByTenantId(String tenantId){
        Map map = new HashMap();
        if(StringUtils.isNotBlank(tenantId)) {
            //根据租户id获取租户类型后获取相对应的数据集
            SaasTenant saasTenant = saasTenantServcie.selectSaasTenantServcie(tenantId);
            if (null != saasTenant) {
                String isCustomizedTenant = saasTenant.getIsCustomizedTenant();//是否定制租户 0：普通租户 1：定制租户
                String isInnerTest = saasTenant.getIsInnerTest();//是否内测租户 1:是 0:否
                if (StringUtils.isBlank(isInnerTest) || "0".equals(isInnerTest)) {
                    List<PlatformProgrameLabel> platformProgrameLabels = new ArrayList<>();
                    String platform_info_platformProgrameLabels_1 = RedisUtil.get("platform_info_platformProgrameLabels_1");
                    if(StringUtils.isNotBlank(platform_info_platformProgrameLabels_1)){
                        platformProgrameLabels = JSON.parseArray(platform_info_platformProgrameLabels_1, PlatformProgrameLabel.class);
                    }else {
                        //正式租户看到的是正式版的数据内容
                        //1、获取正式租户下的所有的标签数据
                        Example example = new Example(PlatformProgrameLabel.class);
                        example.createCriteria().andEqualTo("labelStatus", 1);
                        platformProgrameLabels = platformProgrameLabelMapper.selectByExample(example);
                        if (null != platformProgrameLabels && platformProgrameLabels.size() > 0) {
                            String s = JSON.toJSONString(platformProgrameLabels);
                            //把字符串存redis里面
                            RedisUtil.set("platform_info_platformProgrameLabels_1", s);
                        }
                    }
                    map.put("platformProgrameLabels", platformProgrameLabels);
                    map.put("innerTestPlatformProgrameLabels", null);
                    List<PlatformProgram> platformPrograms = new ArrayList<>();
                    String platform_info_platformProgrames_1 = RedisUtil.get("platform_info_platformProgrames_1");
                    if(StringUtils.isNotBlank(platform_info_platformProgrames_1)){
                        platformPrograms = JSON.parseArray(platform_info_platformProgrames_1, PlatformProgram.class);
                    }else {
                        //2、获取正式租户下的所有的节目数据
                        Example example1 = new Example(PlatformProgram.class);
                        example1.createCriteria().andEqualTo("programStatus", 1).andEqualTo("recorderStatus", 1).andEqualTo("programType",0);
                        Example example3 = new Example(PlatformProgram.class);
                        Example.Criteria criteria = example3.createCriteria().andEqualTo("programStatus", 1).andIsNull("programType").andEqualTo("recorderStatus", 1);
                        example1.or(criteria);
                        platformPrograms = platformProgramMapper.selectByExample(example1);
                        if (null != platformPrograms && platformPrograms.size() > 0) {
                            String s1 = JSON.toJSONString(platformPrograms);
                            //把字符串存redis里面
                            RedisUtil.set("platform_info_platformProgrames_1", s1);
                        }
                    }
                    map.put("platformProgrames", platformPrograms);//正式节目集合
                    map.put("innerTestPlatformPrograme", null);//内测节目集合
                    //判断租户是否为定制租户
                    //String isCustomizedTenant = saasTenant.getIsCustomizedTenant();//是否定制租户 0：普通租户 1：定制租户
                    List<PlatformProgram> platformPrograms1 = new ArrayList<>();
                    if(StringUtils.isNotBlank(isCustomizedTenant) && "1".equals(isCustomizedTenant)){
                    List<String> customizedProgramIds = platformProgramTenantMiddleMapper.selectPlatformProgrameIdsByLabelType(tenantId);
                    Example example4 = new Example(PlatformProgram.class);
                    example4.createCriteria().andEqualTo("programStatus", 1).andEqualTo("recorderStatus", 1).andEqualTo("programType",1).andIn("id",customizedProgramIds);
                    platformPrograms1 = platformProgramMapper.selectByExample(example4);
                    map.put("customizedPlatformProgrames",platformPrograms1);//租户自己的正式定制节目集合
                    map.put("customizedInnerTestPlatformPrograme",null);//租户自己的内测定制节目集合
                    }else if(StringUtils.isBlank(isCustomizedTenant) || "0".equals(isCustomizedTenant)){
                        map.put("customizedPlatformProgrames",platformPrograms1);//租户自己的正式定制节目集合
                        map.put("customizedInnerTestPlatformPrograme",null);//租户自己的内测定制节目集合
                    }
                    //获取节目组件集合
                            List<String> platformProgramIds = new ArrayList<>();
                            if(null != platformPrograms && platformPrograms.size() > 0){
                                for (PlatformProgram platfp:platformPrograms) {
                                    if(null != platfp){
                                        String id = platfp.getId();
                                        if(StringUtils.isNotBlank(id)){
                                            platformProgramIds.add(id);
                                        }
                                    }
                                }
                            }
                    if(null != platformPrograms1 && platformPrograms1.size() > 0){
                        for (PlatformProgram platfp1:platformPrograms1) {
                            if(null != platfp1){
                                String id1 = platfp1.getId();
                                if(StringUtils.isNotBlank(id1)){
                                    platformProgramIds.add(id1);
                                }
                            }
                        }
                    }
                    List<PlatformProgramComponents> componentsList = new ArrayList();
                    List<PlatformProgramComponentsMiddle> componentsMiddleList = new ArrayList();
                    if(null != platformProgramIds && platformProgramIds.size() > 0) {
                        componentsMiddleList = platformProgramComponentsService.selectPlatformProgramComponentsMiddleByProgramIds(platformProgramIds);
                        if(null != componentsMiddleList && componentsMiddleList.size() > 0){
                            List<String> lllist = new ArrayList<>();
                            for (PlatformProgramComponentsMiddle ppcm:componentsMiddleList) {
                                if(null != ppcm){
                                    String componentsId = ppcm.getComponentsId();
                                    if(StringUtils.isNotBlank(componentsId)){
                                        lllist.add(componentsId);
                                    }
                                }
                            }
                            if(null != lllist && lllist.size() > 0){
                                componentsList = platformProgramComponentsService.selectPlatformProgramComponentsByProgramIds(lllist);
                            }
                        }
                    }
                    map.put("platformProgramComponents",componentsList);
                    map.put("platformProgramComponentsMiddle",componentsMiddleList);
                } else if ("1".equals(isInnerTest)) {
                    List<PlatformProgrameLabel> platformProgrameLabels = new ArrayList<>();
                    String platform_info_platformProgrameLabels_1 = RedisUtil.get("platform_info_platformProgrameLabels_1");
                    if(StringUtils.isNotBlank(platform_info_platformProgrameLabels_1)){
                        platformProgrameLabels = JSON.parseArray(platform_info_platformProgrameLabels_1, PlatformProgrameLabel.class);
                    }else {
                        //内测租户看到的是内测和正式两种状态的数据内容
                        //1、获取正式租户下的所有的标签数据    标签状态:草稿状态:0;发布状态:1;内测状态:2
                        Example example = new Example(PlatformProgrameLabel.class);
                        example.createCriteria().andEqualTo("labelStatus", 1);
                        platformProgrameLabels = platformProgrameLabelMapper.selectByExample(example);
                        if (null != platformProgrameLabels && platformProgrameLabels.size() > 0) {
                            String s1 = JSON.toJSONString(platformProgrameLabels);
                            //把字符串存redis里面
                            RedisUtil.set("platform_info_platformProgrameLabels_1", s1);
                        }
                    }
                    map.put("platformProgrameLabels", platformProgrameLabels);
                    List<PlatformProgrameLabel> platformProgrameLabelss = new ArrayList<>();
                            String platform_info_platformProgrameLabels_2 = RedisUtil.get("platform_info_platformProgrameLabels_2");
                    if(StringUtils.isNotBlank(platform_info_platformProgrameLabels_2)){
                        platformProgrameLabelss = JSON.parseArray(platform_info_platformProgrameLabels_2, PlatformProgrameLabel.class);
                    }else {
                        Example examplee = new Example(PlatformProgrameLabel.class);
                        examplee.createCriteria().andEqualTo("labelStatus", 2);
                        platformProgrameLabelss = platformProgrameLabelMapper.selectByExample(examplee);
                        if (null != platformProgrameLabelss && platformProgrameLabelss.size() > 0) {
                            String s = JSON.toJSONString(platformProgrameLabelss);
                            //把字符串存redis里面
                            RedisUtil.set("platform_info_platformProgrameLabels_2", s);
                        }
                    }
                    map.put("innerTestPlatformProgrameLabels", platformProgrameLabelss);
                    //2、获取正式租户下的所有的节目数据  节目状态 草稿状态:0;发布状态:1;内测状态:2
                    List<PlatformProgram> platformPrograms = new ArrayList<>();
                    String platform_info_platformProgrames_1 = RedisUtil.get("platform_info_platformProgrames_1");
                    if(StringUtils.isNotBlank(platform_info_platformProgrames_1)){
                        platformPrograms = JSON.parseArray(platform_info_platformProgrames_1, PlatformProgram.class);
                    }else {
                        Example example1 = new Example(PlatformProgram.class);
                        example1.createCriteria().andEqualTo("programStatus", 1).andEqualTo("recorderStatus", 1).andEqualTo("programType",0);
                        Example example3 = new Example(PlatformProgram.class);
                        Example.Criteria criteria = example3.createCriteria().andEqualTo("programStatus", 1).andIsNull("programType").andEqualTo("recorderStatus", 1);
                        example1.or(criteria);
                        platformPrograms = platformProgramMapper.selectByExample(example1);
                            if (null != platformPrograms && platformPrograms.size() > 0) {
                                String s1 = JSON.toJSONString(platformPrograms);
                                //把字符串存redis里面
                                RedisUtil.set("platform_info_platformProgrames_1", s1);
                            }
                    }
                    map.put("platformProgrames", platformPrograms);
                    List<PlatformProgram> platformPrograms1 = new ArrayList<>();
                    String platform_info_platformProgrames_2 = RedisUtil.get("platform_info_platformProgrames_2");
                    if(StringUtils.isNotBlank(platform_info_platformProgrames_2)){
                        platformPrograms1 = JSON.parseArray(platform_info_platformProgrames_2, PlatformProgram.class);
                    }else {
                        Example example11 = new Example(PlatformProgram.class);
                        example11.createCriteria().andEqualTo("programStatus", 2).andEqualTo("recorderStatus", 1).andEqualTo("programType",0);
                        Example example33 = new Example(PlatformProgram.class);
                        Example.Criteria criteria1 = example33.createCriteria().andEqualTo("programStatus", 2).andIsNull("programType").andEqualTo("recorderStatus", 1);
                        example11.or(criteria1);
                        platformPrograms1 = platformProgramMapper.selectByExample(example11);
                            if (null != platformPrograms1 && platformPrograms1.size() > 0) {
                                String s1 = JSON.toJSONString(platformPrograms1);
                                //把字符串存redis里面
                                RedisUtil.set("platform_info_platformProgrames_2", s1);
                            }
                    }
                    map.put("innerTestPlatformPrograme", platformPrograms1);
                    List<PlatformProgram> platformPrograms11 = new ArrayList<>();
                    //String isCustomizedTenant = saasTenant.getIsCustomizedTenant();
                    if(StringUtils.isNotBlank(isCustomizedTenant) && "1".equals(isCustomizedTenant)) {
                        List<String> customizedProgramIds = platformProgramTenantMiddleMapper.selectPlatformProgrameIdsByLabelType(tenantId);
                        Example example4 = new Example(PlatformProgram.class);
                        example4.createCriteria().andEqualTo("programStatus", 1).andEqualTo("recorderStatus", 1).andEqualTo("programType", 1).andIn("id", customizedProgramIds);
                        platformPrograms11 = platformProgramMapper.selectByExample(example4);
                    }
                    map.put("customizedPlatformProgrames",platformPrograms11);

                    Example example5 = new Example(PlatformProgram.class);
                    example5.createCriteria().andEqualTo("programStatus", 2).andEqualTo("recorderStatus", 1).andEqualTo("programType",1);
                    List<PlatformProgram> customizedInnerTestPlatformPrograme = platformProgramMapper.selectByExample(example5);
                    map.put("customizedInnerTestPlatformPrograme",customizedInnerTestPlatformPrograme);
                    //获取节目组件集合
                    List<String> platformProgramIds3 = new ArrayList<>();
                    if(null != platformPrograms && platformPrograms.size() > 0){
                        for (PlatformProgram platfp:platformPrograms) {
                            if(null != platfp){
                                String id = platfp.getId();
                                if(StringUtils.isNotBlank(id)){
                                    platformProgramIds3.add(id);
                                }
                            }
                        }
                    }
                    if(null != platformPrograms1 && platformPrograms1.size() > 0){
                        for (PlatformProgram platfp1:platformPrograms1) {
                            if(null != platfp1){
                                String id1 = platfp1.getId();
                                if(StringUtils.isNotBlank(id1)){
                                    platformProgramIds3.add(id1);
                                }
                            }
                        }
                    }
                    if(null != platformPrograms11 && platformPrograms11.size() > 0){
                        for (PlatformProgram platfp11:platformPrograms11) {
                            if(null != platfp11){
                                String id11 = platfp11.getId();
                                if(StringUtils.isNotBlank(id11)){
                                    platformProgramIds3.add(id11);
                                }
                            }
                        }
                    }
                    if(null != customizedInnerTestPlatformPrograme && customizedInnerTestPlatformPrograme.size() > 0){
                        for (PlatformProgram platfp111:customizedInnerTestPlatformPrograme) {
                            if(null != platfp111){
                                String id111 = platfp111.getId();
                                if(StringUtils.isNotBlank(id111)){
                                    platformProgramIds3.add(id111);
                                }
                            }
                        }
                    }
                    List<PlatformProgramComponents> componentsList = new ArrayList();
                    List<PlatformProgramComponentsMiddle> componentsMiddleList = new ArrayList();
                    if(null != platformProgramIds3 && platformProgramIds3.size() > 0) {
                        componentsMiddleList = platformProgramComponentsService.selectPlatformProgramComponentsMiddleByProgramIds(platformProgramIds3);
                        if(null != componentsMiddleList && componentsMiddleList.size() > 0){
                            List<String> lllist = new ArrayList<>();
                            for (PlatformProgramComponentsMiddle ppcm:componentsMiddleList) {
                                if(null != ppcm){
                                    String componentsId = ppcm.getComponentsId();
                                    if(StringUtils.isNotBlank(componentsId)){
                                        lllist.add(componentsId);
                                    }
                                }
                            }
                            if(null != lllist && lllist.size() > 0){
                                componentsList = platformProgramComponentsService.selectPlatformProgramComponentsByProgramIds(lllist);
                            }
                        }
                    }
                    map.put("platformProgramComponents",componentsList);
                    map.put("platformProgramComponentsMiddle",componentsMiddleList);
                }
                //3、获取所有的节目标签关联关系表数据
                List<PlatformLabelProgramMiddle> platformLabelProgramMiddles  = new ArrayList<>();
                String platform_info_platformLabelProgramMiddles = RedisUtil.get("platform_info_platformLabelProgramMiddles");
                if(StringUtils.isNotBlank(platform_info_platformLabelProgramMiddles)){
                    platformLabelProgramMiddles = JSON.parseArray(platform_info_platformLabelProgramMiddles, PlatformLabelProgramMiddle.class);
                }else {
                    platformLabelProgramMiddles = platformLabelProgramMiddleMapper.selectAll();
                    if (null != platformLabelProgramMiddles && platformLabelProgramMiddles.size() > 0) {
                        String s1 = JSON.toJSONString(platformLabelProgramMiddles);
                        //把字符串存redis里面
                        RedisUtil.set("platform_info_platformLabelProgramMiddles", s1);
                    }
                }
                map.put("platformLabelProgramMiddles", platformLabelProgramMiddles);
                List<BizImageRecorder> bizImageRecorders  = new ArrayList<>();
                String platform_info_bizImageRecorders = RedisUtil.get("platform_info_bizImageRecorders");
                if(StringUtils.isNotBlank(platform_info_bizImageRecorders)){
                    bizImageRecorders = JSON.parseArray(platform_info_bizImageRecorders, BizImageRecorder.class);
                }else {
                    bizImageRecorders = bizImageRecorderMapper.selectAll();
                    if (null != bizImageRecorders && bizImageRecorders.size() > 0) {
                        String s1 = JSON.toJSONString(bizImageRecorders);
                        //把字符串存redis里面
                        RedisUtil.set("platform_info_bizImageRecorders", s1);
                    }
                }
                map.put("bizImageRecorders", bizImageRecorders);
            }
        }
        return map;
    }

    /**
     * 获取所有的标签
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabels(){
        Example example = new Example(PlatformProgrameLabel.class);
        example.orderBy("labelSort");
        return platformProgrameLabelMapper.selectByExample(example);
    }


    /**
     * 获取所有的标签
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabelsNew(String platformProgrameLabelId){
        Example example = new Example(PlatformProgrameLabel.class);
        if(StringUtils.isBlank(platformProgrameLabelId) || "0".equals(platformProgrameLabelId)) {
            example.orderBy("labelSort");
        }else{
            example.createCriteria().andEqualTo("id",platformProgrameLabelId);
            example.orderBy("labelSort");
        }
        return platformProgrameLabelMapper.selectByExample(example);
    }

    /**
     * 标签排序功能
     * @param platformProgrameLabelId
     * @param sortPlatformProgrameLabelId
     * @param sortType 被移动的标签排序类型 1 参考标签的上方 2 参考标签的下方 3移动到参考标签的目录下
     * @return
     */
    public boolean platformProgrameLabelSort(String platformProgrameLabelId,String sortPlatformProgrameLabelId,String sortType){
        if(StringUtils.isNotBlank(sortType) && "1".equals(sortType)){
           return platformProgrameLabelMoveBefore(platformProgrameLabelId,sortPlatformProgrameLabelId);
        }else if(StringUtils.isNotBlank(sortType) && "2".equals(sortType)){
            return platformProgrameLabelMoveAfter(platformProgrameLabelId,sortPlatformProgrameLabelId);
        }else{
            //此处可以为以后有层级移动的处理做准备
        }
        return false;
    }

    /**
     * 标签向上移动功能
     * @param platformProgrameLabelId
     * @param sortPlatformProgrameLabelId
     * @return
     */
    public boolean platformProgrameLabelMoveBefore(String platformProgrameLabelId,String sortPlatformProgrameLabelId){
        PlatformProgrameLabel platformProgrameLabel = selectPlatformProgrameLabelByLabelId(platformProgrameLabelId);
        PlatformProgrameLabel platformProgrameLabell = selectPlatformProgrameLabelByLabelId(sortPlatformProgrameLabelId);
        if(null != platformProgrameLabel && null !=platformProgrameLabell){
            Integer labelSort = platformProgrameLabel.getLabelSort();
            Integer labelSort2 = platformProgrameLabell.getLabelSort();
            if(null != labelSort && null != labelSort2) {
               Example example = new Example(PlatformProgrameLabel.class);
               example.createCriteria().andGreaterThanOrEqualTo("labelSort", labelSort).andLessThan("labelSort",labelSort2);
                List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelMapper.selectByExample(example);
                if(null != platformProgrameLabels && platformProgrameLabels.size() > 0){
                    for (PlatformProgrameLabel pfpl:platformProgrameLabels) {
                            PlatformProgrameLabel pfpl1 = new PlatformProgrameLabel();
                            pfpl1.setId(pfpl.getId());
                            pfpl1.setLabelSort(pfpl.getLabelSort() + 1);
                            pfpl1.setModifyTime(new Date());
                            pfpl1.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                            int i = platformProgrameLabelMapper.updateByPrimaryKeySelective(pfpl1);
                            if (i <= 0) {
                                return false;
                            }
                    }
                PlatformProgrameLabel ppl = new PlatformProgrameLabel();
                ppl.setId(sortPlatformProgrameLabelId);
                ppl.setLabelSort(labelSort);
                ppl.setModifyTime(new Date());
                ppl.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                int i = platformProgrameLabelMapper.updateByPrimaryKeySelective(ppl);
                if(i > 0) {
                    platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("1");
                    return true;
                }
                }
            }
        }
        return false;
    }


    /**
     * 标签向下移动功能
     * @return
     */
    public boolean platformProgrameLabelMoveAfter(String platformProgrameLabelId,String sortPlatformProgrameLabelId){
        PlatformProgrameLabel platformProgrameLabel = selectPlatformProgrameLabelByLabelId(platformProgrameLabelId);
        PlatformProgrameLabel platformProgrameLabell = selectPlatformProgrameLabelByLabelId(sortPlatformProgrameLabelId);
        if(null != platformProgrameLabel && null !=platformProgrameLabell){
            Integer labelSort = platformProgrameLabel.getLabelSort();//参考id
            Integer labelSort2 = platformProgrameLabell.getLabelSort();//移动id
            if(null != labelSort && null != labelSort2) {
                Example example = new Example(PlatformProgrameLabel.class);
                example.createCriteria().andLessThanOrEqualTo("labelSort", labelSort).andGreaterThan("labelSort",labelSort2);
                List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelMapper.selectByExample(example);
                if(null != platformProgrameLabels && platformProgrameLabels.size() > 0){
                    for (PlatformProgrameLabel pfpl:platformProgrameLabels) {
                        PlatformProgrameLabel pfpl1 = new PlatformProgrameLabel();
                        pfpl1.setId(pfpl.getId());
                        pfpl1.setLabelSort(pfpl.getLabelSort() - 1);
                        pfpl1.setModifyTime(new Date());
                        pfpl1.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                        int i = platformProgrameLabelMapper.updateByPrimaryKeySelective(pfpl1);
                        if (i <= 0) {
                            return false;
                        }
                    }
                    PlatformProgrameLabel ppl = new PlatformProgrameLabel();
                    ppl.setId(sortPlatformProgrameLabelId);
                    ppl.setLabelSort(labelSort);
                    ppl.setModifyTime(new Date());
                    ppl.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                    int i = platformProgrameLabelMapper.updateByPrimaryKeySelective(ppl);
                    if(i > 0) {
                        platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("1");
                        return true;
                    }
                }
            }
        }
        return false;
        }




    /**
     * 根据标签id获取所有与该标签关联的节目id集合
     * @param platformProgrameLabelId
     * @return
     */
    public List<String> selectPlatformProgrameIdsByLabelId(String platformProgrameLabelId){
        if(StringUtils.isNotBlank(platformProgrameLabelId)){
           List list = platformLabelProgramMiddleMapper.selectPlatformProgrameIdsByLabelId(platformProgrameLabelId);
           return list;
        }
        return null;
    }

@Autowired
    PlatformProgramTenantMiddleMapper platformProgramTenantMiddleMapper;
    /**
     * 根据标签id获取所有与该标签关联的节目id集合
     * @param platformProgrameLabeId
     * @return
     */
    public List<String> selectPlatformProgrameIdsByLabelType(String platformProgrameLabeId){
        if(StringUtils.isNotBlank(platformProgrameLabeId)){
            List list = platformProgramTenantMiddleMapper.selectPlatformProgrameIdsByLabelType(platformProgrameLabeId);
            return list;
        }
        return null;
    }




    /**
     * 修改标签的状态
     * @param map
     * @return
     */
    public boolean updateLabelStatusByLabelStatus(Map map){
        int i = platformProgrameLabelMapper.updateLabelStatusByLabelStatus(map);
        if(i>=0){
            return true;
        }
        return false;
    }


    /**
     * 根据条件查询标签列表功能
     * @param example
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabelByExample(Example example){
        return platformProgrameLabelMapper.selectByExample(example);
    }

    /**
     * 根据标签主键id选择性修改标签内容
     * @param platformProgrameLabel
     * @return
     */
    public int updateByPrimaryKeySelective(PlatformProgrameLabel platformProgrameLabel){
        return platformProgrameLabelMapper.updateByPrimaryKeySelective(platformProgrameLabel);
    }












}
