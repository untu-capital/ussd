package com.example.ussd1.sessions;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
    private static final Map<String, String[]> sessionMap = new HashMap<>();

    public static String[] getSession(String phoneNumber) {
        return sessionMap.get(phoneNumber);
    }

    public static void setSession(String phoneNumber, String[] sessionData) {
        sessionMap.put(phoneNumber, sessionData);
    }

}
