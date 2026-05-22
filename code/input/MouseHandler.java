package input;

import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import utils.Vector2D; // 2D math vector

/**
 * Handles all mouse input for combat and UI interaction using Raylib polling
 * 
 * Converted from MouseListener to Raylib's IsMouseButtonPressed/GetMousePosition
 */
public class MouseHandler {
    
    // Mouse position
    private Vector2D position;
    
    // Click tracking for single-press detection
    private boolean leftJustClicked, rightJustClicked, middleJustClicked;
    
    public MouseHandler() {
        position = new Vector2D();
        reset();
    }
    
    public final void reset() {
        leftJustClicked = rightJustClicked = middleJustClicked = false;
    }
    
    /**
     * Poll mouse state from Raylib - called once per frame
     * This replaces the old event-based MouseListener approach
     */
    public void update() {
        // Get mouse position from Raylib
        Vector2 mousePos = GetMousePosition();
        position.set(mousePos.x(), mousePos.y());
        
        // Just clicked this frame (single press)
        leftJustClicked = IsMouseButtonPressed(MOUSE_BUTTON_LEFT);
        rightJustClicked = IsMouseButtonPressed(MOUSE_BUTTON_RIGHT);
        middleJustClicked = IsMouseButtonPressed(MOUSE_BUTTON_MIDDLE);
    }
    
    public double getX() {
        return position.x;
    }
    
    public double getY() {
        return position.y;
    }
    
    public boolean isLeftJustClicked() {
        return leftJustClicked;
    }
}
