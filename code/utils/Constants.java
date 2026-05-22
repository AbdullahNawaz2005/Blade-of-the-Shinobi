package utils;

/**
 * Game constants - centralized configuration
 * Screen dimensions can be updated at runtime for fullscreen support
 * 
 * Note: Colors are stored as int arrays [R, G, B] for Raylib compatibility
 */
public class Constants {
    // Window settings (can be updated for fullscreen)
    public static final String GAME_TITLE = "Blade of the Shinobi";
    public static int WINDOW_WIDTH = 1024;
    public static int WINDOW_HEIGHT = 768;
    
    // Update screen dimensions for fullscreen mode
    public static void setScreenDimensions(int width, int height) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
    }
    
    // Game settings
    public static final int TARGET_FPS = 60;
    public static final double TARGET_TIME = 1000000000.0 / TARGET_FPS; // nanoseconds per frame
    
    // Player settings
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 80;
    public static final double PLAYER_SPEED = 5.0;
    public static final int PLAYER_MAX_HEALTH = 100;
    public static final int PLAYER_MAX_STAMINA = 100;
    public static final int PLAYER_INVULNERABILITY_MS = 500;
    public static final int PLAYER_STUN_DURATION_MS = 1200;
    
    // Combat settings (simplified - only special attack remains)
    public static final int SPECIAL_ATTACK_DAMAGE = 40;
    public static final int SPECIAL_ATTACK_STAMINA = 30;
    public static final int SPECIAL_ATTACK_DURATION_MS = 300;
    public static final int SPECIAL_ATTACK_WIDTH = 100;
    public static final int SPECIAL_ATTACK_HEIGHT = 80;
    public static final int SPECIAL_ATTACK_COOLDOWN_MS = 200;
    
    // Kunai settings
    public static final int KUNAI_DAMAGE = 15;
    public static final double KUNAI_SPEED = 15.0;
    
    // Dodge settings
    public static final int DODGE_STAMINA_COST = 20;
    public static final int DODGE_DURATION_MS = 300;
    public static final double DODGE_SPEED = 15.0;
    
    // Block settings
    public static final int BLOCK_STAMINA_COST = 10;
    public static final double BLOCK_MOVEMENT_MULT = 0.3;
    public static final int BLOCK_KNOCKBACK_DISTANCE = 120;
    public static final int BLOCK_KNOCKBACK_FRAMES = 10;
    
    // Combo settings
    public static final double COMBO_TIMEOUT = 2.0; // seconds
    public static final double COMBO_3_MULTIPLIER = 1.5;
    public static final double COMBO_5_MULTIPLIER = 2.0;
    public static final double COMBO_7_MULTIPLIER = 3.0;
    public static final double COMBO_FINISHER_MULTIPLIER = 3.0;
    public static final int COMBO_FINISHER_THRESHOLD = 7;
    public static final int COMBO_FINISHER_FLASH_FRAMES = 20;
    public static final int COMBO_FINISHER_TEXT_DURATION_MS = 1500;
    
    // Hitlag settings
    public static final int HITLAG_FRAMES = 3;
    
    // Hit flash settings
    public static final int HIT_FLASH_FRAMES = 3;
    
    // Death particle settings
    public static final int DEATH_PARTICLE_COUNT = 10;
    public static final int DEATH_PARTICLE_MIN_SIZE = 4;
    public static final int DEATH_PARTICLE_MAX_SIZE = 8;
    public static final int DEATH_PARTICLE_DURATION_FRAMES = 25;
    public static final double DEATH_PARTICLE_GRAVITY = 0.4;
    
    // Afterimage settings
    public static final int AFTERIMAGE_INTERVAL_FRAMES = 3;
    public static final int AFTERIMAGE_MAX_COUNT = 4;
    public static final float[] AFTERIMAGE_ALPHAS = {0.5f, 0.35f, 0.2f, 0.1f};
    
    // Physics - tuned for smooth jump arc
    public static final double GRAVITY = 0.6;
    public static final double JUMP_FORCE = -14.0;
    public static final double AIR_CONTROL = 0.8;
    public static final double TERMINAL_VELOCITY = 15.0;
    
    // Platform definitions
    public static final Platform[] PLATFORMS = {
        new Platform(420, 480, 180, 20),  // Center platform (original)
        new Platform(80, 400, 140, 20),   // Left mid platform
        new Platform(760, 420, 140, 20)   // Right platform
    };
    
    // Crate settings
    public static final int CRATE_WIDTH = 40;
    public static final int CRATE_HEIGHT = 40;
    public static final int CRATE_HP = 2;
    public static final int CRATES_PER_WAVE = 2;
    public static final int CRATE_PARTICLE_COUNT = 8;
    
    // Enemy settings - Grunt
    public static final int GRUNT_WIDTH = 40;
    public static final int GRUNT_HEIGHT = 60;
    public static final int GRUNT_HEALTH = 30;
    public static final int GRUNT_DAMAGE = 8;
    public static final double GRUNT_SPEED = 2.5;
    public static final double GRUNT_DETECTION_RANGE = 1500;
    public static final double GRUNT_ATTACK_RANGE = 50;
    public static final int GRUNT_ATTACK_COOLDOWN_MS = 800;
    public static final int GRUNT_SCORE = 50;
    
    // Enemy settings - Archer
    public static final int ARCHER_WIDTH = 35;
    public static final int ARCHER_HEIGHT = 55;
    public static final int ARCHER_HEALTH = 20;
    public static final int ARCHER_DAMAGE = 12;
    public static final double ARCHER_SPEED = 1.5;
    public static final double ARCHER_DETECTION_RANGE = 1500;
    public static final double ARCHER_ATTACK_RANGE = 350;
    public static final double ARCHER_MIN_RANGE = 150;
    public static final int ARCHER_SHOOT_COOLDOWN_MS = 2000;
    public static final int ARCHER_SCORE = 75;
    public static final double ARCHER_ARROW_SPEED = 8.0;
    public static final int ARCHER_FLAMING_WAVE = 6;
    
    // Burn DoT settings
    public static final int BURN_DAMAGE_PER_TICK = 5;
    public static final int BURN_TICK_INTERVAL_MS = 1000;
    public static final int BURN_DURATION_MS = 3000;
    
    // Enemy settings - Samurai
    public static final int SAMURAI_WIDTH = 50;
    public static final int SAMURAI_HEIGHT = 75;
    public static final int SAMURAI_HEALTH = 80;
    public static final int SAMURAI_DAMAGE = 20;
    public static final double SAMURAI_SPEED = 1.2;
    public static final double SAMURAI_DETECTION_RANGE = 1500;
    public static final double SAMURAI_ATTACK_RANGE = 70;
    public static final int SAMURAI_ATTACK_COOLDOWN_MS = 1500;
    public static final int SAMURAI_ATTACK_DURATION_MS = 500;
    public static final int SAMURAI_BLOCK_DURATION_MS = 1500;
    public static final int SAMURAI_SCORE = 150;
    public static final double SAMURAI_BLOCK_CHANCE = 0.3;
    public static final double SAMURAI_BLOCK_DAMAGE_MULT = 0.25;
    
    // Enemy settings - ShieldBearer
    public static final int SHIELDBEARER_WIDTH = 45;
    public static final int SHIELDBEARER_HEIGHT = 70;
    public static final int SHIELDBEARER_HEALTH = 70;
    public static final int SHIELDBEARER_DAMAGE = 15;
    public static final double SHIELDBEARER_SPEED = 2.0;
    public static final double SHIELDBEARER_DETECTION_RANGE = 1500;
    public static final double SHIELDBEARER_ATTACK_RANGE = 55;
    public static final int SHIELDBEARER_ATTACK_COOLDOWN_MS = 1200;
    public static final int SHIELDBEARER_SCORE = 125;
    public static final int SHIELDBEARER_SPAWN_WAVE = 3;
    
    // Enemy settings - Bomber
    public static final int BOMBER_WIDTH = 40;
    public static final int BOMBER_HEIGHT = 60;
    public static final int BOMBER_HEALTH = 45;
    public static final double BOMBER_SPEED = 3.0;
    public static final double BOMBER_DETECTION_RANGE = 1500;
    public static final double BOMBER_ATTACK_RANGE = 200;
    public static final int BOMBER_SCORE = 100;
    public static final int BOMBER_SPAWN_WAVE = 5;
    public static final int BOMB_FUSE_MS = 2500;
    public static final int BOMB_EXPLOSION_RADIUS = 80;
    public static final int BOMB_DAMAGE = 35;
    public static final int BOMB_EXPLOSION_FRAMES = 30;
    
    // Boss settings
    public static final int BOSS_WIDTH = 70;
    public static final int BOSS_HEIGHT = 90;
    public static final int BOSS_HEALTH = 500;
    public static final int BOSS_DAMAGE_PHASE1 = 25;
    public static final int BOSS_DAMAGE_PHASE2 = 27;
    public static final int BOSS_DAMAGE_PHASE3 = 30;
    public static final double BOSS_SPEED_PHASE1 = 3.0;
    public static final double BOSS_SPEED_PHASE2 = 3.5;
    public static final double BOSS_SPEED_PHASE3 = 4.5;
    public static final double BOSS_DETECTION_RANGE = 1500;
    public static final double BOSS_ATTACK_RANGE = 90;
    public static final int BOSS_ATTACK_COOLDOWN_PHASE1_MS = 600;
    public static final int BOSS_ATTACK_COOLDOWN_PHASE2_MS = 455;
    public static final int BOSS_ATTACK_COOLDOWN_PHASE3_MS = 350;
    public static final int BOSS_ATTACK_DURATION_MS = 350;
    public static final int BOSS_TELEPORT_COOLDOWN_MS = 3500;
    public static final int BOSS_TELEPORT_ANIMATION_FRAMES = 20;
    public static final int BOSS_SCORE = 1000;
    public static final double BOSS_PHASE2_HEALTH_THRESHOLD = 0.5;
    public static final double BOSS_PHASE3_HEALTH_THRESHOLD = 0.3;
    public static final double BOSS_MINION_SUMMON_THRESHOLD = 0.3;
    public static final int BOSS_MINION_COUNT = 4;
    public static final int BOSS_PHASE_FLASH_FRAMES = 25;
    
    // Boss special attacks
    public static final double BOSS_GRAB_RANGE = 60;
    public static final int BOSS_GRAB_COOLDOWN_MS = 8000;
    public static final int BOSS_SHOCKWAVE_RADIUS = 300;
    public static final int BOSS_SHOCKWAVE_DAMAGE = 20;
    public static final int BOSS_SHOCKWAVE_COOLDOWN_MS = 6000;
    public static final double BOSS_SHOCKWAVE_KNOCKUP_FORCE = -10.0;
    
    // Boss names and colors (stored as int arrays [R, G, B] for Raylib)
    public static final String[] BOSS_NAMES = {
        "Shadow Assassin",
        "Violet Demon",
        "Crimson Warlord",
        "Jade Phantom",
        "Obsidian Master"
    };
    
    public static final int[][] BOSS_COLORS = {
        {15, 15, 15},    // Colors.BLACK
        {80, 0, 100},    // Purple
        {120, 20, 20},   // Crimson
        {20, 80, 60},    // Jade
        {30, 30, 40}     // Obsidian
    };
    
    // Wave settings
    public static final int WAVE_TRANSITION_DURATION_MS = 3000;
    public static final int WAVE_ANNOUNCE_FADE_IN_MS = 500;
    public static final int WAVE_ANNOUNCE_HOLD_MS = 1500;
    public static final int WAVE_ANNOUNCE_FADE_OUT_MS = 500;
    public static final int BOSS_WAVE_INTERVAL = 5;
    public static final int BASE_ENEMIES_PER_WAVE = 3;
    
    // Wave subtitles
    public static final String[] WAVE_SUBTITLES = {
        "The Grunt Patrol Arrives",
        "More Warriors Approach",
        "Shield Bearers Join the Fray",
        "The Enemies Grow Stronger",
        "A Shadow Ninja Master Appears!", // Boss wave
        "The Battle Intensifies",
        "Bombers Take Position",
        "An Army Descends",
        "No Rest for the Blade",
        "A Demon Lord Awakens!", // Boss wave
        "The Horde Advances",
        "Elite Forces Deploy",
        "Flames Light the Night",
        "The Final Push Begins",
        "The Ultimate Challenge Awaits!" // Boss wave
    };
    
    // Day/Night cycle
    public static final int DAY_NIGHT_TRANSITION_MS = 3000;
    public static final int BOSSES_PER_CYCLE = 2;
    
    // Vignette/lighting settings
    public static final int VIGNETTE_ALPHA = 120;
    public static final int PLAYER_LIGHT_RADIUS = 220;
    public static final int BOSS_LIGHT_RADIUS = 150;
    
    // Power-up settings
    public static final int POWERUP_WIDTH = 30;
    public static final int POWERUP_HEIGHT = 30;
    public static final int POWERUP_LIFETIME_MS = 10000;
    public static final int HEALTH_POTION_HEAL = 30;
    public static final int SPEED_BOOST_DURATION_MS = 5000;
    public static final double SPEED_BOOST_MULT = 1.25;
    public static final int DAMAGE_BOOST_DURATION_MS = 6000;
    public static final double DAMAGE_BOOST_MULT = 1.25;
    public static final int SHIELD_HITS = 2;
    
    // Knockback settings
    public static final double ENEMY_KNOCKBACK_FORCE = 6.0;
    public static final int ENEMY_KNOCKBACK_DURATION_MS = 200;
    
    // Screen shake settings
    public static final int PLAYER_HURT_SHAKE_INTENSITY = 8;
    public static final int PLAYER_HURT_SHAKE_DURATION = 10;
    public static final int PLAYER_DEATH_SHAKE_INTENSITY = 20;
    public static final int PLAYER_DEATH_SHAKE_DURATION = 30;
    public static final int EXPLOSION_SHAKE_INTENSITY = 12;
    public static final int EXPLOSION_SHAKE_DURATION = 15;
    
    // Colors (as RGB values for easy use)
    public static final int[] COLOR_STAMINA_GREEN = {50, 200, 50};
    public static final int[] COLOR_COMBO_GOLD = {255, 200, 50};
    public static final int[] COLOR_BACKGROUND = {20, 20, 30};
    public static final int[] COLOR_BACKGROUND_DAY = {135, 180, 220};
    
    // UI settings
    public static final int HUD_PANEL_ALPHA = 180;
    public static final int HUD_PANEL_CORNER_RADIUS = 8;
}
