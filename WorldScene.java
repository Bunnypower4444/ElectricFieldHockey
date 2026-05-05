public class WorldScene {

    private ArrayList<Actor> actors;
    private HashMap<Class, Actor> trackedActors;
    private float globalTimer;
    public boolean paused;

    public WorldScene(Level lever){}

    public void update(){}
    public void render(Graphics2D g){}
    public float globalTimer(){}
    public float deltaTime(){}
    public void addActor(){}
    public void removeActor(){}
    public LinkedList<Actor> getActorsOfType(Class c){}
    public void reset(){}
    public void levelComplete(){}
    public void levelFail(){}
    public void loadLevel(Level level){}


}