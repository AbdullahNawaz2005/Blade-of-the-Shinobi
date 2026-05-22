package game.ui;
import input.InputHandler; 
import input.MouseHandler; 
import utils.Constants; 





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
        
        
        int btnW = 300;
        int btnH = 52;
        int btnX = (screenW - btnW) / 2;
        int btnStartY = screenH / 2 + 80;
        int btnSpacing = 66;
        
        
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
        
        
        for (int i = 0; i < gameOverCardScales.length; i++) {
            float target = (i == gameOverMenuSelection) ? 1.04f : 1.0f;
            gameOverCardScales[i] += (target - gameOverCardScales[i]) * 0.15f;
        }
        
        
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
