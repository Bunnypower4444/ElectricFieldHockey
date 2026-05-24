
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 
 */
public class Puck extends Actor implements RequireReset
{
    private static final float MASS = 1;
    private static final int RADIUS = (int)(10 * Game.RELATIVE_SCALE);
    private static final Color COLOR = Color.BLACK;

    private float charge;
    private Vector2 screenPos;
    private Vector2 initialPosition;

    //Initializes the charge, and position
    public Puck(float charge, Vector2 position)
    {

        initialPosition = position;
        this.charge = charge;
        this.screenPos = position;
    }

    @Override
    public void lateUpdate()
    {

    }
    
    //Creates a box around the puck for detecting collision
    public Rectangle collisionBox()
    {
        int startX = (int)screenPos.x();
        int startY = (int)screenPos.y();
        Rectangle box = new Rectangle(startX - RADIUS, startY - RADIUS, 2*RADIUS, 2*RADIUS);
        return(box);
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(COLOR);
        DrawUtil.fillCircle(g, screenPos.toPoint(), RADIUS);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(2 * RADIUS)));
        DrawUtil.drawText(g, screenPos, getText(), new Vector2(0.5f, 0.5f));
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
    public void reset()
    {
        screenPos = initialPosition;
    }

    @Override
    public int getZIndex()
    {
        return 300;
    }
}