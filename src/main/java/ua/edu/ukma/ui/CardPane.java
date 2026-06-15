package ua.edu.ukma.ui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import ua.edu.ukma.exception.AssetLoadingException;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.resource.*;
import java.util.Objects;

public class CardPane extends StackPane {

    private final Button[] cardButtons = new Button[DefenseType.values().length];
    private final Label[] nameLabels = new Label[DefenseType.values().length];
    private final DefenseController controller;
    private final CardManager cardManager;
    private final ManaManager manaManager;
    private final GoldManager goldManager;
    private final GameMapView gameMapView;

    private final ProgressBar manaBar = new ProgressBar(0);
    private final ProgressBar goldBar = new ProgressBar(0);
    private final Label HUDCenterLabel = new Label("LVL");
    private final Label manaTextLabel = new Label("0");
    private final Label goldTextLabel = new Label("0");

    private final HBox cardsRow = new HBox();

    public CardPane(DefenseController controller, CardManager cardManager, ManaManager manaManager, GameMapView gameMapView, GoldManager goldManager) {
        this.controller = controller;
        this.cardManager = cardManager;
        this.manaManager = manaManager;
        this.goldManager = goldManager;
        this.gameMapView = gameMapView;

        this.getStyleClass().add("card-pane-root");
        Font.loadFont(getClass().getResourceAsStream("/font/jersey10.ttf"), 16);
        String cssPath = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
        this.getStylesheets().add(cssPath);

        cardsRow.setAlignment(Pos.CENTER);
        cardsRow.getStyleClass().add("card-pane");

        HBox hudLineContainer = buildHUDLines();

        this.getChildren().addAll(cardsRow, hudLineContainer);
        StackPane.setAlignment(hudLineContainer, Pos.TOP_CENTER);

        initializeCards();
    }

    private HBox buildHUDLines() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        container.setMouseTransparent(true);
        container.setPrefHeight(20);
        manaBar.getStyleClass().add("hud-bar-mana");
        manaBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(manaBar, Priority.ALWAYS);

