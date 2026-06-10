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

        for (int[] direction : DIRECTIONS) {
            zoneOffsets.add(direction);

            ImageView zoneView = new ImageView(zoneTexture);
            zoneView.setSmooth(false);

            zoneViews.add(zoneView);
            viewGroup.getChildren().add(zoneView);
        }
    }

    public Group getViewGroup(GameMap gameMap, int tileSize, DefenseManager defenseManager) {
        mainView.setFitWidth(tileSize);
        mainView.setFitHeight(tileSize);
        mainView.setX(getCol() * tileSize);
        mainView.setY(getRow() * tileSize);

        for (int i = 0; i < zoneViews.size(); i++) {
            ImageView zoneView = zoneViews.get(i);
            int[] offset = zoneOffsets.get(i);
            int targetRow = getRow() + offset[0];
            int targetCol = getCol() + offset[1];
            if (isDrawableZoneTile(targetRow, targetCol, offset[0], offset[1], gameMap)) {
                zoneView.setVisible(true);
                zoneView.setFitWidth(tileSize);
                zoneView.setFitHeight(tileSize);
                zoneView.setX(targetCol * tileSize);
                zoneView.setY(targetRow * tileSize);
            } else {
                zoneView.setVisible(false);
            }
        }
        return viewGroup;
    }

    private boolean isDrawableZoneTile(int targetRow, int targetCol, int rowOffset, int colOffset, GameMap gameMap) {
        if (!gameMap.isInside(targetRow, targetCol)) return false;
        if (isBlockedCell(targetRow, targetCol, gameMap)) return false;
        if (isDiagonal(rowOffset, colOffset) && isDiagonalBlockedByWalls(rowOffset, colOffset, gameMap)) return false;
        return true;
    }

    private boolean isBlockedCell(int row, int col, GameMap gameMap) {
        CellType cellType = gameMap.getCell(row, col);
        return cellType == CellType.WALL || cellType == CellType.SPAWN || cellType == CellType.TOWER;
    }

    private boolean isDiagonal(int rowOffset, int colOffset) {
        return Math.abs(rowOffset) == 1 && Math.abs(colOffset) == 1;
    }

    private boolean isDiagonalBlockedByWalls(int rowOffset, int colOffset, GameMap gameMap) {
        int sideRow = getRow() + rowOffset;
        int sideCol = getCol();

        int verticalRow = getRow();
        int verticalCol = getCol() + colOffset;

        boolean horizontalSideBlocked = isBlockedCell(sideRow, sideCol, gameMap);
        boolean verticalSideBlocked = isBlockedCell(verticalRow, verticalCol, gameMap);

        return horizontalSideBlocked && verticalSideBlocked;
    }

    public boolean coversTile(int targetRow, int targetCol, GameMap gameMap, DefenseManager defenseManager) {
        if (getRow() == targetRow && getCol() == targetCol) return true;

        for (int[] offset : DIRECTIONS) {
            int row = getRow() + offset[0];
            int col = getCol() + offset[1];
            if (row == targetRow && col == targetCol) {
                return isDrawableZoneTile(row, col, offset[0], offset[1], gameMap);
            }
        }

        return false;
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
