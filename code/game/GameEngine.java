package game;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import java.util.ArrayList; // For dynamic arrays
import java.util.List; // List interface for collections
import java.util.Random; // Random number generator
import input.InputHandler; // Keyboard input handling
import input.MouseHandler; // Mouse input handling
import entities.Player; // Player character
import entities.Enemy; // Base enemy class
import entities.Projectile; // Thrown weapons
import entities.enemies.Archer; // Ranged enemy
import entities.enemies.Boss; // Boss enemy
import entities.enemies.Grunt; // Basic enemy
import powerups.PowerUp; // Base item class
import audio.SoundManager; // Audio playback
import utils.Constants; // Global game constants
import utils.Platform; // Platform structures
import game.state.*; // Imports state functionality
import game.effects.*; // Imports effects functionality
import game.entities.Crate; // Breakable objects
import game.combat.*; // Imports combat functionality
import game.spawn.*; // Imports spawn functionality
import game.ui.*; // Imports ui functionality

/**
 * Main game engine - high-level coordinator.
 * Owns the main game state and delegates to focused subsystems for
 * combat, spawning, effects, and rendering.
 */
public class GameEngine {
    
    // Game state
    private GameState gameState;
    
    // Input
    private InputHandler input;
    private MouseHandler mouse;
    
    // Entities
    private Player player;
    private List<Enemy> enemies;
    private List<Projectile> projectiles;
    private List<PowerUp> powerUps;
    private List<Crate> crates;
    
    // Shared state
    private GameSession session;
    private WaveManager waveManager;
    private HighScoreManager highScoreManager;
    private Random random;
    
    // Effects
    private ParticleManager particleManager;
    private CherryPetalManager cherryPetalManager;

    private DamageFlash damageFlash;
    
    // Combat systems
    private CombatSystem combatSystem;
    private ProjectileSystem projectileSystem;
    private BossAttackSystem bossAttackSystem;
    private BomberExplosionSystem bomberExplosionSystem;
    
    // Spawners
    private EnemySpawner enemySpawner;
    private PowerUpSpawner powerUpSpawner;
    private CrateSpawner crateSpawner;
    
    // Sound manager
    private SoundManager soundManager;
    
    // UI Controllers
    private MenuController menuController;
    private PauseMenuController pauseMenuController;
    private GameOverMenuController gameOverMenuController;
    
    // Renderers
    private MenuRenderer menuRenderer;
    private CreditsRenderer creditsRenderer;
    private GameRenderer gameRenderer;
    private HUDRenderer hudRenderer;
    private PauseOverlayRenderer pauseOverlayRenderer;
    private GameOverRenderer gameOverRenderer;
    private WaveMessageRenderer waveMessageRenderer;
    
    // Pause button constants (needed for click detection in updatePlaying)
    private static final int PAUSE_BTN_X = 20;
    private static final int PAUSE_BTN_Y = 12;
    private static final int PAUSE_BTN_SIZE = 44;
    
    public GameEngine(InputHandler input, MouseHandler mouse) {
        this.input = input;
        this.mouse = mouse;
        
        // Initialize shared state
        gameState = GameState.MENU;
        session = new GameSession();
        waveManager = new WaveManager();
        random = new Random();
        
        // Initialize entity lists
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        powerUps = new ArrayList<>();
        crates = new ArrayList<>();
        
        // Initialize sound manager
        soundManager = SoundManager.getInstance();
        soundManager.startBackgroundMusic();
        
        // Initialize high score
        highScoreManager = new HighScoreManager();
        
        // Initialize effects
        particleManager = new ParticleManager();
        cherryPetalManager = new CherryPetalManager(random);

        damageFlash = new DamageFlash();
        
        // Initialize combat systems
        combatSystem = new CombatSystem();
        projectileSystem = new ProjectileSystem();
        bossAttackSystem = new BossAttackSystem();
        bomberExplosionSystem = new BomberExplosionSystem();
        
        // Initialize spawners
        enemySpawner = new EnemySpawner();
        powerUpSpawner = new PowerUpSpawner();
        crateSpawner = new CrateSpawner();
        
        // Initialize UI controllers
        menuController = new MenuController();
        pauseMenuController = new PauseMenuController();
        gameOverMenuController = new GameOverMenuController();
        
        // Initialize renderers
        menuRenderer = new MenuRenderer();
        creditsRenderer = new CreditsRenderer();
        gameRenderer = new GameRenderer();
        hudRenderer = new HUDRenderer();
        pauseOverlayRenderer = new PauseOverlayRenderer();
        gameOverRenderer = new GameOverRenderer();
        waveMessageRenderer = new WaveMessageRenderer();
        
        // Create player
        player = new Player(Constants.WINDOW_WIDTH / 2.0 - Constants.PLAYER_WIDTH / 2.0,
                           Constants.WINDOW_HEIGHT - 150);
    }
    
