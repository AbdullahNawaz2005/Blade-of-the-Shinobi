package utils;




public class Vector2D {
    public double x;
    public double y;
    
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }
    
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
    }
    
    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }
    
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }
    
    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }
    
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    public Vector2D normalize() {
        double mag = magnitude();
        if (mag == 0) return new Vector2D(0, 0);
        return new Vector2D(x / mag, y / mag);
    }
    
    public double distance(Vector2D other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
    
    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }
    
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void addTo(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
    }
    
    public void multiplyBy(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }
    
    @Override
    public String toString() {
        return String.format("Vector2D(%.2f, %.2f)", x, y);
    }
}
