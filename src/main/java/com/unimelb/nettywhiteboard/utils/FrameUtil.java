package com.unimelb.nettywhiteboard.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

/**
 * @author Zhihao Liang 1367102
 */
public class FrameUtil {
    public static FrameInfo showFrame(Frames frame) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FrameUtil.class.getResource(frame.getPath()));
            Parent root = loader.load();
            Scene scene = new Scene(root, frame.getWidth(), frame.getHeight());
//            scene.getStylesheets().add(Objects.requireNonNull(FrameUtil.class.getResource("/css/styles.css")).toExternalForm());
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            Stage stage = new Stage();
            stage.setTitle(frame.getTitle());
            stage.setScene(scene);

            stage.setMinHeight(frame.getHeight());
            stage.setMinWidth(frame.getWidth());
            stage.setMaxHeight(frame.getHeight());
            stage.setMaxWidth(frame.getWidth());
            stage.widthProperty().addListener((o, oldValue, newValue) -> {
                if (newValue.intValue() < stage.getMinWidth() || newValue.intValue() > stage.getMaxWidth()) {
                    stage.setResizable(false);
                    stage.setWidth(stage.getMinWidth());
                    stage.setResizable(true);
                }
            });
            stage.heightProperty().addListener((o, oldValue, newValue) -> {
                if (newValue.intValue() < stage.getMinHeight() || newValue.intValue() > stage.getMaxHeight()) {
                    stage.setResizable(false);
                    stage.setHeight(stage.getMinHeight());
                    stage.setResizable(true);
                }
            });

            stage.show();

            return new FrameInfo(loader, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Stage getCurrentStage(Node whateverNode) {
        return (Stage) whateverNode.getScene().getWindow();
    }

    public static void closeWindow(Node whateverNode) {
        getCurrentStage(whateverNode).close();
    }

}
