
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * 
 */
public class Wire extends Actor implements HasBField, RequireReset
{
    private static final float LINE_WIDTH = 15 * Game.RELATIVE_SCALE;
    private static final Color CURRENT_ON = new Color(214, 180, 11);
    private static final Color CURRENT_OFF = Color.DARK_GRAY;

    private Vector2 current;
    private Vector2 position;
    private boolean enabled;

    private Point renderPoint1 = null, renderPoint2 = null;
    
    public Wire(float current, Vector2 point1, Vector2 point2)
    {
        this.current = point2.sub(point1).normalize().mult(current);
        this.position = point1;
        enabled = true;

        Vector2[] renderPoints = DrawUtil.getInfiniteLineEndpoints(position, this.current);
        if (renderPoints != null)
        {
            renderPoint1 = DrawUtil.processVector(renderPoints[0]).toPoint();
            renderPoint2 = DrawUtil.processVector(renderPoints[1]).toPoint();
        }
    }

    public void setEnabled(boolean enabled)
    {
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
    }

    public Color getColor()
    {
        return (enabled && !current.equals(Vector2.zero()))
            ? CURRENT_ON : CURRENT_OFF;
    }

    @Override
    public Vector3 getFieldAt(Vector2 position)
    {
        return Calc.ampereCircuitalLaw(this.position, current, position);
    }

    @Override
    public void reset()
    {
        enabled = true;
    }

    @Override
    public int getZIndex()
    {
        return 110;
    }
}
