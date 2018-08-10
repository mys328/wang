package com.thinkwin.common.utils;

import org.apache.commons.lang.StringUtils;

/**
 * User: yinchunlei
 * Date: 2018/6/21
 * Company: thinkwin
 */
public class RequestSourceUtil {
    public static String getRequestSource(String ua){
        String source = "PC";
        if (StringUtils.containsIgnoreCase(ua, "micromessenger")) {
            source = "Weixin";
        }
        else if(StringUtils.containsIgnoreCase(ua, "iPhone")){
            source = "iPhone";
        }
        else if(StringUtils.containsIgnoreCase(ua, "Android")){
            source = "Android";
        }
        else if (StringUtils.containsIgnoreCase(ua, "iPad")){
            source = "iPad";
        }
        return source;
    }
}
