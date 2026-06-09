package ua.edu.ukma.model.defense.type;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.EffectZone;

public class Freeze extends EffectZone {

    private final Group viewGroup;
    private final ImageView mainView;
    private final java.util.List<ImageView> zoneViews = new java.util.ArrayList<>();
    private final java.util.List<int[]> zoneOffsets = new java.util.ArrayList<>();

    public Freeze(int row, int col) {
        super(row, col, DefenseType.FREEZE,  1.5, 0.0, 50.0);
        this.viewGroup = new Group();
        this.viewGroup.setMouseTransparent(true);

        Image mainTexture = new Image(DefenseType.FREEZE.texturePath());
        Image zoneTexture = new Image("/defense/freeze1.png");

        this.mainView = new ImageView(mainTexture);
        this.mainView.setSmooth(false);
        this.viewGroup.getChildren().add(mainView);

        int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1},
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };

        for (int[] dir : directions) {
            zoneOffsets.add(dir);
            ImageView zv = new ImageView(zoneTexture);
            zv.setSmooth(false);
            this.zoneViews.add(zv);
            this.viewGroup.getChildren().add(zv);
        }
    }

    public Group getViewGroup(GameMap gameMap, int tileSize) {
        this.mainView.setFitWidth(tileSize);
        this.mainView.setFitHeight(tileSize);
        this.mainView.setX(getCol() * tileSize);
        this.mainView.setY(getRow() * tileSize);
        for (int i = 0; i < zoneViews.size(); i++) {
            ImageView zv = zoneViews.get(i);
            int[] offset = zoneOffsets.get(i);

            int targetRow = getRow() + offset[0];
            int targetCol = getCol() + offset[1];
            if (gameMap.isInside(targetRow, targetCol) && gameMap.getCell(targetRow, targetCol) != CellType.WALL && gameMap.getCell(targetRow, targetCol)!= CellType.SPAWN && gameMap.getCell(targetRow, targetCol)!= CellType.TOWER ) {
                zv.setVisible(true);
                zv.setFitWidth(tileSize);
                zv.setFitHeight(tileSize);
                zv.setX(targetCol * tileSize);
                zv.setY(targetRow * tileSize);
            } else {
                zv.setVisible(false);
            }
        }return viewGroup;
    }
}