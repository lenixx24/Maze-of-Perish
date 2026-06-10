package ua.edu.ukma.model.defense;

import ua.edu.ukma.annotation.DefenseTexture;
import java.lang.reflect.Field;
import java.util.Arrays;

public enum DefenseType {
    @DefenseTexture(value = "/defense/trap.png", name = "Trap", manaCost = 15)
    TRAP(4),

    @DefenseTexture(value = "/defense/bomb.png", name = "Bomb", manaCost = 35)
    BOMB(5),

    @DefenseTexture(value = "/defense/turret.png", name = "Turret", manaCost = 30)
    TURRET(6),

    @DefenseTexture(value = "/defense/freeze.png", name = "Freeze", manaCost = 30)
    FREEZE(7),

    @DefenseTexture(value = "/defense/poison.png", name = "Poison Cloud", manaCost = 50)
    POISON_CLOUD(8),

    @DefenseTexture(value = "/defense/barrier.png", name = "Barrier", manaCost = 20)
    BARRIER(9),

    @DefenseTexture(value = "/defense/sniper.png", name = "Sniper Tower", manaCost = 70)
    SNIPER_TOWER(10),

    @DefenseTexture(value = "/defense/laser.png", name = "Laser Tower", manaCost = 60)
    LASER_TOWER(11),

    @DefenseTexture(value = "/defense/cannon.png", name = "Cannon Tower", manaCost = 90)
    CANNON_TOWER(12);

    private final int code;

    DefenseType(int code) {
        this.code = code;
    }
    public int code() {
        return code;
    }
    public static DefenseType fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown cell type code: " + code));
    }
    private DefenseTexture getAnnotation() {
        try {
            Field field = DefenseType.class.getField(name());
            return field.getAnnotation(DefenseTexture.class);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Missing annotation for " + name(), e);
        }
    }

    public String texturePath() {
        return getAnnotation().value();
    }
    public String getName() {
        return getAnnotation().name();
    }
    public int manaCost() {
        return getAnnotation().manaCost();
    }
}