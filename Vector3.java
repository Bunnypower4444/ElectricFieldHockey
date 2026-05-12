
/**
 * 
 */
public class Vector3
{
    private float x, y, z;

    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector2 xy, float z)
    {
        this.x = xy.x();
        this.y = xy.y();
        this.z = z;
    }

    public Vector3(Vector2 xy)
    {
        this.x = xy.x();
        this.y = xy.y();
        this.z = 0;
    }

    public static Vector3 fromAngle(float theta, float phi, float magnitude)
    {
        return new Vector3(
            Vector2.fromAngle(theta, (float)(magnitude * Math.cos(phi))),
            (float)(magnitude * Math.sin(phi)));
    }

    public static Vector3 zero()
    {
        return new Vector3(0, 0, 0);
    }

    public float x()
    {
        return x;
    }

    public float y()
    {
        return y;
    }

    public float z()
    {
        return z;
    }

    public Vector3 withX(float x)
    {
        return new Vector3(x, this.y, this.z);
    }

    public Vector3 withY(float y)
    {
        return new Vector3(this.x, y, this.z);
    }

    public Vector3 withZ(float z)
    {
        return new Vector3(this.x, this.y, z);
    }

    public Vector2 xy()
    {
        return new Vector2(x, y);
    }

    public float angleTheta()
    {
        return (float)Math.atan2(y, x);
    }

    public float anglePhi()
    {
        return (float)Math.atan2(z, Math.sqrt(x * x + y * y));
    }

    public float lengthSq()
    {
        return x * x + y * y + z * z;
    }

    public float length()
    {
        return (float)Math.sqrt(lengthSq());
    }

    public Vector3 add(Vector3 other)
    {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 sub(Vector3 other)
    {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 mult(float scalar)
    {
        return new Vector3(scalar * x, scalar * y, scalar * z);
    }

    public Vector3 mult(Vector3 other)
    {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }

    public Vector3 div(float scalar)
    {
        return new Vector3(x / scalar, y / scalar, z / scalar);
    }

    public Vector3 div(Vector3 other)
    {
        return new Vector3(x / other.x, y / other.y, z / other.z);
    }

    public float dot(Vector3 other)
    {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3 cross(Vector3 other)
    {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public Vector3 lerp(Vector3 other, float t)
    {
        return new Vector3((other.x - x) * t + x, (other.y - y) * t + y, (other.z - z) * t + z);
    }

    public Vector3 normalize()
    {
        return div(length());
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Vector3))
            return false;

        Vector3 otherVec = (Vector3)other;

        return x == otherVec.x && y == otherVec.y && z == otherVec.z;
    } 

    @Override
    public String toString()
    {
        return "<" + x + ", " + y + ", " + z + ">";
    }
}