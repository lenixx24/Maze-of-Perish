package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.DefenseType;
import ua.edu.ukma.model.defense.EffectZone;

public class Poison extends EffectZone {
    public Poison(int row, int col) {
        super(row,col, DefenseType.POISON_CLOUD, 10, 10, 10);
    }
}
