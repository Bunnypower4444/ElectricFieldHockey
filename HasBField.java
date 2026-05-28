
/**
 * An interface implemented by Actors that provide a magnetic (B) field.
 * 
 * @author Aarohi Shah
 * @version 5/12/26
 */
public interface HasBField
{
    /**
     * Gets the 3D vector representing the magnetic field strength due to the
     * object at a given position.
     * @param position The world-space position for which to get the magnetic
     * field strength, in meters
     * @return The magnetic field strength, in teslas
     */
    public Vector3 getFieldAt(Vector2 position);
}