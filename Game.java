
import java.util.Stack;

import javax.swing.Timer;
import javax.swing.JPanel;

import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 
 */
public class Game extends JPanel implements ActionListener, MouseListener
{
    private Stack<Scene> scenes = new Stack<>();
    private Timer timer;
    private long lastFrameTimeMillis;
    private float deltaTime = 0;
    private Vector2 mousePos = Vector2.zero();
    private boolean mouseDown = false;
    private boolean mouseClicked = false;
    private boolean mousePressed = false;
    private static Game instance;

    private static final float FPS = 60;

    private Game()
    {
        timer = new Timer((int)(1000 / FPS), this);

        pushScene(new TitleScene());

        timer.start();
        lastFrameTimeMillis = System.currentTimeMillis();
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

    public Vector2 mousePos()
    {
        return mousePos;
    }

    public boolean mouseDown()
    {
        return mouseDown;
    }

    public boolean mouseClicked()
    {
        return mouseClicked;
    }

    public boolean mousePressed()
    {
        return mousePressed;
    }

    public void actionPerformed(ActionEvent e)
    {
        long currentTimeMillis = System.currentTimeMillis();
        deltaTime = (currentTimeMillis - lastFrameTimeMillis) / 1000f;

        mousePos = new Vector2(MouseInfo.getPointerInfo().getLocation());
        
        getCurrentScene().update();

        mouseClicked = false;
        mousePressed = false;

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

    @Override
    public void mouseClicked(MouseEvent e)
    {
        mouseClicked = true;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        mouseDown = true;
        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}