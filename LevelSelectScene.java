import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The screen that lets the player choose which level to play. Renders a grid
 * of level buttons; clicking one pushes a new WorldScene for that level onto
 * the Game's scene stack.
 *
 * @author  Adeline Krishna
 * @version 5/25/26
 */
public class LevelSelectScene extends Scene
{
    private static final int COLUMNS = 4;
    private static final int BUTTON_SIZE = (int)(120 * Game.RELATIVE_SCALE);
    private static final int BUTTON_GAP = (int)(20 * Game.RELATIVE_SCALE);
    private static final int TOP_MARGIN = (int)(120 * Game.RELATIVE_SCALE);

    private Rectangle[] buttons;
    private UIButton backButton;

    /**
     * Lays out a button for each available level.
     */
    public LevelSelectScene()
    {
        int count = Assets.levelCount() - 1;
        buttons = new Rectangle[count];

        int gridWidth = COLUMNS * BUTTON_SIZE + (COLUMNS - 1) * BUTTON_GAP;
        int startX = (Game.WIDTH - gridWidth) / 2;

        for (int i = 0; i < count; i++)
        {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = startX + col * (BUTTON_SIZE + BUTTON_GAP);
            int y = TOP_MARGIN + row * (BUTTON_SIZE + BUTTON_GAP);
            buttons[i] = new Rectangle(x, y, BUTTON_SIZE, BUTTON_SIZE);
        }

        backButton = new UIButton(new Rectangle(
                (int)(20 * Game.RELATIVE_SCALE),
                (int)(25 * Game.RELATIVE_SCALE),

                WorldScene.BUTTON_WIDTH,
                WorldScene.BUTTON_HEIGHT

            ), "Back",
            Game.instance()::popScene);
    }

    /**
     * Opens the clicked level. Input is polled from Game (the same model the
     * WorldScene and Charge use), since Game only dispatches update()/render().
     */
    @Override
    public void update()
    {
        backButton.update();

        if (!Game.instance().mouseClicked())
            return;
        
        Vector2 mouse = Game.instance().mousePos();
        for (int i = 0; i < buttons.length; i++)
        {
            if (buttons[i].contains((int)mouse.x(), (int)mouse.y()))
            {
                Game.instance().pushScene(new WorldScene(i + 1));
                return;
            }
        }
    }

    /**
     * Render the 2D graphics on the screen.
     *
     * @param g the graphics context to draw with
     */
    @Override
    public void render(Graphics2D g)
    {
        g.setColor(new Color(20, 24, 40));
        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(Assets.getFont("JosefinSans", Font.BOLD | Font.ITALIC, 48));
        String title = "Select a Level";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.WIDTH - titleWidth) / 2, 70);

        g.setFont(Assets.getFont("JosefinSans", Font.BOLD, 36));
        for (int i = 0; i < buttons.length; i++)
        {
            Rectangle b = buttons[i];

            g.setColor(new Color(70, 90, 160));
            g.fillRoundRect(b.x, b.y, b.width, b.height, 16, 16);

            g.setColor(Color.WHITE);
            g.drawRoundRect(b.x, b.y, b.width, b.height, 16, 16);

            String label = String.valueOf(i + 1);
            int lw = g.getFontMetrics().stringWidth(label);
            int lh = g.getFontMetrics().getAscent();
            g.drawString(label, b.x + (b.width - lw) / 2, b.y + (b.height + lh) / 2 - 4);
        }

        backButton.render(g);
    }
}


/*
public class LevelSelectScene extends Scene
{
    private static final int COLUMNS = 4;
    private static final int BUTTON_SIZE = (int)(120 * Game.RELATIVE_SCALE);
    private static final int BUTTON_GAP = (int)(20 * Game.RELATIVE_SCALE);
    private static final int TOP_MARGIN = (int)(120 * Game.RELATIVE_SCALE);

    private UIButton[] buttons;
    // Lays out a button for each available level
    public LevelSelectScene()
    {
        int count = Assets.levelCount() - 1;
        buttons = new UIButton[count];

        int gridWidth = COLUMNS * BUTTON_SIZE + (COLUMNS - 1) * BUTTON_GAP;
        int startX = (Game.WIDTH - gridWidth) / 2;

        for (int i = 0; i < count; i++)
        {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = startX + col * (BUTTON_SIZE + BUTTON_GAP);
            int y = TOP_MARGIN + row * (BUTTON_SIZE + BUTTON_GAP);

            final int level = i + 1;
            buttons[i] = new UIButton(
                new Rectangle(x, y, BUTTON_SIZE, BUTTON_SIZE), level + "",
                () -> Game.instance().pushScene(new WorldScene(level))
                );
        }
    }

    // Opens the clicked level. Input is polled from Game (the same model the
    // WorldScene and Charge use), since Game only dispatches update()/render().
    @Override
    public void update()
    {
        for (UIButton button : buttons)
        {
            button.update();
        }
    }
    // Render the 2D graphics on the screen
    @Override
    public void render(Graphics2D g)
    {
        g.setColor(new Color(20, 24, 40));
        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        String title = "Select a Level";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.WIDTH - titleWidth) / 2, 70);

        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        for (int i = 0; i < buttons.length; i++)
        {
            Rectangle b = buttons[i];

            g.setColor(new Color(70, 90, 160));
            g.fillRoundRect(b.x, b.y, b.width, b.height, 16, 16);

            g.setColor(Color.WHITE);
            g.drawRoundRect(b.x, b.y, b.width, b.height, 16, 16);

            String label = String.valueOf(i + 1);
            int lw = g.getFontMetrics().stringWidth(label);
            int lh = g.getFontMetrics().getAscent();
            g.drawString(label, b.x + (b.width - lw) / 2, b.y + (b.height + lh) / 2 - 4);
        }
    }
}
 */
