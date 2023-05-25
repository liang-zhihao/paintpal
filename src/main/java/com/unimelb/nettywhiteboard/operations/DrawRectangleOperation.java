package com.unimelb.nettywhiteboard.operations;

import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class DrawRectangleOperation extends Operation {
    private double x;
    private double y;
    private double width;
    private double height;
    private Color color;


    public DrawRectangleOperation(WhiteboardCanvas gc, double x, double y, double width, double height, Color color) {
        super(gc);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public Shape execute() {
        GraphicsContext gc = whiteboardCanvas.getGc();
        gc.setStroke(color);
        gc.strokeRect(x, y, width, height);


        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setStroke(color);
        return rectangle;
    }
}
