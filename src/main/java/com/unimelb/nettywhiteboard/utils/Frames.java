package com.unimelb.nettywhiteboard.utils;


// String prefix = "/com/unimelb/dictionaryclient/fxml/";

/**
 * @author Zhihao Liang 1367102
 */
public enum Frames {

    //    Login("/fxml/login.fxml", 600, 670, "Login"),
    Main("/com/unimelb/nettywhiteboard/fxml/main.fxml", 800, 650, "Whiteboard");

    private final String path;
    private final int width;
    private final int height;
    private final String title;

    Frames(String path, int width, int height, String title) {
        this.path = path;
        this.width = width;
        this.height = height;
        this.title = title;
    }


    public String getPath() {
        return path;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }
}
