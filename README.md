```bash
mvn package
```

```bash
java -jar ./target/WhiteboardServer-jar-with-dependencies.jar -p 8888
```

```bash
java --module-path ./javafx-sdk-11/lib --add-modules=javafx.controls,javafx.fxml,javafx.web,javafx.graphics -jar ./target/CreateWhiteBoard-jar-with-dependencies.jar -ip localhost -p 8888 -u manager1
```

```bash
java --module-path ./javafx-sdk-11/lib --add-modules=javafx.controls,javafx.fxml,javafx.web,javafx.graphics -jar ./target/JoinWhiteBoard-jar-with-dependencies.jar -ip localhost -p 8888 -u member1
```

```bash
java --module-path ./javafx-sdk-11/lib --add-modules=javafx.controls,javafx.fxml,javafx.web,javafx.graphics -jar ./target/JoinWhiteBoard-jar-with-dependencies.jar -ip localhost -p 8888 -u member2
```

```bash
java --module-path ./javafx-sdk-11/lib --add-modules=javafx.controls,javafx.fxml,javafx.web,javafx.graphics -jar ./target/JoinWhiteBoard-jar-with-dependencies.jar -ip localhost -p 8888 -u member3
```