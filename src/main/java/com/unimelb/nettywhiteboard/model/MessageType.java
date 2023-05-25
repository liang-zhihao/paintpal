package com.unimelb.nettywhiteboard.model;

public enum MessageType {
    JOIN("join"),
    LEAVE("leave"),
    OPERATION("operation"),
    SNAPSHOT("snapshot"),
    KICK("kick"),
    APPROVAL("approval"),
    CHAT("chat"),
    FILE_OPERATION("fileOperation"),
    USER_LIST("userList");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    // Custom equals method
    public boolean equals(String otherType) {
        return this.type.equals(otherType);
    }

    // If you need to convert strings to the enum types
    public static MessageType fromString(String type) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getType().equals(type)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + type + " found");
    }
}