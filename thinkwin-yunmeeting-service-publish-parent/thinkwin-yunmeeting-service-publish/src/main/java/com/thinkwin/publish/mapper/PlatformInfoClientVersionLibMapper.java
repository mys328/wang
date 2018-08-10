package com.thinkwin.publish.mapper;

import com.thinkwin.common.model.publish.PlatformInfoClientVersionLib;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface PlatformInfoClientVersionLibMapper extends Mapper<PlatformInfoClientVersionLib> {

    //获取最新正式或内测版本
    public List<PlatformInfoClientVersionLib> findByVerStatus(String verStatus);

    //版本号查询
    public List<PlatformInfoClientVersionLib> findByVerNum(Map map);

    //获取终端版本列表
    public List<PlatformInfoClientVersionLib> getList(Map map);

    //获取已发布的终端版本列表
    public List<PlatformInfoClientVersionLib> getReleaseList(Map map);

    PlatformInfoClientVersionLib getId(String id);



}