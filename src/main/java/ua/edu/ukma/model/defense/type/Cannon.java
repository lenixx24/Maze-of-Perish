package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.entity.enemy.Enemy;
import java.util.List;

public class Cannon extends AttackTower {

    private final double explosionRadius;

    public Cannon(int row, int col) {
        super(row, col, DefenseType.CANNON_TOWER, 80, 200.0, 400.0, 0.8, "/defense/cannon_bullet.png", 400.0);
        this.explosionRadius = 200.0;
    }

    @Override
    protected void handleBulletHit(FxBullet bullet, Enemy targetEnemy, List<Enemy> activeEnemies, int tileSize) {
        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive()) {
                double dx = enemy.getX() + (tileSize / 2.0) - bullet.getX();
                double dy = enemy.getY() + (tileSize / 2.0) - bullet.getY();
                double distance = Math.hypot(dx, dy);
                if (distance <= explosionRadius) {
                    enemy.takeDamage((int) this.damage);
                }
            }
        }
    }

    public double getExplosionRadius() {
        return explosionRadius;
    }
}