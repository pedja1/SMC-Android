package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by pedja on 10.4.15..
 */
public class LevelEntry extends GameObject
{
    public String direction, name;
    public int type;
    public LevelEntry(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        //invisible
    }

    @Override
    public void update(float delta)
    {
        //invisible
    }

    @Override
    public void loadTextures()
    {
        //invisible
    }
}
