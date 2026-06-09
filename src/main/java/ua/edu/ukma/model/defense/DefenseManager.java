package ua.edu.ukma.model.defense;

import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.type.*;
import java.util.*;

public class DefenseManager {

    private final List<DefenseStructure> activeDefenses = new ArrayList<>();
    private final Map<Enemy, Double> frozenEnemies = new HashMap<>();

    public void updateDefenses(GameMap gameMap, List<Enemy> activeEnemies, int tileSize, double deltaTime) {
        frozenEnemies.keySet().removeIf(enemy -> !activeEnemies.contains(enemy) || !enemy.isActive());
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
            else if (defense instanceof Laser laser) {
                laser.updateTurret(activeEnemies, gameMap, tileSize, deltaTime);
            }
            else if (defense instanceof Sniper sniper) {
                sniper.updateTurret(activeEnemies, gameMap, tileSize, deltaTime);
            }
            else if (defense instanceof Cannon cannon) {
                cannon.updateTurret(activeEnemies, gameMap, tileSize, deltaTime);
            }
            else if (defense instanceof EffectZone zone) {
                zone.updateLifetime(deltaTime);
                if (zone.isExpired()) {
                    if (zone instanceof Freeze) {
                        for (Enemy enemy : activeEnemies) {
                            if (enemy.isActive() && zone.isEnemyInRange(enemy, tileSize)) {
                                restoreEnemySpeed(enemy);
                            }
                        }
                    }
                    iterator.remove();
                    continue;
                }

                for (Enemy enemy : activeEnemies) {
                    if (enemy.isActive()) {

                        if (zone.isEnemyInRange(enemy, tileSize)) {
                            if (zone.getDamagePerSecond() > 0) {
                                enemy.takeDamage((int) (zone.getDamagePerSecond() * deltaTime));
                            }
                            if (zone instanceof Freeze) {
                                if (!frozenEnemies.containsKey(enemy)) {
                                    frozenEnemies.put(enemy, enemy.getSpeed());
                                    enemy.setSpeed(enemy.getSpeed() * 0.3);
                                }
                            }
                        } else {
                            if (zone instanceof Freeze) {
                                boolean insideAnotherFreeze = activeDefenses.stream()
                                        .filter(d -> d != zone && d instanceof Freeze && !((Freeze) d).isExpired())
                                        .anyMatch(f -> ((Freeze) f).isEnemyInRange(enemy, tileSize));
                                if (!insideAnotherFreeze) {
                                    restoreEnemySpeed(enemy);
                                }
                            }
                        }
                    }
                }
            }
            else if (defense instanceof BarrierZone barrier) {
                barrier.updateLifetime(deltaTime);
                for (Enemy enemy : activeEnemies) {
                    if (enemy.isActive() &&
                            enemy.getRow(tileSize) == barrier.getRow() &&
                            enemy.getCol(tileSize) == barrier.getCol()) {

                       if (!frozenEnemies.containsKey(enemy)) {
                            frozenEnemies.put(enemy, enemy.getSpeed());
                        }
                        enemy.setSpeed(enemy.getSpeed() * 0.9);
                        barrier.takeDamage(1);
                    }
                }
                if (barrier.isDestroyed()) {
                    for (Enemy enemy : activeEnemies) {
                        if (enemy.getRow(tileSize) == barrier.getRow() &&
                                enemy.getCol(tileSize) == barrier.getCol()) {
                            restoreEnemySpeed(enemy);
                        }
                    }
                    iterator.remove();
                }
            }
        }
    }
    private void restoreEnemySpeed(Enemy enemy) {
        if (frozenEnemies.containsKey(enemy)) {
            enemy.setSpeed(frozenEnemies.get(enemy));
            frozenEnemies.remove(enemy);
        }
    }
    public boolean hasDefense(int row, int col) {
        for (DefenseStructure d : activeDefenses) {
            if (d.getRow() == row && d.getCol() == col) {
                return true;
            }
            if (d instanceof Freeze || d instanceof Poison) {
                int rowDiff = Math.abs(d.getRow() - row);
                int colDiff = Math.abs(d.getCol() - col);
                if (rowDiff <= 1 && colDiff <= 1) {
                    return true;
                }
            }
        }
        return false;
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