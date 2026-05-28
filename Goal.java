import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
/**
 * The target the player guides the puck into. Has an open mouth (the scoring
 * area) on one side and solid walls on the others.
 *
 * @author  Adeline Krishna
 * @version 5/25/26
 */
public class Goal extends Actor
{
    private Rectangle bounds;
    private Rectangle goalBounds;
    private Rectangle[] wallBounds;
    private static final int WIDTH = 10;

    /**
     * Enumeration of the four possible directions of the goal, with each
     * direction corresponding to the side that the goal's open mouth faces
     */
    public static enum Orientation
    {
        /**
         * A goal whose open mouth is on the left side
         */
        Left,
        /**
         * A goal whose open mouth is on the top side
         */
        Up,
        /**
         * A goal whose open mouth is on the right side
         */
        Right,
        /**
         * A goal whose open mouth is on the bottom side
         */
        Down
    }

    private Orientation orientation;

    /**
     * Creates a new Goal with the specified bounds and orientation.
     * @param bounds      the overall bounds of the goal, in world space
     * @param orientation the side the goal's open mouth faces
     */
    public Goal(Rectangle bounds, Orientation orientation)
    {
        this.bounds = bounds;
        this.orientation = orientation;
        goalBounds = calculateGoalBounds();
        wallBounds = calculateWallBounds();
    }

    /**
     * Initializes the boundary for a goal facing left.
     *
     * @param bounds the overall bounds of the goal, in world space
     */
    public Goal(Rectangle bounds)
    {
        this(bounds, Orientation.Left);
    }

    /**
     * @return the scoring area bounds for the current orientation
     */
    private Rectangle calculateGoalBounds()
    {
        int x, y, width, height;

        switch (orientation)
        {
            case Up:
                x = bounds.x + WIDTH;
                y = bounds.y + WIDTH;
                width = bounds.width - 2 * WIDTH;
                height = bounds.height - WIDTH;
                break;
                
            case Right:
                x = bounds.x + WIDTH;
                y = bounds.y + WIDTH;
                width = bounds.width - WIDTH;
                height = bounds.height - 2 * WIDTH;
                break;
                
            case Down:
                x = bounds.x + WIDTH;
                y = bounds.y;
                width = bounds.width - 2 * WIDTH;
                height = bounds.height - WIDTH;
                break;

            default:
                x = bounds.x;
                y = bounds.y + WIDTH;
                width = bounds.width - WIDTH;
                height = bounds.height - 2 * WIDTH;
        }

        Rectangle goal = new Rectangle(x, y, width, height);
        
        return goal;
    }
    
    /**
     * @return the three wall bounds for the current orientation (all sides except the open mouth)
     */
    private Rectangle[] calculateWallBounds()
    {
        Rectangle bottom = new Rectangle(bounds.x, bounds.y, bounds.width, WIDTH);
        Rectangle top = new Rectangle(bounds.x, bounds.y + bounds.height - WIDTH, bounds.width, WIDTH);
        Rectangle right = new Rectangle(bounds.x + bounds.width - WIDTH, bounds.y, WIDTH, bounds.height);
        Rectangle left = new Rectangle(bounds.x, bounds.y, WIDTH, bounds.height);
        
        switch (orientation)
        {
            case Up:
                return new Rectangle[] { left, right, bottom };
                
            case Right:
                return new Rectangle[] { left, top, bottom };
                
            case Down:
                return new Rectangle[] { left, top , right };

            default:
                return new Rectangle[] { top, right, bottom };
        }
    }
    
    /**
     * Checks if the puck is inside the goal.
     *
     * @param puck the puck to test
     * @return true if the puck is fully inside the scoring area
     */
    public boolean goalCollision(Puck puck)
    {
        return goalBounds.contains(puck.collisionBox());
    }

    /**
     * Checks if the puck collides with the walls of the goal.
     *
     * @param puck the puck to test
     * @return true if the puck hits a wall without scoring
     */
    public boolean wallCollision(Puck puck)
    {
        if(goalCollision(puck)){
            return(false);
        }

        for (Rectangle wall : wallBounds) {
            if (puck.collisionBox().intersects(wall))
                return(true);
        }

        return(false);
    } 

    /**
     * Draws the goal area and its walls.
     *
     * @param g the graphics context to draw with
     */
    @Override
    public void render(Graphics2D g)
    {
        g.setColor(Color.GREEN);
        DrawUtil.fillWorldRectangle(g, bounds);

        g.setColor(Wall.COLOR);
        
        for (Rectangle wall : wallBounds)
        {
            DrawUtil.fillWorldRectangle(g, wall);
        }
    }

    /**
     * @return the draw order index
     */
    @Override
    public int getZIndex()
    {
        return 200;
    }
}
