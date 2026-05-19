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

    public loadLevel(WorldScene world)
    {
        for(HashMap<String, String> obj : objects){
            Actor actor = createActor(obj);
            world.addActor(actor);
        }
        
    }

    private Actor createActor(HashMap<String, String> attrs) {
        String objType = attrs.get("Type");
        switch(objType){
            case "Puck":{
                float charge = Float.parseFloat(attrs.get("Charge"));
                float x = Float.parseFloat(attrs.get("X"));
                float y = Float.parseFloat(attrs.get("Y"));
                Vector2 pos = new Vector2(x, y);
                return(new Puck(charge, pos));
                break;
            }

            case "Goal":{
                int x = Integer.parseFloat(attrs.get("X"));
                int y = Integer.parseFloat(attrs.get("Y"));
                int w = Integer.parseFloat(attrs.get("W"));
                int h = Integer.parseFloat(attrs.get("H"));
                Rectangle rec = new Rectangle(x, y, w, h);
                return(new Goal(rec));
            }
        }
        
    }

}
