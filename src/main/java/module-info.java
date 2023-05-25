module com.unimelb.nettywhiteboard {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires io.netty.all;
    requires java.desktop;
    requires java.logging;
    requires com.google.gson;
    requires org.jfxtras.styles.jmetro;
    requires org.slf4j;
    requires javafx.swing;
    requires net.sourceforge.argparse4j;


    opens com.unimelb.nettywhiteboard.controller to javafx.fxml, javafx.graphics, javafx.base, javafx.controls;

    opens com.unimelb.nettywhiteboard to javafx.fxml, javafx.graphics;
    exports com.unimelb.nettywhiteboard;

    opens com.unimelb.nettywhiteboard.network.client to javafx.base, javafx.controls, javafx.fxml, javafx.graphics;
    exports com.unimelb.nettywhiteboard.network.server;
    opens com.unimelb.nettywhiteboard.network.server to javafx.fxml, javafx.graphics;
}