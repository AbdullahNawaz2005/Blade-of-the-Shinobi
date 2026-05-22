package entities;
import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import java.util.ArrayList; 
import java.util.List; 
import input.InputHandler; 
import utils.Constants; 
import utils.Vector2D; 






public class Player extends Entity {
    
    
    private int stamina;
    private int maxStamina;
    
    
    private int comboCount;
    private long lastHitTime;
    private int currentAttackDamage;
    private AttackType currentAttackType;
    
    
    private PlayerState state;
    private boolean invulnerable;
    private long invulnerableTime;
    private boolean blocking;
    private boolean dodging;
    private long dodgeStartTime;
    
    
    private boolean stunned;
    private long stunEndTime;
    private int stunShakeOffset;
    
    
    private boolean burning;
    private long burnEndTime;
    private long lastBurnTick;
    private int burnDamagePerTick;
    
    
    private long attackStartTime;
    private int attackDuration;
    private long stateTime;
    
    
    private boolean onGround;
    private double groundY;
    private double verticalVelocity;
    private boolean wasOnWall = false; 
    private boolean onPlatform = false; 
    
    
    private boolean hasSpeedBoost;
    private long speedBoostEndTime;
    private boolean hasDamageBoost;
    private long damageBoostEndTime;
    private int shieldHits;
    
    
    private int hitlagFrames;
    
    



    public static class Afterimage {
        public double x, y;
        public boolean facingRight;
        public float alpha;
        
        






