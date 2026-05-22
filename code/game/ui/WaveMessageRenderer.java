package game.ui;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import utils.Constants; // Global game constants
import game.state.WaveManager; // Wave spawning logic

/**
 * Renders wave complete messages, wave announcements, and finisher text.
 */
public class WaveMessageRenderer {

    public void renderWaveCompleteMessage(int wave, long waveTransitionStartTime) {
        long elapsed = System.currentTimeMillis() - waveTransitionStartTime;
        float alpha = 1f;
        if (elapsed < 500) { alpha = elapsed / 500f; }
        else if (elapsed > 2500) { alpha = 1f - ((elapsed - 2500) / 500f); }
        alpha = Math.max(0, Math.min(1, alpha));
        int fontSize = 48;
        String text = "Wave " + wave + " Complete!";
        int textWidth = MeasureText(text, fontSize);
        DrawText(text, (Constants.WINDOW_WIDTH - textWidth) / 2, Constants.WINDOW_HEIGHT / 2, fontSize,
            color(255, 215, 0, (int)(alpha * 255)));
    }

    public void renderWaveAnnouncement(int wave, WaveManager waveManager) {
        long elapsed = System.currentTimeMillis() - waveManager.waveAnnounceStartTime;
        int totalDuration = Constants.WAVE_ANNOUNCE_FADE_IN_MS + Constants.WAVE_ANNOUNCE_HOLD_MS + Constants.WAVE_ANNOUNCE_FADE_OUT_MS;
        if (elapsed >= totalDuration) {
            waveManager.showingWaveAnnounce = false;
            return;
        }
        float alpha;
        if (elapsed < Constants.WAVE_ANNOUNCE_FADE_IN_MS) {
            alpha = elapsed / (float)Constants.WAVE_ANNOUNCE_FADE_IN_MS;
        } else if (elapsed < Constants.WAVE_ANNOUNCE_FADE_IN_MS + Constants.WAVE_ANNOUNCE_HOLD_MS) {
            alpha = 1f;
        } else {
            alpha = 1f - ((elapsed - Constants.WAVE_ANNOUNCE_FADE_IN_MS - Constants.WAVE_ANNOUNCE_HOLD_MS) / (float)Constants.WAVE_ANNOUNCE_FADE_OUT_MS);
        }
        alpha = Math.max(0, Math.min(1, alpha));
        int fontSize = 36;
        String waveText = "-- Wave " + wave + " --";
        int textWidth = MeasureText(waveText, fontSize);
        DrawText(waveText, (Constants.WINDOW_WIDTH - textWidth) / 2, Constants.WINDOW_HEIGHT / 2 - 30, fontSize,
            color(255, 255, 255, (int)(alpha * 255)));
        int subtitleIndex = Math.min(wave - 1, Constants.WAVE_SUBTITLES.length - 1);
        String subtitle = Constants.WAVE_SUBTITLES[subtitleIndex];
        int subFontSize = 20;
        int subWidth = MeasureText(subtitle, subFontSize);
        DrawText(subtitle, (Constants.WINDOW_WIDTH - subWidth) / 2, Constants.WINDOW_HEIGHT / 2 + 10, subFontSize,
            color(200, 200, 200, (int)(alpha * 255)));
    }

    public void renderFinisherText(long finisherTextStartTime) {
        long elapsed = System.currentTimeMillis() - finisherTextStartTime;
        float alpha = 1f - (elapsed / (float)Constants.COMBO_FINISHER_TEXT_DURATION_MS);
        alpha = Math.max(0, alpha);
        int fontSize = 64;
        String text = "FINISHER!";
        int textWidth = MeasureText(text, fontSize);
        int x = (Constants.WINDOW_WIDTH - textWidth) / 2;
        int y = Constants.WINDOW_HEIGHT / 3;
        DrawText(text, x + 4, y + 4, fontSize, color(0, 0, 0, (int)(alpha * 150)));
        DrawText(text, x - 2, y - 2, fontSize, color(255, 200, 0, (int)(alpha * 100)));
        DrawText(text, x + 2, y + 2, fontSize, color(255, 200, 0, (int)(alpha * 100)));
        DrawText(text, x, y, fontSize, color(255, 215, 0, (int)(alpha * 255)));
    }
}
