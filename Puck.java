
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The object the player tries to move into the goal. Being charged, the puck
 * responds to the net electric and magnetic fields every frame (Lorentz force
 * F = q(E + v x B)), handles collisions with goals and walls, and draws an arrow
 * showing the net force acting on it.
 */
public class Puck extends Actor implements RequireReset
{
    private static final float MASS = 1;
    private static final int RADIUS = (int)(10 * Game.RELATIVE_SCALE);
    private static final Color COLOR = Color.BLACK;
    private static final Color ARROW_COLOR = new Color(200, 0, 0);
    private static final float ARROW_LENGTH = 40 * Game.RELATIVE_SCALE;

    // Gameplay tuning factor: the simulation uses real SI constants (Coulomb's /
    // Ampere's law in Calc), so raw forces are far too small to be visible. This
    // scales force into screen-units-per-second-squared. Tune to taste.
    private static final float FORCE_SCALE = 1e15f;

    private float charge;
    private Vector2 screenPos;
    private Vector2 initialPosition;
    private Vector2 velocity = Vector2.zero;
    private Vector2 lastForce = Vector2.zero;

    //Initializes the charge, and position
    public Puck(float charge, Vector2 position)
    {

        initialPosition = position;
        this.charge = charge;
        this.screenPos = position;
    }

    @Override
    public void update()
    {
        WorldScene world = getWorld();

        // The puck only moves once the player presses Play (and not while paused/won/lost).
        if (!world.gameStarted() || world.getPaused())
            return;

        // Field calculations work in world coordinates (y-up), screen is y-down.
        Vector2 worldPos = DrawUtil.screenToWorld(screenPos);

        Vector2 netE = Vector2.zero;
        for (HasEField source : world.getActorsOfType(HasEField.class))
            netE = netE.add(source.getFieldAt(worldPos));

        Vector3 netB = Vector3.zero;
        for (HasBField source : world.getActorsOfType(HasBField.class))
            netB = netB.add(source.getFieldAt(worldPos));

        // Lorentz force: F = q(E + v x B)
        Vector3 force3 = new Vector3(netE).add(new Vector3(velocity).cross(netB)).mult(charge);
        lastForce = force3.xy();

        Vector2 acceleration = lastForce.div(MASS).mult(FORCE_SCALE);

        float dt = world.deltaTime();
        velocity = velocity.add(acceleration.mult(dt));
        worldPos = worldPos.add(velocity.mult(dt));
        screenPos = DrawUtil.worldToScreen(worldPos);

        checkCollisions(world);
    }

    // Wins if the puck reaches a goal's win zone; fails on goal posts or walls.
    private void checkCollisions(WorldScene world)
    {
        for (Goal goal : world.getActorsOfType(Goal.class))
        {
            if (goal.goalCollision(this))
            {
                world.levelComplete();
                return;
            }
            if (goal.wallCollision(this))
            {
                world.levelFail();
                return;
            }
        }

        for (Wall wall : world.getActorsOfType(Wall.class))
        {
            if (wall.collision(this))
            {
                world.levelFail();
                return;
            }
        }
    }

    @Override
    public void lateUpdate()
    {

    }

    //Creates a box around the puck for detecting collision
    public Rectangle collisionBox()
    {
        int startX = (int)screenPos.x();
        int startY = (int)screenPos.y();
        Rectangle box = new Rectangle(startX - RADIUS, startY - RADIUS, 2*RADIUS, 2*RADIUS);
        return(box);
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(COLOR);
        DrawUtil.fillCircle(g, screenPos.toPoint(), RADIUS);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(2 * RADIUS)));
        DrawUtil.drawText(g, screenPos, getText(), new Vector2(0.5f, 0.5f));

        renderForceArrow(g);
    }

    // Draws an arrow from the puck in the direction of the net force on it.
    private void renderForceArrow(Graphics2D g)
    {
        if (lastForce.equals(Vector2.zero))
            return;

        // flip y back to screen space, then use a fixed length so it is always visible
        Vector2 dir = new Vector2(lastForce.x(), -lastForce.y()).normalize();
        Vector2 tip = screenPos.add(dir.mult(ARROW_LENGTH));

        g.setColor(ARROW_COLOR);
        g.setStroke(new BasicStroke(2 * Game.RELATIVE_SCALE));
        g.drawLine((int)screenPos.x(), (int)screenPos.y(), (int)tip.x(), (int)tip.y());

        float angle = dir.angle();
        float headSize = 8 * Game.RELATIVE_SCALE;
        Vector2 left = tip.sub(Vector2.createPolar(angle - 0.5f, headSize));
        Vector2 right = tip.sub(Vector2.createPolar(angle + 0.5f, headSize));
        g.drawLine((int)tip.x(), (int)tip.y(), (int)left.x(), (int)left.y());
        g.drawLine((int)tip.x(), (int)tip.y(), (int)right.x(), (int)right.y());
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
        screenPos = initialPosition;
        velocity = Vector2.zero;
        lastForce = Vector2.zero;
    }

    @Override
    public int getZIndex()
    {
        return 300;
    }
}
