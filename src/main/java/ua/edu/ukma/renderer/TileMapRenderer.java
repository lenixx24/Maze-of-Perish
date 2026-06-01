package ua.edu.ukma.renderer;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.exception.AssetLoadingException;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TileMapRenderer implements Renderer<GameMap> {

    private final int tileSize;

    private final Map<CellType, Image> textures = new HashMap<>();

    public TileMapRenderer(int tileSize) {
        this.tileSize = tileSize;
    }

    @Override
    public Node render(GameMap gameMap) {
        Pane pane = new Pane();

        pane.setPrefSize(gameMap.cols() * tileSize, gameMap.rows() * tileSize);

        for (int row = 0; row < gameMap.rows(); row++) {
            for (int col = 0; col < gameMap.cols(); col++) {
                CellType cellType = gameMap.getCell(row, col);

                ImageView tile = new ImageView(getTexture(cellType));
                tile.setFitWidth(tileSize);
                tile.setFitHeight(tileSize);
                tile.setSmooth(false);
                tile.setLayoutX(col * tileSize);
                tile.setLayoutY(row * tileSize);

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

        return new Image(stream, GameScaleConfig.IMAGE_SIZE, GameScaleConfig.IMAGE_SIZE, false, false);
    }
}