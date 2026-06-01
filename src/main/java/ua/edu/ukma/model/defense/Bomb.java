package ua.edu.ukma.model.defense;

public class Bomb extends DisposableTrap {

    public Bomb(int row, int col) {
        super(row, col, DefenseType.BOMB, 1.0, 1.0);
        setCustomSize(24);
        updateImageViewPosition(48);
    }
}
