
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

public class DrawUtil
{
    public static void fillCircle(Graphics2D g, Point center, int radius)
    {
        g.fillOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }

    public static void drawCircle(Graphics2D g, Point center, int radius)
    {
        g.drawOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }

    public static void drawText(Graphics2D g, Vector2 pos, String text, Vector2 justify)
    {
        if (justify == null)
            justify = Vector2.zero();

        FontMetrics fontMetrics = g.getFontMetrics();
        Vector2 textSize = new Vector2(fontMetrics.stringWidth(text), g.getFont().getSize());

        // -(1 - y) because positive y is down and drawString uses the y position for the bottom of the text
        Point drawPos = pos.sub(textSize.mult(justify.withY(-(1 - justify.y())))).toPoint();
        g.drawString(text, drawPos.x, drawPos.y);
    }

    public static Vector2[] getInfiniteLineEndpoints(Vector2 pos, Vector2 direction)
    {
        if (direction.equals(Vector2.zero()))
            throw new IllegalArgumentException("Line direction cannot be the zero vector");

        // vertical line
        if (direction.x() == 0)
        {
            if (direction.y() > 0)
                return new Vector2[] { new Vector2(pos.x(), 0), new Vector2(pos.x(), Game.HEIGHT) };
            else
                return new Vector2[] { new Vector2(pos.x(), Game.HEIGHT), new Vector2(pos.x(), 0) };
        }

        Vector2[] result = new Vector2[2];

        float slope = direction.y() / direction.x();

        for (int i = 0; i < 2; i++)
        {
            float screenEdgeX = i * Game.WIDTH;
            float intsersectY = pos.y() + slope * (screenEdgeX - pos.x());

            int index = direction.x() < 0 ? 1 - i : i;

            if (intsersectY < 0 || intsersectY > Game.HEIGHT)
            {
                float y = Math.max(Math.min(intsersectY, Game.HEIGHT), 0);
                float x = (pos.y() - y) / slope + screenEdgeX;

                if (x < 0 || x > Game.WIDTH)
                    return null;

                result[index] = new Vector2(x, y);
            }
            else
                result[index] = new Vector2(screenEdgeX, intsersectY);
        }

        return result;
    }

    public static Vector2 processVector(Vector2 vector)
    {
        return vector.withY(-vector.y());
    }
}
