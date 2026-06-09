package com.pup.byod.javafxbyodclient.model.enums;

public enum AuditActionType {
    DEVICE_REGISTERED,
    DEVICE_APPROVED,
    DEVICE_REJECTED,
    DEVICE_DEACTIVATED,
    DEVICE_UPDATED,
    DEVICE_ENTRY,
    DEVICE_EXIT,
    DEVICE_AUTO_EXIT,
    STUDENT_CREATED,
    STUDENT_UPDATED,
    STUDENT_DEACTIVATED,
    USER_CREATED,
    USER_UPDATED,
    USER_DEACTIVATED,
    USER_LOGIN,
    USER_LOGOUT,
    USER_LOGIN_FAILED,
    USER_ROLE_CHANGED,
    ADMIN_CREATED,
    ADMIN_UPDATED,
    ADMIN_DEACTIVATED,
    GUARD_CREATED,
    GUARD_UPDATED,
    GUARD_DEACTIVATED_BY_SUPER,
    EVENT_REQUEST_CREATED,
    EVENT_REQUEST_APPROVED,
    EVENT_REQUEST_RETURNED,
    EVENT_REQUEST_REJECTED,
    SYSTEM_AUTO_EXIT_BATCH,
    SYSTEM_CONFIG_UPDATED;

    public static AuditActionType fromString(String text) {
        for (AuditActionType type : AuditActionType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown audit action: " + text);
    }
}
