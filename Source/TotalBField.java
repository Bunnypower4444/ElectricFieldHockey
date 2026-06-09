import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * An actor that calculates and visualizes the net magnetic field in the world
 * due to Wires.
 */
public class TotalBField extends Actor implements LateUpdate
{
    private Vector3[][] fieldDisplayPoints;

    private static final float FULLY_OPAQUE_LENGTH = 150;
    private static final int VECTOR_RADIUS = (int)(6 * Game.RELATIVE_SCALE);
    private static final int GRID_SPACING = (int)(50 * Game.RELATIVE_SCALE);
    private static final Color COLOR = new Color(61, 7, 115);

    @Override
    public void setWorld(WorldScene world)
    {
        if (getWorld() != world && world != null)
        {
            fieldDisplayPoints = new Vector3[WorldScene.FIELD_WIDTH / GRID_SPACING][WorldScene.FIELD_HEIGHT / GRID_SPACING];
        }

        super.setWorld(world);

        recalculateDisplays();
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
        for (int i = 0; i < fieldDisplayPoints.length; i++)
        {
            int x = i * GRID_SPACING + GRID_SPACING / 2;
            for (int j = 0; j < fieldDisplayPoints[i].length; j++)
            {
                int y = j * GRID_SPACING + GRID_SPACING / 2;

                if (fieldDisplayPoints[i][j].z() == 0)
                    continue;

                Point point = new Point(x, y);

                g.setColor(WorldScene.FIELD_COLOR);
                DrawUtil.fillCircle(g, point, VECTOR_RADIUS + (int)(DrawUtil.VECTOR_STROKE_WIDTH + 0.5f));

                DrawUtil.drawDirectionVectorZ(g, point, fieldDisplayPoints[i][j].z(),
                    COLOR, VECTOR_RADIUS, FULLY_OPAQUE_LENGTH);
            }
        }
    }

    @Override
    public int getZIndex()
    {
        return 11;
    }
}