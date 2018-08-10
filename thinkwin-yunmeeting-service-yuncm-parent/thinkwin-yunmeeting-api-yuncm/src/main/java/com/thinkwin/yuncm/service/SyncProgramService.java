package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.InfoProgramComponentMiddle;
import com.thinkwin.common.vo.SyncProVo;

import java.util.List;
import java.util.Map;

/*
 * 类说明：同步节目服务
 * @author lining 2018/5/9
 * @version 1.0
 *
 */

public interface SyncProgramService {

    /**
     * 获取租户节目版本号
     * @return
     */
    public Map<String,String> getProgramVersion();

    /**
     * 设置租户节目版本号
     * @param tenantProgramReleaseVer
     * @param tenantProgramAlphaVer
     */
    public void setProgramVersion(String tenantProgramReleaseVer,String tenantProgramAlphaVer);

    /**
     * 设置租户定制节目版本号
     * @param tenantDZProgramReleaseVer
     * @param tenantDZProgramAlphaVer
     */
    public void setDZProgramVersion(String tenantDZProgramReleaseVer,String tenantDZProgramAlphaVer);

    /**
     * 查检版本是否要更新
     * 1000：无须升级;
     * 1001：正式租户需升级正式版本节目
     * 1002：内测租户需升级内测节目
     * 1003：内测租户即需升级内测也需升级正式
     */
    public SyncProVo checkVersion(String tenantId, int tenantType);


    /**
     * 同步节目
     * @param tenantId
     * @param tenantType
     * @param code
     * @return
     */
    public SyncProVo updateProgramVer(String tenantId,int tenantType,int code) ;


    /**
     * 内测用户切换正式用户，删除内测节目
     * @param userType
     */
    public void delAlphaProgram(String userType);

    /**
     * 查看节目组件类型
     * @param programId 节目id
     * @return
     */
    public List<InfoProgramComponentMiddle> getInfoProgramComponentMiddleList(String programId);
}
