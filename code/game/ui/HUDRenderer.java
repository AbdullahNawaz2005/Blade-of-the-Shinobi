package game.ui;

import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import java.util.ArrayList; 
import java.util.List; 
import entities.Player; 
import input.MouseHandler; 
import utils.Constants; 
import game.state.GameSession; 





public class HUDRenderer {
    private static final int PAUSE_BTN_X = 20;
    private static final int PAUSE_BTN_Y = 12;
    private static final int PAUSE_BTN_SIZE = 44;

    private static class ActivePowerUp {
        String label;
        int[] colorRGBA;
        double progress;

        ActivePowerUp(String label, int[] colorRGBA, double progress) {
            this.label = label;
            this.colorRGBA = colorRGBA;
            this.progress = progress;
        }
    }

    public void renderHUD(Player player, GameSession session, MouseHandler mouse, int enemyCount) {
        utils.UIRenderer.drawHUDPanel(session.score, session.wave, enemyCount);
        
        int btnX = Constants.WINDOW_WIDTH - PAUSE_BTN_X - PAUSE_BTN_SIZE;
        int btnY = PAUSE_BTN_Y;
        boolean hovered = mouse.getX() >= btnX && mouse.getX() <= btnX + PAUSE_BTN_SIZE &&
                mouse.getY() >= btnY && mouse.getY() <= btnY + PAUSE_BTN_SIZE;
        if (hovered) {
            fillRoundRect(btnX, btnY, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE, 10, color(107, 15, 26, 230));
        } else {
            fillRoundRect(btnX, btnY, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE, 10, color(13, 27, 42, 200));
        }
        float lineThick = hovered ? 2f : 1.5f;
        if (hovered) {
            drawRoundRect(btnX, btnY, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE, 10, lineThick, color(255, 215, 0, 255));
        } else {
            drawRoundRect(btnX, btnY, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE, 10, lineThick, color(139, 105, 20, 255));
        }
        int barWidth = 6;
        int barHeight = 20;
        int gap = 6;
        int barX1 = btnX + (PAUSE_BTN_SIZE - barWidth * 2 - gap) / 2;
        int barX2 = barX1 + barWidth + gap;
        int barY = btnY + (PAUSE_BTN_SIZE - barHeight) / 2;
        fillRoundRect(barX1, barY, barWidth, barHeight, 2, WHITE);
        fillRoundRect(barX2, barY, barWidth, barHeight, 2, WHITE);

        
        fillRoundRect(15, 75, 215, 60, Constants.HUD_PANEL_CORNER_RADIUS, color(0, 0, 0, Constants.HUD_PANEL_ALPHA));
        DrawRectangle(20, 80, 200, 20, DARKGRAY);
        double healthPercent = (double) player.getHealth() / Constants.PLAYER_MAX_HEALTH;
        int healthBarWidth = (int) (200 * healthPercent);
        Color healthColor;
        if (healthPercent > 0.6) {
            double t = (healthPercent - 0.6) / 0.4;
            healthColor = color((int) (50 + (1 - t) * 150), (int) (200 + (1 - t) * 55), 50, 255);
        } else if (healthPercent > 0.3) {
            double t = (healthPercent - 0.3) / 0.3;
            healthColor = color(255, (int) (100 + t * 155), (int) (50 * t), 255);
        } else {
            double t = healthPercent / 0.3;
            healthColor = color((int) (150 + t * 105), (int) (t * 100), (int) (t * 30), 255);
        }
        if (healthBarWidth > 0) {
            DrawRectangle(20, 80, healthBarWidth, 20, healthColor);
            DrawRectangle(20, 80, healthBarWidth, 4, color(255, 255, 255, 40));
            DrawRectangle(20, 96, healthBarWidth, 4, color(0, 0, 0, 60));
        }
        drawRoundRect(19, 79, 202, 22, 3, 1, color(100, 100, 100, 200));
        int hpFontSize = 12;
        String hpText = "HP: " + player.getHealth() + " / " + Constants.PLAYER_MAX_HEALTH;
        DrawText(hpText, 26, 84, hpFontSize, color(0, 0, 0, 180));
        DrawText(hpText, 25, 83, hpFontSize, WHITE);
        DrawRectangle(20, 105, 200, 12, DARKGRAY);
        double staminaPercent = (double) player.getStamina() / Constants.PLAYER_MAX_STAMINA;
        DrawRectangle(20, 105, (int) (200 * staminaPercent), 12,
                color(Constants.COLOR_STAMINA_GREEN[0], Constants.COLOR_STAMINA_GREEN[1],
                        Constants.COLOR_STAMINA_GREEN[2], 255));
        DrawRectangle(20, 122, 200, 8, DARKGRAY);
        double comboPercent = Math.min(1.0, (double) player.getComboCount() / Constants.COMBO_FINISHER_THRESHOLD);
        Color comboColor;
        if (comboPercent >= 1.0) {
            comboColor = color(255, 200, 50, 255);
        } else {
            comboColor = color(Constants.COLOR_COMBO_GOLD[0], Constants.COLOR_COMBO_GOLD[1],
                    Constants.COLOR_COMBO_GOLD[2], 255);
        }
        DrawRectangle(20, 122, (int) (200 * comboPercent), 8, comboColor);
        int combo = player.getComboCount();
        if (combo > 1) {
            int comboFontSize = 32;
            String comboText;
            Color comboTextColor;
            if (combo >= Constants.COMBO_FINISHER_THRESHOLD) {
                comboText = combo + " HIT - FINISHER READY!";
                comboTextColor = color(255, 150, 0, 255);
            } else {
                comboText = combo + " HIT COMBO!";
                comboTextColor = color(255, 255, 0, 255);
            }
            int comboWidth = MeasureText(comboText, comboFontSize);
            DrawText(comboText, (Constants.WINDOW_WIDTH - comboWidth) / 2, 180, comboFontSize, comboTextColor);
        }
        
        int killFontSize = 16;
        String killText = "Kills: " + session.killCount;
        DrawText("☠", 20, 145, killFontSize + 4, color(200, 50, 50, 255));
        DrawText(killText, 42, 147, killFontSize, WHITE);
        
        int progressBarHeight = 6;
        int progressBarY = Constants.WINDOW_HEIGHT - progressBarHeight - 4;
        int progressBarWidth = Constants.WINDOW_WIDTH - 40;
        int progressBarX = 20;
        DrawRectangle(progressBarX, progressBarY, progressBarWidth, progressBarHeight, color(30, 30, 30, 180));
        double progress = Math.max(0,
                Math.min(1.0, player.getPosition().x / (double) (Constants.WINDOW_WIDTH - player.getWidth())));
        int fillWidth = (int) (progressBarWidth * progress);
        DrawRectangle(progressBarX, progressBarY, fillWidth, progressBarHeight, color(100, 180, 255, 220));
        if (fillWidth > 2) {
            DrawRectangle(progressBarX + fillWidth - 3, progressBarY, 3, progressBarHeight, color(200, 230, 255, 255));
        }
        drawRoundRect(progressBarX - 1, progressBarY - 1, progressBarWidth + 2, progressBarHeight + 2, 2, 1,
                color(80, 80, 80, 150));
        
        renderPowerUpIndicators(player);
    }

