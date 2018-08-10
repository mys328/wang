package com.thinkwin.core.service;

import com.thinkwin.common.model.core.SaasUserOauthInfo;

import java.util.List;

/*
 * 类说明：
 * @author lining 2018/1/17
 * @version 1.0
 *
 */
public interface SaasUserOauthInfoService {

    /**
     * 根据UserOauthInfo 查询满足条件的UserOauthInfo数据
     * @param userOauthInfo
     * @returnselectOauthInfoWechat
     */
    public List<SaasUserOauthInfo> findByUserOauthInfo(SaasUserOauthInfo userOauthInfo);

    public boolean save(SaasUserOauthInfo userOauthInfo);

    public boolean update(SaasUserOauthInfo userOauthInfo);

    public boolean delete(SaasUserOauthInfo userOauthInfo);



}
