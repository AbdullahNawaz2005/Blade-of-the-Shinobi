package utils;
import static utils.RaylibRenderer.*; 

import static com.raylib.Colors.WHITE; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 







public class UIRenderer {
    
    
    public static final int[] COLOR_CARD_DEFAULT = {13, 27, 42, 255};       
    public static final int[] COLOR_CARD_SELECTED = {107, 15, 26, 255};     
    public static final int[] COLOR_STROKE_DEFAULT = {139, 105, 20, 255};   
    public static final int[] COLOR_STROKE_SELECTED = {255, 215, 0, 255};   
    public static final int[] COLOR_TITLE = {245, 230, 200, 255};           
    public static final int[] COLOR_SUBTITLE = {201, 168, 76, 255};         
    public static final int[] COLOR_MUTED_GRAY = {102, 102, 136, 255};      
    
    
    public static final int[] COLOR_EASY = {76, 175, 80, 255};              
    public static final int[] COLOR_NORMAL = {255, 193, 7, 255};            
    public static final int[] COLOR_HARD = {244, 67, 54, 255};              
    
    


    public static void drawRoundCard(int x, int y, int w, int h,
                                     boolean selected, String label, float scale) {
        
        if (scale != 1.0f) {
            int scaledW = (int)(w * scale);
            int scaledH = (int)(h * scale);
            x = x + (w - scaledW) / 2;
            y = y + (h - scaledH) / 2;
            w = scaledW;
            h = scaledH;
        }
        
        
        int[] fillColor = selected ? COLOR_CARD_SELECTED : COLOR_CARD_DEFAULT;
        fillRoundRect(x, y, w, h, 16, 
            color(fillColor[0], fillColor[1], fillColor[2], fillColor[3]));
        
        
        int[] strokeColor = selected ? COLOR_STROKE_SELECTED : COLOR_STROKE_DEFAULT;
        float lineThick = selected ? 2.5f : 1.5f;
        drawRoundRect(x, y, w, h, 16, lineThick,
            color(strokeColor[0], strokeColor[1], strokeColor[2], strokeColor[3]));
        
        
        if (selected) {
            fillRoundRect(x, y, 5, h, 3,
                color(strokeColor[0], strokeColor[1], strokeColor[2], strokeColor[3]));
        }
        
        
        int fontSize = 22;
        int textWidth = MeasureText(label, fontSize);
        int textX = x + (w - textWidth) / 2;
        int textY = y + (h - fontSize) / 2;
        
        if (selected) {
            
            DrawText(label, textX + 2, textY + 2, fontSize, color(0, 0, 0, 120));
            DrawText(label, textX, textY, fontSize, WHITE);
        } else {
            DrawText(label, textX, textY, fontSize, color(220, 220, 220, 255));
        }
    }
    
    


    public static void drawDifficultyCard(int x, int y, int w, int h,
                                          boolean selected, Difficulty difficulty, float scale) {
        
        if (scale != 1.0f) {
            int scaledW = (int)(w * scale);
            int scaledH = (int)(h * scale);
            x = x + (w - scaledW) / 2;
            y = y + (h - scaledH) / 2;
            w = scaledW;
            h = scaledH;
        }
        
        
        int[] fillColor = selected ? COLOR_CARD_SELECTED : COLOR_CARD_DEFAULT;
        fillRoundRect(x, y, w, h, 16,
            color(fillColor[0], fillColor[1], fillColor[2], fillColor[3]));
        
        
        int[] strokeColor = selected ? COLOR_STROKE_SELECTED : COLOR_STROKE_DEFAULT;
        float lineThick = selected ? 2.5f : 1.5f;
        drawRoundRect(x, y, w, h, 16, lineThick,
            color(strokeColor[0], strokeColor[1], strokeColor[2], strokeColor[3]));
        
        
        if (selected) {
            fillRoundRect(x, y, 5, h, 3,
                color(strokeColor[0], strokeColor[1], strokeColor[2], strokeColor[3]));
        }
        
        
        int fontSize = 22;
        String prefix = "DIFFICULTY: ";
        String diffName = difficulty.name();
        
        int totalWidth = MeasureText(prefix + diffName, fontSize);
        int textX = x + (w - totalWidth) / 2;
        int textY = y + (h - fontSize) / 2;
        
        
        if (selected) {
            DrawText(prefix, textX + 2, textY + 2, fontSize, color(0, 0, 0, 120));
            DrawText(prefix, textX, textY, fontSize, WHITE);
        } else {
            DrawText(prefix, textX, textY, fontSize, color(220, 220, 220, 255));
        }
        
        
        int diffX = textX + MeasureText(prefix, fontSize);
        int[] diffColor = switch (difficulty) {
            case EASY -> COLOR_EASY;
            case NORMAL -> COLOR_NORMAL;
            case HARD -> COLOR_HARD;
        };
        
        if (selected) {
            DrawText(diffName, diffX + 2, textY + 2, fontSize, color(0, 0, 0, 120));
        }
        DrawText(diffName, diffX, textY, fontSize,
            color(diffColor[0], diffColor[1], diffColor[2], diffColor[3]));
    }
    
    


