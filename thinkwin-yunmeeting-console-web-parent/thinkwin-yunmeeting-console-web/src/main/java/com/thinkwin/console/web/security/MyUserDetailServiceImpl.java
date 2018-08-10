package com.thinkwin.console.web.security;

import com.thinkwin.TenantUser;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.console.service.SaasRoleService;
import com.thinkwin.console.service.SaasUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * 第三步 登录权限验证
 *
 * @author Administrator
 */
public class MyUserDetailServiceImpl implements UserDetailsService {

    private SaasUserService saasUserService;

    private SaasRoleService saasRoleService;

    public void setSaasUserService(SaasUserService saasUserService) {
        this.saasUserService = saasUserService;
    }

    public void setSaasRoleService(SaasRoleService saasRoleService) {
        this.saasRoleService = saasRoleService;
    }

    /**
     * 点击登录后会调用此方法，
     * 获取用户的权限
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //System.out.println("---------MyUserDetailServiceImpl:loadUserByUsername------正在加载用户名和密码，用户名为："+username);

        boolean enabled = true;                //用户帐号是否已启用
        boolean accountNonExpired = true;        //是否过期
        boolean credentialsNonExpired = true;   //用户凭证是否已经过期
        boolean accountNonLocked = true; //是否锁定
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        Map map = new HashMap<>();

        SaasUser saasUser = new SaasUser();
//        saasUser.setPhoneNumber(username);
        saasUser.setEmail(username);
        SaasUser user1 = saasUserService.selectSaasUser(saasUser);
        if (null != user1) {
            map.put("tenantId", user1.getTenantId());
            map.put("userId", user1.getId());
            map.put("password", user1.getPassword());
            map.put("userName", user1.getUserName());
        }

        //如果你使用资源和权限配置在xml文件中，如：<intercept-url pattern="/user/admin" access="hasRole('ROLE_ADMIN')"/>；
        //并且也不想用数据库存储，所有用户都具有相同的权限的话，你可以手动保存角色(如：预订网站)。
        //authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //如果用户为空就不执行加载用户角色和权限的功能
        if (null != map) {
            List<SaasRole> roles = saasRoleService.findUserRolesByUserId((String) map.get("userId"));
            if (null != roles && roles.size() > 0) {
                for (SaasRole role : roles) {
                    GrantedAuthority ga = new SimpleGrantedAuthority(role.getRoleName());
                    authorities.add(ga);
                }
            }
        } else {
            enabled = false;
        }


        TenantUser tenantUser = new TenantUser((String) map.get("userName"), (String) map.get("password"), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, (String) map.get("tenantId"));

        tenantUser.setUserName(map.get("userName").toString());
        tenantUser.setUserId(map.get("userId").toString());
        return tenantUser;
    }

}
