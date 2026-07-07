import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * An actor representing a rectangular region in which a uniform
 * magnetic field is present.
 * 
 * @author Aarohi Shah, Evan Guo
 * @version 5/27/26
 */
public class UniformBField extends Actor implements HasBField
{
    private static final Color BG_COLOR = new Color(106, 28, 184, 100);
    private static final Color VECTOR_COLOR = new Color(106, 28, 184);

    private static final float VECTOR_RADIUS = 8 * Game.RELATIVE_SCALE;
    private static final int VECTOR_SPACING = (int)(70 * Game.RELATIVE_SCALE);
    // The length of the vector such that it will take one second to animate
    private static final float VECTOR_LENGTH_ANIM_1S = 100;
    // Scales from (1 - ANIM_SCALE_CHANGE) to (1 + ANIM_SCALE_CHANGE)
    private static final float ANIM_SCALE_CHANGE = 0.50f;

    private Rectangle bounds;
    private Vector3 strength;

    private static final int RENDER_TARGET_SIZE = (int)(2 * (VECTOR_RADIUS * (1 + 1 * ANIM_SCALE_CHANGE) + DrawUtil.VECTOR_STROKE_WIDTH));
    // if the strength of the field ever is able to be changed,
    // must add a targetDirty field (see classes for total field)
    private BufferedImage renderTarget;
    private Graphics2D targetGraphics;

    private Point screenTopLeft, screenBottomRight;

    /* static
    {
        renderTargetOut = DrawUtil.createScaledImageBuffer(targetSize, targetSize);
        targetGraphicsOut = renderTargetOut.createGraphics();
        targetGraphicsOut.setBackground(new Color(0, 0, 0, 0));
        DrawUtil.scaleGraphics(targetGraphicsOut);

        renderTargetIn = DrawUtil.createScaledImageBuffer(targetSize, targetSize);
        targetGraphicsIn = renderTargetIn.createGraphics();
        targetGraphicsIn.setBackground(new Color(0, 0, 0, 0));
        DrawUtil.scaleGraphics(targetGraphicsIn);

        prerenderArrows();
    } */
    
    /**
     * Creates a new UniformBField with the specified bounds and vector strength.
     * @param bounds The world-space bounds of the uniform field, in meters
     * @param strength The 3-D vector representing the strength of the field, in teslas 
     */
    public UniformBField(Rectangle bounds, Vector3 strength)
    {
        this.bounds = bounds;
        this.strength = strength;

        screenTopLeft = WorldScene.worldToScreenPoint(new Vector2(bounds.x, bounds.y + bounds.height)).toPoint();
        screenBottomRight = WorldScene.worldToScreenPoint(new Vector2(bounds.x + bounds.width, bounds.y)).toPoint();

        renderTarget = DrawUtil.createScaledImageBuffer(RENDER_TARGET_SIZE, RENDER_TARGET_SIZE);
        targetGraphics = renderTarget.createGraphics();
        targetGraphics.setBackground(new Color(0, 0, 0, 0));
        DrawUtil.scaleGraphics(targetGraphics);
    }

    /* private static void prerenderArrows()
    {
        Point center = new Point(targetSize / 2, targetSize / 2);
        float radius = VECTOR_RADIUS * (1 + 0 * ANIM_SCALE_CHANGE);

        // outward arrow
        DrawUtil.clear(renderTargetOut);

        targetGraphicsOut.setColor(VECTOR_COLOR);
        DrawUtil.drawDotCross(targetGraphicsOut, center, +radius);

        // inward arrow
        DrawUtil.clear(renderTargetIn);

        targetGraphicsIn.setColor(VECTOR_COLOR);
        DrawUtil.drawDotCross(targetGraphicsIn, center, -radius);
    } */

    private void renderArrowToTarget(float radius, float alpha)
    {
        Point center = new Point(RENDER_TARGET_SIZE / 2, RENDER_TARGET_SIZE / 2);

        DrawUtil.clear(renderTarget);

        targetGraphics.setColor(new Color(VECTOR_COLOR.getRed(), VECTOR_COLOR.getGreen(), VECTOR_COLOR.getBlue(), (int)(alpha * 255 + 0.5f)));
        
        DrawUtil.drawDotCross(targetGraphics, center, radius);
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(BG_COLOR);
        DrawUtil.fillWorldRectangle(g, bounds);

        if (strength.z() == 0)
            return;

        int width = screenBottomRight.x - screenTopLeft.x;
        int height = screenBottomRight.y - screenTopLeft.y;

        g.setClip(new Rectangle(screenTopLeft.x, screenTopLeft.y, screenBottomRight.x - screenTopLeft.x, screenBottomRight.y - screenTopLeft.y));

        float animTime = animTime();
        float t = (getWorld().globalAnimTimer() % animTime) / animTime;
        // if the z-component is negative (cross), make it get smaller over the animation
        // to make the cross appear to go into the screen
        if (strength.z() < 0)
            t = 1 - t;

        float radius = VECTOR_RADIUS * (1 + (t - 0.5f) * 2 * ANIM_SCALE_CHANGE);

        // the period of the sin(theta) should be 2 * animTime, so one oscillation of sin^2 happens
        // in 1 * animTime
        // (t already accounts for animTime)
        double theta = Math.PI * t;
        float alpha = (float)(Math.sin(theta) * Math.sin(theta));

        renderArrowToTarget(Math.signum(strength.z()) * radius, alpha);

        int startX, startY;

        if (width < VECTOR_SPACING)
            startX = screenTopLeft.x + width / 2;
        else
            startX = screenTopLeft.x - VECTOR_SPACING / 2;

        if (height < VECTOR_SPACING)
            startY = screenTopLeft.y + height / 2;
        else
            startY = screenTopLeft.y - VECTOR_SPACING / 2;

        for (int x = startX; x < screenBottomRight.x + VECTOR_SPACING / 2; x += VECTOR_SPACING)
        for (int y = startY; y < screenBottomRight.y + VECTOR_SPACING / 2; y += VECTOR_SPACING)
        {
            g.drawImage(renderTarget,
                x - RENDER_TARGET_SIZE / 2, y - RENDER_TARGET_SIZE / 2,
                RENDER_TARGET_SIZE, RENDER_TARGET_SIZE, Game.instance());
        }
        
        g.setClip(null);
    }

    private float animTime()
    {
        return VECTOR_LENGTH_ANIM_1S / strength.length();
    }

    @Override
    public Vector3 getFieldAt(Vector2 position){
        if (bounds.contains(position.toPoint()))
            return strength;
        else
            return Vector3.zero;
    }

    @Override
    public int getZIndex()
    {
        return 21;
    }
}
