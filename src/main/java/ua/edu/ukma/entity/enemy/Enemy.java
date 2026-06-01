package ua.edu.ukma.entity.enemy;

import javafx.scene.canvas.GraphicsContext;
import ua.edu.ukma.config.GameScaleConfig;
import ua.edu.ukma.entity.Entity;
import ua.edu.ukma.entity.SpriteAnimation;

public class Enemy extends Entity {
    private final int enemyType; // 0-"Wanderer", 1-"Destroyer", 2-"Ram"
    protected SpriteAnimation walkAnimation;
    protected SpriteAnimation deathAnimation;
    protected boolean isDying = false;
    public Enemy(double startX, double startY, double speed, int maxHealth, int type, String spriteSheetPath) {
        super(startX, startY, speed, maxHealth);
        this.enemyType = type;
        loadSpriteSheet(spriteSheetPath);
    }

    @Override
    public void update() {
       // System.out.println("Updating enemy "+enemyType);
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
            imageView.setX(x);
            imageView.setY(y);
            imageView.toFront();
        }
    }

    @Override
    public void render() {

    }

    @Override
    protected void onDeath() {
        super.onDeath();
    }
    protected void playAnimation(SpriteAnimation newAnimation) {
        if (currentAnimation != null) {
            if(currentAnimation.equals(newAnimation)) return;
            currentAnimation.stop();
        }

        currentAnimation = newAnimation;
        currentAnimation.play();
    }
}