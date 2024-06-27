package com.example.ussd1.util;

import java.util.Random;

public class RandomUtil {
    public static String generateCode(int number){
        Random random = new Random();
        String value = String.valueOf(random.nextInt(9));
        for(int i = 0; i < number -1; i++){
            value = value + String.valueOf(random.nextInt(9));
        }
        return value;
    }
}
