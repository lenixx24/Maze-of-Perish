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
                        if (bomb.isAnimationFinished()) iterator.remove();
                    } else {
                        checkTrapTrigger(bomb, activeEnemies, tileSize, bomb::explode);
                    }
                }
                else if (trap instanceof Trap trapp) {
                    if (trapp.isEat()) {
                        if (trapp.isAnimationFinished()) iterator.remove();
                    } else {
                        checkTrapTrigger(trapp, activeEnemies, tileSize, trapp::eat);
                    }
                }
            }
            else if (defense instanceof AttackTower tower) {
                tower.updateTower(activeEnemies, gameMap, tileSize, deltaTime);
                if (tower instanceof Turret) {
                    if (tower.isDestroyed()) {
                        onSolidStructureDestroyed(tower, activeEnemies, tileSize);
                        for (AttackTower.FxBullet bullet : tower.bullets) bullet.removeView();
                        iterator.remove();
                    }
                }else if (tower.isDestroyed()) {
                    for (AttackTower.FxBullet bullet : tower.bullets) bullet.removeView();
                    iterator.remove();
                }
            }
            else if (defense instanceof EffectZone zone) {
                zone.updateLifetime(deltaTime);
                if (zone.isExpired()) {
                    if (zone instanceof Freeze) {
                        restoreSpeedForZone(zone, activeEnemies, tileSize);
                    }
                    iterator.remove();
                    continue;
                }
                handleEffectZoneInfluence(zone, activeEnemies, tileSize, deltaTime);
            }
            else if (defense instanceof BarrierZone barrier) {
                barrier.updateLifetime(deltaTime);
                handleSolidStructureContact(barrier, activeEnemies, tileSize);

                if (barrier.isDestroyed()) {
                    onSolidStructureDestroyed(barrier, activeEnemies, tileSize);
                    iterator.remove();
                }
            }
        }
    }

    private void handleSolidStructureContact(DefenseStructure structure, List<Enemy> activeEnemies, int tileSize) {
        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive() && isEnemyOnStructureTile(enemy, structure, tileSize)) {
                if (!frozenEnemies.containsKey(enemy)) {
                    frozenEnemies.put(enemy, enemy.getSpeed());
                }
                enemy.setSpeed(enemy.getSpeed() * 0.9);

                if (structure instanceof AttackTower tower) tower.takeDamage(1);
                else if (structure instanceof BarrierZone barrier) barrier.takeDamage(1);
            }
        }
    }

    private void onSolidStructureDestroyed(DefenseStructure structure, List<Enemy> activeEnemies, int tileSize) {
        for (Enemy enemy : activeEnemies) {
            if (isEnemyOnStructureTile(enemy, structure, tileSize)) {
                restoreEnemySpeed(enemy);
            }
        }
    }

    private boolean isEnemyOnStructureTile(Enemy enemy, DefenseStructure structure, int tileSize) {
        return enemy.getRow(tileSize) == structure.getRow() && enemy.getCol(tileSize) == structure.getCol();
    }

    private void handleEffectZoneInfluence(EffectZone zone, List<Enemy> activeEnemies, int tileSize, double deltaTime) {
        for (Enemy enemy : activeEnemies) {
            if (!enemy.isActive()) continue;

            if (zone.isEnemyInRange(enemy, tileSize)) {
                if (zone.getDamagePerSecond() > 0) {
                    enemy.takeDamage((int) (zone.getDamagePerSecond() * deltaTime));
                }
                if (zone instanceof Freeze) {
                    if (!frozenEnemies.containsKey(enemy)) {
                        frozenEnemies.put(enemy, enemy.getSpeed());
                    }
                    enemy.setSpeed(enemy.getSpeed() * 0.3);
                }
            } else if (zone instanceof Freeze) {
                boolean insideAnotherFreeze = activeDefenses.stream()
                        .filter(d -> d != zone && d instanceof Freeze && !((Freeze) d).isExpired())
                        .anyMatch(f -> ((Freeze) f).isEnemyInRange(enemy, tileSize));
                if (!insideAnotherFreeze) {
                    restoreEnemySpeed(enemy);
                }
            }
        }
    }

    private void restoreSpeedForZone(EffectZone zone, List<Enemy> activeEnemies, int tileSize) {
        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive() && zone.isEnemyInRange(enemy, tileSize)) {
                restoreEnemySpeed(enemy);
            }
        }
    }

    private void checkTrapTrigger(DefenseStructure trap, List<Enemy> activeEnemies, int tileSize, Runnable triggerAction) {
        for (Entity enemy : activeEnemies) {
            if (enemy.isActive() &&
                    enemy.getRow(tileSize) == trap.getRow() &&
                    enemy.getCol(tileSize) == trap.getCol()) {
                triggerAction.run();
                break;
            }
        }
    }

    private void restoreEnemySpeed(Enemy enemy) {
        if (frozenEnemies.containsKey(enemy)) {
            enemy.setSpeed(frozenEnemies.get(enemy));
            frozenEnemies.remove(enemy);
        }
    }

    public boolean hasDefense(int row, int col, GameMap gameMap, DefenseType type) {
        for (DefenseStructure d : activeDefenses) {
            if (d.getRow() == row && d.getCol() == col) {
                return true;
            }
            if (d instanceof EffectZone zone && zone.coversTile(row, col, gameMap, this)) {
                if (type == DefenseType.FREEZE || type == DefenseType.POISON_CLOUD || type == DefenseType.BARRIER) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasDefenseExcept(int row, int col, DefenseStructure excludedDefense, EffectZone currentZone) {
        return true;
    }

    public boolean canPlaceZoneAt(int row, int col, DefenseStructure excludedDefense) {
        int currentCenterRow = excludedDefense.getRow();
        int currentCenterCol = excludedDefense.getCol();

        for (DefenseStructure d : activeDefenses) {
            if (d == excludedDefense) continue;
            if (ZoneDist(currentCenterRow, currentCenterCol, d)) return false;
        }
        return true;
    }

    public static boolean ZoneDist(int currentCenterRow, int currentCenterCol, DefenseStructure d) {
        if (d instanceof Freeze || d instanceof Poison) {
            int rowDiff = Math.abs(d.getRow() - currentCenterRow);
            int colDiff = Math.abs(d.getCol() - currentCenterCol);
            return rowDiff <= 2 && colDiff <= 2;
        }
        return false;
    }

    public void addDefense(DefenseStructure defense) { activeDefenses.add(defense); }
    public List<DefenseStructure> getActiveDefenses() { return activeDefenses; }
    public void clear() { activeDefenses.clear(); }
}