package ua.edu.ukma.model.defense.type;

import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

public class Laser extends AttackTower {
    private Line laserLine;
    private final ImagePattern beamPattern;
    private boolean isBeamVisible = false;

    public Laser(int row, int col) {
        super(row, col, DefenseType.LASER_TOWER, 10, 250.0, 50, 10.0, null, 0);
        this.shootDelay = 0.1;
        this.beamPattern = null;

    }
    @Override
    protected void processAttack(Enemy target, double startX, double startY, double targetX, double targetY, int tileSize) {
        drawLaserLine(startX, startY, targetX, targetY);

        if (shootCooldown <= 0) {
            target.takeDamage(5);
            shootCooldown = shootDelay;
        }
    }
    @Override
    protected void onNoTarget() {
        removeLaser();
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
}