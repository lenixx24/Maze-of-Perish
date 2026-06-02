package ua.edu.ukma.entity.enemy;


import javafx.scene.layout.Pane;
import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.model.GameMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyManager {

    private final List<Entity> enemies;
    private final Pane gamePane;

    public EnemyManager(Pane gamePane, GameMap gameMap, int tileSize) {
        this.gamePane = gamePane;
        this.enemies = new ArrayList<>();
        addEnemies(gameMap, tileSize);
    }

    private void addEnemies(GameMap gameMap, int tileSize) {
        System.out.println(tileSize);
        spawnEnemy(new Wanderer(tileSize, tileSize-4, gameMap, tileSize));
        spawnEnemy(new Ram(tileSize*2, tileSize-4, gameMap, tileSize));
        spawnEnemy(new Destroyer(tileSize*3, tileSize-4, gameMap, tileSize));

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
