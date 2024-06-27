package com.example.ussd1.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtil {
    public static Map<Integer, String> processString(String input) {
        Map<Integer, String> result = new HashMap<>();
        String[] lines = input.split("\n");

        for (String line : lines) {
            String[] parts = line.split("\\. ");
            if (parts.length == 2) {
                int key = Integer.parseInt(parts[0].trim());
                String value = parts[1].trim();
                result.put(key, value);
            }
        }
        return result;
    }
}
