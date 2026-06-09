package com.pup.byod.javafxbyodclient.model.enums;

public enum GateEventType {
    entry("entry"),
    exit("exit");

    private final String value;

    GateEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GateEventType fromString(String text) {
        for (GateEventType type : GateEventType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown gate event: " + text);
    }
}
