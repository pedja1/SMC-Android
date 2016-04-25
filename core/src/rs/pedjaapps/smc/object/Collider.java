package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by pedja on 3.1.16..
 */
public class Collider extends Sprite
{
    public Collider(float x, float y, float width, float height)
    {
        super(x, y, width, height);
        type = Type.massive;
    }

    public Collider()
    {

    }

    @Override
    protected void _render(SpriteBatch spriteBatch)
    {
        //do nothing
    }

    @Override
    protected void _update(float delta)
    {
        //do nothing
    }

    @Override
    public void initAssets()
    {
        //do nothing
    }
}
