package com.unimelb.nettywhiteboard.operations;

import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class DrawLineOperation extends Operation {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private double lineWidth;
    private Color color;

    public DrawLineOperation(WhiteboardCanvas gc, double x1, double y1, double x2, double y2, double lineWidth, Color color) {
        super(gc);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.lineWidth = lineWidth;
        this.color = color;

    }

    @Override
    public Shape execute() {
        GraphicsContext gc = whiteboardCanvas.getGc();
        gc.setStroke(color);
        gc.strokeLine(x1, y1, x2, y2);

        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(color);
        return line;
    }
}
