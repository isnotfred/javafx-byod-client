package com.pup.byod.javafxbyodclient.model.enums;

public enum EventDeviceType {
    laptop("laptop"),
    tablet("tablet"),
    phone("phone"),
    camera("camera"),
    projector("projector"),
    other("other");

    private final String value;

    EventDeviceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventDeviceType fromString(String text) {
        for (EventDeviceType type : EventDeviceType.values()) {
            if (type.value.equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return other;
    }
}
