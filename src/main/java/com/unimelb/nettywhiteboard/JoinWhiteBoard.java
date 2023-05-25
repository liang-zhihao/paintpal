package com.unimelb.nettywhiteboard;

import com.unimelb.nettywhiteboard.controller.WhiteBoardController;
import com.unimelb.nettywhiteboard.model.Role;
import com.unimelb.nettywhiteboard.utils.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class JoinWhiteBoard extends Application {
    private static final boolean DEBUG = true;

    @Override
    public void start(Stage stage) {


        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (Platform.isFxApplicationThread()) {
                Platform.runLater(() -> ExceptionUtil.showError(t, e));
            }
            System.out.println("Uncaught exception: " + t + ", " + e);
            e.printStackTrace();
        });
        FrameInfo frameInfo = FrameUtil.showFrame(Frames.Main);
        WhiteBoardController whiteBoardController = (WhiteBoardController) frameInfo.getFxmlLoader().getController();
        whiteBoardController.setStageAndListener(stage);
    }

    public static void main(String[] args) {
        // ip-address and port
//<serverIPAddress> <serverPort> username
        ArgumentParser parser = ArgumentParsers.newFor("Join").build()
                .defaultHelp(true)
                .description("Calculate checksum of given files.");
        parser.addArgument("-ip", "--serverIPAddress").required(true).type(String.class)
                .help("Server IP Address");
        parser.addArgument("-p", "--serverPort").required(true).type(Integer.class)
                .help("Server Port");
        parser.addArgument("-u", "--username").required(true).type(String.class)
                .help("Username");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        Config.USER_ID = ns.getString("username");
        Config.SERVER_PORT = ns.getInt("serverPort");
        Config.SERVER_HOST = ns.getString("serverIPAddress");
        Config.USER_ROLE = Role.MEMBER.getRole();

        launch();
    }
}
