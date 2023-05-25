package com.unimelb.nettywhiteboard.network.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.unimelb.nettywhiteboard.utils.Config;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;


public class CanvasHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(CanvasHandler.class);
    private final ClientServer clientServer;

    public CanvasHandler(ClientServer clientServer) {
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
        if (MessageUtils.isTypeOperation(message)) {
            JsonObject jsonObject = MessageUtils.parseJson(message);
            if (jsonObject.get("userId").getAsString().equals(Config.USER_ID)) {
                clientServer.getSavedCommands().add(message);
                return;
            }
            logger.info("Received operation message: {}", message);
            clientServer.executeDrawCommand(message);
        }
        if (MessageUtils.isSnapshot(message)) {

            JsonObject jsonObject = MessageUtils.parseJson(message);
            JsonArray jsonCommands = jsonObject.getAsJsonArray("commands");
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            List<String> historyCommands = new Gson().fromJson(jsonCommands, listType);
            clientServer.setSavedCommands(historyCommands);
            if (jsonObject.get("writeToBoard").getAsBoolean()) {
                logger.info("Received sync message: {}", message);
                clientServer.executeDrawCommands(historyCommands);
            } else {
                logger.info("Received saved message: {}", message);
            }

        }
        senderCtx.fireChannelRead(msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}