        public Afterimage(double x, double y, boolean facingRight, float alpha) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.alpha = alpha;
        }
    }
    private List<Afterimage> afterimages = new ArrayList<>();
    private int afterimageTimer = 0;
    
    
    private boolean comboFinisherReady;
    
    


    public enum AttackType {
        NONE, SPECIAL
    }
    
    


    public enum PlayerState {
        IDLE, WALKING, ATTACKING, BLOCKING, DODGING, HURT, DEAD, STUNNED
    }
    
    




    public Player(double x, double y) {
        super(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        
        this.health = Constants.PLAYER_MAX_HEALTH;
        this.maxHealth = Constants.PLAYER_MAX_HEALTH;
        this.stamina = Constants.PLAYER_MAX_STAMINA;
        this.maxStamina = Constants.PLAYER_MAX_STAMINA;
        
        this.comboCount = 0;
        this.lastHitTime = 0;
        this.currentAttackDamage = 0;
        this.currentAttackType = AttackType.NONE;
        this.comboFinisherReady = false;
        
        this.state = PlayerState.IDLE;
        this.invulnerable = false;
        this.blocking = false;
        this.dodging = false;
        this.stunned = false;
        this.burning = false;
        
        this.groundY = y;
        this.onGround = true;
        this.onPlatform = false;
        this.verticalVelocity = 0;
        
        this.attackCooldown = Constants.SPECIAL_ATTACK_COOLDOWN_MS;
        this.hitlagFrames = 0;
    }
    
    






    



    public void resetPlatformGroundState() {
        if (onPlatform) {
            
            onGround = false;
            onPlatform = false;
        }
    }
    
    



    @Override
    public void update(Object... args) {
        InputHandler input = (InputHandler) args[0];
        
        
        long currentTime = System.currentTimeMillis();
        
        





        if (hitlagFrames > 0) {
            hitlagFrames--;
            return;
        }
        
        
        if (stunned) {
            
            if (currentTime >= stunEndTime) {
                stunned = false;
                state = PlayerState.IDLE;
            } else {
                
                stunShakeOffset = (int)((Math.random() - 0.5) * 6);
                return;
            }
        }
        
        
        if (burning) {
            
            if (currentTime >= burnEndTime) {
                burning = false;
            
            } else if (currentTime - lastBurnTick >= Constants.BURN_TICK_INTERVAL_MS) {
                
                health -= burnDamagePerTick;
                lastBurnTick = currentTime;
                if (health <= 0) {
                    health = 0;
                    alive = false;
                    state = PlayerState.DEAD;
                }
            }
        }
        
        
        if (stamina < maxStamina && !attacking && !dodging) {
            stamina = Math.min(maxStamina, stamina + 1);
        }
        
        
        if (currentTime - lastHitTime > Constants.COMBO_TIMEOUT * 1000) {
            comboCount = 0;
            comboFinisherReady = false;
        }
        
        
        if (invulnerable && currentTime - invulnerableTime > Constants.PLAYER_INVULNERABILITY_MS) {
            invulnerable = false;
        }
        
        





        if (dodging) {
            
            if (currentTime - dodgeStartTime > Constants.DODGE_DURATION_MS) {
                dodging = false;
                invulnerable = false;
                state = PlayerState.IDLE;
                afterimages.clear();
            } else {
                
                position.addTo(velocity);
                
                
                afterimageTimer++;
                if (afterimageTimer >= Constants.AFTERIMAGE_INTERVAL_FRAMES) {
                    afterimageTimer = 0;
                    
                    if (afterimages.size() >= Constants.AFTERIMAGE_MAX_COUNT) {
                        afterimages.remove(afterimages.size() - 1);
                    }
                    
                    afterimages.add(0, new Afterimage(position.x, position.y, facingRight, 
                                                       Constants.AFTERIMAGE_ALPHAS[0]));
                    
                    for (int i = 1; i < afterimages.size(); i++) {
                        if (i < Constants.AFTERIMAGE_ALPHAS.length) {
                            afterimages.get(i).alpha = Constants.AFTERIMAGE_ALPHAS[i];
                        }
                    }
                }
                
                
                if (position.x < 0) position.x = 0;
                if (position.x > Constants.WINDOW_WIDTH - width) {
                    position.x = Constants.WINDOW_WIDTH - width;
                }
                return;
            }
        }
        
        
        if (attacking) {
            if (currentTime - attackStartTime > attackDuration) {
                attacking = false;
                currentAttackType = AttackType.NONE;
                state = PlayerState.IDLE;
            }
            
            if (!onGround) {
                applyGravityAndMovement(input);
            }
            return;
        }
        
        
        blocking = input.block;
        if (blocking) {
            state = PlayerState.BLOCKING;
            
            handleMovement(input, Constants.BLOCK_MOVEMENT_MULT);
            return;
        }
        
        
        if (input.dodge && stamina >= Constants.DODGE_STAMINA_COST) {
            int horizDir = input.getHorizontalDirection();
            if (horizDir != 0) {
                startDodge(horizDir);
                return;
            }
        }
        
        
        if (input.specialAttack && canAttack() && stamina >= Constants.SPECIAL_ATTACK_STAMINA) {
            startAttack(AttackType.SPECIAL);
        }
        
        
        handleMovement(input, 1.0);
        
        
        if (velocity.x != 0 || velocity.y != 0) {
            state = PlayerState.WALKING;
        } else {
            state = PlayerState.IDLE;
        }
        
        
        if (input.left && !input.right) {
            facingRight = false;
        } else if (input.right && !input.left) {
            facingRight = true;
        }
        
        
        wasOnWall = false;
    }
    
    




    private void handleMovement(InputHandler input, double speedMultiplier) {
        double speed = Constants.PLAYER_SPEED * speedMultiplier;
        
        
        if (hasSpeedBoost) {
            speed *= Constants.SPEED_BOOST_MULT;
        }
        
        
        double airMult = onGround ? 1.0 : Constants.AIR_CONTROL;
        velocity.x = 0;
        if (input.left) velocity.x -= speed * airMult;
        if (input.right) velocity.x += speed * airMult;
        
        
        if (input.up && onGround) {
            verticalVelocity = Constants.JUMP_FORCE; 
            onGround = false;
        }
        
        
        if (!onGround) {
            
            verticalVelocity += Constants.GRAVITY;
            
            if (verticalVelocity > Constants.TERMINAL_VELOCITY) {
                verticalVelocity = Constants.TERMINAL_VELOCITY;
            }
        }
        
        
        position.x += velocity.x;
        position.y += verticalVelocity;
        
        
        if (position.y >= groundY) {
            position.y = groundY; 
            verticalVelocity = 0; 
            onGround = true;
        }
        
        
        if (position.x < 0) {
            position.x = 0; 
            wasOnWall = true;
        }
        if (position.x > Constants.WINDOW_WIDTH - width) {
            position.x = Constants.WINDOW_WIDTH - width; 
            wasOnWall = true;
        }
    }
    
    




    private void applyGravityAndMovement(InputHandler input) {
        
        verticalVelocity += Constants.GRAVITY;
        if (verticalVelocity > Constants.TERMINAL_VELOCITY) {
            verticalVelocity = Constants.TERMINAL_VELOCITY;
        }
        
        double airMult = Constants.AIR_CONTROL;
        double speed = Constants.PLAYER_SPEED * airMult;
        velocity.x = 0;
        
        if (input != null) {
            if (input.left) velocity.x -= speed;
            if (input.right) velocity.x += speed;
        }
        
        position.x += velocity.x;
        position.y += verticalVelocity;
        
        
        if (position.y >= groundY) {
            position.y = groundY;
            verticalVelocity = 0;
            onGround = true;
        }
        
        
        if (position.x < 0) position.x = 0;
        if (position.x > Constants.WINDOW_WIDTH - width) {
            position.x = Constants.WINDOW_WIDTH - width;
        }
    }
    
    



    private boolean canAttack() {
        return !attacking && !dodging && !stunned &&
               System.currentTimeMillis() - lastAttackTime > attackCooldown;
    }
    
    




    public void faceNearestEnemy(java.util.List<? extends Entity> enemies) {
        if (enemies.isEmpty()) return;
        
        Entity nearest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Entity e : enemies) {
            
            double dist = Math.abs(e.getPosition().x - position.x);
            if (dist < minDist) {
                minDist = dist;
                nearest = e;
            }
        }
        
        if (nearest != null) {
            
            facingRight = nearest.getPosition().x > position.x;
        }
    }
    
    



    private void startAttack(AttackType type) {
        attacking = true;
        currentAttackType = type;
        attackStartTime = System.currentTimeMillis();
        lastAttackTime = attackStartTime;
        state = PlayerState.ATTACKING;
        
        
        currentAttackDamage = Constants.SPECIAL_ATTACK_DAMAGE;
        attackDuration = Constants.SPECIAL_ATTACK_DURATION_MS;
        stamina -= Constants.SPECIAL_ATTACK_STAMINA;
        updateAttackHitbox(Constants.SPECIAL_ATTACK_WIDTH, Constants.SPECIAL_ATTACK_HEIGHT, 0);
        
        
        if (comboCount >= Constants.COMBO_FINISHER_THRESHOLD) {
            comboFinisherReady = true;
            
            currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_FINISHER_MULTIPLIER);
        } else {
            
            if (comboCount >= 7) {
                currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_7_MULTIPLIER);
            } else if (comboCount >= 5) {
                currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_5_MULTIPLIER);
            } else if (comboCount >= 3) {
                currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_3_MULTIPLIER);
            }
        }
        
        
        if (hasDamageBoost) {
            currentAttackDamage = (int)(currentAttackDamage * Constants.DAMAGE_BOOST_MULT);
        }
    }
    
    



    private void startDodge(int direction) {
        dodging = true;
        invulnerable = true;
        dodgeStartTime = System.currentTimeMillis();
        state = PlayerState.DODGING;
        stamina -= Constants.DODGE_STAMINA_COST;
        
        
        velocity.set(direction * Constants.DODGE_SPEED, 0);
        facingRight = direction > 0;
        
        
        afterimages.clear();
        afterimageTimer = 0;
    }
    
    



    public void onHitEnemy() {
        comboCount++;
        lastHitTime = System.currentTimeMillis();
    }
    
    



    public void onComboFinisher() {
        comboCount = 0;
        comboFinisherReady = false;
    }
    
    



    public boolean isComboFinisherReady() {
        return comboFinisherReady;
    }
    
    


    public void onBlockHit() {
        
        stamina = Math.max(0, stamina - Constants.BLOCK_STAMINA_COST);
    }
    
    



    public void applyStun(int durationMs) {
        stunned = true;
        stunEndTime = System.currentTimeMillis() + durationMs;
        state = PlayerState.STUNNED;
        attacking = false;
        dodging = false;
    }
    
    




    public void applyBurn(int damagePerTick, int durationMs) {
        burning = true;
        burnDamagePerTick = damagePerTick;
        burnEndTime = System.currentTimeMillis() + durationMs;
        lastBurnTick = System.currentTimeMillis();
    }
    
    



    public void applyHitlag(int frames) {
        hitlagFrames = frames;
    }
    
    



    public int getHitlagFrames() {
        return hitlagFrames;
    }
    
    



    @Override
    public void takeDamage(int damage) {
        if (invulnerable) return;
        
        
        if (shieldHits > 0) {
            shieldHits--;
            invulnerable = true;
            invulnerableTime = System.currentTimeMillis();
            return;
        }
        
        super.takeDamage(damage);
        
        
        invulnerable = true;
        invulnerableTime = System.currentTimeMillis();
        
        
        comboCount = 0;
        comboFinisherReady = false;
        
        
        attacking = false;
        dodging = false;
        afterimages.clear();
        state = PlayerState.HURT;
    }
    
    


    @Override
    public void render() {
        
        if (invulnerable && state != PlayerState.DODGING && System.currentTimeMillis() - invulnerableTime > Constants.PLAYER_INVULNERABILITY_MS) {
            invulnerable = false;
        }

        
        for (int i = afterimages.size() - 1; i >= 0; i--) {
            Afterimage img = afterimages.get(i);
            renderPlayerSprite((int)img.x, (int)img.y, img.facingRight, (int)(img.alpha * 255));
        }
        
        
        if (invulnerable && alive && System.currentTimeMillis() % 100 < 50) {
            return;
        }
        
        int x = (int) position.x;
        int y = (int) position.y;
        
        
        if (stunned) {
            x += stunShakeOffset;
        }
        
        renderPlayerSprite(x, y, facingRight, 255);
        
        
        if (burning) {
            fillOval(x - 5, y - 5, width + 10, height + 10, 
                                    color(255, 100, 0, 100));
        }
    }
    
    






    private void renderPlayerSprite(int x, int y, boolean facingRight, int alpha) {
        
        Color suitColor = color(25, 25, 30, alpha); 
        
        
        if (blocking) {
            suitColor = color(35, 35, 45, alpha);
        } else if (dodging) {
            suitColor = color(20, 20, 25, alpha);
        } else if (stunned) {
            suitColor = color(50, 40, 40, alpha);
        }
        
        
        
        fillOval(x + 12, y + 2, 26, 26, suitColor);
        
        
        DrawRectangle(x + 14, y + 10, 22, 8, color(15, 15, 18, alpha));
        
        
        if (stunned) {
            fillOval(x + 18, y + 11, 6, 6, color(200, 200, 100, alpha));
            fillOval(x + 26, y + 11, 6, 6, color(200, 200, 100, alpha));
        } else {
            
            if (facingRight) {
                fillOval(x + 24, y + 11, 8, 5, color(255, 255, 255, alpha));
                fillOval(x + 28, y + 12, 3, 3, color(0, 0, 0, alpha));
            } else {
                fillOval(x + 18, y + 11, 8, 5, color(255, 255, 255, alpha));
                fillOval(x + 19, y + 12, 3, 3, color(0, 0, 0, alpha));
            }
        }
        
        
        fillRoundRect(x + 10, y + 26, 30, 35, 8, suitColor);
        
        
        DrawRectangle(x + 10, y + 48, 30, 6, color(180, 30, 30, alpha));
        
        DrawRectangle(x + 22, y + 47, 6, 8, color(150, 20, 20, alpha));
        
        
        if (attacking) {
            
            if (facingRight) {
                
                fillRoundRect(x + 38, y + 30, 15, 8, 4, suitColor);
                
                fillOval(x + 50, y + 29, 8, 10, color(60, 60, 65, alpha));
                
                
                
                DrawRectangle(x + 56, y + 32, 40, 3, color(200, 200, 210, alpha));
                
                DrawLine(x + 56, y + 32, x + 96, y + 32, color(230, 230, 240, alpha));
                
                int[] tipX = {x + 96, x + 102, x + 96};
                int[] tipY = {y + 32, y + 33, y + 35};
                fillPolygon(tipX, tipY, 3, color(200, 200, 210, alpha));
                
                DrawRectangle(x + 48, y + 31, 10, 5, color(40, 20, 20, alpha));
                for (int i = 0; i < 3; i++) {
                    DrawLine(x + 49 + i*3, y + 31, x + 51 + i*3, y + 36, color(150, 30, 30, alpha));
                }
                
                fillOval(x + 54, y + 30, 4, 7, color(80, 70, 60, alpha));
            } else {
                
                fillRoundRect(x - 3, y + 30, 15, 8, 4, suitColor);
                
                fillOval(x - 8, y + 29, 8, 10, color(60, 60, 65, alpha));
                
                
                DrawRectangle(x - 46, y + 32, 40, 3, color(200, 200, 210, alpha));
                DrawLine(x - 46, y + 32, x - 6, y + 32, color(230, 230, 240, alpha));
                
                int[] tipX = {x - 46, x - 52, x - 46};
                int[] tipY = {y + 32, y + 33, y + 35};
                fillPolygon(tipX, tipY, 3, color(200, 200, 210, alpha));
                
                DrawRectangle(x - 8, y + 31, 10, 5, color(40, 20, 20, alpha));
                for (int i = 0; i < 3; i++) {
                    DrawLine(x - 7 + i*3, y + 31, x - 5 + i*3, y + 36, color(150, 30, 30, alpha));
                }
                
                fillOval(x - 8, y + 30, 4, 7, color(80, 70, 60, alpha));
            }
        } else {
            
            fillRoundRect(x + 5, y + 28, 8, 22, 4, suitColor);
            fillRoundRect(x + 37, y + 28, 8, 22, 4, suitColor);
            
            fillOval(x + 5, y + 47, 8, 8, color(60, 60, 65, alpha));
            fillOval(x + 37, y + 47, 8, 8, color(60, 60, 65, alpha));
            
            
            Color sheathColor = color(30, 15, 15, alpha);
            if (facingRight) {
                DrawRectangle(x + 8, y + 10, 4, 45, sheathColor);
            } else {
                DrawRectangle(x + 38, y + 10, 4, 45, sheathColor);
            }
        }
        
        
        boolean isMoving = (state == PlayerState.WALKING);
        int legOffset1 = 0, legOffset2 = 0;
        
        if (isMoving && onGround) {
            
            
            
            
            double runCycle = (System.currentTimeMillis() % 300) / 300.0;
            legOffset1 = (int)(Math.sin(runCycle * Math.PI * 2) * 6);
            legOffset2 = (int)(Math.sin((runCycle + 0.5) * Math.PI * 2) * 6);
        }
        
        
        fillRoundRect(x + 12 + legOffset1, y + 54, 10, 22, 4, suitColor);
        
        fillRoundRect(x + 28 + legOffset2, y + 54, 10, 22, 4, suitColor);
        
        
        Color shoeColor = color(240, 240, 245, alpha);
        fillRoundRect(x + 10 + legOffset1, y + 73, 14, 7, 4, shoeColor);
        fillRoundRect(x + 26 + legOffset2, y + 73, 14, 7, 4, shoeColor);
        
        Color shoeSplitColor = color(200, 200, 205, alpha);
        DrawLine(x + 17 + legOffset1, y + 73, x + 17 + legOffset1, y + 78, shoeSplitColor);
        DrawLine(x + 33 + legOffset2, y + 73, x + 33 + legOffset2, y + 78, shoeSplitColor);
    }
    
    
    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }
    public int getComboCount() { return comboCount; }
    public int getCurrentAttackDamage() { return currentAttackDamage; }
    public boolean isBlocking() { return blocking; }
    public boolean isDodging() { return dodging; }
    public boolean isStunned() { return stunned; }
    public boolean isBurning() { return burning; }
    public boolean isInvulnerable() { return invulnerable; }
    public PlayerState getState() { return state; }
    public int getMaxHealth() { return maxHealth; }
    public boolean hasSpeedBoost() { return hasSpeedBoost; }
    public boolean hasDamageBoost() { return hasDamageBoost; }
    public int getShieldHits() { return shieldHits; }
    
    



    public long getSpeedBoostRemainingMs() {
        if (!hasSpeedBoost) return 0;
        return Math.max(0, speedBoostEndTime - System.currentTimeMillis());
    }
    
    



    public long getDamageBoostRemainingMs() {
        if (!hasDamageBoost) return 0;
        return Math.max(0, damageBoostEndTime - System.currentTimeMillis());
    }
    
    



    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    



    public void applySpeedBoost(int durationMs) {
        hasSpeedBoost = true;
        speedBoostEndTime = System.currentTimeMillis() + durationMs;
    }
    
    



    public void applyDamageBoost(int durationMs) {
        hasDamageBoost = true;
        damageBoostEndTime = System.currentTimeMillis() + durationMs;
    }
    
    



    public void applyShield(int hits) {
        shieldHits = hits;
    }
    
    


    public void updatePowerUps() {
        long now = System.currentTimeMillis();
        if (hasSpeedBoost && now > speedBoostEndTime) {
            hasSpeedBoost = false;
        }
        if (hasDamageBoost && now > damageBoostEndTime) {
            hasDamageBoost = false;
        }
    }
    
    





    public void checkPlatformCollision(Rectangle platform) {
        double playerBottom = position.y + height;
        double playerCenterX = position.x + width / 2;
        boolean horizontallyOnPlatform = playerCenterX >= platform.x() && playerCenterX <= platform.x() + platform.width();
        
        
        if (horizontallyOnPlatform) {
            
            if (Math.abs(playerBottom - platform.y()) < 5 && verticalVelocity >= 0) {
                position.y = platform.y() - height;
                verticalVelocity = 0;
                onGround = true;
                onPlatform = true;
            }
            
            else if (verticalVelocity > 0 && playerBottom >= platform.y() && playerBottom <= platform.y() + 20) {
                position.y = platform.y() - height;
                verticalVelocity = 0;
                onGround = true;
                onPlatform = true;
            }
        }
    }
    
    



    public boolean isOnGround() {
        return onGround;
    }
    
    



    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
