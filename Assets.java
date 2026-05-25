import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Loads the game's level files from disk 
 */

public class Assets
{
    private static final String LEVELS_DIR = "levels";

    private static ArrayList<Level> levels = new ArrayList<>();

    public static void load()
    {
        loadLevels();
    }
    //Gets all the info for a given level
    public static Level getLevel(int num)
    {
        int index = num - 1;
        if (index < 0 || index >= levels.size())
            return null;
        return levels.get(index);
    }
    // returns the number of levels
    public static int levelCount()
    {
        return levels.size();
    }
    //Loads data for all the levels
    private static void loadLevels()
    {
        File dir = new File(LEVELS_DIR);
        if (!dir.isDirectory())
        {
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null)
        {
            return;
        }

        // Sort so that level1.txt, level2.txt, ... come out in order.
        java.util.Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        for (File file : files)
        {
            try (InputStream in = new FileInputStream(file))
            {
                levels.add(new Level(in));
            }
            catch (IOException e)
            {
                System.err.println("Failed to load level " + file.getName() + ": " + e.getMessage());
            }
        }

    }
}
