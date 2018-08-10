package com.thinkwin.web.security;

import com.thinkwin.TenantUser;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.utils.RequestSourceUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static com.thinkwin.common.log.LocalIpUtil.getIpAddr;

/**
 * @description 一个自定义的filter，
 * 	必须包含authenticationManager,accessDecisionManager,securityMetadataSource三个属性，
我们的所有控制将在这三个类中实现
 */
public class MyFilterSecurityInterceptor extends AbstractSecurityInterceptor
		implements Filter {

	private Logger logger = Logger.getLogger(MyFilterSecurityInterceptor.class);
	//注入userService服务
	private UserService userService;
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	private SaasTenantService saasTenantCoreService;
	public void setSaasTenantCoreService(SaasTenantService saasTenantCoreService) {
		this.saasTenantCoreService = saasTenantCoreService;
	}
	private FilterInvocationSecurityMetadataSource fisMetadataSource;
	public Class<?> getSecureObjectClass() {
		return FilterInvocation.class;
	}

	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return fisMetadataSource;
	}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse) response;
		//判断是否为ajax请求，默认不是
		boolean isAjaxRequest = false;
		if(!StringUtils.isBlank(req.getHeader("x-requested-with")) && req.getHeader("x-requested-with").equals("XMLHttpRequest")){
			isAjaxRequest = true;
		}
		FilterInvocation fi = new FilterInvocation(request, response, chain);
		Collection<ConfigAttribute> attributes = fisMetadataSource.getAttributes(fi);
		String url1 = fi.getFullRequestUrl();
		String token1 = request.getParameter("token");
		logger.info("token token  token :::: "+token1);
		System.out.println("token token  token :::: "+token1);
		if(url1.indexOf("/system") != -1 || url1.indexOf(".jsp") != -1){
			this.publicSuccess(fi,(HttpServletRequest)request,(HttpServletResponse)response,isAjaxRequest);
		}
		if(null != token1 && !"".equals(token1)) {
			TenantUser tenantUser = null;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			try {
				tenantUser = (TenantUser) auth.getPrincipal();
			} catch (Exception e) {
				publicError((HttpServletRequest) request,(HttpServletResponse) response, 1,isAjaxRequest);
				return;
			}
			if(null != tenantUser){
                String ua = ((HttpServletRequest) request).getHeader("user-agent");
                String source = RequestSourceUtil.getRequestSource(ua);
                tenantUser.setDevice(source);
                String source1 = "WEB";
                if(source.equals("iPhone")||source.equals("Android") || source.equals("iPad")){
                    source1 = "APP";
                }
                String tokenn = RedisUtil.get(tenantUser.getTenantId() +"_yunmeeting_"+source1+"_token_"+tenantUser.getUserId());
				if (!token1.equals(tokenn)) {
					HttpServletRequest req1 = (HttpServletRequest)request;
					HttpServletResponse res1 = (HttpServletResponse) response;
					publicError(req1,res1, 1,isAjaxRequest);
					return;
				}
				//获取当前用户所在企业的租户id
				String tenantId = tenantUser.getTenantId();
				if(StringUtils.isBlank(tenantId)){
					publicError((HttpServletRequest) request,(HttpServletResponse) response, 1,isAjaxRequest);
					return;
				}
				TenantContext.setTenantId(tenantId);
				//获取当前用户的主键id
				String userId = tenantUser.getUserId();
				if(StringUtils.isBlank(userId)){
					publicError((HttpServletRequest) request,(HttpServletResponse) response, 1,isAjaxRequest);
					return;
				}
				//获取用户信息
				SysUser sysUser = userService.selectUserByUserId(userId);
				if(null == sysUser){
					publicError((HttpServletRequest) request,(HttpServletResponse) response, 1,isAjaxRequest);
					return;
				}
				//获取用户状态
				Integer userStatus = sysUser.getStatus();
				if(null != userStatus && userStatus == 89){
					if(!isAjaxRequest) {
						goToUrl(fi, "/quitPage.jsp");
					}else{
						goToJsonUrl(fi,"/system/gotoQuitPage","您没有访问权限","8");//离职       2018/06/27需求定为统一提示语
					}
					return;
				}else if(null != userStatus && userStatus == 3){
					if(!isAjaxRequest) {
						goToUrl(fi, "/disablePage.jsp");
					}else{
						goToJsonUrl(fi,"/system/gotoDisablePage","您没有访问权限","9");//禁用     2018/06/27需求定为统一提示语
					}
					return;
				}
				tenantUser.setIp(getIpAddr(req));
				RedisUtil.expire(tenantUser.getTenantId() +"_yunmeeting_"+source1+"_token_"+tenantUser.getUserId(),900);
				if (tenantUser != null) {
					TenantContext.setTenantId(tenantUser.getTenantId());
					TenantUserVo tenantUserVo = new TenantUserVo();
					tenantUserVo.setUserId(userId);
					tenantUserVo.setUserName(tenantUser.getUserName());
					tenantUserVo.setTenantId(tenantUser.getTenantId());
					tenantUserVo.setEmail(tenantUser.getEmail());
					tenantUserVo.setIp(tenantUser.getIp());
					tenantUserVo.setDevice(tenantUser.getDevice());
					TenantContext.setUserInfo(tenantUserVo);
				}
				this.publicSuccess(fi, (HttpServletRequest) request, (HttpServletResponse) response,isAjaxRequest);
				//}
			}
		}else{
			if(!isAjaxRequest) {
				goToUrl(fi, "/access.jsp");
			}else {
				goToJsonUrl(fi,"/system/loginpage","您没有访问权限","10");//2018/06/27需求定为统一提示语
			}
			return;
		}
	}

	public void init(FilterConfig config) throws ServletException {

	}

	public static void goToUrl( Object obj,String url ){
		FilterInvocation fi = (FilterInvocation) obj;
		HttpServletRequest request = fi.getRequest();
		HttpServletResponse response = fi.getResponse();
		String ua = request.getHeader("user-agent");
		if (StringUtils.containsIgnoreCase(ua, "ThinkwinYunmeeting")) {
			goToJsonUrl(request,response,"/system/loginpage","已在其他地方登录！","10");
			return;
		}
		response.setCharacterEncoding("UTF-8");
		String responseUrl = request.getContextPath() + url;
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println("<html>");
		out.println("<script type=\"text/javascript\">");
		out.println("window.open ('" + responseUrl + "','_top');");
		out.println("</script>");
		out.println("</html>");
	}

	public static void goToJsonUrl( Object obj,String url,String msg,String code){
		FilterInvocation fi = (FilterInvocation) obj;
		HttpServletResponse response = fi.getResponse();
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			out.println("{\"code\":\"0\",\"msg\":\"操作失败！\"}");
		}
		out.println("{\"code\":\""+code+"\",\"msg\":\""+msg+"\",\"url\":\"" + url + "\"}");
	}

	public static void goToJsonUrl( HttpServletRequest request,HttpServletResponse response,String url,String msg,String code){
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			out.println("{\"code\":\"0\",\"msg\":\"操作失败！\"}");
		}
		out.println("{\"code\":\""+code+"\",\"msg\":\""+msg+"\",\"url\":\"" + url + "\"}");
	}

	public void setFisMetadataSource(
			FilterInvocationSecurityMetadataSource fisMetadataSource) {
		this.fisMetadataSource = fisMetadataSource;
	}
	public void publicError(HttpServletRequest request,HttpServletResponse response,Integer status,boolean isAjaxRequest) throws IOException {

		if(!isAjaxRequest) {
			String ua = request.getHeader("user-agent");
			if (StringUtils.containsIgnoreCase(ua, "ThinkwinYunmeeting")) {
				goToJsonUrl(request,response,"/system/loginpage","已在其他地方登录！","10");
				return;
			}
			String url = "/access.jsp";
			if(null != status && status == 2){
				logger.info("第二处error.jsp出现处！！！");
				url = "/error.jsp";
			}
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<script type=\"text/javascript\">");
			out.println("window.open ('" + url + "','_top');");
			out.println("</script>");
			out.println("</html>");
		}else {
			if(null != status && status == 2){
				goToJsonUrl(request,response,"/system/gotoErrorPage","请求失败！！！","10");
			}
			goToJsonUrl(request,response,"/system/loginpage","您没有访问权限","10");//2018/06/27需求定为统一提示语
		}
	}
	public void publicSuccess(FilterInvocation fi,HttpServletRequest request,HttpServletResponse response,boolean isAjaxRequest) throws IOException {
//		boolean isAjaxRequest = false;
//		if(!StringUtils.isBlank(request.getHeader("x-requested-with")) && request.getHeader("x-requested-with").equals("XMLHttpRequest")){
//			isAjaxRequest = true;
//		}
		InterceptorStatusToken token = super.beforeInvocation(fi);
		try {
			fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
		} catch (Exception e) {
			logger.error("出现异常:" + e.getMessage());
			if(!isAjaxRequest) {
				String ua = request.getHeader("user-agent");
				if (StringUtils.containsIgnoreCase(ua, "ThinkwinYunmeeting")) {
					goToJsonUrl(request,response,"/system/loginpage","已在其他地方登录！","10");
					return;
				}
				String url = "";
				if ("Access is denied".equals(e.getMessage())) {
					url = request.getContextPath() + "/access.jsp";
				} else {
					logger.info("第三处error.jsp出现处！！！");
					url = request.getContextPath() + "/error.jsp";
				}
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<html>");
				out.println("<script type=\"text/javascript\">");
				out.println("window.open ('" + url + "','_top');");
				out.println("</script>");
				out.println("</html>");
				return;
			}else {
				response.setHeader("Content-type", "application/json;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				if ("Access is denied".equals(e.getMessage())) {
					//goToJsonUrl(request,response,"/system/gotoAccessPage","权限不足！！！","10");
					goToJsonUrl(request,response,"/system/loginpage","您没有访问权限","10");//2018/06/27需求定为统一提示语
				} else {
					logger.info("第三处error.jsp出现处！！！");
					goToJsonUrl(request,response,"/system/gotoErrorPage","您没有访问权限","10");//2018/06/27需求定为统一提示语
				}
			}
		}finally{
			super.afterInvocation(token,null);
		}
	}
}
