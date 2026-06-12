package ua.edu.ukma.model.defense;

import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.type.*;
import java.util.*;

public class DefenseManager {

    private final List<DefenseStructure> activeDefenses = new ArrayList<>();
    private final Map<Enemy, Double> originalSpeeds = new HashMap<>();

    public void updateDefenses(GameMap gameMap, List<Enemy> activeEnemies, int tileSize, double deltaTime) {
        originalSpeeds.keySet().removeIf(enemy -> !activeEnemies.contains(enemy) || !enemy.isActive());

        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive() && !originalSpeeds.containsKey(enemy)) {
                originalSpeeds.put(enemy, enemy.getSpeed());
            }
        }

        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive()) {
                enemy.setSpeed(originalSpeeds.get(enemy));
            }
        }

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
                if (tower.isDestroyed()) {
                    if (tower instanceof Laser laser) {
                        laser.cleanUp();
                    }
                    for (AttackTower.FxBullet bullet : tower.getBullets()) {
                        bullet.removeView();
                    }
                    iterator.remove();
                }
            }
            else if (defense instanceof EffectZone zone) {
                zone.updateLifetime(deltaTime);
                if (zone.isExpired()) {
                    iterator.remove();
                    continue;
                }
                handleEffectZoneInfluence(zone, activeEnemies, tileSize, deltaTime);
            }
            else if (defense instanceof BarrierZone barrier) {
                barrier.updateLifetime(deltaTime);
                handleSolidStructureContact(barrier, activeEnemies, tileSize);

                if (barrier.isDestroyed()) {
                    iterator.remove();
                }
            }
        }
    }

    private void handleSolidStructureContact(DefenseStructure structure, List<Enemy> activeEnemies, int tileSize) {
        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive() && isEnemyOnStructureTile(enemy, structure, tileSize)) {
                enemy.setSpeed(enemy.getSpeed() * 0.05);

                if (structure instanceof AttackTower tower) tower.takeDamage(1);
                else if (structure instanceof BarrierZone barrier) barrier.takeDamage(1);
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
                    enemy.setSpeed(enemy.getSpeed() * 0.3);
                }
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
    public DefenseStructure getDefenseAt (int row, int col) {
        for (DefenseStructure d : activeDefenses) {
            if (d.getRow() == row && d.getCol() == col) {
                return d;
            }
        }
        return null;
    }
    public boolean isAttackable(DefenseType defenseType){
            return defenseType.equals(DefenseType.BARRIER)||
                    defenseType.equals(DefenseType.TURRET)||
                    defenseType.equals(DefenseType.SNIPER_TOWER)||
                    defenseType.equals(DefenseType.LASER_TOWER)||
                    defenseType.equals(DefenseType.CANNON_TOWER);
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