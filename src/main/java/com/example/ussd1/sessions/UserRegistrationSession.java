package com.example.ussd1.sessions;

import java.util.HashMap;
import java.util.Map;

public class UserRegistrationSession {
    private static final Map<String, String[]> sessionMap = new HashMap<>();

    public static String[] getRegSession(String phoneNumber) {
        return sessionMap.get(phoneNumber);
    }

    public static void setRegSession(String phoneNumber, String[] sessionData) {
        sessionMap.put(phoneNumber, sessionData);
    }

}
