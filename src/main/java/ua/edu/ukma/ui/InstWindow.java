package ua.edu.ukma.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.Objects;

public class InstWindow extends StackPane {

    private final ImageView imageView;
    private final Label rightPageText;
    private GameMapView mapView;
    private Runnable onOpenCallback;
    private Runnable onCloseCallback;

    public InstWindow() {
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("inst-root");
        this.setOpacity(0.0);
        this.setVisible(false);

        String cssPath = Objects.requireNonNull(getClass().getResource("/css/card-pane.css")).toExternalForm();
        this.getStylesheets().add(cssPath);
        Font.loadFont(getClass().getResourceAsStream("/font/jersey10.ttf"), 16);

        Image instructionImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/inst.png")));
        this.imageView = new ImageView(instructionImage);
        this.imageView.setFitWidth(1000);
        this.imageView.setPreserveRatio(true);
        this.imageView.setSmooth(true);
        this.getChildren().add(imageView);

        HBox pagesContainer = new HBox();
        pagesContainer.setAlignment(Pos.CENTER);
        pagesContainer.setMaxSize(900, 520);
        pagesContainer.setTranslateY(-10);
        VBox leftPage = new VBox(20);
        leftPage.setAlignment(Pos.TOP_LEFT);
        leftPage.setPrefWidth(450);
        leftPage.setPadding(new Insets(60, 40, 40, 95));

        Label menuTitle = new Label("INSTRUCTIONS");
        menuTitle.setStyle(
                "-fx-background-color: transparent;" +
                " -fx-cursor: hand;" +
                " -fx-padding: 2px;"+
                "-fx-text-fill: #a5785b;"
        );
        menuTitle.setFont(Font.font("Jersey 10", FontWeight.BOLD, 52));
        leftPage.getChildren().add(menuTitle);

        Button btnRules = createMenuButton("Rules");
        Button btnTowers = createMenuButton("Defense types");
        Button btnEnemies = createMenuButton("Enemy types");

        leftPage.getChildren().addAll(btnRules, btnTowers, btnEnemies);

        VBox rightPage = new VBox(10);
        rightPage.setAlignment(Pos.TOP_LEFT);
        rightPage.setPrefWidth(450);
        rightPage.setPadding(new Insets(70, 60, 40, 120));

        rightPageText = new Label("Select a topic from the contents");
        rightPageText.setTextFill(Color.web("#3b322c"));
        rightPageText.setFont(Font.font("Jersey 10", FontWeight.BOLD, 24));
        rightPageText.setWrapText(true);
        rightPageText.setMaxWidth(350);

        rightPage.getChildren().add(rightPageText);
        btnRules.setOnAction(e -> rightPageText.setText(
                """
                        BASIC RULES
                        
                        - Protect the tower at the center of the maze.
                        
                        - Build defenses during the Preparation phase.
                        
                        - Defeat all incoming waves to secure victory."""
        ));

        btnTowers.setOnAction(e -> rightPageText.setText(
                """
                        DEFENSE TYPES
                        
                        - Trap
                        - Bomb
                        - Turret
                        - Freeze
                        - Poison Cloud
                        - Barrier
                        - Sniper Tower
                        - Laser Tower
                        - Cannon Tower"""
        ));

        btnEnemies.setOnAction(e -> rightPageText.setText(
                """
                        ENEMY TYPES
                        
                        - Wanderer
                        - Ram
                        - Destroyer"""
        ));
        pagesContainer.getChildren().addAll(leftPage, rightPage);
        this.getChildren().add(pagesContainer);
        this.setOnMouseClicked(e -> {
            if (e.getPickResult().getIntersectedNode() == this || e.getPickResult().getIntersectedNode() == imageView) {
                hide();
            }
        });
    }
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Jersey 10", FontWeight.BOLD, 26));
        button.setTextFill(Color.web("#5c4e45"));
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 2px;");
        button.setFocusTraversable(false);
        button.setOnMouseEntered(e -> button.setTextFill(Color.web("#b86f3d")));
        button.setOnMouseExited(e -> button.setTextFill(Color.web("#5c4e45")));

        return button;
    }

    public void show(Runnable onOpen, Runnable onClose) {
        this.onOpenCallback = onOpen;
        this.onCloseCallback = onClose;
        if (onOpenCallback != null) onOpenCallback.run();

        this.setVisible(true);
        this.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void hide() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), this);
        fadeOut.setFromValue(this.getOpacity());
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            this.setVisible(false);
            if (onCloseCallback != null) onCloseCallback.run();
            if (mapView != null) mapView.requestFocus();
        });
        fadeOut.play();
    }

    public void setMapView(GameMapView mapView) {
        this.mapView = mapView;
    }
}