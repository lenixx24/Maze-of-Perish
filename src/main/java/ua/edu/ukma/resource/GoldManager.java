package ua.edu.ukma.resource;

public class GoldManager {
    private int currentGold;

    public GoldManager(int initialGold) {
        this.currentGold = initialGold;
    }

    public int getGold() {
        return currentGold;
    }

    public void addGold(int amount) {

        if (amount > 0) {
            this.currentGold += amount;
        }
    }

    public boolean spendGold(int amount) {
        if (amount <= currentGold) {
            this.currentGold -= amount;
            return true;
        }
        return false;
    }
}
