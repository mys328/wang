package com.thinkwin.web.security;

import com.thinkwin.TenantUser;
import com.thinkwin.auth.service.PermissionService;
import com.thinkwin.auth.service.RolePermissionService;
import com.thinkwin.auth.service.UserRoleService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.SysPermission;
import com.thinkwin.common.model.db.SysRole;
import com.thinkwin.common.model.db.SysRolePermission;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.utils.ValidatorUtil;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * 权限验证
 * @author Administrator
 *
 */
public class MyUserDetailServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(MyUserDetailServiceImpl.class);
    private UserService userService;
    private RolePermissionService rolePermissionService;
    private PermissionService permissionService;
	private UserRoleService userRoleService;

	private SaasTenantService loginRegisterService;
    private SaasTenantService saasTenantService;

    public void setSaasTenantService(SaasTenantService saasTenantService) {
        this.saasTenantService = saasTenantService;
    }
	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public void setRolePermissionService(RolePermissionService rolePermissionService) {
		this.rolePermissionService = rolePermissionService;
	}
	public void setLoginRegisterService(SaasTenantService loginRegisterService) {
		this.loginRegisterService = loginRegisterService;
	}
	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	/**
	 * 点击登录后会调用此方法，
	 * 获取用户的权限
	 */
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		boolean enabled = true;                //用户帐号是否已启用
        boolean accountNonExpired = true;        //是否过期
        boolean credentialsNonExpired = true;   //用户凭证是否已经过期
        boolean accountNonLocked = true; //是否锁定
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		Map map = new HashMap<>();
       // TenantContext.setTenantId("0");
		//判断手机号格式是否正确
		if(ValidatorUtil.isMobile(username)) {
            SaasUserWeb saasUserWeb = new SaasUserWeb();
            saasUserWeb.setAccount(username);
           SaasUserWeb user1 = loginRegisterService.selectUserLoginInfo(saasUserWeb);
           if(null != user1){
               map.put("tenantId",user1.getTenantId());
               map.put("userId",user1.getUserId());
               map.put("password",user1.getPassword());
               map.put("account",user1.getAccount());
           }
        }else{
            SaasUserOauth saasUserOauth = new SaasUserOauth();
            saasUserOauth.setOauthUnionId(username);
            saasUserOauth.setIsBind(1);
            List<SaasUserOauth> saasUserOauth1 = loginRegisterService.selectOAuthLoginInfo(saasUserOauth);
           if(null != saasUserOauth1){
               map.put("tenantId",saasUserOauth1.get(0).getTenantId());
               map.put("userId",saasUserOauth1.get(0).getUserId());
               map.put("password",saasUserOauth1.get(0).getPassword());
               map.put("account",saasUserOauth1.get(0).getOauthUnionId());
           }
        }
            if(null != map) {
		        String ttentantId = (String)map.get("tenantId");
                if(StringUtils.isNotBlank(ttentantId)) {
                    SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(ttentantId);
                    if(null!=saasTenant&&saasTenant.getStatus()!= 2){
                        TenantContext.setTenantId((String) map.get("tenantId"));
                        SysUser sysUser = userService.selectUserByUserId((String) map.get("userId"));
                    if (sysUser == null) {
                        enabled = false;
                    }
                    List<SysRole> roles = userRoleService.findUserRolesByUserId((String) map.get("userId"));
                    if (null != roles && roles.size() > 0) {
                        for (SysRole role : roles) {
                            GrantedAuthority ga = new SimpleGrantedAuthority(role.getRoleName());
                            authorities.add(ga);
                            List<SysRolePermission> perms = rolePermissionService.findPermsByRoleId(role.getRoleId());
                            if (null != perms && perms.size() > 0) {
                                for (SysRolePermission rolePerm : perms) {
                                    SysPermission perm = permissionService.getPermissionById(rolePerm.getPermissionId());
                                    GrantedAuthority perga = new SimpleGrantedAuthority(perm.getUrl());
                                    authorities.add(perga);
                                }
                            }
                        }
                    }
                    String userName = null;
                    if (null != sysUser) {
                        userName = sysUser.getUserName();
                    }
                    map.put("userName", userName);
                }else{
                        GrantedAuthority ga1 = new SimpleGrantedAuthority("普通员工");
                        authorities.add(ga1);
                    }
            }
			}else{
                enabled = false;
			}
		TenantUser tenantUser = new TenantUser(
                (String)map.get("account"),
                (String)map.get("password"),
				enabled, 
				accountNonExpired, 
				credentialsNonExpired, 
				accountNonLocked, 
				authorities,
                (String)map.get("tenantId")
                );
		tenantUser.setEmail("");
        Object userName11 = map.get("userName");
        if(null != userName11) {
            tenantUser.setUserName(userName11.toString());
        }
		tenantUser.setUserId(map.get("userId").toString());
        return tenantUser;
	}

}
