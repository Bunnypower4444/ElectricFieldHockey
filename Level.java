import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;


/**
 * 
 */
public class Level {
    private HashMap<String, String>[] objects;

    public Level(Stream data)
    {

    }
    //Loads the given world scene with a level
    public void loadLevel(WorldScene world)
    {
        for(HashMap<String, String> obj : objects){
            Actor actor = createActor(obj);
            world.addActor(actor);
        }
        
    }
   // Creates the actors in the world scene
    private Actor createActor(HashMap<String, String> attrs) {
        String objType = attrs.get("Type");
        switch(objType){
            case "Puck":{
                float charge = Float.parseFloat(attrs.get("Charge"));
                float x = Float.parseFloat(attrs.get("X"));
                float y = Float.parseFloat(attrs.get("Y"));
                Vector2 pos = new Vector2(x, y);
                return(new Puck(charge, pos));
            }

            case "Goal":{
                int x = Integer.parseInt(attrs.get("X"));
                int y = Integer.parseInt(attrs.get("Y"));
                int w = Integer.parseInt(attrs.get("W"));
                int h = Integer.parseInt(attrs.get("H"));
                Rectangle rec = new Rectangle(x, y, w, h);
                return(new Goal(rec));
            }

            case "Wall":{
                int x = Integer.parseInt(attrs.get("X"));
                int y = Integer.parseInt(attrs.get("Y"));
                int w = Integer.parseInt(attrs.get("W"));
                int h = Integer.parseInt(attrs.get("H"));
                Rectangle rec = new Rectangle(x, y, w, h);
                return(new Wall(rec));

            }
            case "Wire":{
                float c = Float.parseFloat(attrs.get("C"));
                float x1 = Float.parseFloat(attrs.get("X1"));
                float y1 = Float.parseFloat(attrs.get("Y1"));
                Vector2 start = new Vector2(x1, y1);
                float x2 = Float.parseFloat(attrs.get("X2"));
                float y2 = Float.parseFloat(attrs.get("Y2"));
                Vector2 end = new Vector2(x2, y2);
                return(new Wire(c, start, end));

            }
        }
        return(null);
   
    }

}
