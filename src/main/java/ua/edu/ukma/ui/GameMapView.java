package ua.edu.ukma.ui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import ua.edu.ukma.map.MazeFactory;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.renderer.Renderer;
import ua.edu.ukma.renderer.TileMapRenderer;

public class GameMapView extends Pane {

    private final GameMap gameMap;
    private final Renderer<GameMap> renderer;

    public GameMapView() {
        this.gameMap = MazeFactory.createDefaultMaze();
        this.renderer = new TileMapRenderer();

        draw();
    }

    private void draw() {
        Node mapNode = renderer.render(gameMap);
        getChildren().add(mapNode);

        setPrefSize(mapNode.prefWidth(-1), mapNode.prefHeight(-1));
    }
}