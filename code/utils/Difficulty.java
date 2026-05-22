package utils;








public enum Difficulty {
    EASY(0.7, 0.8, 0.45),
    NORMAL(1.0, 1.0, 0.30),
    HARD(1.4, 1.25, 0.20);
    
    public final double enemyHealthMult;
    public final double spawnRateMult;
    public final double dropChance;
    
    Difficulty(double enemyHealthMult, double spawnRateMult, double dropChance) {
        this.enemyHealthMult = enemyHealthMult;
        this.spawnRateMult = spawnRateMult;
        this.dropChance = dropChance;
    }
}
