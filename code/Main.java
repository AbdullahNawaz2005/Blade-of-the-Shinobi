import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import static utils.RaylibRenderer.*; // Custom raylib rendering wrappers
import game.GameLoop; // Imports GameLoop functionality
import utils.Constants; // Global game constants

/**
 * Main entry point for the Blade of the Shinobi game.
 * Responsible for initializing the Raylib window, setting up screen dimensions,
 * handling the primary game loop, and triggering resource cleanup on exit.
 */
public class Main {

    private static boolean isFullscreen = true;

    /**
     * Main method. Initializes the game window, sets up the game loop, and handles
     * frame updates.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Configure window flags before creation:
        // FLAG_VSYNC_HINT attempts to synchronize the framerate with the monitor
        // refresh rate to prevent screen tearing.
        // FLAG_FULLSCREEN_MODE starts the application in fullscreen mode.
        SetConfigFlags(FLAG_VSYNC_HINT | FLAG_FULLSCREEN_MODE);

        // Initialize window with default size first (Raylib needs an active context to
        // query monitor info)
        InitWindow(1280, 720, Constants.GAME_TITLE);

        // Disable Raylib's default ESC-to-close behavior.
        // We set it to 0 (no key) so we can handle ESC manually inside our own
        // GameEngine state machine
        // (e.g., using ESC to pause the game instead of instantly closing it).
        SetExitKey(0);

        // Retrieve actual monitor dimensions to scale the game dynamically
        int monitor = GetCurrentMonitor(); // Gets the primary/current monitor ID
        int monitorWidth = GetMonitorWidth(monitor); // Queries the monitor's native width
        int monitorHeight = GetMonitorHeight(monitor); // Queries the monitor's native height

        // If monitor dimensions are successfully retrieved, set the game window to
        // match native resolution.
        if (monitorWidth > 0 && monitorHeight > 0) {
            Constants.setScreenDimensions(monitorWidth, monitorHeight);
            SetWindowSize(monitorWidth, monitorHeight);
        } else {
            // Fallback: If GetMonitorWidth fails, use the current active screen dimensions
            // instead.
            int screenW = GetScreenWidth();
            int screenH = GetScreenHeight();
            Constants.setScreenDimensions(screenW, screenH);
        }

        // Cap the framerate to the targeted FPS (typically 60) to keep game speed
        // consistent
        SetTargetFPS(Constants.TARGET_FPS);

        // Print startup info to standard output for debugging purposes
        System.out.println("==========================================");
        System.out.println("        BLADE OF THE SHINOBI");
        System.out.println("==========================================");
        System.out.println("Screen: " + Constants.WINDOW_WIDTH + "x" + Constants.WINDOW_HEIGHT);
        System.out.println("==========================================");
        System.out.println("Controls:");
        System.out.println("  WASD / Arrows - Move");
        System.out.println("  Space         - Special Attack");
        System.out.println("  Q             - Throw Kunai");
        System.out.println("  Shift + Dir   - Dodge");
        System.out.println("  Ctrl          - Block");
        System.out.println("  ESC / P       - Pause");
        System.out.println("  F11           - Toggle Fullscreen");
        System.out.println("==========================================");

        // Instantiate the main game controller which holds all game state and entities
        GameLoop gameLoop = new GameLoop();

        // Main game loop: Continues running until the OS or Raylib signals a window
        // close event (e.g., ALT+F4)
        while (!WindowShouldClose()) {
            // Check if the F11 key was pressed this frame to toggle fullscreen mode
            if (IsKeyPressed(KEY_F11)) {
                toggleFullscreen();
            }

            // Update game logic (physics, AI, input, state machine) for the current frame
            gameLoop.update();

            // Begin rendering the frame buffer
            BeginDrawing();

            // Clear the previous frame with the default background color
            ClearBackground(color(
                    Constants.COLOR_BACKGROUND[0],
                    Constants.COLOR_BACKGROUND[1],
                    Constants.COLOR_BACKGROUND[2],
                    255));

            // Delegate all entity and UI rendering to the GameLoop
            gameLoop.render();

            // Swap buffers and display the rendered frame to the screen
            EndDrawing();
        }

        // Cleanup resources (audio devices, textures, etc.) before the JVM terminates
        gameLoop.cleanup();
        CloseWindow();
    }

    /**
     * Toggles the window between fullscreen and a 1024x768 windowed mode.
     * Updates internal screen dimension constants when switching.
     */
    private static void toggleFullscreen() {
        // Invert the fullscreen tracking boolean
        isFullscreen = !isFullscreen;

        if (isFullscreen) {
            // Switching TO fullscreen: Query monitor size and stretch window
            int monitor = GetCurrentMonitor();
            int monitorWidth = GetMonitorWidth(monitor);
            int monitorHeight = GetMonitorHeight(monitor);
            Constants.setScreenDimensions(monitorWidth, monitorHeight);
            SetWindowSize(monitorWidth, monitorHeight);
            ToggleFullscreen(); // Raylib internal toggle
        } else {
            // Switching TO windowed: Exit fullscreen first, then shrink to 1024x768
            ToggleFullscreen(); // Raylib internal toggle out of fullscreen
            Constants.setScreenDimensions(1024, 768);
            SetWindowSize(1024, 768);
            SetWindowPosition(100, 100); // Center the window roughly on screen
        }
    }
}
