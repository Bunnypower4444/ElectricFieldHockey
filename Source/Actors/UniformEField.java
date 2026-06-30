
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * An actor representing a rectangular region in which a uniform
 * electric field is present.
 * 
 * @author Aarohi Shah, Evan Guo
 * @version 5/27/26
 */
public class UniformEField extends Actor implements HasEField
{
    private Rectangle bounds;
    private Vector2 strength;

    private static final Color BG_COLOR = new Color(250, 143, 67, 100);
    private static final Color VECTOR_COLOR = new Color(250, 143, 67);

    private static final float VECTOR_LENGTH = 40;
    private static final int VECTOR_WIDTH = (int)(5 * Game.RELATIVE_SCALE);
    private static final int VECTOR_SPACING = (int)(70 * Game.RELATIVE_SCALE);
    // The length of the vector such that it will take one second to animate
    private static final float VECTOR_LENGTH_ANIM_1S = 100000;

    /**
     * Creates a new UniformEField with the specified bounds and vector strength.
     * @param bounds The world-space bounds of the uniform field, in meters
     * @param strength The vector representing the strength of the field, in N/C 
     */
    public UniformEField(Rectangle bounds, Vector2 strength)
    {
        this.bounds = bounds;
        this.strength = strength;
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(BG_COLOR);
        DrawUtil.fillWorldRectangle(g, bounds);

        Point topLeft = WorldScene.worldToScreenPoint(new Vector2(bounds.x, bounds.y + bounds.height)).toPoint();
        Point bottomRight = WorldScene.worldToScreenPoint(new Vector2(bounds.x + bounds.width, bounds.y)).toPoint();
        int width = bottomRight.x - topLeft.x;
        int height = bottomRight.y - topLeft.y;

        g.setClip(new Rectangle(topLeft.x, topLeft.y, width, height));
        
        float animTime = animTime();
        float t = (getWorld().globalAnimTimer() % animTime) / animTime;

        Vector2 displayVector = strength.normalize().mult(VECTOR_LENGTH);
        Point offset = WorldScene.worldToScreenVector(displayVector).mult(t - 0.5f).toPoint();

        // the period of the sin(theta) should be 2 * animTime, so one oscillation of sin^2 happens
        // in 1 * animTime
        // (t already accounts for animTime)
        double theta = Math.PI * t;
        int alpha = (int)(255 * Math.sin(theta) * Math.sin(theta));
        // Color c = VECTOR_COLOR;
        Color c = new Color(VECTOR_COLOR.getRed(), VECTOR_COLOR.getGreen(), VECTOR_COLOR.getBlue(), alpha);

        int startX, startY;

        if (width < VECTOR_SPACING)
            startX = topLeft.x + width / 2;
        else
            startX = topLeft.x - VECTOR_SPACING / 2;

        if (height < VECTOR_SPACING)
            startY = topLeft.y + height / 2;
        else
            startY = topLeft.y - VECTOR_SPACING / 2;

        for (int x = startX; x < bottomRight.x + VECTOR_SPACING / 2; x += VECTOR_SPACING)
        for (int y = startY; y < bottomRight.y + VECTOR_SPACING / 2; y += VECTOR_SPACING)
        {
            DrawUtil.drawMagnitudeVector(g, new Point(x + offset.x, y + offset.y), displayVector, c, VECTOR_WIDTH, 0);
        }
        
        g.setClip(null);
    }

    private float animTime()
    {
        return VECTOR_LENGTH_ANIM_1S / strength.length();
    }

    @Override
    public Vector2 getFieldAt(Vector2 position){
        if (bounds.contains(position.toPoint()))
            return strength;
        else
            return Vector2.zero;
    }

    @Override
    public int getZIndex()
    {
        return 20;
    }
}
