package game.spawn;
import java.util.List; // List interface for collections
import java.util.Random; // Random number generator
import powerups.*; // Imports import powerups functionality

/**
 * Handles power-up spawning from enemy drops.
 */
public class PowerUpSpawner {
    
    public void spawnPowerUp(double x, double y, List<PowerUp> powerUps, Random random) {
        PowerUp powerUp;
        double roll = random.nextDouble();
        
        if (roll < 0.35) {
            powerUp = new HealthPotion(x, y);
        } else if (roll < 0.55) {
            powerUp = new SpeedBoost(x, y);
        } else if (roll < 0.75) {
            powerUp = new DamageBoost(x, y);
        } else {
            powerUp = new Shield(x, y);
        }
        
        powerUps.add(powerUp);
    }
}
