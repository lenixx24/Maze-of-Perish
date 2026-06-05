package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

public class Turret extends AttackTower {
    public Turret(int row, int col, DefenseType type, double damage, double radius) {
        super( row, col, DefenseType.TURRET, 10, 10, 10, 20);

    }
}
