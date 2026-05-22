package game.effects;

import java.util.Random; 









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
