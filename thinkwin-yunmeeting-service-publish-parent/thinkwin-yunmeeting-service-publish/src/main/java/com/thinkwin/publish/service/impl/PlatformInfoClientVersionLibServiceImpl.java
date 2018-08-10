package com.thinkwin.publish.service.impl;

/*
 * 类说明：
 * @author lining 2018/4/28
 * @version 1.0
 *
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.publish.PlatformInfoClientVersionLib;
import com.thinkwin.publish.mapper.PlatformInfoClientVersionLibMapper;
import com.thinkwin.publish.service.PlatformInfoClientVersionLibService;
import com.thinkwin.publish.util.PublishConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("platformInfoClientVersionLibService")
public class PlatformInfoClientVersionLibServiceImpl implements PlatformInfoClientVersionLibService {


    @Autowired
    PlatformInfoClientVersionLibMapper platformInfoClientVersionLibMapper;


    /**
     * 根据版本号查询
     *
     * @param verNum
     * @return
     */
    @Override
    public PlatformInfoClientVersionLib findByVerNum(String verNum,String verStatus) {
        Map paramMap = new HashMap();
        if(StringUtils.isNotBlank(verNum)) {
            paramMap.put("verNum", verNum);
        }
        if(StringUtils.isNotBlank(verStatus)) {
            paramMap.put("verStatus", verStatus);
        }
            List<PlatformInfoClientVersionLib> list= this.platformInfoClientVersionLibMapper.findByVerNum(paramMap);
            return (null!=list && list.size()>0)?list.get(0):null;
    }

    /**
     * 保存终端版本
     *
     * @param versionLib
     * @return
     */
    @Override
    public int addTerminalVersion(PlatformInfoClientVersionLib versionLib) {

         int f=this.platformInfoClientVersionLibMapper.insert(versionLib);

        return f;
    }

    /**
     * 删除版本
     *
     * @param id 终端版本ID
     * @return
     */
    @Override
    public int delTerminalVersion(String id) {

        int f=this.platformInfoClientVersionLibMapper.deleteByPrimaryKey(id);
        return f;
    }

    /**
     * 设置内测/取消内测
     *
     * @param
     * @return
     */
    @Override
    public int setTerminalVersion(PlatformInfoClientVersionLib versionLib) {

        int f=this.platformInfoClientVersionLibMapper.updateByPrimaryKey(versionLib);
        return f;
    }

    /**
     * 获取终端版本详情
     *
     * @param id
     * @return
     */
    @Override
    public PlatformInfoClientVersionLib getId(String id) {
        PlatformInfoClientVersionLib versionLib=this.platformInfoClientVersionLibMapper.getId(id);
        return versionLib;
    }

    /**
     * 终端列表
     *
     * @param searchKey
     * @return
     */
    @Override
    public List<PlatformInfoClientVersionLib> getAll(String searchKey,String verStatus) {
        Map paramMap=new HashMap();
        if(StringUtils.isNotBlank(searchKey)){
            paramMap.put("searchKey",searchKey);
        }
        if(StringUtils.isNotBlank(verStatus)){
            paramMap.put("verStatus",verStatus);
        }
        List<PlatformInfoClientVersionLib> list=this.platformInfoClientVersionLibMapper.getList(paramMap);

        return list;
    }

    /**
     * 已发布的终端版本列表，包含当前版本和历史版本
     *
     * @param searchKey
     * @param releaseStatus 0：已发布，等于SQL !=0 等于 2，3两个状态值
     * @return
     */
    @Override
    public List<PlatformInfoClientVersionLib> getReleaseAll(String searchKey, String releaseStatus) {
        Map paramMap=new HashMap();
        if(StringUtils.isNotBlank(searchKey)){
            paramMap.put("searchKey",searchKey);
        }
        if(StringUtils.isNotBlank(releaseStatus)){
            paramMap.put("releaseStatus",releaseStatus);
        }
        List<PlatformInfoClientVersionLib> list=this.platformInfoClientVersionLibMapper.getReleaseList(paramMap);

        return list;
    }

    /**
     * 获取某个版本更新列表
     *
     * @param id        终端版本ID
     * @param searchKey
     */
    @Override
    public void getVerUpdateInfo(String id, String searchKey) {

    }

    /**
     * 获取最新终端版本
     *
     * @param verType 版本类型 0内测，1正式，若内测无返回最新正式版本
     * @return
     */
    @Override
    public PlatformInfoClientVersionLib getTerminalVersion(String verType) {

        PlatformInfoClientVersionLib versionLib=null;
        if(PublishConstant.testTenand.equals(verType)){
            versionLib=this.findTerminalVersionByVerType(PublishConstant.VerStatus.ALPHA);
        }

        if(versionLib==null || PublishConstant.formalTenand.equals(verType)){
            versionLib=this.findTerminalVersionByVerType(PublishConstant.VerStatus.RELEASE);
        }

        return versionLib;
    }

    /**
     * 返回列表数据
     *
     * @param searchKey
     * @param basePageEntity
     * @return
     */
    @Override
    public Map getData(String searchKey,String hide, BasePageEntity basePageEntity) {
        Map map = new HashMap();


        //是否显示终端更新记录，只显示已发布的版本记录
        Map releaseMap=new HashMap();
        releaseMap.put("releaseStatus",PublishConstant.releaseStatus);
        List<PlatformInfoClientVersionLib> releaseList=this.platformInfoClientVersionLibMapper.getReleaseList(releaseMap);
        map.put("recorde",(releaseList!=null && releaseList.size()>0)?releaseList.size():0);

        //分页显示终端列表
        PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
        Map paramMap=new HashMap();
        if(StringUtils.isNotBlank(searchKey)){
            paramMap.put("searchKey",searchKey);
        }
        List<PlatformInfoClientVersionLib> list=this.platformInfoClientVersionLibMapper.getList(paramMap);
        PageInfo pageInfo = new PageInfo<>(list);
        map.put("verList",pageInfo);

        //是否显示内测版本、发布版本
        if("1".equals(hide)){
            PlatformInfoClientVersionLib releaseVersion=this.findTerminalVersionByVerType(PublishConstant.VerStatus.RELEASE);
            PlatformInfoClientVersionLib alphaVersion=this.findTerminalVersionByVerType(PublishConstant.VerStatus.ALPHA);
            map.put("alphaVer",alphaVersion);
            map.put("releaseVer",releaseVersion);
        }

        return map;
    }

    /**
     * 获取最新终端版本
     *
     * @param verType 版本类型 0内测，1正式，若内测无返回空
     * @return
     */
    @Override
    public PlatformInfoClientVersionLib findTerminalVersionByVerType(String verType) {
        List<PlatformInfoClientVersionLib> versionLib=this.platformInfoClientVersionLibMapper.findByVerStatus(verType);
        return (versionLib!=null && versionLib.size()>0)?versionLib.get(0):null;
    }
}
