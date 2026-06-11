package ua.edu.ukma.model.defense;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ua.edu.ukma.model.GameMap;

public class BarrierZone extends DefenseStructure {

    protected int hp;
    protected double timeLeft;
    protected final Group viewGroup;
    protected final ImageView mainView;

    public BarrierZone(int row, int col, DefenseType type, int hp, double duration) {
        super(row, col, type);
        this.hp = hp;
        this.timeLeft = duration;
        this.viewGroup = new Group();
        this.viewGroup.setMouseTransparent(true);

        Image mainTexture = new Image(type.texturePath());
        this.mainView = new ImageView(mainTexture);
        this.mainView.setSmooth(false);
        this.viewGroup.getChildren().add(mainView);
    }
    public Group getViewGroup(GameMap gameMap, int tileSize) {
        this.mainView.setFitWidth(tileSize);
        this.mainView.setFitHeight(tileSize);
        this.mainView.setX(getCol() * tileSize);
        this.mainView.setY(getRow() * tileSize);
        this.viewGroup.toFront();
        return viewGroup;
    }

    public void updateLifetime(double deltaTime) {
        this.timeLeft -= deltaTime;
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
    }

    public boolean isDestroyed() {
        return hp <= 0 || timeLeft <= 0;
    }

    public int getHp() {
        return hp;
    }

    public double getTimeLeft() {
        return timeLeft;
    }
}