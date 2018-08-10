package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.SysSetting;

/*
 * 类说明：
 * @author lining 2018/5/15
 * @version 1.0
 *
 */
public interface SysSetingService {

    public SysSetting findByKey(String key);

    public String get(String key);

    public void set(String id,String key, String value);

    public boolean add(SysSetting sysSetting);

    public boolean update(SysSetting sysSetting);
}
