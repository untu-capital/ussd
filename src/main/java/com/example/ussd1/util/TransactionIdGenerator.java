package com.example.ussd1.util;

import com.example.ussd1.commons.UssdConstants;

import java.util.Random;

public class TransactionIdGenerator {
    public static String generateApplicationTransactionID() {
        long timestamp = System.currentTimeMillis();
        return UssdConstants.APPLICATION_TRANSACTION_ID_PREFIX+timestamp;
}
}
