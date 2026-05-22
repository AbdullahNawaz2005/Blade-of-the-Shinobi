package game.combat;
import static utils.RaylibRenderer.*; 
import java.util.List; 
import entities.Player; 
import entities.Enemy; 
import entities.enemies.Boss; 
import utils.Constants; 




public class BossAttackSystem {
    
    public void checkBossSpecialAttacks(List<Enemy> enemies, Player player) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof Boss) {
                Boss boss = (Boss) enemy;
                
                
                if (boss.isPerformingShockwave() && boss.checkShockwaveHit(player)) {
                    player.takeDamage(Constants.BOSS_SHOCKWAVE_DAMAGE);
                    
                    player.getVelocity().y = Constants.BOSS_SHOCKWAVE_KNOCKUP_FORCE;
                    player.setOnGround(false);
                }
            }
        }
    }
}
