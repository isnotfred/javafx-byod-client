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

    public static String formatTimeOnly(String ts) {
        if (ts == null || ts.trim().isEmpty() || "null".equalsIgnoreCase(ts)) {
            return "-";
        }
        try {
            TemporalAccessor temporal;
            if (ts.contains("Z") || ts.contains("+") || (ts.lastIndexOf("-") > 10)) {
                temporal = OffsetDateTime.parse(ts);
            } else if (ts.contains("T")) {
                temporal = LocalDateTime.parse(ts);
            } else if (ts.contains(":")) {
                java.time.LocalTime time;
                try {
                    time = java.time.LocalTime.parse(ts);
                } catch (Exception e1) {
                    try {
                        time = java.time.LocalTime.parse(ts, DateTimeFormatter.ofPattern("H:mm"));
                    } catch (Exception e2) {
                        return ts;
                    }
                }
                return time.format(DateTimeFormatter.ofPattern("h:mm a"));
            } else {
                return ts;
            }
            
            java.time.LocalTime time;
            if (temporal instanceof OffsetDateTime) {
                time = ((OffsetDateTime) temporal).atZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
            } else {
                time = ((LocalDateTime) temporal).toLocalTime();
            }
            
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            return time.format(timeFormatter);
        } catch (Exception e) {
            return ts;
        }
    }
}
