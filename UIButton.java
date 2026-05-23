
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

    public UIButton(Rectangle bounds, String text, Color color, Runnable action)
    {
        this.bounds = bounds;
        this.text = text;
        this.color = color;
        this.action = action;
    }

    public boolean mouseOver(Vector2 mousePos)
    {
        return visible && bounds.contains(mousePos.toPoint());
    }

    public void click()
    {
        action.run();
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

    public void render(Graphics2D g)
    {
        if (!visible)
            return;

        Color prevColor = g.getColor();
        Stroke prevStroke = g.getStroke();

        g.setColor(color);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2 * Game.RELATIVE_SCALE));
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(0.6 * bounds.height)));

        DrawUtil.drawText(g, new Vector2((float)bounds.getCenterX(), (float)bounds.getCenterY()), text, new Vector2(0.5f, 0.5f));

        g.setColor(prevColor);
        g.setStroke(prevStroke);
    }
}
