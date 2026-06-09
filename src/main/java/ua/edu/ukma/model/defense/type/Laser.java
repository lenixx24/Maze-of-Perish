package ua.edu.ukma.model.defense.type;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

import java.util.List;

public class Laser extends AttackTower {
    private double shootCooldown = 0.0;
    private final double shootDelay = 0.1;

    private Line laserLine;
    private ImagePattern beamPattern;
    private boolean isBeamVisible = false;

    public Laser(int row, int col) {
        super(row, col, DefenseType.LASER_TOWER, 10, 10, 10, 10);
    }

    public void updateTurret(List<Enemy> activeEnemies, GameMap gameMap, int tileSize, double deltaTime) {
        double centerX = (getCol() * tileSize) + (tileSize / 2.0);
        double centerY = (getRow() * tileSize) + (tileSize / 2.0);

        Enemy target = null;
        double minDistance = Double.MAX_VALUE;
        double[] targetLaserPoint = new double[]{centerX, centerY};
        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive()) {
                double dist = Math.hypot(enemy.getX() - centerX, enemy.getY() - centerY);
                if (dist < minDistance) {
                    double[] currentTestPoint = new double[]{enemy.getX() + tileSize / 2.0, enemy.getY() + tileSize / 2.0};

                    if (seenTarget(centerX, centerY, currentTestPoint, gameMap, tileSize)) {
                        minDistance = dist;
                        target = enemy;
                        targetLaserPoint = currentTestPoint;
                    }
                }
            }
        }

        if (shootCooldown > 0) {
            shootCooldown -= deltaTime;
        }
        if (target != null) {
            double dx = targetLaserPoint[0] - centerX;
            double dy = targetLaserPoint[1] - centerY;
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            this.barrelView.setRotate(angle + 90);
            drawLaserLine(centerX, centerY, targetLaserPoint[0], targetLaserPoint[1]);

            if (shootCooldown <= 0) {
                target.takeDamage(5);
                shootCooldown = shootDelay;
            }
        } else {
            removeLaser();
        }
    }

    private boolean seenTarget(double startX, double startY, double[] endPoint, GameMap gameMap, int tileSize) {
        double endX = endPoint[0];
        double endY = endPoint[1];

        double distance = Math.hypot(endX - startX, endY - startY);
        double step = 4.0;
        int numSteps = (int) (distance / step);
        double dx = (endX - startX) / distance;
        double dy = (endY - startY) / distance;

        for (int i = 1; i < numSteps; i++) {
            double currentX = startX + dx * (i * step);
            double currentY = startY + dy * (i * step);
            int col = (int) (currentX / tileSize);
            int row = (int) (currentY / tileSize);

            if (row >= 0 && row < gameMap.rows() && col >= 0 && col < gameMap.cols()) {
                if (gameMap.getCell(row, col) == CellType.WALL) {
                    endPoint[0] = currentX;
                    endPoint[1] = currentY;
                    return false;
                }
            }
        }
        return true;
    }

    private void drawLaserLine(double startX, double startY, double endX, double endY) {
        if (laserLine == null) {
            laserLine = new Line();
            laserLine.setMouseTransparent(true);
            laserLine.setStrokeWidth(4);

            if (beamPattern != null) {
                laserLine.setStroke(beamPattern);
            } else {
                laserLine.setStyle("-fx-stroke: #00ffcc;");
            }
        }
        laserLine.setStartX(startX);
        laserLine.setStartY(startY);
        laserLine.setEndX(endX);
        laserLine.setEndY(endY);

        if (!isBeamVisible && viewGroup.getParent() instanceof Pane parentPane) {
            parentPane.getChildren().add(laserLine);
            isBeamVisible = true;
            this.viewGroup.toFront();
        }
    }

    private void removeLaser() {
        if (isBeamVisible && laserLine != null && laserLine.getParent() instanceof Pane parentPane) {
            parentPane.getChildren().remove(laserLine);
        }
        isBeamVisible = false;
    }

    public void cleanUp() {
        removeLaser();
    }

    public Group getViewGroup(int tileSize) {
        this.baseView.setFitWidth(tileSize);
        this.baseView.setFitHeight(tileSize);
        this.barrelView.setFitWidth(tileSize);
        this.barrelView.setFitHeight(tileSize);

        this.baseView.setX(0);
        this.baseView.setY(0);
        this.barrelView.setX(0);
        this.barrelView.setY(0);

        this.viewGroup.setTranslateX(this.getCol() * tileSize);
        this.viewGroup.setTranslateY(this.getRow() * tileSize);

        return viewGroup;
    }
}