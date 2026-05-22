package game.ui;
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import input.InputHandler; // Keyboard input handling
import input.MouseHandler; // Mouse input handling
import utils.Constants; // Global game constants
import game.state.GameSession; // Current game state data

/**
 * Handles main menu and credits screen update logic.
 * Returns actions for GameEngine to execute.
 */
public class MenuController {
    
    public enum MenuAction { NONE, START_GAME, CYCLE_DIFFICULTY, SHOW_CREDITS, EXIT }
    
    // Main menu state
    private int mainMenuSelection = 0;
    private float[] menuCardScales = {1.0f, 1.0f, 1.0f, 1.0f};
    
    public int getMainMenuSelection() { return mainMenuSelection; }
    public float[] getMenuCardScales() { return menuCardScales; }
    
    public MenuAction updateMenu(InputHandler input, MouseHandler mouse) {
        // Menu card scale animation
        for (int i = 0; i < menuCardScales.length; i++) {
            float targetScale = (i == mainMenuSelection) ? 1.04f : 1.0f;
            menuCardScales[i] += (targetScale - menuCardScales[i]) * 0.15f;
        }
        
        // Navigation
        if (input.isKeyJustPressed(KEY_W) ||
            input.isKeyJustPressed(KEY_UP)) {
            mainMenuSelection = (mainMenuSelection - 1 + 4) % 4;
        }
        if (input.isKeyJustPressed(KEY_S) ||
            input.isKeyJustPressed(KEY_DOWN)) {
            mainMenuSelection = (mainMenuSelection + 1) % 4;
        }
        
        // Mouse hover detection
        double mouseY = mouse.getY();
        int cardStartY = Constants.WINDOW_HEIGHT / 2 - 40;
        int cardHeight = 52;
        int cardSpacing = 62;
        for (int i = 0; i < 4; i++) {
            int cardY = cardStartY + i * cardSpacing;
            if (mouseY >= cardY && mouseY <= cardY + cardHeight) {
                int cardX = (Constants.WINDOW_WIDTH - 320) / 2;
                if (mouse.getX() >= cardX && mouse.getX() <= cardX + 320) {
                    mainMenuSelection = i;
                }
            }
        }
        
        // Selection
        boolean select = input.isKeyJustPressed(KEY_ENTER) ||
                        input.isKeyJustPressed(KEY_SPACE) ||
                        mouse.isLeftJustClicked();
        
        if (select) {
            switch (mainMenuSelection) {
                case 0: return MenuAction.START_GAME;
                case 1: return MenuAction.CYCLE_DIFFICULTY;
                case 2: return MenuAction.SHOW_CREDITS;
                case 3: return MenuAction.EXIT;
            }
        }
        
        return MenuAction.NONE;
    }
    
    public boolean updateCredits(InputHandler input, MouseHandler mouse) {
        // Back button - returns true if user wants to go back
        if (input.isKeyJustPressed(KEY_ENTER) ||
            input.isKeyJustPressed(KEY_SPACE) ||
            mouse.isLeftJustClicked()) {
            return true;
        }
        // Exit game on ESC
        if (input.isKeyJustPressed(KEY_ESCAPE)) {
            System.exit(0);
        }
        return false;
    }
}
