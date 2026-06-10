package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.EffectZone;

public class Freeze extends EffectZone {

    public Freeze(int row, int col) {
        super(row, col, DefenseType.FREEZE, 1.5, 0.0, 50.0, "/defense/freeze1.png");
    }
}