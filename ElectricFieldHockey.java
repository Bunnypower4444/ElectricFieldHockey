
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

        this.add(Game.instance());
    }

    public static void main(String[] args)
    {
        ElectricFieldHockey EFH = new ElectricFieldHockey();
        EFH.setBounds(300, 300, Game.WIDTH, Game.HEIGHT);
        EFH.setDefaultCloseOperation(EXIT_ON_CLOSE);
        EFH.setVisible(true);
    }
}