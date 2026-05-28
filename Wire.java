
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * An actor representing an infinitely long current-carrying wire,
 * which produces a magnetic field with field lines
 * in concentric circles around the wire. The strength of the magnetic
 * field at a given point can be found using
 * {@link Calc#ampereCircuitalLaw(Vector2, Vector2, Vector2) Ampère's circuital law}.
 * Wires can also be turned off by a {@link Switch}.
 * 
 * @author Evan Guo
 * @version 5/25/27
 */
public class Wire extends Actor implements HasBField, RequireReset
{
    private static final float LINE_WIDTH = 5 * Game.RELATIVE_SCALE;
    private static final Color CURRENT_ON = new Color(214, 180, 11);
    private static final Color CURRENT_OFF = Color.DARK_GRAY;
    private static final float CURRENT_ANIM_1S = 25_000_000_000f;
    private static final float ARROW_SPACING = 200 * Game.RELATIVE_SCALE;

    private Vector2 current;
    private Vector2 position;
    private boolean enabled;

    private Point renderPoint1 = null, renderPoint2 = null;
    
    /**
     * Creates a new Wire with a specified current, passing through two
     * specified points. The conventional current will flow in the direction
     * from the <code>point1</code> to <code>point2</code>.
     * @param current The magnitude of the conventional current in the wire, in amperes
     * @param point1 The position of point that the wire passes through, in meters
     * @param point2 The position of a second point that the wire passes through, in meters
     */
    public Wire(float current, Vector2 point1, Vector2 point2)
    {
        this.current = point2.sub(point1).normalize().mult(current);
        this.position = point1;
        enabled = true;

        Vector2[] renderPoints = DrawUtil.getInfiniteLineEndpoints(position, this.current, WorldScene.WORLD_DIMENSIONS);
        if (renderPoints != null)
        {
            renderPoint1 = WorldScene.worldToScreenPoint(renderPoints[0]).toPoint();
            renderPoint2 = WorldScene.worldToScreenPoint(renderPoints[1]).toPoint();
        }
    }

    /**
     * Enables or disables a wire. A wire that is disabled will not produce
     * a magnetic field.
     * @param enabled true if the wire should be enabled; false if the wire should
     * be disabled
     */
    public void setEnabled(boolean enabled)
    {
        if (enabled != this.enabled)
            getWorld().bFieldUpdated = true;
        this.enabled = enabled;
    }

    @Override
    public void render(Graphics2D g)
    {
        if (renderPoint1 == null || renderPoint2 == null)
            return;

        g.setStroke(new BasicStroke(LINE_WIDTH));
        g.setColor(getColor());
        g.drawLine(renderPoint1.x, renderPoint1.y, renderPoint2.x, renderPoint2.y);

        if (enabled)
        {
            Vector2 direction = new Vector2(renderPoint2.x - renderPoint1.x, renderPoint2.y - renderPoint1.y).normalize();

            float animTime = animTime();
            float t = (getWorld().globalTimer() % animTime) / animTime;

            for (Vector2 point = new Vector2(renderPoint1).add(direction.mult(ARROW_SPACING * t));
                DrawUtil.pointOnScreen(point);
                point = point.add(direction.mult(ARROW_SPACING)))
            {
                DrawUtil.fillEqTriangle(g, point.toPoint(), current.angle(), (int)(LINE_WIDTH * 2));
            }
        }
    }

    private float animTime()
    {
        return CURRENT_ANIM_1S / current.length();
    }

    private Color getColor()
    {
        return (enabled && !current.equals(Vector2.zero))
            ? CURRENT_ON : CURRENT_OFF;
    }

    @Override
    public Vector3 getFieldAt(Vector2 position)
    {
        return enabled ? Calc.ampereCircuitalLaw(this.position, current, position) : Vector3.zero;
    }

    @Override
    public void reset()
    {
        if (!enabled)
            getWorld().bFieldUpdated = true;
        enabled = true;
    }

    @Override
    public int getZIndex()
    {
        return 110;
    }
}
