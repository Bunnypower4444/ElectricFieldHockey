
import java.io.Serializable;

/**
 * Represents a serializable save file for the player.
 * 
 * @author  Evan Guo
 * @version 8/7/26
 */
public class Save implements Serializable
{
    private static Save instance = new Save();

    /**
     * If set to true, certain actors will be rendered more simply
     * to reduce graphics lag.
     */
    public boolean lowDetailMode = false;

    /**
     * Gets the current save file.
     * @return The save file
     */
    public static Save instance()
    {
        return instance;
    }

    /**
     * Toggles the value of {@link Save#lowDetailMode}.
     */
    public void toggleLowDetailMode()
    {
        lowDetailMode = !lowDetailMode;
    }
}
