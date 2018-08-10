package com.thinkwin.yuncm.util;

/**
 * 类名: YunCmDubboMain </br>
 * 描述:</br>
 * 创建时间：  2017/6/26 </br>
 */
public class YunCmDubboMain {
    // 如果 spring 配置文件的位置是默认的，则可以直接这样启动服务
    public static void main(String[] args) throws Exception {
        com.alibaba.dubbo.container.Main.main(args);
    }
}
