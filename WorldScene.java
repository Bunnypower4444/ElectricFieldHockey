
import java.awt.Graphics2D;
import java.util.ArrayList;
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
        
    }

    @Override
    public void update()
    {

    }

    @Override
    public void render(Graphics2D g)
    {

    }

    public float globalTimer()
    {
        return globalTimer;
    }

    public float deltaTime()
    {
        return Game.instance().deltaTime();
    }

    public void addActor()
    {
        
    }

    public void removeActor(Actor actor)
    {

    }

    public LinkedList<Actor> getActorsOfType(Class<Actor> c)
    {

    }

    public void reset()
    {

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