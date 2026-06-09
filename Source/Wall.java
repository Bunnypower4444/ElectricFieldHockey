
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * An actor that represents a stationary rectangular wall in the world,
 * which serves as the primary obstacle for the puck. The level
 * will instantly be failed if the puck collides with a wall.
 * 
 * @author Aarohi Shah, Evan Guo
 * @version 5/26/26
 */
public class Wall extends Actor
{
    /**
     * The color using which walls are drawn.
     */
    public static final Color COLOR = new Color(87, 104, 255);

    private Rectangle bounds;

    /**
     * Creates a new Wall from the specified bounds.
     * @param bounds The world-space bounds of the Wall, in meters
     */
    public Wall(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(COLOR);
        DrawUtil.fillWorldRectangle(g, bounds);
    }

    /**
     * Checks if the specified Puck is colliding with this Wall.
     * @param puck The Puck for which to check collision
     * @return true if the Puck is colliding with the Wall; false otherwise
     */
    public boolean collision(Puck puck)
    {
        return bounds.intersects(puck.collisionBox());
    }

    @Override
    public int getZIndex()
    {
        return 210;
    }
}