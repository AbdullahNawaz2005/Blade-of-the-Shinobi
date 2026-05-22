package input;

import static com.raylib.Raylib.*; 






public class InputHandler {

    
    public boolean up, down, left, right;

    
    public boolean specialAttack, block, dodge;

    public InputHandler() {
        reset();
    }

    public final void reset() {
        up = down = left = right = false;
        specialAttack = block = dodge = false;
    }

    



    public void update() {
        
        up = IsKeyDown(KEY_W) || IsKeyDown(KEY_UP);
        down = IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN);
        left = IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT);
        right = IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT);

        
        specialAttack = IsKeyDown(KEY_SPACE);
        block = IsKeyDown(KEY_LEFT_CONTROL) || IsKeyDown(KEY_RIGHT_CONTROL);
        dodge = IsKeyDown(KEY_LEFT_SHIFT) || IsKeyDown(KEY_RIGHT_SHIFT);
    }

    




    public boolean isKeyJustPressed(int keyCode) {
        
        return IsKeyPressed(keyCode);
    }

    




    public boolean isKeyPressed(int keyCode) {
        return IsKeyDown(keyCode);
    }

    
    public int getHorizontalDirection() {
        if (left && !right)
            return -1;
        if (right && !left)
            return 1;
        return 0;
    }

    public int getVerticalDirection() {
        if (up && !down)
            return -1;
        if (down && !up)
            return 1;
        return 0;
    }
}
