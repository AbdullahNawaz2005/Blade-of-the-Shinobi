package game.effects;

import java.util.Random; // Random number generator

/**
 * Screen shake effect state.
 * When a heavy impact occurs (e.g. boss hit), intensity and duration are set.
 * During render, if duration > 0, the entire coordinate system is translated by
 * a random offset.
 * The offset is recalculated every frame for a chaotic shake, and the duration
 * decrements until 0.
 */
public class ScreenShake {
    private int intensity;
    private int duration;

    public void trigger(int intensity, int duration) {

    }

    public void update() {
        if (duration > 0) {
            duration--;
        }
    }

    public boolean isActive() {
        return duration > 0;
    }

    public int getIntensity() {
        return intensity;
    }

    public int getDuration() {
        return duration;
    }

    public int randomOffset(Random random) {
        if (intensity <= 0)
            return 0;
        return random.nextInt(intensity * 2) - intensity;
    }
}
