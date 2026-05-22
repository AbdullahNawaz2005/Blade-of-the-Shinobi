package input;

import static com.raylib.Raylib.*; // Jaylib drawing and input functions

/**
 * Handles all keyboard input for the game using Raylib polling
 * Combat: Special Attack (Space), Throw Kunai (Q), Dodge (Shift+direction),
 * Block (Ctrl)
 */
public class InputHandler {

    // Movement keys
    public boolean up, down, left, right;

    // Action keys
    public boolean specialAttack, block, dodge;

    public InputHandler() {
        reset();
    }

    public final void reset() {
        up = down = left = right = false;
        specialAttack = block = dodge = false;
    }

    /**
     * Poll input state from Raylib - called once per frame
     * 
     */
    public void update() {
        // Movement - WASD and Arrow Keys (held state)
        up = IsKeyDown(KEY_W) || IsKeyDown(KEY_UP);
        down = IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN);
        left = IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT);
        right = IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT);

        // Actions - held state for continuous input
        specialAttack = IsKeyDown(KEY_SPACE);
        block = IsKeyDown(KEY_LEFT_CONTROL) || IsKeyDown(KEY_RIGHT_CONTROL);
        dodge = IsKeyDown(KEY_LEFT_SHIFT) || IsKeyDown(KEY_RIGHT_SHIFT);
    }

    /**
     * Check if a key was just pressed this frame
     * 
     * @param keyCode Raylib key code (e.g., KEY_SPACE, KEY_W)
     */
    public boolean isKeyJustPressed(int keyCode) {
        // Use Raylib's IsKeyPressed directly for accuracy
        return IsKeyPressed(keyCode);
    }

    /**
     * Check if a key is currently held down
     * 
     * @param keyCode Raylib key code (e.g., KEY_SPACE, KEY_W)
     */
    public boolean isKeyPressed(int keyCode) {
        return IsKeyDown(keyCode);
    }

    // Direction helper methods
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
