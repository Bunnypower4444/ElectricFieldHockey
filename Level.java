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
 */
public class Level {
    private ArrayList<HashMap<String, String>> objects = new ArrayList<>();

    // Parses the level data into a list of objects and their attributes
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

                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();

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

    //Loads the given world scene with a level
    public void loadLevel(WorldScene world)
    {
        for(HashMap<String, String> obj : objects){
            Actor actor = createActor(obj);
            if (actor != null)
                world.addActor(actor);
        }
    }

   // Creates the actors in the world scene
    private Actor createActor(HashMap<String, String> attrs) {
        String objType = attrs.get("type");
        if (objType == null)
            return null;

        switch(objType){
            case "Puck":
                return new Puck(parseFloat(attrs.get("charge")), parseVector(attrs.get("position")), Vector2.zero);

            case "Goal":
                return new Goal(parseRect(attrs.get("bounds")));

            case "Wall":
                return new Wall(parseRect(attrs.get("bounds")));

            // Pre-placed charges from a level are fixed (cannot be moved by the player)
            case "Charge":
                return new Charge(parseFloat(attrs.get("charge")), parseVector(attrs.get("position")), true);

            case "Wire":
                return new Wire(parseFloat(attrs.get("current")),
                        parseVector(attrs.get("point1")), parseVector(attrs.get("point2")));

            case "UniformEField":
                return new UniformEField(parseVector(attrs.get("strength")));

            // Magnetic fields are restricted to the z-axis to keep the game 2D
            case "UniformBField":
                return new UniformBField(new Vector3(0, 0, parseFloat(attrs.get("strength"))));

            default:
                return null;
        }
    }

    // "1.5" -> 1.5f
    private static float parseFloat(String value)
    {
        return Float.parseFloat(value.trim());
    }

    // "x,y" -> Vector2
    private static Vector2 parseVector(String value)
    {
        String[] parts = value.split(",");
        return new Vector2(Float.parseFloat(parts[0].trim()), Float.parseFloat(parts[1].trim()));
    }

    // "x,y,w,h" -> Rectangle
    private static Rectangle parseRect(String value)
    {
        String[] parts = value.split(",");
        return new Rectangle(
            (int)Float.parseFloat(parts[0].trim()),
            (int)Float.parseFloat(parts[1].trim()),
            (int)Float.parseFloat(parts[2].trim()),
            (int)Float.parseFloat(parts[3].trim()));
    }
}
