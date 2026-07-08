
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;

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

    private transient long lastFrameNanoTime;
    private transient float deltaTime = 0;
    private transient LinkedList<Long> updateNanoTimeHistory = new LinkedList<>();
    private transient long lastRenderFrameNanoTime;
    private transient float renderDeltaTime = 0;
    private transient LinkedList<Long> renderNanoTimeHistory = new LinkedList<>();

    private transient Vector2 mousePos = Vector2.zero;
    private transient boolean mouseDown = false;
    private transient boolean mouseClicked = false;
    private transient boolean mousePressed = false;

    private transient boolean shiftDown = false;

    private static Game instance;

    /**
     * A string containing the version of the application.
     */
    public static final String VERSION_STRING = "1.0.1";
    
    // TODO maybe have a separate update (runs at same freq as render) and physics tick system?
    // should also update all the (int) casts to round the numbers
    /**
     * The default update (physics tick) frequency.
     */
    public static final float DEFAULT_UPDATE_FPS = 300;
    /**
     * The default graphics refresh rate.
     */
    public static final float DEFAULT_RENDER_FPS = 60;

    /**
     * The update (physics tick) frequency.
     */
    public transient float updateFPS;
    /**
     * The graphics refresh rate.
     */
    public transient float renderFPS;

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

    /**
     * The transformation applied when the screen is rendered, which accounts
     * for the screen's pixel density.
     */
    public static final AffineTransform SCREEN_TRANSFORM = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .getDefaultConfiguration()
        .getDefaultTransform();

    private Game(float updateFPS, float renderFPS)
    {
        this.updateFPS = updateFPS;
        this.renderFPS = renderFPS;
        instance = this;
        updateTimer = new Timer((int)(1000 / updateFPS), this);
        renderTimer = new Timer((int)(1000 / renderFPS), this);

        addMouseListener(this);
        addKeyListener(this);

        setFocusable(true);

        pushScene(new TitleScene());
    }

    /**
     * Creates and runs the instance of the Game. This method is required
     * to instantiate the Game, since there can only be one instance of the Game.
     * When the Game is created, a new TitleScene object will be pushed to the stack
     * of scenes. The new Game will run with the specified update and render frequencies.
     * @param updateFPS the update tick frequency
     * @param renderFPS the graphics refresh rate
     * @throws IllegalStateException if <code>createGame()</code> has already
     * been run
     */
    public static void createGame(float updateFPS, float renderFPS)
    {
        if (instance != null)
            throw new IllegalStateException("Game has already been created");

        new Game(updateFPS, renderFPS);
    }

    /**
     * Creates and runs the instance of the Game. This method is required
     * to instantiate the Game, since there can only be one instance of the Game.
     * When the Game is created, a new TitleScene object will be pushed to the stack
     * of scenes. The new Game will run with the specified update frequencies and
     * the default render FPS, given by {@link Game#DEFAULT_RENDER_FPS}.
     * @param updateFPS the update tick frequency
     * @throws IllegalStateException if <code>createGame()</code> has already
     * been run
     */
    public static void createGame(float updateFPS)
    {
        createGame(updateFPS, DEFAULT_RENDER_FPS);
    }

    /**
     * Creates and runs the instance of the Game. This method is required
     * to instantiate the Game, since there can only be one instance of the Game.
     * When the Game is created, a new TitleScene object will be pushed to the stack
     * of scenes. The new Game will run with the the default update and render frequencies,
     * given by {@link Game#DEFAULT_UPDATE_FPS} and {@link Game#DEFAULT_RENDER_FPS}, respectively.
     * @throws IllegalStateException if <code>createGame()</code> has already
     * been run
     */
    public static void createGame()
    {
        createGame(DEFAULT_UPDATE_FPS, DEFAULT_RENDER_FPS);
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
        lastFrameNanoTime = System.currentTimeMillis();
        lastRenderFrameNanoTime = System.currentTimeMillis();
    }

    /**
     * Pushes a new Scene to the scene stack.
     * @param s the new Scene
     */
    public void pushScene(Scene s)
    {
        scenes.push(s);
        s.update();
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

    private Scene getCurrentScene()
    {
        if (!scenes.isEmpty())
            return scenes.peek();

        pushScene(new TitleScene());
        return scenes.peek();
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
     * Gets the amount of time between render ticks/frames.
     * @return The time between render ticks, in seconds
     */
    public float renderDeltaTime()
    {
        return renderDeltaTime;
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
            System.err.print("ERROR: ");
            exception.printStackTrace();
            updateTimer.stop();
            renderTimer.stop();
        }
    }

    private void update()
    {
        long currentTimeNano = System.nanoTime();
        deltaTime = (currentTimeNano - lastFrameNanoTime) / 1e9f;

        mousePos = getRelativeMousePosition();

        boolean pMouseClicked = mouseClicked;
        boolean pMousePressed = mousePressed;
        
        getCurrentScene().update();

        if (pMouseClicked)
            mouseClicked = false;
        if (pMousePressed)
            mousePressed = false;

        lastFrameNanoTime = currentTimeNano;

        updateNanoTimeHistory.add(System.nanoTime() - lastFrameNanoTime);

        if (updateNanoTimeHistory.size() > 60)
            updateNanoTimeHistory.removeFirst();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        try
        {
            long currentTimeNano = System.nanoTime();
            renderDeltaTime = (currentTimeNano - lastRenderFrameNanoTime) / 1e9f;

            super.paintComponent(g2d);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            getCurrentScene().render(g2d);

            // debug FPS
            if (WorldScene.debug)
            {
                final int FPS_INFO_WIDTH = (int)(350 * RELATIVE_SCALE + 0.5f);

                g2d.setColor(new Color(0, 0, 0, 128));
                g2d.fillRect(WIDTH - FPS_INFO_WIDTH, 0, FPS_INFO_WIDTH, 4 * g2d.getFont().getSize());

                g2d.setColor(Color.CYAN);
                g2d.setFont(new Font("Monospace", Font.PLAIN, WorldScene.BUTTON_HEIGHT / 2));

                int line = 0;
                DrawUtil.drawText(g2d, new Vector2(WIDTH, (line++) * g2d.getFont().getSize()),
                    "Update FPS: " + (int)(1 / deltaTime() + 0.5) /* String.format("%.2f", (1 / deltaTime())) */,
                    new Vector2(1, 0));
                DrawUtil.drawText(g2d, new Vector2(WIDTH, (line++) * g2d.getFont().getSize()),
                    "Render FPS: " + (int)(1 / renderDeltaTime() + 0.5) /* String.format("%.2f", (1 / renderDeltaTime())) */,
                    new Vector2(1, 0));

                long avgUpdateNanoTime = 0, avgRenderNanoTime = 0;
                
                for (Long l : updateNanoTimeHistory)
                    avgUpdateNanoTime += l;
                if (avgUpdateNanoTime > 0)
                    avgUpdateNanoTime /= updateNanoTimeHistory.size();

                for (Long l : renderNanoTimeHistory)
                    avgRenderNanoTime += l;
                if (avgRenderNanoTime > 0)
                    avgRenderNanoTime /= renderNanoTimeHistory.size();

                DrawUtil.drawText(g2d, new Vector2(WIDTH, (line++) * g2d.getFont().getSize()),
                    "Avg. Update Time: " + avgUpdateNanoTime / 1e6 + "ms" /* String.format("%.2f", (1 / deltaTime())) */,
                    new Vector2(1, 0));
                DrawUtil.drawText(g2d, new Vector2(WIDTH, (line++) * g2d.getFont().getSize()),
                    "Avg. Render Time " + avgRenderNanoTime / 1e6 + "ms" /* String.format("%.2f", (1 / renderDeltaTime())) */,
                    new Vector2(1, 0));
            }

            lastRenderFrameNanoTime = currentTimeNano;

            renderNanoTimeHistory.add(System.nanoTime() - lastFrameNanoTime);
            
            if (renderNanoTimeHistory.size() > 15)
                renderNanoTimeHistory.removeFirst();
        }
        catch (Exception exception)
        {
            System.err.print("ERROR: ");
            exception.printStackTrace();
            updateTimer.stop();
            renderTimer.stop();
        }
    }

    //#region Input

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

    /**
     * Gets if the shift key is currently being held down.
     * @return true if the shift key is currently held down; false otherwise
     */
    public boolean shiftDown()
    {
        return shiftDown;
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
        // alt + r (reload assets)
        if (e.isAltDown() && e.getKeyCode() == 82)
        {
            try
            {
                Assets.load();
                System.out.println("Assets reloaded");
            }
            catch (Exception err)
            {
                System.err.println("Unable to reload assets");
                System.err.println(err);
            }
        }

        // alt + d (toggle debug mode)
        else if (e.isAltDown() && e.getKeyCode() == 68)
        {
            WorldScene.debug = !WorldScene.debug;
        }

        // alt + s (print out charge positions)
        else if (e.isAltDown() && e.getKeyCode() == 83 && getCurrentScene() instanceof WorldScene)
        {
            WorldScene world = (WorldScene)getCurrentScene();
            for (Charge c : world.getActorsOfType(Charge.class))
            {
                if (!c.isFixed())
                {
                    Vector2 pos = c.getPosition();
                    System.out.println("!solutionpos=" + pos.x() + ", " + pos.y());
                }
            }
            System.out.println();
        }

        // shift
        else if (e.getKeyCode() == 16)
        {
            shiftDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        // shift
        if (e.getKeyCode() == 16)
        {
            shiftDown = false;
        }
    }

    //#endregion

}