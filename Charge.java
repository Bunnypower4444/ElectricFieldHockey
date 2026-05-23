
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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

    public static final float ELEMENTARY_CHARGE = 1.602176634E-19f;

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

    public boolean isFixed()
    {
        return fixed;
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
        else if (dragOffset != null)
        {
            dragOffset = null;

            // remove this charge if the mouse is released over a ChargeBag
            if (getWorld().getActorsOfType(ChargeBag.class).get(0)
                .positionInBounds(Game.instance().mousePos()))
                getWorld().removeActor(this);
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(getColor());
        DrawUtil.fillCircle(g, position.toPoint(), RADIUS);

        if (fixed)
        {
            g.setStroke(new BasicStroke(3 * Game.RELATIVE_SCALE));
            g.setColor(Color.BLACK);
            DrawUtil.drawCircle(g, position.toPoint(), RADIUS);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(0.85 * RADIUS)));
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