package game.ui;
import static utils.RaylibRenderer.*; 
import static com.raylib.Raylib.*; 
import com.raylib.Raylib.Color; 
import java.util.List; 
import java.util.Random; 
import entities.Player; 
import entities.Enemy; 
import entities.Projectile; 
import entities.enemies.Boss; 
import entities.enemies.ShieldBearer; 
import powerups.PowerUp; 
import utils.Constants; 
import utils.Platform; 
import utils.RaylibRenderer; 
import game.effects.*; 
import game.entities.Crate; 
import game.state.GameSession; 
import game.state.WaveManager; 





public class GameRenderer {

    public void renderPlaying(Player player, List<Enemy> enemies, List<Projectile> projectiles,
                              List<PowerUp> powerUps, List<Crate> crates,
                              ParticleManager particleManager,
                              DamageFlash damageFlash, GameSession session,
                              WaveManager waveManager, Random random,
                              WaveMessageRenderer waveMessageRenderer, HUDRenderer hudRenderer,
                              input.MouseHandler mouse) {
        RaylibRenderer.resetShakeOffset();
        renderBackground();
        renderBattleground();
        renderVignette(enemies, damageFlash);
        renderPlatforms();
        for (Crate crate : crates) { crate.render(); }
        for (PowerUp p : powerUps) { p.render(); }
        player.render();
        for (Projectile p : projectiles) { p.render(); }
        for (Enemy enemy : enemies) {
            if (enemy instanceof ShieldBearer) {
                ((ShieldBearer) enemy).render(!session.firstShieldBearerKilled);
            } else {
                enemy.render();
            }
        }
        particleManager.render();
        RaylibRenderer.resetShakeOffset();
        if (session.comboFinisherFlashTimer > 0) {
            int alpha = (int)(200 * ((double)session.comboFinisherFlashTimer / Constants.COMBO_FINISHER_FLASH_FRAMES));
            DrawRectangle(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, color(255, 255, 255, alpha));
        }
        if (waveManager.waveCompleteMessageShown && waveManager.waveTransitioning) {
            waveMessageRenderer.renderWaveCompleteMessage(session.wave, waveManager.waveTransitionStartTime);
        }
        if (waveManager.showingWaveAnnounce) {
            waveMessageRenderer.renderWaveAnnouncement(session.wave, waveManager);
        }
        if (session.showFinisherText) {
            waveMessageRenderer.renderFinisherText(session.finisherTextStartTime);
        }
        hudRenderer.renderHUD(player, session, mouse, enemies.size());
    }

    public void renderBackground() {
        int groundLevel = Constants.WINDOW_HEIGHT - 100;
        int shakeX = RaylibRenderer.getShakeOffsetX();
        int shakeY = RaylibRenderer.getShakeOffsetY();
        DrawRectangleGradientV(shakeX, shakeY, Constants.WINDOW_WIDTH, groundLevel,
            color(10, 15, 35, 255), color(20, 50, 50, 255));
        
        
        Random starRand = new Random(12345);
        for (int i = 0; i < 100; i++) {
            int x = starRand.nextInt(Constants.WINDOW_WIDTH) + shakeX;
            int y = starRand.nextInt(groundLevel - 50) + shakeY;
            int brightness = 150 + starRand.nextInt(100);
            int size = starRand.nextInt(3) + 1;
            int blue = Math.min(255, brightness + 15);
            if (size == 1) {
                DrawRectangle(x, y, 1, 1, color(brightness, brightness, blue, 255));
            } else {
                fillOval(x, y, size, size, color(brightness, brightness, blue, 255));
            }
        }
        renderMoon(shakeX, shakeY);
        renderStaticClouds(shakeX, shakeY);
        int[] m1x = {-50 + shakeX, 120 + shakeX, 290 + shakeX};
        int[] m1y = {groundLevel + shakeY, groundLevel - 200 + shakeY, groundLevel + shakeY};
        fillPolygon(m1x, m1y, 3, color(15, 20, 30, 255));
        int[] m2x = {150 + shakeX, 350 + shakeX, 550 + shakeX};
        int[] m2y = {groundLevel + shakeY, groundLevel - 250 + shakeY, groundLevel + shakeY};
        fillPolygon(m2x, m2y, 3, color(20, 25, 35, 255));
        int[] m3x = {450 + shakeX, 680 + shakeX, 910 + shakeX};
        int[] m3y = {groundLevel + shakeY, groundLevel - 220 + shakeY, groundLevel + shakeY};
        fillPolygon(m3x, m3y, 3, color(15, 20, 30, 255));
        int[] snow1x = {320 + shakeX, 350 + shakeX, 380 + shakeX};
        int[] snow1y = {groundLevel - 220 + shakeY, groundLevel - 250 + shakeY, groundLevel - 220 + shakeY};
        fillPolygon(snow1x, snow1y, 3, color(180, 185, 195, 255));
        renderPagodaSilhouette(shakeX, shakeY, groundLevel);
        renderFogLayer(shakeX, shakeY, groundLevel);
    }

