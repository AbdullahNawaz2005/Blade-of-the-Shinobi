package game.combat;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import java.util.List; // List interface for collections
import entities.Player; // Player character
import entities.Enemy; // Base enemy class
import entities.enemies.Bomber; // Exploding enemy
import audio.SoundManager; // Audio playback
import utils.Constants; // Global game constants
import com.raylib.Raylib.Rectangle; // Raylib rectangle struct

/**
 * Checks bomber bomb explosions hitting the player.
 */
public class BomberExplosionSystem {
    
    public void checkBomberExplosions(List<Enemy> enemies, Player player,
                                      SoundManager soundManager) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof Bomber) {
                Bomber bomber = (Bomber) enemy;
                for (Bomber.Bomb bomb : bomber.getBombs()) {
                    Rectangle explosionBounds = bomb.getExplosionBounds();
                    if (explosionBounds != null && intersects(explosionBounds, player.getHitbox())) {
                        if (!player.isInvulnerable()) {
                            player.takeDamage(Constants.BOMB_DAMAGE);

                            soundManager.playEffect("explosion.wav");
                        }
                    }
                }
            }
        }
    }
}
