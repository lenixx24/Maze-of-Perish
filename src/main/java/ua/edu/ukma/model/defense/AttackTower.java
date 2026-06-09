package ua.edu.ukma.model.defense;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import ua.edu.ukma.entity.SpriteSheet;

public class AttackTower extends DefenseStructure {
    private int hp;
    private final double attackRange;
    private final double damage;
    private final double fireRate;
    protected final SpriteSheet<Integer> spriteSheet;
    protected final ImageView baseView;
    protected final ImageView barrelView;
    protected final Group viewGroup;

    public AttackTower(int row, int col, DefenseType type, int hp, double attackRange, double damage, double fireRate) {
        super(row, col, type);
        this.hp = hp;
        this.attackRange = attackRange;
        this.damage = damage;
        this.fireRate = fireRate;
        this.spriteSheet = new SpriteSheet<>(
                type.texturePath(),
                frame -> frame * 32,
                frame -> 0,
                frame -> 32,
                frame -> 32
        );
        this.baseView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.baseView, 0);
        this.barrelView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.barrelView, 1);
        this.viewGroup = new Group(baseView, barrelView);
        this.viewGroup.setMouseTransparent(true);
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