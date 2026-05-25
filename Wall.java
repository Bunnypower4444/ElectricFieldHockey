
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 
 */

public class Wall extends Actor
{
    public static final Color COLOR = new Color(87, 104, 255);

    private Rectangle bounds;

    public Wall(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(COLOR);
        DrawUtil.drawWorldRectangle(g, bounds);
    }

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