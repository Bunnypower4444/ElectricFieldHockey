
/**
 * 
 */
public class Switch extends Actor implements RequireReset
{
    private Vector2 position;
    private float radius;
    private Wire wire;

    public Switch(Wire wire, Vector2 position, float radius)
    {
        
    }

    public boolean collision(Puck puck)
    {
        boolean bool = false;
        return bool; /*FIX THIS */
    }

    public void activate()
    {
        
    }

    @Override
    public void reset()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reset'");
    }

    @Override
    public int getZIndex()
    {
        return 100;
    }
}
