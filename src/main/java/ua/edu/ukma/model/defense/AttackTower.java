package ua.edu.ukma.model.defense;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ua.edu.ukma.entity.SpriteSheet;
import ua.edu.ukma.entity.enemy.Enemy;
import ua.edu.ukma.exception.AssetLoadingException;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;

import java.util.*;

public class AttackTower extends DefenseStructure {
    private int hp;
    protected final double attackRange;
    protected final double damage;
    protected final double fireRate;
    protected final SpriteSheet<Integer> spriteSheet;
    protected final ImageView baseView;
    protected final ImageView barrelView;
    protected final Group viewGroup;

    protected double shootCooldown;
    protected double shootDelay;
    protected final List<FxBullet> bullets = new ArrayList<>();
    protected Enemy currentTarget = null;
    private final String bulletTexturePath;
    private final double bulletSpeed;
    private static final Map<String, Image> BULLET_TEXTURE = new HashMap<>();

    public AttackTower(int row, int col, DefenseType type, int hp, double attackRange, double damage, double fireRate, String bulletTexturePath, double bulletSpeed) {
        super(row, col, type);
        this.hp = hp;
        this.attackRange = attackRange;
        this.damage = damage;
        this.fireRate = fireRate;

        this.bulletTexturePath = bulletTexturePath;
        this.bulletSpeed = bulletSpeed;

        this.shootDelay = 1.0 / fireRate;
        this.shootCooldown = 0.0;

        this.spriteSheet = new SpriteSheet<>(type.texturePath(), frame -> frame * 32, frame -> 0, frame -> 32, frame -> 32);
        this.baseView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.baseView, 0);
        this.barrelView = this.spriteSheet.createImageView();
        this.spriteSheet.applyFrame(this.barrelView, 1);
        this.viewGroup = new Group(baseView, barrelView);
        this.viewGroup.setMouseTransparent(true);
        preloadBulletTexture(bulletTexturePath);
    }
    private void preloadBulletTexture(String path) {
        if (path != null && !BULLET_TEXTURE.containsKey(path)) {
            try {
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                BULLET_TEXTURE.put(path, img);
            } catch (Exception e) {
                throw new AssetLoadingException("no texture found");
            }
        }
    }

    public void updateTower(List<Enemy> activeEnemies, GameMap gameMap, int tileSize, double deltaTime) {
        double centerX = (getCol() * tileSize) + (tileSize / 2.0);
        double centerY = (getRow() * tileSize) + (tileSize / 2.0);
        if (!shouldLockTarget() || !isTargetValid(currentTarget, centerX, centerY, gameMap, tileSize)) {
            currentTarget = null;
        }
        if (currentTarget == null) {
            double minDistance = Double.MAX_VALUE;
            for (Enemy enemy : activeEnemies) {
                if (enemy.isActive()) {
                    double dist = Math.hypot(enemy.getX() - centerX, enemy.getY() - centerY);
                    if (dist <= attackRange && dist < minDistance) {
                        double[] currentTestPoint = new double[]{enemy.getX() + tileSize / 2.0, enemy.getY() + tileSize / 2.0};

                        if (seenTarget(centerX, centerY, currentTestPoint, gameMap, tileSize)) {
                            minDistance = dist;
                            currentTarget = enemy;
                        }
                    }
                }
            }
        }
        if (shootCooldown > 0) {
            shootCooldown -= deltaTime;
        }
        if (currentTarget != null) {
            double targetX = currentTarget.getX() + tileSize / 2.0;
            double targetY = currentTarget.getY() + tileSize / 2.0;
            double[] targetPoint = new double[]{targetX, targetY};
            seenTarget(centerX, centerY, targetPoint, gameMap, tileSize);

            double dx = targetPoint[0] - centerX;
            double dy = targetPoint[1] - centerY;
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            this.barrelView.setRotate(angle + 90);

            processAttack(currentTarget, centerX, centerY, targetPoint[0], targetPoint[1], tileSize);
        } else {
            onNoTarget();
        }
        updateBullets(activeEnemies, gameMap, tileSize, deltaTime);
    }
    private boolean isTargetValid(Enemy enemy, double centerX, double centerY, GameMap gameMap, int tileSize) {
        if (enemy == null || !enemy.isActive()) return false;
        double dist = Math.hypot(enemy.getX() - centerX, enemy.getY() - centerY);
        if (dist > attackRange) return false;
        double[] testPoint = new double[]{enemy.getX() + tileSize / 2.0, enemy.getY() + tileSize / 2.0};
        return seenTarget(centerX, centerY, testPoint, gameMap, tileSize);
    }
    protected boolean shouldLockTarget() {
        return false;
    }
    protected void processAttack(Enemy target, double startX, double startY, double targetX, double targetY, int tileSize) {
        if (shootCooldown <= 0) {
            shoot(startX, startY, targetX, targetY, tileSize);
            shootCooldown = shootDelay;
        }
    }
    protected void onNoTarget() {
    }

    private void updateBullets(List<Enemy> activeEnemies, GameMap gameMap, int tileSize, double deltaTime) {
        Iterator<FxBullet> bIterator = bullets.iterator();
        while (bIterator.hasNext()) {
            FxBullet bullet = bIterator.next();
            bullet.update(deltaTime, gameMap, tileSize);

            boolean hit = false;
            for (Enemy enemy : activeEnemies) {
                if (enemy.isActive() && bullet.collidesWith(enemy, tileSize)) {
                    handleBulletHit(bullet, enemy, activeEnemies, tileSize);
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
    protected void handleBulletHit(FxBullet bullet, Enemy targetEnemy, List<Enemy> activeEnemies, int tileSize) {
        targetEnemy.takeDamage((int) this.damage);
    }

    protected boolean seenTarget(double startX, double startY, double[] endPoint, GameMap gameMap, int tileSize) {
        double distance = Math.hypot(endPoint[0] - startX, endPoint[1] - startY);
        double step = 4.0;
        int numSteps = (int) (distance / step);
        double dx = (endPoint[0] - startX) / distance;
        double dy = (endPoint[1] - startY) / distance;

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

    private void shoot(double startX, double startY, double targetX, double targetY, int tileSize) {
        if (this.bulletTexturePath == null) return;

        Image cachedImage = BULLET_TEXTURE.get(this.bulletTexturePath);
        if (cachedImage == null) return;

        FxBullet newBullet = new FxBullet(startX, startY, targetX, targetY, tileSize, cachedImage, this.bulletSpeed);
        this.bullets.add(newBullet);

        if (viewGroup.getParent() instanceof Pane parentPane) {
            parentPane.getChildren().add(newBullet.getBulletNode());
            this.viewGroup.toFront();
        }
    }
    public static class FxBullet {
        private double x, y;
        private final double moveX;
        private final double moveY;
        private final ImageView bulletView;
        private boolean hitWall = false;

        public FxBullet(double startX, double startY, double targetX, double targetY, int tileSize, Image bulletImage, double speed) {
            this.x = startX;
            this.y = startY;

            this.bulletView = new ImageView(bulletImage);
            this.bulletView.setFitWidth(tileSize / 2.0);
            this.bulletView.setFitHeight(tileSize / 2.0);
            this.bulletView.setMouseTransparent(true);

            double angle = Math.atan2(targetY - startY, targetX - startX);
            this.bulletView.setRotate(Math.toDegrees(angle) + 90);

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

        public boolean isHitWall() { return hitWall; }

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

        public Node getBulletNode() { return bulletView; }
        public double getX() { return x; }
        public double getY() { return y; }
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
    public List<FxBullet> getBullets() { return bullets; }
    public boolean isDestroyed() { return hp <= 0; }
    public int getHp() { return hp; }
    public void takeDamage(int amount) { this.hp -= amount; }
    public double getAttackRange() { return attackRange; }
    public double getDamage() { return damage; }
    public double getFireRate() { return fireRate; }
}