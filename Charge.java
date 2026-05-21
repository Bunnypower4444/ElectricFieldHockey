
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * 
 */
public class Charge extends Actor implements HasEField
{
    private static final int RADIUS = (int)(20 * Game.RELATIVE_SCALE);
    private static final Color POSITIVE = Color.RED;
    private static final Color NEGATIVE = Color.BLUE;
    private static final Color NEUTRAL = Color.DARK_GRAY;

    private float charge;
    private Vector2 position;
    private boolean fixed;
    private Vector2 dragOffset = null;

    public Charge(float charge, Vector2 position, boolean fixed)
    {
        this.charge = charge;
        this.position = position;
        this.fixed = fixed;
    }

    @Override
    public void update()
    {
        if (fixed)
            return;

        if (!getWorld().gameStarted()
            && Game.instance().mousePressed()
            && mouseOver(Game.instance().mousePos()))
            dragOffset = position.sub(Game.instance().mousePos());
        
        if (!getWorld().gameStarted()
            && dragOffset != null && Game.instance().mouseDown())
            position = Game.instance().mousePos().add(dragOffset);
        else
            dragOffset = null;
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(getColor());
        DrawUtil.drawCircle(g, position.toPoint(), RADIUS);
        
        g.setColor(Color.WHITE);
        DrawUtil.drawText(g, position, getText(), new Vector2(0.5f, 0.5f));
    }

    private boolean mouseOver(Vector2 pos)
    {
        return pos.sub(position).lengthSq() <= RADIUS * RADIUS;
    }

    private Color getColor()
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
        return Calc.coulombLawField(this.position, charge, position);
    }
}