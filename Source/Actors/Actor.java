
import java.awt.Graphics2D;

/**
 * An abstract class that represents an object that exists in the WorldScene.
 * Each frame, it gets update and render method calls from the WorldScene to which it belongs.
 * 
 * @author  Aarohi Shah, Evan Guo
 * @version 5/25/26
 */
public abstract class Actor
{
    private WorldScene world;

    /**
     * Gets the current WorldScene to which the actor belongs.
     * @return The current WorldScene, or null if the actor is not currently assigned to a world
     */
    public WorldScene getWorld()
    {
        return world;
    }

    /**
     * Sets the WorldScene of the actor. This method should only be used by WorldScene, but
     * can be overridden to listen for the actor being added/removed to a WorldScene. If
     * overridden, the method should still call super.setWorld() properly set the WorldScene.
     * @param world The new WorldScene, or null if the actor has been removed.
     */
    public void setWorld(WorldScene world)
    {
        this.world = world;
    }

    /**
     * Updates the state of the actor, which is done by the WorldScene on every physics tick.
     * This method should be overridden by the child class to define update functionality.
     */
    public void update() {}
    
    /**
     * Renders the actor to the given Graphics2D, which is done by the WorldScene on every frame.
     * This method should be overridden by the child class to render the specific actor.
     * <p>
     * Postcondition: the state of the actor should not be modified
     * @param g The Graphics2D object to which to render the actor
     */
    public void render(Graphics2D g) {}
    
    /**
     * Gets the z-index, or depth value, of the actor, which determines
     * whether it should drawn on top of or below other actors. Actors will be
     * drawn in order by increasing z-index, so actors with higher z-indices will
     * appear on top. This method should be overriden by child classes to return
     * the z-index that corresponds to the specific type of actor.
     * 
     * <p>
     * List of actors by their z-index:
     * <ul>
     *  <li>10: UniformEField
     *  <li>11: UniformBField
     *  <li>20: TotalEField
     *  <li>21: TotalBField
     *  <li>100: Switch
     *  <li>110: Wire
     *  <li>200: Goal
     *  <li>210: Wall
     *  <li>300: ChargeBag
     *  <li>310: Charge (fixed)
     *  <li>311: Charge (non-fixed)
     *  <li>400: Puck
     * </ul>
     * @return The z-index of the actor
     */
    public int getZIndex()
    {
        return 0;
    }
}