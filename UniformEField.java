
/**
 * 
 */
public class UniformEField extends Actor implements HasEField
{
    private Vector2 strength;

    public UniformEField(Vector2 strength)
    {
        
    }
    public Vector2 getFieldAt(Vector2 position){
        Vector2 vector = strength;
        return vector; /*FIX THIS */
    }

    @Override
    public int getZIndex()
    {
        return 10;
    }
}
