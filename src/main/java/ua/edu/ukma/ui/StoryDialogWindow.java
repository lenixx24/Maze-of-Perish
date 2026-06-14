package ua.edu.ukma.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StoryDialogWindow {

    private static final double WIDTH = 1040;

    public void show(Stage owner, String speakerName, String text, String buttonText, Runnable onContinue) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.42);");

        VBox dialogBox = createDialogBox(speakerName, text, buttonText, dialogStage, onContinue);
        root.getChildren().add(dialogBox);

        Scene scene = new Scene(root, owner.getWidth(), owner.getHeight());
        scene.setFill(Color.TRANSPARENT);

        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private VBox createDialogBox(String speakerName, String text, String buttonText, Stage dialogStage, Runnable onContinue) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(34, 52, 34, 52));

        box.setPrefWidth(WIDTH);
        box.setMinWidth(WIDTH);
        box.setMaxWidth(WIDTH);

        box.setMinHeight(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);

        box.setStyle("""
                -fx-background-color: rgba(5, 9, 16, 0.96);
                -fx-border-color: linear-gradient(to right, #6d7480, #d8ad4f, #8f5cff, #6d7480);
                -fx-border-width: 1.8;
                -fx-background-radius: 14;
                -fx-border-radius: 14;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.72), 22, 0.35, 0, 7);
                """);

        Label nameLabel = new Label("✦ " + speakerName + " ✦");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setMaxWidth(WIDTH - 120);
        nameLabel.setStyle("""
                -fx-text-fill: #d39cff;
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                """);

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setTextAlignment(TextAlignment.CENTER);
        textLabel.setMaxWidth(WIDTH - 140);
        textLabel.setMinHeight(Region.USE_PREF_SIZE);
        textLabel.setMaxHeight(Double.MAX_VALUE);
        textLabel.setStyle("""
                -fx-text-fill: #eef3ff;
                -fx-font-size: 22px;
                -fx-line-spacing: 7px;
                """);

        Button continueButton = createContinueButton(buttonText);
        continueButton.setOnAction(event -> {
            dialogStage.close();

            if (onContinue != null) {
                onContinue.run();
            }
        });

        box.getChildren().addAll(nameLabel, textLabel, continueButton);
        return box;
    }

    private Button createContinueButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(230, 50);
        button.setFocusTraversable(false);
        button.setFont(Font.font("Jersey 10", FontWeight.BOLD, 22));
        button.setStyle(buttonStyle(false));

        button.setOnMouseEntered(event -> button.setStyle(buttonStyle(true)));
        button.setOnMouseExited(event -> button.setStyle(buttonStyle(false)));

        return button;
    }

    private String buttonStyle(boolean hover) {
        String background = hover ? "#3b322c" : "#ecc8ad";
        String textColor = hover ? "#f0e6df" : "#2c221b";
        String borderColor = hover ? "#5c4e45" : "#ffffff #5c4a3e #5c4a3e #ffffff";

        return """
                -fx-background-color: %s;
                -fx-border-color: %s;
                -fx-border-width: 3;
                -fx-text-fill: %s;
                -fx-font-family: "Jersey 10";
                -fx-font-size: 21px;
                -fx-font-weight: bold;
                -fx-background-radius: 0;
                -fx-border-radius: 0;
                -fx-cursor: hand;
                """.formatted(background, borderColor, textColor);
    }
}