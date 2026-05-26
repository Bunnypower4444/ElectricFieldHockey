import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 
 */
public class TitleScene extends Scene
{

    private UIButton levelSelectButton;

    public TitleScene()
    {
        Rectangle bounds = new Rectangle(300, 300, 200, 50);
        Runnable action = () -> Game.instance().pushScene(new LevelSelectScene());
        levelSelectButton = new UIButton(bounds, "Level Select", action);
    }

    @Override
    public void update()
    {
        levelSelectButton.update();
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.instance().getWidth(), Game.instance().getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Electric Field Hockey", 100, 150);

        levelSelectButton.render(g);
    }
}
