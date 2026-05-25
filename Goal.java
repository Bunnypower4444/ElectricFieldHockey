import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
/**
 *
 */
public class Goal extends Actor
{
    private Rectangle bounds;
    private static final int WIDTH = 10;
    private static final Color POST_COLOR = new Color(0, 140, 0);
    private static final Color ZONE_COLOR = new Color(0, 200, 0, 60);

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

    // Draws the win zone and the three goal posts (top, bottom, right), matching
    // the geometry used by goalCollision()/wallCollision(). The goal opens to the left.
    @Override
    public void render(Graphics2D g)
    {
        int x = (int)bounds.getX();
        int y = (int)bounds.getY();
        int w = (int)bounds.getWidth();
        int h = (int)bounds.getHeight();

        g.setColor(ZONE_COLOR);
        g.fillRect(x, y, w - WIDTH, h);

        g.setColor(POST_COLOR);
        g.fillRect(x, y, w, WIDTH);                     // top post
        g.fillRect(x, y + h - WIDTH, w, WIDTH);         // bottom post
        g.fillRect(x + w - WIDTH, y, WIDTH, h);         // right (back) post
    }

    @Override
    public int getZIndex()
    {
        return 200;
    }
}
