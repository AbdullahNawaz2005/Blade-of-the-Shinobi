package game.ui;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
// Colors accessed via RaylibRenderer (WHITE, BLACK, etc.)
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import utils.Constants; // Global game constants
import utils.UIRenderer; // UI drawing helpers
import game.state.GameSession; // Current game state data

/**
 * Renders the game over overlay, victory screen, and level transition screen.
 */
public class GameOverRenderer {

    public void renderGameOverOverlay(GameSession session, int highScore, boolean isNewHighScore,
                                      int gameOverMenuSelection, float[] gameOverCardScales) {
        int screenW = Constants.WINDOW_WIDTH; int screenH = Constants.WINDOW_HEIGHT;
        DrawRectangle(0, 0, screenW, screenH, color(0, 0, 0, 180));
        int panelW = 440; int panelH = 400;
        int panelX = (screenW - panelW) / 2; int panelY = (screenH - panelH) / 2 - 20;
        fillRoundRect(panelX, panelY, panelW, panelH, 16, color(13, 27, 42, 240));
        drawRoundRect(panelX, panelY, panelW, panelH, 16, 2.5f, color(200, 50, 50, 255));
        int titleFontSize = 56;
        String title = "GAME OVER";
        int titleWidth = MeasureText(title, titleFontSize);
        int titleX = (screenW - titleWidth) / 2; int titleY = panelY + 30;
        DrawText(title, titleX + 3, titleY + 3, titleFontSize, color(150, 0, 0, 120));
        DrawText(title, titleX, titleY, titleFontSize, color(220, 40, 40, 255));
        UIRenderer.drawGoldDivider(screenW / 2, titleY + titleFontSize + 10, 250);
        int infoStartY = titleY + titleFontSize + 30;
        if (isNewHighScore) {
            int newHsFontSize = 24;
            String newHsText = "NEW HIGH SCORE!";
            int newHsWidth = MeasureText(newHsText, newHsFontSize);
            int pulse = (int)(200 + 55 * Math.sin(System.currentTimeMillis() * 0.005));
            DrawText(newHsText, (screenW - newHsWidth) / 2, infoStartY, newHsFontSize, color(255, 215, 0, pulse));
            infoStartY += 32;
        }
        int statsFontSize = 22;
        String scoreText = "Final Score: " + session.score;
        int scoreWidth = MeasureText(scoreText, statsFontSize);
        DrawText(scoreText, (screenW - scoreWidth) / 2, infoStartY, statsFontSize, WHITE);
        String waveText = "Wave Reached: " + session.wave;
        int waveWidth = MeasureText(waveText, statsFontSize);
        DrawText(waveText, (screenW - waveWidth) / 2, infoStartY + 30, statsFontSize, color(200, 200, 200, 255));
        int hsFontSize = 18;
        String hsText = "High Score: " + highScore;
        int hsWidth = MeasureText(hsText, hsFontSize);
        DrawText(hsText, (screenW - hsWidth) / 2, infoStartY + 62, hsFontSize, color(255, 215, 0, 200));
        String[] options = {"RESTART", "MAIN MENU"};
        int btnW = 300; int btnH = 52; int btnStartY = screenH / 2 + 80; int btnSpacing = 66;
        for (int i = 0; i < options.length; i++) {
            int btnX = (screenW - btnW) / 2; int btnY = btnStartY + i * btnSpacing;
            boolean selected = (i == gameOverMenuSelection);
            UIRenderer.drawRoundCard(btnX, btnY, btnW, btnH, selected, options[i], gameOverCardScales[i]);
        }
    }

    public void renderVictory(GameSession session) {
        DrawRectangleGradientV(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT,
            color(20, 50, 20, 255), color(50, 100, 50, 255));
        int cardWidth = 500; int cardHeight = 300;
        int cardX = (Constants.WINDOW_WIDTH - cardWidth) / 2; int cardY = (Constants.WINDOW_HEIGHT - cardHeight) / 2;
        fillRoundRect(cardX, cardY, cardWidth, cardHeight, 20, color(0, 0, 0, 200));
        drawRoundRect(cardX, cardY, cardWidth, cardHeight, 20, 3f, color(255, 215, 0, 255));
        int titleFontSize = 36;
        String title = "You are the";
        int titleWidth = MeasureText(title, titleFontSize);
        DrawText(title, (Constants.WINDOW_WIDTH - titleWidth) / 2, cardY + 40, titleFontSize, color(255, 215, 0, 255));
        int bladeFontSize = 42;
        String blade = "Blade of the Shinobi";
        int bladeWidth = MeasureText(blade, bladeFontSize);
        DrawText(blade, (Constants.WINDOW_WIDTH - bladeWidth) / 2, cardY + 85, bladeFontSize, color(255, 215, 0, 255));
        int statsFontSize = 20;
        DrawText("Final Score: " + session.score, cardX + 50, cardY + 160, statsFontSize, WHITE);
        DrawText("Waves Survived: " + session.wave, cardX + 50, cardY + 190, statsFontSize, WHITE);
        DrawText("Bosses Defeated: " + session.bossCount, cardX + 50, cardY + 220, statsFontSize, WHITE);
    }

    public void renderLevelTransition(int currentLevel) {
        DrawRectangle(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, BLACK);
        int fontSize = 48;
        String text = "Level " + currentLevel;
        int textWidth = MeasureText(text, fontSize);
        DrawText(text, (Constants.WINDOW_WIDTH - textWidth) / 2, Constants.WINDOW_HEIGHT / 2, fontSize, WHITE);
    }
}
