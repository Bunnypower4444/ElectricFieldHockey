import java.util.HashMap;
import java.awt.image.BufferedImage;

/**
 * 
 */

public class Assets
{
    private static final String LEVELS_DIR = "assets/levels";
    private static final String IMAGES_DIR = "assets/images";

    private static Level[] levels = new Level[0];
    private static HashMap<String, BufferedImage> images = new HashMap<>();

    public static void load()
    {
        loadLevels();
        loadImages();
    }
    //Gets all the info for a given level
    public static Level getLevel(int num)
    {
        int index = num - 1;
        if (index < 0 || index >= levels.length)
            return null;
        return levels[index];
    }
    // returns the number of levels
    public static int levelCount()
    {
        return levels.length;
    }
    // Gets the image for a named object
    public static BufferedImage getImage(String name)
    {
        return images.get(name);
    }
    //Loads data for all the levels
    private static void loadLevels()
    {
        File dir = new File(LEVELS_DIR);
        if (!dir.isDirectory())
        {
            levels = new Level[0];
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null)
        {
            levels = new Level[0];
            return;
        }

        // Sort so that level1.txt, level2.txt, ... come out in order.
        java.util.Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        ArrayList<Level> loaded = new ArrayList<>();
        for (File file : files)
        {
            try (InputStream in = new FileInputStream(file))
            {
                loaded.add(new Level(in));
            }
            catch (IOException e)
            {
                System.err.println("Failed to load level " + file.getName() + ": " + e.getMessage());
            }
        }

        levels = loaded.toArray(new Level[0]);
    }
    // Loads images for all the objects
    private static void loadImages()
    {
        File dir = new File(IMAGES_DIR);
        if (!dir.isDirectory())
            return;

        File[] files = dir.listFiles((d, name) ->
        {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });
        if (files == null) return;

        for (File file : files)
        {
            try
            {
                BufferedImage img = ImageIO.read(file);
                if (img != null)
                    images.put(stripExtension(file.getName()), img);
            }
            catch (IOException e)
            {
                System.err.println("Failed to load image " + file.getName() + ": " + e.getMessage());
            }
        }
    }
    // Removes file extension
    private static String stripExtension(String fileName)
    {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
