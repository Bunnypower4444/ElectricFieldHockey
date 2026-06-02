
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The Scene of the main world, which handles UI elements of the
 * simulation, running and displaying the Actors that make up the world, and
 * handling winning and losing.
 * 
 * @author  Evan Guo
 * @version 5/26/26
 */
public class WorldScene extends Scene
{
    protected static boolean debug = false;

    private static final int TOOLBAR_HEIGHT = (int)(80 * Game.RELATIVE_SCALE);
    
    /**
     * The color of the background of the playing field (not including the
     * toolbar at the bottom)
     */
    public static final Color FIELD_COLOR = new Color(0xEEEEEE);
    /**
     * The screen width of the playing field
     */
    public static final int FIELD_WIDTH = Game.WIDTH;
    /**
     * The screen height of the playing field (not including the
     * toolbar at the bottom)
     */
    public static final int FIELD_HEIGHT = Game.HEIGHT - TOOLBAR_HEIGHT;

    /**
     * The ratio of the world's width to its height.
     */
    public static final float WORLD_WIDTH_HEIGHT_RATIO = 1.250f;
    /**
     * A Vector2 containing the world (not screen) width and height, in meters of the
     * playing field. World coordinates are used to calculate physics-based interactions,
     * with the positive y-axis being up, while screen coordinates are used for rendering
     * graphics, with the positive y-axis being down. Using a separate system for
     * world coordinates allows the modification of the display size without affecting
     * physics calculations, as well as allowing the actors to exist in a world
     * where the positive y-axis is up, which preserves the right-hand rule for cross
     * products.
     */
    public static final Vector2 WORLD_DIMENSIONS = new Vector2(1000 * WORLD_WIDTH_HEIGHT_RATIO, 1000);
    
    private static final Vector2 SCALE_FACTOR = new Vector2(FIELD_WIDTH, FIELD_HEIGHT).div(WORLD_DIMENSIONS);

    private ArrayList<Actor> actors = new ArrayList<>();
    private HashSet<Actor> actorSet = new HashSet<>();
    private HashMap<Class<?>, LinkedList<Actor>> trackedActors = new HashMap<>();
    private Queue<Actor> addingActors = new LinkedList<>(), removingActors = new LinkedList<>();
    
    private float globalAnimTimer = 0;

    private static enum GameState { Initial, Paused, Unpaused, Failed, Won };
    
    private GameState gameState = GameState.Initial;

    private LinkedList<UIButton> buttons = new LinkedList<>();
    private UIButton playButton, resetButton, clearButton, nextLevelButton;
    
    private int attempts = 0;
    private int charges = 0;
    private int levelNum;
    private Level level;

    private int statsXPos = TOOLBAR_MARGIN;


    private static final int TOOLBAR_MARGIN = (int)(100 * Game.RELATIVE_SCALE);

    /**
     * The height of a typical button.
     */
    public static final int BUTTON_HEIGHT = TOOLBAR_HEIGHT / 2;
    /**
     * The width of a typical button.
     */
    public static final int BUTTON_WIDTH = (int)(100 * Game.RELATIVE_SCALE);
    /**
     * The distance between the edges of buttons that are next to each other.
     */
    public static final int BUTTON_PADDING = (int)(25 * Game.RELATIVE_SCALE);

    /**
     * Marks whether or not the there has been a change to the electric
     * field. This value is reset to false at the end of every update tick,
     * and is used by Actors that need to be up-to-date with the current
     * state of the electric field.
     */
    public boolean eFieldUpdated = false;
    /**
     * Marks whether or not the there has been a change to the magnetic
     * field. This value is reset to false at the end of every update tick,
     * and is used by Actors that need to be up-to-date with the current
     * state of the magnetic field.
     */
    public boolean bFieldUpdated = false;

