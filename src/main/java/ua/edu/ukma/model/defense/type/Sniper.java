package ua.edu.ukma.model.defense.type;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sniper extends AttackTower {

    private double shootCooldown = 0.0;
    private final double shootDelay = 2.5;

    private final List<SniperBullet> activeBullets = new ArrayList<>();
    private static Image bulletImage;

    public Sniper(int row, int col) {
        super(row, col, DefenseType.SNIPER_TOWER, 10, 10, 10, 10);
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
            double targetX = target.getX() + tileSize / 2.0;
            double targetY = target.getY() + tileSize / 2.0;
            double dx = targetX - centerX;
            double dy = targetY - centerY;

            double angle = Math.toDegrees(Math.atan2(dy, dx));
            this.barrelView.setRotate(angle + 90);

            if (shootCooldown <= 0) {
                shoot(centerX, centerY, targetLaserPoint[0], targetLaserPoint[1], target);
                shootCooldown = shootDelay;
            }
        }
        Iterator<SniperBullet> iterator = activeBullets.iterator();
        while (iterator.hasNext()) {
            SniperBullet bullet = iterator.next();
            bullet.update(deltaTime);
            if (bullet.isDestroyed()) {
                iterator.remove();
            }
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

    private void shoot(double startX, double startY, double targetX, double targetY, Enemy target) {
        if (viewGroup.getParent() instanceof Pane parentPane) {
            SniperBullet bullet = new SniperBullet(startX, startY, targetX, targetY, target, parentPane);
            activeBullets.add(bullet);
            this.viewGroup.toFront();
        }
    }

    public void cleanUp() {
        for (SniperBullet bullet : activeBullets) {
            bullet.removeView();
        }
        activeBullets.clear();
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

    private static class SniperBullet {
        private final ImageView bulletView;
        private final Pane parentPane;
        private final Enemy target;

        private double currentX;
        private double currentY;
        private final double targetX;
        private final double targetY;

        private final double speed = 700.0;
        private boolean destroyed = false;

        public SniperBullet(double startX, double startY, double targetX, double targetY, Enemy target, Pane parentPane) {
            this.currentX = startX;
            this.currentY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.target = target;
            this.parentPane = parentPane;

            this.bulletView = new ImageView(bulletImage);
            this.bulletView.setMouseTransparent(true);

            this.bulletView.setFitWidth(32);
            this.bulletView.setFitHeight(32);

            this.bulletView.setX(-8);
            this.bulletView.setY(-8);

            this.bulletView.setTranslateX(startX);
            this.bulletView.setTranslateY(startY);

            double dx = targetX - startX;
            double dy = targetY - startY;
            double angle = Math.toDegrees(Math.atan2(dy, dx));

            Rotate rotate = new Rotate(angle, 0, 0);
            this.bulletView.getTransforms().add(rotate);

            this.parentPane.getChildren().add(this.bulletView);
        }

        public void update(double deltaTime) {
            if (destroyed) return;

            double dx = targetX - currentX;
            double dy = targetY - currentY;
            double distance = Math.hypot(dx, dy);
            double step = speed * deltaTime;

            if (step >= distance) {
                currentX = targetX;
                currentY = targetY;
                if (target != null && target.isActive() && Math.hypot(target.getX() + 16 - targetX, target.getY() + 16 - targetY) < 20) {
                    target.takeDamage(120);
                }
                destroy();
            } else {
                currentX += (dx / distance) * step;
                currentY += (dy / distance) * step;

                this.bulletView.setTranslateX(currentX);
                this.bulletView.setTranslateY(currentY);
            }
        }

        public void removeView() {
            if (bulletView != null) {
                parentPane.getChildren().remove(bulletView);
            }
        }

        private void destroy() {
            destroyed = true;
            removeView();
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}