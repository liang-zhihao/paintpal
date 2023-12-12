package com.unimelb.nettywhiteboard.network.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

public class ClientFileOperationHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ClientConnectionHandler.class);


    private final ClientServer clientServer;

    public ClientFileOperationHandler(ClientServer clientServer) {
        this.clientServer = clientServer;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {


    }

    @Override
    public void channelRead(ChannelHandlerContext senderCtx, Object msg) {
        String message = ((ByteBuf) msg).toString(Charset.defaultCharset());
        if (MessageUtils.isNewBoard(message)) {
            Platform.runLater(() -> {
                WhiteboardCanvas.getInstance().reset();
                clientServer.getSavedCommands().clear();
                logger.info("New board created");
            });

        }


        if (MessageUtils.isCloseBoard(message)) {
            clientServer.setConnectionStatus(ClientServer.ConnectionStatus.CLOSED.getStatus());
            senderCtx.close();
            senderCtx.channel().close();
            logger.info("Board closed");
        }

        if (MessageUtils.isOpenFIle(message)) {

            JsonObject jsonObject = MessageUtils.parseJson(message);
            JsonArray jsonCommands = jsonObject.getAsJsonArray("commands");
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> commands = new Gson().fromJson(jsonCommands, listType);

            Platform.runLater(() -> {
                WhiteboardCanvas.getInstance().reset();
                clientServer.getSavedCommands().clear();
                clientServer.setSavedCommands(commands);
                clientServer.executeDrawCommands(commands);
            });
            logger.info("Open file");
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
