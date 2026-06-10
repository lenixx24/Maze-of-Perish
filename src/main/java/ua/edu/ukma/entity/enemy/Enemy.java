package ua.edu.ukma.entity.enemy;
import javafx.animation.Animation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import ua.edu.ukma.entity.Direction;
import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.model.CellPosition;
import ua.edu.ukma.model.CellType;
import ua.edu.ukma.model.GameMap;
import ua.edu.ukma.model.defense.DefenseManager;
import ua.edu.ukma.model.defense.DefenseStructure;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Enemy extends Entity {
    private final int enemyType;
    protected SpriteAnimation attackAnimation; // Анімація удару
    private boolean isAttacking = false;
    private long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN_MS = 1000;
    private final int damage;
    protected GameMap gameMap;
    protected int tileSize;
    protected double velocityX;
    protected double velocityY;
protected Direction currentDir;
    protected double hitboxWidth;
    protected double hitboxHeight;

    private List<CellPosition> currentPath;
    private int currentPathIndex;
    private boolean reachedTower = false;
    protected SpriteAnimation walkAnimation;
    protected SpriteAnimation deathAnimation;
    protected boolean isDying = false;

    public Enemy(double startX, double startY, double speed, int damage, int maxHealth, int type, String spriteSheetPath, GameMap gameMap, int tileSize) {
        super(startX, startY, speed, maxHealth);
        this.damage=damage;
        this.enemyType = type;
        this.gameMap = gameMap;
        this.tileSize = tileSize;

        this.hitboxWidth = tileSize * 0.8;
        this.hitboxHeight = tileSize * 0.8;

        this.velocityX = speed;
        this.velocityY = speed;
        this.currentDir = Direction.RIGHT;
        loadSpriteSheet(spriteSheetPath);
    }

    @Override
    public void update() {

    }
    public void update(DefenseManager defenseManager){
        if (!active) return;
        if (!isDying) {
            if (currentPath == null)
                calculatePath();
            if (currentPath != null && currentPathIndex < currentPath.size())
                moveToNextNode(defenseManager);
            else if (currentPath != null && currentPathIndex >= currentPath.size())
                reachTower();
            imageView.setX(x);
            imageView.setY(y);
            imageView.toFront();
        }
    }

    private boolean canMoveTo(double nextX, double nextY) {
        double offsetX = (tileSize - hitboxWidth) / 2.0;
        double offsetY = (tileSize - hitboxHeight) / 2.0;
        double left = nextX + offsetX;
        double right = nextX + offsetX + hitboxWidth - 1;
        double top = nextY + offsetY;
        double bottom = nextY + offsetY + hitboxHeight - 1;
        int minCol = (int) (left / tileSize);
        int maxCol = (int) (right / tileSize);
        int minRow = (int) (top / tileSize);
        int maxRow = (int) (bottom / tileSize);

        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                if (!gameMap.isPassable(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void render() {}

    @Override
    protected void onDeath() {
        if(isDying) return;
        isDying = true;
        playAnimation(deathAnimation);
        deathAnimation.setOnFinished(e -> {
            super.onDeath();
        });
    }
    private void calculatePath() {
        CellPosition start = new CellPosition(getCurrentRow(), getCurrentCol());
        CellPosition towerPos = gameMap.findFirst(CellType.TOWER);
        currentPath = PathFinder.findPath(gameMap, start, towerPos);
        currentPathIndex = 1;
    }

    private void moveToNextNode(DefenseManager defenseManager) {
        CellPosition targetCell = currentPath.get(currentPathIndex);
        DefenseStructure defense = defenseManager.getDefenseAt(targetCell.row(), targetCell.col());

        if (defense != null && defenseManager.isAttackable(defense.getType())) {
            isAttacking = true;
            attackDefense(defense);
            return;
        }
        isAttacking = false;
        playAnimation(walkAnimation);

        double targetX = targetCell.col() * tileSize;
        double targetY = targetCell.row() * tileSize;
        double dx = targetX - x;
        double dy = targetY - y;

        if (dx > 0) imageView.setScaleX(1);
        else if (dx < 0) imageView.setScaleX(-1);

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed) {
            x = targetX;
            y = targetY;
            currentPathIndex++;
        } else {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    private void reachTower() {
        this.reachedTower = true;
        playAnimation(attackAnimation);
        attackAnimation.setOnFinished(e->super.onDeath());
    }

    public boolean isReachedTower() {
        return reachedTower;
    }

    protected void playAnimation(SpriteAnimation newAnimation) {
        if (currentAnimation != null) {
            if(currentAnimation.equals(newAnimation)) return;
            currentAnimation.stop();
        }

        currentAnimation = newAnimation;
        currentAnimation.play();
    }
    Random r = new Random();
    protected void loadSpriteSheet(String path){
        Image spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        this.imageView = new ImageView(spriteSheet);
        this.imageView.setX(x);
        this.imageView.setY(y);
        imageView.setFitWidth(tileSize);
        imageView.setFitHeight(tileSize);
    }
    private int getCurrentRow() {
        return (int) ((y + tileSize / 2.0) / tileSize);
    }

    private int getCurrentCol() {
        return (int) ((x + tileSize / 2.0) / tileSize);
    }
    private void attackDefense(DefenseStructure defense) {
        playAnimation(attackAnimation);

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAttackTime >= ATTACK_COOLDOWN_MS) {
            defense.takeDamage(damage);
            lastAttackTime = currentTime;
            System.out.println("Enemy is damaging "+defense.getType().getName());
        }
    }
    protected void loadAnimations() {
        walkAnimation = new SpriteAnimation(
                imageView, Duration.millis(600),
                6, 3, 0, 0, 48, 48
        );
        walkAnimation.setCycleCount(Animation.INDEFINITE);
        deathAnimation = new SpriteAnimation(
                imageView, Duration.millis(800),
                9, 3, 0, 96, 48, 48
        );
        deathAnimation.setCycleCount(1);
        attackAnimation = new SpriteAnimation(
                imageView, Duration.millis(600),
                6, 3, 0, 240, 48, 48
        );
        attackAnimation.setCycleCount(1);
    }
}