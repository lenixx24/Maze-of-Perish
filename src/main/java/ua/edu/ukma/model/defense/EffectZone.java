package ua.edu.ukma.model.defense;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import java.util.ArrayList;
import java.util.List;

public class EffectZone extends DefenseStructure {

    private final double radius;
    private final double damagePerSecond;
    private double timeLeft;
    protected final Group viewGroup;
    protected final ImageView mainView;
    protected final List<ImageView> zoneViews = new ArrayList<>();
    protected final List<int[]> zoneOffsets = new ArrayList<>();
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    public EffectZone(int row, int col, DefenseType type, double radius, double damagePerSecond, double duration, String zoneTexturePath) {
        super(row, col, type);
        this.radius = radius;
        this.damagePerSecond = damagePerSecond;
        this.timeLeft = duration;
        this.viewGroup = new Group();
        this.viewGroup.setMouseTransparent(true);
        this.mainView = new ImageView(new Image(type.texturePath()));
        this.mainView.setSmooth(false);
        this.viewGroup.getChildren().add(mainView);

        Image zoneTexture = new Image(zoneTexturePath);
        for (int[] dir : DIRECTIONS) {
            zoneOffsets.add(dir);
            ImageView zv = new ImageView(zoneTexture);
            zv.setSmooth(false);
            this.zoneViews.add(zv);
            this.viewGroup.getChildren().add(zv);
        }
    }
    public boolean isValidZoneTile(int targetRow, int targetCol, GameMap gameMap, DefenseManager defenseManager) {
        boolean isInside = gameMap.isInside(targetRow, targetCol);
        if (!isInside) return false;

        boolean isWalkable = gameMap.getCell(targetRow, targetCol) != CellType.WALL &&
                gameMap.getCell(targetRow, targetCol) != CellType.SPAWN &&
                gameMap.getCell(targetRow, targetCol) != CellType.TOWER;

        boolean isFarEnough = defenseManager.canPlaceZoneAt(targetRow, targetCol, this);
        boolean isTileFree = defenseManager.hasDefenseExcept(targetRow, targetCol, this, this);

        return isWalkable && isFarEnough && isTileFree;
    }

    public boolean coversTile(int targetRow, int targetCol, GameMap gameMap, DefenseManager defenseManager) {
        if (this.getRow() == targetRow && this.getCol() == targetCol) {
            return true;
        }

        for (int[] dir : DIRECTIONS) {
            int r = this.getRow() + dir[0];
            int c = this.getCol() + dir[1];
            if (r == targetRow && c == targetCol) {
                return isValidZoneTile(r, c, gameMap, defenseManager);
            }
        }
        return false;
    }

    public Group getViewGroup(GameMap gameMap, int tileSize, DefenseManager defenseManager) {
        this.mainView.setFitWidth(tileSize);
        this.mainView.setFitHeight(tileSize);
        this.mainView.setX(getCol() * tileSize);
        this.mainView.setY(getRow() * tileSize);

        for (int i = 0; i < zoneViews.size(); i++) {
            ImageView zv = zoneViews.get(i);
            int[] offset = zoneOffsets.get(i);
            int targetRow = getRow() + offset[0];
            int targetCol = getCol() + offset[1];
            if (isValidZoneTile(targetRow, targetCol, gameMap, defenseManager)) {
                zv.setVisible(true);
                zv.setFitWidth(tileSize);
                zv.setFitHeight(tileSize);
                zv.setX(targetCol * tileSize);
                zv.setY(targetRow * tileSize);
            } else {
                zv.setVisible(false);
            }
        }
        return viewGroup;
    }

    public void updateLifetime(double deltaTime) {
        this.timeLeft -= deltaTime;
    }

    public boolean isExpired() {
        return timeLeft <= 0;
    }

    public boolean isEnemyInRange(Enemy enemy, int tileSize) {
        double zoneX = (getCol() + 0.5) * tileSize;
        double zoneY = (getRow() + 0.5) * tileSize;

        double enemyX = enemy.getX() + (tileSize / 2.0);
        double enemyY = enemy.getY() + (tileSize / 2.0);

        double distance = Math.sqrt(Math.pow(zoneX - enemyX, 2) + Math.pow(zoneY - enemyY, 2));
        return distance <= (radius * tileSize);
    }
    public double getDamagePerSecond() { return damagePerSecond; }

}
