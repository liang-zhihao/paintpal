package com.unimelb.nettywhiteboard;


import com.unimelb.nettywhiteboard.network.server.BroadcastExecutor;
import com.unimelb.nettywhiteboard.network.server.DrawChatHandler;
import com.unimelb.nettywhiteboard.network.server.ServerConnectionHandler;
import com.unimelb.nettywhiteboard.network.server.ServerFileOperationHandler;
import com.unimelb.nettywhiteboard.utils.Config;
import com.unimelb.nettywhiteboard.utils.MessageBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//ConnectionHandler: This handler can be used for managing connections, including accepting new connections and removing connections when users leave or are kicked out.
//
//AuthenticationHandler: If there is any authentication or approval process required for users to join the shared drawing board, it can be managed by this handler.
//
//OperationHandler: This handler would be responsible for processing drawing operations received from users.
//
//ChatHandler: This handler would manage chat messages, forwarding them to all connected users.
//
//FileOperationHandler: This handler would manage file operations like saving and loading the state of the whiteboard.
public class WhiteboardServer {
    private int port;

    private final ConcurrentHashMap<String, ChannelHandlerContext> waitingMembers = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ChannelHandlerContext> managers = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ChannelHandlerContext> members = new ConcurrentHashMap<>();


    private List<String> historyDrawCommands;

    private List<String> historyMessages;

    private final BroadcastExecutor broadcastExecutor;

    private final Logger logger = LoggerFactory.getLogger(WhiteboardServer.class);

    private final Canvas canvas = new Canvas(800, 600);

    public WhiteboardServer(int port) {
        this.port = port;
        historyDrawCommands = new ArrayList<>();
        broadcastExecutor = new BroadcastExecutor(managers, members);

    }

    public static int count = 0;

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        new Thread(broadcastExecutor).start();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new JsonObjectDecoder(), new StringEncoder(),

                            new ServerConnectionHandler(WhiteboardServer.this, waitingMembers, managers, members), new DrawChatHandler(WhiteboardServer.this, waitingMembers), new ServerFileOperationHandler(WhiteboardServer.this));

                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            System.out.println("WhiteboardServer started on port " + port);
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void syncHistoryDrawCommands(String userId) {
        ChannelHandlerContext context = members.get(userId);
        String msg = MessageBuilder.buildSnapShotMessage(userId, historyDrawCommands, true);
        context.writeAndFlush(msg);
        logger.info("Sync history draw {} commands to {}", historyDrawCommands.size(), context.channel().remoteAddress());
    }


    public static void main(String[] args) throws Exception {
        ArgumentParser parser = ArgumentParsers.newFor("server").build()
                .defaultHelp(true);

        parser.addArgument("-p", "--serverPort").required(true).type(Integer.class)
                .help("Server Port");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
            Config.SERVER_PORT = ns.getInt("serverPort");
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        WhiteboardServer server = new WhiteboardServer(Config.SERVER_PORT);

        new Thread(() -> {
            try {
                server.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void broadcastMessage(String msg, ChannelHandlerContext sender) {
        broadcastExecutor.addCommand(msg, sender);
    }

    public void broadcastMessages(List<String> msg, ChannelHandlerContext sender) {
        for (String m : msg) {
            broadcastExecutor.addCommand(m, sender);
        }
    }

    public void broadcastUserList() {
        logger.info("Broadcast user list: {}", getUserList().toString());
        broadcastMessage(MessageBuilder.buildUserListMessage("System", getUserList()), null);
    }

    public void removeUser(String userId) {


        for (String key : managers.keySet()) {
            if (key.equals(userId)) {
                ChannelHandlerContext context = managers.remove(key);
                context.close();
                break;
            }
        }
        for (String key : members.keySet()) {
            if (key.equals(userId)) {
                ChannelHandlerContext context = members.remove(key);
                this.broadcastMessage(MessageBuilder.buildChatMessage("System", userId + " has left the whiteboard"), null);
                context.close();
                break;
            }
        }
        broadcastUserList();

    }

    public void removeUser(ChannelHandlerContext context) {
        context.close();
        for (String key : managers.keySet()) {
            if (managers.get(key).equals(context)) {
                managers.remove(key);
                break;
            }
        }
        for (String key : members.keySet()) {
            if (members.get(key).equals(context)) {
                members.remove(key);
                break;
            }
        }
        broadcastUserList();

    }

    public boolean hasRepeatedUser(String userId) {
        for (String key : waitingMembers.keySet()) {
            if (key.equals(userId)) {
                return true;
            }
        }
        for (String key : managers.keySet()) {
            if (key.equals(userId)) {
                return true;
            }
        }
        for (String key : members.keySet()) {
            if (key.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getUserList() {
        List<String> allUsers = new ArrayList<>(managers.keySet());
        allUsers.addAll(members.keySet());
        return allUsers;
    }

    public void canvasData(Canvas canvas) {
        // Step 1: Perform a snapshot of the GraphicsContext into a JavaFX Image
        WritableImage writableImage = canvas.snapshot(new SnapshotParameters(), null);

        // Step 2: Convert it into a BufferedImage using SwingFXUtils
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        // Step 3: Convert BufferedImage to byte[] using ImageIO and ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
    }


    /**
     * @return String> return the historyMessages
     */
    public List<String> getHistoryMessages() {return historyMessages;}

    /**
     * @return String> return the historyDrawCommands
     */
    public List<String> getHistoryDrawCommands() {return historyDrawCommands;}


}
