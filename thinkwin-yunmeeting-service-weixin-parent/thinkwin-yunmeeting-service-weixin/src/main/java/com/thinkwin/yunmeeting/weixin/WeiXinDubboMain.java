package com.thinkwin.yunmeeting.weixin;

/*
 * 类说明：
 * @author lining 2017/7/4
 * @version 1.0
 *
 */
public class WeiXinDubboMain {
    // 如果 spring 配置文件的位置是默认的，则可以直接这样启动服务
    public static void main(String[] args) throws Exception {
        com.alibaba.dubbo.container.Main.main(args);
    }
}
