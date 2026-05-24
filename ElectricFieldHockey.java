
import java.awt.Container;
import java.awt.Point;
import javax.swing.JFrame;

/**
 * 
 */
public class ElectricFieldHockey extends JFrame
{
    public ElectricFieldHockey()
    {
        super("Electric Field Hockey");
        
        Game.createGame();

        add(Game.instance());
    }

    public static void main(String[] args)
    {
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
    }
}