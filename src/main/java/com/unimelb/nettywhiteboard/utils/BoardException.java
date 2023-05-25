package com.unimelb.nettywhiteboard.utils;


import javafx.stage.Stage;
/**
 * @author Zhihao Liang 1367102
 */
public class BoardException extends RuntimeException {
    private final Stage stage;

    public BoardException(String message, Stage stage) {
        super(message);
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}