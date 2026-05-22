package game.ui;
import input.InputHandler; // Keyboard input handling
import input.MouseHandler; // Mouse input handling
import utils.Constants; // Global game constants

/**
 * Handles game over screen update logic.
 * Returns actions for GameEngine to execute.
 */
public class GameOverMenuController {
    
    public enum GameOverAction { NONE, RESTART, MAIN_MENU }
    
    private int gameOverMenuSelection = 0;
    private float[] gameOverCardScales = {1.0f, 1.0f};
    
    public int getGameOverMenuSelection() { return gameOverMenuSelection; }
    public float[] getGameOverCardScales() { return gameOverCardScales; }
    
    public void resetSelection() {
        gameOverMenuSelection = 0;
        gameOverCardScales = new float[]{1.0f, 1.0f};
    }
    
    public GameOverAction update(InputHandler input, MouseHandler mouse) {
        int screenW = Constants.WINDOW_WIDTH;
        int screenH = Constants.WINDOW_HEIGHT;
        
        // Button dimensions (match renderGameOver)
        int btnW = 300;
        int btnH = 52;
        int btnX = (screenW - btnW) / 2;
        int btnStartY = screenH / 2 + 80;
        int btnSpacing = 66;
        
        // Mouse hover detection for game over buttons
        double mx = mouse.getX();
        double my = mouse.getY();
        for (int i = 0; i < 2; i++) {
            int btnY = btnStartY + i * btnSpacing;
            if (mx >= btnX && mx <= btnX + btnW &&
                my >= btnY && my <= btnY + btnH) {
                gameOverMenuSelection = i;
                break;
            }
        }
        
        // Animate card scales
        for (int i = 0; i < gameOverCardScales.length; i++) {
            float target = (i == gameOverMenuSelection) ? 1.04f : 1.0f;
            gameOverCardScales[i] += (target - gameOverCardScales[i]) * 0.15f;
        }
        
        // Mouse click selection
        if (mouse.isLeftJustClicked()) {
            for (int i = 0; i < 2; i++) {
                int btnY = btnStartY + i * btnSpacing;
                if (mx >= btnX && mx <= btnX + btnW &&
                    my >= btnY && my <= btnY + btnH) {
                    return actionFromIndex(i);
                }
            }
        }
        
        return GameOverAction.NONE;
    }
    
    private GameOverAction actionFromIndex(int index) {
        switch (index) {
            case 0: return GameOverAction.RESTART;
            case 1: return GameOverAction.MAIN_MENU;
            default: return GameOverAction.NONE;
        }
    }
}
