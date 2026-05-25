
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 
 */
public class Puck extends Actor implements RequireReset, LateUpdate
{
    private static final float MASS = 3; // 1.67262192595E-27F;
    private static final int RADIUS = (int)(10 * Game.RELATIVE_SCALE);
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

    //Initializes the charge, and position
    public Puck(float charge, Vector2 position, Vector2 initialVelocity)
    {
        initialPosition = position;
        this.initialVelocity = initialVelocity;
        this.charge = charge;
        this.position = position;
        velocity = initialVelocity;
    }

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

    private static Color getEForceColor(HasEField e)
    {
        if (e instanceof Charge)
            return ((Charge)e).getCharge() > 0 ? Color.PINK : Color.CYAN;
        if (e instanceof UniformEField)
            return new Color(250, 206, 175);
        return Color.RED;
    }

    private static Color getBForceColor(HasBField e)
    {
        if (e instanceof Wire)
            return new Color(252, 252, 177);
        if (e instanceof UniformBField)
            return new Color(189, 136, 242);
        return Color.MAGENTA;
    }

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

    public Vector2 getPosition()
    {
        return position;
    }

    public Vector2 getVelocity()
    {
        return velocity;
    }

    public Vector2 getAcceleration()
    {
        return acceleration;
    }
    
    //Creates a box around the puck for detecting collision
    public Rectangle collisionBox()
    {
        int startX = (int)position.x();
        int startY = (int)position.y();

        int sideLength = (int)(Math.sqrt(2) * RADIUS);

        Rectangle box = new Rectangle(startX - sideLength / 2, startY - sideLength / 2, sideLength, sideLength);
        return(box);
    }

    @Override
    public void render(Graphics2D g)
    {
        Vector2 screenPos = WorldScene.worldToScreenPoint(position);
        Point screenPosPoint = screenPos.toPoint();

        for (Color c : electricFields.keySet())
        {
            for (Vector2 v : electricFields.get(c))
            {
                DrawUtil.drawMagnitudeVector(g, screenPosPoint, v, c, RADIUS, 30);
            }
        }

        for (Color c : magneticForces.keySet())
        {
            for (Vector2 v : magneticForces.get(c))
            {
                DrawUtil.drawMagnitudeVector(g, screenPosPoint, v, c, RADIUS, 10);
            }
        }

        DrawUtil.drawMagnitudeVector(g, screenPosPoint, velocity.mult(VELOCITY_DISPLAY_FACTOR), VELOCITY_COLOR, RADIUS / 2, 10);

        g.setColor(COLOR);
        DrawUtil.fillCircle(g, screenPosPoint, RADIUS);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(2 * RADIUS)));
        DrawUtil.drawText(g, screenPos, getText(), new Vector2(0.5f, 0.5f));
    }

    private String getText()
    {
        if (charge > 0)
            return "+";
        if (charge < 0)
            return "-";
        return "";
    }

    @Override
    public void reset()
    {
        position = initialPosition;
        velocity = initialVelocity;
        getWorld().eFieldUpdated = true;
        getWorld().bFieldUpdated = true;
    }

    @Override
    public int getZIndex()
    {
        return 300;
    }
}