    /**
     * Creates a new WorldScene using the specified level number.
     * @param levelNum The level number, used to get the corresponding level
     * from the {@link Assets} class.
     */
    public WorldScene(int levelNum)
    {
        this.levelNum = levelNum;

        // play
        playButton = addToolbarButton("Play", this::togglePaused);

        // reset
        resetButton = addToolbarButton("Reset", this::reset);
        resetButton.setEnabled(false);

        // clear
        clearButton = addToolbarButton("Clear", this::clearCharges);

        // back
        buttons.add(new UIButton(new Rectangle(
                (int)(20 * Game.RELATIVE_SCALE),
                (int)(25 * Game.RELATIVE_SCALE),

                BUTTON_WIDTH,
                BUTTON_HEIGHT

            ), "Back",
            Game.instance()::popScene));

        // next level
        nextLevelButton = new UIButton(new Rectangle(
                Game.WIDTH / 2 - (int)(50 * Game.RELATIVE_SCALE),
                Game.HEIGHT / 2 + BUTTON_HEIGHT / 2,

                BUTTON_WIDTH,
                BUTTON_HEIGHT

            ), "Next",
            this::nextLevel);
        nextLevelButton.setVisible(false);
        buttons.add(nextLevelButton);

        level = Assets.getLevel(levelNum);
        level.loadLevel(this);

        addActor(new ChargeBag(
            new Rectangle(
                Game.WIDTH - (int)(175 * Game.RELATIVE_SCALE),
                (int)(25 * Game.RELATIVE_SCALE),
                (int)(150 * Game.RELATIVE_SCALE),
                (int)(75 * Game.RELATIVE_SCALE)
            ), level.getChargeLimit(), level.getPositiveLimit(), level.getNegativeLimit()));

        // testing stuff

        /* addActor(new Wire(1, new Vector2(100, 10), new Vector2(200, 410)));
        addActor(new Wire(1, new Vector2(400, 10), new Vector2(200, 410)));
        addActor(new Wire(1, new Vector2(100, 10), new Vector2(700, 410)));
        addActor(new Wire(1, new Vector2(100, 10), new Vector2(100, 410)));
        addActor(new Wire(1, new Vector2(200, 10), new Vector2(200, 410)));
        addActor(new Wire(1, new Vector2(100, 410), new Vector2(200, 410))); */

        /* Wire w;
        addActor(w = new Wire(10000000000f, new Vector2(100, 410), new Vector2(600, 600)));

        addActor(new Switch(w, new Vector2(150, 750), 50));

        addActor(new Puck(Charge.ELEMENTARY_CHARGE, new Vector2(150f, 500f), Vector2.zero));

        addActor(new Wall(new Rectangle(615, 350, 20, 300)));
        addActor(new Goal(new Rectangle(975, 450, 50, 100), Goal.Orientation.Left));

        addActor(new UniformEField(new Rectangle(500, 475, 250, 175), new Vector2(-100000, -100000)));
        
        addActor(new UniformBField(new Rectangle(500, 350, 250, 175), new Vector3(0, 0, -100)));

        */

        addActor(new TotalEField());
        addActor(new TotalBField());

        eFieldUpdated = true;
        bFieldUpdated = true;
        processActorChanges();
    }

    private UIButton addToolbarButton(String name, Runnable action)
    {
        UIButton button = new UIButton(new Rectangle(
                statsXPos,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2 - BUTTON_HEIGHT / 2,

                BUTTON_WIDTH,
                BUTTON_HEIGHT

            ), name,
            action);
        
        buttons.add(button);
        statsXPos += BUTTON_WIDTH + BUTTON_PADDING;

        return button;
    }

    @Override
    public void update()
    {
        if (!getPaused())
            globalAnimTimer += Game.instance().deltaTime();

        for (UIButton b : buttons)
            b.update();

        for (Actor a : actors)
            a.update();

        processActorChanges();

        for (LateUpdate a : getActorsOfType(LateUpdate.class))
            a.lateUpdate();

        eFieldUpdated = false;
        bFieldUpdated = false;

        processActorChanges();
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setBackground(FIELD_COLOR);
        g.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

        for (Actor a : actors)
            a.render(g);

        //#region UI

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, Game.HEIGHT - TOOLBAR_HEIGHT, Game.WIDTH, TOOLBAR_HEIGHT);

        for (UIButton b : buttons)
            b.render(g);

