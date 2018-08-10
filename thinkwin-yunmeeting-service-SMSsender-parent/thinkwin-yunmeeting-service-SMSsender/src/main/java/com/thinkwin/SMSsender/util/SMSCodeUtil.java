package com.thinkwin.SMSsender.util;

import java.util.Random;

public class SMSCodeUtil {
    public static String generateRandomSMSCode() {
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
