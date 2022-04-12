import static java.lang.Math.*;

public class Vector2D{
    double x, y;
        
    Vector2D(){
    
    }
    
    Vector2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    
    Vector2D(Vector2D v){
            this.x = v.x;
            this.y = v.y;
    }

    void add(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
    }
 
    void sub(Vector2D v) {
        x -= v.x;
        y -= v.y;
    }
 
    void div(double val) {
        x /= val;
        y /= val;
    }
 
    void mult(double val) {
        x *= val;
        y *= val;
    }
 
    double mag() {
        return sqrt(pow(x, 2) + pow(y, 2));
    }

    double dot(Vector2D v) {
        return x * v.x + y * v.y;
    }
 
    void normalize() {
        double mag = mag();
        if (mag != 0) {
            x /= mag;
            y /= mag;
        }
    }
 
    void limit(double lim) {
        double mag = mag();
        if (mag != 0 && mag > lim) {
            x *= lim / mag;
            y *= lim / mag;
        }
    }
 
    double heading() {
        return atan2(y, x);
    }
 
    static Vector2D sub(Vector2D v, Vector2D v2) {
        return new Vector2D(v.x - v2.x, v.y - v2.y);
    }
 
    static double dist(Vector2D v, Vector2D v2) {
        return sqrt(pow(v.x - v2.x, 2) + pow(v.y - v2.y, 2));
    }
 
    static double angleBetween(Vector2D v, Vector2D v2) {
        return acos(v.dot(v2) / (v.mag() * v2.mag()));
    }
}