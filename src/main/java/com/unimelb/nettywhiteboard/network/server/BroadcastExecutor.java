package com.unimelb.nettywhiteboard.network.server;

import io.netty.channel.ChannelHandlerContext;
import javafx.util.Pair;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class BroadcastExecutor implements Runnable {
    private final BlockingQueue<Pair<ChannelHandlerContext, String>> commandQueue = new LinkedBlockingQueue<>();

    private final ConcurrentHashMap<String, ChannelHandlerContext> managers;
    private final ConcurrentHashMap<String, ChannelHandlerContext> members;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(BroadcastExecutor.class);

    public BroadcastExecutor(ConcurrentHashMap<String, ChannelHandlerContext> managers, ConcurrentHashMap<String, ChannelHandlerContext> members) {
        this.managers = managers;
        this.members = members;
    }

    public void addCommand(String command, ChannelHandlerContext senderCtx) {
        commandQueue.add(new Pair<>(senderCtx, command));
    }

    @Override
    public synchronized void run() {
        while (true) {
            try {
                Pair<ChannelHandlerContext, String> command = commandQueue.take();  // This will block if the queue is empty.
                broadcast(command.getKey(), command.getValue());
            } catch (InterruptedException e) {
                // Handle interruption...
                Thread.currentThread().interrupt();  // Preserve interrupt status.
            }
        }
    }

    private synchronized void broadcast(ChannelHandlerContext senderCtx, String message) {
        if (message == null) {
            return;
        }

        for (String userId : managers.keySet()) {
            ChannelHandlerContext ctx = managers.get(userId);
            if (ctx != senderCtx) {
//                logger.info("Broadcasting to manager: {}", userId);
                ctx.writeAndFlush(message);
            }
        }
        for (String userId : members.keySet()) {
            ChannelHandlerContext ctx = members.get(userId);
            if (ctx != senderCtx) {
                ctx.writeAndFlush(message);
            }
        }
    }

}
