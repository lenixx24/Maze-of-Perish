package ua.edu.ukma.entity.enemy;


import javafx.scene.layout.Pane;
import ua.edu.ukma.entity.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyManager {

    private final List<Entity> enemies;
    private final Pane gamePane;

    public EnemyManager(Pane gamePane) {
        this.gamePane = gamePane;
        this.enemies = new ArrayList<>();
        addEnemies();
    }

    private void addEnemies() {
        spawnEnemy(new Wanderer(64, 64));
        spawnEnemy(new Ram(64, 128));
        spawnEnemy(new Destroyer(128, 0));

    }

    public void spawnEnemy(Entity enemy) {
       if(enemy==null) return;
        enemies.add(enemy);
        gamePane.getChildren().add(enemy.getImageView());

    }
    public void update() {
        Iterator<Entity> iterator = enemies.iterator();

        while (iterator.hasNext()) {
            Entity enemy = iterator.next();
            enemy.update();

            if (!enemy.isActive()) {
                removeEnemyFromScene(enemy);
                iterator.remove();
            }
        }
    }
    private void removeEnemyFromScene(Entity enemy) {
            gamePane.getChildren().remove(enemy.getImageView());
    }

    public void clearAll() {
        for (Entity enemy : enemies) {
            removeEnemyFromScene(enemy);
        }
        enemies.clear();
    }
    public List<Entity> getEnemies() {
        return enemies;
    }
}
