package ua.edu.ukma.resource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManaManagerTest {

    @Test
    void decreaseManaSubtractsRequestedAmount() {
        ManaManager manaManager = new ManaManager(100, 100, 10);
        manaManager.decreaseMana(35);

        assertEquals(65, manaManager.getMana());
    }

    @Test
    void decreaseManaDoesNotGoBelowZero() {
        ManaManager manaManager = new ManaManager(20, 100, 10);
        manaManager.decreaseMana(50);

        assertEquals(0, manaManager.getMana());
    }

    @Test
    void regenerateRestoresManaByDeltaTimeAndSpeed() {
        ManaManager manaManager = new ManaManager(50, 100, 10);
        manaManager.regenerate(2.5);

        assertEquals(75, manaManager.getMana());
    }

    @Test
    void regenerateDoesNotExceedMaximumMana() {
        ManaManager manaManager = new ManaManager(95, 100, 10);
        manaManager.regenerate(2.0);

        assertEquals(100, manaManager.getMana());
    }
}
