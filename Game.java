
import java.util.Stack;

import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Graphics;
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
    private Vector2 mousePos = Vector2.zero;
    private boolean mouseDown = false;
    private boolean mouseClicked = false;
    private boolean mousePressed = false;
    private static Game instance;

    private static final float FPS = 60;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final float RELATIVE_SCALE = HEIGHT / 900f;

    private Game()
    {
        instance = this;
        timer = new Timer((int)(1000 / FPS), this);

        addMouseListener(this);

        timer.start();
        lastFrameTimeMillis = System.currentTimeMillis();

        pushScene(new TitleScene());
    }

    public static void createGame()
    {
        if (instance != null)
            throw new IllegalStateException("Game has already been created");

        new Game();
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

    private Vector2 getRelativeMousePosition()
    {
        Vector2 pos = new Vector2(MouseInfo.getPointerInfo().getLocation());
        Container c = this;
        while (true)
        {
            pos = pos.sub(new Vector2(c.getLocation()));
            if (c instanceof ElectricFieldHockey)
                break;
            c = c.getParent();
        }

        return pos;
    }

    public void actionPerformed(ActionEvent e)
    {
        long currentTimeMillis = System.currentTimeMillis();
        deltaTime = (currentTimeMillis - lastFrameTimeMillis) / 1000f;

        mousePos = getRelativeMousePosition();

        boolean pMouseClicked = mouseClicked;
        boolean pMousePressed = mousePressed;
        
        getCurrentScene().update();

        if (pMouseClicked)
            mouseClicked = false;
        if (pMousePressed)
            mousePressed = false;

        lastFrameTimeMillis = currentTimeMillis;

        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        getCurrentScene().render((Graphics2D)g);
    }

    private Scene getCurrentScene()
    {
        if (!scenes.isEmpty())
            return scenes.peek();

        pushScene(new TitleScene());
        return scenes.peek();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

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
        mouseClicked = true;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}