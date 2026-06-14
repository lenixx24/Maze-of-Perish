package ua.edu.ukma.entity.enemy;

import ua.edu.ukma.resource.GoldManager;
import ua.edu.ukma.ui.LevelInfo;
public class WaveManager {
    private Runnable onVictory;
    private final EnemyManager enemyManager;
    private int currentWave = 0;
    public final int MAX_WAVES;
    private final double PREP_TIME_SECONDS;

    private boolean isPreparationPhase = true;
    private double prepTimer = 0;
    private int enemiesToSpawnThisWave = 0;
    private int enemiesSpawnedSoFar = 0;
    private double spawnIntervalTimer = 0;
    private final double SPAWN_INTERVAL_SECONDS;
    private final int START_ENEMIES_AMOUNT;
    private boolean hasStarted=false;
    private final GoldManager goldManager;
    public WaveManager(EnemyManager enemyManager, LevelInfo levelInfo, GoldManager goldManager) {
        this.enemyManager = enemyManager;
        MAX_WAVES = levelInfo.number()+4;
        PREP_TIME_SECONDS = levelInfo.number()*5+10;
        SPAWN_INTERVAL_SECONDS = levelInfo.number();
        START_ENEMIES_AMOUNT = levelInfo.number()+1;
        this.goldManager=goldManager;
    }

    public void update(double deltaTime) {
        if(!hasStarted){
            startPreparationPhase();
            hasStarted=true;
        }
        if (isPreparationPhase) {
            prepTimer -= deltaTime;
            if (prepTimer <= 0) startWave();

        } else {
            if (enemiesSpawnedSoFar < enemiesToSpawnThisWave) {
                spawnIntervalTimer -= deltaTime;

                if (spawnIntervalTimer <= 0) {
                    enemyManager.spawnEnemy();
                    enemiesSpawnedSoFar++;
                    spawnIntervalTimer = SPAWN_INTERVAL_SECONDS;
                }
            } else if (enemyManager.getEnemies().isEmpty()) startPreparationPhase();

        }
    }
    private void startPreparationPhase() {
        currentWave++;
        if (currentWave > MAX_WAVES) {
            System.out.println("Win");
            currentWave=MAX_WAVES;
            if (onVictory != null)
                onVictory.run();
            return;
        }

        isPreparationPhase = true;
        prepTimer = PREP_TIME_SECONDS;
        enemiesToSpawnThisWave = START_ENEMIES_AMOUNT + (currentWave * 2);
        enemiesSpawnedSoFar = 0;
        spawnIntervalTimer = 0;
    }

    public void startWave() {
        if (!isPreparationPhase) return;
        isPreparationPhase = false;
    }
    public void startWaveEarly() {
        if (isPreparationPhase) {
            startWave();
            int bonusGold = (int) (prepTimer * 2);
            goldManager.addGold(bonusGold);
        }
    }

    public int getCurrentWave() { return currentWave; }
    public double getPrepTimer() { return Math.max(0, prepTimer); }
    public boolean isPreparationPhase() { return isPreparationPhase; }
    public void setOnVictory(Runnable onVictory) {
        this.onVictory = onVictory;
    }
}
