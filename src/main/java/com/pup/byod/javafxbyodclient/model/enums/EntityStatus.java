package com.pup.byod.javafxbyodclient.model.enums;

public enum EntityStatus {
    active("active"),
    inactive("inactive"),
    pending("pending");

    private final String value;

    EntityStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EntityStatus fromString(String text) {
        for (EntityStatus status : EntityStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}
