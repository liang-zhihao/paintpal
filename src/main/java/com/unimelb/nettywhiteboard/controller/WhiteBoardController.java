package com.unimelb.nettywhiteboard.controller;


import com.google.gson.JsonObject;
import com.unimelb.nettywhiteboard.model.Role;
import com.unimelb.nettywhiteboard.network.client.ClientServer;
import com.unimelb.nettywhiteboard.utils.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.control.MaskerPane;
import org.slf4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class WhiteBoardController implements CustomController {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(WhiteBoardController.class);


    @FXML
    private Canvas canvas;

    @FXML
    private TextArea chatField;


    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private VBox mainVbox;

    @FXML
    private MenuItem newMenuItem;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private StackPane rootStackPane;

    @FXML
    private MenuItem saveAsMenuItem;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private Button sendButton;

    @FXML
    private HBox shapesHbox;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;


    @FXML
    private ListView<String> userListView;

    @FXML
    private ListView<String> chatListView;


    @FXML
    private MenuBar menuBar;

    @FXML
    private Text userText;

    private ClientServer clientServer;

    private TextField textField = new TextField();
    private ColorPicker colorPicker = new ColorPicker();
    private double startX, startY;


    private double[] lastSent = new double[]{-1, -1};

    private MaskerPane maskerPane = new MaskerPane();
    int count = 0;

    @FXML
    private Menu fileMenu;


    private Stage stage;

    @FXML
    void initialize() {

        clientServer = new ClientServer();
        if (Objects.equals(Config.USER_ROLE, Role.MANAGER.getRole())) {
            clientServer.setClientStatus(ClientServer.ConnectionStatus.ACTIVE.getStatus());
        } else {
            clientServer.setClientStatus(ClientServer.ConnectionStatus.CONNECTING.getStatus());
        }

        clientServer.start();
        initNodes();

        bindProperties();
        applyStyles();
//        addEventHandlers();


    }

    @Override
    public void bindProperties() {
        WhiteboardCanvas.getInstance().filePathProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                Platform.runLater(() -> {
                    logger.info("file path changed: {}", newValue);
                    saveMenuItem.setVisible(true);
                    FrameUtil.getCurrentStage(sendButton).setTitle(String.format("Whiteboard - %s", newValue));
                });
            }
            if (newValue == null || newValue.isEmpty()) {
                Platform.runLater(() -> {
                    stage.setTitle("Whiteboard");
                    saveMenuItem.setVisible(false);
                });
            }
        });
        // NOTE use this to decoupling UI and network logic
        clientServer.chatMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {

                if (MessageUtils.isChatMessage(newValue)) {
                    JsonObject jsonObject = MessageUtils.parseJson(newValue);
                    String userId = jsonObject.get("userId").getAsString();
                    String message = jsonObject.get("message").getAsString();
                    Platform.runLater(() -> {
                        chatListView.getItems().add(String.format("%s: %s", userId, message));
                    });


                }

            }
        });
// when user join the whiteboard, the manager will receive a message
        clientServer.joinMessageProperty().addListener((observable, oldValue, newValue) -> {
//
            if (newValue != null && !newValue.isEmpty()) {
                JsonObject jsonObject = MessageUtils.parseJson(newValue);
                String userId = jsonObject.get("userId").getAsString();
                Platform.runLater(() -> {
                    if (DialogUtil.showAndWaitConfirm("Join", String.format("Can %s Join the whiteboard?", userId))) {
                        clientServer.sendMessage(MessageBuilder.buildManagerApprovalMessage(userId, true, "Accepted by manager"));
                        clientServer.getUserList().add(userId);
                    } else {
                        clientServer.sendMessage(MessageBuilder.buildManagerApprovalMessage(userId, false, "Rejected by manager"));
                    }
                });

            }
        });
