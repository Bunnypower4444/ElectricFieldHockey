
import java.awt.Graphics2D;

/**
 * An interface implemented by classes that inlucde graphics,
 * specifying functionality for update and render calls every frame.
 * 
 * @author  Evan Guo
 * @version 8/7/26
 */
public interface GraphicsElement
{
    /**
     * Updates the state of the GraphicsElement. This method is designed
     * to be called on every update tick, and it is where any changes to
     * the object's state should be made.
     */
    void update();

    /**
     * Renders the GraphicsElement to the given Graphics2D object.
     * This method is designed to be called on every render frame. If
     * any changes need to be made to the object's state, they should
     * preferably be done in update() so that successive calls to
     * render() produce the same result.
     * @param g The Graphics2D object to which to render this object
     */
    void render(Graphics2D g);
}
