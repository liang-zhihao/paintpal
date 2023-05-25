package com.unimelb.nettywhiteboard.network.server;


import com.unimelb.nettywhiteboard.WhiteboardServer;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

public class DrawChatHandler extends ChannelInboundHandlerAdapter {
    private final ConcurrentHashMap<String, ChannelHandlerContext> clients;

    private final WhiteboardServer whiteboardServer;


    private Logger logger = org.slf4j.LoggerFactory.getLogger(DrawChatHandler.class);

    public DrawChatHandler(WhiteboardServer server, ConcurrentHashMap<String, ChannelHandlerContext> clients) {
        this.whiteboardServer = server;
        this.clients = clients;

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        removeClient(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext senderCtx, Object msg) {
        String message = ((ByteBuf) msg).toString(Charset.defaultCharset());

        // Process the received message here
        // Broadcast the message to all clients
        if (MessageUtils.isTypeOperation(message)) {
            WhiteboardServer.count++;
            whiteboardServer.getHistoryDrawCommands().add(message);
            whiteboardServer.broadcastMessage(message, null);
        } else if (MessageUtils.isChatMessage(message)) {
            logger.info("Received chat message: {}", message);
            whiteboardServer.broadcastMessage(message, senderCtx);
        }
        senderCtx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public synchronized void addClient(ChannelHandlerContext ctx) {
        clients.put(generateClientId(ctx), ctx);
    }

    public synchronized void removeClient(ChannelHandlerContext ctx) {
        clients.remove(generateClientId(ctx));
    }


    private void join(String message, ChannelHandlerContext senderCtx) {

    }

    private String generateClientId(ChannelHandlerContext ctx) {
        // Generate a unique ID for the client, e.g., using the remote IP/port
        return ctx.channel().remoteAddress().toString();
    }

}
