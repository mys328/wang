package com.thinkwin.fileupload.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/6/22.
 */
public class FileCache {

    private static Map<String, String> fileMap = new ConcurrentHashMap<String,String>() ;

    /**
     *  添加租户文件信息
     * @param tenantId 租户ID
     * @param size 问价大小
     */
    public static void addTenantFile(String tenantId, String size){
        fileMap.put(tenantId, size);
    }


    /**
     *  读取租户存储的文件信息
     * @param tenantId  租户ID
     * @return
     */
    public static String getTenantFile(String tenantId){
        return fileMap.get(tenantId);
    }

    /**
     *   删除
     * @param tenantId 租户ID
     */
    public static void removeTenantFile(String tenantId){
        fileMap.remove(tenantId);
    }
}
