package ua.edu.ukma.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameScaleConfigTest {

    @ParameterizedTest
    @CsvSource({
            "13, 31, 1600, 900, 48",
            "20, 35, 1120, 640, 32",
            "40, 60, 500, 300, 16",
            "10, 10, 240, 180, 18"
    })
    void calculateTileSizeUsesSmallestDimensionAndClampsToAllowedRange(int rows, int cols, double availableWidth, double availableHeight, int expectedTileSize) {
        int tileSize = GameScaleConfig.calculateTileSize(rows, cols, availableWidth, availableHeight);

        assertEquals(expectedTileSize, tileSize);
    }
}
