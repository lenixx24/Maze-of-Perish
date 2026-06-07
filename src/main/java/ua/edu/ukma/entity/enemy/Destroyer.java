package ua.edu.ukma.entity.enemy;

import javafx.animation.Animation;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.model.GameMap;

public class Destroyer extends Enemy {


    public Destroyer(double startX, double startY, GameMap gameMap, int tileSize) {
        super(startX, startY, 1.3, 100, 1, "/enemies/destroyer.png", gameMap, tileSize);
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