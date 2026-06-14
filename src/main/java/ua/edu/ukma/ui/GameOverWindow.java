package ua.edu.ukma.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GameOverWindow {

    public void show(Runnable onRetry, Runnable onMainMenu) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: rgba(24, 24, 32, 0.95); " +
                "-fx-border-color: #8c3232; " +
                "-fx-border-width: 3; " +
                "-fx-padding: 30;");

        Label defeatLabel = new Label("LOSE!");
        defeatLabel.setTextFill(Color.web("#ff4444"));
        defeatLabel.setFont(Font.font("Jersey 10", FontWeight.BOLD, 50));

        Label subLabel = new Label("The Tower had been destroyed");
        subLabel.setTextFill(Color.WHITE);
        subLabel.setFont(Font.font("Jersey 10", FontWeight.NORMAL, 24));
        Button retryBtn = createStyledButton("Reset");
        Button mainMenuBtn = createStyledButton("Main menu");

        retryBtn.setOnAction(e -> {
            stage.close();
            if (onRetry != null) onRetry.run();
        });

        mainMenuBtn.setOnAction(e -> {
            stage.close();
            if (onMainMenu != null) onMainMenu.run();
        });

        layout.getChildren().addAll(defeatLabel, subLabel, retryBtn, mainMenuBtn);

        Scene scene = new Scene(layout, 400, 300);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Jersey 10", FontWeight.BOLD, 20));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color: #4a4a5a; -fx-cursor: hand; -fx-padding: 10 20 10 20;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #6a6a7a; -fx-cursor: hand; -fx-padding: 10 20 10 20;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #4a4a5a; -fx-cursor: hand; -fx-padding: 10 20 10 20;"));

        return btn;
    }
}
