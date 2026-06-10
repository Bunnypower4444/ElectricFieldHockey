
/**
 * A static utility class that contains methods for implementing physics and math.
 * 
 * @author  Evan Guo
 * @version 5/25/26
 */
public class Calc
{
    /**
     * Coulomb's constant, k, in N·m²/C². Used for calculating the electric field due to a point charge.
     */
    public static final double COULOMB_CONSTANT = 8.987_551_785_972E9;
    /**
     * The permeability of free space (vacuum permeability), μ₀, in T·m/A. Used for calculating
     * the magnetic field due to a current-carrying wire.
     */
    public static final double VACUUM_PERMEABILITY = 1.256_637_061_27E-6;

    private static final double CHARGE_FIELD_MIN_DIST = 25;
    private static final double WIRE_FIELD_MIN_DIST = 1;

    /**
     * Calculates the vector representing the electric field strength at a given test point due to
     * a given point charge, using Coulomb's law. If the test point and the point charge
     * are within a certain minimum distance, the test point will be treated as being
     * the miminum distance away from the point charge, to prevent the electric field strength
     * from blowing up to infinity.
     * @param sourcePos A vector representing the position of the point charge, in meters
     * @param charge The charge of the point charge, in coulombs
     * @param testPoint A vector representing the position of the test point, in meters
     * @return A vector representing the electric field strength at the given test point, in N/C
     */
    public static Vector2 coulombLawField(Vector2 sourcePos, float charge, Vector2 testPoint)
    {
        Vector2 r = testPoint.sub(sourcePos);
        double rSq = r.lengthSq();
        rSq = Math.max(rSq, CHARGE_FIELD_MIN_DIST * CHARGE_FIELD_MIN_DIST);
        double magnitude = COULOMB_CONSTANT * charge / rSq;
        return r.normalize().mult((float)magnitude);
    }

    /**
     * Calculates the vector representing the magnetic field strength at a given test point due to
     * an infinitely long current-carrying wire, using Ampère's circuital law. If the test point and
     * the wire are within a certain minimum distance, the test point will be treated as being
     * the miminum distance away from the wire, to prevent the electric field strength
     * from blowing up to infinity.
     * 
     * The line in space representing the wire is given by a point and
     * a direction vector.
     * @param wirePoint A vector representing a point that the wire passes through, in meters
     * @param current A vector representing the direction and magnitude of the current, in amperes
     * @param testPoint A vector representing the position of the test point, in meters
     * @return A three-dimensional vector representing the electric field strength at the given test point,
     * in teslas. Note that since the wire and test point are confined to be in 2D space, the magnetic field
     * will only have a component in the z-direction.
     */
    public static Vector3 ampereCircuitalLaw(Vector2 wirePoint, Vector2 current, Vector2 testPoint)
    {
        Vector2 r = testPoint.sub(closestPointOnLine(wirePoint, current, testPoint));
        double rMag = r.length();
        rMag = Math.max(rMag, WIRE_FIELD_MIN_DIST);
        Vector3 dir = new Vector3(current).cross(new Vector3(r)).normalize();
        double magnitude = VACUUM_PERMEABILITY * current.length() / (2 * Math.PI * rMag);

        return dir.mult((float)magnitude);
    }

