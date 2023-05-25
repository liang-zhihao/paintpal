package com.unimelb.nettywhiteboard.controller;

public interface CustomController {
    void bindProperties(); // used for binding properties between nodes

    void applyStyles(); // used for applying CSS styles to nodes

    void addEventHandlers(); // used for adding event handlers to nodes

    void initNodes(); // used for initializing nodes
}