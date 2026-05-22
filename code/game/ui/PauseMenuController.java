package game.ui;
import static com.raylib.Raylib.*; 
import input.InputHandler; 
import input.MouseHandler; 
import audio.SoundManager; 
import utils.Constants; 





public class PauseMenuController {
    
    public enum PauseAction { NONE, RESUME, MAIN_MENU, EXIT }
    
    private int pauseMenuSelection = 0;
    private float[] pauseCardScales = {1.0f, 1.0f, 1.0f};
    
    public int getPauseMenuSelection() { return pauseMenuSelection; }
    public float[] getPauseCardScales() { return pauseCardScales; }
    
    public void resetSelection() {
        pauseMenuSelection = 0;
    }
    
    public PauseAction update(InputHandler input, MouseHandler mouse, SoundManager soundManager) {
        int screenW = Constants.WINDOW_WIDTH;
        int screenH = Constants.WINDOW_HEIGHT;
        int panelH = 280;
        int panelY = (screenH - panelH) / 2;
        int cardW = 280;
        int cardH = 46;
        int startY = panelY + 110;
        int spacing = 54;
        int cardX = (screenW - cardW) / 2;
        
        
        double mx = mouse.getX();
        double my = mouse.getY();
        for (int i = 0; i < 3; i++) {
            int cardY = startY + i * spacing;
            if (mx >= cardX && mx <= cardX + cardW &&
                my >= cardY && my <= cardY + cardH) {
                if (pauseMenuSelection != i) {
                    pauseMenuSelection = i;
                    soundManager.playEffect("menu_select.wav");
                }
                break;
            }
        }
        
        
        if (mouse.isLeftJustClicked()) {
            for (int i = 0; i < 3; i++) {
                int cardY = startY + i * spacing;
                if (mx >= cardX && mx <= cardX + cardW &&
                    my >= cardY && my <= cardY + cardH) {
                    return actionFromIndex(i);
                }
            }
        }
        
        
        if (input.isKeyJustPressed(KEY_W) ||
            input.isKeyJustPressed(KEY_UP)) {
            pauseMenuSelection = (pauseMenuSelection - 1 + 3) % 3;
            soundManager.playEffect("menu_select.wav");
        }
        if (input.isKeyJustPressed(KEY_S) ||
            input.isKeyJustPressed(KEY_DOWN)) {
            pauseMenuSelection = (pauseMenuSelection + 1) % 3;
            soundManager.playEffect("menu_select.wav");
        }
        
        
        for (int i = 0; i < pauseCardScales.length; i++) {
            float target = (i == pauseMenuSelection) ? 1.04f : 1.0f;
            pauseCardScales[i] += (target - pauseCardScales[i]) * 0.15f;
        }
        
        
        if (input.isKeyJustPressed(KEY_ENTER) ||
            input.isKeyJustPressed(KEY_SPACE)) {
            return actionFromIndex(pauseMenuSelection);
        }
        
        
        if (input.isKeyJustPressed(KEY_ESCAPE)) {
            return PauseAction.EXIT;
        }
        if (input.isKeyJustPressed(KEY_P)) {
            return PauseAction.RESUME;
        }
        
        return PauseAction.NONE;
    }
    
    private PauseAction actionFromIndex(int index) {
        switch (index) {
            case 0: return PauseAction.RESUME;
            case 1: return PauseAction.MAIN_MENU;
            case 2: return PauseAction.EXIT;
            default: return PauseAction.NONE;
        }
    }
}
