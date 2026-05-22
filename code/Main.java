import static com.raylib.Raylib.*; 
import static utils.RaylibRenderer.*; 
import game.GameLoop; 
import utils.Constants; 






public class Main {

    private static boolean isFullscreen = true;

    





    public static void main(String[] args) {
        
        
        
        
        SetConfigFlags(FLAG_VSYNC_HINT | FLAG_FULLSCREEN_MODE);

        
        
        InitWindow(1280, 720, Constants.GAME_TITLE);

        
        
        
        
        SetExitKey(0);

        
        int monitor = GetCurrentMonitor(); 
        int monitorWidth = GetMonitorWidth(monitor); 
        int monitorHeight = GetMonitorHeight(monitor); 

        
        
        if (monitorWidth > 0 && monitorHeight > 0) {
            Constants.setScreenDimensions(monitorWidth, monitorHeight);
            SetWindowSize(monitorWidth, monitorHeight);
        } else {
            
            
            int screenW = GetScreenWidth();
            int screenH = GetScreenHeight();
            Constants.setScreenDimensions(screenW, screenH);
        }

        
        
        SetTargetFPS(Constants.TARGET_FPS);

        
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

        
        GameLoop gameLoop = new GameLoop();

        
        
        while (!WindowShouldClose()) {
            
            if (IsKeyPressed(KEY_F11)) {
                toggleFullscreen();
            }

            
            gameLoop.update();

            
            BeginDrawing();

            
            ClearBackground(color(
                    Constants.COLOR_BACKGROUND[0],
                    Constants.COLOR_BACKGROUND[1],
                    Constants.COLOR_BACKGROUND[2],
                    255));

            
            gameLoop.render();

            
            EndDrawing();
        }

        
        gameLoop.cleanup();
        CloseWindow();
    }

    



    private static void toggleFullscreen() {
        
        isFullscreen = !isFullscreen;

        if (isFullscreen) {
            
            int monitor = GetCurrentMonitor();
            int monitorWidth = GetMonitorWidth(monitor);
            int monitorHeight = GetMonitorHeight(monitor);
            Constants.setScreenDimensions(monitorWidth, monitorHeight);
            SetWindowSize(monitorWidth, monitorHeight);
            ToggleFullscreen(); 
        } else {
            
            ToggleFullscreen(); 
            Constants.setScreenDimensions(1024, 768);
            SetWindowSize(1024, 768);
            SetWindowPosition(100, 100); 
        }
    }
}
