package ua.edu.ukma.resource;

public class ManaManager {
    private double currentMana;
    private final int maxMana;
    private final double regenSpeed;

    public ManaManager(int initialMana, int maxMana, double regenSpeed) {
        this.currentMana = initialMana;
        this.maxMana = maxMana;
        this.regenSpeed = regenSpeed;
    }

    public int getMana() { return (int) currentMana; }

    public void decreaseMana(int amount) {
        this.currentMana = Math.max(0, this.currentMana - amount);
    }

    public void regenerate(double deltaTime) {
        if (this.currentMana < maxMana) {
            this.currentMana = Math.min(maxMana, this.currentMana + (regenSpeed * deltaTime));
        }
    }
}
