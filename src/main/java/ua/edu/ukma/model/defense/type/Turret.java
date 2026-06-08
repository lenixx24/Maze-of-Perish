package ua.edu.ukma.model.defense.type;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ua.edu.ukma.entity.SpriteSheet;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.AttackTower;
import ua.edu.ukma.model.defense.DefenseType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Turret extends AttackTower {

    private final SpriteSheet<Integer> spriteSheet;
    private final ImageView baseView;
    private final ImageView barrelView;
    private final Group viewGroup;

    private double shootCooldown = 0.0;
    private final double shootDelay = 0.7;

    private final List<FxBullet> bullets = new ArrayList<>();

    public Turret(int row, int col) {
        super(row, col, DefenseType.TURRET, 10, 10, 10, 10);

        this.spriteSheet = new SpriteSheet<>(
                DefenseType.TURRET.texturePath(),
                frame -> frame * 32,
                frame -> 0,
                frame -> 32,
                frame -> 32
        );

        this.baseView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.baseView, 0);

        this.barrelView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.barrelView, 1);

        this.viewGroup = new Group(baseView, barrelView);
        this.viewGroup.setMouseTransparent(true);
    }

    public void updateTurret(List<Enemy> activeEnemies, GameMap gameMap, int tileSize, double deltaTime) {
        double centerX = (getCol() * tileSize) + (tileSize / 2.0);
        double centerY = (getRow() * tileSize) + (tileSize / 2.0);

        Enemy target = null;
        double minDistance = Double.MAX_VALUE;
        for (Enemy enemy : activeEnemies) {
            if (enemy.isActive()) {
                double dist = Math.hypot(enemy.getX() - centerX, enemy.getY() - centerY);
                if (dist < minDistance) {
                    if (seenTarget(centerX, centerY, enemy.getX() + tileSize / 2.0, enemy.getY() + tileSize / 2.0, gameMap, tileSize)) {
                        minDistance = dist;
                        target = enemy;
                    }
                }
            }
        }if (target != null) {
            double predictedX = target.getX() + tileSize / 2.0;
            double predictedY = target.getY() + tileSize / 2.0;
            double dx = predictedX - centerX;
            double dy = predictedY - centerY;
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            this.barrelView.setRotate(angle + 90);
            if (shootCooldown > 0) {
                shootCooldown -= deltaTime;
            }
            if (shootCooldown <= 0) {
                shoot(centerX, centerY, predictedX, predictedY, tileSize);
                shootCooldown = shootDelay;
            }
        }

        Iterator<FxBullet> bIterator = bullets.iterator();
        while (bIterator.hasNext()) {
            FxBullet bullet = bIterator.next();
            bullet.update(deltaTime, gameMap, tileSize);

            boolean hit = false;
            for (Enemy enemy : activeEnemies) {
                if (enemy.isActive() && bullet.collidesWith(enemy, tileSize)) {
                    enemy.takeDamage(25);
                    hit = true;
                    break;
                }
            }

            if (hit || bullet.isHitWall() || bullet.getX() < -200 || bullet.getX() > 3000 || bullet.getY() < -200 || bullet.getY() > 3000) {
                bullet.removeView();
                bIterator.remove();
            }
        }
    }

    private boolean seenTarget(double startX, double startY, double endX, double endY, GameMap gameMap, int tileSize) {
        double distance = Math.hypot(endX - startX, endY - startY);
        double step = 10.0;
        int numSteps = (int) (distance / step);
        double dx = (endX - startX) / distance;
        double dy = (endY - startY) / distance;
        for (int i = 1; i < numSteps; i++) {
            double currentX = startX + dx * (i * step);
            double currentY = startY + dy * (i * step);
            int col = (int) (currentX / tileSize);
            int row = (int) (currentY / tileSize);
            if (row >= 0 && row < gameMap.rows() && col >= 0 && col < gameMap.cols()) {
                if (gameMap.getCell(row, col) == ua.edu.ukma.model.CellType.WALL) {
                    return false;
                }
            }
        }
        return true;
    }

    private void shoot(double startX, double startY, double targetX, double targetY, int tileSize) {
        FxBullet newBullet = new FxBullet(startX, startY, targetX, targetY, tileSize);
        this.bullets.add(newBullet);

        if (viewGroup.getParent() instanceof Pane parentPane) {
            parentPane.getChildren().add(newBullet.getBulletNode());
            this.viewGroup.toFront();
        }
    }

    public Group getViewGroup(int tileSize) {
        this.baseView.setFitWidth(tileSize);
        this.baseView.setFitHeight(tileSize);
        this.barrelView.setFitWidth(tileSize);
        this.barrelView.setFitHeight(tileSize);

        double offset = (tileSize - (double) tileSize) / 2.0;
        this.baseView.setX(offset);
        this.baseView.setY(offset);
        this.barrelView.setX(offset);
        this.barrelView.setY(offset);

        this.viewGroup.setTranslateX(this.getCol() * tileSize);
        this.viewGroup.setTranslateY(this.getRow() * tileSize);

        return viewGroup;
    }

    public List<FxBullet> getBullets() {
        return bullets;
    }


public static class FxBullet {
    private double x, y;
    private final double moveX;
    private final double moveY;
    private final ImageView bulletView;
    private boolean hitWall = false;

    public FxBullet(double startX, double startY, double targetX, double targetY, int tileSize) {
        this.x = startX;
        this.y = startY;

        Image turretImage = new ImageView("/defense/bullet.png").getImage();
        this.bulletView = new ImageView(turretImage);

        this.bulletView.setFitWidth(tileSize);
        this.bulletView.setFitHeight(tileSize);

        this.bulletView.setMouseTransparent(true);

        double angle = Math.atan2(targetY - startY, targetX - startX);
        this.bulletView.setRotate(Math.toDegrees(angle) + 90);

        double speed = 450.0;
        this.moveX = speed * Math.cos(angle);
        this.moveY = speed * Math.sin(angle);

        updatePosition();
    }

    public void update(double deltaTime, GameMap gameMap, int tileSize) {
        this.x += moveX * deltaTime;
        this.y += moveY * deltaTime;
        updatePosition();

        int currentCol = (int) (this.x / tileSize);
        int currentRow = (int) (this.y / tileSize);

        if (currentRow >= 0 && currentRow < gameMap.rows() && currentCol >= 0 && currentCol < gameMap.cols()) {
            if (gameMap.getCell(currentRow, currentCol) == CellType.WALL) {
                this.hitWall = true;
            }
        }
    }

    public boolean isHitWall() {
        return hitWall;
    }

    private void updatePosition() {
        this.bulletView.setX(this.x - this.bulletView.getFitWidth() / 2.0);
        this.bulletView.setY(this.y - this.bulletView.getFitHeight() / 2.0);
    }

    public boolean collidesWith(Enemy enemy, int tileSize) {
        double enemyCenterX = enemy.getX() + tileSize / 2.0;
        double enemyCenterY = enemy.getY() + tileSize / 2.0;
        return Math.hypot(this.x - enemyCenterX, this.y - enemyCenterY) < (tileSize * 0.4);
    }

    public void removeView() {
        if (this.bulletView != null && this.bulletView.getParent() instanceof Pane pane) {
            pane.getChildren().remove(this.bulletView);
        }
    }

    public Node getBulletNode() {
        return bulletView;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}}