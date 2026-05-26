
import java.util.Stack;

import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 
 */
public class Game extends JPanel implements ActionListener, MouseListener, KeyListener
{
    private Stack<Scene> scenes = new Stack<>();
    private Timer updateTimer;
    private Timer renderTimer;
    private long lastFrameTimeMillis;
    private float deltaTime = 0;
    private Vector2 mousePos = Vector2.zero;
    private boolean mouseDown = false;
    private boolean mouseClicked = false;
    private boolean mousePressed = false;
    private static Game instance;

    public static final float UpdateFPS = 300;
    public static final float FPS = 60;

    public static final int HEIGHT = 720;
    public static final int WIDTH = (int)(WorldScene.FIELD_HEIGHT * WorldScene.WORLD_WIDTH_HEIGHT_RATIO);
    public static final float RELATIVE_SCALE = HEIGHT / 900f;

    private Game()
    {
        instance = this;
        updateTimer = new Timer((int)(1000 / UpdateFPS), this);
        renderTimer = new Timer((int)(1000 / FPS), this);

        addMouseListener(this);
        addKeyListener(this);

        setFocusable(true);

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

    public void start()
    {
        updateTimer.start();
        renderTimer.start();
        lastFrameTimeMillis = System.currentTimeMillis();
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

    public boolean consumeClick()
    {
        if (mouseClicked)
        {
            mouseClicked = false;
            return true;
        }
        return false;
    }

    public boolean mousePressed()
    {
        return mousePressed;
    }

    public boolean consumePress()
    {
        if (mousePressed)
        {
            mousePressed = false;
            return true;
        }
        return false;
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
        Timer source = (Timer)e.getSource();

        try
        {
            if (source == updateTimer)
                update();
            else if (source == renderTimer)
                repaint();
        }
        catch (Exception exception)
        {
            System.err.println("ERROR: " + exception);
            updateTimer.stop();
            renderTimer.stop();
        }
    }

    private void update()
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

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.isAltDown() && e.getKeyChar() == 'r')
            Assets.load();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}