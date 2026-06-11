package ua.edu.ukma.entity.enemy;

public class WaveManager {

    private final EnemyManager enemyManager;
    private int currentWave = 0;
    public final int MAX_WAVES = 5;
    private final double PREP_TIME_SECONDS = 15.0;

    private boolean isPreparationPhase = true;
    private double prepTimer = 0;
    private int enemiesToSpawnThisWave = 0;
    private int enemiesSpawnedSoFar = 0;
    private double spawnIntervalTimer = 0;
    private final double SPAWN_INTERVAL_SECONDS = 2;
    private boolean hasStarted=false;
    public WaveManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
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
           if(currentWave==MAX_WAVES+1) System.out.println("Win!!");
           return;
        }

        isPreparationPhase = true;
        prepTimer = PREP_TIME_SECONDS;
        enemiesToSpawnThisWave = 2 + (currentWave * 2);
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
            System.out.println(bonusGold);
        }
    }

    public int getCurrentWave() { return currentWave; }
    public double getPrepTimer() { return Math.max(0, prepTimer); }
    public boolean isPreparationPhase() { return isPreparationPhase; }
}
