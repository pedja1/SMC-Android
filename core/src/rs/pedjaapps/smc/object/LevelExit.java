package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by pedja on 10.4.15..
 */
public class LevelExit extends GameObject
{
    public int type, cameraMotion;
    public String direction, levelName, entry;
    public LevelExit(World world, Vector2 size, Vector3 position)
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
