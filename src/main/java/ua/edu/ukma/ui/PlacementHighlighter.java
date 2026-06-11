package ua.edu.ukma.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ua.edu.ukma.model.CellPosition;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.entity.player.Player;

import java.util.Set;

public class PlacementHighlighter {

    private final Group view = new Group();

    public PlacementHighlighter() {
        view.setMouseTransparent(true);
    }

    public Group getView() {
        return view;
    }

    public void render(GameMap gameMap, DefenseManager defenseManager, Player player, int tileSize, DefenseType selectedType) {
        view.getChildren().clear();

        Set<CellPosition> availableCells = BuildPlacementHelper.calculateAvailableCells(gameMap, defenseManager, player, tileSize, selectedType);

        for (CellPosition cell : availableCells) {
            Rectangle marker = new Rectangle(cell.col() * tileSize, cell.row() * tileSize, tileSize, tileSize);

            marker.setFill(Color.rgb(80, 210, 120, 0.32));
            marker.setStroke(Color.rgb(180, 255, 200, 0.9));
            marker.setStrokeWidth(0.7);
            marker.setArcWidth(tileSize * 0.25);
            marker.setArcHeight(tileSize * 0.25);
            marker.setMouseTransparent(true);

            view.getChildren().add(marker);
        }
    }

    public void clear() {
        view.getChildren().clear();
    }
}