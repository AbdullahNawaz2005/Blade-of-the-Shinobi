package game.combat;
import static utils.RaylibRenderer.*; 
import java.util.List; 
import entities.Player; 
import entities.Enemy; 
import entities.enemies.ShieldBearer; 
import entities.enemies.Boss; 
import audio.SoundManager; 
import utils.Constants; 
import game.state.GameSession; 

import game.effects.ParticleManager; 
import game.entities.Crate; 













public class CombatSystem {
    
    public void checkMeleeHit(Player player, List<Enemy> enemies, List<Crate> crates,
                              SoundManager soundManager, GameSession session,
                              ParticleManager particleManager, java.util.Random random) {
        if (player.isAttacking()) {
            int[] playerAttackBox = player.getAttackHitbox();
            boolean hitEnemy = false;
            
            
            for (int i = crates.size() - 1; i >= 0; i--) {
                Crate crate = crates.get(i);
                
                if (intersects(playerAttackBox, crate.getHitbox())) {
                    crate.hp--;
                    if (crate.hp <= 0) {
                        particleManager.spawnCrateParticles(crate.x + crate.width/2, crate.y + crate.height/2, random);
                        crates.remove(i); 
                    }
                }
            }
            
            for (Enemy enemy : enemies) {
                if (intersects(playerAttackBox, enemy.getHitbox())) {
                    
                    if (enemy instanceof ShieldBearer) {
                        ShieldBearer sb = (ShieldBearer) enemy;
                        
                        
                        double playerBottomY = player.getPosition().y + player.getHeight();
                        boolean attackFromAbove = sb.isAttackFromAbove(playerBottomY, !player.isOnGround());
                        
                        if (!attackFromAbove) {
                            
                            sb.triggerClangEffect();
                            soundManager.playEffect("clang.wav");
                            continue;
                        }
                    }
                    
                    
                    if (enemy instanceof Boss && ((Boss)enemy).isInvulnerable()) {
                        continue;
                    }
                    
                    int damage = player.getCurrentAttackDamage();
                    
                    
                    if (player.isComboFinisherReady()) {
                        
                        session.comboFinisherActive = true;
                        session.comboFinisherFlashTimer = Constants.COMBO_FINISHER_FLASH_FRAMES;
                        session.showFinisherText = true;
                        session.finisherTextStartTime = System.currentTimeMillis();
                        
                        
                        
                        
                        
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
                    
                    
                    player.applyHitlag(Constants.HITLAG_FRAMES);
                    enemy.applyHitlag(Constants.HITLAG_FRAMES);
                    
                    
                    if (enemy instanceof ShieldBearer && enemy.isDead()) {
                        session.firstShieldBearerKilled = true;
                    }
                }
            }
        }
        
        
        for (Enemy enemy : enemies) {
            
            if (enemy.isAttacking() && !player.isInvulnerable()) {
                int[] enemyAttackBox = enemy.getAttackHitbox();
                
                
                if (intersects(enemyAttackBox, player.getHitbox())) {
                    if (!player.isBlocking()) {
                        player.takeDamage(enemy.getAttackDamage());
                    } else {
                        player.onBlockHit();
                        
                        enemy.applyBlockKnockback(player.getCenterX());
                        soundManager.playEffect("block.wav");
                    }
                }
            }
        }
    }
}
