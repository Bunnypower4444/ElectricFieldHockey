
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
 * magnetic field is present.
 * 
 * @author Aarohi Shah, Evan Guo
 * @version 5/27/26
 */
public class UniformBField extends Actor implements HasBField
{
    private static final Color BG_COLOR = new Color(106, 28, 184, 100);
    private static final Color NOALPHA_SHADING_COLOR = DrawUtil.alphaCompositeOver(WorldScene.FIELD_COLOR, BG_COLOR);
    private static final Stroke NOALPHA_OUTLINE_STROKE = new BasicStroke(3 * Game.RELATIVE_SCALE);
    private static final Stroke NOALPHA_SHADING_STROKE = new BasicStroke(5 * Game.RELATIVE_SCALE);
    private static final float NOALPHA_SHADING_SPACING = 40 * Game.RELATIVE_SCALE;
    private static final Color VECTOR_COLOR = new Color(106, 28, 184);

    private static final float VECTOR_RADIUS = 8 * Game.RELATIVE_SCALE;
    private static final int VECTOR_SPACING = (int)(70 * Game.RELATIVE_SCALE);
    // The length of the vector such that it will take one second to animate
    private static final float VECTOR_LENGTH_ANIM_1S = 100;
    // Scales from (1 - ANIM_SCALE_CHANGE) to (1 + ANIM_SCALE_CHANGE)
    private static final float ANIM_SCALE_CHANGE = 0.50f;

    /**
     * If set to true, the total field will be assumed to be drawn on top of
     * a solid color background with nothing else underneath, and an optimization will be
     * used that precalculates the alpha calculation for all the arrows once,
     * interpolating between the vector color and the field color drawn on the background.
     */
    // public boolean usePlainBGOptimization = true;

    /**
     * If set to true, the field arrows will not have the fading out and in effect,
     * and the background will be replaced with diagonal striped shading with no
     * transparency.
     */
    public boolean disableAlpha = false;

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