    public static void drawGoldDivider(int centerX, int y, int width) {
        int halfWidth = width / 2;
        int x1 = centerX - halfWidth;
        int x2 = centerX + halfWidth;
        
        Color goldColor = color(COLOR_SUBTITLE[0], COLOR_SUBTITLE[1], COLOR_SUBTITLE[2], COLOR_SUBTITLE[3]);
        
        
        DrawLineEx(vec2(x1 + 8, y), vec2(x2 - 8, y), 2, goldColor);
        
        
        int[] leftDiamondX = {x1, x1 + 8, x1, x1 - 8};
        int[] leftDiamondY = {y - 6, y, y + 6, y};
        fillPolygon(leftDiamondX, leftDiamondY, 4, goldColor);
        
        
        int[] rightDiamondX = {x2, x2 + 8, x2, x2 - 8};
        int[] rightDiamondY = {y - 6, y, y + 6, y};
        fillPolygon(rightDiamondX, rightDiamondY, 4, goldColor);
    }
    
    


    public static void drawGlowText(String text, int fontSize, int[] color, int x, int y) {
        
        DrawText(text, x + 2, y + 2, fontSize, color(0, 0, 0, 120));
        
        
        DrawText(text, x, y, fontSize, color(color[0], color[1], color[2], color[3]));
    }
    
    


    public static void drawKatana(int centerX, int y, int length) {
        int bladeLength = (int)(length * 0.7);
        int handleLength = (int)(length * 0.3);
        int guardWidth = 20;
        
        int bladeStart = centerX - length / 2;
        int guardX = bladeStart + bladeLength;
        int handleEnd = guardX + handleLength;
        
        
        DrawRectangle(bladeStart, y - 3, bladeLength, 6, color(180, 185, 195, 255));
        
        
        DrawRectangle(bladeStart, y - 3, bladeLength, 2, color(220, 225, 235, 255));
        
        
        int[] tipX = {bladeStart, bladeStart - 15, bladeStart - 15};
        int[] tipY = {y, y - 4, y + 4};
        fillPolygon(tipX, tipY, 3, color(180, 185, 195, 255));
        
        
        fillOval(guardX - 5, y - guardWidth / 2, 10, guardWidth, color(50, 45, 40, 255));
        drawOval(guardX - 5, y - guardWidth / 2, 10, guardWidth, color(139, 105, 20, 255));
        
        
        DrawRectangle(guardX + 5, y - 5, handleLength - 5, 10, color(80, 20, 25, 255));
        
        
        for (int i = 0; i < handleLength - 10; i += 12) {
            DrawLine(guardX + 8 + i, y - 5, guardX + 14 + i, y + 5, color(100, 30, 35, 255));
        }
        
        
        fillOval(handleEnd - 5, y - 6, 10, 12, color(50, 45, 40, 255));
    }
    
    


    public static void drawCrossedKunai(int centerX, int y) {
        
        drawSingleKunaiSimple(centerX - 10, y, true);
        drawSingleKunaiSimple(centerX + 10, y, false);
    }
    
    private static void drawSingleKunaiSimple(int x, int y, boolean pointRight) {
        int dir = pointRight ? 1 : -1;
        
        
        int[] bladeX = {x, x + dir * 8, x + dir * 25, x + dir * 8, x};
        int[] bladeY = {y - 3, y - 3, y, y + 3, y + 3};
        fillPolygon(bladeX, bladeY, 5, color(70, 75, 85, 255));
        
        
        DrawRectangle(x - dir * 15, y - 3, 15, 6, color(50, 45, 40, 255));
        
        
        int ringX = pointRight ? x - 22 : x + 12;
        fillOval(ringX, y - 5, 10, 10, color(60, 55, 50, 255));
        fillOval(ringX + 3, y - 2, 4, 4, color(40, 35, 30, 255));
    }
    
    


    public static void drawHUDPanel(int score, int wave, int enemies) {
        int panelWidth = 320;
        int panelHeight = 54;
        int panelX = (Constants.WINDOW_WIDTH - panelWidth) / 2;
        int panelY = 12;
        
        
        fillRoundRect(panelX, panelY, panelWidth, panelHeight, 12,
            color(13, 27, 42, 200));
        
        
        drawRoundRect(panelX, panelY, panelWidth, panelHeight, 12, 1,
            color(COLOR_STROKE_DEFAULT[0], COLOR_STROKE_DEFAULT[1], COLOR_STROKE_DEFAULT[2], COLOR_STROKE_DEFAULT[3]));
        
        
        int section = panelWidth / 3;
        Color dividerColor = color(139, 105, 20, 100);
        DrawLine(panelX + section, panelY + 10, panelX + section, panelY + panelHeight - 10, dividerColor);
        DrawLine(panelX + section * 2, panelY + 10, panelX + section * 2, panelY + panelHeight - 10, dividerColor);
        
        
        int fontSize = 15;
        int textY = panelY + 32;
        
        
        drawLabeledValue("Score", String.valueOf(score), panelX + section / 2, textY, fontSize);
        
        
        drawLabeledValue("Wave", String.valueOf(wave), panelX + section + section / 2, textY, fontSize);
        
        
        drawLabeledValue("Enemies", String.valueOf(enemies), panelX + section * 2 + section / 2, textY, fontSize);
    }
    
    private static void drawLabeledValue(String label, String value, int centerX, int y, int fontSize) {
        String full = label + ": " + value;
        int textWidth = MeasureText(full, fontSize);
        int x = centerX - textWidth / 2;
        
        
        Color goldColor = color(COLOR_SUBTITLE[0], COLOR_SUBTITLE[1], COLOR_SUBTITLE[2], COLOR_SUBTITLE[3]);
        DrawText(label + ": ", x, y, fontSize, goldColor);
        
        
        int valueX = x + MeasureText(label + ": ", fontSize);
        DrawText(value, valueX, y, fontSize, WHITE);
    }
    
    


    public static void drawRoundCard(int x, int y, int w, int h,
                                     boolean selected, String label) {
        drawRoundCard(x, y, w, h, selected, label, 1.0f);
    }
}
