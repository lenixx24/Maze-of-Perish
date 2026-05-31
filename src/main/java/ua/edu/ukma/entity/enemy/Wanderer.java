package ua.edu.ukma.entity.enemy;

import javafx.animation.Animation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;

public class Wanderer extends Enemy {
    private SpriteAnimation walkAnimation;
    private SpriteAnimation deathAnimation;
    private SpriteAnimation currentAnimation;
    private boolean isDying = false;

    public Wanderer(double startX, double startY) {
        super(startX, startY, 1.5, 100, 0, "/enemies/wanderer.png");
        loadAnimations();
        playAnimation(walkAnimation);
    }

    private void loadAnimations() {
        walkAnimation = new SpriteAnimation(
                imageView, Duration.millis(600),
                8, 4, 0, 0, 64, 64
        );
        walkAnimation.setCycleCount(Animation.INDEFINITE);
        deathAnimation = new SpriteAnimation(
                imageView, Duration.millis(800),
                8, 4, 0, 0, 64, 64
        );
        deathAnimation.setCycleCount(1);
    }

    private void playAnimation(SpriteAnimation newAnimation) {
        if (currentAnimation != null) {
            currentAnimation.stop();
        }
        currentAnimation = newAnimation;
        currentAnimation.play();
    }

    @Override
    public void update() {
        if (!active) return;

        if (health <= 0 && !isDying) {
            isDying = true;
            playAnimation(deathAnimation);
            deathAnimation.setOnFinished(e -> {
                onDeath();
            });
            return;
        }
        if (!isDying) {
            imageView.setX(x);
            imageView.setY(y);
        }
    }
}