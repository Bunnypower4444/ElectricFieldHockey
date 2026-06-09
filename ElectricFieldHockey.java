
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
     * Creates a new instance of the window and instantiates and adds the Game
     */
    public ElectricFieldHockey()
    {
        super("Electric Field Hockey");
        
        Game.createGame();

        add(Game.instance());
    }

    /**
     * Runs the game, creating a new window with the size given by constants in the Game class,
     * and adjusting the size to account for the size of the title of the window.
     * @param args command-line arguments (not used)
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

        ElectricFieldHockey EFH = new ElectricFieldHockey();
        EFH.setBounds(300, 300, Game.WIDTH, Game.HEIGHT);

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