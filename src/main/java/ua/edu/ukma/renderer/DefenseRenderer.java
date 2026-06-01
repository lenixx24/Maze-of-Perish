package ua.edu.ukma.renderer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.model.defense.DefenseStructure;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefenseRenderer {

    private final Map<DefenseStructure, ImageView> views = new HashMap<>();
    private final Pane gamePane;

    public DefenseRenderer(Pane gamePane) {
        this.gamePane = gamePane;
    }

    public void render(DefenseManager defenseManager, int tileSize) {
        views.entrySet().removeIf(entry -> {
            DefenseStructure defense = entry.getKey();
            if (!defenseManager.getActiveDefenses().contains(defense)) {
                gamePane.getChildren().remove(entry.getValue());
                return true;
            }
            return false;
        });

        for (DefenseStructure defense : defenseManager.getActiveDefenses()) {
            ImageView imageView = views.get(defense);

            if (imageView == null) {
                try {
                    String path = defense.getType().texturePath();
                    Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

                    imageView = new ImageView(img);
                    imageView.setFitWidth(32);
                    imageView.setFitHeight(32);
                    imageView.setPreserveRatio(true);
                    imageView.setMouseTransparent(true);

                    views.put(defense, imageView);
                    gamePane.getChildren().add(imageView);

                } catch (Exception e) {
                    continue;
                }
            }

            double pixelX = defense.getCol() * tileSize + (tileSize - imageView.getFitWidth()) / 2.0;
            double pixelY = defense.getRow() * tileSize + (tileSize - imageView.getFitHeight()) / 2.0;

            if (imageView.getLayoutX() != pixelX) imageView.setLayoutX(pixelX);
            if (imageView.getLayoutY() != pixelY) imageView.setLayoutY(pixelY);
        }
    }
}

