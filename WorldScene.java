
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 */
public class WorldScene extends Scene
{
    private static final int TOOLBAR_HEIGHT = (int)(80 * Game.RELATIVE_SCALE);
    
    public static final Color FIELD_COLOR = new Color(0xEEEEEE);
    public static final int FIELD_WIDTH = Game.WIDTH;
    public static final int FIELD_HEIGHT = Game.HEIGHT - TOOLBAR_HEIGHT;

    public static final float WORLD_WIDTH_HEIGHT_RATIO = 1.250f;
    public static final Vector2 WORLD_DIMENSIONS = new Vector2(1000 * WORLD_WIDTH_HEIGHT_RATIO, 1000);
    private static final Vector2 SCALE_FACTOR = new Vector2(FIELD_WIDTH, FIELD_HEIGHT).div(WORLD_DIMENSIONS);

    private ArrayList<Actor> actors = new ArrayList<>();
    private HashMap<Class<?>, LinkedList<Actor>> trackedActors = new HashMap<>();
    private float globalTimer = 0;

    public boolean eFieldUpdated = false;
    public boolean bFieldUpdated = false;

    private Queue<Actor> addingActors = new LinkedList<>(), removingActors = new LinkedList<>();
    
    private static enum GameState { Initial, Paused, Unpaused, Failed, Won };
    
    private GameState gameState = GameState.Initial;
    
    private static final int BUTTON_HEIGHT = TOOLBAR_HEIGHT / 2;
    private static final int BUTTON_WIDTH = (int)(100 * Game.RELATIVE_SCALE);
    private static final int TOOLBAR_MARGIN = (int)(100 * Game.RELATIVE_SCALE);
    private static final int BUTTON_PADDING = (int)(25 * Game.RELATIVE_SCALE);
    private static final Color BUTTON_COLOR = new Color(220, 220, 220);

    private LinkedList<UIButton> buttons = new LinkedList<>();
    private UIButton playButton, resetButton, clearButton, nextLevelButton;
    
    private int attempts = 0;
    private int charges = 0;
    private int levelNum;

