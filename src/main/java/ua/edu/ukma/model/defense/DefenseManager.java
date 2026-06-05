package ua.edu.ukma.model.defense;

import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.enemy.Enemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefenseManager {

    private final List<DefenseStructure> activeDefenses = new ArrayList<>();

    public void updateDefenses(List<Entity> activeEnemies, int tileSize, double deltaTime) {
        Iterator<DefenseStructure> iterator = activeDefenses.iterator();

        while (iterator.hasNext()) {
            DefenseStructure defense = iterator.next();

            if (defense instanceof DisposableTrap trap) {
                for (Entity enemy : activeEnemies) {
                    if (enemy.isActive() &&
                            enemy.getRow(tileSize) == trap.getRow() &&
                            enemy.getCol(tileSize) == trap.getCol()) {
                        enemy.takeDamage((int) trap.getDamage());
                        iterator.remove();
                        break;
                    }
                }
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
