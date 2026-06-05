package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

public class Sniper extends AttackTower {
    public Sniper (int row, int col, DefenseType type, double damage, double radius) {
        super(row, col, DefenseType.SNIPER_TOWER, 10,10, 10, 20);
    }
}
