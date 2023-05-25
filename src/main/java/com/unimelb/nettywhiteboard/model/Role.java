package com.unimelb.nettywhiteboard.model;

public enum Role {
    MANAGER("manager"),
    MEMBER("member");

    private final String role;

    Role(String type) {
        this.role = type;
    }

    public String getRole() {
        return role;
    }

    // Custom equals method
    public boolean equals(String otherType) {
        return this.role.equals(otherType);
    }

}
