import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The opening screen into the game, which allows the player to choose to
 * go to the {@link LevelSelectScene level select screen} or the sandbox level.
 * 
 * @author Aarohi Shah, Evan Guo
 * @version 5/25/26
 */
public class TitleScene extends Scene
{
    private UIButton[] buttons;

    private static final int BUTTON_WIDTH = (int)(250 * Game.RELATIVE_SCALE);

    /**
     * Creates a new TitleScene, instantiating the UIButtons that are present
     * in the scene.
     */
    public TitleScene()
    {
        buttons = new UIButton[3];

        int y = Game.HEIGHT / 2 + WorldScene.BUTTON_PADDING;
        buttons[0] = new UIButton(
            new Rectangle(
                Game.WIDTH / 2 - BUTTON_WIDTH / 2, y,
                BUTTON_WIDTH, WorldScene.BUTTON_HEIGHT
                ),
            "Level Select",
            () -> Game.instance().pushScene(new LevelSelectScene()));
        
        buttons[1] = new UIButton(
            new Rectangle(
                Game.WIDTH / 2 - BUTTON_WIDTH / 2, y += (WorldScene.BUTTON_HEIGHT + WorldScene.BUTTON_PADDING),
                BUTTON_WIDTH, WorldScene.BUTTON_HEIGHT
                ),
            "Sandbox",
            () -> Game.instance().pushScene(new WorldScene(0)));

        buttons[2] = new UIButton(
            new Rectangle(
                Game.WIDTH / 2 - BUTTON_WIDTH / 2, y += (WorldScene.BUTTON_HEIGHT + WorldScene.BUTTON_PADDING),
                BUTTON_WIDTH, WorldScene.BUTTON_HEIGHT
                ),
            "Low Detail Mode: " + (Save.instance().lowDetailMode ? "ON" : "OFF"),
            button ->
            {
                Save.instance().toggleLowDetailMode();
                button.setText("Low Detail Mode: " + (Save.instance().lowDetailMode ? "ON" : "OFF"));
            });
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
        g.setBackground(new Color(20, 24, 40));
        g.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(Assets.getFont("AvenueX", Font.PLAIN, (int)(130 * Game.RELATIVE_SCALE)));

        DrawUtil.drawText(g, new Vector2(Game.WIDTH / 2, Game.HEIGHT / 2 - g.getFont().getSize()),
            "Electric Field", new Vector2(0.5f, 1));
        DrawUtil.drawText(g, new Vector2(Game.WIDTH / 2, Game.HEIGHT / 2),
            "Hockey", new Vector2(0.5f, 1));

        final float PADDING = 10 * Game.RELATIVE_SCALE;


        g.setFont(Assets.getFont("JosefinSans", Font.ITALIC, WorldScene.BUTTON_HEIGHT / 2));

        DrawUtil.drawText(g, new Vector2(0 + PADDING, Game.HEIGHT - PADDING),
            "Version " + Game.VERSION_STRING, new Vector2(0, 1));

        DrawUtil.drawText(g, new Vector2(Game.WIDTH - PADDING, Game.HEIGHT - PADDING),
            "Original AP CSA project by Evan Guo, Adeline Krishna, and Aarohi Shah", new Vector2(1, 1));
        DrawUtil.drawText(g, new Vector2(Game.WIDTH - PADDING, Game.HEIGHT - PADDING - g.getFont().getSize()),
            "By Evan Guo", new Vector2(1, 1));

        for (UIButton b : buttons)
            b.render(g);
    }
}
