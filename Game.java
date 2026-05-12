
import java.util.Stack;

import javax.swing.Timer;
import javax.swing.JPanel;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 */
public class Game extends JPanel implements ActionListener
{
    private Stack<Scene> scenes;
    private Timer timer;
    private long lastFrameTimeMillis;
    private float deltaTime;
    private static Game instance;

    private static final float FPS = 60;

    private Game()
    {
        scenes = new Stack<>();
        timer = new Timer((int)(1000 / FPS), this);

        pushScene(new TitleScene());

        timer.start();
        lastFrameTimeMillis = System.currentTimeMillis();
        deltaTime = 0;
    }

    public static void createGame()
    {
        if (instance == null)
            throw new IllegalStateException("Game has already been created");

        instance = new Game();
    }

    public static Game instance()
    {
        if (Game.instance != null)
            return Game.instance;

        throw new IllegalStateException("Game has not been created with createGame()");
    }

    public void pushScene(Scene s)
    {
        scenes.push(s);
    }

    public void popScene()
    {
        if (!scenes.isEmpty())
            scenes.pop();
    }

    public void switchScene(Scene s)
    {
        popScene();
        pushScene(s);
    }

    public float deltaTime()
    {
        return deltaTime;
    }

    public void actionPerformed(ActionEvent e)
    {
        long currentTimeMillis = System.currentTimeMillis();
        deltaTime = (currentTimeMillis - lastFrameTimeMillis) / 1000f;
        
        getCurrentScene().update();

        lastFrameTimeMillis = currentTimeMillis;
    }

    public void paintComponent(Graphics2D g)
    {
        getCurrentScene().render(g);
    }

    private Scene getCurrentScene()
    {
        if (!scenes.isEmpty())
            return scenes.peek();

        pushScene(new TitleScene());
        return scenes.peek();
    }
}