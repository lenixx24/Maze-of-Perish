package ua.edu.ukma.model.defense.type;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.entity.SpriteSheet;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.DisposableTrap;

public class Bomb extends DisposableTrap {

    private boolean exploded = false;
    private boolean animationFinished = false;

    private final SpriteSheet<Integer> spriteSheet;
    private final ImageView imageView;
    private SpriteAnimation explosionAnimation;

    public Bomb(int row, int col) {
        super(row, col, DefenseType.BOMB, 2.5, 1.0);
        this.spriteSheet = new SpriteSheet<>(
                DefenseType.BOMB.texturePath(),
                frame -> frame * (32 + 2),
                frame -> 0,
                frame -> 32,
                frame -> 32
        );

        this.imageView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.imageView, 0);
    }

    public void explode() {
        if (exploded) return;
        this.exploded = true;
        this.explosionAnimation = new SpriteAnimation(
                this.imageView,
                Duration.millis(400),
                3, 4,
                34, 0,
                34, 32
        );
        this.imageView.setViewport(new Rectangle2D(34, 0, 34, 32));

        this.explosionAnimation.setCycleCount(1);
        this.explosionAnimation.setOnFinished(event -> {
            this.animationFinished = true;
        });

        this.explosionAnimation.play();
    }

    public ImageView getImageView(int tileSize) {
        double targetSize = exploded ? (tileSize*1.3) : (tileSize * 0.6);
        this.imageView.setFitWidth(targetSize);
        this.imageView.setFitHeight(targetSize);
        double offsetX = getCol() * tileSize + (tileSize - targetSize) / 2.0;
        double offsetY = getRow() * tileSize + (tileSize - targetSize) / 2.0;

        this.imageView.setX(offsetX);
        this.imageView.setY(offsetY);
        if (exploded) {
            this.imageView.toFront();
        }

        return imageView;
    }

    public boolean isExploded() {
        return exploded;
    }

    public boolean isAnimationFinished() {
        return animationFinished;
    }
}
