package entities.enemies;
import static utils.RaylibRenderer.*; 

import static com.raylib.Colors.DARKGRAY; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import entities.Enemy; 
import entities.Player; 
import entities.Projectile; 
import utils.Constants; 
import utils.RaylibRenderer; 
import java.util.ArrayList; 
import java.util.List; 





public class Archer extends Enemy {
    
    private List<Projectile> arrows;
    private long lastShootTime;
    private int shootCooldown = Constants.ARCHER_SHOOT_COOLDOWN_MS;
    private int currentWave = 1;
    
    public Archer(double x, double y) {
        super(x, y, Constants.ARCHER_WIDTH, Constants.ARCHER_HEIGHT);
        
        this.health = Constants.ARCHER_HEALTH;
        this.maxHealth = Constants.ARCHER_HEALTH;
        this.attackDamage = Constants.ARCHER_DAMAGE;
        this.moveSpeed = Constants.ARCHER_SPEED;
        this.detectionRange = Constants.ARCHER_DETECTION_RANGE;
        this.attackRange = Constants.ARCHER_ATTACK_RANGE;
        this.attackCooldownTime = Constants.ARCHER_SHOOT_COOLDOWN_MS;
        this.scoreValue = Constants.ARCHER_SCORE;
        this.arrows = new ArrayList<>();
        this.canJumpToPlatform = true;
    }
    
    public void setCurrentWave(int wave) {
        this.currentWave = wave;
    }
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public boolean hasFlaming() {
        return currentWave >= Constants.ARCHER_FLAMING_WAVE;
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
            if (currentTime - knockbackTime > Constants.ENEMY_KNOCKBACK_DURATION_MS) {
                knockedBack = false;
                velocity.set(0, 0);
            } else {
                position.addTo(velocity);
                applyBounds();
                return;
            }
        }
        
        double distanceToPlayer = position.distance(player.getPosition());
        
        
        if (distanceToPlayer < Constants.ARCHER_MIN_RANGE) {
            
            double dir = position.x > player.getPosition().x ? 1 : -1;
            position.x += dir * moveSpeed;
            facingRight = dir < 0;
        } else if (distanceToPlayer <= attackRange) {
            
            facingRight = player.getPosition().x > position.x;
            if (currentTime - lastShootTime > shootCooldown) {
                shoot();
                lastShootTime = currentTime;
            }
        } else if (distanceToPlayer <= detectionRange) {
            
            chasePlayer();
        }
        
        
        for (int i = arrows.size() - 1; i >= 0; i--) {
            Projectile arrow = arrows.get(i);
            arrow.update();
            if (arrow.isOffScreen()) {
                arrows.remove(i);
            }
        }
        
