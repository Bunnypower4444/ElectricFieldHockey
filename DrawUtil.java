
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
            justify = Vector2.zero;

        FontMetrics fontMetrics = g.getFontMetrics();
        Vector2 textSize = new Vector2(fontMetrics.stringWidth(text), fontMetrics.getHeight());

        // add ascent because positive y is down and drawString uses the y position for the bottom of the text
        Point drawPos = pos.sub(textSize.mult(justify)).add(Vector2.unitY.mult(fontMetrics.getAscent())).toPoint();
        g.drawString(text, drawPos.x, drawPos.y);
    }

    public static Vector2[] getInfiniteLineEndpoints(Vector2 pos, Vector2 direction)
    {
        if (direction.equals(Vector2.zero))
            throw new IllegalArgumentException("Line direction cannot be the zero vector");

        // vertical line
        if (direction.x() == 0)
        {
            if (direction.y() > 0)
                return new Vector2[] { new Vector2(pos.x(), 0), new Vector2(pos.x(), WorldScene.HEIGHT) };
            else
                return new Vector2[] { new Vector2(pos.x(), WorldScene.HEIGHT), new Vector2(pos.x(), 0) };
        }

        Vector2[] result = new Vector2[2];

        float slope = direction.y() / direction.x();

        for (int i = 0; i < 2; i++)
        {
            float screenEdgeX = i * WorldScene.WIDTH;
            // point-slope form
            float intsersectY = pos.y() + slope * (screenEdgeX - pos.x());

            int index = direction.x() < 0 ? 1 - i : i;

            if (intsersectY < 0 || intsersectY > WorldScene.HEIGHT)
            {
                float y = Math.max(Math.min(intsersectY, WorldScene.HEIGHT), 0);
                // solve for screenEdgeX in the above point-slope form equation
                // -slope * (x - pos.x()) = pos.y() - y
                // -slope * x = pos.y() - y - slope * pos.x()
                // x = (y - pos.y()) / slope + pos.x()
                float x = (y - pos.y()) / slope + pos.x();

                if (x < 0 || x > WorldScene.WIDTH)
                    return null;

                result[index] = new Vector2(x, y);
            }
            else
                result[index] = new Vector2(screenEdgeX, intsersectY);
        }

        return result;
    }

    public static Vector2 worldToScreen(Vector2 vector)
    {
        return vector.withY(WorldScene.HEIGHT - vector.y());
    }

    public static Vector2 screenToWorld(Vector2 vector)
    {
        return vector.withY(WorldScene.HEIGHT - vector.y());
    }
}
