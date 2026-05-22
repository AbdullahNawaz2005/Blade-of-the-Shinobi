package game;
import static utils.RaylibRenderer.*; 
import static com.raylib.Raylib.*; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.Random; 
import input.InputHandler; 
import input.MouseHandler; 
import entities.Player; 
import entities.Enemy; 
import entities.Projectile; 
import entities.enemies.Archer; 
import entities.enemies.Boss; 
import entities.enemies.Grunt; 
import powerups.PowerUp; 
import audio.SoundManager; 
import utils.Constants; 
import utils.Platform; 
import game.state.*; 
import game.effects.*; 
import game.entities.Crate; 
import game.combat.*; 
import game.spawn.*; 
import game.ui.*; 






public class GameEngine {
    
    
    private GameState gameState;
    
    
    private InputHandler input;
    private MouseHandler mouse;
    
    
    private Player player;
    private List<Enemy> enemies;
    private List<Projectile> projectiles;
    private List<PowerUp> powerUps;
    private List<Crate> crates;
    
    
    private GameSession session;
    private WaveManager waveManager;
    private HighScoreManager highScoreManager;
    private Random random;
    
    
    private ParticleManager particleManager;
    private CherryPetalManager cherryPetalManager;

    private DamageFlash damageFlash;
    
    
    private CombatSystem combatSystem;
    private ProjectileSystem projectileSystem;
    private BossAttackSystem bossAttackSystem;
    private BomberExplosionSystem bomberExplosionSystem;
    
    
    private EnemySpawner enemySpawner;
    private PowerUpSpawner powerUpSpawner;
    private CrateSpawner crateSpawner;
    
    
    private SoundManager soundManager;
    
    
    private MenuController menuController;
    private PauseMenuController pauseMenuController;
    private GameOverMenuController gameOverMenuController;
    
    
    private MenuRenderer menuRenderer;
    private CreditsRenderer creditsRenderer;
    private GameRenderer gameRenderer;
    private HUDRenderer hudRenderer;
    private PauseOverlayRenderer pauseOverlayRenderer;
    private GameOverRenderer gameOverRenderer;
    private WaveMessageRenderer waveMessageRenderer;
    
    
    private static final int PAUSE_BTN_X = 20;
    private static final int PAUSE_BTN_Y = 12;
    private static final int PAUSE_BTN_SIZE = 44;
    
    public GameEngine(InputHandler input, MouseHandler mouse) {
        this.input = input;
        this.mouse = mouse;
        
        
        gameState = GameState.MENU;
        session = new GameSession();
        waveManager = new WaveManager();
        random = new Random();
        
        
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        powerUps = new ArrayList<>();
        crates = new ArrayList<>();
        
        
        soundManager = SoundManager.getInstance();
        soundManager.startBackgroundMusic();
        
        
        highScoreManager = new HighScoreManager();
        
        
        particleManager = new ParticleManager();
        cherryPetalManager = new CherryPetalManager(random);

        damageFlash = new DamageFlash();
        
        
        combatSystem = new CombatSystem();
        projectileSystem = new ProjectileSystem();
        bossAttackSystem = new BossAttackSystem();
        bomberExplosionSystem = new BomberExplosionSystem();
        
        
        enemySpawner = new EnemySpawner();
        powerUpSpawner = new PowerUpSpawner();
        crateSpawner = new CrateSpawner();
        
        
        menuController = new MenuController();
        pauseMenuController = new PauseMenuController();
        gameOverMenuController = new GameOverMenuController();
        
        
        menuRenderer = new MenuRenderer();
        creditsRenderer = new CreditsRenderer();
        gameRenderer = new GameRenderer();
        hudRenderer = new HUDRenderer();
        pauseOverlayRenderer = new PauseOverlayRenderer();
        gameOverRenderer = new GameOverRenderer();
        waveMessageRenderer = new WaveMessageRenderer();
        
        
        player = new Player(Constants.WINDOW_WIDTH / 2.0 - Constants.PLAYER_WIDTH / 2.0,
                           Constants.WINDOW_HEIGHT - 150);
    }
    
    



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
    
    



