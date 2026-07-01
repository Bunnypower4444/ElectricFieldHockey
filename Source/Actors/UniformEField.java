
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * An actor representing a rectangular region in which a uniform
 * electric field is present.
 * 
 * @author Aarohi Shah, Evan Guo
 * @version 5/27/26
 */
public class UniformEField extends Actor implements HasEField
{
    private static final Color BG_COLOR = new Color(250, 143, 67, 100);
    private static final Color VECTOR_COLOR = new Color(250, 143, 67);
    
    private static final float VECTOR_LENGTH = 40;
    private static final int VECTOR_WIDTH = (int)(5 * Game.RELATIVE_SCALE);
    private static final int VECTOR_SPACING = (int)(70 * Game.RELATIVE_SCALE);
    // The length of the vector such that it will take one second to animate
    private static final float VECTOR_LENGTH_ANIM_1S = 100000;

    private Rectangle bounds;
    private Vector2 strength;

    // if the strength of the field ever is able to be changed,
    // must add a targetDirty field (see classes for total field)
    private BufferedImage renderTarget;
    private Graphics2D targetGraphics;

    private Point screenTopLeft, screenBottomRight;
    private Vector2 displayVector;

    /**
     * Creates a new UniformEField with the specified bounds and vector strength.
     * @param bounds The world-space bounds of the uniform field, in meters
     * @param strength The vector representing the strength of the field, in N/C 
     */
    public UniformEField(Rectangle bounds, Vector2 strength)
    {
        this.bounds = bounds;
        this.strength = strength;

        screenTopLeft = WorldScene.worldToScreenPoint(new Vector2(bounds.x, bounds.y + bounds.height)).toPoint();
        screenBottomRight = WorldScene.worldToScreenPoint(new Vector2(bounds.x + bounds.width, bounds.y)).toPoint();
        int width = screenBottomRight.x - screenTopLeft.x;
        int height = screenBottomRight.y - screenTopLeft.y;

        int bufferWidth = width + VECTOR_SPACING;
        int bufferHeight = height + VECTOR_SPACING;

        renderTarget = DrawUtil.createScaledImageBuffer(bufferWidth, bufferHeight);
        targetGraphics = renderTarget.createGraphics();
        targetGraphics.setBackground(new Color(0, 0, 0, 0));
        DrawUtil.scaleGraphics(targetGraphics);
        targetGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        displayVector = strength.normalize().mult(VECTOR_LENGTH);

        renderArrowsToTarget();
    }

    private void renderArrowsToTarget()
    {
        DrawUtil.clear(renderTarget);

        int width = screenBottomRight.x - screenTopLeft.x;
        int height = screenBottomRight.y - screenTopLeft.y;

        int startX, startY;

        // the BufferedImage will always contain the VECTOR_SPACING margin
        // (VECTOR_SPACING, VECTOR_SPACING) is the actual top left point
        if (width < VECTOR_SPACING)
            startX = VECTOR_SPACING / 2 + width / 2;
        else
            startX = 0;

        if (height < VECTOR_SPACING)
            startY = VECTOR_SPACING / 2 + height / 2;
        else
            startY = 0;

        for (int x = startX; x < renderTarget.getWidth(); x += VECTOR_SPACING)
        for (int y = startY; y < renderTarget.getHeight(); y += VECTOR_SPACING)
        {
            DrawUtil.drawMagnitudeVector(targetGraphics, new Point(x, y), displayVector, VECTOR_COLOR, VECTOR_WIDTH, 0);
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(BG_COLOR);
        DrawUtil.fillWorldRectangle(g, bounds);

        int width = screenBottomRight.x - screenTopLeft.x;
        int height = screenBottomRight.y - screenTopLeft.y;

        g.setClip(new Rectangle(screenTopLeft.x, screenTopLeft.y, width, height));
        
        float animTime = animTime();
        float t = (getWorld().globalAnimTimer() % animTime) / animTime;

        Point offset = WorldScene.worldToScreenVector(displayVector).mult(t - 0.5f).toPoint();

        // the period of the sin(theta) should be 2 * animTime, so one oscillation of sin^2 happens
        // in 1 * animTime
        // (t already accounts for animTime)
        double theta = Math.PI * t;
        float alpha = (float)(Math.sin(theta) * Math.sin(theta));
        
        DrawUtil.drawImageAlpha(g, renderTarget,
            screenTopLeft.x + offset.x - VECTOR_SPACING / 2,
            screenTopLeft.y + offset.y - VECTOR_SPACING / 2,
            width + VECTOR_SPACING, height + VECTOR_SPACING,
            alpha, Game.instance());
        
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
