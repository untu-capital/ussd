package com.example.ussd1.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {


    public static String formatCurrentDate() {

        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        // Convert the ZonedDateTime to UTC
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

        // Format the UTC date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formattedDate = utcDateTime.format(formatter);

        return formattedDate;
    }
}
