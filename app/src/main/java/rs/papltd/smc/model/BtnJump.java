package rs.papltd.smc.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by pedja on 1/31/14.
 */
public class BtnJump
{
    Vector2 position;
    Rectangle bounds;
    boolean clicked = false;

    public BtnJump(float height, Vector2 position)
    {
        this.position = position;
        bounds = new Rectangle(position.x, position.y, height, height);//button is square
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

    public boolean isClicked()
    {
        return clicked;
    }

    public void setClicked(boolean clicked)
    {
        this.clicked = clicked;
    }
}