    private void renderStaticClouds(int shakeX, int shakeY) {
        int baseAlpha = 60; int r = 40; int g = 50; int b = 60;
        int c1x = 100 + shakeX;
        fillOval(c1x, 60 + shakeY, 120, 45, color(r, g, b, baseAlpha - 40));
        fillOval(c1x + 40, 50 + shakeY, 90, 55, color(r, g, b, baseAlpha - 40));
        fillOval(c1x + 70, 65 + shakeY, 100, 40, color(r, g, b, baseAlpha - 40));
        int c2x = 400 + shakeX;
        fillOval(c2x, 100 + shakeY, 140, 50, color(r, g, b, baseAlpha - 20));
        fillOval(c2x + 50, 85 + shakeY, 100, 60, color(r, g, b, baseAlpha - 20));
        fillOval(c2x + 90, 105 + shakeY, 110, 45, color(r, g, b, baseAlpha - 20));
        int c3x = 700 + shakeX;
        fillOval(c3x, 140 + shakeY, 100, 35, color(r, g, b, baseAlpha));
        fillOval(c3x + 30, 130 + shakeY, 80, 45, color(r, g, b, baseAlpha));
    }

    private void renderMoon(int shakeX, int shakeY) {
        int moonX = Constants.WINDOW_WIDTH - 120 + shakeX;
        int moonY = 40 + shakeY;
        fillOval(moonX - 30, moonY - 30, 130, 130, color(255, 250, 220, 15));
        fillOval(moonX - 15, moonY - 15, 100, 100, color(255, 250, 220, 25));
        fillOval(moonX - 5, moonY - 5, 80, 80, color(255, 250, 230, 40));
        fillOval(moonX, moonY, 70, 70, color(255, 250, 230, 255));
        fillOval(moonX + 10, moonY + 10, 15, 15, color(230, 225, 200, 255));
        fillOval(moonX + 35, moonY + 30, 10, 10, color(230, 225, 200, 255));
        fillOval(moonX + 20, moonY + 40, 8, 8, color(235, 230, 205, 255));
    }

    private void renderPagodaSilhouette(int shakeX, int shakeY, int groundLevel) {
        int pagodaX = Constants.WINDOW_WIDTH - 280 + shakeX;
        int pagodaBase = groundLevel - 20 + shakeY;
        Color silColor = color(10, 12, 18, 255);
        DrawRectangle(pagodaX + 20, pagodaBase - 30, 80, 30, silColor);
        DrawRectangle(pagodaX + 25, pagodaBase - 60, 70, 30, silColor);
        int[] roof1x = {pagodaX + 5, pagodaX + 60, pagodaX + 115};
        int[] roof1y = {pagodaBase - 60, pagodaBase - 80, pagodaBase - 60};
        fillPolygon(roof1x, roof1y, 3, silColor);
        DrawRectangle(pagodaX + 30, pagodaBase - 100, 60, 40, silColor);
        int[] roof2x = {pagodaX + 15, pagodaX + 60, pagodaX + 105};
        int[] roof2y = {pagodaBase - 100, pagodaBase - 125, pagodaBase - 100};
        fillPolygon(roof2x, roof2y, 3, silColor);
        DrawRectangle(pagodaX + 40, pagodaBase - 145, 40, 45, silColor);
        int[] roof3x = {pagodaX + 25, pagodaX + 60, pagodaX + 95};
        int[] roof3y = {pagodaBase - 145, pagodaBase - 170, pagodaBase - 145};
        fillPolygon(roof3x, roof3y, 3, silColor);
        DrawRectangle(pagodaX + 55, pagodaBase - 195, 10, 25, silColor);
        int[] spirex = {pagodaX + 50, pagodaX + 60, pagodaX + 70};
        int[] spirey = {pagodaBase - 195, pagodaBase - 210, pagodaBase - 195};
        fillPolygon(spirex, spirey, 3, silColor);
    }

