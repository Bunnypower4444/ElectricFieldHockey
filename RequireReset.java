
/**
 * An interface implemented by Actors whose state needs to be reset when the player
 * presses the Reset button on a level.
 * 
 * @author Evan Guo
 * @version 5/21/26
 */
public interface RequireReset
{
    /**
     * Invoked when the player presses the Reset button on a level, allowing
     * an Actor to properly revert to its initial state.
     */
    public void reset();
}
