package ua.edu.ukma.ui;

import ua.edu.ukma.map.MazeFactory;
import ua.edu.ukma.model.GameMap;

import java.util.function.Supplier;

public record LevelInfo(int number, String title, String description, int price, Supplier<GameMap> mapSupplier) {
    public static LevelInfo[] defaultLevels() {
        return new LevelInfo[] {
                new LevelInfo(1, "First Maze", "Basic starting level", 0, MazeFactory::createLevel1Maze),
                new LevelInfo(2, "Dark Corridors", "More turns and traps", 100, MazeFactory::createLevel2Maze),
                new LevelInfo(3, "Final Trial", "The hardest map", 250, MazeFactory::createLevel3Maze)
        };
    }
}
