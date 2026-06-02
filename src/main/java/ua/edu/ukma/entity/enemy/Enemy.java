package ua.edu.ukma.entity.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.entity.Direction;
import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.SpriteAnimation;
import ua.edu.ukma.model.GameMap;

import java.util.Objects;
import java.util.Random;

public class Enemy extends Entity {
    private final int enemyType;

    protected GameMap gameMap;
    protected int tileSize;
    protected double velocityX;
    protected double velocityY;
protected Direction currentDir;
    protected double hitboxWidth;
    protected double hitboxHeight;

    protected SpriteAnimation walkAnimation;
    protected SpriteAnimation deathAnimation;
    protected boolean isDying = false;

    public Enemy(double startX, double startY, double speed, int maxHealth, int type, String spriteSheetPath, GameMap gameMap, int tileSize) {
        super(startX, startY, speed, maxHealth);
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
        if (!active) return;

        if (health <= 0 && !isDying) {
            isDying = true;
            playAnimation(deathAnimation);
            deathAnimation.setOnFinished(e -> {
                onDeath();
            });
            return;
        }

        if (!isDying) {
            double nextX = x + velocityX;
            double nextY = y + velocityY;
            if (canMoveTo(nextX, nextY)) {
                x = nextX;
                y = nextY;
            } else {
               changeDirection();
            }

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
        super.onDeath();
    }
    protected void changeDirection(){

        Direction dir = switch (r.nextInt(0, 4)) {

        case 0 -> Direction.LEFT;

        case 1 -> Direction.UP;

        case 2 -> Direction.DOWN;

        default -> Direction.RIGHT;

        };

velocityX = speed*dir.colDelta();
 velocityY = speed* dir.rowDelta();
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
}