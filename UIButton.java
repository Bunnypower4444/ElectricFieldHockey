
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * A utility class that allows for easily creating, updating,
 * and rendering responsive clickable buttons.
 * 
 * @author Evan Guo
 * @version 5/25/26
 */
public class UIButton
{
    private Rectangle bounds;
    private String text;
    private Runnable action;
    private boolean visible = true;
    private boolean enabled = true;

    private static enum PressedState { Depressed, Hover, Pressed, Disabled }
    private PressedState state = PressedState.Depressed;

    private static final Color BUTTON_COLOR = new Color(220, 220, 220);

    /**
     * Creates a new UIButton with the specified bounds, text label,
     * and <code>Runnable</code> action to be invoked when clicked.
     * @param bounds The rectangular bounds of the button
     * @param text The text label to be displayed on the button
     * @param action The action to be run when the button is clicked
     */
    public UIButton(Rectangle bounds, String text, Runnable action)
    {
        this.bounds = bounds;
        this.text = text;
        this.action = action;
    }

    private boolean positionInBounds(Vector2 mousePos)
    {
        return bounds.contains(mousePos.toPoint());
    }

    /**
     * Updates the state of the button for the update tick. This includes
     * detecting whether the mouse is hovering over or pressing down on
     * the button (which causes the button to be rendered differently),
     * and invoking the button's associated action when clicked.
     */
    public void update()
    {
        if (!visible)
            return;

        if (!enabled)
        {
            state = PressedState.Disabled;
            return;
        }

        if (!positionInBounds(Game.instance().mousePos()))
            state = PressedState.Depressed;
        else if (Game.instance().consumeClick())
        {
            state = PressedState.Depressed;
            action.run();
        }
        else if (Game.instance().mouseDown())
            state = PressedState.Pressed;
        else
            state = PressedState.Hover;
    }

    /**
     * Gets the text label on the button.
     * @return The text label
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text label on the button
     * @param text The new text label
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Gets whether or not the button is visible and will be rendered
     * @return true if the button is visible; false if the button
     * is hidden
     */
    public boolean getVisible()
    {
        return visible;
    }

    /**
     * Sets the visibility state of the button. If the
     * button is turned invisible, it will not be drawn
     * to the screen when the <code>render()</code> method is called,
     * and it will also not detect clicks in <code>update</code>.
     * @param visible true if the button should visible; false if
     * the button should be hidden
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Gets whether the button is currently enabled and can be
     * clicked.
     * @return true if the button is enabled; false if the button
     * is disabled
     */
    public boolean getEnabled()
    {
        return enabled;
    }

    /**
     * Enables or disabled the button. Disabled buttons will
     * not be able to be clicked, and they will always be
     * rendered to look faded out.
     * @param enabled true if the button should be enabled; false if the
     * button should be disabled
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Draws the button to the specified Graphics2D object.
     * If the button's visibility is set to false, it will not be drawn.
     * @param g The Graphics2D to which to render the button
     */
    public void render(Graphics2D g)
    {
        if (!visible)
            return;

        Color prevColor = g.getColor();
        Stroke prevStroke = g.getStroke();

        Color c;
        switch (state)
        {
            case Pressed:
                c = BUTTON_COLOR.darker();
                break;
            default:
                c = BUTTON_COLOR;
                break;
        }

        g.setColor(c);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        if (state == PressedState.Hover || state == PressedState.Pressed)
        {
            g.setColor(Color.GRAY);
            g.setStroke(new BasicStroke(4 * Game.RELATIVE_SCALE));
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        Color strokeColor = state == PressedState.Disabled ? Color.GRAY : Color.BLACK;

        g.setColor(strokeColor);
        g.setStroke(new BasicStroke(2 * Game.RELATIVE_SCALE));
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setFont(Assets.getFont("JosefinSans", Font.ITALIC, (int)(0.5 * bounds.height)));

        DrawUtil.drawText(g, new Vector2((float)bounds.getCenterX(), (float)bounds.getCenterY()), text, new Vector2(0.5f, 0.5f));

        g.setColor(prevColor);
        g.setStroke(prevStroke);
    }
}
