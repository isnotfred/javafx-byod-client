package com.pup.byod.javafxbyodclient.model.enums;

public enum DevicePurpose {
    ACADEMIC_BYOD("Academic BYOD"),
    SCHOOL_EVENT("School Event"),
    ORGANIZATION_USE("Organization Use"),
    OTHER("Other");

    private final String displayName;

    DevicePurpose(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DevicePurpose fromString(String text) {
        for (DevicePurpose purpose : DevicePurpose.values()) {
            if (purpose.name().equalsIgnoreCase(text) || purpose.displayName.equalsIgnoreCase(text)) {
                return purpose;
            }
        }
        return OTHER;
    }
}