//        setup client connect status
        clientServer.clientStatusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (ClientServer.ConnectionStatus.APPROVED.equals(newValue)) {
                    clientServer.setClientStatus(ClientServer.ConnectionStatus.ACTIVE.getStatus());
                    maskerPane.setVisible(false);
                }
                if (newValue.equals(ClientServer.ConnectionStatus.WAITING_FOR_APPROVAL.getStatus())) {
                    maskerPane.setVisible(true);
                }
                if (newValue.equals(ClientServer.ConnectionStatus.KICKED_OUT.getStatus())) {

                    Platform.runLater(() -> {
                        maskerPane.setVisible(true);
                        maskerPane.setText("You have been kicked out");
                    });

                }
                if (newValue.equals(ClientServer.ConnectionStatus.CLOSED.getStatus())) {

                    Platform.runLater(() -> {
                        maskerPane.setVisible(true);
                        maskerPane.setText("The whiteboard has been closed");
                    });

                }
                if (newValue.equals(ClientServer.ConnectionStatus.NOT_APPROVED.getStatus())) {
                    Platform.runLater(() -> {
                        maskerPane.setVisible(true);
                        maskerPane.setText("You are not allowed to join the whiteboard");
                    });

                }
            }
        });

        clientServer.userListProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                Platform.runLater(() -> {

                    userListView.getItems().clear();
                    userListView.getItems().addAll(newValue);
                });
            }

        });
    }

    @Override
    public void applyStyles() {

    }

    @Override
    public void addEventHandlers() {
        setupCanvasEvents(WhiteboardCanvas.getInstance(), WhiteboardCanvas.getInstance().getGc());
        setupButtons(WhiteboardCanvas.getInstance(), WhiteboardCanvas.getInstance().getGc());

        fileMenu.setOnShowing(event -> {
            clientServer.sendMessage(MessageBuilder.buildSaveBoardMessage(Config.USER_ID));
        });
        sendButton.setOnAction(event -> {
            String text = chatField.getText();
            if (text != null && !text.isEmpty()) {
                String msg = MessageBuilder.buildChatMessage(Config.USER_ID, text);
                clientServer.sendMessage(msg);
                chatField.clear();
            }
        });


        newMenuItem.setOnAction(event -> {
            Platform.runLater(() -> {
                try {
                    saveBoard();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                clientServer.sendMessage(MessageBuilder.buildNewBoardMessage(Config.USER_ID));
//            update my board
                WhiteboardCanvas.getInstance().setFilePath("");
                WhiteboardCanvas.getInstance().reset();
            });


        });


        closeMenuItem.setOnAction(event -> {
            try {
                saveBoard();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            closeBoard();
        });


        openMenuItem.setOnAction(event -> {
            try {
                saveBoard();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            FileManager fileManager = new FileManager(FrameUtil.getCurrentStage(sendButton));
            Pair<String, List<String>> commands = fileManager.loadJsonlFile();
            if (commands.getValue().isEmpty()) {
                DialogUtil.showErrorAlert("The file is empty or invalid.");
                return;
            }
            WhiteboardCanvas.getInstance().reset();
            WhiteboardCanvas.getInstance().setFilePath(commands.getKey());
            clientServer.setSavedCommands(commands.getValue());
            clientServer.executeDrawCommands(commands.getValue());
            clientServer.sendMessage(MessageBuilder.buildOpenFileMessage(Config.USER_ID, commands.getValue()));
        });
        saveAsMenuItem.setOnAction(event -> {
            try {
                clientServer.sendMessage(MessageBuilder.buildSaveBoardMessage(Config.USER_ID)).await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            FileManager fileManager = new FileManager(stage);
            String filePath = fileManager.saveAsJsonlFile(clientServer.getSavedCommands());
            if (filePath != null && !filePath.isEmpty()) {
                DialogUtil.showSimpleAlert("Save successfully");
                WhiteboardCanvas.getInstance().setFilePath(filePath);
                return;
            }

            DialogUtil.showErrorAlert("Save failed");

        });
        saveMenuItem.setOnAction(event -> {
            try {
                clientServer.sendMessage(MessageBuilder.buildSaveBoardMessage(Config.USER_ID)).await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            FileManager fileManager = new FileManager(stage);

            if (fileManager.saveAsJsonlFile(WhiteboardCanvas.getInstance().getFilePath(), clientServer.getSavedCommands())) {
                DialogUtil.showSimpleAlert("Save successfully");
                return;
            }
            DialogUtil.showErrorAlert("Save failed");

        });


    }

    @Override
    public void initNodes() {

//setup menu
        fileMenu.setVisible(Objects.equals(Config.USER_ROLE, Role.MANAGER.getRole()));
        saveMenuItem.setVisible(false);
        menuBar.getMenus().add(new Menu(""));
        colorPicker.setValue(Color.BLACK);
        WhiteboardCanvas.getInstance().setCanvas(canvas);
//        init the canvas
        WhiteboardCanvas.getInstance().reset();
//      init the user list
        ListView<String> listView = userListView;
        if (Objects.equals(Config.USER_ROLE, Role.MANAGER.getRole())) {

//      setup right click menu
            listView.setCellFactory(lv -> {
                ListCell<String> cell = new ListCell<>();
                ContextMenu contextMenu = new ContextMenu();
                MenuItem deleteItem = new MenuItem();
                deleteItem.textProperty().bind(Bindings.format("Kick \"%s\"", cell.itemProperty()));


                deleteItem.setOnAction(event -> {
                    if (DialogUtil.showAndWaitConfirm("Kick", String.format("Are you sure to kick %s out?", cell.getItem()))) {
                        clientServer.sendMessage(MessageBuilder.buildKickUserOutMessage(Config.USER_ID, cell.getItem()));
                        listView.getItems().remove(cell.getItem());
                    }
                });
                contextMenu.getItems().addAll(deleteItem);
                cell.textProperty().bind(cell.itemProperty());
                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty || cell.getItem().equals(Config.USER_ID)) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(contextMenu);
                    }
                });
                return cell;
            });
        }
        chatListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    // other stuff to do...
                } else {

                    // set the width's
                    setMaxWidth(200);
                    setPrefWidth(200);

                    // allow wrapping
                    setWrapText(true);
                    setText(item.toString());

                }
            }
        });
        if (Objects.equals(Config.USER_ROLE, Role.MEMBER.getRole())) {
            maskerPane.setVisible(true);
            maskerPane.setText("Waiting for approval");
        } else {
            maskerPane.setVisible(false);
        }
        rootStackPane.getChildren().add(maskerPane);
        userText.setText(Config.USER_ID);
    }

    private void setupCanvasEvents(WhiteboardCanvas whiteboardCanvas, GraphicsContext gc) {
        canvas.setOnMousePressed(event -> onMousePressed(whiteboardCanvas, gc, event));
        canvas.setOnMouseDragged(event -> onMouseDragged(whiteboardCanvas, event));
        canvas.setOnMouseReleased(event -> onMouseReleased(whiteboardCanvas, gc, event));
    }

    private void setupButtons(WhiteboardCanvas whiteboardCanvas, GraphicsContext gc) {
        Button lineButton = new Button("Line");
        lineButton.setOnAction(event -> whiteboardCanvas.setCurrentShape(WhiteboardCanvas.DrawShape.LINE));

        Button rectangleButton = new Button("Rectangle");
        rectangleButton.setOnAction(event -> whiteboardCanvas.setCurrentShape(WhiteboardCanvas.DrawShape.RECTANGLE));

        Button circleButton = new Button("Circle");
        circleButton.setOnAction(event -> whiteboardCanvas.setCurrentShape(WhiteboardCanvas.DrawShape.CIRCLE));
        Button ovalButton = new Button("Oval");
        ovalButton.setOnAction(event -> whiteboardCanvas.setCurrentShape(WhiteboardCanvas.DrawShape.OVAL));
        Button textBtn = new Button("Text");
        textBtn.setOnAction(event -> whiteboardCanvas.setCurrentShape(WhiteboardCanvas.DrawShape.TEXT));

        colorPicker.setOnAction(event -> {
            whiteboardCanvas.setCurrentColor(colorPicker.getValue());
            gc.setStroke(colorPicker.getValue());
        });
        MaskerPane loginMaskerPane = new MaskerPane();

        loginMaskerPane.setVisible(true);
        shapesHbox.getChildren().addAll(lineButton, rectangleButton, circleButton, ovalButton, textBtn, textField, colorPicker);
    }

    private void onMousePressed(WhiteboardCanvas whiteboardCanvas, GraphicsContext gc, MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
//        gc.setStroke(colorPicker.getValue());
        lastSent[0] = startX;
        lastSent[1] = startY;
        if (whiteboardCanvas.getCurrentShape() == WhiteboardCanvas.DrawShape.TEXT) {

            Text text = new Text(startX, startY, textField.getText());
            text.setFill(colorPicker.getValue());
            whiteboardCanvas.addShape(text);
            whiteboardCanvas.redraw();
        } else {
            gc.setStroke(colorPicker.getValue());
            gc.beginPath();
            gc.moveTo(startX, startY);
            gc.stroke();
        }
    }

    private void onMouseDragged(WhiteboardCanvas whiteboardCanvas, MouseEvent event) {
//        unsaved
        WhiteboardCanvas.getInstance().setStatus(WhiteboardCanvas.Status.NOT_SAVED);

        List<Shape> shapes = whiteboardCanvas.getShapes();
        Shape shape = createShape(whiteboardCanvas.getCurrentShape(), startX, startY, event.getX(), event.getY(), colorPicker.getValue());

        if (whiteboardCanvas.getCurrentShape() != WhiteboardCanvas.DrawShape.LINE) {
            if (shapes.size() > 1) {
                shapes.remove(shapes.size() - 1);

            }
        }

        shapes.add(shape);

        if (whiteboardCanvas.getCurrentShape() == WhiteboardCanvas.DrawShape.LINE && isOverThreshold(lastSent[0], lastSent[1], event.getX(), event.getY())) {
            startX = event.getX();
            startY = event.getY();
            clientServer.sendMessage(MessageBuilder.buildDrawLineMessage(Config.USER_ID, lastSent[0], lastSent[1], event.getX(), event.getY(), toRGBCode(colorPicker.getValue())));
            lastSent[0] = startX;
            lastSent[1] = startY;
        }
        whiteboardCanvas.redraw();


    }

    private void onMouseReleased(WhiteboardCanvas whiteboardCanvas, GraphicsContext gc, MouseEvent event) {
        List<Shape> shapes = whiteboardCanvas.getShapes();
        Shape shape = createShape(whiteboardCanvas.getCurrentShape(), startX, startY, event.getX(), event.getY(), colorPicker.getValue());
        shapes.add(shape);
        if (whiteboardCanvas.getCurrentShape() == WhiteboardCanvas.DrawShape.TEXT) {
            clientServer.sendMessage(MessageBuilder.buildDrawTextMessage(Config.USER_ID, startX, startY, textField.getText(), toRGBCode(colorPicker.getValue())));
        } else {
            this.clientServer.sendMessage(MessageBuilder.buildDrawMessage(whiteboardCanvas.getCurrentShape(), Config.USER_ID, startX, startY, event.getX(), event.getY(), toRGBCode(colorPicker.getValue())));
        }

        whiteboardCanvas.redraw();
        gc.closePath();
    }

    private boolean isOverThreshold(double startX, double startY, double currentX, double currentY) {
        final double THRESHOLD = 2.0;
        return Math.abs(startX - currentX) > THRESHOLD || Math.abs(startY - currentY) > THRESHOLD;
    }

    private Shape createShape(WhiteboardCanvas.DrawShape drawShape, double startX, double startY, double endX, double endY, Color color) {
        switch (drawShape) {
            case LINE:
                Line line = new Line(startX, startY, endX, endY);
                line.setStroke(color);
                return line;
            case RECTANGLE:
                Rectangle rect = new Rectangle(startX, startY, endX - startX, endY - startY);
                rect.setStroke(color);
                return rect;
            case CIRCLE:
                Circle circle = new Circle(startX, startY, Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)));
                circle.setStroke(color);
                return circle;
            case OVAL:
                Ellipse oval = new Ellipse(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY));
                oval.setStroke(color);
                return oval;
            case TEXT:
                Text text = new Text(startX, startY, textField.getText());
                text.setFill(color);
                return text;
            default:
                return null;
        }
    }

