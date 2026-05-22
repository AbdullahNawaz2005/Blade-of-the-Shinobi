package game.effects;
import java.util.ArrayList; // For dynamic arrays
import java.util.List; // List interface for collections
import java.util.Random; // Random number generator
import utils.Constants; // Global game constants

/**
 * Manages particle lifecycle: creation, update, and rendering.
 */
public class ParticleManager {
    private List<Particle> particles = new ArrayList<>();
    
    public void update() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (p.isDead()) {
                particles.remove(i);
            }
        }
    }
    
    public void render() {
        for (Particle p : particles) {
            p.render();
        }
    }
    
    public void spawnDeathParticles(double x, double y, Random random) {
        int[][] colors = {{255, 0, 0}, {200, 50, 50}, {150, 30, 30}, {255, 165, 0}};
        for (int i = 0; i < Constants.DEATH_PARTICLE_COUNT; i++) {
            double vx = (random.nextDouble() - 0.5) * 8;
            double vy = -random.nextDouble() * 6 - 2;
            int size = Constants.DEATH_PARTICLE_MIN_SIZE + 
                       random.nextInt(Constants.DEATH_PARTICLE_MAX_SIZE - Constants.DEATH_PARTICLE_MIN_SIZE);
            int[] color = colors[random.nextInt(colors.length)];
            particles.add(new Particle(x, y, vx, vy, size, color, Constants.DEATH_PARTICLE_DURATION_FRAMES));
        }
    }
    
    public void spawnCrateParticles(double x, double y, Random random) {
        for (int i = 0; i < Constants.CRATE_PARTICLE_COUNT; i++) {
            double vx = (random.nextDouble() - 0.5) * 6;
            double vy = -random.nextDouble() * 5 - 2;
            int size = 3 + random.nextInt(4);
            int[] color = {139 - random.nextInt(30), 90 - random.nextInt(20), 43};
            particles.add(new Particle(x, y, vx, vy, size, color, 20));
        }
    }
    
    public void clear() {
        particles.clear();
    }
}
