package game.ui;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import static com.raylib.Colors.WHITE; // Imports WHITE functionality
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import utils.Constants; // Global game constants
import utils.UIRenderer; // UI drawing helpers
import game.effects.CherryPetalManager; // Falling petals effect

public class CreditsRenderer {
    
    public void render(CherryPetalManager cherryPetalManager) {
        int w = Constants.WINDOW_WIDTH;
        int h = Constants.WINDOW_HEIGHT;
        DrawRectangle(0, 0, w, h, color(0, 0, 0, 160));
        cherryPetalManager.render();
        int titleY = h / 5;
        int headingFontSize = 58;
        String heading = "CREDITS";
        int headingWidth = MeasureText(heading, headingFontSize);
        int[] goldColor = {255, 215, 0, 255};
        UIRenderer.drawGlowText(heading, headingFontSize, goldColor, (w - headingWidth) / 2, titleY);

        int cardWidth = 400;
        int cardHeight = 90;
        int cardX = (w - cardWidth) / 2;
        int blockY = h / 2 - 80;
        int blockSpacing = 110;
        renderCreditBlock(cardX, blockY, cardWidth, cardHeight, "DEVELOPED BY", "Abdullah Nawaz");
        renderCreditBlock(cardX, blockY + blockSpacing, cardWidth, cardHeight, "SECTION", "BSCS 15 A");

        int backWidth = 200;
        int backHeight = 46;
        int backX = (w - backWidth) / 2;
        int backY = h - 120;
        UIRenderer.drawRoundCard(backX, backY, backWidth, backHeight, true, "<- BACK", 1.0f);
    }
    
    private void renderCreditBlock(int x, int y, int w, int h, String label, String value) {
        fillRoundRect(x, y, w, h, 16, color(UIRenderer.COLOR_CARD_DEFAULT[0], UIRenderer.COLOR_CARD_DEFAULT[1], UIRenderer.COLOR_CARD_DEFAULT[2], UIRenderer.COLOR_CARD_DEFAULT[3]));
        drawRoundRect(x, y, w, h, 16, 1.5f, color(UIRenderer.COLOR_STROKE_DEFAULT[0], UIRenderer.COLOR_STROKE_DEFAULT[1], UIRenderer.COLOR_STROKE_DEFAULT[2], UIRenderer.COLOR_STROKE_DEFAULT[3]));
        int labelFontSize = 13;
        int labelWidth = MeasureText(label, labelFontSize);
        DrawText(label, x + (w - labelWidth) / 2, y + 30, labelFontSize, color(UIRenderer.COLOR_SUBTITLE[0], UIRenderer.COLOR_SUBTITLE[1], UIRenderer.COLOR_SUBTITLE[2], UIRenderer.COLOR_SUBTITLE[3]));
        int valueFontSize = 28;
        int valueWidth = MeasureText(value, valueFontSize);
        DrawText(value, x + (w - valueWidth) / 2, y + 50, valueFontSize, WHITE);
    }
}
