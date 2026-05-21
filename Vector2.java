
import java.awt.Point;

/**
 * 
 */
public class Vector2
{
    private float x, y;

    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2(Point p)
    {
        x = p.x;
        y = p.y;
    }

    public static Vector2 createPolar(float angle, float length)
    {
        return new Vector2((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }

    public static Vector2 zero()
    {
        return new Vector2(0, 0);
    }

    public float x()
    {
        return x;
    }

    public float y()
    {
        return y;
    }

    public Vector2 withX(float x)
    {
        return new Vector2(x, this.y);
    }

    public Vector2 withY(float y)
    {
        return new Vector2(this.x, y);
    }

    public float angle()
    {
        return (float)Math.atan2(y, x);
    }

    public float lengthSq()
    {
        return x * x + y * y;
    }

    public float length()
    {
        return (float)Math.sqrt(lengthSq());
    }

    public Vector2 add(Vector2 other)
    {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 sub(Vector2 other)
    {
        return new Vector2(x - other.x, y - other.y);
    }

    public Vector2 mult(float scalar)
    {
        return new Vector2(scalar * x, scalar * y);
    }

    public Vector2 mult(Vector2 other)
    {
        return new Vector2(x * other.x, y * other.y);
    }

    public Vector2 div(float scalar)
    {
        return new Vector2(x / scalar, y / scalar);
    }

    public Vector2 div(Vector2 other)
    {
        return new Vector2(x / other.x, y / other.y);
    }

    public float dot(Vector2 other)
    {
        return x * other.x + y * other.y;
    }

    public Vector2 lerp(Vector2 other, float t)
    {
        return new Vector2((other.x - x) * t + x, (other.y - y) * t + y);
    }

    public Vector2 normalize()
    {
        return equals(Vector2.zero()) ? Vector2.zero() : div(length());
    }

    public Point toPoint()
    {
        return new Point((int)x, (int)y);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Vector2))
            return false;

        Vector2 otherVec = (Vector2)other;

        return x == otherVec.x && y == otherVec.y;
    } 

    @Override
    public String toString()
    {
        return "<" + x + ", " + y + ">";
    }
}