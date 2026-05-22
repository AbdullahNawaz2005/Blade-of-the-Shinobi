package powerups;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import utils.RaylibRenderer; // Rendering utilities
import entities.Player; // Player character

/**
 * Shield - blocks next few hits
 */
public class Shield extends PowerUp {
    
    public Shield(double x, double y) {
        super(x, y);
    }
    
    @Override
    public void apply(Player player) {
        player.applyShield(2); // Block 2 hits (nerfed from 3)
    }
    
    @Override
    public void render() {
        int px = (int) x;
        int py = (int) (y + floatOffset);
        
        // Glow
        renderGlow(new int[]{100, 200, 255});
        
        // Shield shape
        int[] shieldX = {px + 15, px + 5, px + 5, px + 15, px + 25, px + 25};
        int[] shieldY = {py + 2, py + 8, py + 20, py + 28, py + 20, py + 8};
        fillPolygon(shieldX, shieldY, 6, color(80, 150, 200, 255));
        
        // Shield border
        drawPolygon(shieldX, shieldY, 6, color(150, 200, 230, 255));
        
        // Shield emblem (star)
        fillOval(px + 11, py + 12, 8, 8, color(200, 230, 255, 255));
        
        // Shine
        fillOval(px + 7, py + 8, 4, 6, color(255, 255, 255, 150));
    }
}
