package game.spawn;
import java.util.List; 
import java.util.Random; 
import entities.Enemy; 
import entities.enemies.*; 
import audio.SoundManager; 
import utils.Constants; 
import utils.Difficulty; 
import game.state.GameSession; 





public class EnemySpawner {
    
    




    public void spawnWave(List<Enemy> enemies, GameSession session, Random random, SoundManager soundManager) {
        session.wave++;
        
        
        int numEnemies = (int)((session.enemiesPerWave + (session.wave - 1)) * session.difficulty.spawnRateMult);
        
        
        if (session.wave % Constants.BOSS_WAVE_INTERVAL == 0) {
            session.bossCount++;
            soundManager.resumeBackgroundMusic();
            double bossX = random.nextBoolean() ? -50 : Constants.WINDOW_WIDTH + 50;
            Boss boss = new Boss(bossX, Constants.WINDOW_HEIGHT - 150, session.bossCount);
            
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
        
        
        
        
        
        if (session.wave >= Constants.BOMBER_SPAWN_WAVE && roll < 0.15) {
            enemy = new Bomber(x, y);
        }
        
        else if (session.wave >= Constants.SHIELDBEARER_SPAWN_WAVE && roll < 0.25) {
            enemy = new ShieldBearer(x, y);
        }
        
        else if (session.wave >= 3 && roll < 0.40) {
            enemy = new Samurai(x, y);
        }
        
        else if (session.wave >= 2 && roll < 0.55) {
            Archer archer = new Archer(x, y);
            archer.setCurrentWave(session.wave);
            enemy = archer;
        }
        
        else {
            enemy = new Grunt(x, y);
        }
        
        
        int scaledHealth = (int)(enemy.getHealth() * session.difficulty.enemyHealthMult);
        enemy.setHealth(scaledHealth);
        enemy.setMaxHealth(scaledHealth);
        
        enemies.add(enemy);
    }
}
