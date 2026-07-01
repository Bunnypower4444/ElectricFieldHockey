
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * An actor that calculates and visualizes the net electric field in the world
 * due to Charges.
 */
public class TotalEField extends Actor implements LateUpdate
{
    private static final int VECTOR_LENGTH = (int)(40 * Game.RELATIVE_SCALE);
    private static final float FULLY_OPAQUE_LENGTH = 50000;
    private static final int VECTOR_WIDTH = (int)(5 * Game.RELATIVE_SCALE);
    private static final int GRID_SPACING = (int)(50 * Game.RELATIVE_SCALE);
    private static final Color COLOR = Color.BLACK;

    private Vector2[][] fieldDisplayPoints;
    
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
            fieldDisplayPoints = new Vector2[WorldScene.FIELD_WIDTH / GRID_SPACING][WorldScene.FIELD_HEIGHT / GRID_SPACING];

            renderTarget = DrawUtil.createScaledImageBuffer(WorldScene.FIELD_WIDTH, WorldScene.FIELD_HEIGHT);
            targetGraphics = renderTarget.createGraphics();
            targetGraphics.setBackground(WorldScene.FIELD_COLOR);
            DrawUtil.scaleGraphics(targetGraphics);
            targetGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            recalculateDisplays();
        }
    }

    @Override
    public void lateUpdate()
    {
        if (getWorld().eFieldUpdated)
            recalculateDisplays();
    }
    
    /**
     * Updates the values of the vectors at each point in the grid
     * of vectors in the electric field visualization based on the current
     * magnetic field due to Charges.
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

    private Vector2 getNetFieldAt(Vector2 position)
    {
        Vector2 sum = Vector2.zero;
        for (HasEField e : getWorld().getActorsOfType(HasEField.class))
        {
            if (e instanceof UniformEField)
                continue;

            sum = sum.add(e.getFieldAt(position));
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

        for (int i = 0; i < fieldDisplayPoints.length; i++)
        {
            int x = i * GRID_SPACING + GRID_SPACING / 2;
            for (int j = 0; j < fieldDisplayPoints[i].length; j++)
            {
                int y = j * GRID_SPACING + GRID_SPACING / 2;

                if (fieldDisplayPoints[i][j].equals(Vector2.zero))
                    continue;

                DrawUtil.drawDirectionVector(targetGraphics, new Point(x, y), fieldDisplayPoints[i][j],
                    COLOR, VECTOR_WIDTH, VECTOR_LENGTH, FULLY_OPAQUE_LENGTH);
            }
        }

        g.drawImage(renderTarget, 0, 0, WorldScene.FIELD_WIDTH, WorldScene.FIELD_HEIGHT, Game.instance());
        targetDirty = false;
    }

    @Override
    public int getZIndex()
    {
        return 10;
    }
}
