
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.PriorityQueue;

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
    private static final Color NOALPHA_SHADING_COLOR = DrawUtil.alphaCompositeOver(WorldScene.FIELD_COLOR, BG_COLOR);
    private static final Stroke NOALPHA_OUTLINE_STROKE = new BasicStroke(3 * Game.RELATIVE_SCALE);
    private static final Stroke NOALPHA_SHADING_STROKE = new BasicStroke(5 * Game.RELATIVE_SCALE);
    private static final float NOALPHA_SHADING_SPACING = 40 * Game.RELATIVE_SCALE;
    private static final Color VECTOR_COLOR = new Color(250, 143, 67);
    
    private static final float VECTOR_LENGTH = 40;
    private static final int VECTOR_WIDTH = (int)(5 * Game.RELATIVE_SCALE);
    private static final int VECTOR_SPACING = (int)(70 * Game.RELATIVE_SCALE);
    // The length of the vector such that it will take one second to animate
    private static final float VECTOR_LENGTH_ANIM_1S = 100000;

    /**
     * If set to true, the field arrows will not have the fading out and in effect.
     */
    public boolean useNoAlphaOptimization = true;

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

        displayVector = strength.normalize().mult(VECTOR_LENGTH);

        renderArrowsToTarget();
    }

    private void renderArrowsToTarget()
    {
        DrawUtil.clear(renderTarget);

        if (strength.equals(Vector2.zero))
            return;

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
        int width = screenBottomRight.x - screenTopLeft.x;
        int height = screenBottomRight.y - screenTopLeft.y;

        if (useNoAlphaOptimization)
        {
            g.setColor(NOALPHA_SHADING_COLOR);
            g.setStroke(NOALPHA_SHADING_STROKE);

            g.setClip(new Rectangle(screenTopLeft.x, screenTopLeft.y, width, height));

            int hash = (strength.hashCode() * 41) ^ (screenTopLeft.hashCode() * 43) ^ (screenBottomRight.hashCode() * 47);
            // randomly offset the shading based on the strength, size, and position
            float r = Math.abs((hash % 4444) / 4444f) * NOALPHA_SHADING_SPACING;

            for (; r < width; r += NOALPHA_SHADING_SPACING)
            {
                int intR = (int)(r + 0.5f);
                int x2 = r > height ? screenTopLeft.x + (intR - height) : screenTopLeft.x;
                int y2 = Math.min(screenTopLeft.y + intR, screenBottomRight.y);

                g.drawLine(screenTopLeft.x + intR, screenTopLeft.y, x2, y2);
            }
            for (; r < width + height; r += NOALPHA_SHADING_SPACING)
            {
                int intR = (int)(r + 0.5f);
                int x2 = r > height ? screenTopLeft.x + (intR - height) : screenTopLeft.x;
                int y2 = Math.min(screenTopLeft.y + intR, screenBottomRight.y);

                g.drawLine(screenBottomRight.x, screenTopLeft.y + intR - width, x2, y2);
            }

            g.setClip(null);

            // the outline is drawn later at a higher z-index
        }
        else
        {
            g.setColor(BG_COLOR);
            DrawUtil.fillWorldRectangle(g, bounds);
        }

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

    private void noAlphaRenderOutline(Graphics2D g)
    {
        g.setColor(VECTOR_COLOR);
        g.setStroke(NOALPHA_OUTLINE_STROKE);
        DrawUtil.drawWorldRectangle(g, bounds);
    }

    @Override
    public void collectRenderCalls(PriorityQueue<RenderCall> renderCalls)
    {
        if (useNoAlphaOptimization)
            renderCalls.add(new RenderCall(this::noAlphaRenderOutline, 25));
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
