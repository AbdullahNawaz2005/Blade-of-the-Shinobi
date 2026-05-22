package utils;
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import utils.RaylibRenderer; // Rendering utilities

import static com.raylib.Raylib.Rectangle; // Imports Rectangle functionality

/**
 * Platform record for level geometry
 * Uses Raylib Rectangle for collision detection
 */
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
    
    /**
     * Convert to Raylib Rectangle for collision detection
     */
    public Rectangle toRectangle() {
        return rect(x, y, width, height);
    }
}
