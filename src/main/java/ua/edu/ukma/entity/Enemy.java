package ua.edu.ukma.entity;

import javafx.scene.canvas.GraphicsContext;

public class Enemy extends Entity {
    private final int enemyType; // 0-"Wanderer", 1-"Monolith", 2-"Ram"

    public Enemy(double startX, double startY, double speed, int maxHealth, int type, String spriteSheetPath) {
        super(startX, startY, speed, maxHealth);
        this.enemyType = type;
        loadSpriteSheet(spriteSheetPath);
    }

    @Override
    public void update() {
        if (!active) return;

    }

    @Override
    public void render(GraphicsContext gc) {

    }

    @Override
    protected void onDeath() {
        super.onDeath();
    }
}