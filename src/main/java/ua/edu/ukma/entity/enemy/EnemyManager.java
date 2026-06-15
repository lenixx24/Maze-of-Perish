package ua.edu.ukma.entity.enemy;


import javafx.scene.layout.Pane;
import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.model.CellPosition;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.music.AudioManager;
import ua.edu.ukma.ui.GameMapView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EnemyManager {
    public int towerHP=5;
    private final List<Enemy> enemies;
    private final Pane gamePane;
    private final GameMap gameMap;
    private final List<CellPosition> spawnPoints;
    private final Random random;
    private final int tileSize;
    private final DefenseManager defenseManager;
    public EnemyManager(Pane gamePane, GameMap gameMap, int tileSize, DefenseManager defenseManager) {
        this.gamePane = gamePane;
        this.enemies = new ArrayList<>();
        this.gameMap=gameMap;
        this.spawnPoints = new ArrayList<>();
        this.random = new Random();
        this.tileSize=tileSize;
        this.defenseManager=defenseManager;
        findSpawnPoints();
    }

    public void addEnemy(Enemy enemy) {
       if(enemy==null) return;
        enemies.add(enemy);
        gamePane.getChildren().add(enemy.getImageView());
    }
    public boolean update(AudioManager audioManager) {
        Iterator<Enemy> iterator = enemies.iterator();
        GameMapView mapView = (gamePane instanceof GameMapView) ? (GameMapView) gamePane : null;

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.update(defenseManager, mapView, audioManager);
            if (!enemy.isActive()) {
                if (enemy.isReachedTower()){
                    towerHP--;
                    audioManager.playSoundEffect("/audio/tower_damage.mp3");
                }
                System.out.println(towerHP);
                removeEnemyFromScene(enemy);
                iterator.remove();
            }
        }
        return towerHP > 0;
    }
    private void findSpawnPoints() {
        for (int row = 0; row < gameMap.rows(); row++) {
            for (int col = 0; col < gameMap.cols(); col++) {
                if (gameMap.getCell(row, col) == CellType.SPAWN) {
                    spawnPoints.add(new CellPosition(row, col));
                }
            }
        }

        if (spawnPoints.isEmpty())
            System.err.println("No spawn");
    }

    public void spawnEnemy() {
        if (spawnPoints.isEmpty()) return;
        CellPosition spawnCell = spawnPoints.get(random.nextInt(spawnPoints.size()));
        double startX = spawnCell.col() * tileSize;
        double startY = spawnCell.row() * tileSize;
        Enemy newEnemy = switch (random.nextInt(3)) {
            case 0 -> new Wanderer(startX, startY, gameMap, tileSize);
            case 1 -> new Ram(startX, startY, gameMap, tileSize);
            case 2 -> new Destroyer(startX, startY, gameMap, tileSize);
            default -> null;
        };

        addEnemy(newEnemy);
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
    public void stopAllAnimations(){
        for (Entity enemy : enemies) {
            if (enemy.getCurrentAnimation() != null) {
            enemy.getCurrentAnimation().stop();}
        }
    }

    public void resumeAllAnimations(){
        for (Entity enemy : enemies) {
            if (enemy.getCurrentAnimation() != null) {
            enemy.getCurrentAnimation().play();
            }
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
