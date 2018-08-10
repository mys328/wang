package com.thinkwin.common.utils;

import java.util.Random;

/**
 * 类名: createRandomNumber </br>
 * 描述:生成随机数</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/19 </br>
 */
public class CreateRandomNumber {
    /**
     * 方法名：createSixByteRandomNumber</br>
     * 描述：生成六位随机数</br>
     * 参数：[]</br>
     * 返回值：java.lang.String</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    public static String createSixByteRandomNumber(){
        int number = new Random().nextInt(1000000);
        while (number < 100000) {
            number = new Random().nextInt(1000000);
            if (number > 100000) {
                break;
            }
        }
        return String.valueOf(number);
    }
}
