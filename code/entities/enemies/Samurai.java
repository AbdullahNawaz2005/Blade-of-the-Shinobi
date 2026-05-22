package entities.enemies;
import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import entities.Enemy; 
import entities.Player; 
import utils.Constants; 
import utils.RaylibRenderer; 




public class Samurai extends Enemy {
    
    private boolean blocking;
    private long blockStartTime;
    private int blockDuration = Constants.SAMURAI_BLOCK_DURATION_MS;
    
    public Samurai(double x, double y) {
        super(x, y, Constants.SAMURAI_WIDTH, Constants.SAMURAI_HEIGHT);
        
        this.health = Constants.SAMURAI_HEALTH;
        this.maxHealth = Constants.SAMURAI_HEALTH;
        this.attackDamage = Constants.SAMURAI_DAMAGE;
        this.moveSpeed = Constants.SAMURAI_SPEED;
        this.detectionRange = Constants.SAMURAI_DETECTION_RANGE;
        this.attackRange = Constants.SAMURAI_ATTACK_RANGE;
        this.attackCooldownTime = Constants.SAMURAI_ATTACK_COOLDOWN_MS;
        this.attackDuration = Constants.SAMURAI_ATTACK_DURATION_MS;
        this.scoreValue = Constants.SAMURAI_SCORE;
        this.blocking = false;
        this.canJumpToPlatform = false;
    }
    
    @Override
    public void update(Object... args) {
        Player player = (Player) args[0];
        this.target = player;
        
        long currentTime = System.currentTimeMillis();
        
        
        if (hitFlashTimer > 0) {
            hitFlashTimer--;
        }
        
        
        if (knockbackTimer > 0) {
            knockbackTimer--;
            position.x += knockbackVelocityX * ((double)knockbackTimer / Constants.BLOCK_KNOCKBACK_FRAMES);
            applyBounds();
        }
        
        
        
        if (knockedBack) {
            if (currentTime - knockbackTime > 150) {
                knockedBack = false;
                velocity.set(0, 0);
            } else {
                
                position.x += velocity.x * 0.5;
                applyBounds();
                return;
            }
        }
        
        
        if (blocking) {
            if (currentTime - blockStartTime > blockDuration) {
                blocking = false;
            }
            return;
        }
        
        
        if (attacking) {
            if (currentTime - attackStartTime > attackDuration) {
                attacking = false;
                state = EnemyState.CHASING;
            }
            return;
        }
        
        double distanceToPlayer = position.distance(player.getPosition());
        
        
        
        if (player.isAttacking() && distanceToPlayer < 100 && Math.random() < Constants.SAMURAI_BLOCK_CHANCE) {
            blocking = true;
            blockStartTime = currentTime;
            return;
        }
        
        if (distanceToPlayer <= attackRange) {
            if (currentTime - lastAttackTime > attackCooldownTime) {
                startAttack();
            }
        } else if (distanceToPlayer <= detectionRange) {
            state = EnemyState.CHASING;
            chasePlayer();
        } else {
            state = EnemyState.IDLE;
        }
        
        applyBounds();
    }
    
    @Override
    protected void startAttack() {
        attacking = true;
        attackStartTime = System.currentTimeMillis();
        lastAttackTime = attackStartTime;
        state = EnemyState.ATTACKING;
        updateAttackHitbox(70, 60, 10);
    }
    
    @Override
    public void takeDamage(int damage) {
        
        
        if (blocking) {
            damage = (int)(damage * Constants.SAMURAI_BLOCK_DAMAGE_MULT);
        }
        super.takeDamage(damage);
    }
    
    @Override
    public void render() {
        int x = (int) position.x;
        int y = (int) position.y;
        
        
        int alpha = hitFlashTimer > 0 ? 178 : 255;
        
        
        Color armorColor = color(120, 40, 40, alpha);
        Color armorDark = color(80, 30, 30, alpha);
        
        if (blocking) {
            armorColor = color(100, 100, 120, alpha);
        } else if (attacking) {
            armorColor = color(150, 50, 50, alpha);
        }
        
        
        fillRoundRect(x + 8, y + 25, width - 16, height - 40, 8, armorColor);
        
        
        DrawRectangle(x + 2, y + 25, 12, 15, armorDark);
        DrawRectangle(x + width - 14, y + 25, 12, 15, armorDark);
        
        
        fillOval(x + 10, y, 30, 30, color(50, 50, 55, alpha));
        
        
        DrawRectangle(x + 22, y - 10, 6, 15, armorColor);
        
        
        DrawRectangle(x + 12, y + 15, 26, 10, color(30, 30, 35, alpha));
        
        
        Color eyeColor = color(255, 100, 100, alpha);
        if (facingRight) {
            DrawRectangle(x + 28, y + 10, 6, 3, eyeColor);
        } else {
            DrawRectangle(x + 16, y + 10, 6, 3, eyeColor);
        }
        
        
        Color swordColor = color(200, 200, 210, alpha);
        if (attacking) {
            
            if (facingRight) {
                DrawRectangle(x + width, y + 15, 50, 4, swordColor);
                int[] tipX = {x + width + 50, x + width + 58, x + width + 50};
                int[] tipY = {y + 13, y + 17, y + 21};
                fillPolygon(tipX, tipY, 3, swordColor);
            } else {
                DrawRectangle(x - 50, y + 15, 50, 4, swordColor);
                int[] tipX = {x - 50, x - 58, x - 50};
                int[] tipY = {y + 13, y + 17, y + 21};
                fillPolygon(tipX, tipY, 3, swordColor);
            }
        } else if (blocking) {
            
            DrawRectangle(x + 15, y + 5, 4, 40, swordColor);
        } else {
            
            if (facingRight) {
                DrawRectangle(x + width - 5, y + 20, 4, 35, swordColor);
            } else {
                DrawRectangle(x + 1, y + 20, 4, 35, swordColor);
            }
        }
        
        
        Color legColor = color(40, 40, 50, alpha);
        DrawRectangle(x + 12, y + height - 20, 10, 20, legColor);
        DrawRectangle(x + 28, y + height - 20, 10, 20, legColor);
        
        
        if (hitFlashTimer > 0) {
            fillRoundRect(x + 8, y + 25, width - 16, height - 40, 8, color(255, 255, 255, 150));
            fillOval(x + 10, y, 30, 30, color(255, 255, 255, 150));
        }
        
        
        renderHealthBar();
        
        
        if (attacking) {
            DrawRectangle(attackHitbox[0], attackHitbox[1], attackHitbox[2], attackHitbox[3], color(255, 0, 0, 50));
        }
    }
}
