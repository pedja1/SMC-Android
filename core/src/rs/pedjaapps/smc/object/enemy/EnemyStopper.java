package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.World;

public class EnemyStopper extends GameObject
{
	public EnemyStopper(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    @Override
	public void render(SpriteBatch spriteBatch)
	{
		// this object is invisible
	}

    @Override
    public void update(float delta)
    {

    }

    @Override
	public void loadTextures()
	{
		// TODO: Implement this method
	}
	
}
