package ua.edu.ukma.ui;

import ua.edu.ukma.entity.Direction;
import ua.edu.ukma.entity.player.Player;
import ua.edu.ukma.model.CellPosition;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.model.defense.DefenseStructure;
import ua.edu.ukma.model.defense.DefenseType;

import java.util.LinkedHashSet;
import java.util.Set;

public final class BuildPlacementHelper {

    private static final int ZONE_RADIUS = 1;

    private BuildPlacementHelper() {
    }

    public static Set<CellPosition> calculateAvailableCells(GameMap gameMap, DefenseManager defenseManager, Player player, int tileSize, DefenseType selectedType) {
        Set<CellPosition> availableCells = new LinkedHashSet<>();

        if (selectedType == null || player.isMoving()) return availableCells;

        int playerRow = player.getRow(tileSize);
        int playerCol = player.getCol(tileSize);

        for (Direction direction : Direction.values()) {
            int row = playerRow + direction.rowDelta();
            int col = playerCol + direction.colDelta();

            while (gameMap.isPassable(row, col)) {
                if (canPlaceOnCell(row, col, selectedType, gameMap, defenseManager)) {
                    availableCells.add(new CellPosition(row, col));
                }

                row += direction.rowDelta();
                col += direction.colDelta();
            }
        }

        return availableCells;
    }

    public static boolean isAvailableCell(int row, int col, GameMap gameMap, DefenseManager defenseManager, Player player, int tileSize, DefenseType selectedType) {
        return calculateAvailableCells(gameMap, defenseManager, player, tileSize, selectedType).contains(new CellPosition(row, col));
    }

    private static boolean canPlaceOnCell(int row, int col, DefenseType type, GameMap gameMap, DefenseManager defenseManager) {
        CellType cellType = gameMap.getCell(row, col);
        if (cellType == CellType.WALL || cellType == CellType.SPAWN || cellType == CellType.TOWER) return false;
        if (hasDefenseExactlyOnCell(row, col, defenseManager)) return false;
        if (isZoneDefense(type) && wouldZoneOverlapExistingZoneDefense(row, col, gameMap, defenseManager)) return false;
        return true;
    }

    private static boolean hasDefenseExactlyOnCell(int row, int col, DefenseManager defenseManager) {
        for (DefenseStructure defense : defenseManager.getActiveDefenses()) {
            if (defense.getRow() == row && defense.getCol() == col) return true;
        }

        return false;
    }

    private static boolean wouldZoneOverlapExistingZoneDefense(int row, int col, GameMap gameMap, DefenseManager defenseManager) {
        Set<CellPosition> newZoneCells = calculateZoneCells(row, col, gameMap);

        for (DefenseStructure defense : defenseManager.getActiveDefenses()) {
            if (!isZoneDefense(defense.getType())) continue;
            Set<CellPosition> existingZoneCells = calculateZoneCells(defense.getRow(), defense.getCol(), gameMap);
            for (CellPosition cell : newZoneCells) {
                if (existingZoneCells.contains(cell)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Set<CellPosition> calculateZoneCells(int centerRow, int centerCol, GameMap gameMap) {
        Set<CellPosition> zoneCells = new LinkedHashSet<>();

        for (int rowOffset = -ZONE_RADIUS; rowOffset <= ZONE_RADIUS; rowOffset++) {
            for (int colOffset = -ZONE_RADIUS; colOffset <= ZONE_RADIUS; colOffset++) {
                int row = centerRow + rowOffset;
                int col = centerCol + colOffset;
                if (!gameMap.isPassable(row, col)) continue;
                if (isDiagonal(rowOffset, colOffset) && isDiagonalBlockedByWalls(centerRow, centerCol, rowOffset, colOffset, gameMap)) continue;

                zoneCells.add(new CellPosition(row, col));
            }
        }

        return zoneCells;
    }

    private static boolean isDiagonal(int rowOffset, int colOffset) {
        return Math.abs(rowOffset) == 1 && Math.abs(colOffset) == 1;
    }

    private static boolean isDiagonalBlockedByWalls(int centerRow, int centerCol, int rowOffset, int colOffset, GameMap gameMap) {
        int sideRow = centerRow + rowOffset;
        int sideCol = centerCol;

        int verticalRow = centerRow;
        int verticalCol = centerCol + colOffset;

        boolean horizontalSideBlocked = !gameMap.isPassable(sideRow, sideCol);
        boolean verticalSideBlocked = !gameMap.isPassable(verticalRow, verticalCol);

        return horizontalSideBlocked && verticalSideBlocked;
    }

    private static boolean isZoneDefense(DefenseType type) {
        return type == DefenseType.FREEZE || type == DefenseType.POISON_CLOUD;
    }
}