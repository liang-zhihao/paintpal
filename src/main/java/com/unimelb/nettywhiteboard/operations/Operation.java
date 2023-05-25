package com.unimelb.nettywhiteboard.operations;

import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import javafx.scene.shape.Shape;

public abstract class Operation {
    protected WhiteboardCanvas whiteboardCanvas;

    public Operation(WhiteboardCanvas whiteboardCanvas) {
        this.whiteboardCanvas = whiteboardCanvas;
    }

    public abstract Shape execute();
}

