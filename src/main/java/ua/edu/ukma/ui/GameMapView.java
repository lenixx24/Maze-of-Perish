package ua.edu.ukma.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.entity.enemy.EnemyManager;
import ua.edu.ukma.entity.Direction;
import ua.edu.ukma.entity.enemy.WaveManager;
import ua.edu.ukma.entity.player.Player;
import ua.edu.ukma.model.CellPosition;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.renderer.DefenseRenderer;
import ua.edu.ukma.renderer.TileMapRenderer;
import ua.edu.ukma.resource.CardManager;
import ua.edu.ukma.resource.ManaManager;
import ua.edu.ukma.resource.GoldManager;
import ua.edu.ukma.model.UserProfile;
import ua.edu.ukma.service.UserStorage;

import java.util.*;

public class GameMapView extends Pane {
    private final int tileSize;
    private final TopPanelView topPanel;
    private final GameMap gameMap;
    private final Player player;
    private final ManaManager manaManager=new ManaManager(100,100,10);
    private final CardManager cardManager = new CardManager();
    private final GoldManager goldManager;
    private final UserProfile userProfile;
    private final UserStorage userStorage;
    private final Runnable onRestart;
    private final Runnable onMainMenu;

    private final DefenseManager defenseManager;
    private final DefenseController defenseController;
    private final DefenseRenderer defenseRenderer;
    private final PlacementHighlighter placementHighlighter;

    private final EnemyManager enemyManager;
    private final WaveManager waveManager;
    private CardPane cardPane;
    private AnimationTimer gameTimer;
    private final List<Gold> activeGold = new ArrayList<>();

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

    public GameMapView(GameMap gameMap, int tileSize, TopPanelView topPanel, UserProfile userProfile, UserStorage userStorage) {
        this(gameMap, tileSize, topPanel, userProfile, userStorage, null, null);
    }

    public GameMapView(GameMap gameMap, int tileSize, TopPanelView topPanel, UserProfile userProfile, UserStorage userStorage, Runnable onRestart, Runnable onMainMenu) {
        this.gameMap = gameMap;
        this.tileSize = tileSize;
        this.topPanel=topPanel;
        this.userProfile = userProfile;
        this.userStorage = userStorage;
        this.onRestart = onRestart;
        this.onMainMenu = onMainMenu;
        this.goldManager = new GoldManager(userProfile != null ? userProfile.getGold() : 0);
        this.defenseManager = new DefenseManager();
        this.defenseController = new DefenseController();
        this.defenseRenderer = new DefenseRenderer(this);
        this.enemyManager=new EnemyManager(this, gameMap, tileSize, defenseManager);

        this.placementHighlighter = new PlacementHighlighter();
        this.waveManager=new WaveManager(enemyManager);
        this.waveManager.setOnVictory(() -> {
            pauseGame();
            VictoryWindow victoryWindow = new VictoryWindow();
            victoryWindow.show(onRestart, onMainMenu);
        });
        this.cardPane = new CardPane(defenseController, cardManager, manaManager, this, goldManager);
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

        getChildren().addAll(mapNode, placementHighlighter.getView(), player.getView());

        setPrefSize(gameMap.cols() * tileSize, gameMap.rows() * tileSize);

        setFocusTraversable(true);

        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                requestFocus();
            }
        });

        setOnMouseClicked(event -> {
            requestFocus();
            defenseController.buildDefense(event.getX(), event.getY(), getWidth(), getHeight(), gameMap, defenseManager, player, manaManager, cardManager);
        });

        setOnKeyPressed(event -> {
            Optional.ofNullable(controls.get(event.getCode())).ifPresent(direction -> {
                player.move(direction);
                placementHighlighter.clear();
            });
            defenseController.handle(event.getCode());

            if (event.getCode() == KeyCode.ENTER) waveManager.startWaveEarly();
        });

        startGameLoop();
    }

    public void spawnGold(int row, int col, double pixelX, double pixelY) {
        Gold gold = new Gold(row, col, pixelX, pixelY, tileSize, goldManager, this);
        activeGold.add(gold);
        this.getChildren().add(gold);
        player.getView().toFront();
    }
    public void removeGold(Gold gold) {
        activeGold.remove(gold);
        this.getChildren().remove(gold);
    }
    private void checkGold() {
        int playerRow = player.getRow(tileSize);
        int playerCol = player.getCol(tileSize);

        Iterator<Gold> iterator = activeGold.iterator();
        while (iterator.hasNext()) {
            Gold gold = iterator.next();
            int rowDiff = Math.abs(gold.getTileY() - playerRow);
            int colDiff = Math.abs(gold.getTileX() - playerCol);
            if (rowDiff <= 1 && colDiff <= 1) {
                goldManager.addGold(gold.getGoldReward());
                saveAccountGold();
                this.getChildren().remove(gold);
                iterator.remove();
                break;
            }
        }
    }

    private void saveAccountGold() {
        if (userProfile == null || userStorage == null) return;
        userProfile.setGold(goldManager.getGold());
        userStorage.saveResources(userProfile);
    }

    public void pauseGame() {
        saveAccountGold();
        enemyManager.stopAllAnimations();
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    public void resumeGame() {
        enemyManager.resumeAllAnimations();
        if (gameTimer != null) {
            gameTimer.start();
        }
    }

    public void setCardPane(CardPane cardPane) {
        this.cardPane = cardPane;
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public ManaManager getManaManager() {
        return manaManager;
    }

    public GoldManager getGoldManager() {
        return goldManager;
    }

    public DefenseController getDefenseController() {
        return defenseController;
    }

    private void startGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                player.update();
                player.updateAnimation(now);
                checkGold();
                if (!enemyManager.update()) {
                    pauseGame();
                    GameOverWindow gameOverWindow = new GameOverWindow();
                    gameOverWindow.show(onRestart, onMainMenu);
                }
                waveManager.update(0.010);
                topPanel.update(waveManager, enemyManager.towerHP);
                manaManager.regenerate(0.01);
                if (cardPane != null) {
                    cardPane.updateUI();
                }
                int size = GameScaleConfig.calculateTileSize(gameMap.rows(), gameMap.cols(), getWidth(), getHeight());
                defenseManager.updateDefenses(gameMap, enemyManager.getEnemies(), size, 0.010);
                defenseRenderer.render(gameMap, defenseManager, size);
                placementHighlighter.render(gameMap, defenseManager, player, size, defenseController.getSelectedType());
                player.getView().toFront();
            }
        };

        gameTimer.start();
    }
}