package ua.edu.ukma.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import ua.edu.ukma.exception.AssetLoadingException;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.resource.CardManager;
import ua.edu.ukma.resource.ManaManager;

import java.util.Objects;

public class CardPane extends HBox {

    private final Button[] cardButtons = new Button[DefenseType.values().length];
    private final Label[] nameLabels = new Label[DefenseType.values().length];
    private final DefenseController controller;
    private final CardManager cardManager;
    private final ManaManager manaManager;
    private final GameMapView gameMapView;

    public CardPane(DefenseController controller, CardManager cardManager, ManaManager manaManager, GameMapView gameMapView) {
        this.controller = controller;
        this.cardManager = cardManager;
        this.manaManager = manaManager;
        this.gameMapView = gameMapView;

        this.setSpacing(6);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("card-pane");
        Font.loadFont(getClass().getResourceAsStream("/font/jersey10.ttf"), 16);
        String cssPath = Objects.requireNonNull(getClass().getResource("/css/card-pane.css")).toExternalForm();
        this.getStylesheets().add(cssPath);

        initializeCards();
    }

    private void initializeCards() {
        this.getChildren().clear();
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
                nameLabel.setLayoutX((85 - newVal.doubleValue()) / 2);
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
                    } else {
                        controller.selectCard(index, type);
                    }
                }
                if (gameMapView != null) {
                    gameMapView.requestFocus();
                }
            });

            container.setOnMouseEntered(e -> {
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.millis(80),
                                new KeyValue(nameLabel.layoutYProperty(), -22),
                                new KeyValue(nameLabel.opacityProperty(), 1.0)
                        )
                );
                fadeIn.play();
            });

            container.setOnMouseExited(e -> {
                Timeline fadeOut = new Timeline(
                        new KeyFrame(Duration.millis(60),
                                new KeyValue(nameLabel.layoutYProperty(), 20),
                                new KeyValue(nameLabel.opacityProperty(), 0.0)
                        )
                );
                fadeOut.play();
            });

            container.getChildren().clear();
            container.getChildren().addAll(nameLabel, btn);

            cardButtons[index] = btn;
            nameLabels[index] = nameLabel;
            this.getChildren().add(container);
        }

    }
    public void updateUI() {
        DefenseType[] hand = cardManager.getHand();
        int currentMana = manaManager.getMana();

        for (int i = 0; i < hand.length; i++) {
            Button btn = cardButtons[i];
            DefenseType type = hand[i];
            int count = cardManager.getCardAmount(i);

            btn.getStyleClass().removeAll("zero-count", "no-mana", "selected-card");

            if (count <= 0) {
                btn.setText("\n");
                btn.getStyleClass().add("zero-count");
                continue;
            }

            if (currentMana < type.manaCost()) {
                btn.setText(type.manaCost() + "\n(" + count + ")");
                btn.getStyleClass().add("no-mana");
            } else {
                btn.setText(type.manaCost() + "\n(" + count + ")");

                if (controller.getSelectedType() == type) {
                    btn.getStyleClass().add("selected-card");
                }
            }
        }
    }
}