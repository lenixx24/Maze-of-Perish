package ua.edu.ukma.model.defense.type;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.BarrierZone;
import ua.edu.ukma.model.defense.DefenseType;

public class Barrier extends BarrierZone {

    private final Group viewGroup;
    private final ImageView mainView;

    public Barrier(int row, int col) {
        super(row, col, DefenseType.BARRIER, 100, 20);
        this.viewGroup = new Group();
        this.viewGroup.setMouseTransparent(true);

        Image mainTexture = new Image(DefenseType.BARRIER.texturePath());

        this.mainView = new ImageView(mainTexture);
        this.mainView.setSmooth(false);
        this.viewGroup.getChildren().add(mainView);
    }

    public Group getViewGroup(GameMap gameMap, int tileSize) {
        this.mainView.setFitWidth(tileSize);
        this.mainView.setFitHeight(tileSize);
        this.mainView.setX(getCol() * tileSize);
        this.mainView.setY(getRow() * tileSize);

        return viewGroup;
    }
}