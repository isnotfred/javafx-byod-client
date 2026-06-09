package com.pup.byod.javafxbyodclient.model.enums;

public enum ApprovalDocType {
    SIGNED_GPOA("Signed GPOA"),
    PAPER_APPROVAL("Paper Approval");

    private final String displayName;

    ApprovalDocType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ApprovalDocType fromString(String text) {
        for (ApprovalDocType type : ApprovalDocType.values()) {
            if (type.name().equalsIgnoreCase(text) || type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return SIGNED_GPOA;
    }
}
