package com.unimelb.nettywhiteboard.network.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.unimelb.nettywhiteboard.WhiteboardServer;
import com.unimelb.nettywhiteboard.utils.Config;
import com.unimelb.nettywhiteboard.utils.MessageBuilder;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

public class ServerFileOperationHandler extends io.netty.channel.ChannelInboundHandlerAdapter {








    // We use a ConcurrentMap to hold the connected channels to make sure it's thread-safe

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ServerFileOperationHandler.class);

    private final WhiteboardServer whiteboardServer;


    public ServerFileOperationHandler(WhiteboardServer whiteboardServer) {
        this.whiteboardServer = whiteboardServer;

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
            whiteboardServer.getHistoryDrawCommands().clear();
            whiteboardServer.broadcastMessage(message, senderCtx);
            logger.info("New board created");
        }
        if (MessageUtils.isSaveBoardMessage(message)) {
            senderCtx.writeAndFlush(MessageBuilder.buildSnapShotMessage(Config.USER_ID, whiteboardServer.getHistoryDrawCommands(), false));
            logger.info("Board saved");
        }


        if (MessageUtils.isCloseBoard(message)) {
            whiteboardServer.broadcastMessage(message, senderCtx);
            logger.info("Board closed");
        }

        if (MessageUtils.isOpenFIle(message)) {
            JsonObject jsonObject = MessageUtils.parseJson(message);
            JsonArray jsonCommands = jsonObject.getAsJsonArray("commands");
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> commands = new Gson().fromJson(jsonCommands, listType);
            whiteboardServer.getHistoryDrawCommands().clear();
            whiteboardServer.getHistoryDrawCommands().addAll(commands);

            whiteboardServer.broadcastMessage(message, senderCtx);
            logger.info("Board opened");
        }
        senderCtx.fireChannelRead(msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
