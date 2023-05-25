package com.unimelb.nettywhiteboard.operations;

import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

public class DrawOvalOperation extends Operation {
    private double centerX;
    private double centerY;
    private double radiusX;
    private double radiusY;
    private Color color;

    public DrawOvalOperation(WhiteboardCanvas whiteboardCanvas, double centerX, double centerY, double radiusX, double radiusY, Color color) {
        super(whiteboardCanvas);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.color = color;
    }

    @Override
    public Shape execute() {

        GraphicsContext gc = whiteboardCanvas.getGc();

        gc.setStroke(color);
//        gc.fillOval(centerX - radiusX, centerY - radiusY, 2 * radiusX, 2 * radiusY);
        gc.strokeOval(centerX , centerY,   radiusX,  radiusY);

        Ellipse ellipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        ellipse.setStroke(color);
        return ellipse;
    }
}
