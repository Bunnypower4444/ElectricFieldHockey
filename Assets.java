import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Loads the game's level files from disk and hands out Level objects by number.
 */

public class Assets
{
    private static final String LEVELS_DIR = "levels";

    private static Level[] levels = new Level[0];

    public static void load()
    {
        loadLevels();
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
}