        g.setColor(Color.BLACK);
        g.setFont(Assets.getFont("JosefinSans", Font.ITALIC, BUTTON_HEIGHT / 2));
        DrawUtil.drawText(g, new Vector2(
                statsXPos,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
            "Charges: " + charges, new Vector2(0, 1));
        DrawUtil.drawText(g, new Vector2(
                statsXPos,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
            "Attempts: " + attempts, new Vector2(0, 0));

        // level has name and is not sandbox
        if (level.getName() != null && levelNum != 0)
        {
            DrawUtil.drawText(g, new Vector2(
                Game.WIDTH - TOOLBAR_MARGIN,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
                "Level " + levelNum, new Vector2(1, 1f));
            DrawUtil.drawText(g, new Vector2(
                Game.WIDTH - TOOLBAR_MARGIN,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
                level.getName(), new Vector2(1, 0));
        }
        // sandbox
        else if (levelNum == 0)
        {
            DrawUtil.drawText(g, new Vector2(
                Game.WIDTH - TOOLBAR_MARGIN,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
                level.getName(), new Vector2(1, 0.5f));
        }
        // level has no name
        else
        {
            DrawUtil.drawText(g, new Vector2(
                Game.WIDTH - TOOLBAR_MARGIN,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
                "Level " + levelNum, new Vector2(1, 0.5f));
        }

        if (gameState == GameState.Failed || gameState == GameState.Won)
        {
            String text = gameState == GameState.Failed ? "Collision!" : "Goal!";
            Color textColor = gameState == GameState.Failed ? Color.RED : new Color(44, 222, 92);

            g.setFont(Assets.getFont("AvenueX", Font.PLAIN, (int)(130 * Game.RELATIVE_SCALE)));
            g.setColor(textColor);

            DrawUtil.drawText(g, new Vector2(Game.WIDTH / 2, Game.HEIGHT / 2), text, new Vector2(0.5f, 1));
        }

        //#endregion

        //#region Debug

        if (!debug)
            return;

        g.setColor(Color.BLACK);
        g.setFont(Assets.getFont("JosefinSans", Font.ITALIC, BUTTON_HEIGHT / 2));

        int line = 0;
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "Screen: " + Game.instance().mousePos(),
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "World: " + screenToWorldPoint(Game.instance().mousePos()),
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "Position: " + getActorsOfType(Puck.class).get(0).getPosition(),
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "Velocity: " + getActorsOfType(Puck.class).get(0).getVelocity(),
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "Acceleration: " + getActorsOfType(Puck.class).get(0).getAcceleration(),
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "t: " + globalAnimTimer,
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "Update FPS: " + String.format("%.2f", (1 / Game.instance().deltaTime())),
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "Render FPS: " + String.format("%.2f", (1 / Game.instance().renderDeltaTime())),
            new Vector2(0, 0));

        //#endregion
    }

    /**
     * A global timer that counts up as long as the simulation is not paused.
     * This value will also count up when the simulation has not started
     * yet, allowing for animations to play out as the player places charges.
     * @return The current value of the timer
     */
    public float globalAnimTimer()
    {
        return globalAnimTimer;
    }

    /**
     * Gets the duration of the time step between update ticks.
     * If <code>getPaused()</code> is true, the value returned will be 0.
     * Otherwise, the value returned will always be fixed to equal
     * <code>1 / Game.UpdateFPS</code>, so that the physics always runs the same
     * regardless of lag.
     * @return The duration of the time step, in seconds, or 0 if the
     * simulation is paused
     */
    public float deltaTime()
    {
        return getPaused() ? 0 : 1 / Game.UpdateFPS;
    }

    /**
     * Queues an actor to be added to the world. Actors that have
     * been queued will be added after the main update, as well
     * as after the late update.
     * @param actor The actor to be added
     * @throws IllegalStateException if the actor is already added to the world
     */
    public void addActor(Actor actor)
    {
        if (actorSet.contains(actor) || addingActors.contains(actor))
            throw new IllegalStateException("Actor is already added to the WorldScene");

        addingActors.add(actor);
        actor.setWorld(this);
    }

    private void processAddingActors()
    {
        while (!addingActors.isEmpty())
        {
            Actor actor = addingActors.remove();

            if (actor instanceof Charge && !((Charge)actor).isFixed())
                charges++;

            int index = Collections.binarySearch(actors, actor, (a, b) -> a.getZIndex() - b.getZIndex());
            if (index < 0)
                index = -(index + 1);

            actors.add(index, actor);
            actorSet.add(actor);

            for (Class<?> type : trackedActors.keySet())
            {
                if (type.isAssignableFrom(actor.getClass()))
                    trackedActors.get(type).add(actor);
            }
        }
    }

    /**
     * Queues an actor to be removed from the world. Actors that have
     * been queued will be removed after the main update, as well
     * as after the late update.
     * @param actor The actor to be removed
     */
    public void removeActor(Actor actor)
    {
        removingActors.add(actor);
    }

    private void processRemovingActors()
    {
        while (!removingActors.isEmpty())
        {
            Actor actor = removingActors.remove();

            if (actors.remove(actor) && actor instanceof Charge && !((Charge)actor).isFixed())
                charges--;

            actorSet.remove(actor);

            actor.setWorld(null);

            for (Class<?> type : trackedActors.keySet())
            {
                if (type.isAssignableFrom(actor.getClass()))
                    trackedActors.get(type).remove(actor);
            }
        }
    }

    private void processActorChanges()
    {
        processAddingActors();
        processRemovingActors();
    }

    /**
     * Gets all the actors that are assignable to a certain type.
     * Once this method has been called for a certain type <code>T</code>,
     * a list of all the actors assignable to that type will be maintained
     * in a HashMap for quick future access.
     * @param <T> The type of actor to get
     * @param c A Class object representing the type of actor get
     * @return A list of all actors assignable to the specified type
     */
    public <T> LinkedList<T> getActorsOfType(Class<T> c)
    {
        if (trackedActors.keySet().contains(c))
            return castActorList(trackedActors.get(c));

        LinkedList<Actor> actorsOfType = new LinkedList<>();
        for (Actor a : actors)
        {
            if (c.isAssignableFrom(a.getClass()))
                actorsOfType.add(a);
        }

        trackedActors.put(c, actorsOfType);

        return castActorList(actorsOfType);
    }

    @SuppressWarnings("unchecked")
    private <T> LinkedList<T> castActorList(LinkedList<Actor> actors)
    {
        LinkedList<T> casted = new LinkedList<T>();
        for (Actor a : actors)
        {
            casted.add((T)a);
        }

        return casted;
    }

    private void setState(GameState state)
    {
        if (state == gameState)
            return;

        if (gameState == GameState.Initial)
            globalAnimTimer = 0;

        gameState = state;
        
        if (gameState != GameState.Won)
            nextLevelButton.setVisible(false);

        if (!gameStarted())
        {
            resetButton.setEnabled(false);
            clearButton.setEnabled(true);
        }
        else
        {
            resetButton.setEnabled(true);
            clearButton.setEnabled(false);
        }

        if (gameState == GameState.Won || gameState == GameState.Failed)
            playButton.setEnabled(false);
        else
            playButton.setEnabled(true);

        if (gameState == GameState.Unpaused)
            playButton.setText("Pause");
        else
            playButton.setText("Play");
    }

    /**
     * Gets whether the game is in a paused state, which occurs when
     * the pause button is clicked, or if the level is completed or failed.
     * This does not include the initial state of the level during which
     * the player can place charges.
     * @return true if the game is paused; false otherwise
     */
    public boolean getPaused()
    {
        return gameState == GameState.Paused
            || gameState == GameState.Failed || gameState == GameState.Won;
    }

    /**
     * Toggles between pausing and unpausing the game.
     */
    public void togglePaused()
    {
        if (getPaused() || !gameStarted())
            setPaused(false);
        else
            setPaused(true);
    }

    /**
     * Pauses or unpauses the game. This method will have no effect
     * if the game is currently in the win or lose state.
     * @param paused true if the game should be paused; false otherwise
     */
    public void setPaused(boolean paused)
    {
        if (gameState != GameState.Failed && gameState != GameState.Won)
            setState(paused ? GameState.Paused : GameState.Unpaused);
    }

    /**
     * Gets whether or not the game is in the initial charge-placing state.
     * @return true if the game is in the initial state; false otherwise
     */
    public boolean gameStarted()
    {
        return gameState != GameState.Initial;
    }

    /**
     * Resets the game, increasing the attempt counter by one.
     */
    public void reset()
    {
        globalAnimTimer = 0;
        setState(GameState.Initial);
        attempts++;

        for (RequireReset p : getActorsOfType(RequireReset.class))
        {
            p.reset();
        }
    }

    /**
     * Removes all player-placed charges from the world and notifies
     * the ChargeBag accordingly.
     */
    public void clearCharges()
    {
        if (gameStarted())
            return;

        ChargeBag bag = getActorsOfType(ChargeBag.class).get(0);
        for (Charge c : getActorsOfType(Charge.class))
        {
            if (!c.isFixed())
            {
                removeActor(c);
                bag.chargeRemoved(c);
            }
        }
    }

    /**
     * If the game is currently not in the level failed state, sets
     * the state to the level completed state. If the level is not
     * the sandbox level (level 0) and there is a next level,
     * shows the button to continue to the next level.
     */
    public void levelComplete()
    {
        if (gameState != GameState.Failed)
        {
            setState(GameState.Won);

            // only show option for next if it is not sandbox and if next level exists
            if (levelNum != 0 && Assets.getLevel(levelNum + 1) != null)
                nextLevelButton.setVisible(true);
        }
    }

    private void nextLevel()
    {
        Game.instance().switchScene(new WorldScene(levelNum + 1));
    }

    /**
     * If the game is currently not in the level completed state, sets
     * the state to the level failed state.
     */
    public void levelFail()
    {
        if (gameState != GameState.Won)
            setState(GameState.Failed);
    }

    /**
     * Converts the given world-space position to its corresponding
     * point in screen-space coordinates. This has the effect of
     * multiplying the point by a scale factor, inverting the y-position,
     * and adjusting the point so that the origin of the world space is in the correct spot.
     * @param point The world-space position of the point, in meters
     * @return The screen-space position of the point 
     */
    public static Vector2 worldToScreenPoint(Vector2 point)
    {
        return point.withY(WORLD_DIMENSIONS.y() - point.y()).mult(SCALE_FACTOR);
    }

    /**
     * Converts the given world-space vector, representing
     * a change in value rather than an absolute value, to its corresponding
     * point in screen-space coordinates. This has the effect of
     * multiplying the vector by a scale factor and inverting
     * the y-component without adjusting it so
     * that the origin of the world space is in the correct spot.
     * @param vector The world-space vector
     * @return The screen-space vector 
     */
    public static Vector2 worldToScreenVector(Vector2 vector)
    {
        return vector.withY(-vector.y()).mult(SCALE_FACTOR);
    }

    /**
     * Converts the given screen-space position to its corresponding
     * point in world-space coordinates. This has the effect of
     * multiplying the point by a scale factor, inverting the y-position,
     * and adjusting the point so that the origin of the screen space is in the correct spot.
     * @param point The screen-space position of the point
     * @return The world-space position of the point, in meters
     */
    public static Vector2 screenToWorldPoint(Vector2 point)
    {
        return point.withY(FIELD_HEIGHT - point.y()).div(SCALE_FACTOR);
    }

    /**
     * Converts the given screen-space vector, representing
     * a change in value rather than an absolute value, to its corresponding
     * point in world-space coordinates. This has the effect of
     * multiplying the vector by a scale factor and inverting
     * the y-component without adjusting it so
     * that the origin of the screen space is in the correct spot.
     * @param vector The screen-space vector
     * @return The world-space vector 
     */
    public static Vector2 screenToWorldVector(Vector2 vector)
    {
        return vector.withY(- vector.y()).div(SCALE_FACTOR);
    }
}