package game.state;
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.PrintWriter; 
import java.util.Scanner; 





public class HighScoreManager {
    private int highScore;
    private static final String HIGH_SCORE_FILE = "E:\\Nust Assignments\\Blade of the shinobi\\highscore.txt";
    
    public HighScoreManager() {
        this.highScore = loadHighScore();
    }
    
    public int getHighScore() { return highScore; }
    
    



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
