package game.combat;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import java.util.List; // List interface for collections
import entities.Player; // Player character
import entities.Enemy; // Base enemy class
import entities.enemies.Boss; // Boss enemy
import utils.Constants; // Global game constants

/**
 * Checks boss special attacks (shockwave) hitting the player.
 */
public class BossAttackSystem {
    
    public void checkBossSpecialAttacks(List<Enemy> enemies, Player player) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof Boss) {
                Boss boss = (Boss) enemy;
                
                // Check shockwave
                if (boss.isPerformingShockwave() && boss.checkShockwaveHit(player)) {
                    player.takeDamage(Constants.BOSS_SHOCKWAVE_DAMAGE);
                    // Knock player up
                    player.getVelocity().y = Constants.BOSS_SHOCKWAVE_KNOCKUP_FORCE;
                    player.setOnGround(false);
                }
            }
        }
    }
}
