package com.pup.byod.javafxbyodclient.model.enums;

public enum EventDeviceStatus {
    pending("pending"),
    approved("approved"),
    returned("returned");

    private final String value;

    EventDeviceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventDeviceStatus fromString(String text) {
        for (EventDeviceStatus status : EventDeviceStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}
