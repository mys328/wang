package com.thinkwin.publish.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.model.publish.*;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.SaasTenantInfoVo;
import com.thinkwin.common.vo.consoleVo.LabelVo;
import com.thinkwin.common.vo.consoleVo.PlatformProgrameLabelVo;
import com.thinkwin.common.vo.consoleVo.PlatformProgrameVo;
import com.thinkwin.common.vo.programe.CustomizingTenantVo;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.publish.mapper.*;
import com.thinkwin.publish.service.PlatformProgramVersionSerevice;
import com.thinkwin.publish.service.PlatformProgrameService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.*;

/**
 * 节目部分的Service层实现类
 * User: yinchunlei
 * Date: 2018/4/23
 * Company: thinkwin
 */
@Service("platformProgrameService")
public class PlatformProgrameServiceImpl implements PlatformProgrameService{
    @Autowired
    PlatformLabelProgramMiddleMapper platformLabelProgramMiddleMapper;
    @Autowired
    PlatformProgramMapper platformProgramMapper;
    @Autowired
    PlatformProgrameLabelMapper platformProgrameLabelMapper;
    @Autowired
    PlatformProgramVersionSerevice platformProgramVersionSerevice;

    @Autowired
    SaasTenantService saasTenantService;

    @Autowired
    PlatformProgramTenantMiddleMapper platformProgramTenantMiddleMapper;

    private BizImageRecorderMapper bizImageRecorderMapper;

    public BizImageRecorderMapper getBizImageRecorderMapper() {
        return bizImageRecorderMapper;
    }

    public void setBizImageRecorderMapper(BizImageRecorderMapper bizImageRecorderMapper) {
        this.bizImageRecorderMapper = bizImageRecorderMapper;
    }

