package ua.edu.ukma.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ua.edu.ukma.resource.GoldManager;

import java.util.Objects;

public class Gold extends Pane {
    private final ImageView imageView;
    private final int tileY;
    private final int tileX;
    private final GoldManager goldManager;
    private final GameMapView gameMapView;
    private final int goldReward;

    private static final int FRAME_COUNT = 4;
    private static final double FRAME_WIDTH = 415.0 / FRAME_COUNT;
    private static final double FRAME_HEIGHT = 80.0;
    private int currentFrame = 0;

    public Gold(int tileY, int tileX, double pixelX, double pixelY, int tileSize, GoldManager goldManager, GameMapView gameMapView, int goldReward) {
        this.tileY = tileY;
        this.tileX = tileX;
        this.goldManager = goldManager;
        this.gameMapView = gameMapView;
        this.goldReward = goldReward;

        Image coinSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/gold.png")));
        this.imageView = new ImageView(coinSheet);
        this.imageView.setViewport(new Rectangle2D(0, 0, FRAME_WIDTH, FRAME_HEIGHT));
        this.imageView.setFitWidth(tileSize);
        this.imageView.setFitHeight(tileSize);
        this.imageView.setPreserveRatio(true);

        this.getChildren().add(imageView);

        this.setLayoutX(pixelX + (tileSize - (double) tileSize) / 2);
        this.setLayoutY(pixelY + (tileSize - (double) tileSize) / 2);

        startAnimation();
    }
    private void startAnimation() {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(140), e -> {
                    currentFrame = (currentFrame + 1) % FRAME_COUNT;

                    double xOffset = currentFrame * FRAME_WIDTH;

                    imageView.setViewport(new Rectangle2D(xOffset, 0, FRAME_WIDTH, FRAME_HEIGHT));
                })
        );
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }
    public void collect() {
        goldManager.addGold(goldReward);
        if (gameMapView != null) {
            gameMapView.removeGold(this);
        }
    }

    public int getGoldReward() {
        return this.goldReward;
    }

    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }
}