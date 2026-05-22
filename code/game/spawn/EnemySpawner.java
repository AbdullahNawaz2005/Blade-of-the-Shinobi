package game.spawn;
import java.util.List; // List interface for collections
import java.util.Random; // Random number generator
import entities.Enemy; // Base enemy class
import entities.enemies.*; // Imports enemies functionality
import audio.SoundManager; // Audio playback
import utils.Constants; // Global game constants
import utils.Difficulty; // Difficulty settings
import game.state.GameSession; // Current game state data

/**
 * Handles wave spawning and random enemy creation.
 * Boss wave every 5 waves. Enemy spawn probabilities scale with wave number.
 */
public class EnemySpawner {
    
    /**
     * Spawn a new wave of enemies. Called when wave transition completes.
     * Enemy count scales with wave number and difficulty's spawnRateMult.
     * Every 5th wave is a boss wave; the boss counts against the normal enemy budget.
     */
    public void spawnWave(List<Enemy> enemies, GameSession session, Random random, SoundManager soundManager) {
        session.wave++;
        
        // Calculate number of enemies
        int numEnemies = (int)((session.enemiesPerWave + (session.wave - 1)) * session.difficulty.spawnRateMult);
        
        // Boss wave every 5 waves
        if (session.wave % Constants.BOSS_WAVE_INTERVAL == 0) {
            session.bossCount++;
            soundManager.resumeBackgroundMusic();
            double bossX = random.nextBoolean() ? -50 : Constants.WINDOW_WIDTH + 50;
            Boss boss = new Boss(bossX, Constants.WINDOW_HEIGHT - 150, session.bossCount);
            // Apply difficulty health mult
            int bossHealth = (int)(Constants.BOSS_HEALTH * session.difficulty.enemyHealthMult);
            boss.setHealth(bossHealth);
            boss.setMaxHealth(bossHealth);
            enemies.add(boss);
            numEnemies = Math.max(0, numEnemies - 3);
        }
        
        for (int i = 0; i < numEnemies; i++) {
            spawnRandomEnemy(enemies, session, random);
        }
    }
    
    private void spawnRandomEnemy(List<Enemy> enemies, GameSession session, Random random) {
        double x = random.nextBoolean() ? -30 : Constants.WINDOW_WIDTH + 30;
        double y = Constants.WINDOW_HEIGHT - 150;
        
        Enemy enemy;
        double roll = random.nextDouble();
        
        // Spawn probability is wave-gated and uses exclusive probability ranges on a single roll.
        // Early waves only have Grunts; as waves progress, stronger enemy types are added to the
        // probability table and assigned the lowest-probability brackets (forcing rarer appearances).
        // Bomber: 15% chance from wave 5+
        if (session.wave >= Constants.BOMBER_SPAWN_WAVE && roll < 0.15) {
            enemy = new Bomber(x, y);
        }
        // ShieldBearer: up to 25% from wave 3+
        else if (session.wave >= Constants.SHIELDBEARER_SPAWN_WAVE && roll < 0.25) {
            enemy = new ShieldBearer(x, y);
        }
        // Samurai: up to 40% from wave 3+
        else if (session.wave >= 3 && roll < 0.40) {
            enemy = new Samurai(x, y);
        }
        // Archer: up to 55% from wave 2+
        else if (session.wave >= 2 && roll < 0.55) {
            Archer archer = new Archer(x, y);
            archer.setCurrentWave(session.wave);
            enemy = archer;
        }
        // Default: Grunt (fills remaining probability)
        else {
            enemy = new Grunt(x, y);
        }
        
        // Apply difficulty health mult
        int scaledHealth = (int)(enemy.getHealth() * session.difficulty.enemyHealthMult);
        enemy.setHealth(scaledHealth);
        enemy.setMaxHealth(scaledHealth);
        
        enemies.add(enemy);
    }
}