    private boolean getNoAlphaEnabled()
    {
        return disableAlpha || Save.instance().lowDetailMode;
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

    private void renderArrowToTarget(float radius)
    {
        Point center = new Point(RENDER_TARGET_SIZE / 2, RENDER_TARGET_SIZE / 2);

        DrawUtil.clear(renderTarget);

        targetGraphics.setColor(VECTOR_COLOR);
        
        DrawUtil.drawDotCross(targetGraphics, center, radius);
    }

    /* // Pretends that the background is a solid color, the color of the uniform field
    // on the playing field.
    private void renderArrowToTargetBGOptimization(float radius, Color playingFieldBG, float alpha)
    {
        Point center = new Point(RENDER_TARGET_SIZE / 2, RENDER_TARGET_SIZE / 2);

        DrawUtil.clear(renderTarget);

        Color bg = DrawUtil.alphaCompositeOver(playingFieldBG, BG_COLOR);
        Color c = DrawUtil.alphaCompositeOver(bg, DrawUtil.colorWithAlpha(VECTOR_COLOR, alpha));
        targetGraphics.setColor(c);
        
        DrawUtil.drawDotCross(targetGraphics, center, radius);
    } */

    /**
     * When useNoAlphaOptimization is set to true:
     * The shading is drawn via a RenderCall at a Z-index
     * of 16 (behind all uniform field arrows)
     * The outline is drawn via a RenderCall at a Z-index
     * of 26 (above all uniform field arrows)
     * Only the arrows are drawn by render()
     */
    @Override
    public void render(Graphics2D g)
    {
        int width = screenBottomRight.x - screenTopLeft.x;
        int height = screenBottomRight.y - screenTopLeft.y;

        if (!getNoAlphaEnabled())
        {
            g.setColor(BG_COLOR);
            DrawUtil.fillWorldRectangle(g, bounds);
        }

        if (strength.z() == 0)
            return;

        g.setClip(new Rectangle(screenTopLeft.x, screenTopLeft.y, width, height));

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

        /* if (usePlainBGOptimization)
            renderArrowToTargetBGOptimization(Math.signum(strength.z()) * radius, g.getBackground(), alpha);
        else
            renderArrowToTarget(Math.signum(strength.z()) * radius); */
        
        if (ElectricFieldHockey.isWebVersion)
            renderArrowToTarget(Math.signum(strength.z()) * radius);

        int startX, startY;

        if (width < VECTOR_SPACING)
            startX = screenTopLeft.x + width / 2;
        else
            startX = screenTopLeft.x - VECTOR_SPACING / 2;

        if (height < VECTOR_SPACING)
            startY = screenTopLeft.y + height / 2;
        else
            startY = screenTopLeft.y - VECTOR_SPACING / 2;

        // The web version runs the non-LDM version faster with the buffered image,
        // but the desktop version does better with directly calling DrawUtil.drawDotCross()
        if (getNoAlphaEnabled() && ElectricFieldHockey.isWebVersion)
        {
            for (int x = startX; x < screenBottomRight.x + VECTOR_SPACING / 2; x += VECTOR_SPACING)
            for (int y = startY; y < screenBottomRight.y + VECTOR_SPACING / 2; y += VECTOR_SPACING)
            {
                g.drawImage(renderTarget,
                    x - RENDER_TARGET_SIZE / 2, y - RENDER_TARGET_SIZE / 2,
                    RENDER_TARGET_SIZE, RENDER_TARGET_SIZE, Game.instance());
            }
        }

        else if (getNoAlphaEnabled())
        {
            g.setColor(VECTOR_COLOR);

            for (int x = startX; x < screenBottomRight.x + VECTOR_SPACING / 2; x += VECTOR_SPACING)
            for (int y = startY; y < screenBottomRight.y + VECTOR_SPACING / 2; y += VECTOR_SPACING)
            {
                DrawUtil.drawDotCross(g, new Point(x, y), Math.signum(strength.z()) * radius);
            }
        }
        
        else if (ElectricFieldHockey.isWebVersion)
        {
            for (int x = startX; x < screenBottomRight.x + VECTOR_SPACING / 2; x += VECTOR_SPACING)
            for (int y = startY; y < screenBottomRight.y + VECTOR_SPACING / 2; y += VECTOR_SPACING)
            {
                DrawUtil.drawImageAlpha(g, renderTarget,
                    x - RENDER_TARGET_SIZE / 2, y - RENDER_TARGET_SIZE / 2,
                    RENDER_TARGET_SIZE, RENDER_TARGET_SIZE, alpha, Game.instance());
            }
        }

        else
        {
            g.setColor(DrawUtil.colorWithAlpha(VECTOR_COLOR, alpha));

            for (int x = startX; x < screenBottomRight.x + VECTOR_SPACING / 2; x += VECTOR_SPACING)
            for (int y = startY; y < screenBottomRight.y + VECTOR_SPACING / 2; y += VECTOR_SPACING)
            {
                DrawUtil.drawDotCross(g, new Point(x, y), Math.signum(strength.z()) * radius);
            }
        }
        
        g.setClip(null);
    }

    private void noAlphaRenderShading(Graphics2D g)
    {
        int width = screenBottomRight.x - screenTopLeft.x;
        int height = screenBottomRight.y - screenTopLeft.y;

        g.setColor(NOALPHA_SHADING_COLOR);
        g.setStroke(NOALPHA_SHADING_STROKE);

        g.setClip(new Rectangle(screenTopLeft.x, screenTopLeft.y, width, height));

        int hash = (strength.hashCode() * 41) ^ (screenTopLeft.hashCode() * 43) ^ (screenBottomRight.hashCode() * 47);
        // randomly offset the shading based on the strength, size, and position
        float r = Math.abs((hash % 4444) / 4444f) * NOALPHA_SHADING_SPACING;

        for (; r < width; r += NOALPHA_SHADING_SPACING)
        {
            int intR = (int)(r + 0.5f);
            int x2 = r > height ? screenBottomRight.x - (intR - height) : screenBottomRight.x;
            int y2 = Math.min(screenTopLeft.y + intR, screenBottomRight.y);

            g.drawLine(screenBottomRight.x - intR, screenTopLeft.y, x2, y2);
        }
        for (; r < width + height; r += NOALPHA_SHADING_SPACING)
        {
            int intR = (int)(r + 0.5f);
            int x2 = r > height ? screenBottomRight.x - (intR - height) : screenBottomRight.x;
            int y2 = Math.min(screenTopLeft.y + intR, screenBottomRight.y);

            g.drawLine(screenTopLeft.x, screenTopLeft.y + intR - width, x2, y2);
        }

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
        if (getNoAlphaEnabled())
        {
            // Shading should go behind all arrows
            renderCalls.add(new RenderCall(this::noAlphaRenderShading, 16));
            // Outlines should go in front of all arrows
            renderCalls.add(new RenderCall(this::noAlphaRenderOutline, 26));
        }
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
