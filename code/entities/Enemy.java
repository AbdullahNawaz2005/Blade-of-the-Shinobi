package entities;

import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import static com.raylib.Colors.DARKGRAY; // Imports DARKGRAY functionality
import static com.raylib.Colors.RED; // Imports RED functionality
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import utils.Constants; // Global game constants
import utils.Vector2D; // 2D math vector

/**
 * Base class for all enemies
 * Uses Raylib for rendering
 */
public abstract class Enemy extends Entity {

    // AI State
    protected EnemyState state;
    protected Player target;

    // Movement
    protected double moveSpeed;
    protected double detectionRange;
    protected double attackRange;
    protected double velocityY = 0;
    protected boolean isGrounded = true;

    // Combat
    protected long attackCooldownTime;
    protected int attackDuration;
    protected long attackStartTime;
    protected int attackTimer;

    // Scoring
    protected int scoreValue;

    // Knockback
    protected boolean knockedBack;
    protected long knockbackTime;

    // Hit flash effect
    protected int hitFlashTimer = 0;

    // Block knockback effect
    protected int knockbackTimer = 0;
    protected double knockbackVelocityX = 0;

    // Colors.WHITE flash on block pushback
    protected int whiteFlashTimer = 0;

    // Hitlag freeze
    protected int hitlagFrames = 0;

    // Platform capability
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
        this.attackCooldown = 1000; // Entity.attackCooldown (int)
        this.attackDuration = 300;
        this.scoreValue = 100;
        this.knockedBack = false;
    }

    public void update(Player player, double deltaTime) {
        // Override in subclasses
        update((Object) player);
    }

    @Override
    public void update(Object... args) {
        Player player = (Player) args[0];
        this.target = player;

        long currentTime = System.currentTimeMillis();

        // Hitlag freeze makes the game feel impactful by pausing the enemy briefly when hit
        if (hitlagFrames > 0) {
            hitlagFrames--;
            return;
        }

        // Update hit flash
        if (hitFlashTimer > 0) {
            hitFlashTimer--;
        }

        // Update Colors.WHITE flash
        if (whiteFlashTimer > 0) {
            whiteFlashTimer--;
        }

        // Handle block knockback - velocity decays linearly each frame.
        // knockbackVelocityX * (remaining/total) makes it decelerate to a stop naturally
        if (knockbackTimer > 0) {
            knockbackTimer--;
            position.x += knockbackVelocityX * ((double) knockbackTimer / Constants.BLOCK_KNOCKBACK_FRAMES);
            applyBounds();
            return; // Skip normal AI while being pushed back
        }

        // Handle standard damage knockback
        if (knockedBack) {
            if (currentTime - knockbackTime > Constants.ENEMY_KNOCKBACK_DURATION_MS) {
                knockedBack = false;
                velocity.set(0, 0); // Stop moving after knockback ends
            } else {
                position.addTo(velocity); // Apply knockback force
                applyBounds();
                return; // Skip AI while flying back
            }
        }

        // Handle attack animation
        if (attacking) {
            if (currentTime - attackStartTime > attackDuration) {
                attacking = false;
                state = EnemyState.CHASING;
            }
            return;
        }

        // Basic AI behavior state machine
        // Calculate direct distance between enemy and player to decide what to do
        double distanceToPlayer = position.distance(player.getPosition());

        if (distanceToPlayer <= attackRange) {
            // Close enough to attack. Check if cooldown has passed.
            if (currentTime - lastAttackTime > attackCooldownTime) {
                startAttack();
            } else {
                state = EnemyState.IDLE; // Wait for cooldown
            }
        } else if (distanceToPlayer <= detectionRange) {
            // Player is in sight, start moving towards them
            state = EnemyState.CHASING;
            chasePlayer();
        } else {
            // Player is too far, just stand still
            state = EnemyState.IDLE;
        }

        applyBounds();
    }

    protected void chasePlayer() {
        if (target == null)
            return;

        // Calculate a normalized vector (direction only, length 1) pointing at the player
        Vector2D direction = target.getPosition().subtract(position).normalize();
        
        // We only move horizontally in this game, so just use the X direction
        velocity.x = direction.x * moveSpeed;

        position.addTo(velocity);

        // Face player left or right
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

        // Trigger hit flash
        hitFlashTimer = Constants.HIT_FLASH_FRAMES;

        if (!dead) {
            // Knockback
            knockedBack = true;
            knockbackTime = System.currentTimeMillis();

            // Knock horizontally away from the player (no vertical launch).
            // Direction is determined by which side of the player the enemy is on.
            if (target != null) {
                double dir = position.x > target.getPosition().x ? 1 : -1;
                velocity.set(dir * Constants.ENEMY_KNOCKBACK_FORCE, 0);
            }

            // Interrupt attack
            attacking = false;
            state = EnemyState.HURT;
        }
    }

    /**
     * Pushes the enemy back when the player successfully blocks.
     * Velocity is calculated so the enemy travels the full BLOCK_KNOCKBACK_DISTANCE
     * over BLOCK_KNOCKBACK_FRAMES frames.
     */
    public void applyBlockKnockback(double playerX) {
        double dir = position.x > playerX ? 1 : -1;
        knockbackVelocityX = dir * (Constants.BLOCK_KNOCKBACK_DISTANCE / Constants.BLOCK_KNOCKBACK_FRAMES);
        knockbackTimer = Constants.BLOCK_KNOCKBACK_FRAMES;
        // Brief white flash gives the player tactile feedback that the block connected
        whiteFlashTimer = 2;
    }

    /**
     * Apply hitlag freeze
     */
    public void applyHitlag(int frames) {
        hitlagFrames = frames;
    }

    public int getHitlagFrames() {
        return hitlagFrames;
    }

    @Override
    public void render() {
        // Override in subclasses for specific appearance
        renderBase(RED);
    }

    protected void renderBase(Color color) {
        int x = (int) position.x;
        int y = (int) position.y;

        // Calculate alpha for hit flash
        int alpha = hitFlashTimer > 0 ? 178 : 255; // 0.7 * 255 = 178
        Color flashedColor = color(color.r(), color.g(), color.b(), alpha);

        // Body
        fillRoundRect(x + 5, y + 15, width - 10, height - 25, 8, flashedColor);

        // Head
        fillOval(x + 8, y, 24, 24, flashedColor);

        // Eyes
        Color eyeColor = color(0, 0, 0, alpha);
        if (facingRight) {
            fillOval(x + 20, y + 8, 6, 4, eyeColor);
        } else {
            fillOval(x + 14, y + 8, 6, 4, eyeColor);
        }

        // Colors.WHITE flash overlay if hit
        if (hitFlashTimer > 0 || whiteFlashTimer > 0) {
            Color whiteFlash = color(255, 255, 255, 150);
            fillRoundRect(x + 5, y + 15, width - 10, height - 25, 8, whiteFlash);
            fillOval(x + 8, y, 24, 24, whiteFlash);
        }

        // Health bar above enemy
        renderHealthBar();
    }

    protected void renderHealthBar() {
        int x = (int) position.x;
        int y = (int) position.y;

        int barWidth = width;
        int barHeight = 5;
        int barX = x;
        int barY = y - 10;

        // Background
        DrawRectangle(barX, barY, barWidth, barHeight, DARKGRAY);

        // Health
        double healthPercent = (double) health / maxHealth;
        DrawRectangle(barX, barY, (int) (barWidth * healthPercent), barHeight, RED);
    }

    // Getters
    public int getScoreValue() {
        return scoreValue;
    }

    public EnemyState getState() {
        return state;
    }
}
