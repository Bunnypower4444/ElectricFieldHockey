
import java.awt.Graphics2D;

/**
 * An abstract class, representing an indivudal scene/page in the game.
 * Scenes receive updates for being updated and rendered by the Game.
 * 
 * @author Aarohi Shah
 * @version 5/12/26
 */
public abstract class Scene
{
    /**
     * Updates the state of the scene, which is done by the Game on every physics tick.
     * This method should be overridden by the child class to define update functionality.
     */
    public abstract void update();
    /**
     * Renders the scene to the given Graphics2D, which is done by the Game on every frame.
     * This method should be overridden by the child class to render the specific scebe.
     * <p>
     * Postcondition: the state of the scene should not be modified
     * @param g The Graphics2D object to which to render the scene
     */
    public abstract void render(Graphics2D g);
}

