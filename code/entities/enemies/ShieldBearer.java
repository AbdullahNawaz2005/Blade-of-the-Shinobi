package entities.enemies;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import entities.Enemy; // Base enemy class
import entities.Player; // Player character
import static com.raylib.Colors.WHITE; // Imports WHITE functionality
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import java.util.Random; // Random number generator
import utils.Constants; // Global game constants
import utils.RaylibRenderer; // Rendering utilities

/**
 * ShieldBearer enemy - can only be damaged from ABOVE (player must be airborne)
 * 
 * BUG FIX: Changed from "attack from behind" to "attack from above"
 * - Vulnerable only when player is airborne AND attacking from above
 * - Removed helmet from render, head is now exposed
 * - Added bobbing golden arrow hint above head
 */
public class ShieldBearer extends Enemy {
    
    private long lastClangTime = 0;
    private static final int CLANG_COOLDOWN_MS = 300;
    
    // Spark burst effect when shield blocks
    private boolean showSparks = false;
    private int sparkTimer = 0;
    private static final int SPARK_DURATION = 8;
    private double[] sparkX;
    private double[] sparkY;
    private double[] sparkVx;
    private double[] sparkVy;
    private static final int SPARK_COUNT = 6;
    
    // Internal attack state
    private boolean isAttacking = false;
    private int attackTimer = 0;
    
    // Hint arrow bob animation
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
        
        // Update hint arrow bob animation
        if (hintArrowUp) {
            hintArrowBob++;
            if (hintArrowBob >= 8) hintArrowUp = false;
        } else {
            hintArrowBob--;
            if (hintArrowBob <= 0) hintArrowUp = true;
        }
        
        // Update spark effect
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
        
        // Update facing direction to always face the player (shield faces threat)
        facingRight = player.getCenterX() > getCenterX();
        
        // Update hit flash
        if (hitFlashTimer > 0) {
            hitFlashTimer--;
        }
        
        // Update knockback
        if (knockbackTimer > 0) {
            knockbackTimer--;
            position.x += knockbackVelocityX * ((double)knockbackTimer / Constants.BLOCK_KNOCKBACK_FRAMES);
            applyBounds();
            return;
        }
        
        // Handle knockback from damage
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
        
