package com.thinkwin.core.service;

import com.thinkwin.common.model.core.SaasUserOauth;

/*
 * 类说明：SaasUserOauth
 * @author lining 2018/1/17
 * @version 1.0
 *
 */
public interface SaasUserOauthService {

    public boolean save(SaasUserOauth userOauth);

    public boolean update(SaasUserOauth userOauth);

    public boolean delete(SaasUserOauth userOauth);

}
