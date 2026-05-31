package ua.edu.ukma.entity;

import javafx.scene.canvas.GraphicsContext;

public class Enemy extends Entity {
    private final String enemyType; // "Wanderer", "Monolith" or "Ram"

    public Enemy(double startX, double startY, double speed, int maxHealth, String type) {
        super(startX, startY, speed, maxHealth);
        this.enemyType = type;
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