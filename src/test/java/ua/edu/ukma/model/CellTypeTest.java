package ua.edu.ukma.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CellTypeTest {

    @ParameterizedTest
    @CsvSource({
            "0, FLOOR",
            "1, WALL",
            "2, SPAWN",
            "3, TOWER"
    })
    void fromCodeReturnsMatchingCellType(int code, CellType expectedType) {
        assertEquals(expectedType, CellType.fromCode(code));
    }

    @ParameterizedTest
    @CsvSource({
            "FLOOR, true",
            "WALL, false",
            "SPAWN, true",
            "TOWER, false"
    })
    void isPassableMatchesGameRules(CellType type, boolean expectedPassable) {
        assertEquals(expectedPassable, type.isPassable());
    }

    @Test
    void texturePathReturnsPathFromAnnotation() {
        assertEquals("/tiles/floor.png", CellType.FLOOR.texturePath());
        assertEquals("/tiles/wall.png", CellType.WALL.texturePath());
        assertEquals("/tiles/spawn.png", CellType.SPAWN.texturePath());
        assertEquals("/tiles/tower.png", CellType.TOWER.texturePath());
    }

    @Test
    void fromCodeRejectsUnknownCode() {
        assertThrows(IllegalArgumentException.class, () -> CellType.fromCode(99));
    }
}
