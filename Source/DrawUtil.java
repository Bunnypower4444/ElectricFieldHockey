
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * A static utility class containing convenient methods for drawing, such as for drawing various shapes
 * to a Graphics2D object.
 * 
 * @author  Evan Guo
 * @version 5/26/26
 */
public class DrawUtil
{
    /**
     * Fills in a circle centered at a specified point with a specified radius with the current color.
     * @param g The Graphics2D object to which to render the shape
     * @param center The center of the circle
     * @param radius The radius of the circle
     */
    public static void fillCircle(Graphics2D g, Point center, int radius)
    {
        g.fillOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }

    /**
     * Fills the outline of a circle centered at a specified point with a specified radius with the current color.
     * @param g The Graphics2D object to which to render the shape
     * @param center The center of the circle
     * @param radius The radius of the circle
     */
    public static void drawCircle(Graphics2D g, Point center, int radius)
    {
        g.drawOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }

    /**
     * Draws a given string of text using a specified text justify.
     * @param g The Graphics2D object to which to render the shape
     * @param pos The position of the text
     * @param text The string to draw
     * @param justify A vector representing the justify of the string, which defines
     * where in the text <code>position</code> refers to. Each component represents
     * the multiple of the text's size in that direction. For example, for the <code>position</code>
     * to refer to the top left corner, the vector &lt;0, 0&gt; should be used; for the center,
     * &lt;0.5, 0.5&gt;; for the bottom right corner, &lt;1, 1&gt;.
     */
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

    /**
     * Calculates the end points used for rendering an infinitely long line.
     * @param pos A vector representing a point that the line passes through
     * @param direction The direction vector of the line
     * @param screenDimensions A vector representing the dimensions of the screen
     * @return An array with two elements, each representing an endpoint of the line.
     * The two endpoints are ordered based on the direction vector.
     * @throws IllegalArgumentException if <code>direction</code> is equal to the zero vector
     */
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

    /**
     * Fills in an equilateral triangle centered at a specified point with a specified side length
     * at a specified angle with the current color.
     * @param g The Graphics2D object to which to render the shape
     * @param position The center of the triangle
     * @param angle The angle from the positive x-axis toward the positive y-axis, in radians
     * @param length The side length of the triangle
     */
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

    /**
     * The stroke width used to render the outline of vectors
     */
    public static final float VECTOR_STROKE_WIDTH = 2 * Game.RELATIVE_SCALE;

    private static final BasicStroke VECTOR_STROKE = new BasicStroke(VECTOR_STROKE_WIDTH);

    /**
     * Draws the outline of a vector, with the opacity decreasing linearly when the vector's length
     * is less than a specified value. However, the vector will always be drawn to have a constant length.
     * This method is useful for highlighting the direction of a vector, such as in
     * visualizing the net electric field.
     * @param g The Graphics2D object to which to render the shape
     * @param position The screen-space position of the tail of the vector
     * @param vector The world-space vector to draw (NOTE: the positive y-axis is up)
     * @param color The color using which to draw the vector
     * @param width The width using which to draw the vector
     * @param length The length using which to draw the vector
     * @param fullyOpaqueLength The minimum length at which the vector will be drawn at full opacity
     */
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

    /**
     * Fills in a vector, with the displayed vector's length reflecting the vector's length and the
     * width decreasing linearly when the length is less than a specified value. This method is useful
     * for highlighting the magnitude of a vector, such as in displaying forces applied to an object.
     * @param g The Graphics2D object to which to render the shape
     * @param position The screen-space position of the tail of the vector
     * @param vector The world-space vector to draw (NOTE: the positive y-axis is up)
     * @param color The color using which to draw the vector
     * @param maxWidth The maximum width of the drawn vector
     * @param maxWidthLength The minimum length at which the vector will be drawn at its maximum width
     */
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

    /**
     * Visualizes the z-component of a 3D vector using dots and crosses, with the opacity decreasing
     * linearly when the z-component is less than a specified value. This method also draws
     * the dot/cross with circle surrounding it. This method is used for highlighting the direction of a vector,
     * such as in visualizing the net magnetic field.
     * @param g The Graphics2D object to which to render the shape
     * @param position The screen-space position at which to draw the vector
     * @param z The world-space vector to draw (NOTE: the positive z-axis is out of the screen)
     * @param color The color using which to draw the vector
     * @param radius The radius using which to draw the vector
     * @param fullyOpaqueLength The minimum length at which the vector will be drawn at full opacity
     */
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

    /**
     * Draws a dot or a cross with a specified radius, used to represent the z-component of a 3D vector.
     * If <code>radius</code> is positive, a dot will be drawn; otherwise, a cross will be drawn. The dots/crosses
     * are designed to be able to fit within a circle with the magnitude of the specified radius.
     * @param g The Graphics2D object to which to render the shape
     * @param point The screen-space position at which to draw the vector
     * @param radius The radius of a circle surrounding the shape. The radius should have the same
     * sign as the z-component in world space of a vector being represented. If <code>radius > 0</code>,
     * a dot will be drawn; otherwise, a cross will be drawn. The magnitude of <code>radius</code> is used to
     * determine the size of a surrounding circle.
     */
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

    /**
     * Fills in a rectangle given by the specified bounds in world space, using the current color.
     * @param g The Graphics2D object to which to render the shape
     * @param worldBounds The world-space bounds of the rectangle (NOTE: the x and y values specify the
     * bottom left corner)
     */
    public static void fillWorldRectangle(Graphics2D g, Rectangle worldBounds)
    {
        Point point = WorldScene.worldToScreenPoint(new Vector2(worldBounds.x, worldBounds.y)).toPoint();
        Point size = WorldScene.worldToScreenVector(new Vector2(worldBounds.width, worldBounds.height)).toPoint();
        // size.y will be negative, so flip it and shift up point
        size.y = -size.y;
        point.y -= size.y;
        g.fillRect(point.x, point.y, size.x, size.y);
    }

    /**
     * Checks if a given point is on the playing field.
     * @param point The point, with its position in screen space
     * @return true if the point is on the playing field; false otherwise
     */
    public static boolean pointOnScreen(Vector2 point)
    {
        return point.x() >= 0 && point.y() >= 0
            && point.x() <= WorldScene.FIELD_WIDTH && point.y() <= WorldScene.FIELD_HEIGHT;
    }
}
