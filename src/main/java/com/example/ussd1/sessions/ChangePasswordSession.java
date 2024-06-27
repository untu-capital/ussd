package com.example.ussd1.sessions;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordSession {

    private static final Map<String, String[]> changePasswordSessionMap = new HashMap<>();


    public static String[] getChangePasswordMapSession(String transactionID) {
        return changePasswordSessionMap.get(transactionID);
    }

    public static void setChangePasswordMapSession(String transactionID, String[] sessionData) {
        changePasswordSessionMap.put(transactionID, sessionData);
    }
}
