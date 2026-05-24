
import java.awt.Rectangle;

/**
 * 
 */
public class Puck extends Actor implements RequireReset
{
    private float charge;
    private static final float MASS = 1;
    private static final int RADIUS = 1;
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