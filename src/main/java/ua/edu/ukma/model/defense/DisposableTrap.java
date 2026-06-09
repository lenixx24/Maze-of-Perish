package ua.edu.ukma.model.defense;

public abstract class DisposableTrap extends DefenseStructure {
    private final double damage;
    private final double radius;

    public DisposableTrap(int row, int col, DefenseType type, double damage, double radius) {
        super(row, col, type);
        this.damage = damage;
        this.radius = radius;
    }
    public double getDamage() {
        return damage;
    }
    public double getRadius() {
        return radius;
    }
}
