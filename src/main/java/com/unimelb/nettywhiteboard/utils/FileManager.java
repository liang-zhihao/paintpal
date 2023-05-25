package com.unimelb.nettywhiteboard.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class FileManager {
    private Stage primaryStage;
    public static final String FILE_PATH = "src/main/resources/";
    public static final String FILE_NAME = "whiteboard.json";
    public static final String FILE_PATH_NAME = FILE_PATH + FILE_NAME;

    public FileManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public String saveAsJsonlFile(List<String> contents) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSONL Files", "*.jsonl")
        );
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            String filePath = file.getAbsolutePath();

            try (PrintWriter out = new PrintWriter(file)) {
                for (String content : contents) {
                    out.println(content);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return filePath;
        } else {
            return null;
        }

    }

    public boolean saveAsJsonlFile(String filePath, List<String> contents) {
        if (filePath != null) {
            try (PrintWriter out = new PrintWriter(filePath)) {
                for (String content : contents) {
                    out.println(content);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public Pair<String, List<String>> loadJsonlFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSONL Files", "*.jsonl")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        List<String> lines = new ArrayList<>();
        String filePath = null;
        if (file != null) {
            filePath = file.getAbsolutePath();
            try {
                lines = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Pair<>(filePath, lines);
    }
}