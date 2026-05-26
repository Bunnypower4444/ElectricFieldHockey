
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;

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

    public static Vector2[] getInfiniteLineEndpoints(Vector2 pos, Vector2 direction, Vector2 screenDimensions)
    {
        if (direction.equals(Vector2.zero))
            throw new IllegalArgumentException("Line direction cannot be the zero vector");

        // vertical line
        if (direction.x() == 0)
        {
            if (direction.y() > 0)
                return new Vector2[] { new Vector2(pos.x(), 0), new Vector2(pos.x(), screenDimensions.y()) };
            else
                return new Vector2[] { new Vector2(pos.x(), screenDimensions.y()), new Vector2(pos.x(), 0) };
        }

        Vector2[] result = new Vector2[2];

        float slope = direction.y() / direction.x();

        for (int i = 0; i < 2; i++)
        {
            float screenEdgeX = i * screenDimensions.x();
            // point-slope form
            float intsersectY = pos.y() + slope * (screenEdgeX - pos.x());

            int index = direction.x() < 0 ? 1 - i : i;

            if (intsersectY < 0 || intsersectY > screenDimensions.y())
            {
                float y = Math.max(Math.min(intsersectY, screenDimensions.y()), 0);
                // solve for screenEdgeX in the above point-slope form equation
                // -slope * (x - pos.x()) = pos.y() - y
                // -slope * x = pos.y() - y - slope * pos.x()
                // x = (y - pos.y()) / slope + pos.x()
                float x = (y - pos.y()) / slope + pos.x();

                if (x < 0 || x > screenDimensions.x())
                    return null;

                result[index] = new Vector2(x, y);
            }
            else
                result[index] = new Vector2(screenEdgeX, intsersectY);
        }

        return result;
    }

    private static Polygon calculateEqTrianglePolygon(float length)
    {
        double altitude = length * Math.sqrt(3) / 2;

        int[] x = new int[] { (int)(-altitude / 2), (int)(altitude / 2), (int)(-altitude / 2) };
        int[] y = new int[] { (int)(-length / 2), 0, (int)(length / 2)};

        return new Polygon(x, y, x.length);
    }

    public static void fillEqTriangle(Graphics2D g, Point position, float angle, float length)
    {

        Polygon arrow = calculateEqTrianglePolygon(length);
        arrow.translate(position.x, position.y);
        
        g.rotate(-angle, position.x, position.y);

        g.drawPolygon(arrow);

        g.rotate(angle, position.x, position.y);
    }

    private static Polygon calculateArrowPolygon(int length, int width)
    {
        // arrow tip will be an equilateral triangle with side length [2 * width]
        // points starting from below the arrow base, going counter-clockwise
        int tipLeft = (int)(length - width * Math.sqrt(3));

        int[] x = new int[]
        {
            0,
            tipLeft,
            tipLeft,
            length,
            tipLeft,
            tipLeft,
            0
        };
        int[] y = new int[]
        {
            -width / 2,
            -width / 2,
            -width,
            0,
            width,
            width / 2,
            width / 2
        };

        return new Polygon(x, y, x.length);
    }

    public static final float VECTOR_STROKE_WIDTH = 2 * Game.RELATIVE_SCALE;
    private static final BasicStroke VECTOR_STROKE = new BasicStroke(VECTOR_STROKE_WIDTH);

    public static void drawDirectionVector(Graphics2D g, Point position, Vector2 vector, Color color, int width, int length, float fullyOpaqueLength)
    {
        vector = WorldScene.worldToScreenVector(vector);

        Color pColor = g.getColor();
        Stroke pStroke = g.getStroke();

        if (vector.lengthSq() < fullyOpaqueLength * fullyOpaqueLength)
        {
            int alpha = (int)(color.getAlpha() * vector.length() / fullyOpaqueLength);
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }

        g.setColor(color);
        g.setStroke(VECTOR_STROKE);

        Polygon arrow = calculateArrowPolygon(length, width);
        arrow.translate(position.x, position.y);
        
        g.rotate(vector.angle(), position.x, position.y);

        g.drawPolygon(arrow);

        g.rotate(-vector.angle(), position.x, position.y);

        g.setColor(pColor);
        g.setStroke(pStroke);
    }

    public static void drawMagnitudeVector(Graphics2D g, Point position, Vector2 vector, Color color, int maxWidth, float maxWidthLength)
    {
        vector = WorldScene.worldToScreenVector(vector);
        
        int width = maxWidth;
        if (vector.lengthSq() < maxWidthLength * maxWidthLength)
            width = (int)(maxWidth * vector.length() / maxWidthLength);

        Color pColor = g.getColor();
        g.setColor(color);

        Polygon arrow = calculateArrowPolygon((int)vector.length(), width);
        arrow.translate(position.x, position.y);
        
        g.rotate(vector.angle(), position.x, position.y);

        g.fillPolygon(arrow);

        g.rotate(-vector.angle(), position.x, position.y);

        g.setColor(pColor);
    }

    public static void drawDirectionVectorZ(Graphics2D g, Point position, float z, Color color, float radius, float fullyOpaqueLength)
    {
        final int CIRCLE_RADIUS = (int)(8 * Game.RELATIVE_SCALE);

        Color pColor = g.getColor();
        Stroke pStroke = g.getStroke();

        if (Math.abs(z) < fullyOpaqueLength)
        {
            int alpha = (int)(color.getAlpha() * Math.abs(z) / fullyOpaqueLength);
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }

        g.setColor(color);
        g.setStroke(VECTOR_STROKE);

        drawCircle(g, position, CIRCLE_RADIUS);
        drawDotCross(g, position, CIRCLE_RADIUS * Math.signum(z));

        g.setColor(pColor);
        g.setStroke(pStroke);
    }

    public static void drawDotCross(Graphics2D g, Point point, float radius)
    {
        // scaling shenanigans so that the result isn't confined to larger pixels
        if (radius > 0)
        {
            final float KNOT_SIZE_PROPORTION = 0.5f;
            g.translate(point.x, point.y);
            g.scale(KNOT_SIZE_PROPORTION * radius, KNOT_SIZE_PROPORTION * radius);
            fillCircle(g, Vector2.zero.toPoint(), 1);
            g.scale(1 / (KNOT_SIZE_PROPORTION * radius), 1 / (KNOT_SIZE_PROPORTION * radius));
            g.translate(-point.x, -point.y);
        }
        else
        {
            Stroke pStroke = g.getStroke();

            g.setStroke(new BasicStroke(VECTOR_STROKE_WIDTH / (-radius * (float)(Math.sqrt(2) / 2))));

            g.translate(point.x, point.y);
            double component = radius * Math.sqrt(2) / 2;
            g.scale(component, component);

            g.drawLine(
                 - 1, - 1,
                 + 1, + 1);
            g.drawLine(
                 - 1, + 1,
                 + 1, - 1);

            g.scale(1 / component, 1 / component);
            g.translate(-point.x, -point.y);
            g.setStroke(pStroke);
        }
    }

    public static void drawWorldRectangle(Graphics2D g, Rectangle worldBounds)
    {
        Point point = WorldScene.worldToScreenPoint(new Vector2(worldBounds.x, worldBounds.y)).toPoint();
        Point size = WorldScene.worldToScreenVector(new Vector2(worldBounds.width, worldBounds.height)).toPoint();
        // size.y will be negative, so flip it and shift up point
        size.y = -size.y;
        point.y -= size.y;
        g.fillRect(point.x, point.y, size.x, size.y);
    }

    public static boolean pointOnScreen(Vector2 point)
    {
        return point.x() >= 0 && point.y() >= 0
            && point.x() <= WorldScene.FIELD_WIDTH && point.y() <= WorldScene.FIELD_HEIGHT;
    }
}
