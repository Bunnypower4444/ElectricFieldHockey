
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 
 */
public class WorldScene extends Scene
{
    private ArrayList<Actor> actors = new ArrayList<>();
    private HashMap<Class<?>, LinkedList<Actor>> trackedActors = new HashMap<>();
    private float globalTimer = 0;

    private static enum GameState { Initial, Paused, Unpaused, Failed, Won };

    private GameState gameState = GameState.Initial;

    private UIButton playButton, resetButton;

    public WorldScene(Level level)
    {
        level.loadLevel(this);

        playButton = new UIButton(new Rectangle(
            (int)(200 * Game.RELATIVE_SCALE),
            Game.HEIGHT - (int)(125 * Game.RELATIVE_SCALE),
            (int)(350 * Game.RELATIVE_SCALE),
            Game.HEIGHT - (int)(75 * Game.RELATIVE_SCALE)
            ), "Play", Color.GRAY,
            () -> { setPaused(!getPaused()); });

        resetButton = new UIButton(new Rectangle(
            (int)(400 * Game.RELATIVE_SCALE),
            Game.HEIGHT - (int)(125 * Game.RELATIVE_SCALE),
            (int)(550 * Game.RELATIVE_SCALE),
            Game.HEIGHT - (int)(75 * Game.RELATIVE_SCALE)
            ), "Reset", Color.GRAY,
            this::reset);
    }

    @Override
    public void update()
    {
        globalTimer += deltaTime();

        if (Game.instance().mouseClicked())
        {
            if (playButton.mouseOver(Game.instance().mousePos()))
                playButton.click();
            if (resetButton.mouseOver(Game.instance().mousePos()))
                resetButton.click();
        }

        for (Actor a : actors)
            a.update();
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

        playButton.render(g);
        resetButton.render(g);

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
        if (actors.contains(actor))
            throw new IllegalStateException("Actor is already added to the WorldScene");
        
        int index = Collections.binarySearch(actors, actor, (a, b) -> a.getZIndex() - b.getZIndex());
        index = -(index + 1);

        actors.add(index, actor);
        actor.addToWorld(this);

        for (Class<?> type : trackedActors.keySet())
        {
            if (type.isAssignableFrom(actor.getClass()))
                trackedActors.get(type).add(actor);
        }
    }

    public void removeActor(Actor actor)
    {
        actors.remove(actor);
        actor.removeFromWorld();

        for (Class<?> type : trackedActors.keySet())
        {
            if (type.isAssignableFrom(actor.getClass()))
                trackedActors.get(type).remove(actor);
        }
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

    public boolean getPaused()
    {
        return gameState == GameState.Paused
            || gameState == GameState.Failed || gameState == GameState.Won;
    }

    public void setPaused(boolean paused)
    {
        switch (gameState)
        {
            case Initial:
                if (!paused)
                    gameState = GameState.Unpaused;
                break;
            
            case Unpaused:
            case Paused:
                gameState = paused ? GameState.Paused : GameState.Unpaused;
                if (paused)
                    playButton.setText("Play");
                else
                    playButton.setText("Pause");
                break;
            
            default:
                break;
        }
    }

    public boolean gameStarted()
    {
        return gameState != GameState.Initial;
    }

    public void reset()
    {
        globalTimer = 0;
        gameState = GameState.Initial;

        for (RequireReset p : getActorsOfType(RequireReset.class))
        {
            p.reset();
        }
    }

    public void levelComplete()
    {
        if (gameState != GameState.Failed)
            gameState = GameState.Won;
    }

    public void levelFail()
    {
        if (gameState != GameState.Won)
            gameState = GameState.Failed;
    }

    public void loadLevel(Level level)
    {
        level.loadLevel(this);
    }
}