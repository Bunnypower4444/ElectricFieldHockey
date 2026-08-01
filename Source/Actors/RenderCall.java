
import java.awt.Graphics2D;
import java.util.function.Consumer;

/**
 * Represents a rendering call provided by an Actor, containing a Consumer<Graphics2D> that
 * performs the desired rendering onto the screen, and a Z-index. This allows the same Actor
 * to perform multiple rendering actions, which can each have their own Z-indices.
 * @param renderFunc The rendering function that takes in a Graphics2D object as an argument
 * to render onto
 * @param zIndex The Z-index associated with the rendering call
 * 
 * @author  Evan Guo
 * @version 7/31/26
 */
public record RenderCall(Consumer<Graphics2D> renderFunc, int zIndex) implements Comparable<RenderCall>
{
    /**
     * Calls the rendering function associated with the call, providing
     * the given Graphics2D object as an argument
     * @param g The Graphics2D object to use for rendering
     */
    public void call(Graphics2D g)
    {
        renderFunc.accept(g);
    }

    @Override
    public int compareTo(RenderCall other)
    {
        return zIndex - other.zIndex;
    }
}