    private void renderFogLayer(int shakeX, int shakeY, int groundLevel) {
        int fogAlpha = 50; int fogHeight = 80;
        int fogY = groundLevel - fogHeight + shakeY;
        for (int i = 0; i < 4; i++) {
            int stripAlpha = fogAlpha - (i * 10);
            if (stripAlpha < 5) stripAlpha = 5;
            int stripY = fogY + (i * 20); int stripHeight = 25;
            DrawRectangle(shakeX, stripY, Constants.WINDOW_WIDTH, stripHeight, color(180, 200, 255, stripAlpha));
        }
    }

    private void renderVignette(List<Enemy> enemies, DamageFlash damageFlash) {
        int w = Constants.WINDOW_WIDTH; int h = Constants.WINDOW_HEIGHT;
        int shakeX = RaylibRenderer.getShakeOffsetX();
        int shakeY = RaylibRenderer.getShakeOffsetY();
        
        
        int vignetteAlpha = Constants.VIGNETTE_ALPHA / 2;
        DrawRectangleGradientV(shakeX, shakeY, w, 100, color(0, 0, 0, vignetteAlpha), color(0, 0, 0, 0));
        DrawRectangleGradientV(shakeX, h - 100 + shakeY, w, 100, color(0, 0, 0, 0), color(0, 0, 0, vignetteAlpha));
        DrawRectangleGradientH(shakeX, shakeY, 100, h, color(0, 0, 0, vignetteAlpha), color(0, 0, 0, 0));
        DrawRectangleGradientH(w - 100 + shakeX, shakeY, 100, h, color(0, 0, 0, 0), color(0, 0, 0, vignetteAlpha));
        for (Enemy enemy : enemies) {
            int ex = (int)enemy.getCenterX() + shakeX;
            int ey = (int)enemy.getCenterY() + shakeY;
            if (enemy instanceof Boss) {
                int bRadius = Constants.BOSS_LIGHT_RADIUS;
                for (int r = bRadius; r > 0; r -= 20) {
                    int alpha = (int)(25 * ((double)(bRadius - r) / bRadius));
                    fillCircle(ex, ey, r, color(255, 80, 60, alpha));
                }
            } else {
                int eRadius = 50;
                for (int r = eRadius; r > 0; r -= 15) {
                    int alpha = (int)(12 * ((double)(eRadius - r) / eRadius));
                    fillCircle(ex, ey, r, color(255, 120, 80, alpha));
                }
            }
        }
        if (damageFlash.isActive()) {
            damageFlash.decrement();
            int flashAlpha = (int)(150 * ((double)damageFlash.getTimer() / damageFlash.getDuration()));
            int edgeSize = 120;
            DrawRectangleGradientV(0, 0, w, edgeSize, color(180, 0, 0, flashAlpha), color(180, 0, 0, 0));
            DrawRectangleGradientV(0, h - edgeSize, w, edgeSize, color(180, 0, 0, 0), color(180, 0, 0, flashAlpha));
            DrawRectangleGradientH(0, 0, edgeSize, h, color(180, 0, 0, flashAlpha), color(180, 0, 0, 0));
            DrawRectangleGradientH(w - edgeSize, 0, edgeSize, h, color(180, 0, 0, 0), color(180, 0, 0, flashAlpha));
        }
    }

