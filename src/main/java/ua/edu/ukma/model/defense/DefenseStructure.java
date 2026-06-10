package ua.edu.ukma.model.defense;

import javafx.scene.image.*;

import java.util.Objects;

public class DefenseStructure {
    private final int row;
    private final int col;
    private final DefenseType type;
    private final ImageView imageView;

    private int customSize = 32;

    public DefenseStructure(int row, int col, DefenseType type) {
        this.row = row;
        this.col = col;
        this.type = type;

        try {
            String texturePath = type.texturePath();
            Image sprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream(texturePath)));

            this.imageView = new ImageView(sprite);
            this.imageView.setFitWidth(customSize);
            this.imageView.setFitHeight(customSize);
            this.imageView.setPreserveRatio(true);
            this.imageView.setMouseTransparent(true);
            updateImageViewPosition(48);
        } catch (Exception e) {
            throw new IllegalStateException( "Cannot read texture for: " + type.name(), e);
        }}

    public void setCustomSize(int size) {
        this.customSize = size;
        if (this.imageView != null) {
            this.imageView.setFitWidth(size);
            this.imageView.setFitHeight(size);
        }
    }

    public int getCustomSize() {
        return customSize;
    }
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

        double pixelX = col * tileSize + (tileSize - customSize) / 2.0;
        double pixelY = row * tileSize + (tileSize - customSize) / 2.0;

        this.imageView.setLayoutX(pixelX);
        this.imageView.setLayoutY(pixelY);
    }
    public void takeDamage(int amount) { }
}