    /**
     * Main update loop for the GameEngine.
     * Updates music, handles delayed sounds, and delegates to state-specific update methods.
     */
    public void update() {
        soundManager.updateMusic();
        
        if (session.bossKilledSoundPending && System.currentTimeMillis() - session.bossKilledSoundTime >= 1500) {
            soundManager.playSound(SoundManager.SOUND_BOSS_KILLED);
            session.bossKilledSoundPending = false;
        }
        
        switch (gameState) {
            case MENU:
                updateMenu();
                break;
            case CREDITS:
                updateCredits();
                break;
            case PLAYING:
                updatePlaying();
                break;
            case PAUSED:
                updatePaused();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
            case VICTORY:
                updateVictory();
                break;
            case LEVEL_TRANSITION:
                updateLevelTransition();
                break;
        }
    }
    
    private void updateMenu() {
        cherryPetalManager.update(random);
        MenuController.MenuAction action = menuController.updateMenu(input, mouse);
        switch (action) {
            case START_GAME:
                gameState = GameState.PLAYING;
                soundManager.pauseBackgroundMusic();
                break;
            case CYCLE_DIFFICULTY:
                session.cycleDifficulty();
                break;
            case SHOW_CREDITS:
                gameState = GameState.CREDITS;
                break;
            case EXIT:
                System.exit(0);
                break;
            default: break;
        }
    }
    
    private void updateCredits() {
        cherryPetalManager.update(random);
        if (menuController.updateCredits(input, mouse)) {
            gameState = GameState.MENU;
        }
    }
    
    /**
     * Core gameplay update loop.
     * Handles spawning, entity updates, collisions, and state transitions during active play.
     */
    private void updatePlaying() {
        // Update cherry petals during boss fights
        boolean hasBoss = false;
        for (Enemy e : enemies) {
            if (e instanceof Boss) {
                hasBoss = true;
                break;
            }
        }
        if (hasBoss) {
            cherryPetalManager.update(random);
        }
        
        // Check for exit (ESC)
        if (input.isKeyJustPressed(KEY_ESCAPE)) {
            System.exit(0);
        }
        
        // Check for pause (P)
        if (input.isKeyJustPressed(KEY_P)) {
            gameState = GameState.PAUSED;
            soundManager.pauseBackgroundMusic();
            pauseMenuController.resetSelection();
            return;
        }
        
        // Check for pause button click (top-right)
        if (mouse.isLeftJustClicked()) {
            double mx = mouse.getX();
            double my = mouse.getY();
            int btnX = Constants.WINDOW_WIDTH - PAUSE_BTN_X - PAUSE_BTN_SIZE;
            if (mx >= btnX && mx <= btnX + PAUSE_BTN_SIZE &&
                my >= PAUSE_BTN_Y && my <= PAUSE_BTN_Y + PAUSE_BTN_SIZE) {
                gameState = GameState.PAUSED;
                soundManager.pauseBackgroundMusic();
                pauseMenuController.resetSelection();
                return;
            }
        }
        
        // Handle wave transition delay
        if (waveManager.waveTransitioning) {
            if (waveManager.isTransitionComplete()) {
                waveManager.endTransition();
                crates.clear();
                crateSpawner.spawnCrates(crates);
                enemySpawner.spawnWave(enemies, session, random, soundManager);
                waveManager.startAnnouncement();
            }
            // Update particles during transition
            particleManager.update();
            return;
        }
        
        // Update combo finisher flash
        if (session.comboFinisherFlashTimer > 0) {
            session.comboFinisherFlashTimer--;
        }
        
        // Update finisher text
        if (session.showFinisherText) {
            if (System.currentTimeMillis() - session.finisherTextStartTime > Constants.COMBO_FINISHER_TEXT_DURATION_MS) {
                session.showFinisherText = false;
            }
        }
        

        
        // Face nearest enemy when attacking
        if (input.specialAttack) {
            player.faceNearestEnemy(enemies);
        }
        
        // Track player health before update
        int healthBefore = player.getHealth();
        
        // Reset platform ground state before checking (handles walking off edges)
        player.resetPlatformGroundState();
        
        // Check platform collisions BEFORE update so gravity knows if we're grounded
        for (Platform p : Constants.PLATFORMS) {
            player.checkPlatformCollision(p.toRectangle());
        }
        
        // Update player
        player.update(input);
        player.updatePowerUps();
        
        // Check platform collisions again after movement (in case we landed)
        for (Platform p : Constants.PLATFORMS) {
            player.checkPlatformCollision(p.toRectangle());
        }
        
        // Check if player took damage
        if (player.getHealth() < healthBefore) {

            damageFlash.trigger();
            soundManager.playSound(SoundManager.SOUND_PLAYER_HURT);
        }
        
        // Check for kunai throw
        if (input.isKeyJustPressed(KEY_Q)) {
            projectileSystem.throwKunai(player, projectiles, soundManager);
        }
        
        // Update projectiles
        projectileSystem.update(projectiles, enemies, player, soundManager, session);
        
        // Update enemies
        updateEnemies();
        
        // Update powerups
        updatePowerUps();
        
        // Update particles
        particleManager.update();
        
        // Check combat
        combatSystem.checkMeleeHit(player, enemies, crates, soundManager, session, particleManager, random);
        
        // Check boss special attacks
        bossAttackSystem.checkBossSpecialAttacks(enemies, player);
        
        // Check bomber explosions
        bomberExplosionSystem.checkBomberExplosions(enemies, player, soundManager);
        
        // Check game over
        if (player.isDead()) {

            gameState = GameState.GAME_OVER;
            
            // Check and save high score
            session.isNewHighScore = highScoreManager.checkAndSaveHighScore(session.score);
            
            // Reset game over menu state
            gameOverMenuController.resetSelection();
        }
        
        // Check wave complete
        if (enemies.isEmpty() && !waveManager.waveTransitioning) {
            waveManager.startTransition();
        }
    }
    
