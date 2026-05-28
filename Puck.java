
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The charged puck the player tries to guide into a goal. Moves under the
 * electric and magnetic fields in the world via the Lorentz force.
 *
 * @author  Adeline Krishna, Evan Guo
 * @version 5/25/26
 */
public class Puck extends Actor implements RequireReset, LateUpdate
{
    /**
     * The radius of the puck, in meters
     */
    public static final int RADIUS = 13;

    private static final float MASS = 3; // 1.67262192595E-27F;
    private static final Color COLOR = Color.BLACK;
    private static final Color VELOCITY_COLOR = new Color(6, 120, 36);
    private static final float FORCE_DISPLAY_FACTOR = 0.03f;
    private static final float VELOCITY_DISPLAY_FACTOR = 0.2f;

    private float charge;
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration = Vector2.zero;
    
    private Vector2 netEField = Vector2.zero;
    private Vector3 netBField = Vector3.zero;

    private final Vector2 initialPosition;
    private final Vector2 initialVelocity;

    private HashMap<Color, LinkedList<Vector2>> electricFields = new HashMap<>();
    private HashMap<Color, LinkedList<Vector2>> magneticForces = new HashMap<>();

    /**
     * Initializes the charge, position and velocity.
     *
     * @param charge          the puck's charge, in coulombs
     * @param position        the world-space starting position, in meters
     * @param initialVelocity the world-space starting velocity, in m/s
     */
    public Puck(float charge, Vector2 position, Vector2 initialVelocity)
    {
        initialPosition = position;
        this.initialVelocity = initialVelocity;
        this.charge = charge;
        this.position = position;
        velocity = initialVelocity;
    }

    /**
     * Activates any switches the puck is currently touching.
     */
    @Override
    public void update()
    {
        // check for activating switches
        for (Switch s : getWorld().getActorsOfType(Switch.class))
        {
            if (s.collision(this))
                s.activate();
        }
    }

    /**
     * Advances the puck's motion for one frame and checks for scoring,
     * goal-wall collisions and wall collisions.
     */
    @Override
    public void lateUpdate()
    {
        if (getWorld().getPaused() || !getWorld().gameStarted())
        {
            if (getWorld().eFieldUpdated)
                netEField = updateEFields();
            if (getWorld().bFieldUpdated)
                netBField = updateBFields();

            acceleration = Calc.lorentzForce(charge, netEField, velocity, netBField).xy().div(MASS);

            return;
        }
        
        netEField = updateEFields();
        netBField = updateBFields();

        // F = ma
        acceleration = Calc.lorentzForce(charge, netEField, velocity, netBField).xy().div(MASS);

        // dv = a dt
        Vector2 velocity0 = velocity;
        velocity = velocity.add(acceleration.mult(getWorld().deltaTime()));

        // dx = v dt, but use average velocity
        position = position.add((velocity.add(velocity0).div(2)).mult(getWorld().deltaTime()));

        // check for collisions or scoring a goal
        // scoring a goal should take priority over collisions
        // in case there are multiple goals, don't immediately stop searching through goals
        //  when a collision with a goal's walls is detected
        boolean collision = false;
        for (Goal g : getWorld().getActorsOfType(Goal.class))
        {
            if (g.goalCollision(this))
            {
                getWorld().levelComplete();
                return;
            }
            else if (g.wallCollision(this))
            {
                collision = true;
            }
        }

        // only need to check walls if there are no collisions with goals
        if (!collision)
        {
            for (Wall w : getWorld().getActorsOfType(Wall.class))
            {
                if (w.collision(this))
                {
                    collision = true;
                    break;
                }
            }
        }

        if (collision)
        {
            getWorld().levelFail();
        }
    }

    /**
     * @param e the electric field source
     * @return the color used to draw that source's force vector
     */
    private static Color getEForceColor(HasEField e)
    {
        if (e instanceof Charge)
            return ((Charge)e).getCharge() > 0 ? Color.PINK : Color.CYAN;
        if (e instanceof UniformEField)
            return new Color(250, 206, 175);
        return Color.RED;
    }

    /**
     * @param e the magnetic field source
     * @return the color used to draw that source's force vector
     */
    private static Color getBForceColor(HasBField e)
    {
        if (e instanceof Wire)
            return new Color(252, 252, 177);
        if (e instanceof UniformBField)
            return new Color(189, 136, 242);
        return Color.MAGENTA;
    }

