package com.example.ussd1.sessions;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordSession {

    private static final Map<String, String[]> resetPasswordSessionMap = new HashMap<>();

    public static String[] getResetPasswordSessionMap(String transactionID) {
        return resetPasswordSessionMap.get(transactionID);
    }

    public static void setResetPasswordSessionMap(String transactionID, String[] sessionData) {
        resetPasswordSessionMap.put(transactionID, sessionData);
    }
}
