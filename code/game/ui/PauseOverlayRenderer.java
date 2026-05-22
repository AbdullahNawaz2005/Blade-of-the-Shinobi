package game.ui;
import static utils.RaylibRenderer.*; 
import static com.raylib.Raylib.*; 
import utils.Constants; 
import utils.UIRenderer; 




public class PauseOverlayRenderer {
    public void render(int pauseMenuSelection, float[] pauseCardScales) {
        int screenW = Constants.WINDOW_WIDTH;
        int screenH = Constants.WINDOW_HEIGHT;
        DrawRectangle(0, 0, screenW, screenH, color(13, 27, 42, 200));
        int panelW = 380; int panelH = 280;
        int panelX = (screenW - panelW) / 2; int panelY = (screenH - panelH) / 2;
        fillRoundRect(panelX, panelY, panelW, panelH, 16, color(13, 27, 42, 230));
        drawRoundRect(panelX, panelY, panelW, panelH, 16, 2f, color(255, 215, 0, 255));
        int pauseFontSize = 40;
        String pausedText = "PAUSED";
        int pausedWidth = MeasureText(pausedText, pauseFontSize);
        UIRenderer.drawGlowText(pausedText, pauseFontSize, new int[]{255, 255, 255, 255},
            screenW / 2 - pausedWidth / 2, panelY + 35);
        UIRenderer.drawGoldDivider(screenW / 2, panelY + 75, 200);
        String[] options = {"RESUME", "MENU", "EXIT"};
        int cardW = 280; int cardH = 46; int startY = panelY + 110; int spacing = 54;
        for (int i = 0; i < options.length; i++) {
            int cardX = (screenW - cardW) / 2; int cardY = startY + i * spacing;
            boolean selected = (i == pauseMenuSelection);
            UIRenderer.drawRoundCard(cardX, cardY, cardW, cardH, selected, options[i], pauseCardScales[i]);
        }
    }
}
