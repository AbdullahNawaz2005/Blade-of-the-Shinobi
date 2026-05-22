package game.effects;

/**
 * Damage flash effect (red screen edge).
 * Timer is decremented during vignette rendering to match original behavior.
 */
public class DamageFlash {
    private int timer = 0;
    private static final int DURATION = 15;
    
    public void trigger() {
        timer = DURATION;
    }
    
    public boolean isActive() {
        return timer > 0;
    }
    
    public int getTimer() { return timer; }
    public int getDuration() { return DURATION; }
    
    public void decrement() {
        if (timer > 0) timer--;
    }
}
