package com.example.ussd1.sessions;

import java.util.HashMap;
import java.util.Map;

public class UserLoginSession {

    private static final Map<String, Boolean> sessionMap = new HashMap<>();

    public static boolean getLoginSession(String transactionID) {
        return sessionMap.getOrDefault(transactionID, false);
    }

    public static void setLoginSession(String transactionID, boolean sessionData) {
        sessionMap.put(transactionID, sessionData);
    }
}
