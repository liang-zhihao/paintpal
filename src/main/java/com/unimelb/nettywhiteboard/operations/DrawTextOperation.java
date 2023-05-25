package com.unimelb.nettywhiteboard.operations;

import com.unimelb.nettywhiteboard.controller.WhiteboardCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class DrawTextOperation extends Operation {

    private double x, y;
    private String text;
    private Color color;

    public DrawTextOperation(WhiteboardCanvas whiteboardCanvas, double x, double y, String text, Color color) {
        super(whiteboardCanvas);
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }


    @Override
    public Shape execute() {
        GraphicsContext gc = whiteboardCanvas.getGc();
        gc.setFill(color);
        gc.fillText(text, x, y);

        Text text1 = new Text(x, y, text);
        text1.setFill(color);
        return text1;
    }
}
