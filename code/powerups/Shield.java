package powerups;
import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import utils.RaylibRenderer; 
import entities.Player; 




public class Shield extends PowerUp {
    
    public Shield(double x, double y) {
        super(x, y);
    }
    
    @Override
    public void apply(Player player) {
        player.applyShield(2); 
    }
    
    @Override
    public void render() {
        int px = (int) x;
        int py = (int) (y + floatOffset);
        
        
        renderGlow(new int[]{100, 200, 255});
        
        
        int[] shieldX = {px + 15, px + 5, px + 5, px + 15, px + 25, px + 25};
        int[] shieldY = {py + 2, py + 8, py + 20, py + 28, py + 20, py + 8};
        fillPolygon(shieldX, shieldY, 6, color(80, 150, 200, 255));
        
        
        drawPolygon(shieldX, shieldY, 6, color(150, 200, 230, 255));
        
        
        fillOval(px + 11, py + 12, 8, 8, color(200, 230, 255, 255));
        
        
        fillOval(px + 7, py + 8, 4, 6, color(255, 255, 255, 150));
    }
}
