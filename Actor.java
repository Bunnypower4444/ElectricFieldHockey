import java.awt.Graphics2D;


/**
 * An abstract class that represents an object that exists in the WorldScene.
 * Each frame, it updates its render and position.
 */
public abstract class Actor
{
    private WorldScene world;

    public WorldScene getWorld()
    {
        return world;
    }

    public void setWorld(WorldScene world)
    {
        this.world = world;
    }

    public void update() {}
    
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
     *  <li>310: Charge
     *  <li>400: Puck
     * </ul>
     * @return Z-index of the actor
     */
    public int getZIndex()
    {
        return 0;
    }
}