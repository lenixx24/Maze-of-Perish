package ua.edu.ukma.model.defense.type;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.entity.SpriteSheet;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.DisposableTrap;

public class Trap extends DisposableTrap {
    private boolean eat = false;
    private boolean animationFinished = false;

    private final SpriteSheet<Integer> spriteSheet;
    private final ImageView imageView;
    private SpriteAnimation eatAnimation;

    public Trap(int row, int col) {
        super(row, col, DefenseType.TRAP, 1.0, 1.0);
        this.spriteSheet = new SpriteSheet<>(
                DefenseType.TRAP.texturePath(),
                frame -> frame,
                frame -> 0,
                frame -> 32,
                frame -> 32
        );

        this.imageView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.imageView, 0);
    }

    public void eat() {
        if (eat) return;
        this.eat = true;
        this.eatAnimation = new SpriteAnimation(
                this.imageView,
                Duration.millis(300),
                2, 2,
                32, 0,
                32, 32
        );
        this.imageView.setViewport(new Rectangle2D(32, 0, 32, 32));

        this.eatAnimation.setCycleCount(1);
        this.eatAnimation.setOnFinished(event -> {
            this.animationFinished = true;
        });

        this.eatAnimation.play();
    }

    public ImageView getImageView(int tileSize) {
        double targetSize = eat ? (tileSize* 0.8) : (tileSize * 0.6);
        this.imageView.setFitWidth(targetSize);
        this.imageView.setFitHeight(targetSize);
        double offsetX = getCol() * tileSize + (tileSize - targetSize) / 2.0;
        double offsetY = getRow() * tileSize + (tileSize - targetSize) / 2.0;

        this.imageView.setX(offsetX);
        this.imageView.setY(offsetY);
        if (eat) {
            this.imageView.toFront();
        }

        return imageView;
    }

    public boolean isEat() {
        return eat;
    }

    public boolean isAnimationFinished() {
        return animationFinished;
    }
}
