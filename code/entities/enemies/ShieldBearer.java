package entities.enemies;
import static utils.RaylibRenderer.*; 

import entities.Enemy; 
import entities.Player; 
import static com.raylib.Colors.WHITE; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import java.util.Random; 
import utils.Constants; 
import utils.RaylibRenderer; 









public class ShieldBearer extends Enemy {
    
    private long lastClangTime = 0;
    private static final int CLANG_COOLDOWN_MS = 300;
    
    
    private boolean showSparks = false;
    private int sparkTimer = 0;
    private static final int SPARK_DURATION = 8;
    private double[] sparkX;
    private double[] sparkY;
    private double[] sparkVx;
    private double[] sparkVy;
    private static final int SPARK_COUNT = 6;
    
    
    private boolean isAttacking = false;
    private int attackTimer = 0;
    
    
    private int hintArrowBob = 0;
    private boolean hintArrowUp = true;
    
    public ShieldBearer(double x, double y) {
        super(x, y, Constants.SHIELDBEARER_WIDTH, Constants.SHIELDBEARER_HEIGHT);
        this.health = Constants.SHIELDBEARER_HEALTH;
        this.maxHealth = Constants.SHIELDBEARER_HEALTH;
        this.attackDamage = Constants.SHIELDBEARER_DAMAGE;
        this.moveSpeed = Constants.SHIELDBEARER_SPEED;
        this.detectionRange = Constants.SHIELDBEARER_DETECTION_RANGE;
        this.attackRange = Constants.SHIELDBEARER_ATTACK_RANGE;
        this.attackCooldown = Constants.SHIELDBEARER_ATTACK_COOLDOWN_MS;
        this.attackCooldownTime = Constants.SHIELDBEARER_ATTACK_COOLDOWN_MS;
        this.scoreValue = Constants.SHIELDBEARER_SCORE;
        
        sparkX = new double[SPARK_COUNT];
        sparkY = new double[SPARK_COUNT];
        sparkVx = new double[SPARK_COUNT];
        sparkVy = new double[SPARK_COUNT];
    }
    
