package ua.edu.ukma.resource;

import ua.edu.ukma.model.defense.DefenseType;

public class CardManager {
    private final DefenseType[] hand = DefenseType.values();
    private final int[] amounts;

    public CardManager(int[] initialAmounts) {
        this.amounts = initialAmounts.clone();
    }

    public DefenseType[] getHand() {
        return hand;
    }
    public int getCardAmount(int index) {
        if (index >= 0 && index < amounts.length) {
            return amounts[index];
        }
        return 0;
    }
    public boolean isCardReady(int index) {
        return getCardAmount(index) > 0;
    }
    public void useCard(int index) {
        if (index >= 0 && index < amounts.length) {
            if (amounts[index] > 0) {
                amounts[index]--;
            }
        }
    }
    public void update(double deltaTime) {
    }
}
