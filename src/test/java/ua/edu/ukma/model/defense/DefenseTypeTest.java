package ua.edu.ukma.model.defense;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DefenseTypeTest {

    @ParameterizedTest
    @CsvSource({
            "4, TRAP",
            "5, BOMB",
            "6, TURRET",
            "7, FREEZE",
            "8, POISON_CLOUD",
            "9, BARRIER",
            "10, SNIPER_TOWER",
            "11, LASER_TOWER",
            "12, CANNON_TOWER"
    })
    void fromCodeReturnsMatchingDefenseType(int code, DefenseType expectedType) {
        assertEquals(expectedType, DefenseType.fromCode(code));
    }

    @ParameterizedTest
    @CsvSource({
            "TRAP, Trap, 15",
            "BOMB, Bomb, 35",
            "TURRET, Turret, 30",
            "FREEZE, Freeze, 30",
            "POISON_CLOUD, Poison Cloud, 50",
            "BARRIER, Barrier, 20",
            "SNIPER_TOWER, Sniper Tower, 70",
            "LASER_TOWER, Laser Tower, 60",
            "CANNON_TOWER, Cannon Tower, 90"
    })
    void annotationValuesExposeDisplayNameAndManaCost(DefenseType type, String expectedName, int expectedManaCost) {
        assertEquals(expectedName, type.getName());
        assertEquals(expectedManaCost, type.manaCost());
    }

    @Test
    void texturePathReturnsDefenseTexturePath() {
        assertEquals("/defense/trap.png", DefenseType.TRAP.texturePath());
        assertEquals("/defense/freeze.png", DefenseType.FREEZE.texturePath());
        assertEquals("/defense/cannon.png", DefenseType.CANNON_TOWER.texturePath());
    }

    @Test
    void fromCodeRejectsUnknownCode() {
        assertThrows(IllegalArgumentException.class, () -> DefenseType.fromCode(99));
    }
}
