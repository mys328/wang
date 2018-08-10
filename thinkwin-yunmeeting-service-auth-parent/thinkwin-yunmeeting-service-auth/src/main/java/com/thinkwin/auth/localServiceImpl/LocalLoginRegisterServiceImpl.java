package com.thinkwin.auth.localServiceImpl;

import com.thinkwin.auth.localService.LocalLoginRegisterService;
import com.thinkwin.auth.mapper.core.*;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.core.SaasUserOauthInfo;
import com.thinkwin.common.model.core.SaasUserTenantMiddle;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 类名: LoginRegisterServiceImpl </br>
 * 描述: 本地登录注册接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/5/26 </br>
 */
@Service("localLoginRegisterService")
public class LocalLoginRegisterServiceImpl implements LocalLoginRegisterService {

    @Autowired
    SaasUserApiMapper saasUserApiMapper;
    @Autowired
    SaasUserOauthMapper saasUserOauthMapper;
    @Autowired
    SaasUserWebMapper saasUserWebMapper;
    @Autowired
    SaasUserTenantMiddleMapper saasUserTenantMiddleMapper;
    @Autowired
    SaasUserOauthInfoMapper saasUserOauthInfoMapper;

    @Override
    public SaasUserWeb selectUserLoginInfo(SaasUserWeb saasUserWeb) {
        List<SaasUserWeb> sysUserWebs = this.saasUserWebMapper.select(saasUserWeb);
        //判断sysUserWebs是否为空
        if (null != sysUserWebs && sysUserWebs.size() > 0) {
            //如果sysUserWebs为空则抛出手机号和密码不正确异常
            //throw new PhoneNumberPasswordException();
            return sysUserWebs.get(0);
        }
        return null;
    }

    @Override
    public SaasUserOauth selectOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        List<SaasUserOauth> sysUserOauths = saasUserOauthMapper.select(saasUserOauth);
        if (null != sysUserOauths && sysUserOauths.size() > 0) {
            String wechat = sysUserOauths.get(0).getOauthUserName();
            if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                try {
                    wechat = StringUtil.parseHexString(wechat);
                    sysUserOauths.get(0).setOauthUserName(wechat);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return sysUserOauths.get(0);
        }
        return null;
    }

    @Override
    public boolean saveOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthMapper.insertSelective(saasUserOauth);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveUserLoginInfo(SaasUserWeb saasUserWeb) {
        int i = this.saasUserWebMapper.insertSelective(saasUserWeb);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb) {
        int i = this.saasUserWebMapper.updateByPrimaryKeySelective(saasUserWeb);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthMapper.updateByPrimaryKeySelective(saasUserOauth);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveSysUserTenantMiddle(SaasUserTenantMiddle saasUserTenantMiddle) {
        int i = this.saasUserTenantMiddleMapper.insertSelective(saasUserTenantMiddle);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo) {
        String nickName = saasUserOauthInfo.getNickName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauthInfo.setNickName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthInfoMapper.insertSelective(saasUserOauthInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }
}
