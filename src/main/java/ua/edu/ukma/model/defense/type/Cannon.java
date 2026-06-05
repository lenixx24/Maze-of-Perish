package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

public class Cannon extends AttackTower {
    public Cannon(int row, int col, DefenseType type, double damage, double radius) {
        super(row, col, DefenseType.CANNON_TOWER, 10, 10, 10, 20);
    }
}