        applyBounds();
    }
    
    private void shoot() {
        
        double startX = facingRight ? position.x + width : position.x;
        double startY = position.y + 20;
        
        double dx = target.getCenterX() - startX;
        double dy = target.getCenterY() - startY;
        double length = Math.sqrt(dx * dx + dy * dy);
        
        
        if (length > 0) {
            dx = (dx / length) * Constants.ARCHER_ARROW_SPEED;
            dy = (dy / length) * Constants.ARCHER_ARROW_SPEED;
        } else {
            dx = facingRight ? Constants.ARCHER_ARROW_SPEED : -Constants.ARCHER_ARROW_SPEED;
            dy = 0;
        }
        
        Projectile arrow = new Projectile(startX, startY, dx, dy, attackDamage, true);
        
        
        if (hasFlaming()) {
            arrow.setFlaming(true);
        }
        
        arrows.add(arrow);
        attacking = true;
        attackStartTime = System.currentTimeMillis();
    }
    
    public List<Projectile> getArrows() {
        List<Projectile> toReturn = new ArrayList<>(arrows);
        arrows.clear();
        return toReturn;
    }
    
    @Override
    public void render() {
        int x = (int) position.x;
        int y = (int) position.y;
        
        
        int alpha = hitFlashTimer > 0 ? 178 : 255;
        
        
        Color archerColor = hasFlaming() ? color(80, 70, 50, alpha) : color(60, 80, 50, alpha);
        if (knockedBack) {
            archerColor = color(90, 110, 70, alpha);
        }
        
        
        fillRoundRect(x + 5, y + 15, width - 10, height - 25, 6, archerColor);
        
        
        fillOval(x + 5, y, 25, 22, archerColor);
        
        
        Color hoodColor = color(50, 70, 40, alpha);
        fillArc(x + 3, y - 2, 29, 20, 0, 180, hoodColor);
        
        
        Color eyeColor = hasFlaming() ? color(255, 150, 50, alpha) : color(255, 255, 255, alpha);
        if (facingRight) {
            fillOval(x + 18, y + 8, 5, 4, eyeColor);
        } else {
            fillOval(x + 12, y + 8, 5, 4, eyeColor);
        }
        
        
        Color bowColor = hasFlaming() ? color(150, 80, 30, alpha) : color(100, 60, 30, alpha);
        if (facingRight) {
            drawArc(x + width, y + 5, 15, 40, -90, 180, bowColor);
            
            Color stringColor = hasFlaming() ? color(255, 200, 100, alpha) : color(255, 255, 255, alpha);
            DrawLine(x + width + 15, y + 5, x + width + 15, y + 45, stringColor);
        } else {
            drawArc(x - 15, y + 5, 15, 40, 90, 180, bowColor);
            Color stringColor = hasFlaming() ? color(255, 200, 100, alpha) : color(255, 255, 255, alpha);
            DrawLine(x, y + 5, x, y + 45, stringColor);
        }
        
        
        DrawRectangle(x + 8, y + height - 15, 7, 15, archerColor);
        DrawRectangle(x + 20, y + height - 15, 7, 15, archerColor);
        
        
        if (hitFlashTimer > 0) {
            Color flashColor = color(255, 255, 255, 150);
            fillRoundRect(x + 5, y + 15, width - 10, height - 25, 6, flashColor);
            fillOval(x + 5, y, 25, 22, flashColor);
        }
        
        
        int barWidth = width;
        DrawRectangle(x, y - 8, barWidth, 4, DARKGRAY);
        double healthPercent = (double) health / maxHealth;
        DrawRectangle(x, y - 8, (int)(barWidth * healthPercent), 4, color(50, 150, 50, 255));
        
        
        for (Projectile arrow : arrows) {
            renderArrow(arrow);
        }
    }
    
    private void renderArrow(Projectile arrow) {
        int ax = (int) arrow.getX();
        int ay = (int) arrow.getY();
        double vx = arrow.getVelocityX();
        double vy = arrow.getVelocityY();
        
        
        double angle = Math.atan2(vy, vx);
        int dirX = vx >= 0 ? 1 : -1;
        
        
        if (arrow.isFlaming()) {
            
            for (int i = 0; i < 3; i++) {
                int trailX = ax - (int)(Math.cos(angle) * (i * 8 + 5));
                int trailY = ay - (int)(Math.sin(angle) * (i * 8 + 5));
                int alpha = 200 - i * 60;
                Color trailColor = color(255, 100 + i * 30, 0, alpha);
                fillOval(trailX - 4, trailY - 3 + (int)(Math.random() * 4), 8 - i, 6 - i, trailColor);
            }
        }
        
        
        int shaftLen = 30;
        int endX = ax + (int)(Math.cos(angle) * shaftLen);
        int endY = ay + (int)(Math.sin(angle) * shaftLen);
        DrawLine(ax, ay, endX, endY, color(100, 60, 30, 255));
        DrawLine(ax, ay + 1, endX, endY + 1, color(100, 60, 30, 255));
        
        
        Color headColor = arrow.isFlaming() ? color(255, 200, 100, 255) : color(150, 150, 160, 255);
        int headX = endX + (int)(Math.cos(angle) * 7);
        int headY = endY + (int)(Math.sin(angle) * 7);
        fillOval(headX - 3, headY - 3, 6, 6, headColor);
        
        
        Color featherColor = arrow.isFlaming() ? color(255, 100, 50, 255) : color(200, 50, 50, 255);
        DrawLine(ax, ay - 2, ax - (int)(Math.cos(angle) * 5), ay - 2, featherColor);
        DrawLine(ax, ay + 2, ax - (int)(Math.cos(angle) * 5), ay + 2, featherColor);
    }
}
