
/**
 * 
 */
public class UniformBField extends Actor implements HasBField
{
    private Vector3 strength;

    public UniformBField(Vector3 strength)
    {

    }

    public Vector3 getFieldAt(Vector2 position){
        Vector3 vector = strength;
        return vector; /*FIX THIS */
    }
}
