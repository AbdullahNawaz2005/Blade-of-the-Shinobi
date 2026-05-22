package game.effects;
import static utils.RaylibRenderer.*; 
import utils.Constants; 




public class Particle {
    public double x, y, vx, vy;
    public int size;
    public int[] colorRGB; 
    public int life;
    public int maxLife;
    
    public Particle(double x, double y, double vx, double vy, int size, int[] colorRGB, int life) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.size = size;
        this.colorRGB = colorRGB;
        this.life = life;
        this.maxLife = life;
    }
    
    public void update() {
        x += vx;
        y += vy;
        vy += Constants.DEATH_PARTICLE_GRAVITY;
        life--;
    }
    
    public boolean isDead() {
        return life <= 0;
    }
    
    public void render() {
        int alpha = (int)(255 * ((double)life / maxLife));
        fillOval((int)x, (int)y, size, size,
            color(colorRGB[0], colorRGB[1], colorRGB[2], alpha));
    }
}
