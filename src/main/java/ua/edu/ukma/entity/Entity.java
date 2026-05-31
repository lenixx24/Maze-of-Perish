package ua.edu.ukma.entity;
import javafx.scene.canvas.GraphicsContext;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double speed;
    protected int health;
    protected int maxHealth;
    protected boolean active;
    public Entity(double x, double y, double speed, int maxHealth) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.active = true;
    }
    public abstract void update();
    public abstract void render(GraphicsContext gc);

    public void takeDamage(int amount) {
        if (!active) return;

        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            this.active = false;
            onDeath();
        }
    }

    protected void onDeath() {
        // Базова логіка деактивації сутності
        this.active = false;
    }

    public int getGridX(int tileSize) {
        return (int) ((x + tileSize / 2.0) / tileSize);
    }

    public int getGridY(int tileSize) {
        return (int) ((y + tileSize / 2.0) / tileSize);
    }


    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public int getHealth() { return health; }
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public int getMaxHealth() { return maxHealth; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}