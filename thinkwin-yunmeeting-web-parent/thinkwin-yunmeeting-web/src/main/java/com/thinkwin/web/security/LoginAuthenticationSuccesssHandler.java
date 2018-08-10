package com.thinkwin.web.security;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.RequestSourceUtil;
import com.thinkwin.common.utils.ValidatorUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.log.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.thinkwin.common.log.LocalIpUtil.getIpAddr;

/*import com.thinkwin.auth.service.SaasTenantService;*/

/**
 * 验证成功页面，
 * 根据url跳转
 * @author Administrator
 *
 */
public class LoginAuthenticationSuccesssHandler implements
		AuthenticationSuccessHandler {
	private static final Logger log = LoggerFactory.getLogger(LoginAuthenticationSuccesssHandler.class);

	private String defaultUrl;
	private LoginRegisterService loginRegisterService;
	private SysLogService sysLogService;
	private UserService userService;
	private SaasTenantService saasTenantService;

	public void setSaasTenantService(SaasTenantService saasTenantService) {
		this.saasTenantService = saasTenantService;
	}

	public void setLoginRegisterService(LoginRegisterService loginRegisterService) {
		this.loginRegisterService = loginRegisterService;
	}
	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/* (non-Javadoc)
         * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
         */
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) throws IOException,
			ServletException {
		String userName = request.getParameter("j_username");
		Map map = new HashMap<>();
		String tenantType1 = "0";
		if(ValidatorUtil.isMobile(userName)) {
			SaasUserWeb saasUserWeb = new SaasUserWeb();
			saasUserWeb.setAccount(userName);
			SaasUserWeb user = saasTenantService.selectUserLoginInfo(saasUserWeb);
			map.put("Id",user.getId());
			map.put("userId",user.getUserId());
			map.put("tentantId",user.getTenantId());
			map.put("time",user.getLastLoginTime());
		}else{
			SaasUserOauth saasUserOauth = new SaasUserOauth();
			saasUserOauth.setOauthUnionId(userName);
			saasUserOauth.setIsBind(1);
			List<SaasUserOauth> saasUserOauth1 = saasTenantService.selectOAuthLoginInfo(saasUserOauth);
			String newUserId = saasUserOauth1.get(0).getUserId();
            if(StringUtils.isNotBlank(newUserId)){
				SaasUserWeb saasUserWeb = new SaasUserWeb();
				saasUserWeb.setUserId(newUserId);
				SaasUserWeb newUser = saasTenantService.selectUserLoginInfo(saasUserWeb);
				if(null != newUser){
					Date timee = newUser.getLastLoginTime();//最后登录时间
					if(null != timee){
						map.put("time",timee);
					}
					map.put("Id",newUser.getId());
				}
			}
           	map.put("userId",saasUserOauth1.get(0).getUserId());
            map.put("tentantId",saasUserOauth1.get(0).getTenantId());
        }
		Collection<? extends GrantedAuthority> list = authentication.getAuthorities();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		if( list == null || list.size() == 0 ){
			out.println("{\"code\":\"0\",\"url\":\"\"}");
			 return;
		 }
		 //在此处可以判断用户的状态（做业务逻辑）
		if(StringUtils.isNotBlank((String) map.get("Id"))) {
			SaasUserWeb saasUserWeb1 = new SaasUserWeb();
			saasUserWeb1.setId((String) map.get("Id"));
			saasUserWeb1.setLastTimeLogin((Date) map.get("time"));
			saasUserWeb1.setLastLoginTime(new Date());
			saasTenantService.updateUserLoginInfo(saasUserWeb1);
		}
        String userId = (String)map.get("userId");
		String tokenn = CreateUUIdUtil.Uuid();
		String tenantId = (String)map.get("tentantId");
		SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie((String) map.get("tentantId"));
		if(null != saasTenant){
			int status = saasTenant.getStatus();
			if(status == 2){
				out.println("{\"code\":\"3\",\"msg\":\"您没有访问权限\",\"userId\":\"" + userId + "\",\"url\":\"/quitPage.jsp \"}");//quitPage.jsp  您已在当前企业离职（统一使用您没有访问权限）
                return;
            }
			String tenantType = saasTenant.getTenantType();
			if(StringUtils.isNotBlank(tenantType)){
				tenantType1 = tenantType;
			}
		}else{
			//需要考虑是不是会有平台级的log记录
			//sysLogService.createLog(BusinessType.loginOp.toString(), EventType.platform_login.toString(),userName+"登录失败","", Loglevel.error.toString(),ip,source);
			out.println("{\"code\":\"3\",\"msg\":\"您没有访问权限\",\"userId\":\"" + userId + "\",\"url\":\"/quitPage.jsp \"}");//quitPage.jsp
			return;
		}
		//增加操作日志
		String ua = ((HttpServletRequest) request).getHeader("user-agent");
        String source = RequestSourceUtil.getRequestSource(ua);
        String source1 = "WEB";
        if(source.equals("iPhone")||source.equals("Android") || source.equals("iPad")){
        	source1 = "APP";
		}
		RedisUtil.set((String)map.get("tentantId") +"_yunmeeting_"+source1+"_token_"+userId,tokenn);
		RedisUtil.expire((String)map.get("tentantId") +"_yunmeeting_"+source1+"_token_"+userId,900);
        String ip = getIpAddr(request);
		SysUser sysUser = userService.selectUserByUserId(userId);
		String name = "";
		if(null!=sysUser){
			name = sysUser.getUserName();
		}
		Integer status = sysUser.getStatus();
		if(null != status && status == 3){
			sysLogService.createLog(BusinessType.loginOp.toString(), EventType.platform_login.toString(),name+"登录失败","", Loglevel.error.toString(),ip,source);
			out.println("{\"code\":\"2\",\"msg\":\"您没有访问权限\",\"url\":\"/disablePage.jsp \"}");//disablePage.jsp  （2018/6/27 您已被管理员禁用统一使用您没有访问权限）
			return;
		}else if (null != status && status == 89){
			sysLogService.createLog(BusinessType.loginOp.toString(), EventType.platform_login.toString(),name+"登录失败","", Loglevel.error.toString(),ip,source);
			out.println("{\"code\":\"3\",\"msg\":\"您没有访问权限\",\"userId\":\"" + userId + "\",\"url\":\"/quitPage.jsp \"}");//quitPage.jsp
		}else {
			if(null != status && status == 2){
				SysUser sysUserr = new SysUser();
				sysUserr.setId(sysUser.getId());
				sysUserr.setStatus(1);
				userService.updateUserByUserId(sysUserr);
			}
			sysLogService.createLog(BusinessType.loginOp.toString(), EventType.platform_login.toString(), name + "登录成功", "", Loglevel.info.toString(), ip, source);
			//获取当前的sessionid值
			String jsessionId = request.getSession().getId();
			out.println("{\"code\":\"1\",\"msg\":\"查询成功！\",\"url\":\"/index.do\",\"jsessionid\":\"" + jsessionId + "\",\"tenantType\":\"" + tenantType1 + "\",\"userId\":\"" + userId + "\",\"token\":\"" + tokenn + "\"}");
		}
	}
	
	/**
	 * @param defaultUrl the defaultUrl to set
	 */
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
}
