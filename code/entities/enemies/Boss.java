package entities.enemies;
import static utils.RaylibRenderer.*; 

import static com.raylib.Colors.DARKGRAY; 
import static com.raylib.Colors.RED; 
import static com.raylib.Colors.WHITE; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import entities.Enemy; 
import entities.Player; 
import utils.Constants; 
import utils.RaylibRenderer; 
import audio.SoundManager; 






public class Boss extends Enemy {
    
    private int phase; 
    private long lastTeleportTime;
    private int teleportCooldown = Constants.BOSS_TELEPORT_COOLDOWN_MS;
    private boolean teleporting;
    private int teleportFrame;
    private String bossName;
    private int bossNumber; 
    private int[] bossColorRGB;
    
    
    private boolean phaseTransitioned50 = false;
    private boolean phaseTransitioned30 = false;
    private int phaseFlashTimer = 0;
    
    
    private boolean minionPhaseActive = false;
    private boolean minionPhaseTriggered = false;
    private boolean minionsSpawned = false;
    private int minionCount = 0;
    
    
    private boolean isInvulnerable = false;
    
    
    private long lastGrabTime = 0;
    private long lastShockwaveTime = 0;
    private boolean performingShockwave = false;
    private int shockwaveFrame = 0;
    private int shockwaveRadius = 0;
    
    
    private int currentPattern;
    private int attacksInPattern;
    
    public Boss(double x, double y) {
        this(x, y, 0);
    }
    
    public Boss(double x, double y, int bossCount) {
        super(x, y, Constants.BOSS_WIDTH, Constants.BOSS_HEIGHT);
        
        this.bossNumber = bossCount % Constants.BOSS_NAMES.length;
        this.bossName = Constants.BOSS_NAMES[bossNumber];
        this.bossColorRGB = Constants.BOSS_COLORS[bossNumber];
        
        this.health = Constants.BOSS_HEALTH;
        this.maxHealth = Constants.BOSS_HEALTH;
        this.attackDamage = Constants.BOSS_DAMAGE_PHASE1;
        this.moveSpeed = Constants.BOSS_SPEED_PHASE1;
        this.detectionRange = Constants.BOSS_DETECTION_RANGE;
        this.attackRange = Constants.BOSS_ATTACK_RANGE;
        this.attackCooldownTime = Constants.BOSS_ATTACK_COOLDOWN_PHASE1_MS;
        this.attackDuration = Constants.BOSS_ATTACK_DURATION_MS;
        this.scoreValue = Constants.BOSS_SCORE;
        this.phase = 1;
        this.teleporting = false;
        this.canJumpToPlatform = true;
    }
    
    
    
    public void setMinionCount(int count) {
        this.minionCount = count;
        if (minionPhaseActive && count == 0) {
            
            minionPhaseActive = false;
            isInvulnerable = false;
            minionsSpawned = false;
            
            if (target != null) {
                position.x = target.getPosition().x + (Math.random() > 0.5 ? 100 : -100);
                position.y = 550 - height; 
            }
            teleporting = true;
            teleportFrame = 0;
        }
    }
    
    public boolean shouldSpawnMinions() {
        if (minionPhaseActive && !minionsSpawned) {
            minionsSpawned = true;
            return true;
        }
        return false;
    }
    
    public int getMinionSpawnCount() {
        return Constants.BOSS_MINION_COUNT;
    }
    
    public boolean isInvulnerable() {
        return isInvulnerable;
    }
    
    public boolean isPerformingShockwave() {
        return performingShockwave;
    }
    
    public int getShockwaveRadius() {
        return shockwaveRadius;
    }
    
    public int getPhaseFlashTimer() {
        return phaseFlashTimer;
    }
    
