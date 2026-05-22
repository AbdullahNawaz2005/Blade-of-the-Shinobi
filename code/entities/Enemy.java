package entities;

import static utils.RaylibRenderer.*; 

import static com.raylib.Colors.DARKGRAY; 
import static com.raylib.Colors.RED; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import utils.Constants; 
import utils.Vector2D; 





public abstract class Enemy extends Entity {

    
    protected EnemyState state;
    protected Player target;

    
    protected double moveSpeed;
    protected double detectionRange;
    protected double attackRange;
    protected double velocityY = 0;
    protected boolean isGrounded = true;

    
    protected long attackCooldownTime;
    protected int attackDuration;
    protected long attackStartTime;
    protected int attackTimer;

    
    protected int scoreValue;

    
    protected boolean knockedBack;
    protected long knockbackTime;

    
    protected int hitFlashTimer = 0;

    
    protected int knockbackTimer = 0;
    protected double knockbackVelocityX = 0;

    
    protected int whiteFlashTimer = 0;

    
    protected int hitlagFrames = 0;

    
    protected boolean canJumpToPlatform = false;

    public enum EnemyState {
        IDLE, PATROLLING, CHASING, ATTACKING, HURT, DEAD
    }

    public Enemy(double x, double y, int width, int height) {
        super(x, y, width, height);
        this.state = EnemyState.IDLE;
        this.moveSpeed = 2.0;
        this.detectionRange = 300;
        this.attackRange = 60;
        this.attackCooldownTime = 1000;
        this.attackCooldown = 1000; 
        this.attackDuration = 300;
        this.scoreValue = 100;
        this.knockedBack = false;
    }

    public void update(Player player, double deltaTime) {
        
        update((Object) player);
    }

    @Override
    public void update(Object... args) {
        Player player = (Player) args[0];
        this.target = player;

        long currentTime = System.currentTimeMillis();

        
        if (hitlagFrames > 0) {
            hitlagFrames--;
            return;
        }

        
        if (hitFlashTimer > 0) {
            hitFlashTimer--;
        }

        
        if (whiteFlashTimer > 0) {
            whiteFlashTimer--;
        }

        
        
        if (knockbackTimer > 0) {
            knockbackTimer--;
            position.x += knockbackVelocityX * ((double) knockbackTimer / Constants.BLOCK_KNOCKBACK_FRAMES);
            applyBounds();
            return; 
        }

        
        if (knockedBack) {
            if (currentTime - knockbackTime > Constants.ENEMY_KNOCKBACK_DURATION_MS) {
                knockedBack = false;
                velocity.set(0, 0); 
            } else {
                position.addTo(velocity); 
                applyBounds();
                return; 
            }
        }

        
        if (attacking) {
            if (currentTime - attackStartTime > attackDuration) {
                attacking = false;
                state = EnemyState.CHASING;
            }
            return;
        }

        
        
        double distanceToPlayer = position.distance(player.getPosition());

        if (distanceToPlayer <= attackRange) {
            
            if (currentTime - lastAttackTime > attackCooldownTime) {
                startAttack();
            } else {
                state = EnemyState.IDLE; 
            }
        } else if (distanceToPlayer <= detectionRange) {
            
            state = EnemyState.CHASING;
            chasePlayer();
        } else {
            
            state = EnemyState.IDLE;
        }

        applyBounds();
    }

    protected void chasePlayer() {
        if (target == null)
            return;

        
        Vector2D direction = target.getPosition().subtract(position).normalize();
        
        
        velocity.x = direction.x * moveSpeed;

        position.addTo(velocity);

        
        facingRight = target.getPosition().x > position.x;
    }

    protected void startAttack() {
        attacking = true;
        attackStartTime = System.currentTimeMillis();
        lastAttackTime = attackStartTime;
        state = EnemyState.ATTACKING;

        updateAttackHitbox(50, 40, 5);
    }

    protected void applyBounds() {
        if (position.x < 0)
            position.x = 0;
        if (position.x > Constants.WINDOW_WIDTH - width) {
            position.x = Constants.WINDOW_WIDTH - width;
        }
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        
        hitFlashTimer = Constants.HIT_FLASH_FRAMES;

        if (!dead) {
            
            knockedBack = true;
            knockbackTime = System.currentTimeMillis();

            
            
            if (target != null) {
                double dir = position.x > target.getPosition().x ? 1 : -1;
                velocity.set(dir * Constants.ENEMY_KNOCKBACK_FORCE, 0);
            }

            
            attacking = false;
            state = EnemyState.HURT;
        }
    }

    




    public void applyBlockKnockback(double playerX) {
        double dir = position.x > playerX ? 1 : -1;
        knockbackVelocityX = dir * (Constants.BLOCK_KNOCKBACK_DISTANCE / Constants.BLOCK_KNOCKBACK_FRAMES);
        knockbackTimer = Constants.BLOCK_KNOCKBACK_FRAMES;
        
        whiteFlashTimer = 2;
    }

    


    public void applyHitlag(int frames) {
        hitlagFrames = frames;
    }

    public int getHitlagFrames() {
        return hitlagFrames;
    }

    @Override
    public void render() {
        
        renderBase(RED);
    }

    protected void renderBase(Color color) {
        int x = (int) position.x;
        int y = (int) position.y;

        
        int alpha = hitFlashTimer > 0 ? 178 : 255; 
        Color flashedColor = color(color.r(), color.g(), color.b(), alpha);

        
        fillRoundRect(x + 5, y + 15, width - 10, height - 25, 8, flashedColor);

        
        fillOval(x + 8, y, 24, 24, flashedColor);

        
        Color eyeColor = color(0, 0, 0, alpha);
        if (facingRight) {
            fillOval(x + 20, y + 8, 6, 4, eyeColor);
        } else {
            fillOval(x + 14, y + 8, 6, 4, eyeColor);
        }

        
        if (hitFlashTimer > 0 || whiteFlashTimer > 0) {
            Color whiteFlash = color(255, 255, 255, 150);
            fillRoundRect(x + 5, y + 15, width - 10, height - 25, 8, whiteFlash);
            fillOval(x + 8, y, 24, 24, whiteFlash);
        }

        
        renderHealthBar();
    }

    protected void renderHealthBar() {
        int x = (int) position.x;
        int y = (int) position.y;

        int barWidth = width;
        int barHeight = 5;
        int barX = x;
        int barY = y - 10;

        
        DrawRectangle(barX, barY, barWidth, barHeight, DARKGRAY);

        
        double healthPercent = (double) health / maxHealth;
        DrawRectangle(barX, barY, (int) (barWidth * healthPercent), barHeight, RED);
    }

    
    public int getScoreValue() {
        return scoreValue;
    }

    public EnemyState getState() {
        return state;
    }
}
