package utils;
import static utils.RaylibRenderer.*; 
import utils.RaylibRenderer; 

import static com.raylib.Raylib.Rectangle; 





public class Platform {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    
    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    


    public Rectangle toRectangle() {
        return rect(x, y, width, height);
    }
}
