package utils;

import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import com.raylib.Colors; // Imports Colors functionality
import com.raylib.Raylib.Color; // Raylib color struct
import com.raylib.Raylib.Rectangle; // Raylib rectangle struct

/**
 * Raylib rendering helper utilities
 * 
 * Provides convenience methods that mirror common Graphics2D operations
 * to ease the migration from AWT/Swing to Raylib
 */
public class RaylibRenderer {
    
    // ==========================================
    // COLOR CONVERSION (new Jaylib 5.5 API)
    // ==========================================
    
    /**
     * Create a Raylib Color from RGB values (0-255)
     */
    public static Color color(int r, int g, int b) {
        return new Color().r((byte)r).g((byte)g).b((byte)b).a((byte)255);
    }
    
    /**
     * Create a Raylib Color from RGBA values (0-255)
     */
    public static Color color(int r, int g, int b, int a) {
        return new Color().r((byte)r).g((byte)g).b((byte)b).a((byte)a);
    }
    
    
    /**
     * Create a Raylib Rectangle
     */
    public static Rectangle rect(float x, float y, float w, float h) {
        return new Rectangle().x(x).y(y).width(w).height(h);
    }
    
    // ==========================================
    // COLLISION DETECTION
    // ==========================================
    
    /**
     * Check if two rectangles intersect
     */
    public static boolean intersects(Rectangle a, Rectangle b) {
        return CheckCollisionRecs(a, b);
    }
    
    /**
     * Check if two rectangles (as int arrays [x,y,w,h]) intersect
     */
    public static boolean intersects(int[] a, int[] b) {
        return CheckCollisionRecs(rect(a[0], a[1], a[2], a[3]), rect(b[0], b[1], b[2], b[3]));
    }
    
    /**
     * Check if Rectangle intersects with int array hitbox
     */
    public static boolean intersects(Rectangle a, int[] b) {
        return CheckCollisionRecs(a, rect(b[0], b[1], b[2], b[3]));
    }
    
    /**
     * Check if int array hitbox intersects with Rectangle
     */
    public static boolean intersects(int[] a, Rectangle b) {
        return CheckCollisionRecs(rect(a[0], a[1], a[2], a[3]), b);
    }
    
    // ==========================================
    // RECTANGLE DRAWING
    // ==========================================
    
    
    /**
     * Draw a rectangle outline (replaces Graphics2D.drawRect)
     */
    public static void drawRect(int x, int y, int width, int height, Color color) {
        DrawRectangleLines(x, y, width, height, color);
    }
    
    /**
     * Fill a rounded rectangle (replaces Graphics2D.fillRoundRect)
     */
    public static void fillRoundRect(int x, int y, int width, int height, int radius, Color color) {
        DrawRectangleRounded(
            rect(x, y, width, height),
            (float) radius / Math.min(width, height),
            8, // segments
            color
        );
    }
    
    /**
     * Draw a rounded rectangle outline (replaces Graphics2D.drawRoundRect)
     */
    public static void drawRoundRect(int x, int y, int width, int height, int radius, float lineThickness, Color color) {
        DrawRectangleRoundedLinesEx(
            rect(x, y, width, height),
            (float) radius / Math.min(width, height),
            8, // segments
            lineThickness,
            color
        );
    }
    
    // ==========================================
    // ELLIPSE/CIRCLE DRAWING
    // ==========================================
    
