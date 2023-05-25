package com.unimelb.nettywhiteboard.network.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import org.slf4j.Logger;

import java.nio.charset.Charset;

public class ChatHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ChatHandler.class);

    private ClientServer clientServer;

    public ChatHandler(ClientServer clientServer) {
        this.clientServer = clientServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = ((ByteBuf) msg).toString(Charset.defaultCharset());
        Gson gson = new Gson();
        if (MessageUtils.isChatMessage(message)) {
            clientServer.setChatMessage(null);
            clientServer.setChatMessage(message);
        }
        if (MessageUtils.isUserList(message)) {
            logger.info("Received user list: {}", message);
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

            JsonArray users = jsonObject.getAsJsonArray("userList");
            Platform.runLater(() -> {
                clientServer.getUserList().clear();
                for (int i = 0; i < users.size(); i++) {
                    clientServer.getUserList().add(users.get(i).getAsString());
                }
            });

        }
        ctx.fireChannelRead(msg);
    }
}
