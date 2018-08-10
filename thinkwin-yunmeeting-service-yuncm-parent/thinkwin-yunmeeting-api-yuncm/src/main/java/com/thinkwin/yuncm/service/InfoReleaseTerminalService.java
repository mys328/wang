package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.InfoReleaseTerminal;

import java.util.List;

/**
 * 类名: InfoReleaseTerminalService </br>
 * 描述:信发终端数据表内部接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/4/28 </br>
 */
public interface InfoReleaseTerminalService {

    /**
     * 方法名：insertInfoReleaseTerminal</br>
     * 描述：根据信发终端表实体增加信发终端表信息</br>
     * 参数：[infoReleaseTerminal]</br>
     * 返回值：boolean</br>
     */
    public boolean insertInfoReleaseTerminal(InfoReleaseTerminal infoReleaseTerminal);

    /**
     * 方法名：updateInfoReleaseTerminal</br>
     * 描述：根据信发终端表实体修改信发终端表</br>
     * 参数：[infoReleaseTerminal]</br>
     * 返回值：boolean</br>
     */
    public boolean updateInfoReleaseTerminal(InfoReleaseTerminal infoReleaseTerminal);

    /**
     * 方法名：selectInfoReleaseTerminalByTenantId</br>
     * 描述：根据租户Id查询信发终端表</br>
     * 参数：[tenantId]</br>
     * 返回值：com.thinkwin.common.model.db.InfoReleaseTerminal</br>
     */
    public List<InfoReleaseTerminal> selectInfoReleaseTerminalByTenantId(String tenantId);

    /**
     * 方法名：selectInfoReleaseTerminalById</br>
     * 描述：根据终端Id查询信发终端表信息</br>
     * 参数：[hardwareId]</br>
     * 返回值：com.thinkwin.common.model.db.InfoReleaseTerminal</br>
     */
    public InfoReleaseTerminal selectInfoReleaseTerminalById(String terminalId);

    /**
     * 方法名：selectInfoReleaseTerminalByHardwareId</br>
     * 描述：根据终端唯一标识查询信发终端表信息</br>
     * 参数：[hardwareId]</br>
     * 返回值：com.thinkwin.common.model.db.InfoReleaseTerminal</br>
     */
    public InfoReleaseTerminal selectInfoReleaseTerminalByHardwareId(String hardwareId);

    /**
     * 方法名：selectInfoReleaseTerminalByMettingRoomId</br>
     * 描述：</br>
     * 参数：[meetingRoomId]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.InfoReleaseTerminal></br>
     */
    public List<InfoReleaseTerminal> selectInfoReleaseTerminalByMettingRoomId(String meetingRoomId);

    /**
     * 方法名：selectInfoReleaseTerminals</br>
     * 描述：根据信发终端实体查询查询信发终端表</br>
     * 参数：[infoReleaseTerminal]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.InfoReleaseTerminal></br>
     */
    public List<InfoReleaseTerminal> selectInfoReleaseTerminals(InfoReleaseTerminal infoReleaseTerminal);

    /**
     * 删除终端绑定的会议室
     * @param roomId
     */
    public void updateInfoReleaseTerminalByRoomId(String roomId);

    /**
     * 方法名：delectInfoReleaseTerminal</br>
     * 描述：</br>
     * 参数：[terminalId]</br>
     * 返回值：boolean</br>
     */
    public boolean delectInfoReleaseTerminal(String terminalId);

}
