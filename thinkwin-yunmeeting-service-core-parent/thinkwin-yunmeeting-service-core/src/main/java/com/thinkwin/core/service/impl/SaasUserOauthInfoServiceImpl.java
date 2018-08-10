package com.thinkwin.core.service.impl;

import com.thinkwin.core.mapper.SaasUserOauthInfoMapper;
import com.thinkwin.core.service.SaasUserOauthInfoService;
import com.thinkwin.common.model.core.SaasUserOauthInfo;
import com.thinkwin.common.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：
 * @author lining 2018/1/17
 * @version 1.0
 *
 */
@Service("saasUserOauthInfoService")
public class SaasUserOauthInfoServiceImpl implements SaasUserOauthInfoService {

    @Autowired
    SaasUserOauthInfoMapper saasUserOauthInfoMapper;

    /**
     * 根据UserOauthInfo 查询满足条件的UserOauthInfo数据
     *
     * @param userOauthInfo
     * @returnselectOauthInfoWechat
     */
    @Override
    public List<SaasUserOauthInfo> findByUserOauthInfo(SaasUserOauthInfo userOauthInfo) {

        Map<String,Object> map=new HashMap<>();
        map.put("openId",userOauthInfo.getOauthOpenId());
        map.put("unionId",userOauthInfo.getOauthUnionId());
        map.put("oauthTye",userOauthInfo.getOauthType());

        List<SaasUserOauthInfo> list=this.saasUserOauthInfoMapper.findByUserOauthInfo(map);

        return list;
    }

    @Override
    public boolean save(SaasUserOauthInfo userOauthInfo) {
        String nickName = userOauthInfo.getNickName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                userOauthInfo.setNickName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthInfoMapper.insertSelective(userOauthInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(SaasUserOauthInfo userOauthInfo) {
        Map<String,Object> map=new HashMap<>();
        map.put("openId",userOauthInfo.getOauthOpenId());
        map.put("unionId",userOauthInfo.getOauthUnionId());
        map.put("oauthTye",userOauthInfo.getOauthType());
        this.saasUserOauthInfoMapper.batchDelete(map);
        return true;
    }

    @Override
    public boolean update(SaasUserOauthInfo userOauthInfo) {
        return false;
    }
}
