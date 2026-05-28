import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Stores the data for a single level: a list of objects/obstacles and their
 * properties, parsed from a level file. Each object is one HashMap of
 * attribute name -> value, and turns those into Actors in a
 * WorldScene.
 *
 * @author  Adeline Krishna
 * @version 5/25/26
 *
 */
public class Level {
    private ArrayList<HashMap<String, String>> objects = new ArrayList<>();
    private int chargeLimit = -1;
    private int positiveLimit = -1;
    private int negativeLimit = -1;
    private String name = null;

    private Wire lastWire = null;

    /**
     * Parses the level data into a list of objects and their attributes.
     *
     * @param data the input stream of the level file to read
     */
    public Level(InputStream data)
    {
        HashMap<String, String> current = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(data)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                // a blank line ends the current object
                if (line.isEmpty())
                {
                    if (current != null)
                    {
                        objects.add(current);
                        current = null;
                    }
                    continue;
                }

                if (line.startsWith("#"))
                    continue;

                int eq = line.indexOf('=');
                if (eq < 0)
                    continue;

                String key = line.substring(0, eq).trim().toLowerCase();
                String value = line.substring(eq + 1).trim();
                
                // global info about the level
                if (line.startsWith("!"))
                {
                    switch(key.substring(1))
                    {
                        case "chargelimit":
                            chargeLimit = parseInt(value);
                            break;
                        case "positivelimit":
                            positiveLimit = parseInt(value);
                            break;
                        case "negativelimit":
                            negativeLimit = parseInt(value);
                            break;
                        case "name":
                            name = value;
                            break;
                    }
                    continue;
                }

                if (current == null)
                    current = new HashMap<>();
                current.put(key, value);
            }
        }
        catch (IOException e)
        {
            System.err.println("Failed to parse level: " + e.getMessage());
        }

        if (current != null)
            objects.add(current);

    }

    /**
     * @return the total charge limit, or -1 if none is set
     */
    public int getChargeLimit()
    {
        return chargeLimit;
    }

    /**
     * @return the positive charge limit, or -1 if none is set
     */
    public int getPositiveLimit()
    {
        return positiveLimit;
    }

    /**
     * @return the negative charge limit, or -1 if none is set
     */
    public int getNegativeLimit()
    {
        return negativeLimit;
    }

    /**
     * @return the level name, or null if none is set
     */
    public String getName()
    {
        return name;
    }

    /**
     * Loads the given world scene with a level.
     *
     * @param world the world scene to add this level's actors to
     */
    public void loadLevel(WorldScene world)
    {
        for(HashMap<String, String> obj : objects){
            Actor actor = createActor(obj);
            if (actor != null)
                world.addActor(actor);
        }
    }

    /**
     * Creates the actor in the world scene for a single object.
     *
     * @param attrs the object's attribute name -> value map
     * @return the created actor, or null if the type is unknown
     */
    private Actor createActor(HashMap<String, String> attrs) {
        String objType = attrs.get("type").toLowerCase();
        if (objType == null)
            return null;

        switch(objType){
            case "puck":
                Vector2 velocity = Vector2.zero;
                if (attrs.containsKey("velocity"))
                    velocity = parseVector(attrs.get("velocity"));

                return new Puck(parseFloat(attrs.get("charge")) * Charge.ELEMENTARY_CHARGE, parseVector(attrs.get("position")), velocity);

            case "goal":
                Goal.Orientation facing = Goal.Orientation.Left;

                if (attrs.containsKey("facing"))
                {
                    String facingStr = attrs.get("facing");
                    facingStr = facingStr.substring(0, 1).toUpperCase()
                        + facingStr.substring(1).toLowerCase();

                    facing = Goal.Orientation.valueOf(facingStr);
                }

                return new Goal(parseRectCentered(attrs.get("bounds")), facing);

            case "wall":
                return new Wall(parseRect(attrs.get("bounds")));

            // Pre-placed charges from a level are fixed (cannot be moved by the player)
            case "charge":
                return new Charge(parseFloat(attrs.get("charge")) * Charge.ELEMENTARY_CHARGE, parseVector(attrs.get("position")), true);

            case "wire":
                return lastWire = new Wire(parseFloat(attrs.get("current")),
                        parseVector(attrs.get("point1")), parseVector(attrs.get("point2")));

            case "switch":
                return new Switch(lastWire, parseVector(attrs.get("position")), parseFloat(attrs.get("radius")));

            case "uniformefield":
                return new UniformEField(parseRect(attrs.get("bounds")), parseVector(attrs.get("strength")));

            // Magnetic fields are restricted to the z-axis to keep the game 2D
            case "uniformbfield":
                return new UniformBField(parseRect(attrs.get("bounds")),new Vector3(0, 0, parseFloat(attrs.get("strength"))));

            default:
                return null;
        }
    }

    /**
     * @param value the text to parse
     * @return the parsed integer
     * @throws NumberFormatException if the value is not a valid integer
     */
    private static int parseInt(String value)
    {
        return Integer.parseInt(value.trim());
    }

    /**
     * Parses a float, e.g. "1.5" -> 1.5f.
     *
     * @param value the text to parse
     * @return the parsed float
     * @throws NumberFormatException if the value is not a valid float
     */
    private static float parseFloat(String value)
    {
        return Float.parseFloat(value.trim());
    }

    /**
     * Parses a 2D vector, e.g. "x,y" -> Vector2.
     *
     * @param value the comma-separated "x,y" text
     * @return the parsed vector
     * @throws NumberFormatException if a component is not a valid float
     */
    private static Vector2 parseVector(String value)
    {
        String[] parts = value.split(",");
        return new Vector2(Float.parseFloat(parts[0].trim()), Float.parseFloat(parts[1].trim()));
    }

    /**
     * Parses a rectangle, e.g. "x,y,w,h" -> Rectangle.
     *
     * @param value the comma-separated "x,y,w,h" text
     * @return the parsed rectangle
     * @throws NumberFormatException if a component is not a valid number
     */
    private static Rectangle parseRect(String value)
    {
        String[] parts = value.split(",");
        return new Rectangle(
            (int)Float.parseFloat(parts[0].trim()),
            (int)Float.parseFloat(parts[1].trim()),
            (int)Float.parseFloat(parts[2].trim()),
            (int)Float.parseFloat(parts[3].trim()));
    }

    /**
     * Parses a rectangle whose "x,y" is treated as its center rather than its
     * top-left corner.
     *
     * @param value the comma-separated "x,y,w,h" text
     * @return the parsed rectangle, repositioned so x,y is the center
     * @throws NumberFormatException if a component is not a valid number
     */
    private static Rectangle parseRectCentered(String value)
    {
        Rectangle rect = parseRect(value);
        rect.x -= rect.width / 2;
        rect.y -= rect.height / 2;
        return rect;
    }
}
