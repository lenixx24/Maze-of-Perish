package ua.edu.ukma.model;

public class UserProfile {

    private final String username;
    private int gold;

    public UserProfile(String username, int gold) {
        this.username = username;
        this.gold = gold;
    }

    public String getUsername() {
        return username;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }
}
