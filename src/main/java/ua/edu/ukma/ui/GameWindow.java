package ua.edu.ukma.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.config.GameWindowConfig;
import ua.edu.ukma.map.MazeFactory;
import ua.edu.ukma.model.GameMap;

public class GameWindow {

    private static final Color BACKGROUND_COLOR = Color.rgb(24, 24, 32);
    private static final Color BORDER_COLOR = Color.rgb(120, 120, 140);

    private static final double TITLE_BAR_HEIGHT = 32;

    private final GameWindowConfig config;

    public GameWindow(GameWindowConfig config) {
        this.config = config;
    }

    public void show(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.initStyle(StageStyle.UNDECORATED);

        Pane root = createRoot(stage, screenBounds);

        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        stage.setTitle(config.title());
        stage.setScene(scene);

        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());

        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        stage.setMinWidth(screenBounds.getWidth());
        stage.setMinHeight(screenBounds.getHeight());
        stage.setMaxWidth(screenBounds.getWidth());
        stage.setMaxHeight(screenBounds.getHeight());

        stage.setResizable(false);

        stage.show();
    }

    private Pane createRoot(Stage stage, Rectangle2D screenBounds) {
        Pane root = new Pane();
        root.setStyle(toBackgroundStyle(BACKGROUND_COLOR));

        Pane titleBar = createTitleBar(stage);
        titleBar.setLayoutX(0);
        titleBar.setLayoutY(0);
        titleBar.prefWidthProperty().bind(root.widthProperty());
        titleBar.setPrefHeight(TITLE_BAR_HEIGHT);

        Pane gameArea = createGameArea(screenBounds);
        gameArea.setLayoutX(0);
        gameArea.setLayoutY(TITLE_BAR_HEIGHT);
        gameArea.prefWidthProperty().bind(root.widthProperty());
        gameArea.prefHeightProperty().bind(root.heightProperty().subtract(TITLE_BAR_HEIGHT));

        root.getChildren().addAll(titleBar, gameArea);

        return root;
    }

    private Pane createTitleBar(Stage stage) {
        Pane titleBar = new Pane();
        titleBar.setStyle("-fx-background-color: #e9eef2;");

        Label title = new Label(config.title());
        title.setLayoutX(12);
        title.setLayoutY(7);
        title.setStyle("-fx-text-fill: #202020; -fx-font-size: 13px;");

        Button minimizeButton = createTitleBarButton("—");
        minimizeButton.layoutXProperty().bind(titleBar.widthProperty().subtract(90));
        minimizeButton.setLayoutY(0);
        minimizeButton.setOnAction(event -> stage.setIconified(true));

        Button closeButton = createTitleBarButton("×");
        closeButton.layoutXProperty().bind(titleBar.widthProperty().subtract(45));
        closeButton.setLayoutY(0);
        closeButton.setOnAction(event -> stage.close());

        titleBar.getChildren().addAll(title, minimizeButton, closeButton);

        return titleBar;
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
                - TITLE_BAR_HEIGHT
                - config.topPanelHeight()
                - config.bottomPanelHeight();

        int tileSize = GameScaleConfig.calculateTileSize(
                gameMap.rows(),
                gameMap.cols(),
                availableWidth,
                availableHeight
        );

        GameMapView mapView = new GameMapView(gameMap, tileSize);

        mapView.layoutXProperty().bind(
                gameArea.widthProperty()
                        .subtract(mapView.prefWidth(-1))
                        .divide(2)
        );

        mapView.layoutYProperty().bind(
                gameArea.heightProperty()
                        .subtract(config.topPanelHeight())
                        .subtract(config.bottomPanelHeight())
                        .subtract(mapView.prefHeight(-1))
                        .divide(2)
                        .add(config.topPanelHeight())
        );

        gameArea.getChildren().addAll(mapView, topDivider, bottomDivider);
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

    private Button createTitleBarButton(String text) {
        Button button = new Button(text);

        button.setPrefSize(45, TITLE_BAR_HEIGHT);
        button.setFocusTraversable(false);

        button.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #202020;
            -fx-font-size: 14px;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
            """);

        button.setOnMouseEntered(event -> button.setStyle("""
            -fx-background-color: #d8dde2;
            -fx-text-fill: #202020;
            -fx-font-size: 14px;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
            """));

        button.setOnMouseExited(event -> button.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #202020;
            -fx-font-size: 14px;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
            """));

        button.setOnMousePressed(event -> button.setStyle("""
            -fx-background-color: #c8cdd2;
            -fx-text-fill: #202020;
            -fx-font-size: 14px;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
            """));

        button.setOnMouseReleased(event -> button.setStyle("""
            -fx-background-color: #d8dde2;
            -fx-text-fill: #202020;
            -fx-font-size: 14px;
            -fx-border-color: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
            """));

        return button;
    }
}