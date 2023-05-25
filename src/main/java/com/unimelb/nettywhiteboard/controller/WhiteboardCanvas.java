package com.unimelb.nettywhiteboard.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardCanvas {
    private Canvas canvas;
    private DrawShape currentShape = DrawShape.LINE;

    private Color currentColor = Color.BLACK;

    private List<Shape> shapes = new ArrayList<>();

    public static Color BOARD_COLOR = Color.WHITE;

    private Status status = Status.NEW;

    private final SimpleStringProperty filePath = new SimpleStringProperty("");

    public WhiteboardCanvas() {

    }

    private static final WhiteboardCanvas single = new WhiteboardCanvas();

    public static WhiteboardCanvas getInstance() {
        return single;
    }


    public void redraw() {
        Canvas canvas = this.getCanvas();
        GraphicsContext gc = this.getGc();
        gc.setLineWidth(1);

        Color currentColor = this.getCurrentColor();
        List<Shape> shapes = this.getShapes();

        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(BOARD_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.TRANSPARENT);


        for (Shape shape : shapes) {

            if (shape instanceof javafx.scene.shape.Line) {
                javafx.scene.shape.Line line = (javafx.scene.shape.Line) shape;
                gc.setStroke(line.getStroke());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
            } else if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                gc.setStroke(rect.getStroke());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            } else if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                gc.setStroke(circle.getStroke());
                gc.strokeOval(circle.getCenterX() - circle.getRadius(), circle.getCenterY() - circle.getRadius(), 2 * circle.getRadius(), 2 * circle.getRadius());
            } else if (shape instanceof Text) {
                Text text = (Text) shape;
                gc.setFill(text.getFill());
                gc.fillText(text.getText(), text.getX(), text.getY());

            } else if (shape instanceof Ellipse) {
                Ellipse ellipse = (Ellipse) shape;
                gc.setStroke(ellipse.getStroke());
                gc.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());

            }
        }
        gc.setStroke(currentColor);
    }

    public enum DrawShape {
        LINE, RECTANGLE, CIRCLE, OVAL, TEXT, ERASER
    }

    public enum Status {
        NOT_SAVED,
        NEW,
        SAVED,
        SAVING;

        private Status() {}

        public Status getStatus() {
            return this;
        }
    }


    public void reset() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(BOARD_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        shapes.clear();
    }

    /**
     * @return Canvas return the canvas
     */
    public Canvas getCanvas() {return canvas;}

    /**
     * @param canvas the canvas to set
     */
    public void setCanvas(Canvas canvas) {this.canvas = canvas;}

    /**
     * @return DrawShape return the currentShape
     */
    public DrawShape getCurrentShape() {return currentShape;}

    /**
     * @param currentShape the currentShape to set
     */
    public void setCurrentShape(DrawShape currentShape) {this.currentShape = currentShape;}

    /**
     * @return String return the currentColor
     */
    public Color getCurrentColor() {return currentColor;}

    /**
     * @param currentColor the currentColor to set
     */
    public void setCurrentColor(Color currentColor) {this.currentColor = currentColor;}


    /**
     * @return Shape> return the shapes
     */
    public List<Shape> getShapes() {return shapes;}

    /**
     * @param shapes the shapes to set
     */
    public void setShapes(List<Shape> shapes) {this.shapes = shapes;}

    public void addShape(Shape shape) {this.shapes.add(shape);}

    public void removeShape(int index) {this.shapes.remove(index);}

    /**
     * @return GraphicsContext return the gc
     */
    public GraphicsContext getGc() {return canvas.getGraphicsContext2D();}

    /**
     * @return WhiteboardStatus return the status
     */
    public Status getStatus() {return status;}

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {this.status = status;}

    public String getFilePath() {
        return filePath.get();
    }

    public SimpleStringProperty filePathProperty() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}

