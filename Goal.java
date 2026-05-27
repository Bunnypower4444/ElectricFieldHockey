import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
/**
 * 
 */
public class Goal extends Actor
{
    private Rectangle bounds;
    private Rectangle goalBounds;
    private Rectangle[] wallBounds;
    private static final int WIDTH = 10;

    public static enum Orientation { Left, Up, Right, Down }

    private Orientation orientation;

    public Goal(Rectangle bounds, Orientation orientation)
    {
        this.bounds = bounds;
        this.orientation = orientation;
        goalBounds = calculateGoalBounds();
        wallBounds = calculateWallBounds();
    }

    //Initializes the boundary for goal
    public Goal(Rectangle bounds)
    {
        this(bounds, Orientation.Left);
    }

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
    
    //Checks if the puck is inside the goal
    public boolean goalCollision(Puck puck)
    {
        return goalBounds.contains(puck.collisionBox());
    }

    // Checks is the puck collides with the wall's of the goal
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

    @Override
    public int getZIndex()
    {
        return 200;
    }
}
