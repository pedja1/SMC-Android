package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by pedja on 10.4.15..
 */
public class LevelExit extends GameObject
{
	public static final int LEVEL_EXIT_BEAM = 0;	// no animation ( f.e. a door or hole )
	public static final int LEVEL_EXIT_WARP = 1;	// rotated player moves slowly into the destination direction

    public int type, cameraMotion;
    public String direction, levelName, entry;
    public LevelExit(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        //invisible
    }

    @Override
    public void _update(float delta)
    {
        //invisible
    }

    @Override
    public void initAssets()
    {
        //invisible
    }

    @Override
    public void dispose()
    {

    }
}
