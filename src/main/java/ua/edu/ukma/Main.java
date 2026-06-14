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
import ua.edu.ukma.service.UserStorage;
import ua.edu.ukma.ui.AuthWindow;
import ua.edu.ukma.ui.GameWindow;
import ua.edu.ukma.ui.LevelInfo;
import ua.edu.ukma.ui.LevelMenuWindow;
import ua.edu.ukma.ui.StoryDialogWindow;

public class Main extends Application {

    private static final Color BACKGROUND_COLOR = Color.rgb(24, 24, 32);
    private static UserProfile currentUser;
    private final UserStorage userStorage = new UserStorage();

    @Override
    public void start(Stage stage) {
        try {
            AuthWindow authWindow = new AuthWindow(userProfile -> {
                currentUser = userProfile;
                showLevelMenuAfterAuthorization(stage);
            });
            authWindow.show(stage);
        } catch (Exception exception) {
            throw new GameInitializationException("Failed to initialize authorization window", exception);
        }
    }

    private void showLevelMenuAfterAuthorization(Stage stage) {
        LevelMenuWindow levelMenuWindow = new LevelMenuWindow(currentUser, userStorage, selectedLevel -> showGameAfterLevelSelection(stage, selectedLevel));
        levelMenuWindow.show(stage, false);
        if (!currentUser.isIntroSeen()) {
            currentUser.setIntroSeen(true);
            userStorage.saveResources(currentUser);
            Platform.runLater(() -> new StoryDialogWindow().show(stage, "Guardian", "You have awakened beside the old Tower. This is the Maze of Perish, a place where darkness creates enemies again and again. Defend the Tower, collect gold, unlock deeper mazes, and one day use that gold to seal the Perish Gate forever.", "Begin", null));
        }
    }

    private void showGameAfterLevelSelection(Stage stage, LevelInfo selectedLevel) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(280), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> showGame(stage, selectedLevel));
        fadeOut.play();
    }

    private void showGame(Stage stage, LevelInfo selectedLevel) {
        Pane loadingRoot = new Pane();
        loadingRoot.setStyle("-fx-background-color: rgb(24, 24, 32);");
        Scene loadingScene = new Scene(loadingRoot, stage.getWidth(), stage.getHeight(), BACKGROUND_COLOR);
        stage.setScene(loadingScene);

        Platform.runLater(() -> {
            GameWindowConfig config = new GameWindowConfig("Maze of Perish", 60, 150);
            GameWindow gameWindow = new GameWindow(
                    config,
                    selectedLevel,
                    currentUser,
                    userStorage,
                    () -> showGame(stage, selectedLevel),
                    () -> showLevelMenuAfterAuthorization(stage)
            );

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

    public static void main(String[] args) {
        launch(args);
    }
}