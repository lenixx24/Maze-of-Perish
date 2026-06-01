package ua.edu.ukma.config;

public final class GameScaleConfig {

    public static final int IMAGE_SIZE = 32;

    public static final int MAX_TILE_SIZE = 48;
    public static final int MIN_TILE_SIZE = 16;

    private GameScaleConfig() {
    }

    public static int calculateTileSize(int rows, int cols, double availableWidth, double availableHeight) {
        int tileByWidth = (int) Math.floor(availableWidth / cols);
        int tileByHeight = (int) Math.floor(availableHeight / rows);

        int calculatedTileSize = Math.min(tileByWidth, tileByHeight);

        return Math.max(MIN_TILE_SIZE, Math.min(MAX_TILE_SIZE, calculatedTileSize));
    }
}