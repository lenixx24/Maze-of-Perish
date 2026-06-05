package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.BarrierZone;
import ua.edu.ukma.model.defense.DefenseType;

public class Barrier extends BarrierZone {
    public Barrier(int row, int col) {
        super(row,col, DefenseType.BARRIER,10,10);
    }
}
