
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * An actor representing a circular button that when pressed by a Puck,
 * can turn off a Wire.
 * 
 * @author Aarohi Shah, Evan Guo
 * @version 5/25/26
 */
public class Switch extends Actor implements RequireReset
{
    private Vector2 position;
    private float radius;
    private Wire wire;
    private boolean pressed = false;

    private static final Color COLOR = new Color(255, 0, 0, 128);
    private static final float STROKE_WIDTH = 5 * Game.RELATIVE_SCALE;
    private static final float[] STROKE_DASH_PATTERN = new float[]{ 20 * Game.RELATIVE_SCALE, 30 * Game.RELATIVE_SCALE };

    /**
     * Creates a new Switch, linked to a specified Wire, at the specified position with a
     * specified radius.
     * @param wire The Wire that this Switch turns off
     * @param position The world-space position of the center of the Switch, in meters
     * @param radius The world-space radius of the switch, in meters
     */
    public Switch(Wire wire, Vector2 position, float radius)
    {
        this.wire = wire;
        this.position = position;
        this.radius = radius;
    }

    /**
     * Checks if a given puck is colliding with this Switch. Note that as long
     * as the puck is slightly overlapping with the Switch, the collision will be
     * counted.
     * @param puck The puck for which to check for collision
     * @return true if the puck is overlapping with the Switch; false otherwise
     */
    public boolean collision(Puck puck)
    {
        float minDist = radius + Puck.RADIUS;
        return puck.getPosition().sub(this.position).lengthSq() <= minDist * minDist;
    }

    /**
     * Activates the Switch, making it appear pressed (filled in rather than just an outline),
     * and turning off the associated Wire.
     */
    public void activate()
    {
        if (!pressed)
        {
            pressed = true;
            wire.setEnabled(false);
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(COLOR);

        int drawRadius = (int)(radius * WorldScene.FIELD_WIDTH / WorldScene.WORLD_DIMENSIONS.x());

        if (pressed)
        {
            DrawUtil.fillCircle(g, WorldScene.worldToScreenPoint(position).toPoint(), drawRadius);
        }
        else
        {
            g.setStroke(new BasicStroke(
                STROKE_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f, STROKE_DASH_PATTERN, 0f
            ));

            DrawUtil.drawCircle(g, WorldScene.worldToScreenPoint(position).toPoint(), drawRadius);
        }
    }

    @Override
    public void reset()
    {
        pressed = false;
    }

    @Override
    public int getZIndex()
    {
        return 100;
    }
}
