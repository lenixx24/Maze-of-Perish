package ua.edu.ukma;

import javafx.application.Application;
import javafx.stage.Stage;
import ua.edu.ukma.config.GameWindowConfig;
import ua.edu.ukma.exception.GameInitializationException;
import ua.edu.ukma.ui.GameWindow;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            GameWindowConfig config = new GameWindowConfig("Maze of Perish", 60, 150);
            GameWindow gameWindow = new GameWindow(config);
            gameWindow.show(stage);
        } catch (Exception exception) {
            throw new GameInitializationException("Failed to initialize game window", exception);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}