    /**
     * Calculates the point on a line given by a point and a direction vector that is closest to a test point,
     * using vector projection.
     * @param linePoint A vector representing a point that the line passes through
     * @param lineDirection The direction vector of the line
     * @param testPoint A vector representing the position of test point
     * @return A vector representing the position of the point on the line closest to the test point
     * @throws IllegalArgumentException if <code>lineDirection</code> is equal to the zero vector
     */
    public static Vector2 closestPointOnLine(Vector2 linePoint, Vector2 lineDirection, Vector2 testPoint)
    {
        // ORIGINAL IMPLEMENTATION
        // find intersection point of the line and a line perpendicular to it that passes through testPoint
        // direction of that line will be Vector2(-lineDirection.y, lineDirection.x) (rotated 90 degrees)

        // Line: y = line.y + (dir.y / dir.x) * (x - line.x)
        // Perpendicular: y = point.y + (- dir.x / dir.y) * (x - point.x)

        // line.y + (dir.y / dir.x) * (x - line.x) = point.y + (- dir.x / dir.y) * (x - point.x)
        // (dir.y / dir.x) * (x - line.x) - (- dir.x / dir.y) * (x - point.x) = point.y - line.y
        // (dir.y / dir.x + dir.x / dir.y) * x = point.y - line.y - ((dir.y / dir.x) * -line.x) + ((-dir.x / dir.y) * -point.x)
        // (dir.y / dir.x + dir.x / dir.y) * x = point.y - line.y + (dir.y / dir.x) * line.x + (dir.x / dir.y) * point.x
        // x = (point.y - line.y + (dir.y / dir.x) * line.x + (dir.x / dir.y) * point.x) / (dir.y / dir.x + dir.x / dir.y)

        // edge cases where line is horizontal, vertical, or if direction vector is 0
        if (lineDirection.equals(Vector2.zero))
            throw new IllegalArgumentException("Line direction cannot be the zero vector");

        // new implementation using vector projection
        Vector2 u = testPoint.sub(linePoint);
        float t = u.dot(lineDirection) / lineDirection.lengthSq();
        return linePoint.add(lineDirection.mult(t));

        // ORIGINAL IMPLEMENTATION
        /* if (lineDirection.x() == 0)
            return new Vector2(linePoint.x(), testPoint.y());

        if (lineDirection.y() == 0)
            return new Vector2(testPoint.x(), linePoint.y());
        
        double lineSlope = lineDirection.y() / lineDirection.x();
        double invSlope = 1 / lineSlope;

        double x = (testPoint.y() - linePoint.y() + lineSlope * linePoint.x() + invSlope * testPoint.x()) / (lineSlope + invSlope);
        double y = linePoint.y() + lineSlope * (x - linePoint.x());

        return new Vector2((float)x, (float)y); */
    }

    /**
     * Calculates the vector representing electric force on a charged object due to an electric field.
     * The resulting electric force is eequal to the charge of the object multiplied by the electric field strength.
     * @param charge The charge of the object, in coulombs
     * @param electricField A vector representing the electric field experienced by the object, in N/C
     * @return A vector representing the electric force on the object, in newtons
     */
    public static Vector2 electricForce(float charge, Vector2 electricField)
    {
        return electricField.mult(charge);
    }

    /**
     * Calculates the vector representing the magnetic force on a moving, charged object due to a magnetic field.
     * The resulting magnetic force is equal to the charge of the object multiplied by the cross product of its
     * velocity and the magnetic field strength.
     * @param charge The charge of the object, in coulombs
     * @param velocity A vector representing the velocity of the object, in m/s
     * @param magneticField A vector representing the magnetic field experienced by the object, in teslas
     * @return A vector representing the magnetic force on the object, in newtons
     */
    public static Vector3 magneticForce(float charge, Vector2 velocity, Vector3 magneticField)
    {
        return new Vector3(velocity).cross(magneticField).mult(charge);
    }

    /**
     * Calculates the vector representing the Lorentz force on a moving, charged object due to an electric
     * and magnetic field. The Lorentz force is the combined total of the electric and magnetic force on an object.
     * @param charge The charge of the object, in coulombs
     * @param electricField A vector representing the electric field experienced by the object, in N/C
     * @param velocity A vector representing the velocity of the object, in m/s
     * @param magneticField A vector representing the magnetic field experienced by the object, in teslas
     * @return A vector representing the Lorentz force on the object, in newtons
     */
    public static Vector3 lorentzForce(float charge, Vector2 electricField, Vector2 velocity, Vector3 magneticField)
    {
        return new Vector3(electricField).add(new Vector3(velocity).cross(magneticField)).mult(charge);
    }
}