package ua.edu.ukma.entity.player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.SpriteSheet;
import ua.edu.ukma.model.GameMap;

import java.util.List;

public class Player extends Entity {

    private static final double DISPLAY_WIDTH_RATIO = 40.0 / 48.0;
    private static final double DISPLAY_HEIGHT_RATIO = 46.0 / 48.0;

    private static final long FRAME_DELAY_NANOS = 95_000_000L;

    private final GameMap gameMap;
    private final int tileSize;

    private final double displayWidth;
    private final double displayHeight;

    private final SpriteSheet<PlayerFrameType> spriteSheet;

    private Direction viewDirection = Direction.RIGHT;
    private PlayerState state = PlayerState.CARD;

    private boolean moving = false;

    private int targetRow;
    private int targetCol;

    private long lastFrameTime = 0;
    private int walkingFrameIndex = 0;

    public Player(int startRow, int startCol, GameMap gameMap, int tileSize) {
        super(calculateX(startCol, tileSize), calculateY(startRow, tileSize), calculateSpeed(tileSize), 100);

        this.gameMap = gameMap;
        this.tileSize = tileSize;
        this.displayWidth = calculateDisplayWidth(tileSize);
        this.displayHeight = calculateDisplayHeight(tileSize);
        this.targetRow = startRow;
        this.targetCol = startCol;

        this.spriteSheet = new SpriteSheet<>("/player/hero.png", PlayerFrameType::x, PlayerFrameType::y, PlayerFrameType::width, PlayerFrameType::height);

        this.imageView = spriteSheet.createImageView();
        this.imageView.setFitWidth(displayWidth);
        this.imageView.setFitHeight(displayHeight);
        this.imageView.setPreserveRatio(false);
        this.imageView.setMouseTransparent(true);

        setCardFrame();
        updateImageViewPosition();
    }

    public void move(Direction direction) {
        if (moving || !active) {
            return;
        }

        if (direction.isHorizontal()) {
            this.viewDirection = direction;
        }

        int currentRow = getCurrentRow();
        int currentCol = getCurrentCol();

        int nextRow = currentRow + direction.rowDelta();
        int nextCol = currentCol + direction.colDelta();

        if (!gameMap.isPassable(nextRow, nextCol)) {
            setCardFrame();
            return;
        }

        this.targetRow = findTargetRow(currentRow, currentCol, direction);
        this.targetCol = findTargetCol(currentRow, currentCol, direction);

        this.state = PlayerState.WALKING;
        this.moving = true;
        this.walkingFrameIndex = 0;
        this.lastFrameTime = 0;

        setWalkingFrame();
    }

    @Override
    public void update() {
        if (!active || !moving) {
            return;
        }

        moveToTarget();

        if (isAtTarget()) {
            snapToTarget();
            moving = false;
            setCardFrame();
        }

        updateImageViewPosition();
    }

    public void updateAnimation(long now) {
        if (state != PlayerState.WALKING) {
            return;
        }

        if (lastFrameTime != 0 && now - lastFrameTime < FRAME_DELAY_NANOS) {
            return;
        }

        setWalkingFrame();

        walkingFrameIndex++;
        lastFrameTime = now;
    }

    @Override
    public void render() {
    }

    private void setWalkingFrame() {
        List<PlayerFrameType> frames = PlayerFrameType.walkingCycle(viewDirection);

        if (frames.isEmpty()) {
            setCardFrame();
            return;
        }

        PlayerFrameType frame = frames.get(walkingFrameIndex % frames.size());
        spriteSheet.applyFrame(imageView, frame);

        imageView.setTranslateY(walkingFrameIndex % 2 == 0 ? 0 : -1);
    }

    private void setCardFrame() {
        PlayerFrameType frame = PlayerFrameType.card(viewDirection);
        spriteSheet.applyFrame(imageView, frame);

        state = PlayerState.CARD;
        imageView.setTranslateY(0);
        imageView.setRotate(0);
    }

    public void showCardFrame() {
        setCardFrame();
    }

    private int findTargetRow(int startRow, int startCol, Direction direction) {
        int row = startRow;
        int col = startCol;

        while (gameMap.isPassable(row + direction.rowDelta(), col + direction.colDelta())) {
            row += direction.rowDelta();
            col += direction.colDelta();
        }

        return row;
    }

    private int findTargetCol(int startRow, int startCol, Direction direction) {
        int row = startRow;
        int col = startCol;

        while (gameMap.isPassable(row + direction.rowDelta(), col + direction.colDelta())) {
            row += direction.rowDelta();
            col += direction.colDelta();
        }

        return col;
    }

    private void moveToTarget() {
        double targetX = calculateX(targetCol, tileSize);
        double targetY = calculateY(targetRow, tileSize);

        double dx = targetX - x;
        double dy = targetY - y;

        if (Math.abs(dx) <= speed) {
            x = targetX;
        } else {
            x += Math.signum(dx) * speed;
        }

        if (Math.abs(dy) <= speed) {
            y = targetY;
        } else {
            y += Math.signum(dy) * speed;
        }
    }

    private boolean isAtTarget() {
        double targetX = calculateX(targetCol, tileSize);
        double targetY = calculateY(targetRow, tileSize);

        return Math.abs(x - targetX) < 0.01 && Math.abs(y - targetY) < 0.01;
    }

    private void snapToTarget() {
        x = calculateX(targetCol, tileSize);
        y = calculateY(targetRow, tileSize);
    }

    private void updateImageViewPosition() {
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
    }

    private int getCurrentRow() {
        return (int) ((y + displayHeight / 2.0) / tileSize);
    }

    private int getCurrentCol() {
        return (int) ((x + displayWidth / 2.0) / tileSize);
    }

    private static double calculateX(int col, int tileSize) {
        return col * tileSize + (tileSize - calculateDisplayWidth(tileSize)) / 2.0;
    }

    private static double calculateY(int row, int tileSize) {
        return row * tileSize + tileSize - calculateDisplayHeight(tileSize);
    }

    private static double calculateDisplayWidth(int tileSize) {
        return tileSize * DISPLAY_WIDTH_RATIO;
    }

    private static double calculateDisplayHeight(int tileSize) {
        return tileSize * DISPLAY_HEIGHT_RATIO;
    }

    private static double calculateSpeed(int tileSize) {
        return Math.max(2.0, tileSize / 16.0);
    }

    public boolean isMoving() {
        return moving;
    }

    public ImageView getView() {
        return imageView;
    }
}