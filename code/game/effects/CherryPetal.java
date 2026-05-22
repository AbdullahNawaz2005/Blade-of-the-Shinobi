package game.effects;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import java.util.Random; // Random number generator

/**
 * Cherry Petal class for menu animation
 */
public class CherryPetal {
    public double x, y;
    public double vx, vy;
    public double oscillation;
    public double oscillationSpeed;
    public int size;
    
    public CherryPetal(Random random, int screenWidth, int screenHeight) {
        reset(random, screenWidth, screenHeight, true);
    }
    
    public final void reset(Random random, int screenWidth, int screenHeight, boolean fromTop) {
        if (fromTop) {
            x = random.nextDouble() * screenWidth;
            y = -10 - random.nextDouble() * 50;
        } else {
            x = random.nextDouble() * screenWidth;
            y = random.nextDouble() * screenHeight;
        }
        vx = 0.5 + random.nextDouble() * 1.5;
        vy = 1.0 + random.nextDouble() * 1.5;
        oscillation = random.nextDouble() * Math.PI * 2;
        oscillationSpeed = 0.02 + random.nextDouble() * 0.03;
        size = 6 + random.nextInt(4);
    }
    
    public void update(int screenWidth, int screenHeight, Random random) {
        oscillation += oscillationSpeed;
        // Add a sine-wave horizontal offset so the petal drifts left and right as it falls,
        // mimicking real cherry blossom petals caught in a gentle breeze.
        x += vx + Math.sin(oscillation) * 0.8;
        y += vy;
        
        // Reset when off screen
        if (y > screenHeight + 20 || x > screenWidth + 20) {
            reset(random, screenWidth, screenHeight, true);
        }
    }
    
    public void render() {
        // Pinkish white petal with slight transparency
        fillOval((int)x, (int)y, size, (int)(size * 0.6), 
            color(255, 240, 245, 180));
        // Lighter center
        fillOval((int)x + 1, (int)y + 1, size - 2, (int)(size * 0.6) - 2,
            color(255, 250, 252, 150));
    }
}
