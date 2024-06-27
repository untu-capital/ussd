package com.example.ussd1.sessions;

import java.util.HashMap;
import java.util.Map;

public class ApplyLoanSession {

    private static final Map<String, Boolean> sessionMap = new HashMap<>();

    public static boolean getApplyLoanSession(String transactionID) {
        return sessionMap.getOrDefault(transactionID, false);
    }

    public static void setApplyLoanSession(String transactionID, boolean sessionData) {
        sessionMap.put(transactionID, sessionData);
    }
}
