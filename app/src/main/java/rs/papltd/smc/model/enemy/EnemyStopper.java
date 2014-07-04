package rs.papltd.smc.model.enemy;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;

public class EnemyStopper extends Enemy // not exactly an enemy, but...
{
	public EnemyStopper(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
    }

	@Override
	public void render(SpriteBatch spriteBatch)
	{
		// thus object is invisible
	}

	@Override
	public void loadTextures()
	{
		// TODO: Implement this method
	}

	@Override
	public BodyDef.BodyType getBodyType()
	{
		return BodyDef.BodyType.StaticBody;
	}

	@Override
	public void handleCollision(Enemy.ContactType ContactType)
	{
		// TODO: Implement this method
	}
	
}
