import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 
 */
public class TitleScene extends Scene
{
    private UIButton[] buttons;

    private static final int BUTTON_WIDTH = (int)(250 * Game.RELATIVE_SCALE);

    public TitleScene()
    {
        buttons = new UIButton[2];

        buttons[0] = new UIButton(
            new Rectangle(
                Game.WIDTH / 2 - BUTTON_WIDTH / 2, Game.HEIGHT / 2 + WorldScene.BUTTON_PADDING,
                BUTTON_WIDTH, WorldScene.BUTTON_HEIGHT
                ),
            "Level Select",
            () -> Game.instance().pushScene(new LevelSelectScene()));
        
        buttons[1] = new UIButton(
            new Rectangle(
                Game.WIDTH / 2 - BUTTON_WIDTH / 2, Game.HEIGHT / 2 + WorldScene.BUTTON_HEIGHT + 2 * WorldScene.BUTTON_PADDING,
                BUTTON_WIDTH, WorldScene.BUTTON_HEIGHT
                ),
            "Sandbox",
            () -> Game.instance().pushScene(new WorldScene(0)));
    }

    @Override
    public void update()
    {
        for (UIButton b : buttons)
            b.update();
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(new Color(20, 24, 40));
        g.fillRect(0, 0, Game.instance().getWidth(), Game.instance().getHeight());

        g.setColor(Color.WHITE);
        g.setFont(Assets.getFont("AvenueX", Font.PLAIN, (int)(130 * Game.RELATIVE_SCALE)));
        DrawUtil.drawText(g, new Vector2(Game.WIDTH / 2, Game.HEIGHT / 2 - g.getFont().getSize()),
            "Electric Field", new Vector2(0.5f, 1));
        DrawUtil.drawText(g, new Vector2(Game.WIDTH / 2, Game.HEIGHT / 2),
            "Hockey", new Vector2(0.5f, 1));

        for (UIButton b : buttons)
            b.render(g);
    }
}
