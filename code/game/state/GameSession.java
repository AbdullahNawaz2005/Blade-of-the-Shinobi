package game.state;
import utils.Constants; 
import utils.Difficulty; 





public class GameSession {
    
    public int currentLevel = 1;
    public int score = 0;
    public int wave = 0;
    public int enemiesPerWave = Constants.BASE_ENEMIES_PER_WAVE;
    public int bossCount = 0;
    public int killCount = 0;
    
    
    public Difficulty difficulty = Difficulty.NORMAL;
    
    
    public boolean comboFinisherActive;
    public int comboFinisherFlashTimer;
    public boolean showFinisherText;
    public long finisherTextStartTime;
    
    
    public boolean firstShieldBearerKilled = false;
    
    
    public boolean showHitboxes = false;
    
    
    public boolean bossKilledSoundPending = false;
    public long bossKilledSoundTime = 0;
    
    
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