    @Override
    public void update(Object... args) {
        Player player = (Player) args[0];
        this.target = player;
        
        
        if (hintArrowUp) {
            hintArrowBob++;
            if (hintArrowBob >= 8) hintArrowUp = false;
        } else {
            hintArrowBob--;
            if (hintArrowBob <= 0) hintArrowUp = true;
        }
        
        
        if (showSparks) {
            sparkTimer++;
            for (int i = 0; i < SPARK_COUNT; i++) {
                sparkX[i] += sparkVx[i];
                sparkY[i] += sparkVy[i];
            }
            if (sparkTimer >= SPARK_DURATION) {
                showSparks = false;
                sparkTimer = 0;
            }
        }
        
        
        facingRight = player.getCenterX() > getCenterX();
        
        
        if (hitFlashTimer > 0) {
            hitFlashTimer--;
        }
        
        
        if (knockbackTimer > 0) {
            knockbackTimer--;
            position.x += knockbackVelocityX * ((double)knockbackTimer / Constants.BLOCK_KNOCKBACK_FRAMES);
            applyBounds();
            return;
        }
        
        
        if (knockedBack) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - knockbackTime > Constants.ENEMY_KNOCKBACK_DURATION_MS) {
                knockedBack = false;
                velocity.set(0, 0);
            } else {
                position.addTo(velocity);
                applyBounds();
                return;
            }
        }
        
        
        double dx = player.getCenterX() - getCenterX();
        double dy = player.getCenterY() - getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        
        if (isAttacking) {
            attackTimer--;
            if (attackTimer <= 0) {
                isAttacking = false;
                attacking = false;
            }
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (distance < detectionRange) {
            if (distance > attackRange) {
                
                double dirX = dx / distance;
                position.x += dirX * moveSpeed;
            } else if (currentTime - lastAttackTime > attackCooldownTime) {
                
                isAttacking = true;
                attacking = true;
                attackTimer = 18; 
                lastAttackTime = currentTime;
                attackStartTime = currentTime;
                updateAttackHitbox(50, 40, 5);
            }
        }
        
        applyBounds();
    }
    
    @Override
    public int getAttackDamage() {
        return Constants.SHIELDBEARER_DAMAGE;
    }
    
    @Override
    public void takeDamage(int damage) {
        
        health -= damage;
        if (health <= 0) {
            health = 0;
            dead = true;
            alive = false;
        }
        hitFlashTimer = Constants.HIT_FLASH_FRAMES;
    }
    
    







    public boolean isAttackFromAbove(double attackerBottomY, boolean isAttackerAirborne) {
        if (!isAttackerAirborne) {
            return false; 
        }
        
        return attackerBottomY < (position.y + 15);
    }
    
    






    public boolean isKunaiFromAbove(double kunaiY, double kunaiVy) {
        
        return kunaiVy > 0 && kunaiY < (position.y + 15);
    }
    
    


    public void triggerClangEffect() {
        long now = System.currentTimeMillis();
        if (now - lastClangTime > CLANG_COOLDOWN_MS) {
            showSparks = true;
            sparkTimer = 0;
            lastClangTime = now;
            
            
            Random rand = new Random();
            double shieldX = facingRight ? position.x + width : position.x;
            double shieldY = position.y + height / 2;
            
            for (int i = 0; i < SPARK_COUNT; i++) {
                sparkX[i] = shieldX;
                sparkY[i] = shieldY + rand.nextDouble() * 30 - 15;
                sparkVx[i] = (rand.nextDouble() - 0.5) * 8;
                sparkVy[i] = (rand.nextDouble() - 0.5) * 6;
            }
        }
    }
    
    


    public void render(boolean showHintArrow) {
        int x = (int) position.x;
        int y = (int) position.y;
        
        
        int alpha = hitFlashTimer > 0 ? 178 : 255;
        
        
        Color bodyColor = color(50, 55, 65, alpha);
        DrawRectangle(x, y, width, height, bodyColor);
        
        
        Color armorColor = color(70, 75, 85, alpha);
        DrawRectangle(x + 5, y + 15, width - 10, 15, armorColor);  
        DrawRectangle(x + 8, y + 40, width - 16, 20, armorColor);
        
        
        Color shieldColor = color(140, 145, 150, alpha);
        int shieldWidth = 15;
        int shieldHeight = 45;  
        int shieldX = facingRight ? x + width - 5 : x - 10;
        int shieldY = y + 18;  
        DrawRectangle(shieldX, shieldY, shieldWidth, shieldHeight, shieldColor);
        
        
        Color shieldDetailColor = color(100, 105, 110, alpha);
        DrawLine(shieldX + shieldWidth/2, shieldY + 5, shieldX + shieldWidth/2, shieldY + shieldHeight - 5, shieldDetailColor);
        DrawLine(shieldX + 3, shieldY + shieldHeight/2, shieldX + shieldWidth - 3, shieldY + shieldHeight/2, shieldDetailColor);
        
        
        Color shieldRimColor = color(160, 165, 170, alpha);
        DrawRectangleLines(shieldX, shieldY, shieldWidth, shieldHeight, shieldRimColor);
        
        
        Color fleshColor = color(210, 175, 140, alpha);  
        int headX = x + width/2 - 12;
        int headY = y - 8;
        int headW = 24;
        int headH = 22;
        fillOval(headX, headY, headW, headH, fleshColor);
        
        
        Color hairColor = color(40, 35, 30, alpha);
        fillArc(headX + 2, headY - 2, headW - 4, 12, 0, 180, hairColor);
        
        
        Color eyeColor = color(50, 50, 50, alpha);
        int eyeOffsetX = facingRight ? 6 : -2;
        fillOval(headX + headW/2 + eyeOffsetX - 3, headY + 8, 4, 3, eyeColor);
        fillOval(headX + headW/2 + eyeOffsetX + 3, headY + 8, 4, 3, eyeColor);
        
        
        DrawLine(headX + headW/2 + eyeOffsetX - 4, headY + 6, headX + headW/2 + eyeOffsetX, headY + 7, hairColor);
        DrawLine(headX + headW/2 + eyeOffsetX + 2, headY + 7, headX + headW/2 + eyeOffsetX + 6, headY + 6, hairColor);
        
        
        if (hitFlashTimer > 0 || whiteFlashTimer > 0) {
            Color flashColor = color(255, 255, 255, 150);
            DrawRectangle(x, y, width, height, flashColor);
        }
        
        
        if (showSparks) {
            Color sparkColor = color(255, 200, 50, 255);
            for (int i = 0; i < SPARK_COUNT; i++) {
                int size = 3 + (int)(Math.random() * 3);
                DrawRectangle((int)sparkX[i], (int)sparkY[i], size, size, sparkColor);
            }
            
            for (int i = 0; i < 3; i++) {
                DrawRectangle((int)sparkX[i], (int)sparkY[i], 2, 2, WHITE);
            }
        }
        
        
        if (showHintArrow) {
            int arrowX = x + width / 2;
            int arrowY = headY - 20 - hintArrowBob;
            
            
            Color glowColor = color(255, 215, 0, 80);
            fillOval(arrowX - 12, arrowY - 5, 24, 20, glowColor);
            
            
            Color arrowColor = color(255, 200, 0, 255);
            DrawLineEx(vec2(arrowX, arrowY), vec2(arrowX, arrowY + 10), 3, arrowColor);
            
            
            int[] arrowHeadX = {arrowX - 6, arrowX, arrowX + 6};
            int[] arrowHeadY = {arrowY + 10, arrowY + 18, arrowY + 10};
            fillPolygon(arrowHeadX, arrowHeadY, 3, arrowColor);
        }
        
        
        if (isAttacking) {
            Color attackColor = color(255, 100, 100, 150);
            int attackX = facingRight ? x + width : x - 30;
            DrawRectangle(attackX, y + 20, 30, 40, attackColor);
        }
        
        
        renderHealthBar();
    }
    
    @Override
    public void render() {
        
        render(true);
    }
}
