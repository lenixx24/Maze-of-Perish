package ua.edu.ukma.resource;

import ua.edu.ukma.model.defense.DefenseType;

public class CardManager {
    private final DefenseType[] hand = DefenseType.values();
    private final int[] amounts = {
            10, // trap
            5,  // bomb
            4,  // turret
            3,  // freeze
            3,  // poison
            6,  // barrier
            2,  // sniper
            1,  // laser
            2   // cannon
    };

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
