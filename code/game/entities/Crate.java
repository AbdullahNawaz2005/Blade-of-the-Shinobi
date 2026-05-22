package game.entities;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Rectangle; // Raylib rectangle struct
import utils.Constants; // Global game constants

/**
 * Destructible crate entity
 */
public class Crate {
    public double x, y;
    public int width = Constants.CRATE_WIDTH;
    public int height = Constants.CRATE_HEIGHT;
    public int hp = Constants.CRATE_HP;
    
    public Crate(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Rectangle getHitbox() {
        return rect((float)x, (float)y, width, height);
    }
    
    public void render() {
        // Brown box
        DrawRectangle((int)x, (int)y, width, height, color(139, 90, 43, 255));
        
        // Cross-hatch pattern
        DrawLine((int)x, (int)y, (int)x + width, (int)y + height, color(100, 60, 30, 255));
        DrawLine((int)x + width, (int)y, (int)x, (int)y + height, color(100, 60, 30, 255));
        
        // Border
        DrawRectangleLines((int)x, (int)y, width, height, color(80, 50, 25, 255));
    }
}