    private void renderPlatforms() {
        int shakeX = RaylibRenderer.getShakeOffsetX();
        int shakeY = RaylibRenderer.getShakeOffsetY();
        for (Platform p : Constants.PLATFORMS) {
            int groundY = Constants.WINDOW_HEIGHT - 100;
            int pillarHeight = groundY - p.y - p.height;
            DrawRectangle(p.x + 20 + shakeX, p.y + p.height + shakeY, 15, pillarHeight, color(80, 60, 45, 255));
            for (int i = 0; i < pillarHeight; i += 12) {
                DrawLine(p.x + 22 + shakeX, p.y + p.height + i + shakeY, p.x + 33 + shakeX, p.y + p.height + i + shakeY, color(70, 50, 35, 255));
            }
            DrawRectangle(p.x + p.width - 35 + shakeX, p.y + p.height + shakeY, 15, pillarHeight, color(80, 60, 45, 255));
            for (int i = 0; i < pillarHeight; i += 12) {
                DrawLine(p.x + p.width - 33 + shakeX, p.y + p.height + i + shakeY, p.x + p.width - 22 + shakeX, p.y + p.height + i + shakeY, color(70, 50, 35, 255));
            }
            DrawRectangle(p.x + shakeX, p.y + shakeY, p.width, p.height, color(100, 75, 55, 255));
            for (int i = 5; i < p.height - 3; i += 5) {
                DrawLine(p.x + 3 + shakeX, p.y + i + shakeY, p.x + p.width - 3 + shakeX, p.y + i + shakeY, color(90, 65, 45, 200));
            }
            DrawRectangle(p.x + shakeX, p.y + p.height - 4 + shakeY, p.width, 4, color(60, 45, 35, 255));
            DrawRectangle(p.x + shakeX, p.y + shakeY, p.width, 5, color(120, 90, 65, 255));
            Random grassRand = new Random(p.x * 100 + p.y);
            int grassCount = p.width / 15;
            for (int i = 0; i < grassCount; i++) {
                int gx = p.x + 5 + grassRand.nextInt(p.width - 10) + shakeX;
                int gy = p.y + shakeY;
                int gh = 4 + grassRand.nextInt(6); int gw = 2 + grassRand.nextInt(2);
                int greenVar = grassRand.nextInt(40);
                Color grassColor = color(40 + greenVar, 80 + greenVar, 30, 255);
                DrawRectangle(gx, gy - gh, gw, gh + 2, grassColor);
            }
            int mossCount = p.width / 40;
            for (int i = 0; i < mossCount; i++) {
                int mx = p.x + 10 + grassRand.nextInt(p.width - 20) + shakeX;
                int my = p.y + 3 + shakeY;
                int mw = 8 + grassRand.nextInt(12); int mh = 3 + grassRand.nextInt(3);
                fillOval(mx, my, mw, mh, color(50, 75, 40, 180));
            }
        }
    }

    public void renderBattleground() {
        int groundY = Constants.WINDOW_HEIGHT - 100;
        int shakeX = RaylibRenderer.getShakeOffsetX();
        int shakeY = RaylibRenderer.getShakeOffsetY();
        int w = Constants.WINDOW_WIDTH;
        DrawRectangle(shakeX, groundY + 15 + shakeY, w, 85, color(60, 45, 30, 255));
        Random dirtRand = new Random(11111);
        for (int i = 0; i < 30; i++) {
            int dx = dirtRand.nextInt(w) + shakeX; int dy = groundY + 20 + dirtRand.nextInt(70) + shakeY;
            int dw = 15 + dirtRand.nextInt(30); int dh = 5 + dirtRand.nextInt(10);
            int shade = dirtRand.nextInt(15);
            fillOval(dx, dy, dw, dh, color(50 - shade, 35 - shade, 20, 150));
        }
        for (int i = 0; i < 20; i++) {
            int sx = dirtRand.nextInt(w) + shakeX; int sy = groundY + 25 + dirtRand.nextInt(60) + shakeY;
            int sr = 2 + dirtRand.nextInt(4); int gray = 70 + dirtRand.nextInt(30);
            fillOval(sx, sy, sr * 2, sr, color(gray, gray - 10, gray - 15, 255));
        }
        DrawRectangle(shakeX, groundY + shakeY, w, 18, color(45, 90, 35, 255));
        DrawRectangle(shakeX, groundY + shakeY, w, 5, color(60, 110, 45, 255));
        DrawRectangle(shakeX, groundY + 13 + shakeY, w, 5, color(35, 70, 25, 255));
        Random grassRand = new Random(54321);
        for (int i = 0; i < 150; i++) {
            int x = grassRand.nextInt(w) + shakeX; int h = 8 + grassRand.nextInt(14);
            int greenShade = 70 + grassRand.nextInt(50);
            int sway = grassRand.nextInt(5) - 2; int thickness = grassRand.nextInt(2) + 1;
            for (int t = 0; t < thickness; t++) {
                DrawLine(x + t, groundY + shakeY, x + sway + t, groundY - h + shakeY,
                    color(30 + grassRand.nextInt(20), greenShade, 25 + grassRand.nextInt(15), 255));
            }
        }
        Random flowerRand = new Random(99999);
        for (int i = 0; i < 8; i++) {
            int fx = flowerRand.nextInt(w) + shakeX; int fy = groundY - 5 + shakeY;
            DrawLine(fx, fy + 5, fx, fy - 3, color(40, 80, 30, 255));
            int flowerType = flowerRand.nextInt(3);
            Color flowerColor;
            if (flowerType == 0) { flowerColor = color(255, 255, 150, 255); }
            else if (flowerType == 1) { flowerColor = color(255, 200, 200, 255); }
            else { flowerColor = color(200, 200, 255, 255); }
            fillOval(fx - 2, fy - 5, 5, 5, flowerColor);
        }
    }
}
