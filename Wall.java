import java.awt.Rectangle;


/**
 * 
 */

public class Wall extends Actor
{
    private Rectangle bounds;

    public Wall(Rectangle bounds)
    {

    }

    public boolean collision(Puck puck)
    {
        boolean bool = false;
        return bool; /*FIX THIS */
    }

    @Override
    public int getZIndex()
    {
        return 210;
    }
}