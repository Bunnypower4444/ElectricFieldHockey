
import java.awt.Color;
import java.awt.Graphics2D;
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
    private HashMap<Class<Actor>, LinkedList<Actor>> trackedActors = new HashMap<>();
    private float globalTimer = 0;

    public boolean paused = true;

    public WorldScene(Level level)
    {
        level.loadLevel(this);
    }

    @Override
    public void update()
    {
        for (Actor a : actors)
            a.update();
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setBackground(Color.WHITE);

        for (Actor a : actors)
            a.render(g);
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

        for (Class<Actor> type : trackedActors.keySet())
        {
            if (type.isAssignableFrom(actor.getClass()))
                trackedActors.get(type).add(actor);
        }
    }

    public void removeActor(Actor actor)
    {
        actors.remove(actor);
        actor.removeFromWorld();

        for (Class<Actor> type : trackedActors.keySet())
        {
            if (type.isAssignableFrom(actor.getClass()))
                trackedActors.get(type).remove(actor);
        }
    }

    public LinkedList<Actor> getActorsOfType(Class<Actor> c)
    {
        if (trackedActors.keySet().contains(c))
            return trackedActors.get(c);

        LinkedList<Actor> actorsOfType = new LinkedList<>();
        for (Actor a : actors)
        {
            if (c.isAssignableFrom(a.getClass()))
                actorsOfType.add(a);
        }

        trackedActors.put(c, actorsOfType);

        return actorsOfType;
    }

    public void reset()
    {
        globalTimer = 0;
        paused = true;
    }

    public void levelComplete()
    {

    }

    public void levelFail()
    {

    }

    public void loadLevel(Level level)
    {
        level.loadLevel(this);
    }
}