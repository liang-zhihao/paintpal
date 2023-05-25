package com.unimelb.nettywhiteboard.network.client;

public enum ConnectionStatus {
    DISCONNECTED("Disconnected"),
    CONNECTING("Connecting"),
    WAITING_FOR_APPROVAL("Waiting for Approval"),
    APPROVED("Approved"),

    NOT_APPROVED("Not Approved"),
    ACTIVE("Active"),
    KICKED_OUT("Kicked Out"),
    DISCONNECTING("Disconnecting");

    private final String status;

    ConnectionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}