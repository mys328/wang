package com.thinkwin.auth.localService;


import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.core.SaasUserOauthInfo;
import com.thinkwin.common.model.core.SaasUserTenantMiddle;
import com.thinkwin.common.model.core.SaasUserWeb;

/**
 * 类名: LocalLoginRegisterService </br>
 * 描述: 注册登录本地服务接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/5/26 </br>
 */
public interface LocalLoginRegisterService {

    /**
     * 方法名：selectUserLoginInfo</br>
     * 描述：查询用户名密码表</br>
     * 参数：saasUserWeb 用户名密码实体表  只按照不为空字段查询</br>
     * 返回值：SaasUserWeb  用户名密码表实体对象</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public SaasUserWeb selectUserLoginInfo(SaasUserWeb saasUserWeb);

    /**
     * 方法名：selectOAuthLoginInfo</br>
     * 描述：查询第三方登录表</br>
     * 参数：saasUserOauth 第三方登录表实体 只按照不为空字段查询</br>
     * 返回值：SaasUserOauth  第三方登录表实体对象</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public SaasUserOauth selectOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：saveOAuthLoginInfo</br>
     * 描述：增加第三方登录信息</br>
     * 参数：saasUserOauth  第三方登录表信息实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean saveOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：saveUserLoginInfo</br>
     * 描述：增加用户名和密码登录表信息</br>
     * 参数：saasUserWeb 用户名密码登录表实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean saveUserLoginInfo(SaasUserWeb saasUserWeb);

    /**
     * 方法名：updateUserLoginInfo</br>
     * 描述：修改用户名密码登录表信息</br>
     * 参数：saasUserWeb 用户名密码登录表实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb);

    /**
     * 方法名：updateOAuthLoginInfo</br>
     * 描述：修改第三方登录信息表</br>
     * 参数：saasUserOauth  第三方登录表信息实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean updateOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：saveSysUserTenantMiddle</br>
     * 描述：增加用户和公司中间表</br>
     * 参数：saasUserTenantMiddle</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/27  </br>
     */
    public boolean saveSysUserTenantMiddle(SaasUserTenantMiddle saasUserTenantMiddle);

    /**
     * 方法名：saveOAuthUserInfo</br>
     * 描述：增加第三方登录信息表信息</br>
     * 参数：saasUserOauthInfo 第三方登录信息表实体对象</br>
     * 返回值：</br>
     */
    public boolean saveOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo);
}