    @Override
    public void update(Object... args) {
        Player player = (Player) args[0];
        this.target = player;
        
        long currentTime = System.currentTimeMillis();
        
        
        if (phaseFlashTimer > 0) {
            phaseFlashTimer--;
        }
        
        
        double healthPercent = (double) health / maxHealth;
        
        
        if (!phaseTransitioned50 && healthPercent <= Constants.BOSS_PHASE2_HEALTH_THRESHOLD) {
            phaseTransitioned50 = true;
            phase = 2;
            phaseFlashTimer = Constants.BOSS_PHASE_FLASH_FRAMES;
            moveSpeed = Constants.BOSS_SPEED_PHASE2;
            attackCooldownTime = Constants.BOSS_ATTACK_COOLDOWN_PHASE2_MS;
            attackDamage = Constants.BOSS_DAMAGE_PHASE2;
            SoundManager.getInstance().playEffect("boss_phase.wav");
        }
        
        
        if (!phaseTransitioned30 && healthPercent <= Constants.BOSS_PHASE3_HEALTH_THRESHOLD) {
            phaseTransitioned30 = true;
            phase = 3;
            moveSpeed = Constants.BOSS_SPEED_PHASE3;
            attackCooldownTime = Constants.BOSS_ATTACK_COOLDOWN_PHASE3_MS;
            attackDamage = Constants.BOSS_DAMAGE_PHASE3;
            
            
            
            if (!minionPhaseTriggered) {
                minionPhaseTriggered = true;
                minionPhaseActive = true;
                isInvulnerable = true;
                
                
                position.x = Constants.WINDOW_WIDTH / 2 - width / 2;
                position.y = 200;
                teleporting = true;
                teleportFrame = 0;
            }
        }
        
        
        if (performingShockwave) {
            shockwaveFrame++;
            shockwaveRadius = (int)(Constants.BOSS_SHOCKWAVE_RADIUS * ((double)shockwaveFrame / 20));
            if (shockwaveFrame >= 20) {
                performingShockwave = false;
                shockwaveFrame = 0;
                shockwaveRadius = 0;
            }
        }
        
        
        if (teleporting) {
            teleportFrame++;
            if (teleportFrame > Constants.BOSS_TELEPORT_ANIMATION_FRAMES) {
                teleporting = false;
                teleportFrame = 0;
            }
            return;
        }
        
        
        if (minionPhaseActive) {
            return;
        }
        
        
        if (hitFlashTimer > 0) {
            hitFlashTimer--;
        }
        
        
        if (knockedBack) {
            if (currentTime - knockbackTime > 100) {
                knockedBack = false;
                velocity.set(0, 0);
            } else {
                position.x += velocity.x * 0.3;
                applyBounds();
                return;
            }
        }
        
        
        if (attacking) {
            if (currentTime - attackStartTime > attackDuration) {
                attacking = false;
                attacksInPattern++;
            }
            return;
        }
        
        double distanceToPlayer = position.distance(player.getPosition());
        
        
        
        
        
        if (distanceToPlayer <= Constants.BOSS_GRAB_RANGE && 
            currentTime - lastGrabTime > Constants.BOSS_GRAB_COOLDOWN_MS &&
            !player.isInvulnerable() && !player.isStunned()) {
            performGrab(player);
            lastGrabTime = currentTime;
            return;
        }
        
        
        if (phase >= 2 && player.isOnGround() && distanceToPlayer <= Constants.BOSS_SHOCKWAVE_RADIUS &&
            currentTime - lastShockwaveTime > Constants.BOSS_SHOCKWAVE_COOLDOWN_MS) {
            performShockwave();
            lastShockwaveTime = currentTime;
            return;
        }
        
        
        if (phase >= 2 && distanceToPlayer > 300 && 
            currentTime - lastTeleportTime > teleportCooldown) {
            teleportNearPlayer(player);
            lastTeleportTime = currentTime;
            return;
        }
        
        
        if (distanceToPlayer <= attackRange) {
            if (currentTime - lastAttackTime > attackCooldownTime) {
                performAttack();
            }
        } else if (distanceToPlayer <= detectionRange) {
            chasePlayer();
        }
        
        facingRight = player.getPosition().x > position.x;
        applyBounds();
    }
    
    private void performGrab(Player player) {
        
        player.applyStun(Constants.PLAYER_STUN_DURATION_MS);
        SoundManager.getInstance().playEffect("grab.wav");
    }
    
    private void performShockwave() {
        performingShockwave = true;
        shockwaveFrame = 0;
        shockwaveRadius = 0;
        SoundManager.getInstance().playEffect("shockwave.wav");
    }
    
    





    public boolean checkShockwaveHit(Player player) {
        if (!performingShockwave || shockwaveFrame != 5) return false; 
        
        double dx = player.getCenterX() - getCenterX();
        double dy = player.getCenterY() - getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance <= Constants.BOSS_SHOCKWAVE_RADIUS && player.isOnGround();
    }
    
    private void teleportNearPlayer(Player player) {
        teleporting = true;
        teleportFrame = 0;
        
        
        double offsetX = (Math.random() > 0.5 ? 100 : -100);
        position.x = player.getPosition().x + offsetX;
        position.y = player.getPosition().y;
        
        applyBounds();
    }
    
    private void performAttack() {
        attacking = true;
        attackStartTime = System.currentTimeMillis();
        lastAttackTime = attackStartTime;
        state = EnemyState.ATTACKING;
        
        
        if (phase == 3) {
            updateAttackHitbox(100, 80, 5);
        } else if (phase == 2) {
            updateAttackHitbox(80, 70, 5);
        } else {
            updateAttackHitbox(70, 60, 5);
        }
    }
    
    @Override
    public void takeDamage(int damage) {
        if (isInvulnerable) return;
        super.takeDamage(damage);
    }
    
