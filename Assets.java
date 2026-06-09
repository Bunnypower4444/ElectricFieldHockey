import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads the game's level files from disk 
 *
 * @author  Adeline Krishna, Evan Guo
 * @version 5/25/26
 */

public class Assets
{
    private static final String ASSETS_FOLDER = "Assets";
    private static final String LEVELS_DIR = "Levels";
    private static final String IMAGES_DIR = "Images";
    private static final String FONTS_DIR = "Fonts";

    private static Path assetsPath;

    private static ArrayList<Level> levels = new ArrayList<>();
    private static HashMap<String, BufferedImage> images = new HashMap<>();
    private static HashMap<String, Font> baseFonts = new HashMap<>();

    private static class FontParameters
    {
        private int style;
        private float size;

        public FontParameters(int style, float size)
        {
            this.style = style;
            this.size = size;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof FontParameters))
                return false;

            FontParameters fp = (FontParameters)obj;
            return style == fp.style && size == fp.size;
        }

        @Override
        public int hashCode()
        {
            return style + Float.valueOf(size).hashCode();
        }
    }

    private static HashMap<String, HashMap<FontParameters, Font>> cachedFonts = new HashMap<>();

    /**
     * Loads all levels, images and fonts from disk.
     * @throws FileNotFoundException if the Assets folder cannot be found
     * @throws URISyntaxException if unable to get the location of the application
     */
    public static void load() throws FileNotFoundException, URISyntaxException
    {
        // finds that path to the assets folder, since
        // the place where the code is run may not be the
        // same directory as where the assets are located
        // (code adapted from Celeste64's Assets.ContentPath getter)

        // get location of the running code (works also for jar files)
        Path appPath = Path.of(Assets.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (!Files.isDirectory(appPath))
            appPath = appPath.getParent();

        Path searchUpPath = Path.of("");
        int up = 0;

        while (!Files.isDirectory(appPath.resolve(searchUpPath).resolve(ASSETS_FOLDER)) && up++ < 5)
        {
            searchUpPath = searchUpPath.resolve("..");
        }

        if (!Files.isDirectory(appPath.resolve(searchUpPath).resolve(ASSETS_FOLDER)))
            throw new FileNotFoundException("Unable to find " + ASSETS_FOLDER + " Directory from " + appPath);

        assetsPath = appPath.resolve(searchUpPath).resolve(ASSETS_FOLDER);

        loadLevels();
        loadImages();
        loadFonts();
    }

    /**
     * Gets all the info for a given level.
     *
     * @param num the zero-based level index
     * @return the level, or null if the index is out of range
     */
    public static Level getLevel(int num)
    {
        int index = num;
        if (index < 0 || index >= levels.size())
            return null;
        return levels.get(index);
    }

    /**
     * Returns the number of levels.
     *
     * @return the number of loaded levels
     */
    public static int levelCount()
    {
        return levels.size();
    }

    /**
     * Gets the image for a named object.
     *
     * @param name the image name (file name without extension)
     * @return the image, or null if no image with that name is loaded
     */
    public static BufferedImage getImage(String name)
    {
        return images.get(name);
    }

    /**
     * Gets a named font derived with the given style and size.
     *
     * @param name  the font name (file name without extension)
     * @param style the font style (e.g. {@link Font#PLAIN}, {@link Font#BOLD})
     * @param size  the point size
     * @return the derived font
     * @throws NullPointerException if no font with that name is loaded
     */
    public static Font getFont(String name, int style, float size)
    {
        FontParameters fp = new FontParameters(style, size);
        if (cachedFonts.get(name).containsKey(fp))
        {
            return cachedFonts.get(name).get(fp);
        }
        else
        {
            Font derivedFont = baseFonts.get(name).deriveFont(style, size);
            cachedFonts.get(name).put(fp, derivedFont);
            return derivedFont;
        }
    }

    /**
     * Loads data for all the levels.
     */
    private static void loadLevels()
    {
        levels.clear();

        File dir = assetsPath.resolve(LEVELS_DIR).toFile();
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
    /**
     * Loads images for all the objects.
     */
    private static void loadImages()
    {
        File dir = assetsPath.resolve(IMAGES_DIR).toFile();
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

    /**
     * Loads fonts for all the objects and registers them with the graphics environment.
     */
    private static void loadFonts()
    {
        File dir = assetsPath.resolve(FONTS_DIR).toFile();
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
                    String name = stripExtension(file.getName());
                    baseFonts.put(name, font);

                    HashMap<FontParameters, Font> fontCache = new HashMap<>();
                    cachedFonts.put(name, fontCache);
                    fontCache.put(new FontParameters(font.getStyle(), font.getSize()), font);

                    ge.registerFont(font);
                }
            }
            catch (Exception e)
            {
                System.err.println("Failed to load font " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Removes the file extension from a file name.
     *
     * @param fileName the file name
     * @return the file name without its extension
     */
    private static String stripExtension(String fileName)
    {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
