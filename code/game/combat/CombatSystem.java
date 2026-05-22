package game.combat;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import java.util.List; // List interface for collections
import entities.Player; // Player character
import entities.Enemy; // Base enemy class
import entities.enemies.ShieldBearer; // Defensive enemy
import entities.enemies.Boss; // Boss enemy
import audio.SoundManager; // Audio playback
import utils.Constants; // Global game constants
import game.state.GameSession; // Current game state data

import game.effects.ParticleManager; // Particle system controller
import game.entities.Crate; // Breakable objects

/*
 * COMBO SYSTEM MECHANIC:
 * Hits string together into combos if landed within the COMBO_TIMEOUT.
 * Passing certain combo thresholds (3, 5, 7) grants increasing damage multipliers.
 * Reaching the COMBO_FINISHER_THRESHOLD queues a massive finisher attack that resets the combo upon connecting.
 */
/**
 * Handles all melee combat between the player and enemies.
 * It checks if weapon hitboxes overlap with character hitboxes.
 * If they do, it applies damage, hitlag (a brief freeze for impact), screen shake, 
 * and increments the player's combo counter.
 */
public class CombatSystem {
    
    public void checkMeleeHit(Player player, List<Enemy> enemies, List<Crate> crates,
                              SoundManager soundManager, GameSession session,
                              ParticleManager particleManager, java.util.Random random) {
        if (player.isAttacking()) {
            int[] playerAttackBox = player.getAttackHitbox();
            boolean hitEnemy = false;
            
            // Break crates if the attack hitbox hits them
            for (int i = crates.size() - 1; i >= 0; i--) {
                Crate crate = crates.get(i);
                // intersects() checks if the player's attack rectangle overlaps the crate's rectangle
                if (intersects(playerAttackBox, crate.getHitbox())) {
                    crate.hp--;
                    if (crate.hp <= 0) {
                        particleManager.spawnCrateParticles(crate.x + crate.width/2, crate.y + crate.height/2, random);
                        crates.remove(i); // Remove the broken crate from the game
                    }
                }
            }
            
            for (Enemy enemy : enemies) {
                if (intersects(playerAttackBox, enemy.getHitbox())) {
                    // Check for ShieldBearer - can only damage from above when player is airborne
                    if (enemy instanceof ShieldBearer) {
                        ShieldBearer sb = (ShieldBearer) enemy;
                        
                        // Player must be airborne AND attacking from above
                        double playerBottomY = player.getPosition().y + player.getHeight();
                        boolean attackFromAbove = sb.isAttackFromAbove(playerBottomY, !player.isOnGround());
                        
                        if (!attackFromAbove) {
                            // Blocked by shield
                            sb.triggerClangEffect();
                            soundManager.playEffect("clang.wav");
                            continue;
                        }
                    }
                    
                    // Check for Boss invulnerability
                    if (enemy instanceof Boss && ((Boss)enemy).isInvulnerable()) {
                        continue;
                    }
                    
                    int damage = player.getCurrentAttackDamage();
                    
                    // Check for combo finisher
                    if (player.isComboFinisherReady()) {
                        // Trigger combo finisher
                        session.comboFinisherActive = true;
                        session.comboFinisherFlashTimer = Constants.COMBO_FINISHER_FLASH_FRAMES;
                        session.showFinisherText = true;
                        session.finisherTextStartTime = System.currentTimeMillis();
                        
                        // Apply a radial knockback explosion to all enemies on screen.
                        // Force = 200 / distance. This means closer enemies are pushed away much harder.
                        // We use Math.max(dist, 50) so if an enemy is standing exactly on the player (dist=0),
                        // we don't accidentally divide by zero and crash the game.
                        for (Enemy e : enemies) {
                            double dx = e.getCenterX() - player.getCenterX();
                            double dy = e.getCenterY() - player.getCenterY();
                            double dist = Math.sqrt(dx * dx + dy * dy);
                            if (dist > 0) {
                                double force = 200 / Math.max(dist, 50);
                                e.getPosition().x += (dx / dist) * force;
                            }
                        }
                        
                        player.onComboFinisher();
                        soundManager.playEffect("finisher.wav");
                    }
                    
                    enemy.takeDamage(damage);
                    player.onHitEnemy();
                    hitEnemy = true;
                    soundManager.playSound(SoundManager.SOUND_HIT_ENEMY);
                    
                    // Apply hitlag
                    player.applyHitlag(Constants.HITLAG_FRAMES);
                    enemy.applyHitlag(Constants.HITLAG_FRAMES);
                    
                    // Track first ShieldBearer kill
                    if (enemy instanceof ShieldBearer && enemy.isDead()) {
                        session.firstShieldBearerKilled = true;
                    }
                }
            }
        }
        
        // Check if any enemy attacks are hitting the player
        for (Enemy enemy : enemies) {
            // Only check if the enemy is actively swinging and the player isn't currently invincible
            if (enemy.isAttacking() && !player.isInvulnerable()) {
                int[] enemyAttackBox = enemy.getAttackHitbox();
                
                // If the enemy's weapon hitbox overlaps the player's body hitbox
                if (intersects(enemyAttackBox, player.getHitbox())) {
                    if (!player.isBlocking()) {
                        player.takeDamage(enemy.getAttackDamage());
                    } else {
                        player.onBlockHit();
                        // Apply block knockback to enemy
                        enemy.applyBlockKnockback(player.getCenterX());
                        soundManager.playEffect("block.wav");
                    }
                }
            }
        }
    }
}
