package game.state;
import utils.Constants; // Global game constants

/**
 * Manages wave transition timing and wave announcements.
 * The announcement cycle has three sub-phases controlled by Constants:
 *   1. WAVE_ANNOUNCE_FADE_IN_MS  - alpha ramps from 0 to 255
 *   2. WAVE_ANNOUNCE_HOLD_MS     - text is displayed at full opacity
 *   3. WAVE_ANNOUNCE_FADE_OUT_MS - alpha ramps back to 0
 * WaveMessageRenderer reads waveAnnounceStartTime to calculate which sub-phase to render.
 */
public class WaveManager {
    // Wave transition
    public boolean waveTransitioning;
    public long waveTransitionStartTime;
    public boolean waveCompleteMessageShown;
    
    // Wave announcement
    public boolean showingWaveAnnounce;
    public long waveAnnounceStartTime;
    
    public boolean isTransitionComplete() {
        if (!waveTransitioning) return false;
        long elapsed = System.currentTimeMillis() - waveTransitionStartTime;
        return elapsed >= Constants.WAVE_TRANSITION_DURATION_MS;
    }
    
    public void startTransition() {
        waveTransitioning = true;
        waveTransitionStartTime = System.currentTimeMillis();
        waveCompleteMessageShown = true;
    }
    
    public void endTransition() {
        waveTransitioning = false;
        waveCompleteMessageShown = false;
    }
    
    public void startAnnouncement() {
        showingWaveAnnounce = true;
        waveAnnounceStartTime = System.currentTimeMillis();
    }
    
    public void reset() {
        waveTransitioning = false;
        waveCompleteMessageShown = false;
        showingWaveAnnounce = false;
    }
}
