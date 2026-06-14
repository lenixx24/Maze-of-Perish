package ua.edu.ukma.ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.UserProfile;
import ua.edu.ukma.service.UserStorage;

import java.util.Objects;
import java.util.function.Consumer;

public class LevelMenuWindow {

    private static final Color BACKGROUND_COLOR = Color.rgb(24, 24, 32);
    private static final double CARD_WIDTH = 345;
    private static final double CARD_HEIGHT = 470;

    private final UserProfile userProfile;
    private final UserStorage userStorage;
    private final Consumer<LevelInfo> onLevelSelected;

    private Label goldLabel;
    private HBox cardsRow;
    private Label messageLabel;

    public LevelMenuWindow(UserProfile userProfile, UserStorage userStorage, Consumer<LevelInfo> onLevelSelected) {
        this.userProfile = userProfile;
        this.userStorage = userStorage;
        this.onLevelSelected = onLevelSelected;
    }

    public void show(Stage stage) {
        show(stage, true);
    }

    public void show(Stage stage, boolean animated) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Pane root = new Pane();
        root.setStyle("-fx-background-color: #181820;");

        ImageView background = createBackground(screenBounds);

        WindowTitleBar windowTitleBar = new WindowTitleBar("Maze of Perish - level menu");
        Pane titleBar = windowTitleBar.create(stage);
        titleBar.setLayoutX(0);
        titleBar.setLayoutY(0);
        titleBar.prefWidthProperty().bind(root.widthProperty());
        titleBar.setPrefHeight(WindowTitleBar.HEIGHT);

        VBox content = createContent(screenBounds);
        content.layoutXProperty().bind(root.widthProperty().subtract(content.prefWidthProperty()).divide(2));
        content.layoutYProperty().bind(root.heightProperty().subtract(WindowTitleBar.HEIGHT).subtract(content.prefHeightProperty()).divide(2).add(WindowTitleBar.HEIGHT));

