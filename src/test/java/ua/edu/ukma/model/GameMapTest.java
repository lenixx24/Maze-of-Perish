package ua.edu.ukma.model;

import org.junit.jupiter.api.Test;
import ua.edu.ukma.exception.GameMapException;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    @Test
    void constructorRejectsEmptyMap() {
        assertThrows(GameMapException.class, () -> new GameMap(new int[][]{}));
    }

    @Test
    void constructorRejectsRowsWithDifferentWidths() {
        int[][] rawMap = {{1, 1, 1}, {1, 0}};

        assertThrows(GameMapException.class, () -> new GameMap(rawMap));
    }

    @Test
    void getCellConvertsRawCodesToCellTypes() {
        GameMap map = new GameMap(new int[][]{{1, 2}, {0, 3}});

        assertEquals(CellType.WALL, map.getCell(0, 0));
        assertEquals(CellType.SPAWN, map.getCell(0, 1));
        assertEquals(CellType.FLOOR, map.getCell(1, 0));
        assertEquals(CellType.TOWER, map.getCell(1, 1));
    }

    @Test
    void isInsideReturnsFalseForCoordinatesOutsideMap() {
        GameMap map = new GameMap(new int[][]{{1, 1}, {1, 0}});

        assertFalse(map.isInside(-1, 0));
        assertFalse(map.isInside(0, -1));
        assertFalse(map.isInside(2, 0));
        assertFalse(map.isInside(0, 2));
    }

    @Test
    void isPassableReturnsTrueOnlyForPassableInsideCells() {
        GameMap map = new GameMap(new int[][]{{1, 0, 2, 3}});

        assertFalse(map.isPassable(0, 0));
        assertTrue(map.isPassable(0, 1));
        assertTrue(map.isPassable(0, 2));
        assertFalse(map.isPassable(0, 3));
        assertFalse(map.isPassable(10, 10));
    }

    @Test
    void findFirstReturnsFirstMatchingCellPosition() {
        GameMap map = new GameMap(new int[][]{{1, 0, 2}, {3, 2, 0}});

        assertEquals(new CellPosition(0, 2), map.findFirst(CellType.SPAWN));
    }
}
