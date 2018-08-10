package com.thinkwin.publish.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.publish.PlatformInfoClientVersionLib;

import java.util.List;
import java.util.Map;

/**
 * 终端版本Service
 * 上传、删除、发布、内测、详情、查询等操作
 *
 */
public interface PlatformInfoClientVersionLibService {

    /**
     * 保存终端版本
     * @param
     * @return
     */
    public int addTerminalVersion(PlatformInfoClientVersionLib versionLib);

    /**
     * 删除版本
     * @param id 终端版本ID
     * @return
     */
    public int delTerminalVersion(String id);

    /**
     * 修改版本信息
     * @param versionLib
     * @return
     */
    public int setTerminalVersion(PlatformInfoClientVersionLib versionLib);

    /**
     * 获取终端版本详情
     * @param id
     * @return
     */
    public PlatformInfoClientVersionLib getId(String id);

    /**
     * 终端列表
     * @param searchKey
     * @param verStatus 1：内测 2.正式
     * @return
     */
    public List<PlatformInfoClientVersionLib> getAll(String searchKey,String verStatus);

    /**
     * 已发布的终端版本列表，包含当前版本和历史版本
     * @param searchKey
     * @param releaseStatus 0：已发布，等于SQL !=0 等于 2，3两个状态值
     * @return
     */
    public List<PlatformInfoClientVersionLib> getReleaseAll(String searchKey,String releaseStatus);





    /**
     * 获取某个版本更新列表
     * @param id 终端版本ID
     * @param searchKey
     */
    public void getVerUpdateInfo(String id,String searchKey);


    /**
     * 获取最新终端版本
     * @param verType 版本类型 0内测，1正式，若内测无返回最新正式版本
     * @return
     */
    public PlatformInfoClientVersionLib getTerminalVersion(String verType);



    /**
     * 获取最新终端版本
     * @param verType 版本类型 0内测，1正式，若内测无返回空
     * @return
     */
    public PlatformInfoClientVersionLib findTerminalVersionByVerType(String verType);

    /**
     * 返回列表数据
     * @param searchKey
     * @param hide
     * @param basePageEntity
     * @return
     */
    public Map getData(String searchKey,String hide, BasePageEntity basePageEntity);


    /**
     * 根据版本号查询
     * @param verNum
     * @return
     */
    PlatformInfoClientVersionLib findByVerNum(String verNum,String verStatus);



}
