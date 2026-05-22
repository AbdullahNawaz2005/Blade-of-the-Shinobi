package game.effects;
import java.util.ArrayList; // For dynamic arrays
import java.util.List; // List interface for collections
import java.util.Random; // Random number generator
import utils.Constants; // Global game constants

/**
 * Manages cherry blossom petal list for menu animations.
 */
public class CherryPetalManager {
    private List<CherryPetal> petals = new ArrayList<>();
    
    public CherryPetalManager(Random random) {
        for (int i = 0; i < 40; i++) {
            petals.add(new CherryPetal(random, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        }
    }
    
    public void update(Random random) {
        for (CherryPetal petal : petals) {
            petal.update(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, random);
        }
    }
    
    public void render() {
        for (CherryPetal petal : petals) {
            petal.render();
        }
    }
}
