package game.spawn;
import java.util.List; 
import utils.Constants; 
import game.entities.Crate; 




public class CrateSpawner {
    
    public void spawnCrates(List<Crate> crates) {
        
        crates.add(new Crate(200, Constants.WINDOW_HEIGHT - 100 - Constants.CRATE_HEIGHT));
        crates.add(new Crate(750, Constants.WINDOW_HEIGHT - 100 - Constants.CRATE_HEIGHT));
    }
}
