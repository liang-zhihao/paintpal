package com.unimelb.nettywhiteboard.network.client;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import com.unimelb.nettywhiteboard.operations.*;
import com.unimelb.nettywhiteboard.utils.Config;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


//ConnectionHandler: This handler can manage the connection to the server, including establishing the connection and handling disconnection events.
//
//ApprovalHandler: This handler would be responsible for receiving and handling approval or disapproval messages from the server.
//
//OperationHandler: This handler would be responsible for sending drawing operation messages to the server and receiving broadcasted operation messages from other users.
//
//ChatHandler: This handler would manage sending and receiving chat messages.
//
//FileOperationHandler: This handler would be responsible for managing file operations like saving and loading the state of the whiteboard.
//
//KickOutHandler: This handler would be responsible for handling "kick out" messages from the server.
public class ClientServer {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ClientServer.class);

    private Channel channel;
    private final DrawingCommandHandler drawCommandHandler;

    private SimpleStringProperty chatMessage = new SimpleStringProperty("");


    private SimpleStringProperty approvalMessage = new SimpleStringProperty("");
    private SimpleStringProperty joinMessage = new SimpleStringProperty("");

    private SimpleStringProperty clientStatus = new SimpleStringProperty(ConnectionStatus.CONNECTING.getStatus());

    private SimpleListProperty<String> userList = new SimpleListProperty<>(FXCollections.observableArrayList());


    private List<String> savedCommands = new ArrayList<>();

    public ClientServer() {
        this.drawCommandHandler = new DrawingCommandHandler(WhiteboardCanvas.getInstance());
        new Thread(drawCommandHandler).start();
    }


    public void start() {
        String host = "localhost";
        int port = Config.SERVER_PORT;

        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(new JsonObjectDecoder(), new StringEncoder(), new ClientConnectionHandler(ClientServer.this), new CanvasHandler(ClientServer.this), new ChatHandler(ClientServer.this), new ClientFileOperationHandler(ClientServer.this));
                }
            });

            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture sendMessage(String message) {
        return channel.writeAndFlush(message + "\r\n");
    }

    public void executeDrawCommand(String command) {
        drawCommandHandler.executeDrawCommand(command);
    }

    public void executeDrawCommands(List<String> commands) {
        drawCommandHandler.executeDrawCommands(commands);
    }

    static class DrawingCommandHandler implements Runnable {
        private final BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

        private final WhiteboardCanvas whiteboardCanvas;


        public DrawingCommandHandler(WhiteboardCanvas whiteboardCanvas) {
            this.whiteboardCanvas = whiteboardCanvas;

        }

        public void executeDrawCommand(String command) {
            commandQueue.add(command);
        }

        public void executeDrawCommands(List<String> commands) {
            commandQueue.addAll(commands);
        }

        @Override
        public synchronized void run() {
            while (true) {
                try {
                    String command = commandQueue.take();  // This will block if the queue is empty.
                    draw(command);
                } catch (InterruptedException e) {
                    // Handle interruption...
                    Thread.currentThread().interrupt();  // Preserve interrupt status.
                }
            }
        }


        public synchronized void draw(String msg) {

            Platform.runLater(() -> {

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(msg, JsonObject.class);
                if (MessageUtils.isDrawLine(msg)) {
                    double startX = jsonObject.get("startX").getAsDouble();
                    double startY = jsonObject.get("startY").getAsDouble();
                    double endX = jsonObject.get("endX").getAsDouble();
                    double endY = jsonObject.get("endY").getAsDouble();
                    String color = jsonObject.get("color").getAsString();
                    Operation operation = new DrawLineOperation(whiteboardCanvas, startX, startY, endX, endY, 2, Color.web(color));
                    whiteboardCanvas.addShape(operation.execute());
                } else if (MessageUtils.isDrawRectangle(msg)) {
                    double x = jsonObject.get("x").getAsDouble();
                    double y = jsonObject.get("y").getAsDouble();
                    double width = jsonObject.get("width").getAsDouble();
                    double height = jsonObject.get("height").getAsDouble();
                    String color = jsonObject.get("color").getAsString();
                    Operation operation = new DrawRectangleOperation(whiteboardCanvas, x, y, width, height, Color.web(color));
                    whiteboardCanvas.addShape(operation.execute());
                } else if (MessageUtils.isDrawCircle(msg)) {
                    double centerX = jsonObject.get("centerX").getAsDouble();
                    double centerY = jsonObject.get("centerY").getAsDouble();
                    double radius = jsonObject.get("radius").getAsDouble();
                    String color = jsonObject.get("color").getAsString();
                    Operation operation = new DrawCircleOperation(whiteboardCanvas, centerX, centerY, radius, Color.web(color));
                    whiteboardCanvas.addShape(operation.execute());
                } else if (MessageUtils.isDrawOval(msg)) {
                    double centerX = jsonObject.get("centerX").getAsDouble();
                    double centerY = jsonObject.get("centerY").getAsDouble();
                    double radiusX = jsonObject.get("radiusX").getAsDouble();
                    double radiusY = jsonObject.get("radiusY").getAsDouble();
                    String color = jsonObject.get("color").getAsString();
                    Operation operation = new DrawOvalOperation(whiteboardCanvas, centerX, centerY, radiusX, radiusY, Color.web(color));
                    whiteboardCanvas.addShape(operation.execute());
                } else if (MessageUtils.isErase(msg)) {
                    double x = jsonObject.get("x").getAsDouble();
                    double y = jsonObject.get("y").getAsDouble();
                    double width = jsonObject.get("width").getAsDouble();
                    double height = jsonObject.get("height").getAsDouble();
                    Operation operation = new EraseOperation(whiteboardCanvas, x, y, width, height);
                    whiteboardCanvas.addShape(operation.execute());
                } else if (MessageUtils.isDrawText(msg)) {
                    double x = jsonObject.get("startX").getAsDouble();
                    double y = jsonObject.get("startY").getAsDouble();
                    String text = jsonObject.get("text").getAsString();
                    String color = jsonObject.get("color").getAsString();
                    Operation operation = new DrawTextOperation(whiteboardCanvas, x, y, text, Color.web(color));
                    whiteboardCanvas.addShape(operation.execute());
                }
            });
        }


    }

    /**
     * @return String> return the savedCommands
     */
    public List<String> getSavedCommands() {return savedCommands;}

    /**
     * @param savedCommands the savedCommands to set
     */
    public void setSavedCommands(List<String> savedCommands) {this.savedCommands = savedCommands;}

    public String getChatMessage() {
        return chatMessage.get();
    }

    public SimpleStringProperty chatMessageProperty() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage.set(chatMessage);
    }


    public String getApprovalMessage() {
        return approvalMessage.get();
    }

    public SimpleStringProperty approvalMessageProperty() {
        return approvalMessage;
    }

    public void setApprovalMessage(String approvalMessage) {
        this.approvalMessage.set(approvalMessage);
    }

    public String getJoinMessage() {
        return joinMessage.get();
    }

    public SimpleStringProperty joinMessageProperty() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage.set(joinMessage);
    }

    public String getClientStatus() {
        return clientStatus.get();
    }

    public SimpleStringProperty clientStatusProperty() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus.set(clientStatus);
    }

    public ObservableList<String> getUserList() {
        return userList.get();
    }

    public SimpleListProperty<String> userListProperty() {
        return userList;
    }

    public void setUserList(ObservableList<String> userList) {
        this.userList.set(userList);
    }

    public enum ConnectionStatus {
        DISCONNECTED("Disconnected"), CONNECTING("Connecting"), WAITING_FOR_APPROVAL("Waiting for Approval"), APPROVED("Approved"), ACTIVE("Active"), KICKED_OUT("Kicked Out"), DISCONNECTING("Disconnecting"), CLOSED("Closed"),NOT_APPROVED("Not Approved");

        private final String status;

        ConnectionStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public boolean equals(String status) {
            return this.status.equals(status);
        }

    }
}
