package game.entities;
import static utils.RaylibRenderer.*; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Rectangle; 
import utils.Constants; 




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
        
        DrawRectangle((int)x, (int)y, width, height, color(139, 90, 43, 255));
        
        
        DrawLine((int)x, (int)y, (int)x + width, (int)y + height, color(100, 60, 30, 255));
        DrawLine((int)x + width, (int)y, (int)x, (int)y + height, color(100, 60, 30, 255));
        
        
        DrawRectangleLines((int)x, (int)y, width, height, color(80, 50, 25, 255));
    }
}
