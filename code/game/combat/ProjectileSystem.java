package game.combat;
import static utils.RaylibRenderer.*; 
import java.util.List; 
import entities.Player; 
import entities.Enemy; 
import entities.Projectile; 
import entities.enemies.ShieldBearer; 
import audio.SoundManager; 
import utils.Constants; 
import game.state.GameSession; 










public class ProjectileSystem {
    
    


    public void throwKunai(Player player, List<Projectile> projectiles, SoundManager soundManager) {
        double px = player.getPosition().x + player.getWidth() / 2;
        double py = player.getPosition().y + player.getHeight() / 2;
        double dir = player.isFacingRight() ? 1 : -1;
        Projectile kunai = new Projectile(px, py, dir * Constants.KUNAI_SPEED, 0, Constants.KUNAI_DAMAGE);
        projectiles.add(kunai);
        soundManager.playEffect("kunai.wav");
    }
    
    


    public void update(List<Projectile> projectiles, List<Enemy> enemies, Player player,
                       SoundManager soundManager, GameSession session) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update();
            
            if (p.isOffScreen()) {
                projectiles.remove(i);
                continue;
            }
            
            
            if (!p.isEnemyProjectile()) {
                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    
                    
                    if (enemy instanceof ShieldBearer) {
                        ShieldBearer sb = (ShieldBearer) enemy;
                        if (intersects(p.getHitbox(), enemy.getHitbox())) {
                            
                            if (sb.isKunaiFromAbove(p.getY(), p.getVelocityY())) {
                                enemy.takeDamage(p.getDamage());
                                player.onHitEnemy();
                                
                                
                                if (enemy.isDead()) {
                                    session.firstShieldBearerKilled = true;
                                }
                            } else {
                                
                                sb.triggerClangEffect();
                                soundManager.playEffect("clang.wav");
                            }
                            projectiles.remove(i);
                            break;
                        }
                    } else if (intersects(p.getHitbox(), enemy.getHitbox())) {
                        enemy.takeDamage(p.getDamage());
                        projectiles.remove(i);
                        player.onHitEnemy();
                        break;
                    }
                }
            } else {
                
                if (intersects(p.getHitbox(), player.getHitbox()) && !player.isInvulnerable()) {
                    if (!player.isBlocking()) {
                        player.takeDamage(p.getDamage());
                        
                        if (p.isFlaming()) {
                            player.applyBurn(Constants.BURN_DAMAGE_PER_TICK, Constants.BURN_DURATION_MS);
                        }
                    }
                    projectiles.remove(i);
                }
            }
        }
    }
}