    private void updatePlaying() {
        
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
        
        
        if (input.isKeyJustPressed(KEY_ESCAPE)) {
            System.exit(0);
        }
        
        
        if (input.isKeyJustPressed(KEY_P)) {
            gameState = GameState.PAUSED;
            soundManager.pauseBackgroundMusic();
            pauseMenuController.resetSelection();
            return;
        }
        
        
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
        
        
        if (waveManager.waveTransitioning) {
            if (waveManager.isTransitionComplete()) {
                waveManager.endTransition();
                crates.clear();
                crateSpawner.spawnCrates(crates);
                enemySpawner.spawnWave(enemies, session, random, soundManager);
                waveManager.startAnnouncement();
            }
            
            particleManager.update();
            return;
        }
        
        
        if (session.comboFinisherFlashTimer > 0) {
            session.comboFinisherFlashTimer--;
        }
        
        
        if (session.showFinisherText) {
            if (System.currentTimeMillis() - session.finisherTextStartTime > Constants.COMBO_FINISHER_TEXT_DURATION_MS) {
                session.showFinisherText = false;
            }
        }
        

        
        
        if (input.specialAttack) {
            player.faceNearestEnemy(enemies);
        }
        
        
        int healthBefore = player.getHealth();
        
        
        player.resetPlatformGroundState();
        
        
        for (Platform p : Constants.PLATFORMS) {
            player.checkPlatformCollision(p.toRectangle());
        }
        
        
        player.update(input);
        player.updatePowerUps();
        
        
        for (Platform p : Constants.PLATFORMS) {
            player.checkPlatformCollision(p.toRectangle());
        }
        
        
        if (player.getHealth() < healthBefore) {

            damageFlash.trigger();
            soundManager.playSound(SoundManager.SOUND_PLAYER_HURT);
        }
        
        
        if (input.isKeyJustPressed(KEY_Q)) {
            projectileSystem.throwKunai(player, projectiles, soundManager);
        }
        
        
        projectileSystem.update(projectiles, enemies, player, soundManager, session);
        
        
        updateEnemies();
        
        
        updatePowerUps();
        
        
        particleManager.update();
        
        
        combatSystem.checkMeleeHit(player, enemies, crates, soundManager, session, particleManager, random);
        
        
        bossAttackSystem.checkBossSpecialAttacks(enemies, player);
        
        
        bomberExplosionSystem.checkBomberExplosions(enemies, player, soundManager);
        
        
        if (player.isDead()) {

            gameState = GameState.GAME_OVER;
            
            
            session.isNewHighScore = highScoreManager.checkAndSaveHighScore(session.score);
            
            
            gameOverMenuController.resetSelection();
        }
        
        
        if (enemies.isEmpty() && !waveManager.waveTransitioning) {
            waveManager.startTransition();
        }
    }
    
    private void updateEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            
            
            enemy.update(player, 1.0 / 60.0);
            
            
            if (enemy instanceof Archer) {
                Archer archer = (Archer) enemy;
                archer.setCurrentWave(session.wave);
                projectiles.addAll(archer.getArrows());
            }
            
            
            if (enemy instanceof Boss) {
                Boss boss = (Boss) enemy;
                if (boss.shouldSpawnMinions()) {
                    
                    for (int m = 0; m < boss.getMinionSpawnCount(); m++) {
                        double mx = random.nextBoolean() ? 50 : Constants.WINDOW_WIDTH - 50;
                        Grunt minion = new Grunt(mx, Constants.WINDOW_HEIGHT - 150);
                        enemies.add(minion);
                    }
                }
                
                int minionCount = 0;
                for (Enemy e : enemies) {
                    if (!(e instanceof Boss) && e.isAlive()) {
                        minionCount++;
                    }
                }
                boss.setMinionCount(minionCount);
            }
            
            
            if (enemy.isDead()) {
                session.score += enemy.getScoreValue();
                
                
                if (enemy instanceof Boss) {
                    session.bossKilledSoundPending = true;
                    session.bossKilledSoundTime = System.currentTimeMillis();
                }
                
                
                session.killCount++;
                
                
                particleManager.spawnDeathParticles(enemy.getCenterX(), enemy.getCenterY(), random);
                
                
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
        
        
        if (input.isKeyJustPressed(KEY_ESCAPE)) {
            soundManager.stopBackgroundMusic();
            soundManager.startBackgroundMusic();
            gameState = GameState.MENU;
        }
    }
    
    private void updateLevelTransition() {
        gameState = GameState.PLAYING;
    }
    
    





    



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
    
    
    private boolean isBossActive() {
        for (Enemy e : enemies) {
            if (e instanceof Boss) {
                return true;
            }
        }
        return false;
    }

    
    public GameState getGameState() { return gameState; }
    
    


    public void cleanup() {
        soundManager.cleanup();
    }
}
