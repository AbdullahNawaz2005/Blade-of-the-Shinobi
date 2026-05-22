package game.combat;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import java.util.List; // List interface for collections
import entities.Player; // Player character
import entities.Enemy; // Base enemy class
import entities.Projectile; // Thrown weapons
import entities.enemies.ShieldBearer; // Defensive enemy
import audio.SoundManager; // Audio playback
import utils.Constants; // Global game constants
import game.state.GameSession; // Current game state data

/*
 * PROJECTILE MECHANIC:
 * When the player presses Q, a Projectile entity is instantiated at their center, moving linearly in the direction they face.
 * Every frame, updateProjectiles moves each active projectile and checks intersection with the bounding box of every active enemy.
 * If an intersection occurs, damage is applied and the projectile is destroyed.
 */
/**
 * Handles projectile movement, collision detection, and kunai throwing.
 */
public class ProjectileSystem {
    
    /**
     * Instantiates a kunai projectile fired by the player.
     */
    public void throwKunai(Player player, List<Projectile> projectiles, SoundManager soundManager) {
        double px = player.getPosition().x + player.getWidth() / 2;
        double py = player.getPosition().y + player.getHeight() / 2;
        double dir = player.isFacingRight() ? 1 : -1;
        Projectile kunai = new Projectile(px, py, dir * Constants.KUNAI_SPEED, 0, Constants.KUNAI_DAMAGE);
        projectiles.add(kunai);
        soundManager.playEffect("kunai.wav");
    }
    
    /**
     * Updates all active projectiles (kunai, arrows), moving them and checking for collisions with entities or walls.
     */
    public void update(List<Projectile> projectiles, List<Enemy> enemies, Player player,
                       SoundManager soundManager, GameSession session) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update();
            
            if (p.isOffScreen()) {
                projectiles.remove(i);
                continue;
            }
            
            // Player kunai hitting enemies
            if (!p.isEnemyProjectile()) {
                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    
                    // ShieldBearer special handling - must hit from above
                    if (enemy instanceof ShieldBearer) {
                        ShieldBearer sb = (ShieldBearer) enemy;
                        if (intersects(p.getHitbox(), enemy.getHitbox())) {
                            // Kunai must be travelling downward and above ShieldBearer's head
                            if (sb.isKunaiFromAbove(p.getY(), p.getVelocityY())) {
                                enemy.takeDamage(p.getDamage());
                                player.onHitEnemy();
                                
                                // Track first ShieldBearer kill to hide hint arrow
                                if (enemy.isDead()) {
                                    session.firstShieldBearerKilled = true;
                                }
                            } else {
                                // Blocked by shield
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
                // Enemy projectile hitting player
                if (intersects(p.getHitbox(), player.getHitbox()) && !player.isInvulnerable()) {
                    if (!player.isBlocking()) {
                        player.takeDamage(p.getDamage());
                        // Apply burn from flaming arrows
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
