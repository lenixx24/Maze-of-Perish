package ua.edu.ukma.model.defense;

import ua.edu.ukma.entity.enemy.Enemy;

public class EffectZone extends DefenseStructure {

    private final double radius;
    private final double damagePerSecond;
    private final double slowPercentage;
    private double timeLeft;

    public EffectZone(int row, int col,DefenseType type, double radius, double damagePerSecond, double slowPercentage, double duration) {
        super(row, col, type);
        this.radius = radius;
        this.damagePerSecond = damagePerSecond;
        this.slowPercentage = slowPercentage;
        this.timeLeft = duration;
    }

    public void updateLifetime(double deltaTime) {
        this.timeLeft -= deltaTime;
    }

    public boolean isExpired() {
        return timeLeft <= 0;
    }

    public boolean isEnemyInRange(Enemy enemy, int tileSize) {
        double zoneX = (getCol() + 0.5) * tileSize;
        double zoneY = (getRow() + 0.5) * tileSize;
        double enemyX = enemy.getX();
        double enemyY = enemy.getY();
        double distance = Math.sqrt(Math.pow(zoneX - enemyX, 2) + Math.pow(zoneY - enemyY, 2));
        return distance <= (radius * tileSize);
    }

    public double getDamagePerSecond() { return damagePerSecond; }
    public double getSlowPercentage() { return slowPercentage; }
}
