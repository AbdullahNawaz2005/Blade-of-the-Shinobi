package game.ui;
import static utils.RaylibRenderer.*; 
import static com.raylib.Raylib.*; 
import utils.Constants; 
import utils.UIRenderer; 
import utils.Difficulty; 
import game.effects.CherryPetalManager; 




public class MenuRenderer {
    
    public void render(CherryPetalManager cherryPetalManager, int highScore,
                       Difficulty difficulty, int mainMenuSelection, float[] menuCardScales) {
        int w = Constants.WINDOW_WIDTH;
        int h = Constants.WINDOW_HEIGHT;
        
        
        DrawRectangle(0, 0, w, h, color(0, 0, 0, 160));
        
        
        cherryPetalManager.render();
        
        
        int titleY = h / 6;
        
        
        UIRenderer.drawKatana(w / 2, titleY + 68, 400);
        
        
        int titleFontSize = 72;
        String titleLine1 = "BLADE OF THE";
        String titleLine2 = "SHINOBI";
        
        
        int line1Width = MeasureText(titleLine1, titleFontSize);
        int line2Width = MeasureText(titleLine2, titleFontSize);
        UIRenderer.drawGlowText(titleLine1, titleFontSize, UIRenderer.COLOR_TITLE, 
                               (w - line1Width) / 2, titleY);
        UIRenderer.drawGlowText(titleLine2, titleFontSize, UIRenderer.COLOR_TITLE, 
                               (w - line2Width) / 2, titleY + 70);
        
        
        UIRenderer.drawGoldDivider(w / 2, titleY + 110, 300);
        
        
        int subFontSize = 18;
        String subtitle = "Master the Shadow. Become the Legend.";
        int subWidth = MeasureText(subtitle, subFontSize);
        DrawText(subtitle, (w - subWidth) / 2, titleY + 140, subFontSize, 
            color(UIRenderer.COLOR_SUBTITLE[0], UIRenderer.COLOR_SUBTITLE[1], 
                      UIRenderer.COLOR_SUBTITLE[2], UIRenderer.COLOR_SUBTITLE[3]));
        
        
        int cardWidth = 320;
        int cardHeight = 52;
        int cardX = (w - cardWidth) / 2;
        int cardStartY = h / 2 - 40;
        int cardSpacing = 62;
        
        
        UIRenderer.drawRoundCard(cardX, cardStartY, cardWidth, cardHeight, 
                                mainMenuSelection == 0, "START GAME", menuCardScales[0]);
        
        
        UIRenderer.drawDifficultyCard(cardX, cardStartY + cardSpacing, cardWidth, cardHeight,
                                      mainMenuSelection == 1, difficulty, menuCardScales[1]);
        
        
        UIRenderer.drawRoundCard(cardX, cardStartY + cardSpacing * 2, cardWidth, cardHeight,
                                mainMenuSelection == 2, "CREDITS", menuCardScales[2]);
        
        
        UIRenderer.drawRoundCard(cardX, cardStartY + cardSpacing * 3, cardWidth, cardHeight,
                                mainMenuSelection == 3, "EXIT", menuCardScales[3]);
        
        
        int hsFontSize = 18;
        String hsText = "High Score: " + highScore;
        int hsWidth = MeasureText(hsText, hsFontSize);
        DrawText(hsText, (w - hsWidth) / 2, cardStartY + cardSpacing * 4 + 10, hsFontSize,
            color(255, 215, 0, 200));
        
        
        int bottomY = h - 60;
        
        
        UIRenderer.drawCrossedKunai(w / 2, bottomY - 20);
        
        
        int cpFontSize = 11;
        String copyright = "© Blade of the Shinobi";
        int cpWidth = MeasureText(copyright, cpFontSize);
        DrawText(copyright, (w - cpWidth) / 2, bottomY + 15, cpFontSize,
            color(UIRenderer.COLOR_MUTED_GRAY[0], UIRenderer.COLOR_MUTED_GRAY[1],
                      UIRenderer.COLOR_MUTED_GRAY[2], UIRenderer.COLOR_MUTED_GRAY[3]));
    }
}
