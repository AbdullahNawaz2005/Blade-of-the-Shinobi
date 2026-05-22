package powerups;

import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import static com.raylib.Colors.WHITE; // Imports WHITE functionality
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import utils.RaylibRenderer; // Rendering utilities
import entities.Player; // Player character

/**
 * Speed boost - increases movement speed temporarily
 */
public class SpeedBoost extends PowerUp {

    public SpeedBoost(double x, double y) {
        super(x, y);
    }

    @Override
    public void apply(Player player) {
        player.applySpeedBoost(5000); // 5 seconds
    }

    @Override
    public void render() {
        int px = (int) x;
        int py = (int) (y + floatOffset);

        // Glow
        renderGlow(new int[] { 50, 150, 255 });

        // Lightning bolt shape
        int[] boltX = { px + 18, px + 10, px + 14, px + 8, px + 16, px + 12, px + 22 };
        int[] boltY = { py + 2, py + 12, py + 12, py + 28, py + 16, py + 16, py + 2 };
        fillPolygon(boltX, boltY, 7, color(50, 150, 255, 255));

        // Outline
        drawPolygon(boltX, boltY, 7, color(100, 200, 255, 255));

        // Shine
        fillOval(px + 13, py + 8, 3, 3, WHITE);
    }
}
