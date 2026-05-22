package game.state;
import utils.Constants; // Global game constants
import utils.Difficulty; // Difficulty settings

/**
 * Mutable game session data: score, wave, killCount, difficulty, and cross-cutting combat state.
 * Used as a shared data bag to avoid excessive parameter passing between systems.
 */
public class GameSession {
    // Level info
    public int currentLevel = 1;
    public int score = 0;
    public int wave = 0;
    public int enemiesPerWave = Constants.BASE_ENEMIES_PER_WAVE;
    public int bossCount = 0;
    public int killCount = 0;
    
    // Difficulty
    public Difficulty difficulty = Difficulty.NORMAL;
    
    // Combo finisher state (shared between CombatSystem and renderers)
    public boolean comboFinisherActive;
    public int comboFinisherFlashTimer;
    public boolean showFinisherText;
    public long finisherTextStartTime;
    
    // ShieldBearer hint tracking
    public boolean firstShieldBearerKilled = false;
    
    // Debug flags
    public boolean showHitboxes = false;
    
    // Boss sound delay
    public boolean bossKilledSoundPending = false;
    public long bossKilledSoundTime = 0;
    
    // Game Over menu state
    public boolean isNewHighScore = false;
    
    public void reset() {
        score = 0;
        killCount = 0;
        wave = 0;
        bossCount = 0;
        currentLevel = 1;
        comboFinisherActive = false;
        showFinisherText = false;
    }
    
    public void cycleDifficulty() {
        if (difficulty == Difficulty.EASY) difficulty = Difficulty.NORMAL;
        else if (difficulty == Difficulty.NORMAL) difficulty = Difficulty.HARD;
        else difficulty = Difficulty.EASY;
    }
}
