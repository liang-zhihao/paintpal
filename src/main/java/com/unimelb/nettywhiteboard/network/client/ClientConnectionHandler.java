package com.unimelb.nettywhiteboard.network.client;

import com.google.gson.JsonObject;
import com.unimelb.nettywhiteboard.model.Role;
import com.unimelb.nettywhiteboard.utils.Config;
import com.unimelb.nettywhiteboard.utils.DialogUtil;
import com.unimelb.nettywhiteboard.utils.MessageBuilder;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import org.slf4j.Logger;

import java.nio.charset.Charset;

public class ClientConnectionHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    // We use a ConcurrentMap to hold the connected channels to make sure it's thread-safe

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ClientConnectionHandler.class);

    private ClientServer clientServer;

    public ClientConnectionHandler(ClientServer clientServer) {
        this.clientServer = clientServer;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Send join message to server");
        ctx.writeAndFlush(MessageBuilder.buildJoinMessage(Config.USER_ID, Config.USER_ROLE));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        logger.info("Disconnected!");

    }

    @Override
    public void channelRead(ChannelHandlerContext senderCtx, Object msg) {
//TODO receive approve result from server
        String message = ((ByteBuf) msg).toString(Charset.defaultCharset());
// when member receive kick out message
        System.out.println(message);

        if (MessageUtils.isKickUserOut(message)) {
            logger.info("You are kicked out by manager");
            clientServer.setClientStatus(ConnectionStatus.KICKED_OUT.getStatus());
            senderCtx.close();
        }
// when member receive approve message
        if (MessageUtils.isManagerApproval(message)) {
            JsonObject jsonObject = MessageUtils.parseJson(message);
            String reason = jsonObject.get("reason").getAsString();
            if (jsonObject.has("approved") && jsonObject.get("approved").getAsBoolean() && jsonObject.get("userId").getAsString().equals(Config.USER_ID)) {
                clientServer.setClientStatus(ConnectionStatus.APPROVED.getStatus());
                logger.info("You are approved by one of managers");

            } else {
                clientServer.setClientStatus(ConnectionStatus.NOT_APPROVED.getStatus());
                Platform.runLater(() -> {
                    DialogUtil.showSimpleAlert(reason);
                });
                senderCtx.close();

            }

        }
//        when manger receive join message
        if (Role.MANAGER.equals(Config.USER_ROLE) && MessageUtils.isJoin(message)) {
            JsonObject jsonObject = MessageUtils.parseJson(message);
            if (jsonObject.has("userId") && jsonObject.has("role")) {
                String userId = jsonObject.get("userId").getAsString();
                String userRole = jsonObject.get("role").getAsString();
                logger.info("User {} with role {} want to join", userId, userRole);
                clientServer.setJoinMessage(message);

            }
        }
        if (clientServer.getClientStatus().equals(ConnectionStatus.APPROVED.getStatus())
                || clientServer.getClientStatus().equals(ConnectionStatus.ACTIVE.getStatus())) {
            senderCtx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
