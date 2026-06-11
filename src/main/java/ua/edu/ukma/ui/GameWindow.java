package ua.edu.ukma.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.config.GameWindowConfig;
import ua.edu.ukma.map.MazeFactory;
import ua.edu.ukma.model.GameMap;
import java.util.Objects;

public class GameWindow {

    private static final Color BACKGROUND_COLOR = Color.rgb(24, 24, 32);
    private static final Color BORDER_COLOR = Color.rgb(74, 59, 50);

    private final GameWindowConfig config;

    public GameWindow(GameWindowConfig config) {
        this.config = config;
    }

    public Scene createScene(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Pane root = createRoot(stage, screenBounds);
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight(), BACKGROUND_COLOR);
        String cssPath = Objects.requireNonNull(getClass().getResource("/css/card-pane.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);
        return scene;
    }

    public void applyStageSettings(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.setTitle(config.title());

        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());

        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        stage.setMinWidth(screenBounds.getWidth());
        stage.setMinHeight(screenBounds.getHeight());
        stage.setMaxWidth(screenBounds.getWidth());
        stage.setMaxHeight(screenBounds.getHeight());

        stage.setResizable(false);
    }

    private Pane createRoot(Stage stage, Rectangle2D screenBounds) {
        Pane root = new Pane();
        root.setStyle(toBackgroundStyle(BACKGROUND_COLOR));

        WindowTitleBar windowTitleBar = new WindowTitleBar(config.title());

        Pane titleBar = windowTitleBar.create(stage);
        titleBar.setLayoutX(0);
        titleBar.setLayoutY(0);
        titleBar.prefWidthProperty().bind(root.widthProperty());
        titleBar.setPrefHeight(WindowTitleBar.HEIGHT);

        Pane gameArea = createGameArea(screenBounds);
        gameArea.setLayoutX(0);
        gameArea.setLayoutY(WindowTitleBar.HEIGHT);
        gameArea.prefWidthProperty().bind(root.widthProperty());
        gameArea.prefHeightProperty().bind(root.heightProperty().subtract(WindowTitleBar.HEIGHT));

        root.getChildren().addAll(titleBar, gameArea);

        return root;
    }

    private Pane createGameArea(Rectangle2D screenBounds) {
        Pane gameArea = new Pane();
        gameArea.setStyle(toBackgroundStyle(BACKGROUND_COLOR));

        Line topDivider = createHorizontalDivider(gameArea);
        topDivider.setStartY(config.topPanelHeight());
        topDivider.setEndY(config.topPanelHeight());

        Line bottomDivider = createHorizontalDivider(gameArea);
        bottomDivider.startYProperty().bind(gameArea.heightProperty().subtract(config.bottomPanelHeight()));
        bottomDivider.endYProperty().bind(gameArea.heightProperty().subtract(config.bottomPanelHeight()));

        GameMap gameMap = MazeFactory.createDefaultMaze();

        double availableWidth = screenBounds.getWidth();
        double availableHeight = screenBounds.getHeight()
                - WindowTitleBar.HEIGHT
                - config.topPanelHeight()
                - config.bottomPanelHeight();

        int tileSize = GameScaleConfig.calculateTileSize(
                gameMap.rows(),
                gameMap.cols(),
                availableWidth,
                availableHeight
        );

        InstWindow instWindow = new InstWindow();
        instWindow.prefWidthProperty().bind(gameArea.widthProperty());
        instWindow.prefHeightProperty().bind(gameArea.heightProperty());
        instWindow.setLayoutX(0);
        instWindow.setLayoutY(0);

        TopPanelView topPanel = new TopPanelView(screenBounds.getWidth(), config.topPanelHeight(), instWindow);
        topPanel.setLayoutX(0);
        topPanel.setLayoutY(0);

        GameMapView mapView = new GameMapView(gameMap, tileSize, topPanel);
        mapView.setLayoutX(0);
        mapView.setLayoutY(0);

        mapView.layoutXProperty().bind(gameArea.widthProperty().subtract(mapView.prefWidth(-1)).divide(2));
        mapView.layoutYProperty().bind(gameArea.heightProperty()
                .subtract(config.topPanelHeight())
                .subtract(config.bottomPanelHeight())
                .subtract(mapView.prefHeight(-1))
                .divide(2)
                .add(config.topPanelHeight())
        );

        topPanel.setMapView(mapView);
        instWindow.setMapView(mapView);

        CardPane cardPane = new CardPane(
                mapView.getDefenseController(),
                mapView.getCardManager(),
                mapView.getManaManager(),
                mapView
        );

        cardPane.setPrefWidth(screenBounds.getWidth());
        cardPane.setPrefHeight(config.bottomPanelHeight());
        cardPane.setLayoutX(0);
        cardPane.layoutYProperty().bind(gameArea.heightProperty().subtract(config.bottomPanelHeight()));
        mapView.setCardPane(cardPane);

        gameArea.getChildren().addAll(mapView, topDivider, bottomDivider, topPanel, cardPane, instWindow);
        return gameArea;
    }

    private Line createHorizontalDivider(Pane root) {
        Line line = new Line();
        line.setStartX(0);
        line.endXProperty().bind(root.widthProperty());
        line.setStroke(BORDER_COLOR);
        line.setStrokeWidth(3);
        return line;
    }

    private String toBackgroundStyle(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);

        return "-fx-background-color: rgb(" + red + ", " + green + ", " + blue + ");";
    }
}