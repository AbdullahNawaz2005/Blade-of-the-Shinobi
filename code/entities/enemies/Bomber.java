package entities.enemies;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import static com.raylib.Colors.DARKGRAY; // Imports DARKGRAY functionality
import static com.raylib.Colors.RED; // Imports RED functionality
import entities.Enemy; // Base enemy class
import entities.Player; // Player character
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import java.util.ArrayList; // For dynamic arrays
import java.util.List; // List interface for collections
import utils.Constants; // Global game constants
import utils.RaylibRenderer; // Rendering utilities

/**
 * Bomber enemy - throws explosive bombs from range
 */
public class Bomber extends Enemy {
    
    private long lastThrowTime = 0;
    private static final int THROW_COOLDOWN_MS = 3000;
    
    // Bomb class for tracking thrown bombs
    public static class Bomb {
        public double x, y;
        public double vx, vy;
        public boolean landed = false;
        public long landTime = 0;
        public boolean exploded = false;
        public int explosionFrame = 0;
        public int fuseBlinkTimer = 0;
        
        public Bomb(double x, double y, double targetX, double targetY) {
            this.x = x;
            this.y = y;
            
            // Calculate arc trajectory
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            // Projectile motion: given a fixed 60-frame (~1 second) flight time,
            // solve for the initial velocities that land on (targetX, targetY).
            // vx is straightforward. vy must cancel out the total gravitational pull
            // accumulated over the flight time (GRAVITY * time / 2) and reach dy.
            double time = 60.0; // frames
            this.vx = dx / time;
            this.vy = (dy / time) - (Constants.GRAVITY * time / 2);
        }
        
        public void update() {
            if (exploded) {
                explosionFrame++;
                return;
            }
            
            if (!landed) {
                vy += Constants.GRAVITY;
                x += vx;
                y += vy;
                
                // Ground check
                if (y > Constants.WINDOW_HEIGHT - 100 - 20) {
                    y = Constants.WINDOW_HEIGHT - 100 - 20;
                    landed = true;
                    landTime = System.currentTimeMillis();
                    vx = 0;
                    vy = 0;
                }
            } else {
                // Check fuse timer
                fuseBlinkTimer++;
                if (System.currentTimeMillis() - landTime >= Constants.BOMB_FUSE_MS) {
                    exploded = true;
                    explosionFrame = 0;
                }
            }
        }
        
        public boolean isFinished() {
            return exploded && explosionFrame >= Constants.BOMB_EXPLOSION_FRAMES;
        }
        
        public void render() {
            if (exploded) {
                // Render explosion - expanding orange/Colors.RED circle
                float progress = (float)explosionFrame / Constants.BOMB_EXPLOSION_FRAMES;
                int radius = (int)(Constants.BOMB_EXPLOSION_RADIUS * progress);
                int alpha = (int)(255 * (1 - progress));
                
                // Outer orange
                fillOval((int)x - radius, (int)y - radius, radius * 2, radius * 2, color(255, 100, 0, alpha));
                
                // Inner Colors.RED
                int innerRadius = (int)(radius * 0.7);
                fillOval((int)x - innerRadius, (int)y - innerRadius, innerRadius * 2, innerRadius * 2, color(255, 50, 0, alpha));
                
                // Center Colors.WHITE flash (first few frames)
                if (explosionFrame < 5) {
                    int coreRadius = (int)(radius * 0.3);
                    fillOval((int)x - coreRadius, (int)y - coreRadius, coreRadius * 2, coreRadius * 2, color(255, 255, 200, alpha));
                }
            } else {
                // Render bomb
                fillOval((int)x - 10, (int)y - 10, 20, 20, color(40, 40, 40, 255));
                
                // Fuse
                DrawLine((int)x, (int)y - 10, (int)x + 5, (int)y - 18, color(139, 90, 43, 255));
                
                // Fuse spark/blinking
                if (landed) {
                    boolean blink = (fuseBlinkTimer / 5) % 2 == 0;
                    if (blink) {
                        fillOval((int)x + 3, (int)y - 21, 6, 6, RED);
                    }
                } else {
                    // Fuse spark while flying
                    fillOval((int)x + 3, (int)y - 21, 5, 5, color(255, 150, 50, 255));
                }
                
                // Bomb shine
                fillOval((int)x - 6, (int)y - 6, 5, 5, DARKGRAY);
            }
        }
        