        root.getChildren().addAll(background, titleBar, content);

        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight(), BACKGROUND_COLOR);
        String cssPath = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("Maze of Perish - level menu");
        stage.setScene(scene);
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setResizable(false);

        if (animated) {
            root.setOpacity(0.0);
        } else {
            root.setOpacity(1.0);
        }

        stage.show();

        if (animated) {
            Platform.runLater(() -> {
                FadeTransition fadeIn = new FadeTransition(Duration.millis(350), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
        }
    }

    private ImageView createBackground(Rectangle2D screenBounds) {
        ImageView background = new ImageView();
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/background/auth_background.png")));
            background.setImage(image);
        } catch (Exception exception) {
            background.setStyle("-fx-background-color: #181820;");
        }
        background.setFitWidth(screenBounds.getWidth());
        background.setFitHeight(screenBounds.getHeight());
        background.setPreserveRatio(false);
        background.setOpacity(0.55);
        return background;
    }

    private VBox createContent(Rectangle2D screenBounds) {
        VBox content = new VBox(26);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(Math.min(screenBounds.getWidth() - 120, 1180));
        content.setPrefHeight(690);

        Label title = new Label("Choose a map");
        title.setStyle("-fx-text-fill: #f3f6fb; -fx-font-size: 42px; -fx-font-weight: bold;");

        HBox topPanel = createTopPanel();

        cardsRow = new HBox(26);
        cardsRow.setAlignment(Pos.CENTER);
        rebuildLevelCards();

        messageLabel = new Label("Purchased maps stay on your account. The first map is available right away.");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setStyle("-fx-text-fill: #c7d0dc; -fx-font-size: 17px;");
        messageLabel.setPrefWidth(content.getPrefWidth());

        content.getChildren().addAll(title, topPanel, cardsRow, messageLabel);
        return content;
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(18);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(12, 28, 12, 28));
        topPanel.setMaxWidth(420);
        topPanel.setStyle("""
                -fx-background-color: rgba(8, 13, 20, 0.84);
                -fx-border-color: #d8ad4f;
                -fx-border-width: 1.5;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                """);

        Label caption = new Label("Gold:");
        caption.setStyle("-fx-text-fill: #e9edf5; -fx-font-size: 20px; -fx-font-weight: bold;");

        goldLabel = new Label();
        goldLabel.setStyle("-fx-text-fill: #ffd56b; -fx-font-size: 26px; -fx-font-weight: bold;");
        updateGoldLabel();

        topPanel.getChildren().addAll(caption, goldLabel);
        return topPanel;
    }

    private void rebuildLevelCards() {
        cardsRow.getChildren().clear();
        for (LevelInfo level : LevelInfo.defaultLevels()) {
            cardsRow.getChildren().add(createLevelCard(level));
        }
    }

    private StackPane createLevelCard(LevelInfo level) {
        boolean unlocked = userProfile.isLevelUnlocked(level.number());

        StackPane card = new StackPane();
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        card.setStyle("""
                -fx-background-color: rgba(7, 11, 18, 0.88);
                -fx-border-color: #687385;
                -fx-border-width: 1.5;
                -fx-background-radius: 16;
                -fx-border-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.55), 18, 0.3, 0, 8);
                """);

        VBox content = new VBox(14);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(22));
        content.setMouseTransparent(true);

        Label levelTitle = new Label("Map " + level.number());
        levelTitle.setStyle("-fx-text-fill: #f4f7ff; -fx-font-size: 29px; -fx-font-weight: bold;");

        Label levelName = new Label(level.title());
        levelName.setAlignment(Pos.CENTER);
        levelName.setWrapText(true);
        levelName.setStyle("-fx-text-fill: #cfd7e3; -fx-font-size: 18px;");
        levelName.setPrefWidth(CARD_WIDTH - 54);

        StackPane previewHolder = createMapPreview(level.mapSupplier().get(), unlocked);

        Label description = new Label(level.description());
        description.setAlignment(Pos.CENTER);
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #aeb9c8; -fx-font-size: 15px;");
        description.setPrefWidth(CARD_WIDTH - 54);

        content.getChildren().addAll(levelTitle, levelName, previewHolder, description);

        VBox actionBox = createActionBox(level, unlocked);
        StackPane.setAlignment(actionBox, Pos.CENTER);
        actionBox.setTranslateY(18);

        card.getChildren().addAll(content, actionBox);
        return card;
    }

    private StackPane createMapPreview(GameMap map, boolean unlocked) {
        StackPane holder = new StackPane();
        holder.setPrefSize(CARD_WIDTH - 48, 245);
        holder.setMaxSize(CARD_WIDTH - 48, 245);
        holder.setStyle("-fx-background-color: rgba(1, 4, 9, 0.8); -fx-background-radius: 12; -fx-border-color: #303a49; -fx-border-radius: 12;");

        Pane preview = new Pane();
        preview.setPrefSize(CARD_WIDTH - 64, 225);
        preview.setMaxSize(CARD_WIDTH - 64, 225);

        double cell = Math.min(preview.getPrefWidth() / map.cols(), preview.getPrefHeight() / map.rows());
        double startX = (preview.getPrefWidth() - map.cols() * cell) / 2;
        double startY = (preview.getPrefHeight() - map.rows() * cell) / 2;

        for (int row = 0; row < map.rows(); row++) {
            for (int col = 0; col < map.cols(); col++) {
                Rectangle tile = new Rectangle(startX + col * cell, startY + row * cell, Math.ceil(cell), Math.ceil(cell));
                tile.setArcWidth(2);
                tile.setArcHeight(2);
                tile.setFill(getPreviewColor(map.getCell(row, col)));
                preview.getChildren().add(tile);
            }
        }

        if (!unlocked) {
            preview.setEffect(new GaussianBlur(12));
            preview.setOpacity(0.55);
        }

        holder.getChildren().add(preview);
        return holder;
    }

    private Color getPreviewColor(CellType type) {
        return switch (type) {
            case WALL -> Color.web("#253044");
            case SPAWN -> Color.web("#8c3a3a");
            case TOWER -> Color.web("#d9b45c");
            default -> Color.web("#75859b");
        };
    }

    private VBox createActionBox(LevelInfo level, boolean unlocked) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16, 22, 16, 22));
        box.setPrefSize(235, 150);
        box.setMinSize(235, 150);
        box.setMaxSize(235, 150);
        box.setStyle("""
                -fx-background-color: rgba(4, 8, 13, 0.90);
                -fx-border-color: #d8ad4f;
                -fx-border-width: 1.3;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                """);

        Label priceLabel = new Label(unlocked ? "Map unlocked" : level.price() + " gold");
        priceLabel.setAlignment(Pos.CENTER);
        priceLabel.setWrapText(true);
        priceLabel.setMaxWidth(205);
        priceLabel.setStyle("-fx-text-fill: #ffd56b; -fx-font-size: 23px; -fx-font-weight: bold;");

        Button actionButton = createActionButton(unlocked ? "Play" : "Buy");
        actionButton.setOnAction(event -> {
            if (userProfile.isLevelUnlocked(level.number())) {
                onLevelSelected.accept(level);
            } else {
                buyLevel(level);
            }
        });

        box.getChildren().addAll(priceLabel, actionButton);
        return box;
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(185, 52);
        button.setFocusTraversable(false);
        button.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #6d91c2, #385375);
                -fx-border-color: #a9c7ee;
                -fx-border-width: 1;
                -fx-text-fill: white;
                -fx-font-size: 21px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-cursor: hand;
                """);
        button.setOnMouseEntered(event -> button.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #84a9da, #45658c);
                -fx-border-color: #d0e2fa;
                -fx-border-width: 1;
                -fx-text-fill: white;
                -fx-font-size: 21px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-cursor: hand;
                """));
        button.setOnMouseExited(event -> button.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #6d91c2, #385375);
                -fx-border-color: #a9c7ee;
                -fx-border-width: 1;
                -fx-text-fill: white;
                -fx-font-size: 21px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-cursor: hand;
                """));
        return button;
    }

    private void buyLevel(LevelInfo level) {
        if (!userProfile.spendGold(level.price())) {
            messageLabel.setText("Not enough gold for map " + level.number() + ". You still need " + (level.price() - userProfile.getGold()) + ".");
            messageLabel.setStyle("-fx-text-fill: #ffb4b4; -fx-font-size: 17px;");
            return;
        }

        userProfile.unlockLevel(level.number());
        userStorage.saveResources(userProfile);
        updateGoldLabel();
        rebuildLevelCards();
        messageLabel.setText("Map " + level.number() + " purchased. You can play it now!");
        messageLabel.setStyle("-fx-text-fill: #bff0c2; -fx-font-size: 17px;");
    }

    private void updateGoldLabel() {
        if (goldLabel != null) {
            goldLabel.setText(String.valueOf(userProfile.getGold()));
        }
    }
}