    private int statsXPos = TOOLBAR_MARGIN;

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

            ), "Back", BUTTON_COLOR,
            Game.instance()::popScene));

        // next level
        nextLevelButton = new UIButton(new Rectangle(
                Game.WIDTH / 2 - (int)(50 * Game.RELATIVE_SCALE),
                Game.HEIGHT / 2 + BUTTON_HEIGHT / 2,

                BUTTON_WIDTH,
                BUTTON_HEIGHT

            ), "Next", BUTTON_COLOR,
            this::nextLevel);
        nextLevelButton.setVisible(false);
        buttons.add(nextLevelButton);

        // Assets.getLevel(levelNum).loadLevel(this);

        addActor(new ChargeBag(new Rectangle(
            Game.WIDTH - (int)(175 * Game.RELATIVE_SCALE),
            (int)(25 * Game.RELATIVE_SCALE),
            (int)(150 * Game.RELATIVE_SCALE),
            (int)(75 * Game.RELATIVE_SCALE)
        )));

        /* addActor(new Wire(1, new Vector2(100, 10), new Vector2(200, 410)));
        addActor(new Wire(1, new Vector2(400, 10), new Vector2(200, 410)));
        addActor(new Wire(1, new Vector2(100, 10), new Vector2(700, 410)));
        addActor(new Wire(1, new Vector2(100, 10), new Vector2(100, 410)));
        addActor(new Wire(1, new Vector2(200, 10), new Vector2(200, 410)));
        addActor(new Wire(1, new Vector2(100, 410), new Vector2(200, 410))); */

        Wire w;
        addActor(w = new Wire(10000000000f, new Vector2(100, 410), new Vector2(200, 600)));

        addActor(new Switch(w, new Vector2(150, 750), 50));

        addActor(new Puck(Charge.ELEMENTARY_CHARGE, new Vector2(150f, 500f), Vector2.zero));

        addActor(new Wall(new Rectangle(615, 350, 20, 300)));
        addActor(new Goal(new Rectangle(975, 450, 50, 100), Goal.Orientation.Left));

        addActor(new UniformEField(new Rectangle(500, 475, 250, 175), new Vector2(-100000, -100000)));
        
        addActor(new UniformBField(new Rectangle(500, 350, 250, 175), new Vector3(0, 0, -100)));

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

            ), name, BUTTON_COLOR,
            action);
        
        buttons.add(button);
        statsXPos += BUTTON_WIDTH + BUTTON_PADDING;

        return button;
    }

    @Override
    public void update()
    {
        globalTimer += deltaTime();

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
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(0.5 * BUTTON_HEIGHT)));
        DrawUtil.drawText(g, new Vector2(
                statsXPos,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
            "Charges: " + charges, new Vector2(0, 1));
        DrawUtil.drawText(g, new Vector2(
                statsXPos,
                Game.HEIGHT - TOOLBAR_HEIGHT / 2),
            "Attempts: " + attempts, new Vector2(0, 0));

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
            "t: " + globalTimer,
            new Vector2(0, 0));
        DrawUtil.drawText(g, Vector2.unitY.mult((line++) * g.getFont().getSize()),
            "FPS: " + String.format("%.2f", (1 / Game.instance().deltaTime())),
            new Vector2(0, 0));

        if (gameState == GameState.Failed || gameState == GameState.Won)
        {
            String text = gameState == GameState.Failed ? "Collision!" : "Goal!";
            Color textColor = gameState == GameState.Failed ? Color.RED : new Color(38, 173, 75);

            g.setFont(new Font("Monospaced", Font.PLAIN, (int)(100 * Game.RELATIVE_SCALE)));
            g.setColor(textColor);

            DrawUtil.drawText(g, new Vector2(Game.WIDTH / 2, Game.HEIGHT / 2), text, new Vector2(0.5f, 1));
        }

        //#endregion
    }

    public float globalTimer()
    {
        return globalTimer;
    }

    public float deltaTime()
    {
        return getPaused() ? 0 : 1 / Game.UpdateFPS;
    }

    public void addActor(Actor actor)
    {
        if (actors.contains(actor) || addingActors.contains(actor))
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

            for (Class<?> type : trackedActors.keySet())
            {
                if (type.isAssignableFrom(actor.getClass()))
                    trackedActors.get(type).add(actor);
            }
        }
    }

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
            globalTimer = 0;

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

        if (gameState == GameState.Unpaused)
            playButton.setText("Pause");
        else
            playButton.setText("Play");
    }

    public boolean getPaused()
    {
        return gameState == GameState.Paused
            || gameState == GameState.Failed || gameState == GameState.Won;
    }

    public void togglePaused()
    {
        if (getPaused() || !gameStarted())
            setPaused(false);
        else
            setPaused(true);
    }

    public void setPaused(boolean paused)
    {
        if (gameState != GameState.Failed && gameState != GameState.Won)
            setState(paused ? GameState.Paused : GameState.Unpaused);
    }

    public boolean gameStarted()
    {
        return gameState != GameState.Initial;
    }

    public void reset()
    {
        globalTimer = 0;
        setState(GameState.Initial);
        attempts++;

        for (RequireReset p : getActorsOfType(RequireReset.class))
        {
            p.reset();
        }
    }

    public void clearCharges()
    {
        if (gameStarted())
            return;

        for (Charge c : getActorsOfType(Charge.class))
        {
            if (!c.isFixed())
                removeActor(c);
        }
    }

    public void levelComplete()
    {
        if (gameState != GameState.Failed)
        {
            setState(GameState.Won);

            if (Assets.getLevel(levelNum + 1) != null)
                nextLevelButton.setVisible(true);
        }
    }

    private void nextLevel()
    {
        Game.instance().switchScene(new WorldScene(levelNum + 1));
    }

    public void levelFail()
    {
        if (gameState != GameState.Won)
            setState(GameState.Failed);
    }

    public static Vector2 worldToScreenPoint(Vector2 point)
    {
        return point.withY(WORLD_DIMENSIONS.y() - point.y()).mult(SCALE_FACTOR);
    }

    public static Vector2 worldToScreenVector(Vector2 vector)
    {
        return vector.withY(-vector.y()).mult(SCALE_FACTOR);
    }

    public static Vector2 screenToWorldPoint(Vector2 point)
    {
        return point.withY(FIELD_HEIGHT - point.y()).div(SCALE_FACTOR);
    }

    public static Vector2 screenToWorldVector(Vector2 vector)
    {
        return vector.withY(- vector.y()).div(SCALE_FACTOR);
    }
}