package com.unimelb.nettywhiteboard.operations;

import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import javafx.scene.shape.Shape;

public class EraseOperation extends Operation {
    private double x;
    private double y;
    private double width;
    private double height;

    public EraseOperation(WhiteboardCanvas gc, double x, double y, double width, double height) {
        super(gc);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public Shape execute() {

//        gc.clearRect(x, y, width, height);
        return null;
    }
}
