package com.pup.byod.javafxbyodclient.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateFormatter {
    private static final DateTimeFormatter TARGET_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm:ss a");

    public static String formatTimestamp(String ts) {
        if (ts == null || ts.trim().isEmpty() || "null".equalsIgnoreCase(ts)) {
            return "";
        }
        try {
            TemporalAccessor temporal;
            if (ts.contains("Z") || ts.contains("+") || (ts.lastIndexOf("-") > 10)) {
                temporal = OffsetDateTime.parse(ts);
            } else if (ts.contains("T")) {
                temporal = LocalDateTime.parse(ts);
            } else {
                return ts;
            }
            
            LocalDateTime localDateTime;
            if (temporal instanceof OffsetDateTime) {
                localDateTime = ((OffsetDateTime) temporal).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            } else {
                localDateTime = (LocalDateTime) temporal;
            }
            
            return localDateTime.format(TARGET_FORMATTER);
        } catch (Exception e) {
            return ts;
        }
    }
}
