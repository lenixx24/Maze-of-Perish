package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.EffectZone;

public class Freeze extends EffectZone {
    public Freeze(int row, int col) {
        super(row,col, DefenseType.FREEZE, 10, 10, 10, 20);
    }
}
