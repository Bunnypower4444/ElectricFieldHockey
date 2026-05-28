
/**
 * An interface implemented by Actors that provide an electric (E) field.
 * 
 * @author Aarohi Shah
 * @version 5/12/26
 */
public interface HasEField
{
    /**
     * Gets the vector representing the electric field strength due to the
     * object at a given position.
     * @param position The world-space position for which to get the electric
     * field strength, in meters
     * @return The electric field strength, in N/C
     */
    public Vector2 getFieldAt(Vector2 position);
}