    @Override
    public void render() {
        
        if (phaseFlashTimer > 0) {
            int alpha = (int)(200 * ((double)phaseFlashTimer / Constants.BOSS_PHASE_FLASH_FRAMES));
            DrawRectangle(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, color(255, 0, 0, alpha));
        }
        
        
        if (teleporting) {
            int alpha = Math.max(0, 150 - teleportFrame * 7);
            Color teleportColor = color(bossColorRGB[0], bossColorRGB[1], bossColorRGB[2], alpha);
            fillOval((int)position.x - 10, (int)position.y - 10, width + 20, height + 20, teleportColor);
            return;
        }
        
        int x = (int) position.x;
        int y = (int) position.y;
        
        
        Color mainColor, accentColor;
        
        int r = bossColorRGB[0];
        int gr = bossColorRGB[1];
        int b = bossColorRGB[2];
        
        if (phase == 3) {
            mainColor = color(Math.min(255, r + 40), Math.min(255, gr + 40), Math.min(255, b + 40), 255);
            accentColor = color(255, 150, 150, 255);
        } else if (phase == 2) {
            mainColor = color(Math.min(255, r + 20), Math.min(255, gr + 20), Math.min(255, b + 20), 255);
            accentColor = color(200, 200, 200, 255);
        } else {
            mainColor = color(r, gr, b, 255);
            accentColor = color(150, 150, 150, 255);
        }
        
        
        if (isInvulnerable) {
            fillOval(x - 15, y - 10, width + 30, height + 20, color(255, 255, 100, 80));
        }
        
        
        fillOval(x - 10, y - 5, width + 20, height + 10, color(accentColor.r(), accentColor.g(), accentColor.b(), 50));
        
        
        fillRoundRect(x + 10, y + 30, width - 20, height - 45, 10, mainColor);
        
        
        Color capeColor = color(
            Math.min(255, mainColor.r() + 20), 
            mainColor.g(), 
            Math.min(255, mainColor.b() + 20), 255);
        int[] capeX = {x + 15, x + width/2, x + width - 15, x + width - 10, x + width/2, x + 10};
        int[] capeY = {y + 35, y + 40, y + 35, y + height + 10, y + height - 5, y + height + 10};
        fillPolygon(capeX, capeY, 6, capeColor);
        
        
        fillOval(x + 15, y + 5, 40, 35, mainColor);
        
        
        DrawRectangle(x + 18, y + 20, 34, 12, color(10, 10, 20, 255));
        
        
        Color eyeColor = phase == 3 ? RED : accentColor;
        if (facingRight) {
            fillOval(x + 38, y + 22, 10, 6, eyeColor);
        } else {
            fillOval(x + 22, y + 22, 10, 6, eyeColor);
        }
        
        
        if (phase >= 2) {
            Color hornColor = color(60, 60, 80, 255);
            DrawRectangle(x + 18, y - 5, 5, 15, hornColor);
            DrawRectangle(x + 47, y - 5, 5, 15, hornColor);
        }
        
        
        Color bladeColor = color(40, 40, 60, 255);
        if (attacking) {
            
            if (facingRight) {
                DrawRectangle(x + width - 5, y + 25, 60, 6, bladeColor);
                DrawRectangleLines(x + width - 5, y + 25, 60, 6, accentColor);
            } else {
                DrawRectangle(x - 55, y + 25, 60, 6, bladeColor);
                DrawRectangleLines(x - 55, y + 25, 60, 6, accentColor);
            }
        } else {
            if (facingRight) {
                DrawRectangle(x + width - 10, y + 30, 6, 45, bladeColor);
            } else {
                DrawRectangle(x + 4, y + 30, 6, 45, bladeColor);
            }
        }
        
        
        DrawRectangle(x + 18, y + height - 20, 12, 20, mainColor);
        DrawRectangle(x + 40, y + height - 20, 12, 20, mainColor);
        
        
        if (performingShockwave && shockwaveRadius > 0) {
            int alpha = (int)(200 * (1 - (double)shockwaveFrame / 20));
            Color shockwaveColor = color(255, 255, 100, alpha);
            int cx = x + width / 2;
            int cy = y + height;
            drawOval(cx - shockwaveRadius, cy - shockwaveRadius / 4, 
                       shockwaveRadius * 2, shockwaveRadius / 2, shockwaveColor);
        }
        
        
        int barWidth = 200;
        int barX = x + width/2 - barWidth/2;
        
        
        DrawRectangle(barX, y - 35, barWidth, 12, DARKGRAY);
        
        
        double healthPercent = (double) health / maxHealth;
        Color healthBarColor = phase == 3 ? color(200, 50, 50, 255) : accentColor;
        DrawRectangle(barX, y - 35, (int)(barWidth * healthPercent), 12, healthBarColor);
        
        
        DrawText(bossName, barX, y - 50, 12, WHITE);
        
        
        String phaseText = isInvulnerable ? "INVULNERABLE" : "Phase " + phase;
        DrawText(phaseText, barX + barWidth - 70, y - 50, 10, WHITE);
    }
    
    public String getBossName() {
        return bossName;
    }
    
    public int getPhase() {
        return phase;
    }
}
