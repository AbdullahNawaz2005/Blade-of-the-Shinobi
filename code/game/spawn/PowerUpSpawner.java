package game.spawn;
import java.util.List; 
import java.util.Random; 
import powerups.*; 




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
