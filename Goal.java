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
        goalBounds = calculateGoalBounds(bounds);
        wallBounds = calculateWallBounds(bounds);

        this.orientation = orientation;
    }

    //Initializes the boundary for goal
    public Goal(Rectangle bounds)
    {
        this(bounds, Orientation.Left);
    }

    private static Rectangle calculateGoalBounds(Rectangle bounds)
    {
        int x = (int)bounds.getX();
        int y = (int)bounds.getY() + WIDTH;
        int width = (int)bounds.getWidth() - WIDTH;
        int height = (int)bounds.getHeight() - 2*WIDTH;
        Rectangle goal = new Rectangle(x, y, width, height);
        
        return goal;
    }
    
    private static Rectangle[] calculateWallBounds(Rectangle bounds)
    {
        Rectangle top = new Rectangle((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), 
                        WIDTH);
        Rectangle bottom = new Rectangle((int)bounds.getX(), (int)bounds.getY() + (int)bounds.getHeight()-WIDTH,
                        (int)bounds.getWidth(), WIDTH);
        Rectangle side = new Rectangle((int)bounds.getX()+(int)bounds.getWidth()-WIDTH,(int)bounds.getY(),
                            WIDTH, (int)bounds.getHeight());
        
        return new Rectangle[] { top, side, bottom };
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
        DrawUtil.drawWorldRectangle(g, bounds);

        g.setColor(Wall.COLOR);
        
        for (Rectangle wall : wallBounds)
        {
            DrawUtil.drawWorldRectangle(g, wall);
        }
    }

    @Override
    public int getZIndex()
    {
        return 200;
    }
}
