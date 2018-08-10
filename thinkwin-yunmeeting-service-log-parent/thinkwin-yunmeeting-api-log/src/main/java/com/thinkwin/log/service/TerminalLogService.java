package com.thinkwin.log.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.log.TerminalLog;

/**
 * User:wangxilei
 * Date:2018/5/31
 * Company:thinkwin
 */
public interface TerminalLogService {

    /**
     * 添加终端日志
     * @param log
     * @return
     */
    void addTerminalLog(TerminalLog log);

    /**
     * 获取所有终端错误日志
     * @param word
     * @param eventType
     * @return
     */
    PageInfo getAllTerminalLog(String tenantId, String word, String eventType, BasePageEntity page);

    /**
     * 查看日志详情
     * @param logId
     * @return
     */
    String getTerminalLogResult(String logId);

    /**
     * 删除终端日志
     * @param log
     */
    void deleteTerminalLog(TerminalLog log);
}
