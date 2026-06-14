package ua.edu.ukma.ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ua.edu.ukma.model.UserProfile;
import ua.edu.ukma.service.UserStorage;

import java.util.Objects;
import java.util.function.Consumer;

public class AuthWindow {

    private static final double PANEL_WIDTH = 620;
    private static final double PANEL_HEIGHT = 520;

    private final UserStorage userStorage = new UserStorage();
    private final Consumer<UserProfile> onAuthSuccess;

    private boolean registrationMode = false;

    private Button loginTab;
    private Button registerTab;
    private Label titleLabel;
    private Label messageLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button mainButton;

    public AuthWindow(Consumer<UserProfile> onAuthSuccess) {
        this.onAuthSuccess = onAuthSuccess;
    }

    public void show(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.initStyle(StageStyle.UNDECORATED);

        Pane root = new Pane();
        root.setStyle("-fx-background-color: #181820;");

        ImageView background = createBackground(screenBounds);

        WindowTitleBar windowTitleBar = new WindowTitleBar("Maze of Perish");

        Pane titleBar = windowTitleBar.create(stage);
        titleBar.setLayoutX(0);
        titleBar.setLayoutY(0);
        titleBar.prefWidthProperty().bind(root.widthProperty());
        titleBar.setPrefHeight(WindowTitleBar.HEIGHT);

        Pane panel = createPanel();

        panel.layoutXProperty().bind(
                root.widthProperty()
                        .subtract(PANEL_WIDTH)
                        .divide(2)
        );

        panel.layoutYProperty().bind(
                root.heightProperty()
                        .subtract(WindowTitleBar.HEIGHT)
                        .subtract(PANEL_HEIGHT)
                        .divide(2)
                        .add(WindowTitleBar.HEIGHT)
        );

        root.getChildren().addAll(background, titleBar, panel);

        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight(), Color.rgb(24, 24, 32));

        stage.setOpacity(0.0);

        stage.setTitle("Maze of Perish");
        stage.setScene(scene);
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setResizable(false);

        root.setOpacity(0.0);

        stage.show();

        Platform.runLater(() -> {
            stage.setOpacity(1.0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }

    private ImageView createBackground(Rectangle2D screenBounds) {
        ImageView background = new ImageView();
        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/background/auth_background.png")
            ));
            background.setImage(image);
        } catch (Exception exception) {
            background.setStyle("-fx-background-color: #181820;");
        }

        background.setFitWidth(screenBounds.getWidth());
        background.setFitHeight(screenBounds.getHeight());
        background.setPreserveRatio(false);
        background.setOpacity(0.85);

