package ua.edu.ukma.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ua.edu.ukma.entity.enemy.WaveManager;

public class TopPanelView extends HBox {

    private final Label waveLabel;
    private final Label timerLabel;

    public TopPanelView(double width, double height) {
        setPrefSize(width, height);
        setAlignment(Pos.CENTER);
        setSpacing(50);

        waveLabel = createStyledLabel("Wave: 1");
        timerLabel = createStyledLabel("Preparation: 0.0с");

        getChildren().addAll(waveLabel, timerLabel);
    }

    private Label createStyledLabel(String defaultText) {
        Label label = new Label(defaultText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
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
}
