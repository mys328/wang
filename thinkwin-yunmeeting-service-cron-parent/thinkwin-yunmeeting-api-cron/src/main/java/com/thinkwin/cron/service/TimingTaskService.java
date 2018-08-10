package com.thinkwin.cron.service;

/**
 * Created by Administrator on 2018/2/2 0002.
 */
public interface TimingTaskService {

    /**
     * 删除租户所有文件信息
     * @param param
     */
    public String deleteAllTenantFile(String param);

    /**
     * 处理数据库删除时未能删除的物理库的定时删除接口功能
     */
    public void delDBDate();
}
