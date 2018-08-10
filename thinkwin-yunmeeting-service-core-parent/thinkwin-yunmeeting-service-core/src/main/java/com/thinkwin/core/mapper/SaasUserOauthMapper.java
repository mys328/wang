package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.SaasUserOauth;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SaasUserOauthMapper extends Mapper<SaasUserOauth> {

    /**
     * 方法名：selectSaasUserOauthByWechat</br>
     * 描述：根据第三方type查询第三方登录表(为微信的查询)</br>
     * 参数：saasUserOauth 第三方实体</br>
     * 返回值：List<SaasUserOauth> 第三方登录表实体</br>
     */
    public List<SaasUserOauth> selectSaasUserOauthByWechat(SaasUserOauth saasUserOauth);
    /**
     * 根据租户id修改saas_user_oauth租户id信息
     * @param tenantId
     * @return
     */
    public boolean updateSaasUserOauthTenantIdByTenantId(String tenantId);

    /**
     * 选择性修改saasUserOauth表格中数据功能
     * @param
     * @return
     */
    public int updateSaasUserOauth(Map map);

}