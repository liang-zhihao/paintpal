package com.unimelb.nettywhiteboard.utils;


import com.unimelb.nettywhiteboard.utils.dialog.ConfirmDialog;
import com.unimelb.nettywhiteboard.utils.dialog.SimpleDialog;
import javafx.scene.control.Alert;

/**
 * @author Zhihao Liang 1367102
 */
public class DialogUtil {


    public static Boolean showAndWaitConfirm(String headingIn, String bodyIn) {

        ConfirmDialog confirmDialog = new ConfirmDialog(headingIn, bodyIn);
        return confirmDialog.showAndGetResult();
    }


    public static void showSimpleAlert(String body) {
        SimpleDialog simpleDialog = new SimpleDialog("Message", body, Alert.AlertType.INFORMATION);
        simpleDialog.showThenWait();
    }

    public static void showErrorAlert(String body) {
        SimpleDialog simpleDialog = new SimpleDialog("Error", body, Alert.AlertType.ERROR);
        simpleDialog.showThenWait();
    }


}
