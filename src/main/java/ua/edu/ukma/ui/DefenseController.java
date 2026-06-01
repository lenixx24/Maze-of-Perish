package ua.edu.ukma.ui;

import javafx.scene.input.KeyCode;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.entity.player.Player;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.model.defense.DefenseType;

import java.util.Map;
import java.util.Optional;

public class DefenseController {

    private final Map<KeyCode, DefenseType> defenseControls = Map.ofEntries(
            Map.entry(KeyCode.DIGIT4, DefenseType.TRAP),
            Map.entry(KeyCode.DIGIT5, DefenseType.BOMB),
            Map.entry(KeyCode.DIGIT6, DefenseType.TURRET),
            Map.entry(KeyCode.DIGIT7, DefenseType.FREEZE),
            Map.entry(KeyCode.DIGIT8, DefenseType.POISON_CLOUD),
            Map.entry(KeyCode.DIGIT9, DefenseType.BARRIER),
            Map.entry(KeyCode.DIGIT0, DefenseType.SNIPER_TOWER),
            Map.entry(KeyCode.E, DefenseType.LASER_TOWER),
            Map.entry(KeyCode.T, DefenseType.CANNON_TOWER)
    );

    private DefenseType selectedType = null;

    public void handle(KeyCode code) {
        Optional.ofNullable(defenseControls.get(code))
                .ifPresent(type -> {
                    this.selectedType = type;
                });
    }

    public void buildDefense(double clickX, double clickY, double availableWidth, double availableHeight,
                             GameMap gameMap, DefenseManager defenseManager, Player player) {

        if (selectedType == null) {
            return;
        }

        if (player.isMoving()) {
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

        switch (selectedType) {
            case BOMB, TRAP, TURRET, FREEZE, POISON_CLOUD, BARRIER, SNIPER_TOWER, LASER_TOWER, CANNON_TOWER -> {
                defenseManager.addDefense(new AttackTower(targetRow, targetCol, selectedType, 10, 10, 10, 10));
            }
            default -> {
                return;
            }
        }
        resetSelection();
    }

    public DefenseType getSelectedType() {
        return selectedType;
    }

    public void resetSelection() {
        this.selectedType = null;
    }
}