    /**
     * 给单个节目添加单个标签功能接口
     * @return
     */
    public boolean addPlatformLabelProgramMiddle (String platformProgrameId, String platformProgrameLabelId){
        if(StringUtils.isNotBlank(platformProgrameId) && StringUtils.isNotBlank(platformProgrameLabelId)){
            Example example = new Example(PlatformLabelProgramMiddle.class);
            example.createCriteria().andEqualTo("programLabelId",platformProgrameLabelId).andEqualTo("programId",platformProgrameId);
            List<PlatformLabelProgramMiddle> platformLabelProgramMiddles = platformLabelProgramMiddleMapper.selectByExample(example);
            if(null == platformLabelProgramMiddles || platformLabelProgramMiddles.size() <= 0) {
                PlatformLabelProgramMiddle platformLabelProgramMiddle = new PlatformLabelProgramMiddle();
                platformLabelProgramMiddle.setId(CreateUUIdUtil.Uuid());
                platformLabelProgramMiddle.setProgramLabelId(platformProgrameLabelId);
                platformLabelProgramMiddle.setProgramId(platformProgrameId);
                int i = platformLabelProgramMiddleMapper.insertSelective(platformLabelProgramMiddle);
                if (i < 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 节目批量添加标签功能接口
     * @return
     */
    public boolean programListAddLabel(List<String> platformProgramIds, String platformProgrameLabelId){
        if(StringUtils.isNotBlank(platformProgrameLabelId) && null != platformProgramIds && platformProgramIds.size() >0){
            for (String platformProgramId:platformProgramIds) {
                boolean b = addPlatformLabelProgramMiddle(platformProgramId, platformProgrameLabelId);
                if(!b){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 节目添加功能接口
     * @return
     */
    public boolean addPlatformProgram (PlatformProgram platformProgram){
        if(null != platformProgram){
            String programName = platformProgram.getProgramName();
            if(StringUtils.isNotBlank(programName)) {
                String pingYin = PingYinUtil.getPingYin(programName);
                if(StringUtils.isNotBlank(pingYin)){
                    platformProgram.setProgramNamePinyin(pingYin);
                }
                //String firstSpell = PingYinUtil.getFirstSpell(programName);
                String firstSpell = ChineseInital.getAllFirstLetter(programName);
                if(StringUtils.isNotBlank(firstSpell)){
                    platformProgram.setProgramNameJianpin(firstSpell);
                }
                String id = platformProgram.getId();
                if (StringUtils.isBlank(id)) {
                    id = CreateUUIdUtil.Uuid();
                }
                platformProgram.setId(id);
                platformProgram.setProgramUpdateBatch(CreateUUIdUtil.Uuid());
                platformProgram.setCreatTime(new Date());
                platformProgram.setVer(CreateUUIdUtil.Uuid());
                platformProgram.setProgramStatus("0");
                platformProgram.setRecorderStatus("1");
                int i = platformProgramMapper.insertSelective(platformProgram);
                if(i>0){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 节目删除功能接口
     * @return
     */
    public boolean delPlatformPrograms(List<String> platformProgramIds){
        if(null != platformProgramIds && platformProgramIds.size() > 0){
            List list = new ArrayList();
            List list1 = new ArrayList();
            for (String platformProgramId:platformProgramIds) {
                PlatformProgram platformProgram = platformProgramMapper.selectByPrimaryKey(platformProgramId);
                if(null != platformProgram){
                    String programStatus = platformProgram.getProgramStatus();
                    if(StringUtils.isNotBlank(programStatus) && !"0".equals(programStatus)){
                        list.add(platformProgramId);
                    }else{
                        list1.add(platformProgramId);
                    }
                }
            }
            Map map = new HashMap();
            int i = 1;
            if(null != list && list.size() > 0) {
                map.put("platformProgramIds", list);
                int i1 = platformProgramMapper.delPlatformPrograms(map);
                if(i1<=0){
                    i=0;
                }
            }
            if(null != list1 && list1.size() > 0){
                Example example = new Example(PlatformProgram.class);
                example.createCriteria().andIn("id",list1);
                int i2 = platformProgramMapper.deleteByExample(example);
                if(i2 <= 0){
                    i = 0;
                }
            }
            if(i > 0){
                Map mapp = new HashMap();
                mapp.put("platformProgramIds",platformProgramIds);
               int ii =  platformLabelProgramMiddleMapper.delPlatformLabelProgramMiddle(mapp);
                if(ii < 0){
                    return false;
                }
                if(null != list1 && list1.size() > 0) {
                    Example example1 = new Example(PlatformProgramTenantMiddle.class);
                    example1.createCriteria().andIn("programId", list1);
                    int i11 = platformProgramTenantMiddleMapper.deleteByExample(example1);
                    if (i11 < 0) {
                        return false;
                    }
                    Example example2 = new Example(PlatformProgramComponentsMiddle.class);
                    example2.createCriteria().andIn("programId", list1);
                    int i2 = platformProgramComponentsMiddleMapper.deleteByExample(example2);
                    if (i2 < 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    @Autowired
    PlatformProgramComponentsMiddleMapper platformProgramComponentsMiddleMapper;

    /**
     * 节目删除功能接口(添加删除解压路径)
     * @return
     */
    public boolean delPlatformPrograms(List<String> platformProgramIds,String path){
        if(null != platformProgramIds && platformProgramIds.size() > 0){
            List list = new ArrayList();//平台发版节目集合
            List list1 = new ArrayList();//平台未发版节目集合
            for (String platformProgramId:platformProgramIds) {
                PlatformProgram platformProgram = platformProgramMapper.selectByPrimaryKey(platformProgramId);
                if(null != platformProgram){
                    String programVersionNum = platformProgram.getProgramVersionNum();
                    //定制节目评审时熊总确定为只要没有发过版本的节目就是物理删除（2018/7/16）
                    if(StringUtils.isNotBlank(programVersionNum) && "1".equals(platformProgram.getProgramStatus())){
                        list.add(platformProgramId);
                    }else {
                        BizImageRecorder bir = getBizImageRecorderByBizId(platformProgramId);
                        if (null  == bir) {
                            return false;
                        }
                        String imageId = bir.getImageId();
                        if(StringUtils.isBlank(imageId)){
                            return false;
                        }
                        clearFiles(path+File.separator+imageId);
                        list1.add(platformProgramId);
                    }
                }
            }
            Map map = new HashMap();
            int i = 1;
            if(null != list && list.size() > 0) {
                map.put("platformProgramIds", list);
                int i1 = platformProgramMapper.delPlatformPrograms(map);
                if(i1<=0){
                    i=0;
                }
            }
            if(null != list1 && list1.size() > 0){
                Example example = new Example(PlatformProgram.class);
                example.createCriteria().andIn("id",list1);
                int i2 = platformProgramMapper.deleteByExample(example);
                if(i2 <= 0){
                    i = 0;
                }
                Example examplee = new Example(BizImageRecorder.class);
                examplee.createCriteria().andIn("bizId",list1);
                bizImageRecorderMapper.deleteByExample(examplee);
            }
            if(i > 0){
                Example example = new Example(PlatformLabelProgramMiddle.class);
                example.createCriteria().andIn("programId",platformProgramIds);
                int i1 = platformLabelProgramMiddleMapper.deleteByExample(example);
                if(i1 < 0){
                    return false;
                }
                if(null != list1 && list1.size() > 0) {
                    Example example1 = new Example(PlatformProgramTenantMiddle.class);
                    example1.createCriteria().andIn("programId", list1);
                    int i11 = platformProgramTenantMiddleMapper.deleteByExample(example1);
                    if (i11 < 0) {
                        return false;
                    }
                    Example example2 = new Example(PlatformProgramComponentsMiddle.class);
                    example2.createCriteria().andIn("programId", list1);
                    int i2 = platformProgramComponentsMiddleMapper.deleteByExample(example2);
                    if (i2 < 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
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
            if(null != files) {
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
        }
        file.delete();
    }

    /**
     * 节目修改功能接口
     * @return
     */
    public boolean updatePlatformProgram(PlatformProgram  platformProgram){
        if(null != platformProgram){
            String id = platformProgram.getId();
            if(StringUtils.isNotBlank(id)){
                PlatformProgram platformProgram1 = platformProgramMapper.selectByPrimaryKey(id);
                String programName = platformProgram.getProgramName();
                if(StringUtils.isNotBlank(programName)){
                    String pingYin = PingYinUtil.getPingYin(programName);
                    if(StringUtils.isNotBlank(pingYin)){
                        platformProgram.setProgramNamePinyin(pingYin);
                    }
                    String firstSpell = ChineseInital.getAllFirstLetter(programName);
                    if(StringUtils.isNotBlank(firstSpell)){
                        platformProgram.setProgramNameJianpin(firstSpell);
                    }
                }
                platformProgram.setProgramUpdateBatch(CreateUUIdUtil.Uuid());
                platformProgram.setVer(CreateUUIdUtil.Uuid());
                platformProgram.setModifyTime(new Date());
                int i = platformProgramMapper.updateByPrimaryKeySelective(platformProgram);
                if(i > 0){
                    if("2".equals(platformProgram1.getProgramStatus())) {
                        platformProgramVersionSerevice.updatePlatformProgramVersionVersionUpdateBatch("3");
                    }
                    return true;
                }
            }
        }
        return false;
    }
    @Autowired
    SaasUserService saasUserService;

    /**
     * 根据节目主键id查询节目详情的功能接口
     * @return
     */
    public PlatformProgrameVo selectPlatformProgramById(String platformProgramId){
        if(StringUtils.isNotBlank(platformProgramId)){
            Example example1e = new Example(PlatformProgram.class);
            example1e.createCriteria().andEqualTo("id",platformProgramId).andEqualTo("recorderStatus",1);
            List<PlatformProgram> platformPrograms = platformProgramMapper.selectByExample(example1e);
            if(null != platformPrograms && platformPrograms.size() > 0) {
                PlatformProgram platformProgram = platformPrograms.get(0);
                if (null != platformProgram) {
                    PlatformProgrameVo ppvo = new PlatformProgrameVo();
                    BeanUtils.copyProperties(platformProgram, ppvo);
                    Example example = new Example(PlatformLabelProgramMiddle.class);
                    example.createCriteria().andEqualTo("programId", platformProgramId);
                    List<PlatformLabelProgramMiddle> platformLabelProgramMiddles = platformLabelProgramMiddleMapper.selectByExample(example);
                    if (null != platformLabelProgramMiddles && platformLabelProgramMiddles.size() > 0) {
                        List list = new ArrayList();
                        for (PlatformLabelProgramMiddle platformLabelProgramMiddle : platformLabelProgramMiddles) {
                            String programLabelId = platformLabelProgramMiddle.getProgramLabelId();
                            if (StringUtils.isNotBlank(programLabelId)) {
                                PlatformProgrameLabel platformProgrameLabel = platformProgrameLabelMapper.selectByPrimaryKey(programLabelId);
                                if (null != platformProgrameLabel) {
                                    LabelVo lv = new LabelVo();
                                    lv.setLabelId(programLabelId);
                                    lv.setLabelName(platformProgrameLabel.getLabelName());
                                    list.add(lv);

                                }
                            }
                        }
                        ppvo.setLabelIds(list);
                    }
                    Example example1 = new Example(BizImageRecorder.class);
                    example1.createCriteria().andEqualTo("bizId", platformProgramId).andEqualTo("type", 1);
                    List<BizImageRecorder> bizImageRecorders = bizImageRecorderMapper.selectByExample(example1);
                    if (null != bizImageRecorders && bizImageRecorders.size() > 0) {
                        for (BizImageRecorder bir : bizImageRecorders) {
                            if (null != bir) {
                                String imageType = bir.getImageType();
                                if ("big".equals(imageType)) {
                                    ppvo.setPhotoUrlBig(bir.getImageUrl());
                                } else if ("in".equals(imageType)) {
                                    ppvo.setPhotoUrl(bir.getImageUrl());
                                }
                            }
                        }
                    }
                    String creater = platformProgram.getCreater();
                    if (StringUtils.isNotBlank(creater)) {
                        SaasUser saasUser = saasUserService.selectSaasUserByUserId(creater);
                        if (null != saasUser) {
                            String userName = saasUser.getUserName();
                            if (StringUtils.isNotBlank(userName)) {
                                ppvo.setCreater(userName);
                            }
                        }
                    }
                    return ppvo;
                }
            }
        }
        return null;
    }


    /**
     * 根据节目主键id查询节目详情的功能接口
     * @return
     */
    public PlatformProgrameVo selectPlatformProgramByType(String platformProgramId,String type){
        if(StringUtils.isNotBlank(platformProgramId)){
            Example example1e = new Example(PlatformProgram.class);
            example1e.createCriteria().andEqualTo("id",platformProgramId).andEqualTo("recorderStatus",1);
            List<PlatformProgram> platformPrograms = platformProgramMapper.selectByExample(example1e);
            if(null != platformPrograms && platformPrograms.size() > 0) {
                PlatformProgram platformProgram = platformPrograms.get(0);
                if (null != platformProgram) {
                    PlatformProgrameVo ppvo = new PlatformProgrameVo();
                    BeanUtils.copyProperties(platformProgram, ppvo);
                    if(StringUtils.isBlank(type) || "0".equals(type)){
                        Example example = new Example(PlatformLabelProgramMiddle.class);
                        example.createCriteria().andEqualTo("programId", platformProgramId);
                        List<PlatformLabelProgramMiddle> platformLabelProgramMiddles = platformLabelProgramMiddleMapper.selectByExample(example);
                        if (null != platformLabelProgramMiddles && platformLabelProgramMiddles.size() > 0) {
                            List list = new ArrayList();
                            for (PlatformLabelProgramMiddle platformLabelProgramMiddle : platformLabelProgramMiddles) {
                                String programLabelId = platformLabelProgramMiddle.getProgramLabelId();
                                if (StringUtils.isNotBlank(programLabelId)) {
                                    PlatformProgrameLabel platformProgrameLabel = platformProgrameLabelMapper.selectByPrimaryKey(programLabelId);
                                    if (null != platformProgrameLabel) {
                                        LabelVo lv = new LabelVo();
                                        lv.setLabelId(programLabelId);
                                        lv.setLabelName(platformProgrameLabel.getLabelName());
                                        list.add(lv);

                                    }
                                }
                            }
                            ppvo.setLabelIds(list);
                        }
                    }else if(StringUtils.isNotBlank(type)&& "1".equals(type)){
                        Example example = new Example(PlatformProgramTenantMiddle.class);
                        example.createCriteria().andEqualTo("programId", platformProgramId);
                        List<PlatformProgramTenantMiddle> PlatformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectByExample(example);
                        if (null != PlatformProgramTenantMiddles && PlatformProgramTenantMiddles.size() > 0) {
                            List list = new ArrayList();
                            for (PlatformProgramTenantMiddle platformProgramTenantMiddle : PlatformProgramTenantMiddles) {
                                String tenantId = platformProgramTenantMiddle.getTenantId();
                                if (StringUtils.isNotBlank(tenantId)) {
                                    LabelVo lv = null;
                                    ////////////////////////////////////////////////////////////////////
                                    String s1 = RedisUtil.get(tenantId+"_SaasTenantInfo");
                                    if(StringUtils.isNotBlank(s1)){
                                        SaasTenantInfoVo saasTenantInfoVo = JSON.parseObject(s1, SaasTenantInfoVo.class);
                                        if(null != saasTenantInfoVo) {
                                            lv = new LabelVo();
                                            lv.setLabelId(saasTenantInfoVo.getTenantId());
                                            lv.setLabelName(saasTenantInfoVo.getTenantName());
                                        }
                                    }else {

                                            SaasTenantInfo saasTenantInfo = saasTenantService.selectSaasTenantInfoByTenantId(tenantId);
                                        if(null != saasTenantInfo) {
                                            lv = new LabelVo();
                                            lv.setLabelId(saasTenantInfo.getTenantId());
                                            lv.setLabelName(saasTenantInfo.getTenantName());
                                        }
                                    }
                                    list.add(lv);
                                }
                            }
                            ppvo.setLabelIds(list);
                        }
                    }
                    Example example1 = new Example(BizImageRecorder.class);
                    example1.createCriteria().andEqualTo("bizId", platformProgramId).andEqualTo("type", 1);
                    List<BizImageRecorder> bizImageRecorders = bizImageRecorderMapper.selectByExample(example1);
                    if (null != bizImageRecorders && bizImageRecorders.size() > 0) {
                        for (BizImageRecorder bir : bizImageRecorders) {
                            if (null != bir) {
                                String imageType = bir.getImageType();
                                if ("big".equals(imageType)) {
                                    ppvo.setPhotoUrlBig(bir.getImageUrl());
                                } else if ("in".equals(imageType)) {
                                    ppvo.setPhotoUrl(bir.getImageUrl());
                                }
                            }
                        }
                    }
                    String creater = platformProgram.getCreater();
                    if (StringUtils.isNotBlank(creater)) {
                        SaasUser saasUser = saasUserService.selectSaasUserByUserId(creater);
                        if (null != saasUser) {
                            String userName = saasUser.getUserName();
                            if (StringUtils.isNotBlank(userName)) {
                                ppvo.setCreater(userName);
                            }
                        }
                    }
                    return ppvo;
                }
            }
        }
        return null;
    }




    /**
     * 查询所有节目详情的功能
     * @return
     */
    public List<PlatformProgram> selectPlatformPrograms(PlatformProgram platformProgram){
        Example example = new Example(PlatformProgram.class);
        if(null != platformProgram){
            String programName = platformProgram.getProgramName();
            if(StringUtils.isNotBlank(programName)){
                example.or().andLike("programName","%"+programName+"%");
                example.or().andLike("programNamePinyin","%"+programName+"%");
                example.or().andLike("programNameJianpin","%"+programName+"%");
            }
            String programStatus = platformProgram.getProgramStatus();
            if(StringUtils.isNotBlank(programStatus)){
                example.createCriteria().andEqualTo("programStatus",programStatus);
            }
            example.createCriteria().andEqualTo("recorderStatus",1);
            example.setOrderByClause("creat_time desc");
            return platformProgramMapper.selectByExample(example);
        }else{
            example.createCriteria().andEqualTo("recorderStatus",1);
            example.setOrderByClause("creat_time desc");
            return platformProgramMapper.selectByExample(example);
        }
    }

    /**
     * 获取所有未删除状态的节目集合数据
     * @return
     */
    public List<PlatformProgram> selectAllPlatformProgram(BasePageEntity page,String type){
        Example example = new Example(PlatformProgram.class);
        example.createCriteria().andEqualTo("recorderStatus",1).andEqualTo("programType",type);
        if(type.equals("0")){
            Example example1 = new Example(PlatformProgram.class);
            Example.Criteria programType = example1.createCriteria().andIsNull("programType").andEqualTo("recorderStatus",1);
            example.or(programType);
        }
        example.setOrderByClause("creat_time desc");
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        return platformProgramMapper.selectByExample(example);
    }


    /**
     *  根据标签id获取相对应的数据集
     * @param platformProgrameLabelId
     * @param page
     * @return
     */
    public List<PlatformProgram> selectPlatformProgramByLabelId(String platformProgrameLabelId,BasePageEntity page,String type) {
        List platformProgrameIds = new ArrayList();
        List<PlatformLabelProgramMiddle> platformLabelProgramMiddles = new ArrayList<>();
        if (StringUtils.isBlank(type) || "0".equals(type)){
            Example example1 = new Example(PlatformLabelProgramMiddle.class);
        example1.createCriteria().andEqualTo("programLabelId", platformProgrameLabelId);
        platformLabelProgramMiddles = platformLabelProgramMiddleMapper.selectByExample(example1);
            if(null != platformLabelProgramMiddles && platformLabelProgramMiddles.size() > 0) {
                for (PlatformLabelProgramMiddle ppm : platformLabelProgramMiddles) {
                    String programId = ppm.getProgramId();
                    if (StringUtils.isNotBlank(programId)) {
                        platformProgrameIds.add(programId);
                    }
                }
            }
    }else if(StringUtils.isNotBlank(type) && "1".equals(type)){
            Example example = new Example(PlatformProgramTenantMiddle.class);
            example.createCriteria().andEqualTo("tenantId",platformProgrameLabelId);
            List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectByExample(example);
            if(null != platformProgramTenantMiddles && platformProgramTenantMiddles.size() > 0) {
                for (PlatformProgramTenantMiddle ppm : platformProgramTenantMiddles) {
                    String programId = ppm.getProgramId();
                    if (StringUtils.isNotBlank(programId)) {
                        platformProgrameIds.add(programId);
                    }
                }
            }
        }
    if(StringUtils.isBlank(type) || "0".equals(type)) {
        List<PlatformProgram> platformPrograms = new ArrayList<>();
        if(null != platformProgrameIds && platformProgrameIds.size() > 0) {
            Map map = new HashMap();
            map.put("platformProgrameIds", platformProgrameIds);
            map.put("type", type);
            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
            platformPrograms = platformProgramMapper.selectProgramByType(map);
        }
        return platformPrograms;
    }else if(StringUtils.isNotBlank(type) && "1".equals(type)){
        List<PlatformProgram> platformPrograms = new ArrayList<>();
        if(null != platformProgrameIds && platformProgrameIds.size() > 0) {
            Example example1 = new Example(PlatformProgram.class);
            example1.createCriteria().andEqualTo("recorderStatus", 1).andEqualTo("programType", type).andIn("id", platformProgrameIds);
            example1.setOrderByClause("creat_time desc");
            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
            platformPrograms = platformProgramMapper.selectByExample(example1);
        }
        return platformPrograms;
    }
 return null;
    }


    /**
     * 获取节目总数量
     * @return
     */
    public Integer selectPlatformProgramTotalNum(String type){
        int i = 0;
        if(StringUtils.isNotBlank(type) && "1".equals(type)) {
            Example example = new Example(PlatformProgram.class);
            example.createCriteria().andEqualTo("programType", type).andEqualTo("recorderStatus", 1);
            i = platformProgramMapper.selectCountByExample(example);
        }else if(StringUtils.isBlank(type) || "0".equals(type)){
            i = platformProgramMapper.selectProgramNumByType();
        }
        return i;
    }


    /**
     * 获取节目总数量
     * @return
     */
    public Integer selectPlatformProgramTotalNum(String type,String platformProgrameLabelId){
        int i = 0;
        if(StringUtils.isBlank(platformProgrameLabelId) || "0".equals(platformProgrameLabelId)) {
            Example example = new Example(PlatformProgram.class);
            example.createCriteria().andEqualTo("programType", type).andEqualTo("recorderStatus", 1);
            if (StringUtils.isBlank(type) || type.equals("0")) {
                Example example1 = new Example(PlatformProgram.class);
                Example.Criteria programType = example1.createCriteria().andIsNull("programType").andEqualTo("recorderStatus", 1);
                example.or(programType);
            }
            i = platformProgramMapper.selectCountByExample(example);
        }else {
            List platformProgrameIds = new ArrayList();
            List<PlatformLabelProgramMiddle> platformLabelProgramMiddles = new ArrayList<>();
            if (StringUtils.isBlank(type) || "0".equals(type)){
                Example example1 = new Example(PlatformLabelProgramMiddle.class);
                example1.createCriteria().andEqualTo("programLabelId", platformProgrameLabelId);
                platformLabelProgramMiddles = platformLabelProgramMiddleMapper.selectByExample(example1);
                if(null != platformLabelProgramMiddles && platformLabelProgramMiddles.size() > 0) {
                    for (PlatformLabelProgramMiddle ppm : platformLabelProgramMiddles) {
                        String programId = ppm.getProgramId();
                        if (StringUtils.isNotBlank(programId)) {
                            platformProgrameIds.add(programId);
                        }
                    }
                }
            }else if(StringUtils.isNotBlank(type) && "1".equals(type)){
                Example example = new Example(PlatformProgramTenantMiddle.class);
                example.createCriteria().andEqualTo("tenantId",platformProgrameLabelId);
                List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectByExample(example);
                if(null != platformProgramTenantMiddles && platformProgramTenantMiddles.size() > 0) {
                    for (PlatformProgramTenantMiddle ppm : platformProgramTenantMiddles) {
                        String programId = ppm.getProgramId();
                        if (StringUtils.isNotBlank(programId)) {
                            platformProgrameIds.add(programId);
                        }
                    }
                }
            }
            if(StringUtils.isBlank(type) || "0".equals(type)) {
                List<PlatformProgram> platformPrograms = new ArrayList<>();
                if(null != platformProgrameIds && platformProgrameIds.size() > 0) {
                    Map map = new HashMap();
                    map.put("platformProgrameIds", platformProgrameIds);
                    map.put("type", type);
                    platformPrograms = platformProgramMapper.selectProgramByType(map);
                }
               i = platformPrograms.size();
            }else if(StringUtils.isNotBlank(type) && "1".equals(type)){
               // List<PlatformProgram> platformPrograms = new ArrayList<>();
                if(null != platformProgrameIds && platformProgrameIds.size() > 0) {
                    Example example1 = new Example(PlatformProgram.class);
                    example1.createCriteria().andEqualTo("recorderStatus", 1).andEqualTo("programType", type).andIn("id", platformProgrameIds);
                    //example1.setOrderByClause("creat_time desc");
                    //platformPrograms = platformProgramMapper.selectByExample(example1);
                    i = platformProgramMapper.selectCountByExample(example1);
                }
               // i = platformPrograms.size();
            }
        }
        return i;
    }

    /**
     * 为前端获取内测、同步显示按钮机制
     * @return
     */
    public String getBatchOperationState(String type){
        Example example = new Example(PlatformProgram.class);
        example.createCriteria().andEqualTo("programStatus",0).andEqualTo("programType",type).andEqualTo("recorderStatus",1);
        if(type.equals("0")){
            Example example1 = new Example(PlatformProgram.class);
            Example.Criteria programType = example1.createCriteria().andEqualTo("programStatus",0).andIsNull("programType").andEqualTo("recorderStatus",1);
            example.or(programType);
        }

        example.setOrderByClause("creat_time desc");
        List<PlatformProgram> platformPrograms = platformProgramMapper.selectByExample(example);
        if(null != platformPrograms && platformPrograms.size() > 0){
            return "0";
        }
        Example example1 = new Example(PlatformProgram.class);
        example1.createCriteria().andEqualTo("programStatus",2).andEqualTo("programType",type).andEqualTo("recorderStatus",1);
        if(type.equals("0")){
            Example example11 = new Example(PlatformProgram.class);
            Example.Criteria programType = example11.createCriteria().andEqualTo("programStatus",2).andIsNull("programType").andEqualTo("recorderStatus",1);
            example1.or(programType);
        }
        List<PlatformProgram> platformPrograms1 = platformProgramMapper.selectByExample(example1);
        if(null != platformPrograms1  &&  platformPrograms1.size() > 0){
            return "1";
        }
        return "2";
    }


    /**
     * 文件存储功能接口
     * @return
     */
    public boolean addBizImageRecorder(String userId,FileVo fileVo){
        if(null != fileVo){
            String id = fileVo.getId();
            if(StringUtils.isNotBlank(id)){
                BizImageRecorder bizImageRecorder = new BizImageRecorder();
                String big = fileVo.getBig();
                bizImageRecorder.setId(CreateUUIdUtil.Uuid());
                bizImageRecorder.setImageId(id);
                bizImageRecorder.setCreateTime(new Date());
                bizImageRecorder.setImageType("big");
                bizImageRecorder.setImageUrl(big);
                bizImageRecorder.setType("1");
                int i = bizImageRecorderMapper.insertSelective(bizImageRecorder);
                if(i > 0){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据查询条件查询节目集合
     * @param seachKey
     * @param list
     * @param basePageEntity
     * @return
     */
    public List<PlatformProgram> selectAllPlatformProgramBySeachKey(String seachKey,List list,BasePageEntity basePageEntity,String type){
        if(StringUtils.isBlank(type)){
            type = "0";
        }
        if (null != list && list.size() > 0) {
            return getPlatformProgramBySeachKey(seachKey,list,basePageEntity,type);
        }else {
            return getPlatformProgramBySeachKey(seachKey,basePageEntity,type);
        }
    }

    /**
     * 根据条件查询符合条件的节目数量
     * @param seachKey
     * @param list
     * @return
     */
    public Integer selectPlatformProgramTotalNumBySeachKey(String seachKey,List list,String type){
        if(StringUtils.isBlank(type)){
            type = "0";
        }
        if (null != list && list.size() > 0) {
            List platformProgramBySeachKey3 = getPlatformProgramBySeachKey3(seachKey, list,type);
            return platformProgramBySeachKey3.size();
        }else {
            List platformProgramBySeachKey4 = getPlatformProgramBySeachKey4(seachKey,type);
            return platformProgramBySeachKey4.size();
        }
    }

    /**
     * 条件查询1
     * @param seachKey
     * @param list
     * @param basePageEntity
     * @return
     */
    public List getPlatformProgramBySeachKey(String seachKey,List list,BasePageEntity basePageEntity,String type){
        Map map = new HashMap();
        if(StringUtils.isNotBlank(seachKey)) {
            String seachKeyy = "%" + seachKey + "%";
            map.put("seachKey", seachKeyy);
        }
        map.put("ids",list);
        map.put("type",type);
        PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
        return platformProgramMapper.getPlatformProgramBySeachKey(map);
    }
    /**
     * 条件查询2
     * @param seachKey
     * @param basePageEntity
     * @return
     */
    public List getPlatformProgramBySeachKey(String seachKey,BasePageEntity basePageEntity,String type){
        Map map = new HashMap();
        if(StringUtils.isNotBlank(seachKey)) {
            String seachKeyy = "%" + seachKey + "%";
            map.put("seachKey", seachKeyy);
        }
        map.put("type",type);
        PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
        return platformProgramMapper.getPlatformProgramBySeachKey(map);
    }


    /**
     * 条件查询3
     * @param seachKey
     * @param list
     * @return
     */
    public List getPlatformProgramBySeachKey3(String seachKey,List list,String type){
        Map map = new HashMap();
        if(StringUtils.isNotBlank(seachKey)) {
            String seachKeyy = "%" + seachKey + "%";
            map.put("seachKey", seachKeyy);
        }
        map.put("type",type);
        map.put("ids",list);
        return platformProgramMapper.getPlatformProgramBySeachKey(map);
    }
    /**
     * 条件查询4
     * @param seachKey
     * @return
     */
    public List getPlatformProgramBySeachKey4(String seachKey,String type){
        Map map = new HashMap();
        if(StringUtils.isNotBlank(seachKey)) {
            String seachKeyy = "%" + seachKey + "%";
            map.put("seachKey", seachKeyy);
        }
        map.put("type",type);
            return platformProgramMapper.getPlatformProgramBySeachKey(map);
    }

    /**
     * 文件的检查和添加修改
     * @param userId
     * @param fileVo
     * @param platformProgrameId
     * @return
     */
    public boolean updateBizImageRecorderByBizId(String userId,FileVo fileVo,String platformProgrameId,String type){
        if(StringUtils.isNotBlank(userId) && null != fileVo && StringUtils.isNotBlank(platformProgrameId) && StringUtils.isNotBlank(type)){
            Example example = new Example(BizImageRecorder.class);
            example.createCriteria().andEqualTo("bizId",platformProgrameId).andEqualTo("type",type);
            List<BizImageRecorder> bizImageRecorders = bizImageRecorderMapper.selectByExample(example);
            if(null != bizImageRecorders && bizImageRecorders.size() > 0) {
                for (BizImageRecorder bir : bizImageRecorders) {
                    String id = bir.getId();
                    bizImageRecorderMapper.deleteByPrimaryKey(id);
                }
            }
                String id = fileVo.getId();
                if("1".equals(type)){
                        if(StringUtils.isNotBlank(id)){
                            BizImageRecorder bizImageRecorder = new BizImageRecorder();
                            String big = fileVo.getBig();
                            if(StringUtils.isNotBlank(big)) {
                                bizImageRecorder.setId(CreateUUIdUtil.Uuid());
                                bizImageRecorder.setImageId(id);
                                bizImageRecorder.setBizId(platformProgrameId);
                                bizImageRecorder.setCreateTime(new Date());
                                bizImageRecorder.setImageType("big");
                                bizImageRecorder.setImageUrl(big);
                                bizImageRecorder.setType("1");
                                int i = bizImageRecorderMapper.insertSelective(bizImageRecorder);
                                if (i <= 0) {
                                    return false;
                                }
                            }
                            BizImageRecorder bizImageRecorder1 = new BizImageRecorder();
                            String in = fileVo.getIn();
                            if(StringUtils.isNotBlank(in)) {
                                bizImageRecorder1.setId(CreateUUIdUtil.Uuid());
                                bizImageRecorder1.setImageId(id);
                                bizImageRecorder1.setBizId(platformProgrameId);
                                bizImageRecorder1.setCreateTime(new Date());
                                bizImageRecorder1.setImageType("in");
                                bizImageRecorder1.setImageUrl(in);
                                bizImageRecorder1.setType("1");
                                int i1 = bizImageRecorderMapper.insertSelective(bizImageRecorder1);
                                if (i1 <= 0) {
                                    return false;
                                }
                            }
                            BizImageRecorder bizImageRecorder2 = new BizImageRecorder();
                            String small = fileVo.getSmall();
                            if(StringUtils.isNotBlank(small)) {
                                bizImageRecorder2.setId(CreateUUIdUtil.Uuid());
                                bizImageRecorder2.setImageId(id);
                                bizImageRecorder2.setBizId(platformProgrameId);
                                bizImageRecorder2.setCreateTime(new Date());
                                bizImageRecorder2.setImageType("small");
                                bizImageRecorder2.setImageUrl(small);
                                bizImageRecorder2.setType("1");
                                int i2 = bizImageRecorderMapper.insertSelective(bizImageRecorder2);
                                if (i2 <= 0) {
                                    return false;
                                }
                            }
                            BizImageRecorder bizImageRecorder3 = new BizImageRecorder();
                            String primary = fileVo.getPrimary();
                            if(StringUtils.isNotBlank(primary)) {
                                bizImageRecorder3.setId(CreateUUIdUtil.Uuid());
                                bizImageRecorder3.setImageId(id);
                                bizImageRecorder3.setBizId(platformProgrameId);
                                bizImageRecorder3.setCreateTime(new Date());
                                bizImageRecorder3.setImageType("primary");
                                bizImageRecorder3.setImageUrl(primary);
                                bizImageRecorder3.setType("1");
                                int i3 = bizImageRecorderMapper.insertSelective(bizImageRecorder3);
                                if (i3 <= 0) {
                                    return false;
                                }
                            }
                        }
                        return true;
                }else{
                    if(StringUtils.isNotBlank(id)){
                        BizImageRecorder bizImageRecorder = new BizImageRecorder();
                        String primary = fileVo.getPrimary();
                        bizImageRecorder.setId(CreateUUIdUtil.Uuid());
                        bizImageRecorder.setBizId(platformProgrameId);
                        bizImageRecorder.setImageId(id);
                        bizImageRecorder.setCreateTime(new Date());
                        bizImageRecorder.setImageUrl(primary);
                        bizImageRecorder.setType("2");
                        int i = bizImageRecorderMapper.insertSelective(bizImageRecorder);
                        if(i <= 0){
                            return false;
                        }
                    }
                    return true;
                }
        }
        return false;
    }


    /**
     * 根据节目名称获取节目信息
     * @param platformProgramName
     * @return
     */
    public List<PlatformProgram> selectPlatformProgramsByName(String platformProgramName){
        if(StringUtils.isNotBlank(platformProgramName)) {
            Example example = new Example(PlatformProgram.class);
            example.createCriteria().andEqualTo("programName",platformProgramName).andEqualTo("recorderStatus",1);
            List<PlatformProgram> platformPrograms = platformProgramMapper.selectByExample(example);
            return platformPrograms;
        }
        return null;
    }


    /**
     * 根据bizid和类型获取相关信息
     * @param platformProgrameId
     * @return
     */
    public BizImageRecorder getBizImageRecorderByBizId(String platformProgrameId){
        if(StringUtils.isNotBlank(platformProgrameId)){
            Example example = new Example(BizImageRecorder.class);
            example.createCriteria().andEqualTo("bizId",platformProgrameId).andEqualTo("type",2);
            List<BizImageRecorder> bizImageRecorders = bizImageRecorderMapper.selectByExample(example);
            if(null != bizImageRecorders && bizImageRecorders.size() > 0){
                return bizImageRecorders.get(0);
            }
        }
        return null;
    }

    /**
     * 节目状态修改功能
     * @param map
     * @return
     */
    public boolean updateProgramStatusByStatus(Map map){
        int i = platformProgramMapper.updateProgramStatusByStatus(map);
        if(i>=0){
            return true;
        }
        return false;
    }

    /**
     * 根据条件查询节目功能接口
     * @param example
     * @return
     */
    public List<PlatformProgram> selectPlatformProgramByExample(Example example){
        return platformProgramMapper.selectByExample(example);
    }

    /**
     * 根据节目的key选择性修改节目内容
     * @param platformProgram
     * @return
     */
    public int updateByPrimaryKeySelective(PlatformProgram platformProgram){
        return platformProgramMapper.updateByPrimaryKeySelective(platformProgram);
    }



    /**
     * 节目同步功能
     * @return
     */
    public boolean platformProgrameSynchronization(String nextProgramVersionNum,String type) {
        if (StringUtils.isBlank(type)) {
            type = "0";
        }
        if (StringUtils.isBlank(type) || "0".equals(type)) {
            Example example = new Example(PlatformProgrameLabel.class);
            example.createCriteria().andEqualTo("labelStatus", 2);
            List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelMapper.selectByExample(example);
            if (null != platformProgrameLabels && platformProgrameLabels.size() > 0) {
                for (PlatformProgrameLabel platformProgrameLabel : platformProgrameLabels) {
                    //编辑需要改变的标签值表格中需要添加ver属性
                    if (null != platformProgrameLabel) {
                        PlatformProgrameLabel ppl1 = new PlatformProgrameLabel();
                        ppl1.setId(platformProgrameLabel.getId());
                        ppl1.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                        ppl1.setModifyTime(new Date());
                        ppl1.setVer(CreateUUIdUtil.Uuid());
                        ppl1.setLabelStatus("1");
                        int i = platformProgrameLabelMapper.updateByPrimaryKeySelective(ppl1);
                        if (i < 0) {
                            return false;
                        }
                    }
                }
            }
        }
        Example example1 = new Example(PlatformProgram.class);
        if (StringUtils.isBlank(type) || "0".equals(type)) {
            example1.createCriteria().andEqualTo("programStatus", 2).andEqualTo("recorderStatus", 1).andEqualTo("programType", 0);
            Example example6 = new Example(PlatformProgram.class);
            Example.Criteria criteria = example6.createCriteria().andEqualTo("programStatus", 2).andIsNull("programType").andEqualTo("recorderStatus", 1);
            example1.or(criteria);
        } else if (StringUtils.isNotBlank(type) && "1".equals(type)) {
            example1.createCriteria().andEqualTo("programStatus", 2).andEqualTo("recorderStatus", 1).andEqualTo("programType", 1);
        }
        List<PlatformProgram> platformPrograms = selectPlatformProgramByExample(example1);
        if(StringUtils.isBlank(type) || "0".equals(type)){
        if (null != platformPrograms && platformPrograms.size() > 0) {
            for (PlatformProgram pfp : platformPrograms) {
                if (null != pfp) {
                    PlatformProgram pfp1 = new PlatformProgram();
                    pfp1.setId(pfp.getId());
                    pfp1.setProgramStatus("1");
                    pfp1.setVer(CreateUUIdUtil.Uuid());
                    pfp1.setModifyTime(new Date());
                    pfp1.setProgramUpdateBatch(CreateUUIdUtil.Uuid());
                    pfp1.setProgramVersionNum(nextProgramVersionNum);
                    int i1 = updateByPrimaryKeySelective(pfp1);
                    if (i1 < 0) {
                        return false;
                    }
                }
            }
        }
    }else if(StringUtils.isNotBlank(type) && "1".equals(type)){
            if (null != platformPrograms && platformPrograms.size() > 0){
                List<String> list = new ArrayList();
                for (PlatformProgram platp:platformPrograms) {
                    if(null != platp){
                        String programId = platp.getId();
                        if(StringUtils.isNotBlank(programId)){
                            Example example11 = new Example(PlatformProgramTenantMiddle.class);
                            example11.createCriteria().andEqualTo("programId",programId);
                            List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectByExample(example11);
                            if(null != platformProgramTenantMiddles && platformProgramTenantMiddles.size() > 0){
                                for (PlatformProgramTenantMiddle pptm:platformProgramTenantMiddles) {
                                    if(null != pptm){
                                        String tenantId = pptm.getTenantId();
                                        boolean contains = list.contains(tenantId);
                                        if(!contains){
                                            list.add(tenantId);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(null != list && list.size() > 0) {
                    for (String tenantIdd:list) {
                        if(StringUtils.isNotBlank(tenantIdd)){
                            String newProgramVersionNum = "0.0";
                            boolean b = false;
                            List<PlatformProgramVersion> platformProgramVersionss = platformProgramVersionSerevice.newSelectPlatformProgramVersion("1", tenantIdd);
                            if(null != platformProgramVersionss && platformProgramVersionss.size() > 0){
                                PlatformProgramVersion platformProgramVersion = platformProgramVersionss.get(0);
                                if(null != platformProgramVersion){
                                    String programVersionNum = platformProgramVersion.getProgramVersionNum();
                                    PlatformProgramVersion platformProgramVersionNew = new PlatformProgramVersion();
                                   String platformProgramVersionId = CreateUUIdUtil.Uuid();
                                    double d = 0.0;
                                    if ( StringUtils.isNotBlank(programVersionNum)) {
                                        d = Double.parseDouble(programVersionNum);
                                    }else{
                                        platformProgramVersionId = platformProgramVersion.getId();
                                    }
                                    newProgramVersionNum = d + 1 + "";
                                        platformProgramVersionNew.setId(platformProgramVersionId);
                                        platformProgramVersionNew.setProgramVersionNum(newProgramVersionNum);
                                        platformProgramVersionNew.setCreatTime(new Date());
                                        platformProgramVersionNew.setPublishTime(new Date());
                                        platformProgramVersionNew.setVer(CreateUUIdUtil.Uuid());
                                        platformProgramVersionNew.setVersionUpdateBatch(CreateUUIdUtil.Uuid());
                                        platformProgramVersionNew.setCustomTenantId(tenantIdd);
                                        if(StringUtils.isNotBlank(programVersionNum)){
                                        b = platformProgramVersionSerevice.newAddPlatformProgramVersion(platformProgramVersionNew);
                                   }else{
                                          b = platformProgramVersionSerevice.updatePlatformProgarmVersion(platformProgramVersionNew);
                                        }
                                }
                            }else{
                                PlatformProgramVersion platformProgramVersionNew = new PlatformProgramVersion();
                                double d = Double.parseDouble("0.0");
                                newProgramVersionNum = d + 1 + "";
                                //如果版本号符合生成规则就向下执行否则返回操作失败提示
                                platformProgramVersionNew.setId(CreateUUIdUtil.Uuid());
                                platformProgramVersionNew.setProgramVersionNum(newProgramVersionNum);
                                platformProgramVersionNew.setCreatTime(new Date());
                                platformProgramVersionNew.setPublishTime(new Date());
                                platformProgramVersionNew.setVer(CreateUUIdUtil.Uuid());
                                platformProgramVersionNew.setVersionUpdateBatch(CreateUUIdUtil.Uuid());
                                platformProgramVersionNew.setCustomTenantId(tenantIdd);
                                b = platformProgramVersionSerevice.newAddPlatformProgramVersion(platformProgramVersionNew);
                            }
                            if(!b){
                                return false;
                            }
                            Map map = new HashMap();
                            //修改对应数据的状态和版本号
                            map.put("programStatus","1");
                            map.put("ver",CreateUUIdUtil.Uuid());
                            map.put("modifyTime",new Date());
                            map.put("programUpdateBatch",CreateUUIdUtil.Uuid());
                            map.put("programVersionNum",newProgramVersionNum);
                            map.put("tenantId",tenantIdd);
                            boolean b1 = platformProgramMapper.updateProgramStatusByTypeAndTenantId(map);
                            if(!b1){
                                return false;
                            }
                        }
                    }
                    List<PlatformProgramVersion> ppvs1 = platformProgramVersionSerevice.newSelectPlatformProgramVersion("0",null);
                    if(null != ppvs1 && ppvs1.size() > 0){  //在同步必须通过内测功能才能执行同步功能的情况下不回去状况
                        PlatformProgramVersion platformProgramVersion1 = ppvs1.get(0);
                        if(null != platformProgramVersion1){
                            PlatformProgramVersion platformProgramVersionNew1 = new PlatformProgramVersion();
                            platformProgramVersionNew1.setId(platformProgramVersion1.getId());
                            boolean b =  platformProgramVersionSerevice.updatePlatformProgarmVersion(platformProgramVersionNew1);
                            if(!b){
                             return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取定制节目上传时的租户数据集功能接口
     * @param seachKey
     * @return
     */
    public List<CustomizingTenantVo> getAllTenants(String seachKey,BasePageEntity basePageEntity){
        if(StringUtils.isNotBlank(seachKey)){
            List list = new ArrayList();
         List<SaasTenant> saasTenantList = saasTenantService.selectAllSaasTenantBySeachKey(seachKey,basePageEntity);
         if(null != saasTenantList && saasTenantList.size() > 0){
             for (SaasTenant st:saasTenantList) {
                if(null != st){
                    CustomizingTenantVo ctv = new CustomizingTenantVo();
                    ctv.setId(st.getId());
                    ctv.setTenantName(st.getTenantName());
                    list.add(ctv);
                }
             }
         }
         return list;
        }
        return null;
    }
    /**
     * 获取定制节目上传时的租户数据集功能接口
     //* @param seachKey
     * @return
     */
    public List<CustomizingTenantVo> getAllTenants(){
        List list = new ArrayList();
        List<SaasTenant> saasTenantList = saasTenantService.selectAllSaasTenants();
        if(null != saasTenantList && saasTenantList.size() > 0){
                for (SaasTenant st:saasTenantList) {
                    if(null != st){
                        CustomizingTenantVo ctv = new CustomizingTenantVo();
                        ctv.setId(st.getId());
                        ctv.setTenantName(st.getTenantName());
                        list.add(ctv);
                    }
                }
            }
       return list;
    }
    /**
     * 获得去重后的所有的有定制节目的租户主键集合接口
     * @return
     */
    public List<PlatformProgramTenantMiddle> selectPlatformProgramTenantMiddle(String tenantId){
        if(StringUtils.isBlank(tenantId) || "0".equals(tenantId)) {
            List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectPlatformProgramTenantMiddle();
            return platformProgramTenantMiddles;
        }else{
            Example example = new Example(PlatformProgramTenantMiddle.class);
            example.createCriteria().andEqualTo("tenantId",tenantId);
            List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectByExample(example);
            return platformProgramTenantMiddles;
        }
    }

    /**
     * 获取所有的节目与租户之间的关联关系
     * @return
     */
    public List<PlatformProgramTenantMiddle> selectPlatformProgramTenantMiddles(){
        List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = new ArrayList<>();
        List<PlatformProgramTenantMiddle> platformProgramTenantMiddles1 = platformProgramTenantMiddleMapper.selectPlatformProgramTenantMiddle();
        if(null != platformProgramTenantMiddles1  && platformProgramTenantMiddles1.size() > 0){
            for (PlatformProgramTenantMiddle pptm:platformProgramTenantMiddles1) {
                if(null != pptm){
                    String tenantId = pptm.getTenantId();
                    if(StringUtils.isNotBlank(tenantId)){
                        Example example = new Example(PlatformProgramTenantMiddle.class);
                        example.createCriteria().andEqualTo("tenantId",tenantId);
                        List<PlatformProgramTenantMiddle> platformProgramTenantMiddles2 = platformProgramTenantMiddleMapper.selectByExample(example);
                        if(null != platformProgramTenantMiddles2 && platformProgramTenantMiddles2.size() > 0){
                            for (PlatformProgramTenantMiddle pptms:platformProgramTenantMiddles2) {
                                if(null != pptms){
                                    String programId = pptms.getProgramId();
                                    if(StringUtils.isNotBlank(programId)){
                                        Example example1 = new Example(PlatformProgram.class);
                                        example1.createCriteria().andEqualTo("id",programId).andEqualTo("recorderStatus",1);
                                        List<PlatformProgram> platformPrograms = platformProgramMapper.selectByExample(example1);
                                        if(null != platformPrograms && platformPrograms.size() > 0){
                                            platformProgramTenantMiddles.add(pptm);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return platformProgramTenantMiddles;
    }

    /**
     * 根据租户主键获取相应的定制节目数量
     * @param tenantId
     * @return
     */
    public Integer selectpPlatformProgramTenantMiddleNumByTenantId(String tenantId){
        Integer programNum = 0;
        Example example = new Example(PlatformProgramTenantMiddle.class);
        example.createCriteria().andEqualTo("tenantId",tenantId);
        List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectByExample(example);
        if(null != platformProgramTenantMiddles && platformProgramTenantMiddles.size() > 0){
            for (PlatformProgramTenantMiddle pptm:platformProgramTenantMiddles) {
                if(null != pptm){
                    String programId = pptm.getProgramId();
                    if(StringUtils.isNotBlank(programId)){
                        PlatformProgram platformProgram = platformProgramMapper.selectByPrimaryKey(programId);
                        if(null != platformProgram){
                            String recorderStatus = platformProgram.getRecorderStatus();
                            if(StringUtils.isNotBlank(recorderStatus) && "1".equals(recorderStatus)){
                                programNum +=1;
                            }
                        }
                    }
                }
            }
           // programNum = platformProgramTenantMiddles.size();
        }
        return programNum;
    }
















}
