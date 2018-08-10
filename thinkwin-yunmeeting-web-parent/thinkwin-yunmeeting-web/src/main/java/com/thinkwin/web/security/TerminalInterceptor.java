package com.thinkwin.web.security;

import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.utils.SignCheckUtil;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import org.springframework.context.ApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 终端部分权限拦截功能类
 * User: yinchunlei
 * Date: 2018/5/21
 * Company: thinkwin
 */
public class TerminalInterceptor implements Filter {

    private SaasTenantService saasTenantService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        saasTenantService = applicationContext.getBean(SaasTenantService.class);
        if (saasTenantService == null) {
            throw new ServletException("not init SecurityConfiguration bean in ApplicationContext");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        String url1 = fi.getFullRequestUrl();
        if(url1.indexOf("/terminalClient") != -1){
            String sign = request.getParameter("sign");
            String tenantId = request.getParameter("tenantId");
            Map map = new HashMap<>();
            Enumeration enu=request.getParameterNames();
            while(enu.hasMoreElements()){
                String paraName=(String)enu.nextElement();
                map.put(paraName, new String[]{request.getParameter(paraName)});
            }
            if(StringUtils.isBlank(sign)){
                goToJsonUrl(response,"","sign值不可为空","0");
                return;
            }
            if(StringUtils.isBlank(tenantId)){
                goToJsonUrl(response,"","租户主键id不可为空","0");
                return;
            }
            SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(tenantId);
            if(null == saasTenant){
                goToJsonUrl(response,"","租户信息错误","0");
                return;
            }
            SignCheckUtil scu = new SignCheckUtil();
            scu.setCooperatorSecureKey(saasTenant.getSignCode());
            scu.setParameterMap(map);
            boolean b = scu.checkSecureKey();
            if(!b){
                goToJsonUrl(response,"","sign值信息错误","0");
                return;
            }

            //否则，放行
            TenantContext.setTenantId(tenantId);
            chain.doFilter(request, response);

        }
    }

    @Override
    public void destroy() {}


    public static void goToJsonUrl( ServletResponse response,String url,String msg,String code){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
            out.println("{\"code\":\"0\",\"msg\":\"操作失败！\"}");
        }
        out.println("{\"code\":\""+code+"\",\"msg\":\""+msg+"\",\"url\":\"" + url + "\"}");
    }



}
