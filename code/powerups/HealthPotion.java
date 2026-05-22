package powerups;

import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import static com.raylib.Colors.WHITE; // Imports WHITE functionality
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import utils.RaylibRenderer; // Rendering utilities
import entities.Player; // Player character

/**
 * Health potion - restores 50 HP
 */
public class HealthPotion extends PowerUp {

    public HealthPotion(double x, double y) {
        super(x, y);
    }

    @Override
    public void apply(Player player) {
        int heal = 30; // health increases by 30
        player.heal(heal);
    }

    @Override
    public void render() {
        int px = (int) x;
        int py = (int) (y + floatOffset);

        // Glow
        renderGlow(new int[] { 255, 50, 50 });

        // Potion bottle
        fillRoundRect(px + 5, py + 10, 20, 18, 5, color(200, 50, 50, 255));

        // Bottle neck
        DrawRectangle(px + 10, py + 5, 10, 8, color(180, 40, 40, 255));

        // Cork
        DrawRectangle(px + 11, py + 2, 8, 5, color(139, 90, 43, 255));

        // Shine
        fillOval(px + 8, py + 12, 4, 6, color(255, 150, 150, 255));

        // Plus symbol
        DrawRectangle(px + 13, py + 14, 4, 10, WHITE);
        DrawRectangle(px + 10, py + 17, 10, 4, WHITE);
    }
}
