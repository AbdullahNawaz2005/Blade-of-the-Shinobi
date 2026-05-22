package powerups;

import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import entities.Player; 
import utils.Constants; 
import utils.RaylibRenderer; 





public abstract class PowerUp {

    protected double x, y;
    protected int width = 30;
    protected int height = 30;
    protected boolean collected;
    protected long spawnTime;
    protected int lifetime = 10000; 

    
    protected double floatOffset;

    public PowerUp(double x, double y) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.spawnTime = System.currentTimeMillis();
        this.floatOffset = 0;
    }

    public void update() {
        
        floatOffset = Math.sin(System.currentTimeMillis() * 0.005) * 5;
    }

    public abstract void apply(Player player);

    public abstract void render();

    public void applyTo(Player player) {
        if (!collected) {
            apply(player);
            collected = true;
        }
    }

    public Rectangle getHitbox() {
        return rect((float) x, (float) y, width, height);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > lifetime;
    }

    protected void renderGlow(int[] colorRGB) {
        
        fillOval((int) x - 5, (int) (y + floatOffset) - 5, width + 10, height + 10,
                color(colorRGB[0], colorRGB[1], colorRGB[2], 50));
    }
}
