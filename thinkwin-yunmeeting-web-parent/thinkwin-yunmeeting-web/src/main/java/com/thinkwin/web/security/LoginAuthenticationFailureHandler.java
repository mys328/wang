package com.thinkwin.web.security;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.utils.RequestSourceUtil;
import com.thinkwin.log.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.thinkwin.common.log.LocalIpUtil.getIpAddr;

/**
 * 验证失败页面
 * @author Administrator
 *
 */
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private static final Logger log = LoggerFactory.getLogger(LoginAuthenticationFailureHandler.class);
	//@Autowired
	private UserService userService;
	private LoginRegisterService loginRegisterService;
	private SysLogService sysLogService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public void setLoginRegisterService(LoginRegisterService loginRegisterService) {
		this.loginRegisterService = loginRegisterService;
	}
	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}
	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AuthenticationFailureHandler#onAuthenticationFailure(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
	 */
	@SuppressWarnings("deprecation")
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authentication)
			throws IOException, ServletException {
		//String userName = (String) authentication.getAuthentication().getPrincipal();
//		SysUserWeb sysUserWeb = new SysUserWeb();
//		sysUserWeb.setUserName(userName);
//		SysUserWeb user = loginRegisterService.selectUserLoginInfo(sysUserWeb);
//		Integer param = 0;
	/*	if( user.getLockStatus() == 1 ){
			param = 2;//用户为锁定状态
		}else{
			param = 1;//密码错误
		}*/
//		String url = request.getContextPath() + "/login.jsp";
//		if( param == 1 ){
//			url = url + "?error=1";
//		}else if( param == 2 ){
//			url = url + "?error=2";
//		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		//以下注释部分为from提交时方法
/*		out.println("<html>");
		out.println("<script type=\"text/javascript\">");
		//out.println("window.open ('" + url + "','_top')");
		out.println("window.location.href ='/system/loginpage?flag=1'");
		out.println("</script>");
		out.println("</html>");*/

		String ua = ((HttpServletRequest) request).getHeader("user-agent");
//		String source = "PC";
//		if (StringUtils.containsIgnoreCase(ua, "micromessenger")) {
//			source = "Weixin";
//		}
//		else if(StringUtils.containsIgnoreCase(ua, "iPhone")){
//			source = "iPhone";
//		}
//		else if(StringUtils.containsIgnoreCase(ua, "Android")){
//			source = "Android";
//		}
		String source = RequestSourceUtil.getRequestSource(ua);
//		String ip = request.getRemoteAddr();
		String ip = getIpAddr(request);
		//增加操作日志
		sysLogService.createLog(BusinessType.loginOp.toString(), EventType.platform_login.toString(),"登录失败","", Loglevel.error.toString(),ip,source);
		out.println("{\"code\":\"0\",\"msg\":\"用户名与密码不匹配\",\"url\":\"\"}");
		//return;
	}

}
