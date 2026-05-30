package ua.edu.ukma.renderer;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ua.edu.ukma.exception.AssetLoadingException;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TileMapRenderer implements Renderer<GameMap> {

    private static final int TILE_SIZE = 48;

    private final Map<CellType, Image> textures = new HashMap<>();

    @Override
    public Node render(GameMap gameMap) {
        Pane pane = new Pane();

        pane.setPrefSize(gameMap.cols() * TILE_SIZE, gameMap.rows() * TILE_SIZE);

        for (int row = 0; row < gameMap.rows(); row++) {
            for (int col = 0; col < gameMap.cols(); col++) {
                CellType cellType = gameMap.getCell(row, col);

                ImageView tile = new ImageView(getTexture(cellType));
                tile.setFitWidth(TILE_SIZE);
                tile.setFitHeight(TILE_SIZE);
                tile.setLayoutX(col * TILE_SIZE);
                tile.setLayoutY(row * TILE_SIZE);

                pane.getChildren().add(tile);
            }
        }

        return pane;
    }

    private Image getTexture(CellType cellType) {
        return textures.computeIfAbsent(cellType, this::loadTexture);
    }

    private Image loadTexture(CellType cellType) {
        String path = cellType.texturePath();

        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            throw new AssetLoadingException("Cannot load tile texture: " + path);
        }

        return new Image(stream);
    }
}