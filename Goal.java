import java.awt.Rectangle;
/**
 * 
 */
public class Goal extends Actor
{
    private Rectangle bounds;
    private static final int WIDTH = 10;

    //Initializes the boundary for goal
    public Goal(Rectangle bounds)
    {
        this.bounds = bounds;
    }
    //Checks if the puck is inside the goal
    public boolean goalCollision(Puck puck)
    {
        int x = (int)bounds.getX();
        int y = (int)bounds.getY();
        int width = (int)bounds.getWidth() - WIDTH;
        int height = (int)bounds.getHeight() - 2*WIDTH;
        Rectangle goal = new Rectangle(x, y, width, height);

        return(puck.collisionBox().intersects(goal));
    }
    // Checks is the puck collides with the wall's of the goal
    public boolean wallCollision(Puck puck)
    {
        if(goalCollision(puck)){
            return(false);
        }
        Rectangle top = new Rectangle((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), 
                        WIDTH);
        Rectangle bottom = new Rectangle((int)bounds.getX(), (int)bounds.getY() + (int)bounds.getHeight()-WIDTH,
                        (int)bounds.getWidth(), WIDTH);
        Rectangle side = new Rectangle((int)bounds.getX()+(int)bounds.getWidth()-WIDTH,(int)bounds.getY(),
                            WIDTH, (int)bounds.getHeight());

        if(puck.collisionBox().intersects(top) || puck.collisionBox().intersects(bottom)
            || puck.collisionBox().intersects(side)){
                return(true);
        }
        return(false);

    } 
}
