import java.awt.Rectangle;
/**
 * 
 */
public class Puck extends Actor
{
    private float charge;
    private static final float MASS = 1;
    private Vector2 position;
    private Vector2 initialPosition;

    public Puck(float charge, Vector2 position)
    {

        initialPosition = position;
    }
    
    public Rectangle collisionBox()
    {
        
    }

    public void reset()
    {
        position = initialPosition;
    }
}