    /**
     * Recomputes the per-source electric force vectors for display.
     *
     * @return the net electric field at the puck's position
     */
    private Vector2 updateEFields()
    {
        electricFields.clear();

        Vector2 netEField = Vector2.zero;
        for (HasEField e : getWorld().getActorsOfType(HasEField.class))
        {
            Vector2 field = e.getFieldAt(position);
            netEField = netEField.add(field);

            if (!field.equals(Vector2.zero))
            {
                Color c = getEForceColor(e);
                if (electricFields.get(c) == null)
                    electricFields.put(c, new LinkedList<>());

                electricFields.get(c).add(
                    Calc.electricForce(charge, field)
                    .mult(FORCE_DISPLAY_FACTOR));
            }
        }

        return netEField;
    }

    /**
     * Recomputes the per-source magnetic force vectors for display.
     *
     * @return the net magnetic field at the puck's position
     */
    private Vector3 updateBFields()
    {
        magneticForces.clear();

        Vector3 netBField = Vector3.zero;
        for (HasBField b : getWorld().getActorsOfType(HasBField.class))
        {
            Vector3 field = b.getFieldAt(position);
            netBField = netBField.add(field);

            if (!field.equals(Vector3.zero))
            {
                Color c = getBForceColor(b);
                if (magneticForces.get(c) == null)
                    magneticForces.put(c, new LinkedList<>());
                
                magneticForces.get(c).add(
                    Calc.magneticForce(charge, velocity, field).xy()
                    .mult(FORCE_DISPLAY_FACTOR));
            }
        }

        return netBField;
    }

    /**
     * Gets the world space position of the puck.
     * @return the puck's current position, in meters
     */
    public Vector2 getPosition()
    {
        return position;
    }

    /**
     * Gets the velocity of the puck.
     * @return the puck's current velocity, in m/s
     */
    public Vector2 getVelocity()
    {
        return velocity;
    }

    /**
     * Gets the acceleration of the puck.
     * @return the puck's current acceleration, in m/s²
     */
    public Vector2 getAcceleration()
    {
        return acceleration;
    }

    /**
     * Creates a box around the puck for detecting collision.
     *
     * @return the puck's collision world-space bounding box
     */
    public Rectangle collisionBox()
    {
        int startX = (int)position.x();
        int startY = (int)position.y();

        int sideLength = (int)(Math.sqrt(2) * RADIUS);

        Rectangle box = new Rectangle(startX - sideLength / 2, startY - sideLength / 2, sideLength, sideLength);
        return(box);
    }

    /**
     * Draws the puck along with its force and velocity vectors.
     *
     * @param g the graphics context to draw with
     */
    @Override
    public void render(Graphics2D g)
    {
        Vector2 screenPos = WorldScene.worldToScreenPoint(position);
        Point screenPosPoint = screenPos.toPoint();

        int drawRadius = (int)(RADIUS * WorldScene.FIELD_WIDTH / WorldScene.WORLD_DIMENSIONS.x());

        for (Color c : electricFields.keySet())
        {
            for (Vector2 v : electricFields.get(c))
            {
                DrawUtil.drawMagnitudeVector(g, screenPosPoint, v, c, drawRadius, 30);
            }
        }

        for (Color c : magneticForces.keySet())
        {
            for (Vector2 v : magneticForces.get(c))
            {
                DrawUtil.drawMagnitudeVector(g, screenPosPoint, v, c, drawRadius, 10);
            }
        }

        DrawUtil.drawMagnitudeVector(g, screenPosPoint, velocity.mult(VELOCITY_DISPLAY_FACTOR), VELOCITY_COLOR, drawRadius / 2, 10);

        g.setColor(COLOR);
        DrawUtil.fillCircle(g, screenPosPoint, drawRadius);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(2 * drawRadius)));
        DrawUtil.drawText(g, screenPos, getText(), new Vector2(0.5f, 0.5f));
    }

    /**
     * @return "+" for a positive charge, "-" for a negative charge, or "" for neutral
     */
    private String getText()
    {
        if (charge > 0)
            return "+";
        if (charge < 0)
            return "-";
        return "";
    }

    /**
     * Restores the puck to its initial position and velocity.
     */
    @Override
    public void reset()
    {
        position = initialPosition;
        velocity = initialVelocity;
        getWorld().eFieldUpdated = true;
        getWorld().bFieldUpdated = true;
    }

    /**
     * @return the draw order index
     */
    @Override
    public int getZIndex()
    {
        return 300;
    }
}