//TODO bug report: when you draw a line, then draw a rectangle,  first rectangle will disappear in other clients.


    public String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }

    public void saveBoard() throws InterruptedException {
        if (!Role.MANAGER.equals(Config.USER_ROLE)) {
            return;
        }
        clientServer.sendMessage(MessageBuilder.buildSaveBoardMessage(Config.USER_ID)).await();

        if (clientServer.getSavedCommands().isEmpty()) {
            DialogUtil.showSimpleAlert("There is nothing to save.");
            return;
        }

        if ((WhiteboardCanvas.getInstance().getStatus() == WhiteboardCanvas.Status.NEW || WhiteboardCanvas.getInstance().getStatus() == WhiteboardCanvas.Status.NOT_SAVED) && DialogUtil.showAndWaitConfirm("Save", "Do you want to save the current whiteboard?")) {
            if (WhiteboardCanvas.getInstance().getFilePath().isEmpty() || WhiteboardCanvas.getInstance() == null) {
                saveAsMenuItem.fire();
            } else {
                saveMenuItem.fire();
            }
            WhiteboardCanvas.getInstance().setStatus(WhiteboardCanvas.Status.SAVED);
        }
    }

    /**
     * @param stage the stage to set
     */
    public void setStageAndListener(Stage stage) {
        this.stage = stage;

        FrameUtil.getCurrentStage(sendButton).setOnCloseRequest(event -> {
            event.consume();
            if (Role.MANAGER.equals(Config.USER_ROLE)) {
                try {
                    saveBoard();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            closeBoard();
        });
        addEventHandlers();
    }

    public void closeBoard() {
        if (DialogUtil.showAndWaitConfirm("Exit", "Do you want to exit?")) {
            if (Role.MANAGER.equals(Config.USER_ROLE)) {
                clientServer.sendMessage(MessageBuilder.buildCloseBoardMessage());
            } else {
                clientServer.sendMessage(MessageBuilder.buildLeaveMessage(Config.USER_ID));
            }

            Platform.exit();
            System.exit(0);
        }
    }
}
