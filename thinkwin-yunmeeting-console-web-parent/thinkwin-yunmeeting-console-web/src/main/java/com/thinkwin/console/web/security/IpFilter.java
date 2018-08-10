package com.thinkwin.console.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 功能：对请求的IP地址进行过滤
 */
public class IpFilter implements Filter {


    private final static Logger logger = LoggerFactory.getLogger(IpFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //String requestUrl=servletRequest.getScheme()+"://"+ servletRequest.getServerName();//+request.getRequestURI();
        String IpWhiteList ="210.12.69.101";
        String domainWhiteList ="console.yunmeetings.com";
        String requestUrl= servletRequest.getServerName();//+request.getRequestURI();
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("text/html;charset=utf-8");
        String remoteIP = getIpAddr((HttpServletRequest)servletRequest);
        logger.debug("域名="+requestUrl);
        logger.debug("---------------------->>>获取到的IP:"+remoteIP);
        logger.debug("---------------------->>>白名单IP:"+IpWhiteList);

        if(domainWhiteList.equals(requestUrl)){//如果是正式域名访问
            if(IpWhiteList.equals(remoteIP)){
                filterChain.doFilter(servletRequest, servletResponse);
            }else{
                servletResponse.getWriter().println("您无权限访问该页面!");
            }
        }else {//否则不限制
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }


    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public void destroy() {

    }
}
