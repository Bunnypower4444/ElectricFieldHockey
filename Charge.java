
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * 
 */
public class Charge extends Actor implements HasEField
{
    private static final int RADIUS = (int)(10 * Game.RELATIVE_SCALE);
    private static final Color POSITIVE = Color.RED;
    private static final Color NEGATIVE = Color.BLUE;
    private static final Color NEUTRAL = Color.DARK_GRAY;

    public static final float ELEMENTARY_CHARGE = 1E-1f; // 1.602176634E-19f;

    private float charge;
    private Vector2 screenPos;
    private boolean fixed;
    private Vector2 dragOffset = null;

    public Charge(float charge, Vector2 position, boolean fixed)
    {
        this.charge = charge;
        this.screenPos = position;
        this.fixed = fixed;
    }

    @Override
    public void setWorld(WorldScene world)
    {
        if (getWorld() != null && getWorld() != world)
            getWorld().eFieldUpdated = true;
        if (world != null)
            world.eFieldUpdated = true;

        super.setWorld(world);
    }

    public boolean isFixed()
    {
        return fixed;
    }

    public float getCharge()
    {
        return charge;
    }

    @Override
    public void update()
    {
        if (fixed)
            return;

        if (!getWorld().gameStarted()
            && mouseOver(Game.instance().mousePos())
            && Game.instance().consumePress())
            dragOffset = screenPos.sub(Game.instance().mousePos());
        
        if (!getWorld().gameStarted()
            && dragOffset != null && Game.instance().mouseDown())
        {
            screenPos = Game.instance().mousePos().add(dragOffset);
            getWorld().eFieldUpdated = true;
        }
        else if (dragOffset != null)
        {
            dragOffset = null;

            getWorld().eFieldUpdated = true;

            ChargeBag bag = getWorld().getActorsOfType(ChargeBag.class).get(0);
            // remove this charge if the mouse is released over a ChargeBag
            if (bag.positionInBounds(Game.instance().mousePos()))
            {
                getWorld().removeActor(this);
                bag.chargeRemoved(this);
            }
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(getColor());
        DrawUtil.fillCircle(g, screenPos.toPoint(), RADIUS);

        if (fixed)
        {
            g.setStroke(new BasicStroke(3 * Game.RELATIVE_SCALE));
            g.setColor(Color.BLACK);
            DrawUtil.drawCircle(g, screenPos.toPoint(), RADIUS);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(2 * RADIUS)));
        DrawUtil.drawText(g, screenPos, getText(), new Vector2(0.5f, 0.5f));
    }

    private boolean mouseOver(Vector2 pos)
    {
        return pos.sub(screenPos).lengthSq() <= RADIUS * RADIUS;
    }

    public Color getColor()
    {
        if (charge > 0)
            return POSITIVE;
        if (charge < 0)
            return NEGATIVE;
        return NEUTRAL;
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
    public Vector2 getFieldAt(Vector2 position)
    {
        return Calc.coulombLawField(WorldScene.screenToWorldPoint(this.screenPos), charge, position);
    }

    @Override
    public int getZIndex()
    {
        return 310;
    }
}