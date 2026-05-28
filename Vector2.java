
import java.awt.Point;

/**
 * A utility class that represents a two-dimensional vector
 * value, with an x- and y-component. Vector2 objects are immutable,
 * meaning that a new object must be created to change the value
 * of the vector.
 * 
 * @author Evan Guo
 * @version 5/23/26
 */
public class Vector2
{
    private float x, y;

    /**
     * Creates a new Vector2 with the specified x- and y- components.
     * @param x The x-component
     * @param y The y-component
     */
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new Vector2 from the specified {@link Point}.
     * @param p The source point
     */
    public Vector2(Point p)
    {
        x = p.x;
        y = p.y;
    }

    /**
     * Creates a new Vector2 from the specified polar coordinates.
     * @param angle The angle in radians from the positive x-axis to the positive y-axis
     * @param length The length or magnitude of the vector
     * @return The Vector2 created from the given polar coordinates
     */
    public static Vector2 createPolar(float angle, float length)
    {
        return new Vector2((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }

    /**
     * A Vector2 object that has all of its components set to 0,
     * &lt;0, 0&gt;.
     */
    public static final Vector2 zero = new Vector2(0, 0);
    /**
     * The unit vector in the positive x-direction, &lt;1, 0&gt;.
     */
    public static final Vector2 unitX = new Vector2(1, 0);
    /**
     * The unit vector in the positive y-direction, &lt;0, 1&gt;.
     */
    public static final Vector2 unitY = new Vector2(0, 1);

    /**
     * Gets the x-component of the vector.
     * @return The x-component of the vector
     */
    public float x()
    {
        return x;
    }

    /**
     * Gets the y-component of the vector.
     * @return The y-component of the vector
     */
    public float y()
    {
        return y;
    }

    /**
     * Creates a new Vector2 object with the specified x-component
     * and the same y-component as the original vector. This is
     * useful for "setting" the x-component of the vector,
     * since Vector2 objects are immutable.
     * @param x The new x-component of the vector
     * @return The new vector with the specified x-component
     */
    public Vector2 withX(float x)
    {
        return new Vector2(x, this.y);
    }

    /**
     * Creates a new Vector2 object with the specified y-component
     * and the same x-component as the original vector. This is
     * useful for "setting" the y-component of the vector,
     * since Vector2 objects are immutable.
     * @param y The new y-component of the vector
     * @return The new vector with the specified y-component
     */
    public Vector2 withY(float y)
    {
        return new Vector2(this.x, y);
    }

    /**
     * Calculates the angle that the vector makes with the positive
     * x-axis, with a positive angle representing a rotation towards the positive
     * y-axis.
     * @return The angle to the positive x-axis in radians
     */
    public float angle()
    {
        return (float)Math.atan2(y, x);
    }

    /**
     * Calculates the square of the length of the vector, equal to
     * the dot product of the vector with itself. This method should
     * be used in favor of <code>length()</code> when making length
     * comparisons, due to its faster time complexity.
     * @return The square of the length of the vector
     */
    public float lengthSq()
    {
        return x * x + y * y;
    }

    /**
     * Calculates the length of the vector. <code>lengthSq()</code>
     * should be used instead of this method when making length comparisons due to
     * its faster time complexity.
     * @return The length of the vector
     */
    public float length()
    {
        return (float)Math.sqrt(lengthSq());
    }

    /**
     * Calculates the vector sum of this vector and the other specified vector.
     * @param other The vector to add to this vector
     * @return A new Vector2 object representing the sum of the vectors
     */
    public Vector2 add(Vector2 other)
    {
        return new Vector2(x + other.x, y + other.y);
    }

    /**
     * Calculates the vector difference of this vector and the other specified vector.
     * @param other The vector to subtract from this vector
     * @return A new Vector2 object representing the difference of the vectors
     */
    public Vector2 sub(Vector2 other)
    {
        return new Vector2(x - other.x, y - other.y);
    }

    /**
     * Calculates the result of multiplying this vector by a scalar.
     * @param scalar The scalar by which to multiply the vector
     * @return A new Vector2 object representing the result of the multiplication
     */
    public Vector2 mult(float scalar)
    {
        return new Vector2(scalar * x, scalar * y);
    }

    /**
     * Calculates the result of multiplying this vector by another vector,
     * component-wise. For the dot product of two vectors, see {@link Vector2#dot(Vector2)}
     * @param other The other vector by which to multiply this vector component-wise
     * @return A new Vector2 object representing the result of the multiplication
     */
    public Vector2 mult(Vector2 other)
    {
        return new Vector2(x * other.x, y * other.y);
    }

    /**
     * Calculates the result of dividing this vector by a scalar.
     * @param scalar The scalar by which to divide the vector
     * @return A new Vector2 object representing the result of the division
     */
    public Vector2 div(float scalar)
    {
        return new Vector2(x / scalar, y / scalar);
    }

    /**
     * Calculates the result of dividng this vector by another vector,
     * component-wise.
     * @param other The other vector by which to divide this vector component-wise
     * @return A new Vector2 object representing the result of the division
     */
    public Vector2 div(Vector2 other)
    {
        return new Vector2(x / other.x, y / other.y);
    }

    /**
     * Calculates the dot product of this vector and another specified vector.
     * @param other The other vector
     * @return The dot product of the two vectors
     */
    public float dot(Vector2 other)
    {
        return x * other.x + y * other.y;
    }

    /**
     * Calculates the linear interpolation (lerp) between this vector and another
     * specified vector; that is, the vector that lies a certain percentage <code>t</code>
     * on the line between the two vectors.
     * @param other The other vector
     * @param t The percentage of the way between the two vectors
     * @return The result of the interpolation
     */
    public Vector2 lerp(Vector2 other, float t)
    {
        return new Vector2((other.x - x) * t + x, (other.y - y) * t + y);
    }

    /**
     * Calculates the vector that has the same direction as this
     * vector with a length of 1. If this vector is equal to the
     * zero vector, this method will return the zero vector.
     * @return The normalized vector
     */
    public Vector2 normalize()
    {
        return equals(Vector2.zero) ? Vector2.zero : div(length());
    }

    /**
     * Converts this vector into a {@link Point} by casting
     * its <code>float</code> components into <code>int</code>s.
     * @return The resulting Point object
     */
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