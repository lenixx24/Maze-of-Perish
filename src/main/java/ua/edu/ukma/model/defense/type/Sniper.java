package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

public class Sniper extends AttackTower {

    public Sniper(int row, int col) {
        super(row, col, DefenseType.SNIPER_TOWER, 70, 600.0, 60.0, 0.3, "/defense/sniper_b.png", 1200.0);
    }
    @Override
    protected boolean shouldLockTarget() {
        return true;
    }
}