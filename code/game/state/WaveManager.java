package game.state;
import utils.Constants; 









public class WaveManager {
    
    public boolean waveTransitioning;
    public long waveTransitionStartTime;
    public boolean waveCompleteMessageShown;
    
    
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
