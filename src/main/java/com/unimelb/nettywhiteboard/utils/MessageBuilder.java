package com.unimelb.nettywhiteboard.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import com.unimelb.nettywhiteboard.model.MessageType;
import com.unimelb.nettywhiteboard.model.OperationType;

import java.util.List;

public class MessageBuilder {
    private static final Gson GSON = new Gson();

    public static String buildJoinMessage(String userId, String role) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.JOIN.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("time", System.currentTimeMillis());
        jsonObject.addProperty("role", role);

        return GSON.toJson(jsonObject);
    }

    public static String buildLeaveMessage(String userId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.LEAVE.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildDrawRectangleMessage(String userId, double startX, double startY, double endX, double endY, String color) {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.OPERATION.getType());
        jsonObject.addProperty("operationType", OperationType.DRAW_RECTANGLE.getOperationType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("x", startX);
        jsonObject.addProperty("y", startY);
        jsonObject.addProperty("width", width);
        jsonObject.addProperty("height", height);
        jsonObject.addProperty("color", color);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildDrawCircleMessage(String userId, double startX, double startY, double endX, double endY, String color) {
        double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.OPERATION.getType());
        jsonObject.addProperty("operationType", OperationType.DRAW_CIRCLE.getOperationType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("centerX", startX);
        jsonObject.addProperty("centerY", startY);
        jsonObject.addProperty("radius", radius);
        jsonObject.addProperty("color", color);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildDrawOvalMessage(String userId, double startX, double startY, double endX, double endY, String color) {
        double radiusX = Math.abs(endX - startX);
        double radiusY = Math.abs(endY - startY);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.OPERATION.getType());
        jsonObject.addProperty("operationType", OperationType.DRAW_OVAL.getOperationType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("centerX", startX);
        jsonObject.addProperty("centerY", startY);
        jsonObject.addProperty("radiusX", radiusX);
        jsonObject.addProperty("radiusY", radiusY);
        jsonObject.addProperty("color", color);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildEraseMessage(String userId, double startX, double startY, double endX, double endY) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.OPERATION.getType());
        jsonObject.addProperty("operationType", OperationType.ERASE.getOperationType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("x", startX);
        jsonObject.addProperty("y", startY);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildDrawLineMessage(String userId, double startX, double startY, double endX, double endY, String color) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.OPERATION.getType());
        jsonObject.addProperty("operationType", OperationType.DRAW_LINE.getOperationType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("startX", startX);
        jsonObject.addProperty("startY", startY);
        jsonObject.addProperty("endX", endX);
        jsonObject.addProperty("endY", endY);
        jsonObject.addProperty("color", color);
        jsonObject.addProperty("time", System.currentTimeMillis());
        return GSON.toJson(jsonObject);
    }

    public static String buildDrawTextMessage(String userId, double startX, double startY, String text, String color) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.OPERATION.getType());
        jsonObject.addProperty("operationType", OperationType.DRAW_TEXT.getOperationType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("startX", startX);
        jsonObject.addProperty("startY", startY);
        jsonObject.addProperty("text", text);
        jsonObject.addProperty("color", color);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildDrawMessage(WhiteboardCanvas.DrawShape drawShape, String userId, double startX, double startY, double endX, double endY, String color) {
        if (drawShape == WhiteboardCanvas.DrawShape.RECTANGLE) {
            return buildDrawRectangleMessage(userId, startX, startY, endX, endY, color);
        } else if (drawShape == WhiteboardCanvas.DrawShape.CIRCLE) {
            return buildDrawCircleMessage(userId, startX, startY, endX, endY, color);
        } else if (drawShape == WhiteboardCanvas.DrawShape.OVAL) {
            return buildDrawOvalMessage(userId, startX, startY, endX, endY, color);
        } else if (drawShape == WhiteboardCanvas.DrawShape.LINE) {
            return buildDrawLineMessage(userId, startX, startY, endX, endY, color);
        } else if (drawShape == WhiteboardCanvas.DrawShape.TEXT) {
            return buildDrawTextMessage(userId, startX, startY, "TEXT", color);
        } else if (drawShape == WhiteboardCanvas.DrawShape.ERASER) {
            return buildEraseMessage(userId, startX, startY, endX, endY);
        }
        return null;
    }

    public static String buildUserListMessage(String userId, List<String> userList) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.USER_LIST.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.add("userList", GSON.toJsonTree(userList));
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildFileOperationMessage(String userId, String operation, String filePath) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.FILE_OPERATION.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("operation", operation);
        jsonObject.addProperty("filePath", filePath);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildChatMessage(String userId, String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.CHAT.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildManagerApprovalMessage(String userId, boolean approved,String reason) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.APPROVAL.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("approved", approved);
        jsonObject.addProperty("reason", reason);

        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildKickUserOutMessage(String userId, String targetUserId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.KICK.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("targetUserId", targetUserId);
        jsonObject.addProperty("time", System.currentTimeMillis());

        return GSON.toJson(jsonObject);
    }

    public static String buildSnapShotMessage(String userId, List<String> drawCommands, boolean writeToBoard) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.SNAPSHOT.getType());
        jsonObject.addProperty("userId", userId);
        jsonObject.add("commands", GSON.toJsonTree(drawCommands));
        jsonObject.addProperty("time", System.currentTimeMillis());
        jsonObject.addProperty("writeToBoard", writeToBoard);

        return GSON.toJson(jsonObject);
    }

    public static String buildSaveBoardMessage(String userId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.FILE_OPERATION.getType());
        jsonObject.addProperty("operationType", "save");
        jsonObject.addProperty("userId", userId);
        return GSON.toJson(jsonObject);
    }

    public static String buildNewBoardMessage(String userId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.FILE_OPERATION.getType());
        jsonObject.addProperty("operationType", "new");
        jsonObject.addProperty("userId", userId);
        return GSON.toJson(jsonObject);
    }

    public static String buildCloseBoardMessage() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.FILE_OPERATION.getType());
        jsonObject.addProperty("operationType", "close");

        return GSON.toJson(jsonObject);
    }

    public static String buildOpenFileMessage(String userId, List<String> drawCommands) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", MessageType.FILE_OPERATION.getType());
        jsonObject.addProperty("operationType", "open");
        jsonObject.add("commands", GSON.toJsonTree(drawCommands));
        return GSON.toJson(jsonObject);
    }
}
