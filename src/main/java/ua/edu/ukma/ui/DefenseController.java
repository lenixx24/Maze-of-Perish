package ua.edu.ukma.ui;

import javafx.scene.input.KeyCode;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.entity.player.Player;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.type.*;
import ua.edu.ukma.resource.ManaManager;

import java.util.Map;
import java.util.Optional;

public class DefenseController {

    private final Map<KeyCode, DefenseType> defenseControls = Map.ofEntries(
            Map.entry(KeyCode.DIGIT1, DefenseType.TRAP),
            Map.entry(KeyCode.DIGIT2, DefenseType.BOMB),
            Map.entry(KeyCode.DIGIT3, DefenseType.TURRET),
            Map.entry(KeyCode.DIGIT4, DefenseType.FREEZE),
            Map.entry(KeyCode.DIGIT5, DefenseType.POISON_CLOUD),
            Map.entry(KeyCode.DIGIT6, DefenseType.BARRIER),
            Map.entry(KeyCode.DIGIT7, DefenseType.SNIPER_TOWER),
            Map.entry(KeyCode.DIGIT8, DefenseType.LASER_TOWER),
            Map.entry(KeyCode.DIGIT9, DefenseType.CANNON_TOWER)
    );

    private DefenseType selectedType = null;

    public void handle(KeyCode code) {
        Optional.ofNullable(defenseControls.get(code))
                .ifPresent(type -> {
                    this.selectedType = type;
                });
    }

    public void buildDefense(double clickX, double clickY, double availableWidth, double availableHeight,
                             GameMap gameMap, DefenseManager defenseManager, Player player, ManaManager manaManager) {

        if (selectedType == null) return;
        if (player.isMoving()) return;
        if (manaManager.getMana() < selectedType.manaCost()) {
            return;
        }

        int tileSize = GameScaleConfig.calculateTileSize(gameMap.rows(), gameMap.cols(), availableWidth, availableHeight);
        int targetCol = (int) (clickX / tileSize);
        int targetRow = (int) (clickY / tileSize);

        if (targetRow < 0 || targetRow >= gameMap.rows() || targetCol < 0 || targetCol >= gameMap.cols()) {
            return;
        }

        int playerRow = player.getRow(tileSize);
        int playerCol = player.getCol(tileSize);

        boolean isHorizontalLine = (playerRow == targetRow);
        boolean isVerticalLine = (playerCol == targetCol);

        if (!isHorizontalLine && !isVerticalLine) {
            return;
        }
        if (!putDefense(targetRow, targetCol, selectedType, gameMap, defenseManager)) {
            return;
        }

        switch (selectedType) {
            case TRAP -> defenseManager.addDefense(new Trap(targetRow, targetCol));
            case BOMB -> defenseManager.addDefense(new Bomb(targetRow, targetCol));
            case FREEZE -> defenseManager.addDefense(new Freeze(targetRow, targetCol));
            case POISON_CLOUD -> defenseManager.addDefense(new Poison(targetRow, targetCol));
            case BARRIER -> defenseManager.addDefense(new Barrier(targetRow, targetCol));
            case TURRET -> defenseManager.addDefense(new Turret(targetRow, targetCol));
            case LASER_TOWER -> defenseManager.addDefense(new Laser(targetRow, targetCol));
            case SNIPER_TOWER -> defenseManager.addDefense(new Sniper(targetRow, targetCol));
            case CANNON_TOWER  -> defenseManager.addDefense(new Cannon(targetRow, targetCol));
            default -> { return; }
        }
        manaManager.decreaseMana(selectedType.manaCost());
        resetSelection();
    }
    private boolean putDefense(int row, int col, DefenseType type, GameMap gameMap, DefenseManager defenseManager) {
        if (defenseManager.hasDefense(row, col, gameMap, type)) {
            return false;
        }

        CellType cellType = gameMap.getCell(row, col);
        if (cellType == CellType.WALL || cellType == CellType.SPAWN || cellType == CellType.TOWER) {
            return false;
        }
        if (type == DefenseType.FREEZE || type == DefenseType.POISON_CLOUD) {
            for (ua.edu.ukma.model.defense.DefenseStructure d : defenseManager.getActiveDefenses()) {
                if (DefenseManager.ZoneDist(row, col, d)) return false;
            }
        }

        return true;
    }

    public DefenseType getSelectedType() {
        return selectedType;
    }

    public void resetSelection() {
        this.selectedType = null;
    }
}