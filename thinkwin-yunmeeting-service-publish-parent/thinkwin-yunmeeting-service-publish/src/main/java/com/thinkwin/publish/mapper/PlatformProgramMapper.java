package com.thinkwin.publish.mapper;

import com.thinkwin.common.model.publish.PlatformProgram;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface PlatformProgramMapper extends Mapper<PlatformProgram> {

    /**
     * 批量删除节目功能
     * @param map
     * @return
     */
    public int delPlatformPrograms(Map map);

    /**
     * 节目状态修改功能
     * @param map
     * @return
     */
    int updateProgramStatusByStatus(Map map);

    /**
     * 条件查询节目功能
     * @param map
     * @return
     */
    List<PlatformProgram> getPlatformProgramBySeachKey(Map map);


    /**
     * 修改节目的状态和版本号信息接口
     * @param map
     * @return
     */
    boolean updateProgramStatusByTypeAndTenantId(Map map);

    /**
     * 根据标签查询节目
     * @param map
     * @return
     */
    List<PlatformProgram> selectProgramByType(Map map);

    /**
     * 查询非定制节目的数量
     * @return
     */
    int selectProgramNumByType();


}