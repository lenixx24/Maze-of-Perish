package ua.edu.ukma.model.defense;

import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.type.*;
import java.util.*;

public class DefenseManager {

    private final List<DefenseStructure> activeDefenses = new ArrayList<>();

    public void updateDefenses(GameMap gameMap, List<Enemy> activeEnemies, int tileSize, double deltaTime) {
        Iterator<DefenseStructure> iterator = activeDefenses.iterator();

        while (iterator.hasNext()) {
            DefenseStructure defense = iterator.next();

            if (defense instanceof DisposableTrap trap) {
                if (trap instanceof Bomb bomb) {
                    if (bomb.isExploded()) {
                        if (bomb.isAnimationFinished()) {
                            iterator.remove();
                        }
                    }
                    else {
                        for (Entity enemy : activeEnemies) {
                            if (enemy.isActive() &&
                                    enemy.getRow(tileSize) == bomb.getRow() &&
                                    enemy.getCol(tileSize) == bomb.getCol()) {
                                    bomb.explode();
                                break;
                            }
                        }
                    }
                }
                if (trap instanceof Trap trapp) {
                    if (trapp.isEat()) {
                        if (trapp.isAnimationFinished()) {
                            iterator.remove();
                        }
                    }
                    else {
                        for (Entity enemy : activeEnemies) {
                            if (enemy.isActive() &&
                                    enemy.getRow(tileSize) == trapp.getRow() &&
                                    enemy.getCol(tileSize) == trapp.getCol()) {
                                trapp.eat();
                                break;
                            }
                        }
                    }
                }
            }
            else if (defense instanceof Turret turret) {
                turret.updateTurret(activeEnemies, gameMap, tileSize, deltaTime);
            }
            else if (defense instanceof EffectZone zone) {
                zone.updateLifetime(deltaTime);
                if (zone.isExpired()) {
                    iterator.remove();
                    continue;
                }
                for (Entity enemy : activeEnemies) {
                    if (enemy.isActive() && zone.isEnemyInRange((Enemy) enemy, tileSize)) {
                        if (zone.getDamagePerSecond() > 0) {
                            enemy.takeDamage((int) (zone.getDamagePerSecond() * deltaTime));
                }}}}
            else if (defense instanceof BarrierZone barrier) {
                barrier.updateLifetime(deltaTime);
                if (barrier.isDestroyed()) {
                    iterator.remove();
                }
            }
        }
    }
    public boolean hasDefense(int row, int col) {
        return activeDefenses.stream()
                .anyMatch(d -> d.getRow() == row && d.getCol() == col);
    }
    public void addDefense(DefenseStructure defense) {
        activeDefenses.add(defense);
    }

    public List<DefenseStructure> getActiveDefenses() {
        return activeDefenses;
    }

    public void clear() {
        activeDefenses.clear();
    }
}