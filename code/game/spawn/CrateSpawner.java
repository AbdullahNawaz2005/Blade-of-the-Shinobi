package game.spawn;
import java.util.List; // List interface for collections
import utils.Constants; // Global game constants
import game.entities.Crate; // Breakable objects

/**
 * Handles crate spawning at fixed positions each wave.
 */
public class CrateSpawner {
    
    public void spawnCrates(List<Crate> crates) {
        // Fixed positions for crates
        crates.add(new Crate(200, Constants.WINDOW_HEIGHT - 100 - Constants.CRATE_HEIGHT));
        crates.add(new Crate(750, Constants.WINDOW_HEIGHT - 100 - Constants.CRATE_HEIGHT));
    }
}
