package ua.edu.ukma.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class WindowTitleBar {

    public static final double HEIGHT = 32;

    private final String title;

    public WindowTitleBar(String title) {
        this.title = title;
    }

    public Pane create(Stage stage) {
        Pane titleBar = new Pane();
        titleBar.setStyle("-fx-background-color: #e9eef2;");

        Label titleLabel = new Label(title);
        titleLabel.setLayoutX(12);
        titleLabel.setLayoutY(7);
        titleLabel.setStyle("-fx-text-fill: #202020; -fx-font-size: 13px;");

        Button minimizeButton = createButton("—");
        minimizeButton.layoutXProperty().bind(titleBar.widthProperty().subtract(90));
        minimizeButton.setLayoutY(0);
        minimizeButton.setOnAction(event -> stage.setIconified(true));

        Button closeButton = createButton("×");
        closeButton.layoutXProperty().bind(titleBar.widthProperty().subtract(45));
        closeButton.setLayoutY(0);
        closeButton.setOnAction(event -> stage.close());

        titleBar.getChildren().addAll(titleLabel, minimizeButton, closeButton);

        return titleBar;
    }

    private Button createButton(String text) {
        Button button = new Button(text);

        button.setPrefSize(45, HEIGHT);
        button.setFocusTraversable(false);

        button.setStyle(normalStyle());

        button.setOnMouseEntered(event -> button.setStyle(hoverStyle()));
        button.setOnMouseExited(event -> button.setStyle(normalStyle()));
        button.setOnMousePressed(event -> button.setStyle(pressedStyle()));
        button.setOnMouseReleased(event -> button.setStyle(hoverStyle()));

        return button;
    }

    private String normalStyle() {
        return """
                -fx-background-color: transparent;
                -fx-text-fill: #202020;
                -fx-font-size: 14px;
                -fx-border-color: transparent;
                -fx-background-insets: 0;
                -fx-padding: 0;
                """;
    }

    private String hoverStyle() {
        return """
                -fx-background-color: #d8dde2;
                -fx-text-fill: #202020;
                -fx-font-size: 14px;
                -fx-border-color: transparent;
                -fx-background-insets: 0;
                -fx-padding: 0;
                """;
    }

    private String pressedStyle() {
        return """
                -fx-background-color: #c8cdd2;
                -fx-text-fill: #202020;
                -fx-font-size: 14px;
                -fx-border-color: transparent;
                -fx-background-insets: 0;
                -fx-padding: 0;
                """;
    }
}