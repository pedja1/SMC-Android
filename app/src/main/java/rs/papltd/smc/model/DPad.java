package rs.papltd.smc.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by pedja on 1/31/14.
 */
public class DPad
{
    Vector2 position;
    Rectangle bounds;
    CLICKED_AREA clickedArea = CLICKED_AREA.NONE;//nothing is clicked by default

    public enum CLICKED_AREA
    {
        NONE, RIGHT, LEFT, TOP, BOTTOM
    }

    public DPad(float height)
    {
        this.position = new Vector2();
        bounds = new Rectangle(0, 0, height, height);//controller is square
    }

    public Vector2 getPosition()
    {
        return position;
    }

    public void setPosition(Vector2 position)
    {
        this.position = position;
    }

    public Rectangle getBounds()
    {
        return bounds;
    }

    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    public CLICKED_AREA getClickedArea()
    {
        return clickedArea;
    }

    public void setClickedArea(CLICKED_AREA clickedArea)
    {
        this.clickedArea = clickedArea;
    }
}
