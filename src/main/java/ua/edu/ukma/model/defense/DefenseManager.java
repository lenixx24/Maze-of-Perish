package ua.edu.ukma.model.defense;

import ua.edu.ukma.entity.enemy.Enemy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefenseManager {

    private final List<DefenseStructure> activeDefenses = new ArrayList<>();

    public void updateDefenses(List<Enemy> activeEnemies, int tileSize) {
        Iterator<DefenseStructure> iterator = activeDefenses.iterator();

        while (iterator.hasNext()) {
            DefenseStructure defense = iterator.next();

            if (defense instanceof DisposableTrap trap) {
                for (Enemy enemy : activeEnemies) {
                    if (enemy.isActive() &&
                            enemy.getRow(tileSize) == trap.getRow() &&
                            enemy.getCol(tileSize) == trap.getCol()) {
                        enemy.takeDamage((int) trap.getDamage());
                        iterator.remove();
                        break;
                    }
                }
            }



        }
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
