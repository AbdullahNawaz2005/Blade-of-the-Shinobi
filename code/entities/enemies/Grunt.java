package entities.enemies;
import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import entities.Enemy; 
import utils.Constants; 
import utils.RaylibRenderer; 





public class Grunt extends Enemy {
    
    public Grunt(double x, double y) {
        super(x, y, Constants.GRUNT_WIDTH, Constants.GRUNT_HEIGHT);
        
        this.health = Constants.GRUNT_HEALTH;
        this.maxHealth = Constants.GRUNT_HEALTH;
        this.attackDamage = Constants.GRUNT_DAMAGE;
        this.moveSpeed = Constants.GRUNT_SPEED;
        this.detectionRange = Constants.GRUNT_DETECTION_RANGE;
        this.attackRange = Constants.GRUNT_ATTACK_RANGE;
        this.attackCooldownTime = Constants.GRUNT_ATTACK_COOLDOWN_MS;
        this.scoreValue = Constants.GRUNT_SCORE;
        this.canJumpToPlatform = false;
    }
    
    @Override
    public void render() {
        int x = (int) position.x;
        int y = (int) position.y;
        
        
        int alpha = hitFlashTimer > 0 ? 178 : 255;
        
        
        int[] gruntRGB = {120, 80, 60};
        
        if (knockedBack) {
            gruntRGB = new int[]{180, 100, 80};
        } else if (attacking) {
            gruntRGB = new int[]{150, 90, 70};
        }
        
        Color gruntColor = color(gruntRGB[0], gruntRGB[1], gruntRGB[2], alpha);
        
        
        fillRoundRect(x + 5, y + 18, width - 10, height - 28, 6, gruntColor);
        
        
        fillOval(x + 6, y, 28, 26, gruntColor);
        
        
        Color blackColor = color(0, 0, 0, alpha);
        if (facingRight) {
            DrawLine(x + 18, y + 8, x + 28, y + 6, blackColor);
            fillOval(x + 20, y + 10, 6, 4, blackColor);
        } else {
            DrawLine(x + 12, y + 6, x + 22, y + 8, blackColor);
            fillOval(x + 14, y + 10, 6, 4, blackColor);
        }
        
        
        DrawRectangle(x + 3, y + 5, 34, 4, color(100, 50, 50, alpha));
        
        
        if (attacking) {
            
            if (facingRight) {
                DrawRectangle(x + width - 2, y + 25, 25, 8, gruntColor);
            } else {
                DrawRectangle(x - 23, y + 25, 25, 8, gruntColor);
            }
        } else {
            DrawRectangle(x + 2, y + 22, 8, 18, gruntColor);
            DrawRectangle(x + width - 10, y + 22, 8, 18, gruntColor);
        }
        
        
        DrawRectangle(x + 10, y + height - 18, 7, 18, gruntColor);
        DrawRectangle(x + 23, y + height - 18, 7, 18, gruntColor);
        
        
        if (hitFlashTimer > 0) {
            Color whiteFlash = color(255, 255, 255, 150);
            fillRoundRect(x + 5, y + 18, width - 10, height - 28, 6, whiteFlash);
            fillOval(x + 6, y, 28, 26, whiteFlash);
        }
        
        
        renderHealthBar();
    }
}
