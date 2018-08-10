package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.SaasUserOauthInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SaasUserOauthInfoMapper extends Mapper<SaasUserOauthInfo> {

    /**
     * 方法名：selectOauthInfoWechat</br>
     * 描述：查询微信第三方信息根据第三方用户Id</br>
     * 参数：oauthUserId 第三方用户Id</br>
     * 返回值：SaasUserOauthInfo</br>
     */
    public SaasUserOauthInfo selectOauthInfoWechat(String userOauthId);

    /**
     * 根据UserOauthInfo 查询满足条件的UserOauthInfo数据
     * @param map
     * @returnselectOauthInfoWechat
     */
    public List<SaasUserOauthInfo> findByUserOauthInfo(Map map);

    /**
     * 批量删除
     * @param map
     */
    public void batchDelete(Map map);
}