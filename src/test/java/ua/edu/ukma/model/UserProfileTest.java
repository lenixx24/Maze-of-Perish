package ua.edu.ukma.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void constructorAlwaysUnlocksFirstLevel() {
        UserProfile profile = new UserProfile("stas", 100, Set.of(2), false, false);

        assertTrue(profile.isLevelUnlocked(1));
        assertTrue(profile.isLevelUnlocked(2));
    }

    @Test
    void setGoldDoesNotAllowNegativeBalance() {
        UserProfile profile = new UserProfile("stas", 100, Set.of(), false, false);
        profile.setGold(-50);

        assertEquals(0, profile.getGold());
    }

    @Test
    void spendGoldSubtractsAmountWhenEnoughGoldExists() {
        UserProfile profile = new UserProfile("stas", 100, Set.of(), false, false);
        boolean result = profile.spendGold(40);

        assertTrue(result);
        assertEquals(60, profile.getGold());
    }

    @Test
    void spendGoldReturnsFalseAndKeepsBalanceWhenNotEnoughGold() {
        UserProfile profile = new UserProfile("stas", 30, Set.of(), false, false);
        boolean result = profile.spendGold(40);

        assertFalse(result);
        assertEquals(30, profile.getGold());
    }

    @Test
    void unlockedLevelsCopyCannotModifyProfileState() {
        UserProfile profile = new UserProfile("stas", 0, Set.of(), false, false);
        Set<Integer> levels = profile.getUnlockedLevels();

        assertThrows(UnsupportedOperationException.class, () -> levels.add(3));
        assertFalse(profile.isLevelUnlocked(3));
    }

    @Test
    void storyFlagsCanBeChanged() {
        UserProfile profile = new UserProfile("stas", 0, Set.of(), false, false);
        profile.setIntroSeen(true);
        profile.setEndingCompleted(true);

        assertTrue(profile.isIntroSeen());
        assertTrue(profile.isEndingCompleted());
    }
}
