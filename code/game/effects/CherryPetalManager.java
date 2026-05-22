package game.effects;
import java.util.ArrayList; 
import java.util.List; 
import java.util.Random; 
import utils.Constants; 




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
