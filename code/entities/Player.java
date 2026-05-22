package entities;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers

import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Raylib.Color; // Raylib color struct
import java.util.ArrayList; // For dynamic arrays
import java.util.List; // List interface for collections
import input.InputHandler; // Keyboard input handling
import utils.Constants; // Global game constants
import utils.Vector2D; // 2D math vector

/**
 * Player character - the ninja.
 * Manages all physical interactions, states, animations, and combat mechanics 
 * (except projectile spawning which is handled by GameEngine).
 */
public class Player extends Entity {
    
    // Stats
    private int stamina;
    private int maxStamina;
    
    // Combat - simplified (no light/heavy attacks)
    private int comboCount;
    private long lastHitTime;
    private int currentAttackDamage;
    private AttackType currentAttackType;
    
    // States
    private PlayerState state;
    private boolean invulnerable;
    private long invulnerableTime;
    private boolean blocking;
    private boolean dodging;
    private long dodgeStartTime;
    
    // Stun state (from boss grab)
    private boolean stunned;
    private long stunEndTime;
    private int stunShakeOffset;
    
    // Burn damage over time
    private boolean burning;
    private long burnEndTime;
    private long lastBurnTick;
    private int burnDamagePerTick;
    
    // Animation timing
    private long attackStartTime;
    private int attackDuration;
    private long stateTime;
    
    // Movement - fix wall stick bug
    private boolean onGround;
    private double groundY;
    private double verticalVelocity;
    private boolean wasOnWall = false; // Track wall contact for reset
    private boolean onPlatform = false; // Track if standing on a platform (not floor)
    
    // Power-up effects
    private boolean hasSpeedBoost;
    private long speedBoostEndTime;
    private boolean hasDamageBoost;
    private long damageBoostEndTime;
    private int shieldHits;
    
    // Hitlag (freeze on heavy impact)
    private int hitlagFrames;
    
    /**
     * Inner class representing a static snapshot of the player's position 
     * used to draw the trailing dodge effect.
     */
    public static class Afterimage {
        public double x, y;
        public boolean facingRight;
        public float alpha;
        