        // Calculate distance to player
        double dx = player.getCenterX() - getCenterX();
        double dy = player.getCenterY() - getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Update attack timer
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
                // Move toward player
                double dirX = dx / distance;
                position.x += dirX * moveSpeed;
            } else if (currentTime - lastAttackTime > attackCooldownTime) {
                // Attack
                isAttacking = true;
                attacking = true;
                attackTimer = 18; // frames
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
        // ShieldBearer takes normal damage - the above/below check is done in GameEngine
        health -= damage;
        if (health <= 0) {
            health = 0;
            dead = true;
            alive = false;
        }
        hitFlashTimer = Constants.HIT_FLASH_FRAMES;
    }
    
    /**
     * Check if attack is from above (player/projectile is above the ShieldBearer's head)
     * The attacker must be airborne AND their bottom edge must be above the ShieldBearer's top edge
     * 
     * @param attackerBottomY The bottom Y position of the attacker (player.y + player.height or projectile.y)
     * @param isAttackerAirborne Whether the attacker is airborne (true for projectiles travelling down)
     * @return true if the attack is coming from above (vulnerable), false if blocked
     */
    public boolean isAttackFromAbove(double attackerBottomY, boolean isAttackerAirborne) {
        if (!isAttackerAirborne) {
            return false; // Ground attacks are always blocked
        }
        // Attack from above if attacker's bottom is above ShieldBearer's top + small margin
        return attackerBottomY < (position.y + 15);
    }
    
    /**
     * Check if a kunai projectile is attacking from above
     * 
     * @param kunaiY The Y position of the kunai
     * @param kunaiVy The vertical velocity of the kunai (positive = moving down)
     * @return true if kunai is attacking from above
     */
    public boolean isKunaiFromAbove(double kunaiY, double kunaiVy) {
        // Kunai must be travelling downward AND be above ShieldBearer's top
        return kunaiVy > 0 && kunaiY < (position.y + 15);
    }
    
    /**
     * Trigger the shield clang effect (blocked attack)
     */
    public void triggerClangEffect() {
        long now = System.currentTimeMillis();
        if (now - lastClangTime > CLANG_COOLDOWN_MS) {
            showSparks = true;
            sparkTimer = 0;
            lastClangTime = now;
            
            // Initialize spark positions at shield location
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
    
    /**
     * Render the ShieldBearer with exposed head (no helmet) and hint arrow
     */
    public void render(boolean showHintArrow) {
        int x = (int) position.x;
        int y = (int) position.y;
        
        // Calculate alpha for hit flash
        int alpha = hitFlashTimer > 0 ? 178 : 255;
        
        // Body - dark Colors.BLUE/Colors.GRAY armored look
        Color bodyColor = color(50, 55, 65, alpha);
        DrawRectangle(x, y, width, height, bodyColor);
        
        // Armor details (torso only - no shoulder pads that go up to head)
        Color armorColor = color(70, 75, 85, alpha);
        DrawRectangle(x + 5, y + 15, width - 10, 15, armorColor);  // Lower on body
        DrawRectangle(x + 8, y + 40, width - 16, 20, armorColor);
        
        // Shield - large steel rectangle (only covers torso, not head)
        Color shieldColor = color(140, 145, 150, alpha);
        int shieldWidth = 15;
        int shieldHeight = 45;  // Slightly shorter, doesn't protect head
        int shieldX = facingRight ? x + width - 5 : x - 10;
        int shieldY = y + 18;  // Starts below head level
        DrawRectangle(shieldX, shieldY, shieldWidth, shieldHeight, shieldColor);
        
        // Shield detail - cross pattern
        Color shieldDetailColor = color(100, 105, 110, alpha);
        DrawLine(shieldX + shieldWidth/2, shieldY + 5, shieldX + shieldWidth/2, shieldY + shieldHeight - 5, shieldDetailColor);
        DrawLine(shieldX + 3, shieldY + shieldHeight/2, shieldX + shieldWidth - 3, shieldY + shieldHeight/2, shieldDetailColor);
        
        // Shield rim
        Color shieldRimColor = color(160, 165, 170, alpha);
        DrawRectangleLines(shieldX, shieldY, shieldWidth, shieldHeight, shieldRimColor);
        
        // Head - EXPOSED flesh colored (no helmet!)
        Color fleshColor = color(210, 175, 140, alpha);  // Flesh tone
        int headX = x + width/2 - 12;
        int headY = y - 8;
        int headW = 24;
        int headH = 22;
        fillOval(headX, headY, headW, headH, fleshColor);
        
        // Hair (dark, short, not a helmet)
        Color hairColor = color(40, 35, 30, alpha);
        fillArc(headX + 2, headY - 2, headW - 4, 12, 0, 180, hairColor);
        
        // Eyes (menacing)
        Color eyeColor = color(50, 50, 50, alpha);
        int eyeOffsetX = facingRight ? 6 : -2;
        fillOval(headX + headW/2 + eyeOffsetX - 3, headY + 8, 4, 3, eyeColor);
        fillOval(headX + headW/2 + eyeOffsetX + 3, headY + 8, 4, 3, eyeColor);
        
        // Eyebrows (angry)
        DrawLine(headX + headW/2 + eyeOffsetX - 4, headY + 6, headX + headW/2 + eyeOffsetX, headY + 7, hairColor);
        DrawLine(headX + headW/2 + eyeOffsetX + 2, headY + 7, headX + headW/2 + eyeOffsetX + 6, headY + 6, hairColor);
        
        // Apply Colors.WHITE flash if hit
        if (hitFlashTimer > 0 || whiteFlashTimer > 0) {
            Color flashColor = color(255, 255, 255, 150);
            DrawRectangle(x, y, width, height, flashColor);
        }
        
        // Render sparks (when shield blocks)
        if (showSparks) {
            Color sparkColor = color(255, 200, 50, 255);
            for (int i = 0; i < SPARK_COUNT; i++) {
                int size = 3 + (int)(Math.random() * 3);
                DrawRectangle((int)sparkX[i], (int)sparkY[i], size, size, sparkColor);
            }
            // Brighter center sparks
            for (int i = 0; i < 3; i++) {
                DrawRectangle((int)sparkX[i], (int)sparkY[i], 2, 2, WHITE);
            }
        }
        
        // Hint arrow (golden down arrow) - only shown if player hasn't killed one yet
        if (showHintArrow) {
            int arrowX = x + width / 2;
            int arrowY = headY - 20 - hintArrowBob;
            
            // Glow effect
            Color glowColor = color(255, 215, 0, 80);
            fillOval(arrowX - 12, arrowY - 5, 24, 20, glowColor);
            
            // Arrow shaft
            Color arrowColor = color(255, 200, 0, 255);
            DrawLineEx(vec2(arrowX, arrowY), vec2(arrowX, arrowY + 10), 3, arrowColor);
            
            // Arrow head (pointing down)
            int[] arrowHeadX = {arrowX - 6, arrowX, arrowX + 6};
            int[] arrowHeadY = {arrowY + 10, arrowY + 18, arrowY + 10};
            fillPolygon(arrowHeadX, arrowHeadY, 3, arrowColor);
        }
        
        // Attack indicator
        if (isAttacking) {
            Color attackColor = color(255, 100, 100, 150);
            int attackX = facingRight ? x + width : x - 30;
            DrawRectangle(attackX, y + 20, 30, 40, attackColor);
        }
        
        // Health bar
        renderHealthBar();
    }
    
    @Override
    public void render() {
        // Default render with hint arrow shown
        render(true);
    }
}
