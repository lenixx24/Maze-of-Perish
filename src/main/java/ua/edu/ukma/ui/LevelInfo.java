package ua.edu.ukma.ui;

import ua.edu.ukma.map.MazeFactory;
import ua.edu.ukma.model.GameMap;

import java.util.function.Supplier;

public record LevelInfo(int number, String title, String description, int price, Supplier<GameMap> mapSupplier,int[] initialCards,int goldPerCoin) {
    public static LevelInfo[] defaultLevels() {
        return new LevelInfo[] {
                new LevelInfo(1, "First Maze", "Basic starting level", 0, MazeFactory::createLevel1Maze, new int[]{3, 2, 2, 1, 1, 2, 1, 1, 1}, 1),    // trap, bomb, turret, freeze, poison, barrier, sniper, laser, cannon
                new LevelInfo(2, "Dark Corridors", "More turns and traps", 100, MazeFactory::createLevel2Maze, new int[]{5, 4, 2, 2, 2, 4, 2, 1, 1}, 2),
                new LevelInfo(3, "Final Trial", "The hardest map", 250, MazeFactory::createLevel3Maze, new int[]{8, 7, 3, 3, 5, 5, 3, 2, 2}, 3),
        };
    }
}
