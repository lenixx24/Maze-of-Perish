package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.DisposableTrap;

public class Trap extends DisposableTrap {
    public Trap(int row, int col) {
        super(row, col, DefenseType.TRAP,10,10);
    }
}
