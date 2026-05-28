
/**
 * An interface used by actors that need to have additional update functionality
 * that occurs after the actors are updated normally.
 * 
 * @author Evan Guo
 * @version 5/25/26
 */
public interface LateUpdate
{
    /**
     * Updates the state of the actor, which is done by the WorldScene on every physics tick.
     * This method is invoked after actors are updated normally through <code>update()</code>.
     */
    public void lateUpdate();
}
