package com.pup.byod.javafxbyodclient.model.enums;

public enum RegistrationStatus {
    pending("pending"),
    approved("approved"),
    rejected("rejected");

    private final String value;

    RegistrationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RegistrationStatus fromString(String text) {
        for (RegistrationStatus status : RegistrationStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown registration status: " + text);
    }
}
