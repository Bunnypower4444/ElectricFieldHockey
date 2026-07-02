import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * An actor that calculates and visualizes the net magnetic field in the world
 * due to Wires.
 */
public class TotalBField extends Actor implements LateUpdate
{
    private static final float FULLY_OPAQUE_LENGTH = 150;
    private static final int VECTOR_RADIUS = (int)(6 * Game.RELATIVE_SCALE);
    private static final int GRID_SPACING = (int)(50 * Game.RELATIVE_SCALE);
    private static final Color COLOR = new Color(61, 7, 115);

    /**
     * If set to true, the total field will be assumed to be drawn on top of
     * a solid color background with nothing else underneath, and an optimization will be
     * used that precalculates the alpha calculation for an entire arrow once.
     */
    public boolean usePlainBGOptimization = true;
    
    private Vector3[][] fieldDisplayPoints;

    private BufferedImage renderTarget;
    private Graphics2D targetGraphics;
    private boolean targetDirty = false;

    @Override
    public void setWorld(WorldScene world)
    {
        WorldScene pWorld = getWorld();
        super.setWorld(world);
        
        if (pWorld != world && world != null)
        {
            fieldDisplayPoints = new Vector3[WorldScene.FIELD_WIDTH / GRID_SPACING][WorldScene.FIELD_HEIGHT / GRID_SPACING];

            renderTarget = DrawUtil.createScaledImageBuffer(WorldScene.FIELD_WIDTH, WorldScene.FIELD_HEIGHT);
            targetGraphics = renderTarget.createGraphics();
            targetGraphics.setBackground(WorldScene.FIELD_COLOR);
            DrawUtil.scaleGraphics(targetGraphics);

            recalculateDisplays();
        }
    }

    @Override
    public void lateUpdate()
    {
        if (getWorld().bFieldUpdated)
            recalculateDisplays();
    }
    
    /**
     * Updates the values of the vectors at each point in the grid
     * of vectors in the magnetic field visualization based on the current
     * magnetic field due to Wires.
     */
    public void recalculateDisplays()
    {
        for (int i = 0; i < fieldDisplayPoints.length; i++)
        {
            int x = i * GRID_SPACING + GRID_SPACING / 2;
            for (int j = 0; j < fieldDisplayPoints[i].length; j++)
            {
                int y = j * GRID_SPACING + GRID_SPACING / 2;

                fieldDisplayPoints[i][j] = getNetFieldAt(WorldScene.screenToWorldPoint(new Vector2(x, y)));
            }
        }

        targetDirty = true;
    }

    private Vector3 getNetFieldAt(Vector2 position)
    {
        Vector3 sum = Vector3.zero;
        for (HasBField b : getWorld().getActorsOfType(HasBField.class))
        {
            if (b instanceof UniformBField)
                continue;

            sum = sum.add(b.getFieldAt(position));
        }

        return sum;
    }

    @Override
    public void render(Graphics2D g)
    {
        if (!targetDirty)
        {
            g.drawImage(renderTarget, 0, 0, WorldScene.FIELD_WIDTH, WorldScene.FIELD_HEIGHT, Game.instance());
            return;
        }

        DrawUtil.clear(renderTarget);

        if (usePlainBGOptimization)
        {
            // Assumes the background is a plain color, with nothing else already drawn
            for (int i = 0; i < fieldDisplayPoints.length; i++)
            {
                int x = i * GRID_SPACING + GRID_SPACING / 2;
                for (int j = 0; j < fieldDisplayPoints[i].length; j++)
                {
                    int y = j * GRID_SPACING + GRID_SPACING / 2;

                    if (fieldDisplayPoints[i][j].z() == 0)
                        continue;

                    Point point = new Point(x, y);

                    targetGraphics.setColor(targetGraphics.getBackground());
                    DrawUtil.fillCircle(targetGraphics, point, VECTOR_RADIUS + (int)(DrawUtil.VECTOR_STROKE_WIDTH + 0.5f));

                    // precalculate the resulting color due to transparency for the entire arrow
                    // (since we are assuming a solid color background)
                    Color c = fieldDisplayPoints[i][j].lengthSq() < FULLY_OPAQUE_LENGTH * FULLY_OPAQUE_LENGTH
                        ? DrawUtil.lerpColor(targetGraphics.getBackground(), COLOR, fieldDisplayPoints[i][j].length() / FULLY_OPAQUE_LENGTH)
                        : COLOR;
                    DrawUtil.drawDirectionVectorZ(targetGraphics, point, fieldDisplayPoints[i][j].z(),
                        c, VECTOR_RADIUS, 0);
                }
            }
        }

        else
        {
            for (int i = 0; i < fieldDisplayPoints.length; i++)
            {
                int x = i * GRID_SPACING + GRID_SPACING / 2;
                for (int j = 0; j < fieldDisplayPoints[i].length; j++)
                {
                    int y = j * GRID_SPACING + GRID_SPACING / 2;

                    if (fieldDisplayPoints[i][j].z() == 0)
                        continue;

                    Point point = new Point(x, y);

                    targetGraphics.setColor(targetGraphics.getBackground());
                    DrawUtil.fillCircle(targetGraphics, point, VECTOR_RADIUS + (int)(DrawUtil.VECTOR_STROKE_WIDTH + 0.5f));

                    DrawUtil.drawDirectionVectorZ(targetGraphics, point, fieldDisplayPoints[i][j].z(),
                        COLOR, VECTOR_RADIUS, FULLY_OPAQUE_LENGTH);
                }
            }
        }

        g.drawImage(renderTarget, 0, 0, WorldScene.FIELD_WIDTH, WorldScene.FIELD_HEIGHT, Game.instance());
        targetDirty = false;
    }

    @Override
    public int getZIndex()
    {
        return 11;
    }
}