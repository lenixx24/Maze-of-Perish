package ua.edu.ukma.model.defense.type;

import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

public class Laser extends AttackTower {
 public Laser(int row, int col, DefenseType type, double damage, double radius) {
     super(row, col, DefenseType.LASER_TOWER, 10, 10, 10, 20);
 }
}
