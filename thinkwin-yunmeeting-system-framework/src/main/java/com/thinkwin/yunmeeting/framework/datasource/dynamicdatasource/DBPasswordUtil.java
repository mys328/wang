package com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource;

import com.alibaba.druid.filter.config.ConfigTools;
import org.apache.commons.lang3.StringUtils;

/**
 * 第四步：此方式直接调用解密方法
 * 数据库密码回调解密
 */
public class DBPasswordUtil {
    //上述生成的公钥
    public static final String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjDhm/JI3ztmq7zobZ2VPtCKRvUhae+5JcoFe2Tjq4qLFORpxb/mBUhMBg/dvMYcWkyZMnqr0KuSOq335TuQfoz0FTM6eA71+7qD2rno+vvLKUsvHLBKm+AEzCFN4BBnME4Ai5Yij8SeaYKrqocP8qvdp/EYdPxTe8H5z54i2TwQIDAQAB";

    public static String getPassword(String pwd){

        String password ="";
        if (StringUtils.isNotBlank(pwd)) {
            try {
                //TODO 将pwd进行解密;
                 password = ConfigTools.decrypt(PUBLIC_KEY_STRING, pwd);

            } catch (Exception e) {
            }
        }
        return password;
    }

    public static void main(String[] args) {
        String pwd =
                "WcWemEv9RLqoHHaJ2fH/QsxH0NAZrD3pSC0Zqe3n4JaGA0dufoMS+KXluMpgMZTSK6vCsXNBHRbjr0wRnvwiaYjuFT10JDlI2HxS3bXJhXC4TVw/+7pvlwoE8pHgeIumL/Lh2RmDkCsi5JQNG4g/V+PY4XQHZkNbY9wjBei+tKE=";
        String ss =
                "";
        String password = getPassword(pwd);
        System.out.println("数据库解密后密码："+password);
    }

}
