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
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
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
    private static final double CARD_WIDTH = 280;
    private static final double CARD_HEIGHT = 430;
    private static final int FINAL_SEAL_PRICE = 500;

    private final UserProfile userProfile;
    private final UserStorage userStorage;
    private final Consumer<LevelInfo> onLevelSelected;

    private Stage currentStage;
    private Label goldLabel;
    private HBox cardsRow;
    private Label messageLabel;

    public LevelMenuWindow(UserProfile userProfile, UserStorage userStorage, Consumer<LevelInfo> onLevelSelected) {
        this.userProfile = userProfile;
        this.userStorage = userStorage;
        this.onLevelSelected = onLevelSelected;
    }

    public void show(Stage stage, boolean animated) {
        this.currentStage = stage;
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Pane root = new Pane();
        root.setStyle("-fx-background-color: #181820;");

        ImageView background = createBackground(screenBounds);

        WindowTitleBar windowTitleBar = new WindowTitleBar("Maze of Perish");
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

        stage.setTitle("Maze of Perish");
        stage.setScene(scene);
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setResizable(false);

        root.setOpacity(animated ? 0.0 : 1.0);
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
        background.setOpacity(0.72);
        return background;
    }

    private VBox createContent(Rectangle2D screenBounds) {
        VBox content = new VBox(18);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(Math.min(screenBounds.getWidth() - 80, 1240));
        content.setPrefHeight(665);

        Label title = new Label("Choose a map");
        title.setStyle("-fx-text-fill: #f3f6fb; -fx-font-size: 42px; -fx-font-weight: bold;");

        HBox topPanel = createTopPanel();

        cardsRow = new HBox(18);
        cardsRow.setAlignment(Pos.CENTER);
        rebuildCards();

        messageLabel = new Label(getDefaultMessage());
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
        topPanel.setPadding(new Insets(10, 28, 10, 28));
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

    private void rebuildCards() {
        cardsRow.getChildren().clear();
        for (LevelInfo level : LevelInfo.defaultLevels()) {
            cardsRow.getChildren().add(createLevelCard(level));
        }
        cardsRow.getChildren().add(createPerishGateCard());
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

        VBox content = new VBox(11);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(18));
        content.setMouseTransparent(true);

        Label levelTitle = new Label("Map " + level.number());
        levelTitle.setStyle("-fx-text-fill: #f4f7ff; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label levelName = new Label(level.title());
        levelName.setAlignment(Pos.CENTER);
        levelName.setWrapText(true);
        levelName.setStyle("-fx-text-fill: #cfd7e3; -fx-font-size: 17px;");
        levelName.setPrefWidth(CARD_WIDTH - 42);

        StackPane previewHolder = createMapPreview(level.mapSupplier().get(), unlocked);

        Label description = new Label(level.description());
        description.setAlignment(Pos.CENTER);
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #aeb9c8; -fx-font-size: 14px;");
        description.setPrefWidth(CARD_WIDTH - 42);

        content.getChildren().addAll(levelTitle, levelName, previewHolder, description);

        VBox actionBox = createActionBox(level, unlocked);
        StackPane.setAlignment(actionBox, Pos.CENTER);
        actionBox.setTranslateY(-8);

        card.getChildren().addAll(content, actionBox);
        return card;
    }

    private StackPane createMapPreview(GameMap map, boolean unlocked) {
        StackPane holder = new StackPane();
        holder.setPrefSize(CARD_WIDTH - 42, 210);
        holder.setMaxSize(CARD_WIDTH - 42, 210);
        holder.setStyle("""
                -fx-background-color: rgba(1, 4, 9, 0.8);
                -fx-background-radius: 12;
                -fx-border-color: #303a49;
                -fx-border-radius: 12;
                """);

        Pane preview = new Pane();
        preview.setPrefSize(CARD_WIDTH - 56, 190);
        preview.setMaxSize(CARD_WIDTH - 56, 190);

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
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12, 18, 12, 18));
        box.setPrefSize(210, 122);
        box.setMinSize(210, 122);
        box.setMaxSize(210, 122);
        box.setStyle("""
                -fx-background-color: rgba(4, 8, 13, 0.90);
                -fx-border-color: #d8ad4f;
                -fx-border-width: 1.3;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                """);

        Label priceLabel = new Label(getActionBoxTitle(level, unlocked));
        priceLabel.setAlignment(Pos.CENTER);
        priceLabel.setWrapText(true);
        priceLabel.setMaxWidth(190);
        priceLabel.setStyle("-fx-text-fill: #ffd56b; -fx-font-size: 20px; -fx-font-weight: bold;");

        Button actionButton = createActionButton(getActionButtonText(unlocked), userProfile.isEndingCompleted());
        actionButton.setOnAction(event -> {
            if (userProfile.isEndingCompleted()) {
                messageLabel.setText("The story is complete. The Perish Gate is sealed, so the mazes are no longer available.");
                messageLabel.setStyle("-fx-text-fill: #ffcf8a; -fx-font-size: 17px;");
                return;
            }

            if (userProfile.isLevelUnlocked(level.number())) {
                onLevelSelected.accept(level);
            } else {
                buyLevel(level);
            }
        });

        box.getChildren().addAll(priceLabel, actionButton);
        return box;
    }

    private String getActionBoxTitle(LevelInfo level, boolean unlocked) {
        if (userProfile.isEndingCompleted()) return "Story completed";
        if (unlocked) return "Map unlocked";
        return level.price() + " gold";
    }

    private String getActionButtonText(boolean unlocked) {
        if (userProfile.isEndingCompleted()) return "Completed";
        if (unlocked) return "Play";
        return "Buy";
    }

    private StackPane createPerishGateCard() {
        boolean darkCorridorsUnlocked = userProfile.isLevelUnlocked(2);
        boolean finalMazeUnlocked = userProfile.isLevelUnlocked(3);
        boolean perishGateUnlocked = darkCorridorsUnlocked && finalMazeUnlocked;
        boolean enoughGold = userProfile.getGold() >= FINAL_SEAL_PRICE;
        boolean endingCompleted = userProfile.isEndingCompleted();

        StackPane card = new StackPane();
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        card.setStyle("""
                -fx-background-color: rgba(14, 6, 25, 0.91);
                -fx-border-color: #a15cff;
                -fx-border-width: 1.8;
                -fx-background-radius: 16;
                -fx-border-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(149, 76, 255, 0.45), 24, 0.42, 0, 0);
                """);

        VBox content = new VBox(13);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(22, 18, 22, 18));
        content.setMaxWidth(CARD_WIDTH - 36);
        content.setMouseTransparent(false);

        Label title = new Label("Perish Gate");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(CARD_WIDTH - 42);
        title.setStyle("-fx-text-fill: #d39cff; -fx-font-size: 27px; -fx-font-weight: bold;");

        StackPane gateIcon = createGateIcon(perishGateUnlocked, endingCompleted);

        Label status = new Label(getGateStatusText(finalMazeUnlocked, enoughGold, endingCompleted));
        status.setAlignment(Pos.CENTER);
        status.setWrapText(true);
        status.setPrefWidth(CARD_WIDTH - 56);
        status.setMaxWidth(CARD_WIDTH - 56);
        status.setStyle("-fx-text-fill: #e1d9f0; -fx-font-size: 15px; -fx-line-spacing: 3px;");

        content.getChildren().addAll(title, gateIcon, status);

        if (perishGateUnlocked || endingCompleted) {
            Label price = new Label(endingCompleted ? "Gate sealed" : "Seal Cost: " + FINAL_SEAL_PRICE + " gold");
            price.setAlignment(Pos.CENTER);
            price.setMaxWidth(CARD_WIDTH - 56);
            price.setStyle("-fx-text-fill: #ffd56b; -fx-font-size: 20px; -fx-font-weight: bold;");
            content.getChildren().add(price);
        }

        Button button = createGateButton(perishGateUnlocked, enoughGold, endingCompleted);
        button.setOnAction(event -> handleGateClick(finalMazeUnlocked, enoughGold, endingCompleted));
        content.getChildren().add(button);

        card.getChildren().add(content);
        return card;
    }

    private StackPane createGateIcon(boolean unlocked, boolean sealed) {
        StackPane icon = new StackPane();
        icon.setPrefSize(CARD_WIDTH - 72, 158);
        icon.setMaxSize(CARD_WIDTH - 72, 158);
        icon.setStyle("""
                -fx-background-color: rgba(5, 3, 12, 0.78);
                -fx-border-color: rgba(177, 103, 255, 0.56);
                -fx-border-width: 1.2;
                -fx-background-radius: 14;
                -fx-border-radius: 14;
                """);

        Circle aura = new Circle(48);
        aura.setTranslateY(-12);
        aura.setFill(Color.rgb(119, 58, 190, unlocked ? 0.24 : 0.10));
        aura.setStroke(Color.rgb(182, 111, 255, unlocked ? 0.78 : 0.32));
        aura.setStrokeWidth(2.0);
        aura.setEffect(new Glow(unlocked ? 0.72 : 0.20));

        Polygon crystal = new Polygon(0.0, -48.0, 18.0, -18.0, 0.0, 18.0, -18.0, -18.0);
        crystal.setTranslateY(-10);
        crystal.setFill(Color.rgb(186, 94, 255, unlocked ? 0.94 : 0.48));
        crystal.setStroke(Color.rgb(231, 204, 255, unlocked ? 0.96 : 0.46));
        crystal.setStrokeWidth(1.2);
        crystal.setEffect(new Glow(unlocked ? 0.86 : 0.28));

        Label stateText = new Label(sealed ? "SEALED" : unlocked ? "OPEN" : "LOCKED");
        stateText.setTranslateY(52);
        stateText.setAlignment(Pos.CENTER);
        stateText.setMaxWidth(CARD_WIDTH - 90);
        stateText.setStyle(sealed
                ? "-fx-text-fill: #bff0c2; -fx-font-size: 18px; -fx-font-weight: bold;"
                : unlocked
                ? "-fx-text-fill: #d39cff; -fx-font-size: 18px; -fx-font-weight: bold;"
                : "-fx-text-fill: #b8bfcc; -fx-font-size: 18px; -fx-font-weight: bold;");

        icon.getChildren().add(aura);

        if (unlocked || sealed) {
            ImageView tower = new ImageView();
            try {
                tower.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/tower.png"))));
            } catch (Exception ignored) {
                icon.getChildren().add(crystal);
            }

            tower.setFitWidth(58);
            tower.setFitHeight(58);
            tower.setPreserveRatio(true);
            tower.setSmooth(false);
            tower.setTranslateY(-11);
            tower.setOpacity(sealed ? 0.95 : 0.88);
            tower.setEffect(new Glow(0.85));

            if (tower.getImage() != null) {
                icon.getChildren().add(tower);
            }
        } else {
            icon.getChildren().add(crystal);
        }

        icon.getChildren().add(stateText);
        return icon;
    }

    private String getGateStatusText(boolean finalMazeUnlocked, boolean enoughGold, boolean endingCompleted) {
        if (endingCompleted) return "The Perish Gate is sealed. The mazes are no longer available.";
        if (!userProfile.isLevelUnlocked(2)) return "The path to the Gate is hidden. Unlock Dark Corridors first.";
        if (!finalMazeUnlocked) return "The path to the Gate is hidden. Unlock the Final Trial first.";
        if (!enoughGold) return "The Gate is weakened, but not enough. Keep fighting in the last maze and gather more gold.";
        return "You have enough gold. Seal the Gate when you are ready to finish the story.";
    }

    private Button createGateButton(boolean finalMazeUnlocked, boolean enoughGold, boolean endingCompleted) {
        String text;

        if (endingCompleted) {
            text = "Story completed";
        } else if (!finalMazeUnlocked) {
            text = "Locked";
        } else if (!enoughGold) {
            text = "Not enough gold";
        } else {
            text = "Seal Gate";
        }

        Button button = new Button(text);
        button.setPrefSize(190, 50);
        button.setFocusTraversable(false);
        button.setDisable(endingCompleted);
        button.setStyle(gateButtonStyle(enoughGold && finalMazeUnlocked && !endingCompleted, false));
        button.setOnMouseEntered(event -> button.setStyle(gateButtonStyle(enoughGold && finalMazeUnlocked && !endingCompleted, true)));
        button.setOnMouseExited(event -> button.setStyle(gateButtonStyle(enoughGold && finalMazeUnlocked && !endingCompleted, false)));
        return button;
    }

    private void handleGateClick(boolean finalMazeUnlocked, boolean enoughGold, boolean endingCompleted) {
        if (endingCompleted) return;

        if (!userProfile.isLevelUnlocked(2)) {
            messageLabel.setText("Unlock Dark Corridors before approaching the Perish Gate.");
            messageLabel.setStyle("-fx-text-fill: #ffcf8a; -fx-font-size: 17px;");
            return;
        }

        if (!finalMazeUnlocked) {
            messageLabel.setText("Unlock the Final Trial before approaching the Perish Gate.");
            messageLabel.setStyle("-fx-text-fill: #ffcf8a; -fx-font-size: 17px;");
            return;
        }

        if (!enoughGold) {
            new StoryDialogWindow().show(currentStage, "Guardian", "The Gate is weaker now, but it cannot be sealed yet. You need " + FINAL_SEAL_PRICE + " gold for the final seal. Return to the mazes, defeat more enemies, and bring enough power back to the Gate.", "Back", null);
            return;
        }

        new StoryDialogWindow().show(currentStage, "Guardian", "You have gathered enough gold. It carries the power of every enemy you defeated. Use it now to restore the Tower Core and seal the Perish Gate forever.", "Seal the Gate", this::sealPerishGate);
    }

    private void sealPerishGate() {
        if (!userProfile.spendGold(FINAL_SEAL_PRICE)) return;

        userProfile.setEndingCompleted(true);
        userStorage.saveResources(userProfile);
        updateGoldLabel();
        rebuildCards();

        messageLabel.setText("The Perish Gate is sealed. The story is complete.");
        messageLabel.setStyle("-fx-text-fill: #bff0c2; -fx-font-size: 17px;");

        new StoryDialogWindow().show(currentStage, "Guardian", "The light has returned to the Tower. The Perish Gate is sealed, and the maze can no longer create enemies. You did not only survive the Maze of Perish. You ended it.", "Continue", null);
    }

    private String gateButtonStyle(boolean ready, boolean hover) {
        if (!ready) {
            return """
                    -fx-background-color: #3b322c;
                    -fx-border-color: #5c4a3e;
                    -fx-border-width: 3;
                    -fx-text-fill: #9f9187;
                    -fx-font-family: "Jersey 10";
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 0;
                    -fx-border-radius: 0;
                    -fx-cursor: default;
                    -fx-opacity: 0.82;
                    """;
        }

        String background = hover ? "#3b322c" : "#ecc8ad";
        String textColor = hover ? "#f0e6df" : "#2c221b";
        String borderColor = hover ? "#5c4e45" : "#ffffff #5c4a3e #5c4a3e #ffffff";

        return """
                -fx-background-color: %s;
                -fx-border-color: %s;
                -fx-border-width: 3;
                -fx-text-fill: %s;
                -fx-font-family: "Jersey 10";
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-background-radius: 0;
                -fx-border-radius: 0;
                -fx-cursor: hand;
                """.formatted(background, borderColor, textColor);
    }

    private Button createActionButton(String text, boolean disabled) {
        Button button = new Button(text);
        button.setPrefSize(165, 48);
        button.setFocusTraversable(false);
        button.setDisable(disabled);
        button.setStyle(actionButtonStyle(false, disabled));
        button.setOnMouseEntered(event -> button.setStyle(actionButtonStyle(true, disabled)));
        button.setOnMouseExited(event -> button.setStyle(actionButtonStyle(false, disabled)));
        return button;
    }

    private String actionButtonStyle(boolean hover, boolean disabled) {
        if (disabled) {
            return """
                    -fx-background-color: #3b322c;
                    -fx-border-color: #5c4a3e;
                    -fx-border-width: 3;
                    -fx-text-fill: #9f9187;
                    -fx-font-family: "Jersey 10";
                    -fx-font-size: 20px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 0;
                    -fx-border-radius: 0;
                    -fx-cursor: default;
                    -fx-opacity: 0.82;
                    """;
        }

        String background = hover ? "#3b322c" : "#ecc8ad";
        String textColor = hover ? "#f0e6df" : "#2c221b";
        String borderColor = hover ? "#5c4e45" : "#ffffff #5c4a3e #5c4a3e #ffffff";

        return """
                -fx-background-color: %s;
                -fx-border-color: %s;
                -fx-border-width: 3;
                -fx-text-fill: %s;
                -fx-font-family: "Jersey 10";
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-background-radius: 0;
                -fx-border-radius: 0;
                -fx-cursor: hand;
                """.formatted(background, borderColor, textColor);
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
        rebuildCards();

        if (level.number() == 2) {
            if (userProfile.isLevelUnlocked(3)) {
                messageLabel.setText("Dark Corridors purchased. Both required maps are open. The Perish Gate is now available on the right.");
            } else {
                messageLabel.setText("Dark Corridors purchased. The Tower begins to answer your victories.");
            }
            messageLabel.setStyle("-fx-text-fill: #bff0c2; -fx-font-size: 17px;");
            showLevel2Story();
        } else if (level.number() == 3) {
            if (userProfile.isLevelUnlocked(2)) {
                messageLabel.setText("Final Trial purchased. Both required maps are open. The Perish Gate is now available on the right.");
            } else {
                messageLabel.setText("Final Trial purchased. Unlock Dark Corridors too before approaching the Perish Gate.");
            }
            messageLabel.setStyle("-fx-text-fill: #bff0c2; -fx-font-size: 17px;");
            showLevel3Story();
        } else {
            messageLabel.setText("Map " + level.number() + " purchased. You can play it now!");
            messageLabel.setStyle("-fx-text-fill: #bff0c2; -fx-font-size: 17px;");
        }
    }

    private void showLevel2Story() {
        Platform.runLater(() -> new StoryDialogWindow().show(currentStage, "Guardian", "The Dark Corridors are open. The shadows are deeper there, but the Tower has begun to answer your victories. Keep gathering gold. It will open the final path and feed the power of the last seal.", "Continue", null));
    }

    private void showLevel3Story() {
        String storyText = userProfile.isLevelUnlocked(2)
                ? "The Final Trial is open. Each victory there weakens the Perish Gate and fills your gold with power. Keep fighting in the last maze, and when you are ready, return to the Gate on the right to seal it forever."
                : "The Final Trial is open, but the Perish Gate is still hidden. Unlock Dark Corridors too, then both required maps will be open and the Gate will become available.";

        Platform.runLater(() -> new StoryDialogWindow().show(currentStage, "Guardian", storyText, "Enter Final Maze", null));
    }

    private String getDefaultMessage() {
        if (userProfile.isEndingCompleted()) return "The story is complete. The Perish Gate is sealed, so the mazes are no longer available.";
        if (userProfile.isLevelUnlocked(2) && userProfile.isLevelUnlocked(3)) return "Replay the Final Trial to gather enough gold, then use the Perish Gate on the right to finish the story.";
        if (userProfile.isLevelUnlocked(3)) return "Unlock Dark Corridors too. The Perish Gate opens only when both required maps are unlocked.";
        return "Defeat enemies to earn gold. Unlock new maps and prepare for the final seal.";
    }

    private void updateGoldLabel() {
        if (goldLabel != null) goldLabel.setText(String.valueOf(userProfile.getGold()));
    }
}
