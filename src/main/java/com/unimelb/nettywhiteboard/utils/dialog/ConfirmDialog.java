package com.unimelb.nettywhiteboard.utils.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * @author Zhihao Liang 1367102
 */
public class ConfirmDialog extends Alert {

    public ConfirmDialog(String headingIn, String bodyIn) {
        super(AlertType.CONFIRMATION);
        setTitle("Confirmation Dialog");
        setHeaderText(headingIn);
        setContentText(bodyIn);
    }

    public boolean showAndGetResult() {
        Optional<ButtonType> result = showAndWait();
        return result.get() == ButtonType.OK;
    }


}
