
/**
 * A utility class that represents a three-dimensional vector
 * value, with an x-, y-, and z-component. Vector3 objects are immutable,
 * meaning that a new object must be created to change the value
 * of the vector.
 * 
 * @author Evan Guo
 * @version 5/27/26
 */
public class Vector3
{
    private float x, y, z;

    /**
     * Creates a new Vector2 with the specified x-, y-, and z- components.
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     */
    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a new Vector3 by combining a Vector2 with a
     * z-component
     * @param xy The Vector2 containing the x- and y- components of the
     * resulting Vector3
     * @param z The z-component of the vector
     */
    public Vector3(Vector2 xy, float z)
    {
        this.x = xy.x();
        this.y = xy.y();
        this.z = z;
    }

    /**
     * Creates a new Vector3 from a Vector2, with a z-component of
     * to zero.
     * @param xy The Vector2 containing the x- and y- components of the
     * resulting Vector3
     */
    public Vector3(Vector2 xy)
    {
        this.x = xy.x();
        this.y = xy.y();
        this.z = 0;
    }

    /**
     * Creates a new Vector3 from the specified spherical polar coordinates.
     * @param theta The azimuthal (xy plane) angle in radians from the
     * positive x-axis to the positive y-axis
     * @param phi The polar angle in radians from the positive z-axis to the xy-plane
     * @param length The length or magnitude of the vector
     * @return The Vector3 created from the given spherical polar coordinates
     */
    public static Vector3 createPolar(float theta, float phi, float length)
    {
        return new Vector3(
            Vector2.createPolar(theta, (float)(length * Math.sin(phi))),
            (float)(length * Math.cos(phi)));
    }

    /**
     * A Vector3 object that has all of its components set to 0,
     * <0, 0, 0>.
     */
    public static final Vector3 zero = new Vector3(0, 0, 0);
    /**
     * The unit vector in the positive x-direction, <1, 0, 0>.
     */
    public static final Vector3 unitX = new Vector3(1, 0, 0);
    /**
     * The unit vector in the positive y-direction, <0, 1, 0>.
     */
    public static final Vector3 unitY = new Vector3(0, 1, 0);
    /**
     * The unit vector in the positive z-direction, <0, 0, 1>.
     */
    public static final Vector3 unitZ = new Vector3(0, 0, 1);

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
     * Gets the z-component of the vector.
     * @return The z-component of the vector
     */
    public float z()
    {
        return z;
    }

    /**
     * Gets the x- and y- components of the vector,
     * represented in a Vector2 object.
     * @return The Vector2 with this vector's x- and y- components
     */
    public Vector2 xy()
    {
        return new Vector2(x, y);
    }

    /**
     * Creates a new Vector3 object with the specified x-component
     * and the same y- and z-components as the original vector. This is
     * useful for "setting" the x-component of the vector,
     * since Vector3 objects are immutable.
     * @param x The new x-component of the vector
     * @return The new vector with the specified x-component
     */
    public Vector3 withX(float x)
    {
        return new Vector3(x, this.y, this.z);
    }

    /**
     * Creates a new Vector3 object with the specified y-component
     * and the same x- and z-components as the original vector. This is
     * useful for "setting" the y-component of the vector,
     * since Vector3 objects are immutable.
     * @param y The new y-component of the vector
     * @return The new vector with the specified y-component
     */
    public Vector3 withY(float y)
    {
        return new Vector3(this.x, y, this.z);
    }

    /**
     * Creates a new Vector3 object with the specified z-component
     * and the same x- and y-components as the original vector. This is
     * useful for "setting" the z-component of the vector,
     * since Vector3 objects are immutable.
     * @param z The new z-component of the vector
     * @return The new vector with the specified z-component
     */
    public Vector3 withZ(float z)
    {
        return new Vector3(this.x, this.y, z);
    }

    /**
     * Calculates the azimuthal (xy-plane) angle that the vector makes with the positive
     * x-axis, with a positive angle representing a rotation towards the positive
     * y-axis.
     * @return The azimuthal angle in radians
     */
    public float angleTheta()
    {
        return (float)Math.atan2(y, x);
    }

    /**
     * Calculates the polar angle that the vector makes with the positive
     * z-axis, with a positive angle representing a rotation towards the positive
     * xy-plane.
     * @return The polar angle in radians
     */
    public float anglePhi()
    {
        return (float)Math.atan2(z, Math.sqrt(x * x + y * y));
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
        return x * x + y * y + z * z;
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
     * @return A new Vector3 object representing the sum of the vectors
     */
    public Vector3 add(Vector3 other)
    {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Calculates the vector difference of this vector and the other specified vector.
     * @param other The vector to subtract from this vector
     * @return A new Vector3 object representing the difference of the vectors
     */
    public Vector3 sub(Vector3 other)
    {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Calculates the result of multiplying this vector by a scalar.
     * @param scalar The scalar by which to multiply the vector
     * @return A new Vector3 object representing the result of the multiplication
     */
    public Vector3 mult(float scalar)
    {
        return new Vector3(scalar * x, scalar * y, scalar * z);
    }

    /**
     * Calculates the result of multiplying this vector by another vector,
     * component-wise. For the dot product of two vectors, see {@link Vector3#dot(Vector3)}
     * @param other The other vector by which to multiply this vector component-wise
     * @return A new Vector3 object representing the result of the multiplication
     */
    public Vector3 mult(Vector3 other)
    {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }

    /**
     * Calculates the result of dividing this vector by a scalar.
     * @param scalar The scalar by which to divide the vector
     * @return A new Vector3 object representing the result of the division
     */
    public Vector3 div(float scalar)
    {
        return new Vector3(x / scalar, y / scalar, z / scalar);
    }

    /**
     * Calculates the result of dividng this vector by another vector,
     * component-wise.
     * @param other The other vector by which to divide this vector component-wise
     * @return A new Vector3 object representing the result of the division
     */
    public Vector3 div(Vector3 other)
    {
        return new Vector3(x / other.x, y / other.y, z / other.z);
    }

    /**
     * Calculates the dot product of this vector and another specified vector.
     * @param other The other vector
     * @return The dot product of the two vectors
     */
    public float dot(Vector3 other)
    {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Calculates the cross product of this vector and another specified vector.
     * @param other The other vector
     * @return The cross product of the two vectors
     */
    public Vector3 cross(Vector3 other)
    {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    /**
     * Calculates the linear interpolation (lerp) between this vector and another
     * specified vector; that is, the vector that lies a certain percentage <code>t</code>
     * on the line between the two vectors.
     * @param other The other vector
     * @param t The percentage of the way between the two vectors
     * @return The result of the interpolation
     */
    public Vector3 lerp(Vector3 other, float t)
    {
        return new Vector3((other.x - x) * t + x, (other.y - y) * t + y, (other.z - z) * t + z);
    }

    /**
     * Calculates the vector that has the same direction as this
     * vector with a length of 1. If this vector is equal to the
     * zero vector, this method will return the zero vector.
     * @return The normalized vector
     */
    public Vector3 normalize()
    {
        return equals(Vector3.zero) ? Vector3.zero : div(length());
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