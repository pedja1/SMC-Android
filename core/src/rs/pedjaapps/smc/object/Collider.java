package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by pedja on 3.1.16..
 */
public class Collider extends Sprite
{
    public Collider(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height, null);
        type = Type.massive;
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        //do nothing
    }

    @Override
    public void _update(float delta)
    {
        //do nothing
    }

    @Override
    public void initAssets()
    {
        //do nothing
    }
}
