package entities;
import static utils.RaylibRenderer.*; 

import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import utils.Constants; 






public class Projectile {
    
    private double x, y;
    private double vx, vy;
    private int damage;
    private boolean facingRight;
    private boolean enemyProjectile;
    private int[] projectileColor; 
    private boolean flaming = false;
    
    public Projectile(double x, double y, double vx, double vy, int damage) {
        this(x, y, vx, vy, damage, false);
    }
    
    public Projectile(double x, double y, double vx, double vy, int damage, boolean isEnemy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.facingRight = vx > 0;
        this.enemyProjectile = isEnemy;
        this.projectileColor = isEnemy ? new int[]{139, 69, 19} : new int[]{20, 20, 25};
    }
    
    public void setFlaming(boolean flaming) {
        this.flaming = flaming;
    }
    
    public boolean isFlaming() {
        return flaming;
    }
    
    public void update() {
        x += vx;
        y += vy;
    }
    
    public void render() {
        int px = (int) x;
        int py = (int) y;
        
        Color mainColor = color(projectileColor[0], projectileColor[1], projectileColor[2], 255);
        
        if (enemyProjectile) {
            
            if (facingRight) {
                
                DrawRectangle(px - 15, py - 1, 25, 2, mainColor);
                
                int[] headX = {px + 10, px + 18, px + 10};
                int[] headY = {py - 4, py, py + 4};
                fillPolygon(headX, headY, 3, mainColor);
                
                Color fletchColor = flaming ? color(255, 100, 0, 255) : color(150, 150, 150, 255);
                DrawRectangle(px - 18, py - 3, 5, 6, fletchColor);
            } else {
                DrawRectangle(px - 10, py - 1, 25, 2, mainColor);
                int[] headX = {px - 10, px - 18, px - 10};
                int[] headY = {py - 4, py, py + 4};
                fillPolygon(headX, headY, 3, mainColor);
                Color fletchColor = flaming ? color(255, 100, 0, 255) : color(150, 150, 150, 255);
                DrawRectangle(px + 13, py - 3, 5, 6, fletchColor);
            }
            
            
            if (flaming) {
                for (int i = 0; i < 3; i++) {
                    int trailOffset = facingRight ? -(i * 6 + 8) : (i * 6 + 8);
                    int alpha = 180 - i * 50;
                    Color trailColor = color(255, 120 + i * 40, 0, alpha);
                    fillOval(px + trailOffset - 3, py - 2 + (int)(Math.random() * 3), 6 - i, 5 - i, trailColor);
                }
            }
        } else {
            
            if (facingRight) {
                
                int[] bladeX = {px, px + 16, px + 22, px + 16, px};
                int[] bladeY = {py - 2, py - 2, py, py + 2, py + 2};
                fillPolygon(bladeX, bladeY, 5, mainColor);
                
                
                DrawRectangle(px - 10, py - 2, 12, 4, mainColor);
                
                
                Color ringColor = color(40, 40, 45, 255);
                fillOval(px - 14, py - 3, 6, 6, ringColor);
            } else {
                
                int[] bladeX = {px, px - 16, px - 22, px - 16, px};
                int[] bladeY = {py - 2, py - 2, py, py + 2, py + 2};
                fillPolygon(bladeX, bladeY, 5, mainColor);
                
                
                DrawRectangle(px - 2, py - 2, 12, 4, mainColor);
                
                
                Color ringColor = color(40, 40, 45, 255);
                fillOval(px + 8, py - 3, 6, 6, ringColor);
            }
        }
    }
    
    


    public Rectangle getHitbox() {
        return rect((float)x - 12, (float)y - 3, 34, 6);
    }
    
    public boolean isOffScreen() {
        return x < -50 || x > Constants.WINDOW_WIDTH + 50 
            || y < -50 || y > Constants.WINDOW_HEIGHT + 50;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public boolean isEnemyProjectile() {
        return enemyProjectile;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelocityX() { return vx; }
    public double getVelocityY() { return vy; }
}
