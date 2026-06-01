package ua.edu.ukma.model;

import ua.edu.ukma.exception.GameMapException;

public class GameMap {

    private final CellType[][] cells;

    public GameMap(int[][] rawMap) {
        if (rawMap == null || rawMap.length == 0) {
            throw new GameMapException("Map cannot be empty");
        }

        int width = rawMap[0].length;

        for (int row = 0; row < rawMap.length; row++) {
            if (rawMap[row].length != width) {
                throw new GameMapException("All map rows must have the same width");
            }
        }

        this.cells = new CellType[rawMap.length][width];

        for (int row = 0; row < rawMap.length; row++) {
            for (int col = 0; col < width; col++) {
                cells[row][col] = CellType.fromCode(rawMap[row][col]);
            }
        }
    }

    public int rows() {
        return cells.length;
    }

    public int cols() {
        return cells[0].length;
    }

    public CellType getCell(int row, int col) {
        if (!isInside(row, col)) {
            throw new GameMapException("Cell is outside the map: row=" + row + ", col=" + col);
        }

        return cells[row][col];
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < rows() && col >= 0 && col < cols();
    }

    public boolean isPassable(int row, int col) {
        return isInside(row, col) && getCell(row, col).isPassable();
    }

    public CellPosition findFirst(CellType targetType) {
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < cols(); col++) {
                if (cells[row][col] == targetType) {
                    return new CellPosition(row, col);
                }
            }
        }

        throw new GameMapException("Cell type was not found on map: " + targetType);
    }
}