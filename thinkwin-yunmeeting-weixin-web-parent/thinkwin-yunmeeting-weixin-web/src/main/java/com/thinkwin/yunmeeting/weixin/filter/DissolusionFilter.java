package com.thinkwin.yunmeeting.weixin.filter;

import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.redis.RedisUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * 类说明：拦截请求判断企业是否已经解散
 * @author lining 2017/7/11
 * @version 1.0
 *
 */
public class DissolusionFilter implements Filter {

    /**
     * 需要排除的页面
     */
    private String excludedPages;
    private String[] excludedPageArray;

    /**
     * 跳转页面的URL
     */
    private String skipPages;
    private String[] skipPageArray;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        //不走过滤器的接口
        excludedPages = filterConfig.getInitParameter("excludeUrl");
        if (StringUtils.isNotEmpty(excludedPages)) {
            excludedPageArray = excludedPages.split(";");
        }

        //跳转页的接口
        skipPages = filterConfig.getInitParameter("skipPages");
        if (StringUtils.isNotEmpty(skipPages)) {
            skipPageArray = skipPages.split(";");
        }

        return;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //转换类型
        HttpServletRequest req=(HttpServletRequest) request;
        HttpServletResponse res=(HttpServletResponse) response;
        ResponseResult responseResult=new ResponseResult();


        boolean isExcludedPage = false;
        for (String page : excludedPageArray) {//判断是否在过滤url之外
            System.out.println(((HttpServletRequest) request).getServletPath()+"******"+page.trim());
            if(((HttpServletRequest) request).getServletPath().equals(page.trim())){
                isExcludedPage = true;
                break;
            }
        }

        boolean isSkipPage = false;
        for (String page : skipPageArray) {//判断是否在过滤url之外
            if(((HttpServletRequest) request).getServletPath().indexOf(page)!=-1){
                isSkipPage = true;
                if(page.equals("/s")){
                    isExcludedPage = true;
                }
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
            if(isExcludedPage){

                chain.doFilter(request,response);
            }else{
                String tenantId=req.getParameter("tenantId");
                String userAgent= RedisUtil.get("yunmeeting_qiyejiesan_status_"+tenantId);
                if(null!=tenantId && null==userAgent){
                    chain.doFilter(request,response);
                }else {

                    if(isSkipPage){
                        // 处理响应乱码
                        response.setContentType("text/html;charset=utf-8");
                        request.getRequestDispatcher("/dissolusion.jsp").include(request, response);
                    }else {
                        // 处理响应乱码
                        response.setContentType("application/json; charset=utf-8");

                        //将实体对象转换为JSON Object转换
                        PrintWriter writer = response.getWriter();
                        responseResult.setIfSuc(-1);
                        responseResult.setMsg("用户认证失败");
                        JSONObject responseJSONObject = JSONObject.fromObject(responseResult);
                        writer.write(responseJSONObject.toString());
                        writer.flush();
                        writer.close();
                    }


                    }
            }
        }
    }

    @Override
    public void destroy() {

    }
}
