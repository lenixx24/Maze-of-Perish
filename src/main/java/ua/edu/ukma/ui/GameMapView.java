package ua.edu.ukma.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.entity.enemy.EnemyManager;
import ua.edu.ukma.entity.player.Direction;
import ua.edu.ukma.entity.player.Player;
import ua.edu.ukma.model.CellPosition;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.renderer.DefenseRenderer;
import ua.edu.ukma.renderer.TileMapRenderer;

import java.util.Map;
import java.util.Optional;

public class GameMapView extends Pane {

    private final int tileSize;

    private final GameMap gameMap;
    private final Player player;

    private final DefenseManager defenseManager;
    private final DefenseController defenseController;
    private final DefenseRenderer defenseRenderer;

    private final EnemyManager enemyManager;
    private final Map<KeyCode, Direction> controls = Map.of(
            KeyCode.A, Direction.LEFT,
            KeyCode.LEFT, Direction.LEFT,

            KeyCode.D, Direction.RIGHT,
            KeyCode.RIGHT, Direction.RIGHT,

            KeyCode.W, Direction.UP,
            KeyCode.UP, Direction.UP,

            KeyCode.S, Direction.DOWN,
            KeyCode.DOWN, Direction.DOWN
    );

    public GameMapView(GameMap gameMap, int tileSize) {
        this.gameMap = gameMap;
        this.tileSize = tileSize;

        this.defenseManager = new DefenseManager();
        this.defenseController = new DefenseController();
        this.defenseRenderer = new DefenseRenderer(this);
        this.enemyManager=new EnemyManager(this);
        TileMapRenderer renderer = new TileMapRenderer(tileSize);
        Node mapNode = renderer.render(gameMap);

        CellPosition towerPosition = gameMap.findFirst(CellType.TOWER);
        CellPosition playerStartPosition = new CellPosition(
                towerPosition.row() + 1,
                towerPosition.col()
        );

        this.player = new Player(
                playerStartPosition.row(),
                playerStartPosition.col(),
                gameMap,
                tileSize
        );

        getChildren().addAll(mapNode, player.getView());

        setPrefSize(gameMap.cols() * tileSize, gameMap.rows() * tileSize);

        setFocusTraversable(true);

        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                requestFocus();
            }
        });

        setOnMouseClicked(event -> {
            requestFocus();
            defenseController.buildDefense(event.getX(), event.getY(), getWidth(), getHeight(), gameMap, defenseManager, player);
        });

        setOnKeyPressed(event -> {
            Optional.ofNullable(controls.get(event.getCode())).ifPresent(player::move);
            defenseController.handle(event.getCode());
        });

        startGameLoop();
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                player.update();
                player.updateAnimation(now);
                enemyManager.update();
                int size = GameScaleConfig.calculateTileSize(gameMap.rows(), gameMap.cols(), getWidth(), getHeight());
                defenseRenderer.render(defenseManager, size);
                player.getView().toFront();
            }
        };

        timer.start();
    }
}