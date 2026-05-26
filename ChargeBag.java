
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ChargeBag extends Actor
{
    private Rectangle bounds;
    private int chargeLimit, positiveLimit, negativeLimit;
    private int numCharges = 0, numPositive = 0, numNegative = 0;

    private static final int TEXT_PADDING = (int)(10 * Game.RELATIVE_SCALE);

    public ChargeBag(Rectangle bounds, int chargeLimit, int positiveLimit, int negativeLimit)
    {
        this.bounds = bounds;
        this.chargeLimit = chargeLimit;
        this.positiveLimit = positiveLimit;
        this.negativeLimit = negativeLimit;
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
            && bounds.contains(mousePos.toPoint()) && (numCharges < chargeLimit || chargeLimit < 0))
        {
            Charge c;
            float chargeValue = 0;

            // left side: positive charges
            if (mousePos.x() < bounds.getCenterX() && (numPositive < positiveLimit || positiveLimit < 0))
            {
                chargeValue = Charge.ELEMENTARY_CHARGE;
                numPositive++;
            }
            else if (numNegative < negativeLimit || negativeLimit < 0)
            {
                chargeValue = -Charge.ELEMENTARY_CHARGE;
                numNegative++;
            }

            if (chargeValue == 0)
                return;

            numCharges++;

            c = new Charge(chargeValue, mousePos, false);
            getWorld().addActor(c);
            c.update();
        }
    }

    public void chargeRemoved(Charge c)
    {
        numCharges--;
        if (c.getCharge() > 0)
            numPositive--;
        else
            numNegative--;
    }

    @Override
    public void render(Graphics2D g)
    {    
        // charge limits
        if (positiveLimit >= 0 || negativeLimit >= 0 || chargeLimit >= 0)
        {
            g.setColor(
                new Color(WorldScene.FIELD_COLOR.getRed(),
                WorldScene.FIELD_COLOR.getGreen(),
                WorldScene.FIELD_COLOR.getBlue(), 200));
            g.fillRect(bounds.x, bounds.y + bounds.height, bounds.width, WorldScene.BUTTON_HEIGHT / 2 + 2 * TEXT_PADDING);
        }

        g.setColor(Color.BLACK);
        g.setFont(Assets.getFont("JosefinSans", Font.ITALIC, WorldScene.BUTTON_HEIGHT / 2));
        if (positiveLimit >= 0)
            DrawUtil.drawText(g, new Vector2(
                bounds.x + bounds.width / 4, bounds.y + bounds.height + TEXT_PADDING),
                (positiveLimit - numPositive) + "", new Vector2(0.5f, 0));
        if (chargeLimit >= 0)
            DrawUtil.drawText(g, new Vector2(
                bounds.x + bounds.width / 2, bounds.y + bounds.height + TEXT_PADDING),
                (chargeLimit - numCharges) + "", new Vector2(0.5f, 0));
        if (negativeLimit >= 0)
            DrawUtil.drawText(g, new Vector2(
                bounds.x + 3 * bounds.width / 4, bounds.y + bounds.height + TEXT_PADDING),
                (negativeLimit - numNegative) + "", new Vector2(0.5f, 0));

        // actual bag part
        g.setStroke(new BasicStroke(5 * Game.RELATIVE_SCALE));

        g.drawImage(Assets.getImage("plusBag"), bounds.x, bounds.y, bounds.width / 2, bounds.height, Game.instance());
        g.drawImage(Assets.getImage("minusBag"), bounds.x + bounds.width / 2, bounds.y, bounds.width / 2, bounds.height, Game.instance());

        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.drawLine(
            (int)bounds.getCenterX(), (int)bounds.getMinY(),
            (int)bounds.getCenterX(), (int)bounds.getMaxY());
    }

    @Override
    public int getZIndex()
    {
        return 300;
    }
}
