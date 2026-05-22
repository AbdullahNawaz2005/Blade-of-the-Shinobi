package input;

import static com.raylib.Raylib.*; 
import utils.Vector2D; 






public class MouseHandler {
    
    
    private Vector2D position;
    
    
    private boolean leftJustClicked, rightJustClicked, middleJustClicked;
    
    public MouseHandler() {
        position = new Vector2D();
        reset();
    }
    
    public final void reset() {
        leftJustClicked = rightJustClicked = middleJustClicked = false;
    }
    
    



    public void update() {
        
        Vector2 mousePos = GetMousePosition();
        position.set(mousePos.x(), mousePos.y());
        
        
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
