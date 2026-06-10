package ua.edu.ukma.entity.enemy;

import javafx.animation.Animation;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;

public class Destroyer extends Enemy {


    public Destroyer(double startX, double startY, GameMap gameMap, int tileSize) {
        super(startX, startY, 1.2, 10,100, 1, "/enemies/destroyer.png", gameMap, tileSize);
        loadAnimations();
        playAnimation(walkAnimation);
    }
}