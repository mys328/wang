package com.thinkwin.console.web.security;

import com.thinkwin.TenantUser;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.service.TenantContext;
import org.apache.log4j.Logger;
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

/**
 * 第二步 @description 一个自定义的filter，
 * 必须包含authenticationManager,accessDecisionManager,securityMetadataSource三个属性，
 * 我们的所有控制将在这三个类中实现
 */
public class MyFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    private Logger logger = Logger.getLogger(MyFilterSecurityInterceptor.class);

    private FilterInvocationSecurityMetadataSource fisMetadataSource;

    /* (non-Javadoc)
     * @see org.springframework.security.access.intercept.AbstractSecurityInterceptor#getSecureObjectClass()
     */
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return fisMetadataSource;
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //super.beforeInvocation(fi);源码  
        //1.获取请求资源的权限
        //执行Collection<ConfigAttribute> attributes = SecurityMetadataSource.getAttributes(object);
        //2.是否拥有权限
        //this.accessDecisionManager.decide(authenticated, object, attributes);
        //System.out.println("------------MyFilterSecurityInterceptor.doFilter()-----------开始拦截器了....");
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        String url1 = fi.getFullRequestUrl();
        //System.out.println("request url    :::" + url1);
        String token1 = request.getParameter("token");
        //System.out.println("token token :" + token1);
        if (url1.indexOf("/system") != -1 || url1.indexOf(".jsp") != -1) {
            this.publicSuccess(fi, (HttpServletRequest) request, (HttpServletResponse) response);
        }
        if (null != token1 && !"".equals(token1)) {
            TenantUser tenantUser = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            try {
                tenantUser = (TenantUser) auth.getPrincipal();
            } catch (Exception e) {
                publicError((HttpServletResponse) response, 1);
                return;
            }

            String tokenn = RedisUtil.get("platform_console_web_token_" + token1);
            if (!token1.equals(tokenn)) {
                publicError((HttpServletResponse) response, 1);
                return;
            } else {
                RedisUtil.expire("platform_console_web_token_" + tokenn, 1200);
                if (tenantUser != null) {
                    TenantContext.setTenantId(tenantUser.getTenantId());
                    TenantUserVo tenantUserVo = new TenantUserVo();
                    tenantUserVo.setUserId(tenantUser.getUserId());
                    tenantUserVo.setUserName(tenantUser.getUserName());
                    tenantUserVo.setIp(tenantUser.getIp());
                    tenantUserVo.setDevice(tenantUser.getDevice());
                    TenantContext.setUserInfo(tenantUserVo);
                }
                this.publicSuccess(fi, (HttpServletRequest) request, (HttpServletResponse) response);
            }

        } else {
            publicError((HttpServletResponse) response, 2);
            return;
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

    public static void goToUrl(Object obj, String url) {
        FilterInvocation fi = (FilterInvocation) obj;
        HttpServletRequest request = fi.getRequest();
        HttpServletResponse response = fi.getResponse();
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

    public void setFisMetadataSource(FilterInvocationSecurityMetadataSource fisMetadataSource) {
        this.fisMetadataSource = fisMetadataSource;
    }

    public void publicError(HttpServletResponse response, Integer status) throws IOException {
        String url = "/access.jsp";
        if (null != status && status == 2) {
            url = "/error.jsp";
        }
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<script type=\"text/javascript\">");
        out.println("window.open ('" + url + "','_top');");
        out.println("</script>");
        out.println("</html>");
    }

    public void publicSuccess(FilterInvocation fi, HttpServletRequest request, HttpServletResponse response) throws IOException {
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } catch (Exception e) {
            logger.error("出现异常:" + e.getMessage());
            String url = "";
            if ("Access is denied".equals(e.getMessage())) {
                url = request.getContextPath() + "/access.jsp";
            } else {
                url = request.getContextPath() + "/error.jsp";
            }
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<script type=\"text/javascript\">");
            out.println("window.open ('" + url + "','_top');");
            out.println("</script>");
            out.println("</html>");
            return;
        } finally {
            super.afterInvocation(token, null);
        }
    }
}
