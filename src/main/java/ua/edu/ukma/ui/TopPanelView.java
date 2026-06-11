package ua.edu.ukma.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ua.edu.ukma.entity.enemy.WaveManager;

public class TopPanelView extends HBox {

    private final Label waveLabel;
    private final Label timerLabel;
    private final Button helpButton;
    private final InstWindow instWindow;
    private GameMapView mapView;

    public TopPanelView(double width, double height, InstWindow instWindow) {
        this.instWindow = instWindow;

        setPrefSize(width, height);
        setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new javafx.geometry.Insets(0, 20, 0, 100));
        this.getStyleClass().add("top-panel");
        Font.loadFont(getClass().getResourceAsStream("/font/jersey10.ttf"), 16);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        waveLabel = createStyledLabel("Wave: 1");
        setAlignment(Pos.CENTER);
        setSpacing(17);
        timerLabel = createStyledLabel("Preparation: 0.0с");

        helpButton = new Button("!");
        helpButton.getStyleClass().add("help-button");
        helpButton.setFocusTraversable(false);

        helpButton.setOnAction(event -> {
            if (this.instWindow != null && this.mapView != null) {
                this.instWindow.show(
                        () -> this.mapView.pauseGame(),
                        () -> this.mapView.resumeGame()
                );
            }
        });

        getChildren().addAll(leftSpacer, waveLabel, timerLabel, rightSpacer, helpButton);
    }

    private Label createStyledLabel(String defaultText) {
        Label label = new Label(defaultText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Jersey 10", FontWeight.BOLD, 30));
        return label;
    }

    public void update(WaveManager waveManager) {
        waveLabel.setText("Wave: " + waveManager.getCurrentWave());

        if (waveManager.isPreparationPhase()) {
            timerLabel.setText(String.format("Preparation: %.1fс", waveManager.getPrepTimer()));
            timerLabel.setTextFill(Color.YELLOW);
        } else {
            timerLabel.setText("Attack!");
            timerLabel.setTextFill(Color.web("#ff4444"));
        }
    }

    public void setMapView(GameMapView mapView) {
        this.mapView = mapView;
    }
}
