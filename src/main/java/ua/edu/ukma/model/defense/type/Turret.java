package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

public class Turret extends AttackTower {

    public Turret(int row, int col) {
        super(row, col, DefenseType.TURRET, 35, 200.0, 20, 1.5, "/defense/turret_bullet.png", 600.0);
    }

}