package ua.edu.ukma.entity.enemy;

import javafx.animation.Animation;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.model.GameMap;

public class Wanderer extends Enemy {

    public Wanderer(double startX, double startY, GameMap gameMap, int tileSize) {
        super(startX, startY, 0.8, 4, 100, 0, "/enemies/wanderer.png", gameMap, tileSize);
        loadAnimations();
        playAnimation(walkAnimation);
    }

    @Override
    public void update() {
       super.update();
    }
}