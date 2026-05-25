
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * 
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

    public Switch(Wire wire, Vector2 position, float radius)
    {
        this.wire = wire;
        this.position = position;
        this.radius = radius;
    }

    public boolean collision(Puck puck)
    {
        float minDist = radius + Puck.RADIUS;
        return puck.getPosition().sub(this.position).lengthSq() <= minDist * minDist;
    }

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
