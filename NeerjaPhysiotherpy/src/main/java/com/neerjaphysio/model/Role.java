package com.neerjaphysio.model;

public enum Role {
    SUPER_ADMIN("Super Admin"),
    ADMIN("Admin"),
    USER("User");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
