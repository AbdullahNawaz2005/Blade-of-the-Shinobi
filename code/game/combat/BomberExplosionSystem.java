package game.combat;
import static utils.RaylibRenderer.*; 
import java.util.List; 
import entities.Player; 
import entities.Enemy; 
import entities.enemies.Bomber; 
import audio.SoundManager; 
import utils.Constants; 
import com.raylib.Raylib.Rectangle; 




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
