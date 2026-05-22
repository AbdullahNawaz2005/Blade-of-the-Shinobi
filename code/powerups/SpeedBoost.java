package powerups;

import static utils.RaylibRenderer.*; 

import static com.raylib.Colors.WHITE; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import utils.RaylibRenderer; 
import entities.Player; 




public class SpeedBoost extends PowerUp {

    public SpeedBoost(double x, double y) {
        super(x, y);
    }

    @Override
    public void apply(Player player) {
        player.applySpeedBoost(5000); 
    }

    @Override
    public void render() {
        int px = (int) x;
        int py = (int) (y + floatOffset);

        
        renderGlow(new int[] { 50, 150, 255 });

        
        int[] boltX = { px + 18, px + 10, px + 14, px + 8, px + 16, px + 12, px + 22 };
        int[] boltY = { py + 2, py + 12, py + 12, py + 28, py + 16, py + 16, py + 2 };
        fillPolygon(boltX, boltY, 7, color(50, 150, 255, 255));

        
        drawPolygon(boltX, boltY, 7, color(100, 200, 255, 255));

        
        fillOval(px + 13, py + 8, 3, 3, WHITE);
    }
}
