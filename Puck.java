
import java.awt.Rectangle;

/**
 * 
 */
public class Puck extends Actor implements RequireReset
{
    private float charge;
    private static final float MASS = 1;
    private static final int RADIUS = 1;
    private Vector2 position;
    private Vector2 initialPosition;

    //Initializes the charge, and position
    public Puck(float charge, Vector2 position)
    {

        initialPosition = position;
        this.charge = charge;
        this.position = position;
    }

    @Override
    public void lateUpdate()
    {

    }
    
    //Creates a box around the puck for detecting collision
    public Rectangle collisionBox()
    {
        int startX = (int)position.x();
        int startY = (int)position.y();
        Rectangle box = new Rectangle(startX - RADIUS, startY - RADIUS, 2*RADIUS, 2*RADIUS);
        return(box);
    }

    @Override
    public void reset()
    {
        position = initialPosition;
    }
}