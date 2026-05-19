import java.awt.Graphics2D;


/**
 * An abstract class that represents an object that exists in the WorldScene.
 * Each frame, it updates its render and position.
 */
public abstract class Actor
{
    private WorldScene world;

    public void removeFromWorld()
    {
        world = null;
    }

    public void addToWorld(WorldScene world)
    {
        this.world = world;
    }

    public WorldScene getWorld()
    {
        return world;
    }

    public void update()
    {
        
    }
    
    public void render(Graphics2D g) {}
    
    public int getZIndex()
    {
        return 0;
    }
}