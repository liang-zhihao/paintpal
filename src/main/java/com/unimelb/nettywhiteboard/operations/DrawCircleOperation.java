package com.unimelb.nettywhiteboard.operations;

import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class DrawCircleOperation extends Operation {
    private double centerX;
    private double centerY;
    private double radius;
    private Color color;

    public DrawCircleOperation(WhiteboardCanvas whiteboardCanvas, double centerX, double centerY, double radius, Color color) {
        super(whiteboardCanvas);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public Shape execute() {
        GraphicsContext gc = whiteboardCanvas.getCanvas().getGraphicsContext2D();
        gc.setStroke(color);
//        gc.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        gc.strokeOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setStroke(color);
        return circle;
    }
}
