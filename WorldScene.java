
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
    private ArrayList<Actor> actors = new ArrayList<>();
    private HashMap<Class<?>, LinkedList<Actor>> trackedActors = new HashMap<>();
    private float globalTimer = 0;

    private Queue<Actor> addingActors = new LinkedList<>(), removingActors = new LinkedList<>();
    
    private static enum GameState { Initial, Paused, Unpaused, Failed, Won };
    
    private GameState gameState = GameState.Initial;
    
    private static final Color BUTTON_COLOR = new Color(220, 220, 220);

    private LinkedList<UIButton> buttons = new LinkedList<>();
    private UIButton playButton, nextLevelButton;
    
    private int attempts = 0;
    private int charges = 0;
    private int levelNum;

    public WorldScene(int levelNum)
    {
        this.levelNum = levelNum;

        // play
        playButton = new UIButton(new Rectangle(
                (int)(200 * Game.RELATIVE_SCALE),
                Game.HEIGHT - (int)(125 * Game.RELATIVE_SCALE),

                (int)(100 * Game.RELATIVE_SCALE),
                (int)(50 * Game.RELATIVE_SCALE)

            ), "Play", BUTTON_COLOR,
            this::togglePaused);
        buttons.add(playButton);

        // reset
        buttons.add(new UIButton(new Rectangle(
                (int)(325 * Game.RELATIVE_SCALE),
                Game.HEIGHT - (int)(125 * Game.RELATIVE_SCALE),

                (int)(100 * Game.RELATIVE_SCALE),
                (int)(50 * Game.RELATIVE_SCALE)

            ), "Reset", BUTTON_COLOR,
            this::reset));

        // clear
        buttons.add(new UIButton(new Rectangle(
                (int)(450 * Game.RELATIVE_SCALE),
                Game.HEIGHT - (int)(125 * Game.RELATIVE_SCALE),

                (int)(100 * Game.RELATIVE_SCALE),
                (int)(50 * Game.RELATIVE_SCALE)

            ), "Clear", BUTTON_COLOR,
            this::clearCharges));

        // back
        buttons.add(new UIButton(new Rectangle(
                (int)(20 * Game.RELATIVE_SCALE),
                (int)(25 * Game.RELATIVE_SCALE),

                (int)(100 * Game.RELATIVE_SCALE),
                (int)(50 * Game.RELATIVE_SCALE)

            ), "Back", BUTTON_COLOR,
            Game.instance()::popScene));

        // next level
        nextLevelButton = new UIButton(new Rectangle(
                Game.WIDTH / 2 - (int)(50 * Game.RELATIVE_SCALE),
                Game.HEIGHT / 2 + (int)(25 * Game.RELATIVE_SCALE),

                (int)(100 * Game.RELATIVE_SCALE),
                (int)(50 * Game.RELATIVE_SCALE)

            ), "Next", BUTTON_COLOR,
            this::nextLevel);
        nextLevelButton.setVisible(false);
        buttons.add(nextLevelButton);

        Assets.getLevel(levelNum).loadLevel(this);

        addActor(new ChargeBag(new Rectangle(
            Game.WIDTH - (int)(175 * Game.RELATIVE_SCALE),
            (int)(25 * Game.RELATIVE_SCALE),
            (int)(150 * Game.RELATIVE_SCALE),
            (int)(75 * Game.RELATIVE_SCALE)
        )));

        processActorChanges();
    }

    @Override
    public void update()
    {
        globalTimer += deltaTime();

        if (Game.instance().mouseClicked())
        {
            for (UIButton b : buttons)
                if (b.mouseOver(Game.instance().mousePos()))
                    b.click();
        }

        for (Actor a : actors)
            a.update();
        for (Actor a : actors)
            a.lateUpdate();

        processActorChanges();
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setBackground(Color.WHITE);

        for (Actor a : actors)
            a.render(g);

        //#region UI

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, Game.HEIGHT - (int)(200 * Game.RELATIVE_SCALE), Game.WIDTH, (int)(200 * Game.RELATIVE_SCALE));

        for (UIButton b : buttons)
            b.render(g);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, (int)(30 * Game.RELATIVE_SCALE)));
        DrawUtil.drawText(g, new Vector2(
                700 * Game.RELATIVE_SCALE,
                Game.HEIGHT - 100 * Game.RELATIVE_SCALE),
            "Charges: " + charges, new Vector2(0, 1));
        DrawUtil.drawText(g, new Vector2(
                700 * Game.RELATIVE_SCALE,
                Game.HEIGHT - 100 * Game.RELATIVE_SCALE),
            "Attempts: " + attempts, new Vector2(0, 0));

        DrawUtil.drawText(g, Vector2.zero, Game.instance().mousePos().toString(), new Vector2(0, 0));

        if (gameState == GameState.Failed || gameState == GameState.Won)
        {
            String text = gameState == GameState.Failed ? "Collision!" : "Goal!";
            Color textColor = gameState == GameState.Failed ? Color.RED : Color.GREEN;

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
        return Game.instance().deltaTime();
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

        gameState = state;
        
        if (gameState != GameState.Won)
            nextLevelButton.setVisible(false);

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

            try
            {
                Assets.getLevel(levelNum + 1);

                nextLevelButton.setVisible(true);
            }
            catch (Exception e)
            {}
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
}