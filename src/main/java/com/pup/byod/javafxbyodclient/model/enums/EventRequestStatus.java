package com.pup.byod.javafxbyodclient.model.enums;

public enum EventRequestStatus {
    pending("pending"),
    approved("approved"),
    returned("returned"),
    rejected("rejected");

    private final String value;

    EventRequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventRequestStatus fromString(String text) {
        for (EventRequestStatus status : EventRequestStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}
