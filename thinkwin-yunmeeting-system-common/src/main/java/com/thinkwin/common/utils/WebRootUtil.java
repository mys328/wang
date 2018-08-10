package com.thinkwin.common.utils;

/**
 * 在Java状态下获取应用名称类
 * auth:yyq
 */
public class WebRootUtil {

    //获取web应用名称
    public  String getWebAppName(){
        String path = this.getClass().getProtectionDomain().getCodeSource()
                .getLocation().getPath();
        String webRoot=path;
        if(null != path && path.length()>0){
            if (path.indexOf("WEB-INF") != -1) {
                String [] pathSplit = path.split("/");
                if(null != pathSplit && pathSplit.length >0){
                    for (int i = 0; i < pathSplit.length; i++) {
                        if("WEB-INF".equals(pathSplit[i])){
                            webRoot=pathSplit[i-1];
                        }
                    }
                }
            }
        }
        return webRoot;
    }
}
