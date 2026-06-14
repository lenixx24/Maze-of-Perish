package ua.edu.ukma.model;

import java.util.HashSet;
import java.util.Set;

public class UserProfile {

    private final String username;
    private int gold;
    private final Set<Integer> unlockedLevels;
    private boolean introSeen;
    private boolean endingCompleted;

    public UserProfile(String username, int gold, Set<Integer> unlockedLevels, boolean introSeen, boolean endingCompleted) {
        this.username = username;
        this.gold = gold;
        this.unlockedLevels = new HashSet<>(unlockedLevels);
        this.unlockedLevels.add(1);
        this.introSeen = introSeen;
        this.endingCompleted = endingCompleted;
    }

    public String getUsername() {
        return username;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = Math.max(0, gold);
    }

    public boolean spendGold(int amount) {
        if (amount <= 0) return true;
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }

    public boolean isLevelUnlocked(int levelNumber) {
        return unlockedLevels.contains(levelNumber);
    }

    public void unlockLevel(int levelNumber) {
        unlockedLevels.add(levelNumber);
    }

    public Set<Integer> getUnlockedLevels() {
        return Set.copyOf(unlockedLevels);
    }

    public boolean isIntroSeen() {
        return introSeen;
    }

    public void setIntroSeen(boolean introSeen) {
        this.introSeen = introSeen;
    }

    public boolean isEndingCompleted() {
        return endingCompleted;
    }

    public void setEndingCompleted(boolean endingCompleted) {
        this.endingCompleted = endingCompleted;
    }
}
