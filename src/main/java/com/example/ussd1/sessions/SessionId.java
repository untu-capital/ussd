package com.example.ussd1.sessions;

import java.util.HashMap;
import java.util.Map;

public class SessionId {

    private static final Map<String, String> sessionMap = new HashMap<>();



    public static String getSession(String transactionID) {
        return sessionMap.get(transactionID);
    }

    public static void setSession(String transactionID, String sessionData) {
        sessionMap.put(transactionID, sessionData);
    }
}
