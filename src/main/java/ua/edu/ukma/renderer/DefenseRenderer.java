package ua.edu.ukma.renderer;

import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.*;
import ua.edu.ukma.model.defense.*;
import ua.edu.ukma.model.defense.type.*;
import java.util.*;

public class DefenseRenderer {

    private final Map<DefenseStructure, Node> views = new HashMap<>();
    private final Pane gamePane;
    private final Map<Class<? extends DefenseStructure>, Image> staticTextures = new HashMap<>();

    public DefenseRenderer(Pane gamePane) {
        this.gamePane = gamePane;
        loadTextures();
    }
    private void loadTextures() {
    }

    public void render(DefenseManager defenseManager, int tileSize) {
        views.keySet().removeIf(defense -> {
            if (!defenseManager.getActiveDefenses().contains(defense)) {
                gamePane.getChildren().remove(views.get(defense));
                if (defense instanceof Turret turret) {
                    turret.getBullets().forEach(Turret.FxBullet::removeView);
                }
                return true;
            }
            return false;
        });

        for (DefenseStructure defense : defenseManager.getActiveDefenses()) {
            if (defense instanceof Turret turret) {
                Group currentGroup = turret.getViewGroup(tileSize);
                if (!gamePane.getChildren().contains(currentGroup)) {
                    gamePane.getChildren().add(currentGroup);
                }
                views.put(turret, currentGroup);
                continue;
            }
            if (defense instanceof Bomb bomb) {
                ImageView currentView = bomb.getImageView(tileSize);
                if (!gamePane.getChildren().contains(currentView)) {
                    gamePane.getChildren().add(currentView);
                }
                views.put(bomb, currentView);
                continue;
            }
            if (defense instanceof Trap trap) {
                ImageView currentView = trap.getImageView(tileSize);
                if (!gamePane.getChildren().contains(currentView)) {
                    gamePane.getChildren().add(currentView);
                }
                views.put(trap, currentView);
                continue;
            }
            if (!views.containsKey(defense)) {
                Image img = staticTextures.get(defense.getClass());
                if (img != null) {
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(tileSize);
                    imageView.setFitHeight(tileSize);
                    imageView.setX(defense.getCol() * tileSize);
                    imageView.setY(defense.getRow() * tileSize);

                    gamePane.getChildren().add(imageView);
                    views.put(defense, imageView);
                }
            }
        }
    }
}

