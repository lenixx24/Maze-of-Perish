package ua.edu.ukma.model.defense;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class DefenseStructure {
    private final int row;
    private final int col;
    private final DefenseType type;
    private final ImageView imageView;

    public DefenseStructure(int row, int col, DefenseType type) {
        this.row = row;
        this.col = col;
        this.type = type;

        try {
            String texturePath = type.texturePath();
            Image sprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream(texturePath)));

            this.imageView = new ImageView(sprite);
            this.imageView.setFitWidth(32);
            this.imageView.setFitHeight(32);
            this.imageView.setPreserveRatio(true);
            this.imageView.setMouseTransparent(true);
            updateImageViewPosition(48);
        } catch (Exception e) {
            throw new IllegalStateException( "Cannot read texture for: " + type.name(), e);
        }}
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public DefenseType getType() {
        return type;
    }
    public ImageView getView() {
        return imageView;
    }
    public void updateImageViewPosition(int tileSize) {
        if (imageView == null) return;

        double pixelX = col * tileSize + (tileSize - imageView.getFitWidth()) / 2.0;
        double pixelY = row * tileSize + (tileSize - imageView.getFitHeight()) / 2.0;

        this.imageView.setLayoutX(pixelX);
        this.imageView.setLayoutY(pixelY);
    }
}
