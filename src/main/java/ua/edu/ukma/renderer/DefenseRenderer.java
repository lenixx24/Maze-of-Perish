package ua.edu.ukma.renderer;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.model.defense.DefenseStructure;
import ua.edu.ukma.model.defense.type.*;

import java.util.HashMap;
import java.util.Map;

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

    public void render(GameMap gameMap, DefenseManager defenseManager, int tileSize) {
        views.keySet().removeIf(defense -> {
            if (!defenseManager.getActiveDefenses().contains(defense)) {
                gamePane.getChildren().remove(views.get(defense));
                if (defense instanceof AttackTower tower) {
                    tower.getBullets().forEach(Turret.FxBullet::removeView);
                }
                return true;
            }
            return false;
        });

        for (DefenseStructure defense : defenseManager.getActiveDefenses()) {
            if (defense instanceof Laser laser) {
                Group currentGroup = laser.getViewGroup(tileSize);
                if (!gamePane.getChildren().contains(currentGroup)) {
                    gamePane.getChildren().add(currentGroup);
                }
                views.put(laser, currentGroup);
                continue;
            }
            if (defense instanceof Sniper sniper) {
                Group currentGroup = sniper.getViewGroup(tileSize);
                if (!gamePane.getChildren().contains(currentGroup)) {
                    gamePane.getChildren().add(currentGroup);
                }
                views.put(sniper, currentGroup);
                continue;
            }
            if (defense instanceof Cannon cannon) {
                Group currentGroup = cannon.getViewGroup(tileSize);
                if (!gamePane.getChildren().contains(currentGroup)) {
                    gamePane.getChildren().add(currentGroup);
                }
                views.put(cannon, currentGroup);
                continue;
            }
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
            if (defense instanceof Freeze freeze) {
                Group currentGroup = freeze.getViewGroup(gameMap, tileSize, defenseManager);

                if (!gamePane.getChildren().contains(currentGroup)) {
                    if (gamePane.getChildren().size() > 1) {
                        gamePane.getChildren().add(1, currentGroup);
                    } else {
                        gamePane.getChildren().add(currentGroup);
                    }
                }

                views.put(freeze, currentGroup);
                continue;
            }
            if (defense instanceof Poison poison) {
                var currentGroup = poison.getViewGroup(gameMap, tileSize,  defenseManager);

                if (!gamePane.getChildren().contains(currentGroup)) {
                    if (gamePane.getChildren().size() > 1) {
                        gamePane.getChildren().add(1, currentGroup);
                    } else {
                        gamePane.getChildren().add(currentGroup);
                    }
                }

                views.put(poison, currentGroup);
                continue;
            }
            if (defense instanceof Barrier barrier) {
                Group currentGroup = barrier.getViewGroup(gameMap, tileSize);

                if (!gamePane.getChildren().contains(currentGroup)) {
                    if (gamePane.getChildren().size() > 1) {
                        gamePane.getChildren().add(1, currentGroup);
                    } else {
                        gamePane.getChildren().add(currentGroup);
                    }
                }

                views.put(barrier, currentGroup);
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