        /**
         * Creates an Afterimage.
         * @param x X coordinate
         * @param y Y coordinate
         * @param facingRight The direction the player was facing
         * @param alpha The opacity of the image (0.0 to 1.0)
         */
        public Afterimage(double x, double y, boolean facingRight, float alpha) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.alpha = alpha;
        }
    }
    private List<Afterimage> afterimages = new ArrayList<>();
    private int afterimageTimer = 0;
    
    // Combo finisher tracking
    private boolean comboFinisherReady;
    
    /**
     * Defines the type of attack currently executing.
     */
    public enum AttackType {
        NONE, SPECIAL
    }
    
    /**
     * Defines the high-level state of the player character.
     */
    public enum PlayerState {
        IDLE, WALKING, ATTACKING, BLOCKING, DODGING, HURT, DEAD, STUNNED
    }
    
    /**
     * Constructs a Player at the specified coordinates.
     * @param x Initial X coordinate
     * @param y Initial Y coordinate
     */
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
    
    /*
     * PLATFORM COLLISION MECHANIC:
     * To allow jumping off edges of floating platforms properly, the player's ground state
     * must first be optimistically reset to 'false' (falling) before checking collisions.
     * If they are still over the platform, checkPlatformCollision will set it back to true.
     * If they walked off, it remains false and gravity takes over naturally.
     */
    /**
     * Resets ground state before checking platforms.
     * We assume the player has walked off a platform until proven otherwise by a collision check.
     */
    public void resetPlatformGroundState() {
        if (onPlatform) {
            // Assume we've fallen off, so gravity will pull us down this frame
            onGround = false;
            onPlatform = false;
        }
    }
    
    /**
     * Updates player logic including physics, state transitions, and damage over time.
     * @param args Accepts an InputHandler as args[0]
     */
    @Override
    public void update(Object... args) {
        InputHandler input = (InputHandler) args[0];
        
        // System.currentTimeMillis() is used as the absolute clock for timed events (cooldowns, buffs)
        long currentTime = System.currentTimeMillis();
        
        /*
         * HITLAG FREEZE MECHANIC:
         * Hitlag freezes the player (and the enemy hit) for a few frames upon landing a heavy blow.
         * This adds weight and impact to combat without changing the timescale of the entire game.
         * We simply return early from the update loop to prevent physics/animations from advancing.
         */
        if (hitlagFrames > 0) {
            hitlagFrames--;
            return;
        }
        
        // Handle stun state
        if (stunned) {
            // Compare currentTime to the stun end timestamp to check if the stun has expired
            if (currentTime >= stunEndTime) {
                stunned = false;
                state = PlayerState.IDLE;
            } else {
                // Shake animation while stunned: Random value between -3 and +3 pixels
                stunShakeOffset = (int)((Math.random() - 0.5) * 6);
                return;
            }
        }
        
        // Handle burn damage
        if (burning) {
            // Check if the burn duration has completely finished
            if (currentTime >= burnEndTime) {
                burning = false;
            // Check if enough time has passed since the last damage tick
            } else if (currentTime - lastBurnTick >= Constants.BURN_TICK_INTERVAL_MS) {
                // Apply burn damage
                health -= burnDamagePerTick;
                lastBurnTick = currentTime;
                if (health <= 0) {
                    health = 0;
                    alive = false;
                    state = PlayerState.DEAD;
                }
            }
        }
        
        // Slowly recover stamina over time as long as we aren't using it
        if (stamina < maxStamina && !attacking && !dodging) {
            stamina = Math.min(maxStamina, stamina + 1);
        }
        
        // Check combo timeout. If too much time has passed since the last hit, reset combo counters
        if (currentTime - lastHitTime > Constants.COMBO_TIMEOUT * 1000) {
            comboCount = 0;
            comboFinisherReady = false;
        }
        
        // Handle i-frames expiration
        if (invulnerable && currentTime - invulnerableTime > Constants.PLAYER_INVULNERABILITY_MS) {
            invulnerable = false;
        }
        
        /*
         * DODGE AFTERIMAGE TRAIL MECHANIC:
         * When the player dodges, we periodically snapshot their X/Y position and facing direction.
         * These snapshots are stored in the 'afterimages' list. The oldest images are removed, 
         * and the opacity (alpha) of existing images decays over time, creating a trailing ghost effect.
         */
        if (dodging) {
            // Check if the dodge duration has expired
            if (currentTime - dodgeStartTime > Constants.DODGE_DURATION_MS) {
                dodging = false;
                invulnerable = false;
                state = PlayerState.IDLE;
                afterimages.clear();
            } else {
                // Continue moving the player by their current dodge velocity
                position.addTo(velocity);
                
                // Update afterimages every few frames to prevent too many overlapping sprites
                afterimageTimer++;
                if (afterimageTimer >= Constants.AFTERIMAGE_INTERVAL_FRAMES) {
                    afterimageTimer = 0;
                    // Cap the maximum number of trailing images
                    if (afterimages.size() >= Constants.AFTERIMAGE_MAX_COUNT) {
                        afterimages.remove(afterimages.size() - 1);
                    }
                    // Insert the freshest afterimage at index 0 (the front)
                    afterimages.add(0, new Afterimage(position.x, position.y, facingRight, 
                                                       Constants.AFTERIMAGE_ALPHAS[0]));
                    // Decay the alphas of all older afterimages based on their new index
                    for (int i = 1; i < afterimages.size(); i++) {
                        if (i < Constants.AFTERIMAGE_ALPHAS.length) {
                            afterimages.get(i).alpha = Constants.AFTERIMAGE_ALPHAS[i];
                        }
                    }
                }
                
                // Constrain the player to screen bounds during the dodge to prevent escaping the arena
                if (position.x < 0) position.x = 0;
                if (position.x > Constants.WINDOW_WIDTH - width) {
                    position.x = Constants.WINDOW_WIDTH - width;
                }
                return;
            }
        }
        
        // Handle attack animation length
        if (attacking) {
            if (currentTime - attackStartTime > attackDuration) {
                attacking = false;
                currentAttackType = AttackType.NONE;
                state = PlayerState.IDLE;
            }
            // If the player is airborne during an attack, continue applying gravity to prevent them hanging in midair
            if (!onGround) {
                applyGravityAndMovement(input);
            }
            return;
        }
        
        // Read block state from the InputHandler
        blocking = input.block;
        if (blocking) {
            state = PlayerState.BLOCKING;
            // Heavily reduce movement speed while blocking
            handleMovement(input, Constants.BLOCK_MOVEMENT_MULT);
            return;
        }
        
        // Handle dodge input initialization
        if (input.dodge && stamina >= Constants.DODGE_STAMINA_COST) {
            int horizDir = input.getHorizontalDirection();
            if (horizDir != 0) {
                startDodge(horizDir);
                return;
            }
        }
        
        // Handle special attack input
        if (input.specialAttack && canAttack() && stamina >= Constants.SPECIAL_ATTACK_STAMINA) {
            startAttack(AttackType.SPECIAL);
        }
        
        // Handle regular WASD/Arrow physics movement
        handleMovement(input, 1.0);
        
        // Update visual state flag based on whether velocity exists
        if (velocity.x != 0 || velocity.y != 0) {
            state = PlayerState.WALKING;
        } else {
            state = PlayerState.IDLE;
        }
        
        // Update facing direction based on horizontal input
        if (input.left && !input.right) {
            facingRight = false;
        } else if (input.right && !input.left) {
            facingRight = true;
        }
        
        // Reset wall contact flag each frame, it is recalculated inside handleMovement
        wasOnWall = false;
    }
    
    /**
     * Calculates and applies horizontal velocity and vertical gravity.
     * @param input Input handler for reading movement keys
     * @param speedMultiplier Modifier for movement speed (e.g., slower when blocking)
     */
    private void handleMovement(InputHandler input, double speedMultiplier) {
        double speed = Constants.PLAYER_SPEED * speedMultiplier;
        
        // Apply speed boost from power-ups
        if (hasSpeedBoost) {
            speed *= Constants.SPEED_BOOST_MULT;
        }
        
        // Ternary operator: If on the ground, allow full control (1.0). If in the air, reduce control.
        double airMult = onGround ? 1.0 : Constants.AIR_CONTROL;
        velocity.x = 0;
        if (input.left) velocity.x -= speed * airMult;
        if (input.right) velocity.x += speed * airMult;
        
        // Jump logic: only jump if we're standing on something solid
        if (input.up && onGround) {
            verticalVelocity = Constants.JUMP_FORCE; // Negative value to move up on screen
            onGround = false;
        }
        
        // Apply gravity if in the air
        if (!onGround) {
            // Gravity pulls the player down every frame
            verticalVelocity += Constants.GRAVITY;
            // Prevent the player from falling too fast (terminal velocity) so they don't clip through the floor
            if (verticalVelocity > Constants.TERMINAL_VELOCITY) {
                verticalVelocity = Constants.TERMINAL_VELOCITY;
            }
        }
        
        // Update absolute coordinate positions
        position.x += velocity.x;
        position.y += verticalVelocity;
        
        // Ground floor collision check
        if (position.y >= groundY) {
            position.y = groundY; // Snap exactly to the floor
            verticalVelocity = 0; // Stop falling
            onGround = true;
        }
        
        // Screen bounds collision (keep player inside the window)
        if (position.x < 0) {
            position.x = 0; // Left edge
            wasOnWall = true;
        }
        if (position.x > Constants.WINDOW_WIDTH - width) {
            position.x = Constants.WINDOW_WIDTH - width; // Right edge
            wasOnWall = true;
        }
    }
    
    /**
     * Alternate movement handler exclusively used when the player is airborne while attacking.
     * Ensures they still fall and can slightly steer, preventing them from freezing in mid-air against walls.
     * @param input Input handler for reading movement keys
     */
    private void applyGravityAndMovement(InputHandler input) {
        // Apply gravity during attacks in air
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
        
        // Ground check
        if (position.y >= groundY) {
            position.y = groundY;
            verticalVelocity = 0;
            onGround = true;
        }
        
        // Screen bounds
        if (position.x < 0) position.x = 0;
        if (position.x > Constants.WINDOW_WIDTH - width) {
            position.x = Constants.WINDOW_WIDTH - width;
        }
    }
    
    /**
     * Validates if the player is currently allowed to initiate an attack.
     * @return true if the player can attack, false otherwise
     */
    private boolean canAttack() {
        return !attacking && !dodging && !stunned &&
               System.currentTimeMillis() - lastAttackTime > attackCooldown;
    }
    
    /**
     * Automatically faces the player towards the nearest enemy horizontally.
     * Used right before executing an attack to ensure it hits.
     * @param enemies The list of currently active enemies on the screen
     */
    public void faceNearestEnemy(java.util.List<? extends Entity> enemies) {
        if (enemies.isEmpty()) return;
        
        Entity nearest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Entity e : enemies) {
            // Calculate absolute horizontal distance
            double dist = Math.abs(e.getPosition().x - position.x);
            if (dist < minDist) {
                minDist = dist;
                nearest = e;
            }
        }
        
        if (nearest != null) {
            // True if the enemy is to the right of the player, false otherwise
            facingRight = nearest.getPosition().x > position.x;
        }
    }
    
    /**
     * Initiates the attack sequence, deducting stamina and calculating damage multipliers.
     * @param type The type of attack being executed (e.g., SPECIAL)
     */
    private void startAttack(AttackType type) {
        attacking = true;
        currentAttackType = type;
        attackStartTime = System.currentTimeMillis();
        lastAttackTime = attackStartTime;
        state = PlayerState.ATTACKING;
        
        // Set up attack parameters
        currentAttackDamage = Constants.SPECIAL_ATTACK_DAMAGE;
        attackDuration = Constants.SPECIAL_ATTACK_DURATION_MS;
        stamina -= Constants.SPECIAL_ATTACK_STAMINA;
        updateAttackHitbox(Constants.SPECIAL_ATTACK_WIDTH, Constants.SPECIAL_ATTACK_HEIGHT, 0);
        
        // Check if combo count has reached the threshold to trigger a massive finisher attack
        if (comboCount >= Constants.COMBO_FINISHER_THRESHOLD) {
            comboFinisherReady = true;
            // Cast to int to truncate floating point multiplier logic
            currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_FINISHER_MULTIPLIER);
        } else {
            // Apply ascending combo multipliers based on the current streak
            if (comboCount >= 7) {
                currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_7_MULTIPLIER);
            } else if (comboCount >= 5) {
                currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_5_MULTIPLIER);
            } else if (comboCount >= 3) {
                currentAttackDamage = (int)(currentAttackDamage * Constants.COMBO_3_MULTIPLIER);
            }
        }
        
        // Apply secondary damage boost from power-ups if active
        if (hasDamageBoost) {
            currentAttackDamage = (int)(currentAttackDamage * Constants.DAMAGE_BOOST_MULT);
        }
    }
    
    /**
     * Initiates the dodge roll maneuver.
     * @param direction -1 for left, +1 for right
     */
    private void startDodge(int direction) {
        dodging = true;
        invulnerable = true;
        dodgeStartTime = System.currentTimeMillis();
        state = PlayerState.DODGING;
        stamina -= Constants.DODGE_STAMINA_COST;
        
        // Set fixed horizontal velocity for the duration of the dodge
        velocity.set(direction * Constants.DODGE_SPEED, 0);
        facingRight = direction > 0;
        
        // Clear ghost trail
        afterimages.clear();
        afterimageTimer = 0;
    }
    
    /**
     * Called externally when the player successfully damages an enemy.
     * Used to maintain the combo meter.
     */
    public void onHitEnemy() {
        comboCount++;
        lastHitTime = System.currentTimeMillis();
    }
    
    /**
     * Called externally when the massive combo finisher connects.
     * Resets the combo meter.
     */
    public void onComboFinisher() {
        comboCount = 0;
        comboFinisherReady = false;
    }
    
    /**
     * Checks if the next attack will be a combo finisher.
     * @return true if the finisher is queued
     */
    public boolean isComboFinisherReady() {
        return comboFinisherReady;
    }
    
    /**
     * Called externally when an enemy attack strikes the player while blocking.
     */
    public void onBlockHit() {
        // Reduces stamina. Uses Math.max to prevent stamina from falling below 0.
        stamina = Math.max(0, stamina - Constants.BLOCK_STAMINA_COST);
    }
    
    /**
     * Applies a stun status effect (e.g., from boss grab).
     * @param durationMs Length of the stun in milliseconds
     */
    public void applyStun(int durationMs) {
        stunned = true;
        stunEndTime = System.currentTimeMillis() + durationMs;
        state = PlayerState.STUNNED;
        attacking = false;
        dodging = false;
    }
    
    /**
     * Applies a damage-over-time burn effect.
     * @param damagePerTick Amount of damage per interval
     * @param durationMs Total duration of the effect in milliseconds
     */
    public void applyBurn(int damagePerTick, int durationMs) {
        burning = true;
        burnDamagePerTick = damagePerTick;
        burnEndTime = System.currentTimeMillis() + durationMs;
        lastBurnTick = System.currentTimeMillis();
    }
    
    /**
     * Freezes the player update loop for a brief dramatic pause.
     * @param frames Number of frames to freeze
     */
    public void applyHitlag(int frames) {
        hitlagFrames = frames;
    }
    
    /**
     * Gets the remaining frames of hitlag freeze.
     * @return remaining hitlag frames
     */
    public int getHitlagFrames() {
        return hitlagFrames;
    }
    
    /**
     * Decreases the player's health, bypassing if invulnerable or shielded.
     * @param damage The amount of raw damage to take
     */
    @Override
    public void takeDamage(int damage) {
        if (invulnerable) return;
        
        // Shield absorbs hit entirely, deducting a charge instead of health
        if (shieldHits > 0) {
            shieldHits--;
            invulnerable = true;
            invulnerableTime = System.currentTimeMillis();
            return;
        }
        
        super.takeDamage(damage);
        
        // Grant temporary invincibility frames (i-frames) to prevent getting combo'd to death instantly
        invulnerable = true;
        invulnerableTime = System.currentTimeMillis();
        
        // Reset combo
        comboCount = 0;
        comboFinisherReady = false;
        
        // Interrupt current action
        attacking = false;
        dodging = false;
        afterimages.clear();
        state = PlayerState.HURT;
    }
    
    /**
     * Renders the player, including afterimages, status effects, and the character model.
     */
    @Override
    public void render() {
        // Clear invulnerable if time has passed, even if update() is paused/skipped
        if (invulnerable && state != PlayerState.DODGING && System.currentTimeMillis() - invulnerableTime > Constants.PLAYER_INVULNERABILITY_MS) {
            invulnerable = false;
        }

        // Render afterimages first so they draw behind the player
        for (int i = afterimages.size() - 1; i >= 0; i--) {
            Afterimage img = afterimages.get(i);
            renderPlayerSprite((int)img.x, (int)img.y, img.facingRight, (int)(img.alpha * 255));
        }
        
        // Flicker effect: Skip rendering for 50ms every 100ms when invulnerable
        if (invulnerable && alive && System.currentTimeMillis() % 100 < 50) {
            return;
        }
        
        int x = (int) position.x;
        int y = (int) position.y;
        
        // Apply the random jitter offset to the X coordinate if stunned
        if (stunned) {
            x += stunShakeOffset;
        }
        
        renderPlayerSprite(x, y, facingRight, 255);
        
        // Render an orange transparent circle over the player to signify burning
        if (burning) {
            fillOval(x - 5, y - 5, width + 10, height + 10, 
                                    color(255, 100, 0, 100));
        }
    }
    
    /**
     * Primary rendering routine to draw the Ninja using basic geometric shapes.
     * @param x X coordinate
     * @param y Y coordinate
     * @param facingRight Direction to draw the face/weapons
     * @param alpha Opacity (0-255)
     */
    private void renderPlayerSprite(int x, int y, boolean facingRight, int alpha) {
        // === Colors.BLACK NINJA SUIT ===
        Color suitColor = color(25, 25, 30, alpha); // Dark Colors.BLACK suit
        
        // Shift suit color slightly based on state for visual feedback
        if (blocking) {
            suitColor = color(35, 35, 45, alpha);
        } else if (dodging) {
            suitColor = color(20, 20, 25, alpha);
        } else if (stunned) {
            suitColor = color(50, 40, 40, alpha);
        }
        
        // === HEAD with mask ===
        // Head base (Colors.BLACK mask)
        fillOval(x + 12, y + 2, 26, 26, suitColor);
        
        // Eye slit area (darker band across the face)
        DrawRectangle(x + 14, y + 10, 22, 8, color(15, 15, 18, alpha));
        
        // Eyes (intense Colors.WHITE) - draw yellow dazed eyes if stunned
        if (stunned) {
            fillOval(x + 18, y + 11, 6, 6, color(200, 200, 100, alpha));
            fillOval(x + 26, y + 11, 6, 6, color(200, 200, 100, alpha));
        } else {
            // Draw eyes facing the proper direction
            if (facingRight) {
                fillOval(x + 24, y + 11, 8, 5, color(255, 255, 255, alpha));
                fillOval(x + 28, y + 12, 3, 3, color(0, 0, 0, alpha));
            } else {
                fillOval(x + 18, y + 11, 8, 5, color(255, 255, 255, alpha));
                fillOval(x + 19, y + 12, 3, 3, color(0, 0, 0, alpha));
            }
        }
        
        // === BODY (Colors.BLACK suit) ===
        fillRoundRect(x + 10, y + 26, 30, 35, 8, suitColor);
        
        // === Colors.RED BELT ===
        DrawRectangle(x + 10, y + 48, 30, 6, color(180, 30, 30, alpha));
        // Belt knot
        DrawRectangle(x + 22, y + 47, 6, 8, color(150, 20, 20, alpha));
        
        // === ARMS ===
        if (attacking) {
            // Arm extended with SWORD
            if (facingRight) {
                // Arm
                fillRoundRect(x + 38, y + 30, 15, 8, 4, suitColor);
                // Hand
                fillOval(x + 50, y + 29, 8, 10, color(60, 60, 65, alpha));
                
                // === KATANA SWORD (slim) ===
                // Blade (silver, slim)
                DrawRectangle(x + 56, y + 32, 40, 3, color(200, 200, 210, alpha));
                // Blade edge highlight (a 1px bright line across the top)
                DrawLine(x + 56, y + 32, x + 96, y + 32, color(230, 230, 240, alpha));
                // Blade tip (triangle polygon)
                int[] tipX = {x + 96, x + 102, x + 96};
                int[] tipY = {y + 32, y + 33, y + 35};
                fillPolygon(tipX, tipY, 3, color(200, 200, 210, alpha));
                // Handle (dark with Colors.RED wrap)
                DrawRectangle(x + 48, y + 31, 10, 5, color(40, 20, 20, alpha));
                for (int i = 0; i < 3; i++) {
                    DrawLine(x + 49 + i*3, y + 31, x + 51 + i*3, y + 36, color(150, 30, 30, alpha));
                }
                // Guard (tsuba)
                fillOval(x + 54, y + 30, 4, 7, color(80, 70, 60, alpha));
            } else {
                // Arm
                fillRoundRect(x - 3, y + 30, 15, 8, 4, suitColor);
                // Hand
                fillOval(x - 8, y + 29, 8, 10, color(60, 60, 65, alpha));
                
                // === KATANA SWORD (slim) - facing left ===
                DrawRectangle(x - 46, y + 32, 40, 3, color(200, 200, 210, alpha));
                DrawLine(x - 46, y + 32, x - 6, y + 32, color(230, 230, 240, alpha));
                // Blade tip
                int[] tipX = {x - 46, x - 52, x - 46};
                int[] tipY = {y + 32, y + 33, y + 35};
                fillPolygon(tipX, tipY, 3, color(200, 200, 210, alpha));
                // Handle
                DrawRectangle(x - 8, y + 31, 10, 5, color(40, 20, 20, alpha));
                for (int i = 0; i < 3; i++) {
                    DrawLine(x - 7 + i*3, y + 31, x - 5 + i*3, y + 36, color(150, 30, 30, alpha));
                }
                // Guard
                fillOval(x - 8, y + 30, 4, 7, color(80, 70, 60, alpha));
            }
        } else {
            // Normal arms resting at sides
            fillRoundRect(x + 5, y + 28, 8, 22, 4, suitColor);
            fillRoundRect(x + 37, y + 28, 8, 22, 4, suitColor);
            // Hands
            fillOval(x + 5, y + 47, 8, 8, color(60, 60, 65, alpha));
            fillOval(x + 37, y + 47, 8, 8, color(60, 60, 65, alpha));
            
            // Sword resting on back (sheathed)
            Color sheathColor = color(30, 15, 15, alpha);
            if (facingRight) {
                DrawRectangle(x + 8, y + 10, 4, 45, sheathColor);
            } else {
                DrawRectangle(x + 38, y + 10, 4, 45, sheathColor);
            }
        }
        
        // === LEGS (Colors.BLACK pants) with running animation ===
        boolean isMoving = (state == PlayerState.WALKING);
        int legOffset1 = 0, legOffset2 = 0;
        
        if (isMoving && onGround) {
            // Modulo math creates a repeating 300ms cycle.
            // Math.sin creates a smooth oscillation from -1 to 1 based on the cycle progress (0 to 2 PI).
            // Multiplying by 6 gives a leg swing offset between -6px and +6px.
            // Adding 0.5 to the runCycle offsets the second leg's wave by half a cycle, so the legs swing opposite to each other.
            double runCycle = (System.currentTimeMillis() % 300) / 300.0;
            legOffset1 = (int)(Math.sin(runCycle * Math.PI * 2) * 6);
            legOffset2 = (int)(Math.sin((runCycle + 0.5) * Math.PI * 2) * 6);
        }
        
        // Left leg
        fillRoundRect(x + 12 + legOffset1, y + 54, 10, 22, 4, suitColor);
        // Right leg
        fillRoundRect(x + 28 + legOffset2, y + 54, 10, 22, 4, suitColor);
        
        // === Colors.WHITE SHOES (tabi) ===
        Color shoeColor = color(240, 240, 245, alpha);
        fillRoundRect(x + 10 + legOffset1, y + 73, 14, 7, 4, shoeColor);
        fillRoundRect(x + 26 + legOffset2, y + 73, 14, 7, 4, shoeColor);
        // Shoe split (tabi style toe separation)
        Color shoeSplitColor = color(200, 200, 205, alpha);
        DrawLine(x + 17 + legOffset1, y + 73, x + 17 + legOffset1, y + 78, shoeSplitColor);
        DrawLine(x + 33 + legOffset2, y + 73, x + 33 + legOffset2, y + 78, shoeSplitColor);
    }
    
    // Simple Getters for internal state
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
    
    /**
     * Gets the remaining time of the speed boost.
     * @return ms remaining, or 0 if inactive
     */
    public long getSpeedBoostRemainingMs() {
        if (!hasSpeedBoost) return 0;
        return Math.max(0, speedBoostEndTime - System.currentTimeMillis());
    }
    
    /**
     * Gets the remaining time of the damage boost.
     * @return ms remaining, or 0 if inactive
     */
    public long getDamageBoostRemainingMs() {
        if (!hasDamageBoost) return 0;
        return Math.max(0, damageBoostEndTime - System.currentTimeMillis());
    }
    
    /**
     * Restores health up to the maximum limit.
     * @param amount Health to add
     */
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    /**
     * Grants a speed boost power-up effect.
     * @param durationMs Duration of the boost in milliseconds
     */
    public void applySpeedBoost(int durationMs) {
        hasSpeedBoost = true;
        speedBoostEndTime = System.currentTimeMillis() + durationMs;
    }
    
    /**
     * Grants a damage boost power-up effect.
     * @param durationMs Duration of the boost in milliseconds
     */
    public void applyDamageBoost(int durationMs) {
        hasDamageBoost = true;
        damageBoostEndTime = System.currentTimeMillis() + durationMs;
    }
    
    /**
     * Grants an invulnerability shield that absorbs a specific number of hits.
     * @param hits Number of attacks the shield can absorb
     */
    public void applyShield(int hits) {
        shieldHits = hits;
    }
    
    /**
     * Checks power-up expiration times and removes effects if necessary.
     */
    public void updatePowerUps() {
        long now = System.currentTimeMillis();
        if (hasSpeedBoost && now > speedBoostEndTime) {
            hasSpeedBoost = false;
        }
        if (hasDamageBoost && now > damageBoostEndTime) {
            hasDamageBoost = false;
        }
    }
    
    /**
     * Resolves collisions with physical platforms in the level.
     * Checks if the player's horizontal bounding box intersects the platform, 
     * and if their vertical feet align with the platform's surface.
     * @param platform The rectangle geometry of the platform to check against
     */
    public void checkPlatformCollision(Rectangle platform) {
        double playerBottom = position.y + height;
        double playerCenterX = position.x + width / 2;
        boolean horizontallyOnPlatform = playerCenterX >= platform.x() && playerCenterX <= platform.x() + platform.width();
        
        // Check if player is positioned over the platform
        if (horizontallyOnPlatform) {
            // If feet are within 5 pixels of the surface and velocity is neutral or downwards, snap to the surface
            if (Math.abs(playerBottom - platform.y()) < 5 && verticalVelocity >= 0) {
                position.y = platform.y() - height;
                verticalVelocity = 0;
                onGround = true;
                onPlatform = true;
            }
            // If heavily falling (velocity > 0) and passing through the top 20 pixels of the platform, snap to the surface
            else if (verticalVelocity > 0 && playerBottom >= platform.y() && playerBottom <= platform.y() + 20) {
                position.y = platform.y() - height;
                verticalVelocity = 0;
                onGround = true;
                onPlatform = true;
            }
        }
    }
    
    /**
     * Checks if the player is currently standing on a solid surface.
     * @return true if standing, false if airborne
     */
    public boolean isOnGround() {
        return onGround;
    }
    
    /**
     * Explicitly sets the ground state flag.
     * @param onGround true to set player as standing, false for airborne
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
