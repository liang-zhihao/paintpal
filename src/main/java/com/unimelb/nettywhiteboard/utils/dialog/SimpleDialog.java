package com.unimelb.nettywhiteboard.utils.dialog;

import javafx.scene.control.Alert;
/**
 * @author Zhihao Liang 1367102
 */
public class SimpleDialog extends Alert {

    public SimpleDialog(String title, String body, AlertType alertType) {
        super(alertType);

        setTitle(title);
        setHeaderText("");
        setContentText(body);


    }

    public void showThenWait() {
        showAndWait();
    }
}