        // Damage is only applied during the first 5 frames of the explosion animation.
        // After that the explosion visually continues but is no longer dangerous.
        public Rectangle getExplosionBounds() {
            if (exploded && explosionFrame < 5) {
                return rect(
                    (int)x - Constants.BOMB_EXPLOSION_RADIUS,
                    (int)y - Constants.BOMB_EXPLOSION_RADIUS,
                    Constants.BOMB_EXPLOSION_RADIUS * 2,
                    Constants.BOMB_EXPLOSION_RADIUS * 2
                );
            }
            return null;
        }
    }
    
    private List<Bomb> bombs = new ArrayList<>();
    
    public Bomber(double x, double y) {
        super(x, y, Constants.BOMBER_WIDTH, Constants.BOMBER_HEIGHT);
        this.health = Constants.BOMBER_HEALTH;
        this.maxHealth = Constants.BOMBER_HEALTH;
        this.attackDamage = 10; // Contact damage is low
        this.moveSpeed = Constants.BOMBER_SPEED;
        this.detectionRange = Constants.BOMBER_DETECTION_RANGE;
        this.attackRange = Constants.BOMBER_ATTACK_RANGE;
        this.attackCooldown = THROW_COOLDOWN_MS;
        this.attackCooldownTime = THROW_COOLDOWN_MS;
        this.scoreValue = Constants.BOMBER_SCORE;
    }
    
    @Override
    public void update(Object... args) {
        Player player = (Player) args[0];
        this.target = player;
        
        // Update bombs
        bombs.removeIf(Bomb::isFinished);
        for (Bomb bomb : bombs) {
            bomb.update();
        }
        
        // Update facing direction
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
        
        if (distance < detectionRange) {
            if (distance > attackRange) {
                // Move toward player but stop at range
                double dirX = dx / distance;
                position.x += dirX * moveSpeed;
            } else if (distance < attackRange * 0.5) {
                // Too close, back away
                double dirX = dx / distance;
                position.x -= dirX * moveSpeed * 0.5;
            } else {
                // In range - throw bombs
                long now = System.currentTimeMillis();
                if (now - lastThrowTime > THROW_COOLDOWN_MS) {
                    throwBomb(player);
                    lastThrowTime = now;
                }
            }
        }
        
        applyBounds();
    }
    
    private void throwBomb(Player player) {
        // Lead the target by 20 frames to account for the bomb's flight time.
        // Without prediction, fast-moving players could always sidestep the landing zone.
        double targetX = player.getCenterX() + player.getVelocity().x * 20;
        double targetY = player.getPosition().y + player.getHeight();
        
        Bomb bomb = new Bomb(getCenterX(), position.y, targetX, targetY);
        bombs.add(bomb);
    }
    
    @Override
    public void render() {
        int x = (int) position.x;
        int y = (int) position.y;
        
        // Render bombs first
        for (Bomb bomb : bombs) {
            bomb.render();
        }
        
        // Calculate alpha for hit flash
        int alpha = hitFlashTimer > 0 ? 178 : 255;
        
        // Body - dark with explosive hazard colors
        DrawRectangle(x, y, width, height, color(60, 50, 40, alpha));
        
        // Hazard stripes
        for (int i = 0; i < 3; i++) {
            DrawRectangle(x + 5, y + 15 + i * 15, width - 10, 5, color(200, 150, 0, alpha));
        }
        
        // Head with hood
        fillOval(x + 8, y - 5, 24, 24, color(50, 40, 30, alpha));
        
        // Hood
        fillArc(x + 5, y - 8, 30, 20, 0, 180, color(40, 30, 20, alpha));
        
        // Goggles
        int eyeX1 = facingRight ? x + 20 : x + 10;
        fillOval(eyeX1, y + 2, 6, 6, color(100, 200, 100, alpha));
        drawOval(eyeX1 - 1, y + 1, 8, 8, color(80, 80, 80, alpha));
        
        // Backpack with bombs
        int packX = facingRight ? x - 8 : x + width;
        DrawRectangle(packX, y + 10, 12, 30, color(70, 60, 50, alpha));
        
        // Bombs in backpack
        fillOval(packX + 1, y + 12, 10, 10, color(40, 40, 40, alpha));
        fillOval(packX + 1, y + 25, 10, 10, color(40, 40, 40, alpha));
        
        // Apply Colors.WHITE flash if hit
        if (hitFlashTimer > 0 || whiteFlashTimer > 0) {
            DrawRectangle(x, y, width, height, color(255, 255, 255, 150));
        }
        
        // Health bar
        renderHealthBar();
    }
    
    public List<Bomb> getBombs() {
        return bombs;
    }
    
    public void clearBombs() {
        bombs.clear();
    }
}
