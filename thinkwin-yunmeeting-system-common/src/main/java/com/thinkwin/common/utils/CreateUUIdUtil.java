package com.thinkwin.common.utils;

import java.util.UUID;

/**
 * 类名: UidUtil </br>
 * 描述: 生成UUid工具类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/5/27 </br>
 */
public class CreateUUIdUtil {
    public static String Uuid() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        String uid = s;
        String uids="";
        for(int i=0;i<uid.length();i++){
            if (i!=8&&i!=13&&i!=18&&i!=23) {
                uids += uid.charAt(i);//8 13 18 23

            }
        }
        return uids;
    }
}
