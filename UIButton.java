
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class UIButton
{
    private Rectangle bounds;
    private String text;
    private Color color;
    private Runnable action;

    public UIButton(Rectangle bounds, String text, Color color, Runnable action)
    {
        this.bounds = bounds;
        this.text = text;
        this.color = color;
        this.action = action;
    }

    public boolean mouseOver(Vector2 mousePos)
    {
        return bounds.contains((int)mousePos.x(), (int)mousePos.y());
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

    public void render(Graphics2D g)
    {
        Color prevColor = g.getColor();

        g.setColor(color);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setColor(Color.BLACK);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setFont(new Font("Monospace", Font.PLAIN, (int)(0.85 * bounds.height)));

        DrawUtil.drawText(g, new Vector2((float)bounds.getCenterX(), (float)bounds.getCenterY()), text, new Vector2(0.5f, 0.5f));

        g.setColor(prevColor);
    }
}
