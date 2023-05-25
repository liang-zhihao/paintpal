package com.unimelb.nettywhiteboard.utils;

import javafx.application.Platform;
/**
 * @author Zhihao Liang 1367102
 */
public class ExceptionUtil {
    public static Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    public static void showError(Thread t, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            showErrorDialog(e);
            return;
        }
        System.err.println("An unexpected error occurred in " + t);


    }

    private static void showErrorDialog(Throwable e) {
        e = getRootCause(e);
        if (e instanceof BoardException) {
            BoardException BoardException = (BoardException) e;
            DialogUtil.showSimpleAlert(BoardException.getMessage());
        }
    }
}
