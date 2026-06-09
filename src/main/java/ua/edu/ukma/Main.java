package ua.edu.ukma;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import ua.edu.ukma.config.GameWindowConfig;
import ua.edu.ukma.exception.GameInitializationException;
import ua.edu.ukma.model.UserProfile;
import ua.edu.ukma.ui.AuthWindow;
import ua.edu.ukma.ui.GameWindow;

public class Main extends Application {

    private static final Color BACKGROUND_COLOR = Color.rgb(24, 24, 32);
    private static UserProfile currentUser;

    @Override
    public void start(Stage stage) {
        try {
            AuthWindow authWindow = new AuthWindow(userProfile -> {
                currentUser = userProfile;
                FadeTransition fadeOut = new FadeTransition(Duration.millis(350), stage.getScene().getRoot());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> showGameAfterAuthorization(stage));
                fadeOut.play();
            });
            authWindow.show(stage);
        } catch (Exception exception) {
            throw new GameInitializationException("Failed to initialize authorization window", exception);
        }
    }

    private void showGameAfterAuthorization(Stage stage) {
        Pane loadingRoot = new Pane();
        loadingRoot.setStyle("-fx-background-color: rgb(24, 24, 32);");

        Scene loadingScene = new Scene(loadingRoot, stage.getWidth(), stage.getHeight(), BACKGROUND_COLOR);

        stage.setScene(loadingScene);

        Platform.runLater(() -> {
            GameWindowConfig config = new GameWindowConfig("Maze of Perish - " + currentUser.getUsername(), 60, 150);
            GameWindow gameWindow = new GameWindow(config);

            Scene gameScene = gameWindow.createScene(stage);
            gameScene.getRoot().setOpacity(0.0);
            gameWindow.applyStageSettings(stage);
            stage.setScene(gameScene);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), gameScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }

    public static UserProfile getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        launch(args);
    }
}