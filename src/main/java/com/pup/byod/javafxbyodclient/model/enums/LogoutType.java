package com.pup.byod.javafxbyodclient.model.enums;

public enum LogoutType {
    manual("manual"),
    automatic("automatic");

    private final String value;

    LogoutType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LogoutType fromString(String text) {
        if (text == null) return null;
        for (LogoutType type : LogoutType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
