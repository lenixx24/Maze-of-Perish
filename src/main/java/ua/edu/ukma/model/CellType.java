package ua.edu.ukma.model;

import ua.edu.ukma.annotation.TileTexture;

import java.lang.reflect.Field;
import java.util.Arrays;

public enum CellType {

    @TileTexture("/tiles/floor.png")
    FLOOR(0, true),

    @TileTexture("/tiles/wall.png")
    WALL(1, false),

    @TileTexture("/tiles/spawn.png")
    SPAWN(2, true),

    @TileTexture("/tiles/tower.png")
    TOWER(3, false);

    private final int code;
    private final boolean passable;

    CellType(int code, boolean passable) {
        this.code = code;
        this.passable = passable;
    }

    public int code() {
        return code;
    }

    public boolean isPassable() {
        return passable;
    }

    public static CellType fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown cell type code: " + code));
    }

    public String texturePath() {
        try {
            Field field = CellType.class.getField(name());
            TileTexture annotation = field.getAnnotation(TileTexture.class);
            if (annotation == null) {
                throw new IllegalStateException("Missing texture annotation for: " + name());
            }

            return annotation.value();
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("Cannot read texture for: " + name(), exception);
        }
    }
}