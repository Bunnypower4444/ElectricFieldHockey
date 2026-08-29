
import java.awt.Container;
import java.awt.Point;
import javax.swing.JFrame;

/**
 * The main entry-point class for the application, which creates the window containing the game.
 * 
 * @author  Evan Guo
 * @version 5/25/26
 */
public class ElectricFieldHockey extends JFrame
{
    /**
     * If this value is set to true, it indicates that the application
     * is running on the web.
     */
    public static boolean isWebVersion = false;
    
    /**
     * Creates a new instance of the window and adds the Game as a component
     */
    public ElectricFieldHockey()
    {
        super("Electric Field Hockey");

        add(Game.instance());
    }

    /**
     * Runs the game, creating a new window and instantiating the Game, with the size given by
     * constants in the Game class, and adjusting the size to account for the size of the
     * title of the window. The update and render FPS can be optionally specified through
     * the command-line arguments.
     * @param args command-line arguments; up to two floating-point arguments can be specified,
     * with the first specifying the update FPS and the second the render FPS. A third flag "web" can be added
     * to indicate that the application is running on the web.
     */
    public static void main(String[] args)
    {
        try
        {
            Assets.load();
        }
        catch (Exception e)
        {
            System.err.println(e);
            return;
        }

        if (args.length >= 3 && args[2].equals("web"))
        {
            isWebVersion = true;
            Save.instance().lowDetailMode = true;
        }

        if (args.length >= 2)
            Game.createGame(Float.parseFloat(args[0]), Float.parseFloat(args[1]));
        else if (args.length >= 1)
            Game.createGame(Float.parseFloat(args[0]));
        else
            Game.createGame();

        ElectricFieldHockey EFH = new ElectricFieldHockey();
        EFH.setBounds(100, 100, Game.WIDTH, Game.HEIGHT);

        EFH.setDefaultCloseOperation(EXIT_ON_CLOSE);
        EFH.setVisible(true);

        Vector2 gamePanelOffset = Vector2.zero;
        Container c = Game.instance();
        while (true)
        {
            if (c instanceof ElectricFieldHockey)
                break;
            
            gamePanelOffset = gamePanelOffset.add(new Vector2(c.getLocation()));
            
            c = c.getParent();
        }
            
        Point windowSize = new Vector2(Game.WIDTH, Game.HEIGHT).add(gamePanelOffset).toPoint();
        EFH.setSize(windowSize.x, windowSize.y);
        EFH.setResizable(false);
        
        Game.instance().start();
    }
}