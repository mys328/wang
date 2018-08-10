package com.thinkwin.console.web.security;

import com.thinkwin.common.utils.ResourceMap;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 第五步 @description 资源源数据定义，将所有的资源和权限对应关系建立起来，即定义某一资源可以被哪些角色访问
 */
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private AntPathMatcher urlMatcher = new AntPathMatcher();

    public MySecurityMetadataSource() {
        loadResourcesDefine();
    }

    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    /**
     * 加载所有资源和权限的关系
     * 资源表相关的
     */
    //@PostConstruct
    public void loadResourcesDefine() {

    }

    /*
     * 根据请求的资源地址，获取它所拥有的权限
     * 返回请求的资源需要的权限
     */
    public Collection<ConfigAttribute> getAttributes(Object obj) throws IllegalArgumentException {
        //获取请求的url方法
        String url = ((FilterInvocation) obj).getRequestUrl();
        Collection<ConfigAttribute> urlSet = new HashSet<ConfigAttribute>();
        if (url.indexOf("&") != -1) {
            url = url.substring(0, url.indexOf("&"));
        }
        //获取请求的url资源
        String methodUrl = "";
        if (url.indexOf("?") != -1) {
            methodUrl = url.substring(0, url.indexOf("?"));
        }
        Iterator<String> it = ResourceMap.ketSetIterator();
        while (it.hasNext()) {
            String _url = it.next();
            //如果该拥有url资源
            if (!"".equals(methodUrl) && urlMatcher.match(methodUrl, _url)) {
                Iterator<ConfigAttribute> urlIt = ResourceMap.get(_url).iterator();
                while (urlIt.hasNext()) {
                    urlSet.add(urlIt.next());
                }
                //如果拥有方法资源
            } else if (urlMatcher.match(url, _url)) {
                Iterator<ConfigAttribute> urlIt = ResourceMap.get(_url).iterator();
                while (urlIt.hasNext()) {
                    urlSet.add(urlIt.next());
                }
            }
        }
        //返回所有的角色
        if (urlSet != null && urlSet.size() > 0) {
            return urlSet;
        }
        return null;
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }

}
