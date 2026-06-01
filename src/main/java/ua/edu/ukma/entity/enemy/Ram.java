package ua.edu.ukma.entity.enemy;

import javafx.animation.Animation;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;

public class Ram extends Enemy {


    public Ram(double startX, double startY) {
        super(startX, startY, 0.5, 300, 2, "/enemies/ram.png");
        loadAnimations();
        playAnimation(walkAnimation);
    }

    private void loadAnimations() {
        walkAnimation = new SpriteAnimation(
                imageView, Duration.millis(600),
                6, 3, 0, 0, 48, 48
        );
        walkAnimation.setCycleCount(Animation.INDEFINITE);
        deathAnimation = new SpriteAnimation(
                imageView, Duration.millis(800),
                6, 3, 0, 0, 48, 48
        );
        deathAnimation.setCycleCount(1);
    }



    @Override
    public void update() {
        super.update();
    }
}