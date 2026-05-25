
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class UIButton
{
    private Rectangle bounds;
    private String text;
    private Color color;
    private Runnable action;
    private boolean visible = true;
    private boolean enabled = true;

    private static enum PressedState { Depressed, Hover, Pressed, Disabled }
    private PressedState state = PressedState.Depressed;

    public UIButton(Rectangle bounds, String text, Color color, Runnable action)
    {
        this.bounds = bounds;
        this.text = text;
        this.color = color;
        this.action = action;
    }

    private boolean positionInBounds(Vector2 mousePos)
    {
        return bounds.contains(mousePos.toPoint());
    }

    public void update()
    {
        if (!visible)
            return;

        if (!enabled)
        {
            state = PressedState.Disabled;
            return;
        }

        if (!positionInBounds(Game.instance().mousePos()))
            state = PressedState.Depressed;
        else if (Game.instance().consumeClick())
        {
            state = PressedState.Depressed;
            action.run();
        }
        else if (Game.instance().mouseDown())
            state = PressedState.Pressed;
        else
            state = PressedState.Hover;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public boolean getVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void render(Graphics2D g)
    {
        if (!visible)
            return;

        Color prevColor = g.getColor();
        Stroke prevStroke = g.getStroke();

        Color c;
        switch (state)
        {
            case Pressed:
                c = color.darker();
                break;
            default:
                c = color;
                break;
        }

        g.setColor(c);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        if (state == PressedState.Hover || state == PressedState.Pressed)
        {
            g.setColor(Color.GRAY);
            g.setStroke(new BasicStroke(4 * Game.RELATIVE_SCALE));
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        Color strokeColor = state == PressedState.Disabled ? Color.GRAY : Color.BLACK;

        g.setColor(strokeColor);
        g.setStroke(new BasicStroke(2 * Game.RELATIVE_SCALE));
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(0.5 * bounds.height)));

        DrawUtil.drawText(g, new Vector2((float)bounds.getCenterX(), (float)bounds.getCenterY()), text, new Vector2(0.5f, 0.5f));

        g.setColor(prevColor);
        g.setStroke(prevStroke);
    }
}
