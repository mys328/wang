package com.thinkwin.yuncm.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.InfoBootDownLog;

import java.util.Map;

/**
 * 开关机任务日志
 * User:wangxilei
 * Date:2018/5/10
 * Company:thinkwin
 */
public interface InfoBootDownLogService {
    /**
     * 查看日志
     * @param word 搜索关键字（任务名称/终端名称）
     * @param state 发送状态（1成功0失败，默认全部）
     * @param taskId 任务Id
     * @param startTime 搜索开始时间
     * @param endTime 搜索结束时间
     * @return
     */
    PageInfo getLogs(BasePageEntity page,String word, String state, String taskId, String startTime, String endTime);

    Map<String,Integer> getLogsCount(BasePageEntity page, String word, String state, String taskId, String startTime, String endTime);
    /**
     * 添加计划开关机日志
     * @param log
     * @return
     */
    boolean insertLog(InfoBootDownLog log);

    /**
     * 清除任务日志
     * @param taskId
     * @return
     */
    boolean clearLog(String taskId);
}
