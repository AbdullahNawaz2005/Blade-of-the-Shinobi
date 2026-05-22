package game;

import static utils.RaylibRenderer.*; 
import static com.raylib.Raylib.*; 
import input.InputHandler; 
import input.MouseHandler; 
import utils.Constants; 










public class GameLoop {

    
    private InputHandler inputHandler;
    private MouseHandler mouseHandler;

    
    private GameEngine gameEngine;

    
    private int currentFPS;

    


    public GameLoop() {
        
        
        inputHandler = new InputHandler();
        mouseHandler = new MouseHandler();

        
        
        gameEngine = new GameEngine(inputHandler, mouseHandler);

        System.out.println("[GameLoop] Initialized with Raylib backend");
    }

    



    public void update() {
        
        currentFPS = GetFPS();

        
        
        
        inputHandler.update();
        mouseHandler.update();

        
        
        gameEngine.update();
    }

    



    public void render() {
        
        
        gameEngine.render();

        
        
        
        DrawText("FPS: " + currentFPS, 10, Constants.WINDOW_HEIGHT - 20, 11,
                color(102, 102, 136, 255));
    }

    



    public void cleanup() {
        
        gameEngine.cleanup();
        System.out.println("[GameLoop] Cleanup complete");
    }
}
