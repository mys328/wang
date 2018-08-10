package com.thinkwin.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 校验器：利用正则表达式校验邮箱、手机号等
 * @author yinchunlei
 * @date 2016年9月19日
 */
public class ValidatorUtil {
	 /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\\\\\-()（）]{0,100}$";
    /**
     * 正则表达式：验证公司名   数组,字母,汉字,(),-,\
     */
    public static final String REGEX_TENANTNAME = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\-()\\\\（）]{0,100}$";
    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$";
    //此证则是必须8到20位数字与字母组合
    /**
     * 正则表达式：验证手机号
     * weining 修改正则支持199  198   166号段 18/06/11 原正则 ^1[34578]\d{9}$
     */
    public static final String REGEX_MOBILE = "^(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$";
 
    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
 
    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";
 
    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
 
    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
 
    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
 
    /**
     * 校验用户名
     * 
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验公司名
     *
     * @param tenantName
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isTenantName(String tenantName) {
        return Pattern.matches(REGEX_TENANTNAME, tenantName);
    }
 
    /**
     * 校验密码
     * 
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }
 
    /**
     * 校验手机号
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }
 
    /**
     * 校验邮箱
     * 
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }
 
    /**
     * 校验汉字
     * 
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }
 
    /**
     * 校验身份证
     * 
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }
 
    /**
     * 校验URL
     * 
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }
 
    /**
     * 校验IP地址
     * 
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }
 
    
    public static boolean checkPassword(String password){
        if(password.matches("\\w+")){
            Pattern p1= Pattern.compile("[a-z]+");
            Pattern p2= Pattern.compile("[A-Z]+");
            Pattern p3= Pattern.compile("[0-9]+");
            Matcher m=p1.matcher(password);
            if(!m.find())
                return false;
            else{
                m.reset().usePattern(p2);
                if(!m.find())
                    return false;
                else{
                    m.reset().usePattern(p3);
                    if(!m.find())
                        return false;
                    else{
                        return true;
                    }
                }
            }
        }else{
            return false;
        }
    }
    /*public static void main(String[] args) {
        String username = "fdsdfsdj";
        String password = "adsfasdfsa";
        String phoneNumber = "jing.wang@56pingtai.com.cn";
        System.out.println(ValidatorUtil.isEmail(phoneNumber));
        //System.out.println(ValidatorUtil.isChinese(username));
        //System.out.println(ValidatorUtil.isPassword(password));
    }*/
}
