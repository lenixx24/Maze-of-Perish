package ua.edu.ukma.model.defense;

public abstract class FieldEffectStructure extends DefenseStructure {
    private double timeLeft;
    private final double radius;

    public FieldEffectStructure(int row, int col, DefenseType type, double duration, double radius) {
        super(row, col, type);
        this.timeLeft = duration;
        this.radius = radius;
    }

    public double getTimeLeft() {
        return timeLeft;
    }
    public double getRadius() {
        return radius;
    }

    public void decreaseTime(double a) {
        this.timeLeft -= a;
    }
}