        goldBar.getStyleClass().add("hud-bar-gold");
        goldBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(goldBar, Priority.ALWAYS);
        goldBar.setScaleX(-1);
        manaTextLabel.getStyleClass().add("hud-digits-mana");
        manaTextLabel.setPrefWidth(50);
        manaTextLabel.setAlignment(Pos.CENTER_LEFT);
        goldTextLabel.getStyleClass().add("hud-digits-gold");
        goldTextLabel.setPrefWidth(60);
        goldTextLabel.setAlignment(Pos.CENTER_RIGHT);
        HUDCenterLabel.getStyleClass().add("hud-center-badge");
        Region leftSquare = new Region();
        leftSquare.getStyleClass().add("hud-pixel-square");
        Region rightSquare = new Region();
        rightSquare.getStyleClass().add("hud-pixel-square");
        container.getChildren().addAll(
                manaTextLabel,
                manaBar,
                leftSquare,
                HUDCenterLabel,
                rightSquare,
                goldBar,
                goldTextLabel
        );
        container.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, 10, 0.0, 0, 1));
        return container;
    }

    private void initializeCards() {
        cardsRow.getChildren().clear();
        DefenseType[] hand = cardManager.getHand();

        for (int i = 0; i < hand.length; i++) {
            final int index = i;
            DefenseType type = hand[index];

            Pane container = new Pane();
            container.getStyleClass().add("card-container");

            Label nameLabel = new Label(type.getName());
            nameLabel.getStyleClass().add("card-label");
            nameLabel.setMouseTransparent(true);

            nameLabel.widthProperty().addListener((obs, oldVal, newVal) -> {
                nameLabel.setLayoutX((63 - newVal.doubleValue()) / 2);
            });

            Button btn = new Button();
            btn.getStyleClass().add("card-button");
            btn.setFocusTraversable(false);

            try {
                String imageName = type.name().toLowerCase() + ".png";
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/" + imageName)));
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(32);
                imgView.setFitHeight(32);
                imgView.setPreserveRatio(true);
                imgView.setSmooth(false);
                btn.setGraphic(imgView);
            } catch (Exception e) {
                throw new AssetLoadingException("no picture found");
            }

            btn.setOnAction(e -> {
                int count = cardManager.getCardAmount(index);
                int currentMana = manaManager.getMana();

                if (count > 0 && currentMana >= type.manaCost()) {
                    if (controller.getSelectedType() == type) {
                        controller.resetSelection();
                        animateLabel(nameLabel, 20, 0.0, 60);
                    } else {
                        for (Label label : nameLabels) {
                            if (label != null && label.getOpacity() > 0) {
                                animateLabel(label, 20, 0.0, 60);
                            }
                        }

                        controller.selectCard(index, type);
                        animateLabel(nameLabel, -17, 1.0, 80);
                    }
                }

                if (gameMapView != null) {
                    gameMapView.requestFocus();
                }
            });
            container.getChildren().addAll(nameLabel, btn);
            cardButtons[index] = btn;
            nameLabels[index] = nameLabel;
            cardsRow.getChildren().add(container);}
    }
    private void animateLabel(Label label, double targetY, double targetOpacity, int durationMs) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(durationMs),
                        new KeyValue(label.layoutYProperty(), targetY),
                        new KeyValue(label.opacityProperty(), targetOpacity)
                )
        );
        timeline.play();
    }
    public void updateUI() {
        DefenseType[] hand = cardManager.getHand();
        int currentMana = manaManager.getMana();
        int currentGold = goldManager.getGold();
        manaBar.setProgress((double) currentMana / 100.0);
        manaTextLabel.setText(String.valueOf(currentMana));

        if (gameMapView != null && gameMapView.getGoldManager() != null) {
            goldBar.setProgress((double) currentGold / 500.0);
            goldTextLabel.setText(String.valueOf(currentGold));
        }

        for (int i = 0; i < hand.length; i++) {
            Button btn = cardButtons[i];
            DefenseType type = hand[i];
            int count = cardManager.getCardAmount(i);

            if (btn == null) continue;
            btn.getStyleClass().removeAll("zero-count", "no-mana", "selected-card");

            AnchorPane cardOverlay = new AnchorPane();
            cardOverlay.setPrefSize(49, 49);
            cardOverlay.setMouseTransparent(true);
            try {
                String imageName = type.name().toLowerCase() + ".png";
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/" + imageName)));
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(32);
                imgView.setFitHeight(32);
                imgView.setPreserveRatio(true);
                imgView.setSmooth(false);

                AnchorPane.setTopAnchor(imgView, 8.0);
                AnchorPane.setRightAnchor(imgView, 23.0);
                cardOverlay.getChildren().add(imgView);
            } catch (Exception e) {
                System.err.println(type.name());
            }
            Label countLabel = new Label(count + "x");
            countLabel.getStyleClass().add("card-count-text");
            AnchorPane.setTopAnchor(countLabel, -6.0);
            AnchorPane.setLeftAnchor(countLabel, 43.5);

            StackPane manaBadge = new StackPane();
            Circle manaCircle = new Circle(9);
            manaCircle.getStyleClass().add("mana-circle");

            Label manaLabel = new Label(String.valueOf(type.manaCost()));
            manaLabel.getStyleClass().add("mana-text");
            manaBadge.getChildren().addAll(manaCircle, manaLabel);

            AnchorPane.setBottomAnchor(manaBadge, -2.0);
            AnchorPane.setLeftAnchor(manaBadge, 42.0);

            cardOverlay.getChildren().addAll(countLabel, manaBadge);
            btn.setGraphic(cardOverlay);

            if (count <= 0) {
                btn.setDisable(true);
                btn.getStyleClass().add("zero-count");

                if (nameLabels[i] != null && nameLabels[i].getOpacity() > 0) {
                    animateLabel(nameLabels[i], 20, 0.0, 60);
                }
            } else {
                btn.setDisable(false);

                if (currentMana < type.manaCost()) {
                    btn.getStyleClass().add("no-mana");
                    if (nameLabels[i].getOpacity() > 0) {
                        animateLabel(nameLabels[i], 20, 0.0, 60);
                    }
                } else {
                    if (controller.getSelectedType() == type) {
                        btn.getStyleClass().add("selected-card");
                    } else {
                        if (nameLabels[i].getOpacity() > 0) {
                            animateLabel(nameLabels[i], 20, 0.0, 60);
                        }
                    }
                }
            }
        }
    }
}