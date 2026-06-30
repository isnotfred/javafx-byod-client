package com.pup.byod.javafxbyodclient.model.enums;

public enum DeviceType {
    PERSONAL_COMPUTERS("Personal Computers"),
    COMPONENTS_AND_PERIPHERALS("Components & Peripherals"),
    DISPLAY_AND_PROJECTION("Display & Projection"),
    PROJECT_PROTOTYPES("Project Prototypes"),
    APPLIANCES("Appliances (TLE)"),
    OTHER("Other");

    private final String displayName;

    DeviceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DeviceType fromString(String text) {
        for (DeviceType type : DeviceType.values()) {
            if (type.name().equalsIgnoreCase(text) || type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown device type: " + text);
    }
}
