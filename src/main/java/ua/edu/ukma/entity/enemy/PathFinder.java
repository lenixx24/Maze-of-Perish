package ua.edu.ukma.entity.enemy;


import ua.edu.ukma.model.CellPosition;
import ua.edu.ukma.model.GameMap;
import java.util.*;

public class PathFinder {

    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };
    public static List<CellPosition> findPath(GameMap gameMap, CellPosition start, CellPosition target) {
        Queue<CellPosition> queue = new LinkedList<>();
        Map<CellPosition, CellPosition> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            CellPosition current = queue.poll();
            if (current.row() == target.row() && current.col() == target.col())
                return reconstructPath(cameFrom, current);
            for (int[] dir : DIRECTIONS) {
                int nextRow = current.row() + dir[0];
                int nextCol = current.col() + dir[1];
                CellPosition next = new CellPosition(nextRow, nextCol);

                if (isValid(gameMap, nextRow, nextCol, target) && !cameFrom.containsKey(next)) {
                    queue.add(next);
                    cameFrom.put(next, current);
                }
            }
        }
        return Collections.emptyList();
    }

    private static boolean isValid(GameMap gameMap, int row, int col, CellPosition target) {

        if (row < 0 || row >= gameMap.rows() || col < 0 || col >= gameMap.cols())
            return false;
        boolean isTarget = (row == target.row() && col == target.col());
        return isTarget || gameMap.isPassable(row, col);
    }


    private static List<CellPosition> reconstructPath(Map<CellPosition, CellPosition> cameFrom, CellPosition current) {
        List<CellPosition> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
            System.out.println(current);
        }
        Collections.reverse(path);
        return path;
    }
}