        return background;
    }

    private Pane createPanel() {
        Pane panel = new Pane();
        panel.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.setStyle("""
                -fx-background-color: rgba(8, 13, 20, 0.82);
                -fx-border-color: #6d7480;
                -fx-border-width: 1.5;
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                """);

        loginTab = createTabButton("Login", true);
        loginTab.setLayoutX(40);
        loginTab.setLayoutY(35);
        loginTab.setOnAction(event -> setRegistrationMode(false));

        registerTab = createTabButton("Register", false);
        registerTab.setLayoutX(315);
        registerTab.setLayoutY(35);
        registerTab.setOnAction(event -> setRegistrationMode(true));

        titleLabel = new Label("Game login");
        titleLabel.setLayoutX(40);
        titleLabel.setLayoutY(115);
        titleLabel.setPrefWidth(PANEL_WIDTH - 80);
        titleLabel.setStyle("-fx-text-fill: #e8edf4; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label usernameLabel = createFieldLabel("Username");
        usernameLabel.setLayoutX(48);
        usernameLabel.setLayoutY(165);

        usernameField = createTextField("Enter username...");
        usernameField.setLayoutX(48);
        usernameField.setLayoutY(197);

        Label passwordLabel = createFieldLabel("Password");
        passwordLabel.setLayoutX(48);
        passwordLabel.setLayoutY(270);

        passwordField = createPasswordField();
        passwordField.setLayoutX(48);
        passwordField.setLayoutY(302);

        mainButton = createMainButton("Log in");
        mainButton.setLayoutX(48);
        mainButton.setLayoutY(385);
        mainButton.setOnAction(event -> handleAuth());

        messageLabel = new Label("");
        messageLabel.setLayoutX(48);
        messageLabel.setLayoutY(460);
        messageLabel.setPrefWidth(PANEL_WIDTH - 96);
        messageLabel.setStyle("-fx-text-fill: #b6c0cc; -fx-font-size: 15px; -fx-alignment: center;");
        messageLabel.setWrapText(true);

        panel.getChildren().addAll(loginTab, registerTab, titleLabel, usernameLabel, usernameField, passwordLabel, passwordField, mainButton, messageLabel);

        return panel;
    }

    private Button createTabButton(String text, boolean active) {
        Button button = new Button(text);
        button.setPrefSize(265, 64);
        button.setFocusTraversable(false);
        button.setStyle(active ? activeTabStyle() : inactiveTabStyle());
        return button;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #d8dde5; -fx-font-size: 20px;");
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefSize(PANEL_WIDTH - 96, 58);
        field.setStyle("""
                -fx-background-color: rgba(12, 18, 26, 0.65);
                -fx-border-color: #6d7480;
                -fx-border-width: 1;
                -fx-text-fill: #ffffff;
                -fx-prompt-text-fill: #87909c;
                -fx-font-size: 20px;
                -fx-background-radius: 2;
                -fx-border-radius: 2;
                """);
        return field;
    }

    private PasswordField createPasswordField() {
        PasswordField field = new PasswordField();
        field.setPromptText("Enter password...");
        field.setPrefSize(PANEL_WIDTH - 96, 58);
        field.setStyle("""
                -fx-background-color: rgba(12, 18, 26, 0.65);
                -fx-border-color: #6d7480;
                -fx-border-width: 1;
                -fx-text-fill: #ffffff;
                -fx-prompt-text-fill: #87909c;
                -fx-font-size: 20px;
                -fx-background-radius: 2;
                -fx-border-radius: 2;
                """);
        return field;
    }

    private Button createMainButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(PANEL_WIDTH - 96, 62);
        button.setFocusTraversable(false);
        button.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #5d7fa9, #385375);
                -fx-border-color: #89a9d0;
                -fx-border-width: 1;
                -fx-text-fill: white;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                -fx-background-radius: 3;
                -fx-border-radius: 3;
                """);
        return button;
    }

    private void setRegistrationMode(boolean registrationMode) {
        this.registrationMode = registrationMode;
        loginTab.setStyle(registrationMode ? inactiveTabStyle() : activeTabStyle());
        registerTab.setStyle(registrationMode ? activeTabStyle() : inactiveTabStyle());
        titleLabel.setText(registrationMode ? "Create account" : "Game login");
        mainButton.setText(registrationMode ? "Register" : "Log in");
        messageLabel.setText("Player resources are saved in the account file.");
        messageLabel.setStyle("-fx-text-fill: #b6c0cc; -fx-font-size: 15px; -fx-alignment: center;");
    }

    private void handleAuth() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            UserProfile userProfile = registrationMode ? userStorage.register(username, password) : userStorage.login(username, password);
            onAuthSuccess.accept(userProfile);
        } catch (Exception exception) {
            messageLabel.setText(exception.getMessage());
            messageLabel.setStyle("-fx-text-fill: #ffb4b4; -fx-font-size: 15px; -fx-alignment: center;");
        }
    }

    private String activeTabStyle() {
        return """
                -fx-background-color: linear-gradient(to bottom, #5d7fa9, #385375);
                -fx-border-color: #89a9d0;
                -fx-border-width: 1;
                -fx-text-fill: white;
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-background-radius: 2;
                -fx-border-radius: 2;
                """;
    }

    private String inactiveTabStyle() {
        return """
                -fx-background-color: rgba(14, 20, 28, 0.75);
                -fx-border-color: #555d67;
                -fx-border-width: 1;
                -fx-text-fill: #c5cad1;
                -fx-font-size: 20px;
                -fx-background-radius: 2;
                -fx-border-radius: 2;
                """;
    }
}