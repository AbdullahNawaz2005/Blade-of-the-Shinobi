package entities;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import utils.Vector2D; // 2D math vector

/**
 * Base class for all game entities (players, enemies, projectiles)
 * 
 * Uses Raylib for rendering instead of Graphics2D
 */
public abstract class Entity {
    
    // Position and movement
    protected Vector2D position;
    protected Vector2D velocity;
    protected int width;
    protected int height;
    
    // State
    protected int health;
    protected int maxHealth;
    protected boolean dead;
    protected boolean alive = true;
    protected boolean facingRight;
    
    // Combat
    protected boolean attacking;
    protected int attackDamage;
    // Hitbox stored as [x, y, width, height] instead of Rectangle
    protected int[] attackHitbox;
    
    // Timing
    protected long lastAttackTime;
    protected int attackCooldown; // milliseconds
    
    public Entity(double x, double y, int width, int height) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D();
        this.width = width;
        this.height = height;
        this.dead = false;
        this.facingRight = true;
        this.attacking = false;
        this.attackHitbox = new int[4]; // [x, y, width, height]
        this.lastAttackTime = 0;
        this.attackCooldown = 500;
    }
    
    public abstract void update(Object... args);
    
    /**
     * Render the entity using Raylib
     * Override in subclasses to draw the entity
     */
    public abstract void render();
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            dead = true;
            alive = false;
            onDeath();
        }
    }
    
    protected void onDeath() {
        // Override in subclasses
    }
    
    /**
     * Get entity hitbox as [x, y, width, height] array
     */
    public int[] getHitbox() {
        return new int[] {(int) position.x, (int) position.y, width, height};
    }
    
    /**
     * Get attack hitbox as [x, y, width, height] array
     */
    public int[] getAttackHitbox() {
        return attackHitbox;
    }
    
    protected void updateAttackHitbox(int attackWidth, int attackHeight, int offsetX) {
        int x = facingRight ? 
            (int) position.x + width + offsetX : 
            (int) position.x - attackWidth - offsetX;
        attackHitbox[0] = x;
        attackHitbox[1] = (int) position.y + height/4;
        attackHitbox[2] = attackWidth;
        attackHitbox[3] = attackHeight;
    }
    
    // Getters
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isDead() { return dead; }
    public boolean isAlive() { return alive; }
    public boolean isFacingRight() { return facingRight; }
    public boolean isAttacking() { return attacking; }
    public int getAttackDamage() { return attackDamage; }
    public double getCenterX() { return position.x + width / 2.0; }
    public double getCenterY() { return position.y + height / 2.0; }
    
    // Setters
    public void setPosition(double x, double y) { position.set(x, y); }
    public void setVelocity(double vx, double vy) { velocity.set(vx, vy); }
    public void setFacingRight(boolean facing) { this.facingRight = facing; }
    public void setHealth(int h) { this.health = h; }
    public void setMaxHealth(int h) { this.maxHealth = h; }
}
