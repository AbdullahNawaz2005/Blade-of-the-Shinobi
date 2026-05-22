package game;

import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import input.InputHandler; // Keyboard input handling
import input.MouseHandler; // Mouse input handling
import utils.Constants; // Global game constants

/**
 * 
 * 
 * Uses Raylib for rendering and input polling instead of Java Swing/AWT. As ik
 * rayyylibbbb from lest sem
 * The actual while loop execution is driven externally by Main.java
 * via the WindowShouldClose() loop, which calls this class's update()
 * and render() methods sequentially every frame.
 */
public class GameLoop {

    // Input handlers for tracking keyboard and mouse state globally
    private InputHandler inputHandler;
    private MouseHandler mouseHandler;

    // The core game engine holding state machines, entities, and physics
    private GameEngine gameEngine;

    // Tracks frames rendered per second to display debug info
    private int currentFPS;

    /**
     * Constructs the GameLoop, initializing input handlers and the GameEngine.
     */
    public GameLoop() {
        // Initialize input handlers. Unlike Java AWT Event Listeners,
        // these are poll-based, querying Raylib's state every frame.
        inputHandler = new InputHandler();
        mouseHandler = new MouseHandler();

        // Pass references of the input handlers into the GameEngine so it can read user
        // inputs.
        gameEngine = new GameEngine(inputHandler, mouseHandler);

        System.out.println("[GameLoop] Initialized with Raylib backend");
    }

    /**
     * Updates all game logic and input tracking.
     * Called exactly once per frame from Main.java.
     */
    public void update() {
        // Query Raylib's internal FPS counter to track game performance
        currentFPS = GetFPS();

        // Poll input state from Raylib.
        // This updates internal boolean arrays mapping which keys are currently held
        // down or just pressed.
        inputHandler.update();
        mouseHandler.update();

        // Step the primary GameEngine logic forward by one frame (physics, AI,
        // collisions)
        gameEngine.update();
    }

    /**
     * Renders the current frame.
     * Must be called between BeginDrawing() and EndDrawing() in Main.java.
     */
    public void render() {
        // Delegate all complex rendering (entities, UI, background) to the GameEngine.
        // GameEngine handles its own camera translations, such as screen shake.
        gameEngine.render();

        // Draw the current FPS counter in the bottom-left corner.
        // Useful for profiling drops in framerate when too many particles or entities
        // are active.
        DrawText("FPS: " + currentFPS, 10, Constants.WINDOW_HEIGHT - 20, 11,
                color(102, 102, 136, 255));
    }

    /**
     * Cleans up hardware resources (audio clips, texture VRAM) before exiting the
     * application.
     */
    public void cleanup() {
        // Delegate cleanup to GameEngine, which owns the SoundManager and other systems
        gameEngine.cleanup();
        System.out.println("[GameLoop] Cleanup complete");
    }
}
