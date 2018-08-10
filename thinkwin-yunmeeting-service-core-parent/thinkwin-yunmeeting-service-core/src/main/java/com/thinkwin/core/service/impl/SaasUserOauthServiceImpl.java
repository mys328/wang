package com.thinkwin.core.service.impl;

import com.thinkwin.core.mapper.SaasUserOauthMapper;
import com.thinkwin.core.service.SaasUserOauthService;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/*
 * 类说明：
 * @author lining 2018/1/17
 * @version 1.0
 *
 */
@Service("saasUserOauthService")
public class SaasUserOauthServiceImpl implements SaasUserOauthService {

    @Autowired
    public SaasUserOauthMapper saasUserOauthMapper;

    @Override
    public boolean save(SaasUserOauth userOauth) {
        String nickName = userOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)){
            try {
                userOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthMapper.insertSelective(userOauth);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(SaasUserOauth userOauth) {
        String nickName = userOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)){
            try {
                userOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int n=this.saasUserOauthMapper.updateByPrimaryKey(userOauth);
        return (n>=0)?true:false;
    }

    @Override
    public boolean delete(SaasUserOauth userOauth) {
        int n=this.saasUserOauthMapper.delete(userOauth);
        return (n>=0)?true:false;
    }
}
