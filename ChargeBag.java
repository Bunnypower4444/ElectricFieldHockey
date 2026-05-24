
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ChargeBag extends Actor
{
    private Rectangle bounds;

    public ChargeBag(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    public boolean positionInBounds(Vector2 pos)
    {
        return bounds.contains(pos.toPoint());
    }

    @Override
    public void update()
    {
        Vector2 mousePos = Game.instance().mousePos();
        if (!getWorld().gameStarted() && Game.instance().mousePressed()
            && bounds.contains(mousePos.toPoint()))
        {
            Charge c;
            float chargeValue;

            // left side: positive charges
            if (mousePos.x() < bounds.getCenterX())
                chargeValue = Charge.ELEMENTARY_CHARGE;
            else
                chargeValue = -Charge.ELEMENTARY_CHARGE;

            c = new Charge(chargeValue, mousePos, false);
            getWorld().addActor(c);
            c.update();
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setStroke(new BasicStroke(5 * Game.RELATIVE_SCALE));
        g.setColor(Color.BLACK);

        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.drawLine(
            (int)bounds.getCenterX(), (int)bounds.getMinY(),
            (int)bounds.getCenterX(), (int)bounds.getMaxY());

        // TODO: add images
    }

    @Override
    public int getZIndex()
    {
        return 120;
    }
}
