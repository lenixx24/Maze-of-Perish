package ua.edu.ukma.entity.enemy;

import javafx.animation.Animation;
import javafx.util.Duration;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;

public class Ram extends Enemy {


    public Ram(double startX, double startY, GameMap gameMap, int tileSize) {
        super(startX, startY, 0.4, 15, 300, 2, "/enemies/ram.png", gameMap, tileSize);
        loadAnimations();
        playAnimation(walkAnimation);
    }


}