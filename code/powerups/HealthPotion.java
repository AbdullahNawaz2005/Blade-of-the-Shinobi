package powerups;

import static utils.RaylibRenderer.*; 

import static com.raylib.Colors.WHITE; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import utils.RaylibRenderer; 
import entities.Player; 




public class HealthPotion extends PowerUp {

    public HealthPotion(double x, double y) {
        super(x, y);
    }

    @Override
    public void apply(Player player) {
        int heal = 30; 
        player.heal(heal);
    }

    @Override
    public void render() {
        int px = (int) x;
        int py = (int) (y + floatOffset);

        
        renderGlow(new int[] { 255, 50, 50 });

        
        fillRoundRect(px + 5, py + 10, 20, 18, 5, color(200, 50, 50, 255));

        
        DrawRectangle(px + 10, py + 5, 10, 8, color(180, 40, 40, 255));

        
        DrawRectangle(px + 11, py + 2, 8, 5, color(139, 90, 43, 255));

        
        fillOval(px + 8, py + 12, 4, 6, color(255, 150, 150, 255));

        
        DrawRectangle(px + 13, py + 14, 4, 10, WHITE);
        DrawRectangle(px + 10, py + 17, 10, 4, WHITE);
    }
}
