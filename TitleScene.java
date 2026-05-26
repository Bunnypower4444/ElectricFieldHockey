import java.awt.Graphics2D;

/**
 * 
 */
public class TitleScene extends Scene
{
    private static final int BUTTON_X = 300;
    private static final int BUTTON_Y = 300;
    private static final int BUTTON_W = 200;
    private static final int BUTTON_H = 50;
    
    public TitleScene()
    {
        addMouseListener(
            new MouseAdapter(){
                public void mouseClicked(MouseEvent e){
                    if (e.getX() >= BUTTON_X && e.getX() <= BUTTON_X + BUTTON_W && e.getY() >= BUTTON_Y && e.getY() <= BUTTON_Y + BUTTON_H)
                        Game.instance().pushScene(new LevelSelectScene());
                }
            }
        );
    }


    // public void update()
    // {

    // }

    public void render(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Electric Field Hockey", 100, 150);

        g.setColor(Color.WHITE);
        g.fillRect(BUTTON_X, BUTTON_Y, BUTTON_W, BUTTON_H);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Level Select", BUTTON_X + 25, BUTTON_Y + 33);
    }
}
