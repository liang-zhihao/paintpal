package com.unimelb.nettywhiteboard.model;

public enum OperationType {
    DRAW_RECTANGLE("drawRectangle"),
    DRAW_CIRCLE("drawCircle"),
    DRAW_OVAL("drawOval"),
    DRAW_LINE("drawLine"),
    ERASE("erase"),

    DRAW_TEXT("drawText");

    private final String operationType;

    OperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    // Custom equals method
    public boolean equals(String otherType) {
        return this.operationType.equals(otherType);
    }

    // If you need to convert strings to the enum types
    public static OperationType fromString(String operationType) {
        for (OperationType opType : OperationType.values()) {
            if (opType.getOperationType().equals(operationType)) {
                return opType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + operationType + " found");
    }
}
