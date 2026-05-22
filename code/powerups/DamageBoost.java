package powerups;

import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import utils.RaylibRenderer; 
import entities.Player; 




public class DamageBoost extends PowerUp {

    public DamageBoost(double x, double y) {
        super(x, y);
    }

    @Override
    public void apply(Player player) {
        player.applyDamageBoost(6000); 
    }

    @Override
    public void render() {
        int px = (int) x;
        int py = (int) (y + floatOffset);

        
        renderGlow(new int[] { 255, 150, 50 });

        
        
        DrawRectangle(px + 13, py + 2, 4, 20, color(200, 200, 210, 255));
        
        int[] tipX = { px + 13, px + 15, px + 17 };
        int[] tipY = { py + 2, py - 3, py + 2 };
        fillPolygon(tipX, tipY, 3, color(200, 200, 210, 255));

        
        DrawRectangle(px + 8, py + 20, 14, 4, color(180, 140, 50, 255));

        
        DrawRectangle(px + 12, py + 23, 6, 8, color(80, 50, 30, 255));

        
        fillOval(px + 5, py + 5, 8, 12, color(255, 100, 0, 150));
        fillOval(px + 17, py + 5, 8, 12, color(255, 100, 0, 150));

        fillOval(px + 7, py + 8, 5, 8, color(255, 200, 0, 100));
        fillOval(px + 18, py + 8, 5, 8, color(255, 200, 0, 100));
    }
}