    private void updateEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            
            // Update enemy
            enemy.update(player, 1.0 / 60.0);
            
            // Collect archer arrows
            if (enemy instanceof Archer) {
                Archer archer = (Archer) enemy;
                archer.setCurrentWave(session.wave);
                projectiles.addAll(archer.getArrows());
            }
            
            // Boss minion spawn check
            if (enemy instanceof Boss) {
                Boss boss = (Boss) enemy;
                if (boss.shouldSpawnMinions()) {
                    // Spawn minions
                    for (int m = 0; m < boss.getMinionSpawnCount(); m++) {
                        double mx = random.nextBoolean() ? 50 : Constants.WINDOW_WIDTH - 50;
                        Grunt minion = new Grunt(mx, Constants.WINDOW_HEIGHT - 150);
                        enemies.add(minion);
                    }
                }
                // Update boss minion count
                int minionCount = 0;
                for (Enemy e : enemies) {
                    if (!(e instanceof Boss) && e.isAlive()) {
                        minionCount++;
                    }
                }
                boss.setMinionCount(minionCount);
            }
            
            // Remove dead enemies
            if (enemy.isDead()) {
                session.score += enemy.getScoreValue();
                
                // Boss death handling
                if (enemy instanceof Boss) {
                    session.bossKilledSoundPending = true;
                    session.bossKilledSoundTime = System.currentTimeMillis();
                }
                
                // Increment kill counter
                session.killCount++;
                
                // Spawn death particles
                particleManager.spawnDeathParticles(enemy.getCenterX(), enemy.getCenterY(), random);
                
                // Power-up drop
                double dropChance = session.difficulty.dropChance;
                if (random.nextDouble() < dropChance) {
                    powerUpSpawner.spawnPowerUp(enemy.getPosition().x, enemy.getPosition().y, powerUps, random);
                }
                
                enemies.remove(i);
            }
        }
    }
    
    private void updatePowerUps() {
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.update();
            
            if (intersects(p.getHitbox(), player.getHitbox())) {
                p.applyTo(player);
                powerUps.remove(i);
                continue;
            }
            
            if (p.isExpired()) {
                powerUps.remove(i);
            }
        }
    }
    
    private void updatePaused() {
        PauseMenuController.PauseAction action = pauseMenuController.update(input, mouse, soundManager);
        switch (action) {
            case RESUME:
                gameState = GameState.PLAYING;
                soundManager.resumeBackgroundMusic();
                break;
            case MAIN_MENU:
                resetGameState();
                soundManager.stopBackgroundMusic();
                soundManager.startBackgroundMusic();
                gameState = GameState.MENU;
                break;
            case EXIT:
                System.exit(0);
                break;
            default: break;
        }
    }
    
    private void updateGameOver() {
        GameOverMenuController.GameOverAction action = gameOverMenuController.update(input, mouse);
        switch (action) {
            case RESTART:
                resetGameState();
                gameState = GameState.PLAYING;
                break;
            case MAIN_MENU:
                resetGameState();
                soundManager.stopBackgroundMusic();
                soundManager.startBackgroundMusic();
                gameState = GameState.MENU;
                break;
            default: break;
        }
    }
    
    // Reset game state without changing gameState
    private void resetGameState() {
        player = new Player(Constants.WINDOW_WIDTH / 2.0 - Constants.PLAYER_WIDTH / 2.0,
                           Constants.WINDOW_HEIGHT - 150);
        enemies.clear();
        projectiles.clear();
        powerUps.clear();
        crates.clear();
        particleManager.clear();
        session.reset();
        waveManager.reset();
    }
    
    private void updateVictory() {
        if (input.isKeyJustPressed(KEY_ENTER)) {
            session.currentLevel++;
            gameState = GameState.PLAYING;
        }
        
        // Return to menu from victory screen on ESC
        if (input.isKeyJustPressed(KEY_ESCAPE)) {
            soundManager.stopBackgroundMusic();
            soundManager.startBackgroundMusic();
            gameState = GameState.MENU;
        }
    }
    
    private void updateLevelTransition() {
        gameState = GameState.PLAYING;
    }
    
    /*
     * SCREEN SHAKE MECHANIC:
     * When a heavy impact occurs (e.g. boss hit, player death), shakeIntensity and shakeDuration are set.
     * During render(), if duration > 0, the entire coordinate system is translated by a random offset (-intensity to +intensity).
     * The offset is recalculated every frame for a chaotic shake, and the duration decrements until 0, restoring normal rendering.
     */
    /**
     * Main render loop for the GameEngine.
     * Applies camera translations (screen shake) and delegates rendering to entities and UI layers.
     */
    public void render() {
        switch (gameState) {
            case MENU:
                gameRenderer.renderBackground();
                gameRenderer.renderBattleground();
                menuRenderer.render(cherryPetalManager, highScoreManager.getHighScore(),
                                   session.difficulty, menuController.getMainMenuSelection(),
                                   menuController.getMenuCardScales());
                break;
            case CREDITS:
                gameRenderer.renderBackground();
                gameRenderer.renderBattleground();
                creditsRenderer.render(cherryPetalManager);
                break;
            case PLAYING:
                gameRenderer.renderPlaying(player, enemies, projectiles, powerUps, crates,
                    particleManager, damageFlash, session, waveManager, random,
                    waveMessageRenderer, hudRenderer, mouse);
                if (isBossActive()) {
                    cherryPetalManager.render();
                }
                break;
            case PAUSED:
                gameRenderer.renderPlaying(player, enemies, projectiles, powerUps, crates,
                    particleManager, damageFlash, session, waveManager, random,
                    waveMessageRenderer, hudRenderer, mouse);
                if (isBossActive()) {
                    cherryPetalManager.render();
                }
                pauseOverlayRenderer.render(pauseMenuController.getPauseMenuSelection(),
                                           pauseMenuController.getPauseCardScales());
                break;
            case GAME_OVER:
                gameRenderer.renderPlaying(player, enemies, projectiles, powerUps, crates,
                    particleManager, damageFlash, session, waveManager, random,
                    waveMessageRenderer, hudRenderer, mouse);
                if (isBossActive()) {
                    cherryPetalManager.render();
                }
                gameOverRenderer.renderGameOverOverlay(session, highScoreManager.getHighScore(),
                    session.isNewHighScore, gameOverMenuController.getGameOverMenuSelection(),
                    gameOverMenuController.getGameOverCardScales());
                break;
            case VICTORY:
                gameOverRenderer.renderVictory(session);
                break;
            case LEVEL_TRANSITION:
                gameOverRenderer.renderLevelTransition(session.currentLevel);
                break;
        }
    }
    
    // Helper method for rendering boss effects
    private boolean isBossActive() {
        for (Enemy e : enemies) {
            if (e instanceof Boss) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public GameState getGameState() { return gameState; }
    
    /**
     * Cleanup method for consistency with GameLoop
     */
    public void cleanup() {
        soundManager.cleanup();
    }
}
