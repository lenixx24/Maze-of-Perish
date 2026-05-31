package ua.edu.ukma.model;

public class DefenseStructure {
    private final int row;
    private final int col;
    private final DefenseType type;

    public DefenseStructure(int row, int col, DefenseType type) {
        this.row = row;
        this.col = col;
        this.type = type;
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
}
