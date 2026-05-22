package game.state;
import java.io.File; // File system operations
import java.io.FileWriter; // Imports FileWriter functionality
import java.io.IOException; // Input/output error handling
import java.io.PrintWriter; // Writing text to files
import java.util.Scanner; // Imports Scanner functionality

/**
  Load high score from file. Returns 0 if file doesn't exist or is invalid.
  Save high score to file.
 */
public class HighScoreManager {
    private int highScore;
    private static final String HIGH_SCORE_FILE = "E:\\Nust Assignments\\Blade of the shinobi\\highscore.txt";
    
    public HighScoreManager() {
        this.highScore = loadHighScore();
    }
    
    public int getHighScore() { return highScore; }
    
    /**
     * Check if score beats high score, save if so.
     * @return true if it's a new high score
     */
    public boolean checkAndSaveHighScore(int score) {
        boolean isNew = (score > highScore);
        if (isNew) {
            highScore = score;
            saveHighScore(highScore);
        }
        return isNew;
    }
    
    private int loadHighScore() {
        try {
            File file = new File(HIGH_SCORE_FILE);
            if (!file.exists()) {
                // Create file with default value of 0
                PrintWriter writer = new PrintWriter(file);
                writer.print(0);
                writer.close();
                return 0;
            }
            Scanner scanner = new Scanner(file);
            int hs = 0;
            if (scanner.hasNextInt()) {
                hs = scanner.nextInt();
            }
            scanner.close();
            return hs;
        } catch (IOException e) {
            System.out.println("[GameEngine] Error loading high score: " + e.getMessage());
            return 0;
        }
    }
    
    private void saveHighScore(int score) {
        try {
            PrintWriter writer = new PrintWriter(HIGH_SCORE_FILE);
            writer.print(score);
            writer.close();
            System.out.println("[GameEngine] New high score saved: " + score);
        } catch (IOException e) {
            System.out.println("[GameEngine] Error saving high score: " + e.getMessage());
        }
    }
}
