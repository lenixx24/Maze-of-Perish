package ua.edu.ukma.model.defense;

public class AttackTower extends DefenseStructure {
    private int hp;
    private final double attackRange;
    private final double damage;
    private final double fireRate;

    public AttackTower(int row, int col, DefenseType type, int hp, double attackRange, double damage, double fireRate) {
        super(row, col, type);
        this.hp = hp;
        this.attackRange = attackRange;
        this.damage = damage;
        this.fireRate = fireRate;
    }

    public int getHp() {
        return hp;
    }
    public void takeDamage(int amount) {
        this.hp -= amount;
    }
    public double getAttackRange() {
        return attackRange;
    }
    public double getDamage() {
        return damage;
    }
    public double getFireRate() {
        return fireRate;
    }
}