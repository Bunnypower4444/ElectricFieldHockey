
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
 * The main class that handles running and maintaining the game, i.e., the "engine,"
 * running it within a JPanel.
 * The Game class starts the update/render chain, and stores a stack of
 * Scenes, using Timers to call the appropriate
 * methods every frame. The Game class also provides a system for handling
 * mouse input.
 * 
 * The Game class is a singleton class, meaning there can only be one instance
 * of it, which is accessed in a static way.
 * 
 * @author  Evan Guo
 * @version 5/27/26
 */
public class Game extends JPanel implements ActionListener, MouseListener, KeyListener
{
    // all these private fields are marked as transient to prevent serialization
    // (since JPanel extends something that implements Serializable)
    // (also so that javadoc stops yelling about adding comments for these)
    private transient Stack<Scene> scenes = new Stack<>();
    private transient Timer updateTimer;
    private transient Timer renderTimer;
    private transient long lastFrameTimeMillis;
    private transient float deltaTime = 0;
    private transient Vector2 mousePos = Vector2.zero;
    private transient boolean mouseDown = false;
    private transient boolean mouseClicked = false;
    private transient boolean mousePressed = false;
    private static Game instance;

    /**
     * The update (physics tick) frequency.
     */
    public static final float UpdateFPS = 300;
    /**
     * The graphics refresh rate.
     */
    public static final float FPS = 60;

    /**
     * The height that the Game JPanel should be at.
     */
    public static final int HEIGHT = 720;
    /**
     * The width that the Game JPanel should be at, which is
     * at a constant ratio to the height.
     */
    public static final int WIDTH = (int)(WorldScene.FIELD_HEIGHT * WorldScene.WORLD_WIDTH_HEIGHT_RATIO);
    /**
     * A scale factor used to scale graphics to the given size of the
     * window, so that elements maintain the same relative size when
     * the size of the window changes.
     */
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

    /**
     * Creates and runs the instance of the Game. This method is required
     * to instantiate the Game, since there can only be one instance of the Game.
     * When the Game is created, a new TitleScene object will be pushed to the stack
     * of scenes.
     * @throws IllegalStateException if <code>createGame()</code> has already
     * been run
     */
    public static void createGame()
    {
        if (instance != null)
            throw new IllegalStateException("Game has already been created");

        new Game();
    }

    /**
     * Gets the current instance of the game.
     * @return The current instance of the game.
     * @throws IllegalStateException if the Game has not been created with <code>createGame()</code> yet
     */
    public static Game instance()
    {
        if (Game.instance != null)
            return Game.instance;

        throw new IllegalStateException("Game has not been created with createGame()");
    }

    /**
     * Starts the update and render cycles for the Game.
     */
    public void start()
    {
        updateTimer.start();
        renderTimer.start();
        lastFrameTimeMillis = System.currentTimeMillis();
    }

    /**
     * Pushes a new Scene to the scene stack.
     * @param s the new Scene
     */
    public void pushScene(Scene s)
    {
        scenes.push(s);
    }

    /**
     * Pops the current Scene from the stack of scenes.
     */
    public void popScene()
    {
        if (!scenes.isEmpty())
            scenes.pop();
    }

    /**
     * Pops the current Scene, then pushes the new Scene to the stack
     * of scenes.
     * @param s the new Scene
     */
    public void switchScene(Scene s)
    {
        popScene();
        pushScene(s);
    }

    /**
     * Gets the amount of time between update (physics) ticks.
     * @return The time between physics ticks, in seconds
     */
    public float deltaTime()
    {
        return deltaTime;
    }

    /**
     * Gets the screen coordinates of the mouse.
     * @return A vector representing the position of the mouse
     */
    public Vector2 mousePos()
    {
        return mousePos;
    }

    /**
     * Gets whether or not the mouse is currently pressed down.
     * @return true if the mouse is pressed down; false otherwise
     */
    public boolean mouseDown()
    {
        return mouseDown;
    }

    /**
     * Gets whether or not the mouse button has been clicked (released)
     * in the current update tick. This value is cleared after every update tick.
     * @return true if the mouse button was just released; false otherwise
     */
    public boolean mouseClicked()
    {
        return mouseClicked;
    }

    /**
     * Gets whether or not not the mouse button has been clicked (released)
     * in the current update tick, "using up" the click if true.
     * @return true if the mouse button was just released; false otherwise
     */
    public boolean consumeClick()
    {
        if (mouseClicked)
        {
            mouseClicked = false;
            return true;
        }
        return false;
    }

    /**
     * Gets whether or not the mouse button has been pressed
     * in the current update tick. This value is cleared after every update tick.
     * @return true if the mouse button was just pressed; false otherwise
     */
    public boolean mousePressed()
    {
        return mousePressed;
    }

    /**
     * Gets whether or not not the mouse button has been pressed
     * in the current update tick, "using up" the press if true.
     * @return true if the mouse button was just pressed; false otherwise
     */
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

    @Override
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
        // alt + r
        if (e.isAltDown() && e.getKeyCode() == 82)
        {
            Assets.load();
            System.out.println("Assets reloaded");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}