package com.thinkwin.yunmeeting.weixin.filter;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * 类说明：判断是否微信浏览器及过滤的URL
 * @author lining 2017/7/11
 * @version 1.0
 *
 */
public class BrowserFilter implements Filter {

    /**
     * 需要排除的页面
     */
    private String excludedPages;
    private String[] excludedPageArray;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        excludedPages = filterConfig.getInitParameter("excludeUrl");
        if (StringUtils.isNotEmpty(excludedPages)) {
            excludedPageArray = excludedPages.split(";");
        }
        return;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //转换类型
        HttpServletRequest req=(HttpServletRequest) request;
        HttpServletResponse res=(HttpServletResponse) response;

        String userAgent=req.getHeader("User-Agent");
        boolean isExcludedPage = false;

        for (String page : excludedPageArray) {//判断是否在过滤url之外
            if(((HttpServletRequest) request).getServletPath().equals(page.trim())){
                isExcludedPage = true;
                break;
            }
        }

        //获取请求界面的路径
        String a=req.getRequestURI();
        if(a.contains(".css") || a.contains(".js") || a.contains(".png")|| a.contains(".jpg")|| a.contains(".ico")
                || a.contains(".eot")|| a.contains(".svg")|| a.contains(".ttf")|| a.contains(".woff")){
            //如果发现是css或者js文件，直接放行
            chain.doFilter(request, response);
        }else{  //在else中放对网页过滤的代码

            //微信内置浏览器
            if(userAgent.contains("MicroMessenger") || isExcludedPage){

                chain.doFilter(request,response);
            }else{

                // 处理响应乱码
                response.setContentType("text/html;charset=utf-8");
                request.getRequestDispatcher("/error.jsp").include(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
