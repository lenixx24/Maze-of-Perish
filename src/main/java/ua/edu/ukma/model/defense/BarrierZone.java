package ua.edu.ukma.model.defense;

public class BarrierZone extends DefenseStructure {

    private int hp;
    private double timeLeft;

    public BarrierZone(int row, int col,DefenseType type, int hp, double duration) {
        super(row, col, type);
        this.hp = hp;
        this.timeLeft = duration;
    }

    public void updateLifetime(double deltaTime) {
        this.timeLeft -= deltaTime;
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
    }

    public boolean isDestroyed() {
        return hp <= 0 || timeLeft <= 0;
    }

    public int getHp() { return hp; }
}