package com.unimelb.nettywhiteboard.utils;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/**
 * @author Zhihao Liang 1367102
 */
public class FrameInfo {
    private final Stage stage;
    private final FXMLLoader fxmlLoader;

    public FrameInfo(FXMLLoader fxmlLoader, Stage stage) {
        this.stage = stage;
        this.fxmlLoader = fxmlLoader;

    }

    public Stage getStage() {
        return stage;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }


}
