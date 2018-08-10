package com.thinkwin.auth.mapper.core;

import com.thinkwin.common.model.core.SaasUserOauth;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SaasUserOauthMapper extends Mapper<SaasUserOauth> {
    /**
     * 方法名：selectSaasUserOauthByWechat</br>
     * 描述：根据第三方type查询第三方登录表(为微信的查询)</br>
     * 参数：saasUserOauth 第三方实体</br>
     * 返回值：List<SaasUserOauth> 第三方登录表实体</br>
     */
    public List<SaasUserOauth> selectSaasUserOauthByWechat(SaasUserOauth saasUserOauth);

}