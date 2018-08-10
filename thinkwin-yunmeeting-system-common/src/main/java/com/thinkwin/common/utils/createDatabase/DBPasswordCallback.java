package com.thinkwin.common.utils.createDatabase;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.DruidPasswordCallback;

import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * 第三步：此方式适用于配置文件方式进行数据库操作
 * 解析密码的时候需要的Callback类
 * 数据库密码回调解密
 * Druid  数据源配置
 */
@SuppressWarnings("serial")
public class DBPasswordCallback extends DruidPasswordCallback {
    //上述生成的公钥
    public static final String PUBLIC_KEY_STRING = "";

    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String pwd = properties.getProperty("password");
        if (StringUtils.isNotBlank(pwd)) {
            try {
                //这里的password是将jdbc.properties配置得到的密码进行解密之后的值
                //所以这里的代码是将密码进行解密
                //TODO 将pwd进行解密;
                String password = ConfigTools.decrypt(PUBLIC_KEY_STRING, pwd);
                setPassword(password.toCharArray());
            } catch (Exception e) {
                setPassword(pwd.toCharArray());
            }
        }
    }

    // 请使用该方法加密后，把密文写入classpath:/config/jdbc.properties
    public static void main(String[] args) {
//        System.out.println(SecurityUtil.encryptDes("", key));
    }
}
