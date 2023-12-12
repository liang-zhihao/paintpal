package com.unimelb.nettywhiteboard.network.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.unimelb.nettywhiteboard.model.Role;
import com.unimelb.nettywhiteboard.WhiteboardServer;
import com.unimelb.nettywhiteboard.utils.MessageBuilder;
import com.unimelb.nettywhiteboard.utils.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentMap;

//import logger.Logger;
public class ServerConnectionHandler extends ChannelInboundHandlerAdapter {
    // We use a ConcurrentMap to hold the connected channels to make sure it's thread-safe

    private final ConcurrentMap<String, ChannelHandlerContext> managers;
    private final ConcurrentMap<String, ChannelHandlerContext> members;

    private final ConcurrentMap<String, ChannelHandlerContext> waitingMembers;


    //    logger
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ServerConnectionHandler.class);
    private final WhiteboardServer whiteboardServer;

    public ServerConnectionHandler(WhiteboardServer whiteboardServer, ConcurrentMap<String, ChannelHandlerContext> waitingMembers, ConcurrentMap<String, ChannelHandlerContext> managers, ConcurrentMap<String, ChannelHandlerContext> members) {
        this.whiteboardServer = whiteboardServer;
        this.managers = managers;
        this.members = members;
        this.waitingMembers = waitingMembers;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // When a new channel is active (i.e., connected), we add it to the map
//        channels.put(ctx.channel().id().asLongText(), ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext senderCtx, Object msg) {
        //User user = (User)msg;
        //log.debug("{}",user);
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(Charset.defaultCharset());
        Gson gson = new Gson();
//        Transfer message to other users
        if (MessageUtils.isJoin(message)) {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
            String userId = jsonObject.get("userId").getAsString();
            String role = jsonObject.get("role").getAsString();

            if (whiteboardServer.hasRepeatedUser(userId)) {
                logger.info("User {} is already in the whiteboard", userId);
                senderCtx.writeAndFlush(MessageBuilder.buildManagerApprovalMessage(userId, false, "Same user ID is in the whiteboard"));
                return;
            }


            logger.info("User {} is joining the whiteboard", userId);
//            Transfer join message to manager
            if (Role.MANAGER.equals(role)) {
                managers.put(userId, senderCtx);
                whiteboardServer.broadcastUserList();
            }
//            Transfer join message to managers
            if (Role.MEMBER.equals(role)) {
                waitingMembers.put(userId, senderCtx);
                managers.values().forEach(ctx -> {
                    ctx.writeAndFlush(message);
                    logger.info("Send join to managers");
                });
            }
        } else if (MessageUtils.isManagerApproval(message)) {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
            String userId = jsonObject.get("userId").getAsString();
            String approval = jsonObject.get("approved").getAsString();
            if (approval.equals("true")) {
                if (waitingMembers.get(userId) != null && !waitingMembers.get(userId).channel().isOpen()) {
                    logger.info("User {} is disconnected", userId);
                    waitingMembers.remove(userId);
                    return;
                }
                ChannelHandlerContext memberCtx = waitingMembers.get(userId);
//                send approval message to member
                logger.info("User {} is approved to join the whiteboard", userId);

                ChannelFuture future = memberCtx.writeAndFlush(message);
//                add member to member list
                members.put(userId, memberCtx);
//                remove member from waiting list
                waitingMembers.remove(userId);

//
//                send user list to all users
                whiteboardServer.broadcastUserList();
                whiteboardServer.broadcastMessage(MessageBuilder.buildChatMessage("System", "Welcome " + userId + " to join the whiteboard"), null);

//                sync canvas data to new member
                future.addListener((ChannelFutureListener) f -> {
                    if (f.isSuccess()) {
                        // If successful, write and flush the second message.
                        whiteboardServer.syncHistoryDrawCommands(userId);
                    } else {
                        // Handle failure of the operation.
                        Throwable cause = f.cause();
                        // Log error or perform other failure handling.
                    }
                });
            } else {

                ChannelHandlerContext memberCtx = waitingMembers.get(userId);
                memberCtx.writeAndFlush(message);
                logger.info("User {} is rejected to join the whiteboard", userId);
                waitingMembers.remove(userId);
            }
        } else if (MessageUtils.isKickUserOut(message)) {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
            String userId = jsonObject.get("userId").getAsString();
            String targetId = jsonObject.get("targetUserId").getAsString();
            if (managers.containsKey(userId)) {
                ChannelHandlerContext targetCtx = members.get(targetId);
                targetCtx.writeAndFlush(message);
                whiteboardServer.removeUser(targetId);
                logger.info("User {} is kicked out the whiteboard", targetId);
                targetCtx.close();
            }
        } else if (MessageUtils.isLeave(message)) {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
            String userId = jsonObject.get("userId").getAsString();
            logger.info("User {} is leaving the whiteboard", userId);
            whiteboardServer.removeUser(userId);
        }

        // Process the received message here
        // Broadcast the message to all clients


        senderCtx.fireChannelRead(msg);


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // When a channel is inactive (i.e., disconnected), we remove it from the map
//        channels.remove(ctx.channel().id().asLongText());
        super.channelInactive(ctx);
        whiteboardServer.removeUser(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised
        cause.printStackTrace();
        ctx.close();
    }


}
