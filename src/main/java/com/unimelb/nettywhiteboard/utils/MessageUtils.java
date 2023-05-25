package com.unimelb.nettywhiteboard.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.unimelb.nettywhiteboard.model.MessageType;
import com.unimelb.nettywhiteboard.model.OperationType;

import java.util.Objects;

public class MessageUtils {
    private static final Gson GSON = new Gson();


    public static boolean isValidJson(String jsonString) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    public static boolean isTypeOperation(String msg) {

        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject == null) {
            return false;
        }
        try {
            if (jsonObject.has("type")) {
                return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.OPERATION;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDrawLine(String msg) {
        if (isTypeOperation(msg)) {
            JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
            if (jsonObject.has("operationType")) {
                return OperationType.fromString(jsonObject.get("operationType").getAsString()) == OperationType.DRAW_LINE;
            }
        }
        return false;
    }

    public static boolean isDrawRectangle(String msg) {
        if (isTypeOperation(msg)) {
            JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
            if (jsonObject.has("operationType")) {
                return OperationType.fromString(jsonObject.get("operationType").getAsString()) == OperationType.DRAW_RECTANGLE;
            }
        }
        return false;
    }

    public static boolean isDrawCircle(String msg) {
        if (isTypeOperation(msg)) {
            JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
            if (jsonObject.has("operationType")) {
                return OperationType.fromString(jsonObject.get("operationType").getAsString()) == OperationType.DRAW_CIRCLE;
            }
        }
        return false;
    }

    public static boolean isDrawOval(String msg) {
        if (isTypeOperation(msg)) {
            JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
            if (jsonObject.has("operationType")) {
                return OperationType.fromString(jsonObject.get("operationType").getAsString()) == OperationType.DRAW_OVAL;
            }
        }
        return false;
    }

    public static boolean isErase(String msg) {
        if (isTypeOperation(msg)) {
            JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
            if (jsonObject.has("operationType")) {
                return OperationType.fromString(jsonObject.get("operationType").getAsString()) == OperationType.ERASE;
            }
        }
        return false;
    }

    public static boolean isDrawText(String msg) {
        if (isTypeOperation(msg)) {
            JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
            if (jsonObject.has("operationType")) {
                return OperationType.fromString(jsonObject.get("operationType").getAsString()) == OperationType.DRAW_TEXT;
            }
        }
        return false;
    }

    public static boolean isJoin(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.JOIN;
        }
        return false;
    }

    public static boolean isLeave(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.LEAVE;
        }
        return false;
    }

    public static boolean isSnapshot(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.SNAPSHOT;
        }
        return false;
    }

    public static boolean isUserList(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.USER_LIST;
        }
        return false;
    }

    public static boolean isFileOperation(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.FILE_OPERATION;
        }
        return false;
    }

    public static boolean isChatMessage(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.CHAT;
        }
        return false;
    }

    public static boolean isManagerApproval(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.APPROVAL;
        }
        return false;
    }

    public static boolean isCloseBoard(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.FILE_OPERATION
                    && Objects.equals(jsonObject.get("operationType").getAsString(), "close");

        }
        return false;
    }

    public static boolean isNewBoard(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.FILE_OPERATION
                    && Objects.equals(jsonObject.get("operationType").getAsString(), "new");

        }
        return false;
    }

    public static boolean isSaveBoardMessage(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.FILE_OPERATION
                    && Objects.equals(jsonObject.get("operationType").getAsString(), "save");
        }
        return false;
    }

    public static boolean isKickUserOut(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.KICK;
        }
        return false;
    }

    public static boolean isOpenFIle(String msg) {
        if (!isValidJson(msg)) {
            return false;
        }
        JsonObject jsonObject = GSON.fromJson(msg, JsonObject.class);
        if (jsonObject.has("type")) {
            return MessageType.fromString(jsonObject.get("type").getAsString()) == MessageType.FILE_OPERATION
                    && Objects.equals(jsonObject.get("operationType").getAsString(), "open");
        }
        return false;
    }

    public static JsonObject parseJson(String msg) {

        return GSON.fromJson(msg, JsonObject.class);
    }

}