    private void renderPowerUpIndicators(Player player) {
        int baseX = Constants.WINDOW_WIDTH / 2 - 100;
        int baseY = Constants.WINDOW_HEIGHT - 60;
        int iconSize = 40;
        int spacing = 50;
        List<ActivePowerUp> activePowerUps = new ArrayList<>();
        if (player.hasSpeedBoost()) {
            activePowerUps.add(new ActivePowerUp("SPD", new int[] { 50, 150, 255, 255 },
                    (double) player.getSpeedBoostRemainingMs() / Constants.SPEED_BOOST_DURATION_MS));
        }
        if (player.hasDamageBoost()) {
            activePowerUps.add(new ActivePowerUp("DMG", new int[] { 255, 150, 50, 255 },
                    (double) player.getDamageBoostRemainingMs() / Constants.DAMAGE_BOOST_DURATION_MS));
        }
        if (player.getShieldHits() > 0) {
            activePowerUps.add(new ActivePowerUp("x" + player.getShieldHits(), new int[] { 100, 200, 255, 255 }, 1.0));
        }
        baseX = Constants.WINDOW_WIDTH / 2 - (activePowerUps.size() * spacing) / 2;
        for (int i = 0; i < activePowerUps.size(); i++) {
            ActivePowerUp ap = activePowerUps.get(i);
            int x = baseX + i * spacing;
            fillOval(x, baseY, iconSize, iconSize, color(0, 0, 0, 150));
            Color arcColor = color(ap.colorRGBA[0], ap.colorRGBA[1], ap.colorRGBA[2], ap.colorRGBA[3]);
            int progressSize = (int) (iconSize * ap.progress);
            if (progressSize > 0) {
                fillOval(x + (iconSize - progressSize) / 2, baseY + (iconSize - progressSize) / 2, progressSize,
                        progressSize, arcColor);
            }
            fillOval(x + 5, baseY + 5, iconSize - 10, iconSize - 10, color(30, 30, 30, 255));
            int textFontSize = 10;
            int textWidth = MeasureText(ap.label, textFontSize);
            DrawText(ap.label, x + iconSize / 2 - textWidth / 2, baseY + iconSize / 2 - 3, textFontSize, WHITE);
        }
    }
}
