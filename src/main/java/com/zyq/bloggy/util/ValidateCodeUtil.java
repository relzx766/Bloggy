package com.zyq.bloggy.util;

import java.util.Random;

public class ValidateCodeUtil {
    public static String createMailCode() {
        StringBuffer s = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            if (i < 2) {
                s.append((char) (random.nextInt(25) + 65));
            } else {
                s.append(random.nextInt(10));
            }

        }
        return s.toString();
    }

}