    /**
     * Fill an ellipse (replaces Graphics2D.fillOval)
     */
    public static void fillOval(int x, int y, int width, int height, Color color) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        DrawEllipse(centerX, centerY, width / 2.0f, height / 2.0f, color);
    }
    
    /**
     * Draw an ellipse outline (replaces Graphics2D.drawOval)
     */
    public static void drawOval(int x, int y, int width, int height, Color color) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        DrawEllipseLines(centerX, centerY, width / 2.0f, height / 2.0f, color);
    }
    
    /**
     * Fill a circle
     */
    public static void fillCircle(int centerX, int centerY, int radius, Color color) {
        DrawCircle(centerX, centerY, radius, color);
    }
    
    // ==========================================
    // LINE DRAWING
    // ==========================================
    
    /**
     * Draw a line (replaces Graphics2D.drawLine)
     */
    public static void drawLine(int x1, int y1, int x2, int y2, Color color) {
        DrawLine(x1, y1, x2, y2, color);
    }
    
    
    /**
     * Create a Vector2
     */
    public static Vector2 vec2(float x, float y) {
        return new Vector2().x(x).y(y);
    }
    
    // ==========================================
    // POLYGON DRAWING
    // ==========================================
    
    /**
     * Fill a polygon (replaces Graphics2D.fillPolygon)
     */
    public static void fillPolygon(int[] xPoints, int[] yPoints, int nPoints, Color color) {
        // Raylib draws triangles, so we triangulate the polygon
        // For simple convex polygons, use fan triangulation from first vertex
        if (nPoints < 3) return;
        
        for (int i = 1; i < nPoints - 1; i++) {
            DrawTriangle(
                vec2(xPoints[0], yPoints[0]),
                vec2(xPoints[i], yPoints[i]),
                vec2(xPoints[i + 1], yPoints[i + 1]),
                color
            );
        }
    }
    
    /**
     * Draw a polygon outline
     */
    public static void drawPolygon(int[] xPoints, int[] yPoints, int nPoints, Color color) {
        if (nPoints < 2) return;
        for (int i = 0; i < nPoints; i++) {
            int next = (i + 1) % nPoints;
            DrawLine(xPoints[i], yPoints[i], xPoints[next], yPoints[next], color);
        }
    }
    
    /**
     * Fill an arc (approximate with triangle fan)
     */
    public static void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle, Color color) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        float radiusX = width / 2.0f;
        float radiusY = height / 2.0f;
        int segments = 16;
        float step = (float) Math.toRadians(arcAngle) / segments;
        float start = (float) Math.toRadians(startAngle);
        
        for (int i = 0; i < segments; i++) {
            float a1 = start + step * i;
            float a2 = start + step * (i + 1);
            DrawTriangle(
                vec2(centerX, centerY),
                vec2(centerX + (float)Math.cos(a1) * radiusX, centerY - (float)Math.sin(a1) * radiusY),
                vec2(centerX + (float)Math.cos(a2) * radiusX, centerY - (float)Math.sin(a2) * radiusY),
                color
            );
        }
    }
    
    /**
     * Draw an arc outline
     */
    public static void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, Color color) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        float radiusX = width / 2.0f;
        float radiusY = height / 2.0f;
        int segments = 16;
        float step = (float) Math.toRadians(arcAngle) / segments;
        float start = (float) Math.toRadians(startAngle);
        
        for (int i = 0; i < segments; i++) {
            float a1 = start + step * i;
            float a2 = start + step * (i + 1);
            DrawLine(
                (int)(centerX + Math.cos(a1) * radiusX), (int)(centerY - Math.sin(a1) * radiusY),
                (int)(centerX + Math.cos(a2) * radiusX), (int)(centerY - Math.sin(a2) * radiusY),
                color
            );
        }
    }
    
    // ==========================================
    // TEXT DRAWING
    // ==========================================
    
    /**
     * Draw text (replaces Graphics2D.drawString)
     */
    public static void drawText(String text, int x, int y, int fontSize, Color color) {
        // Raylib's DrawText y is top of text, not baseline like Graphics2D
        DrawText(text, x, y, fontSize, color);
    }
    
    
    /**
     * Measure text width
     */
    public static int measureText(String text, int fontSize) {
        return MeasureText(text, fontSize);
    }
    
    
    // ==========================================
    // TRANSFORMS (screen shake, etc.)
    // ==========================================
    
    // Note: For screen shake, we'll offset all draw positions rather than 
    // using transforms, since Raylib doesn't have Graphics2D-style transforms
    
    private static int shakeOffsetX = 0;
    private static int shakeOffsetY = 0;
    
    public static void setShakeOffset(int x, int y) {
        shakeOffsetX = x;
        shakeOffsetY = y;
    }
    
    public static int getShakeOffsetX() {
        return shakeOffsetX;
    }
    
    public static int getShakeOffsetY() {
        return shakeOffsetY;
    }
    
    public static void resetShakeOffset() {
        shakeOffsetX = 0;
        shakeOffsetY = 0;
    }
    
    // ==========================================
    // COMMON COLORS (use Colors.* or color())
    // ==========================================
    
    public static final Color WHITE = Colors.WHITE;
    public static final Color BLACK = Colors.BLACK;
    public static final Color RED = Colors.RED;
    public static final Color GREEN = Colors.GREEN;
    public static final Color BLUE = Colors.BLUE;
    public static final Color YELLOW = Colors.YELLOW;
    public static final Color GRAY = Colors.GRAY;
    public static final Color DARKGRAY = Colors.DARKGRAY;
}
