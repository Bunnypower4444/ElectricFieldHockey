import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads the game's level files from disk 
 */

public class Assets
{
    private static final String LEVELS_DIR = "Assets/Levels";
    private static final String IMAGES_DIR = "Assets/Images";
    private static final String FONTS_DIR = "Assets/Fonts";

    private static ArrayList<Level> levels = new ArrayList<>();
    private static HashMap<String, BufferedImage> images = new HashMap<>();
    private static HashMap<String, Font> fonts = new HashMap<>();

    public static void load()
    {
        loadLevels();
        loadImages();
        loadFonts();
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

    public static Font getFont(String name, int style, float size)
    {
        return fonts.get(name).deriveFont(style, size);
    }

    private static void loadFonts()
    {
        File dir = new File(FONTS_DIR);
        if (!dir.isDirectory())
            return;

        File[] files = dir.listFiles((d, name) ->
        {
            String lower = name.toLowerCase();
            return lower.endsWith(".ttf") || lower.endsWith(".otf");
        });
        if (files == null) return;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (File file : files)
        {
            try
            {
                Font font = Font.createFont(Font.TRUETYPE_FONT, file);
                if (font != null)
                {
                    fonts.put(stripExtension(file.getName()), font);
                    ge.registerFont(font);
                }
            }
            catch (Exception e)
            {
                System.err.println("Failed to load font " + file.getName() + ": " + e.getMessage());
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
