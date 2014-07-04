package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import rs.papltd.smc.*;
import rs.papltd.smc.utility.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import rs.papltd.smc.model.enemy.Enemy.*;

/**
 * Created by pedja on 18.5.14..
 */
public class Furball extends Enemy
{

	@Override
	public BodyDef.BodyType getBodyType()
	{
		return BodyDef.BodyType.DynamicBody;
	}

    public static final float VELOCITY = 3f;

    public Furball(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = atlas.getRegions();//new Array<TextureAtlas.AtlasRegion>();


        Assets.animations.put(textureAtlas, new Animation(0.25f, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, body.getPosition().x - bounds.width / 2, body.getPosition().y - bounds.height / 2, bounds.height);
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        Vector2 position = body.getPosition();
        Vector2 velocity = body.getLinearVelocity();

		switch(direction)
		{
			case right:
				body.setLinearVelocity(velocity.x =+((Constants.CAMERA_WIDTH - position.x)/VELOCITY), velocity.y);
				break;
			case left:
				body.setLinearVelocity(velocity.x =-((Constants.CAMERA_WIDTH - position.x)/VELOCITY), velocity.y);
				break;
		}
    }

	@Override
	public void handleCollision(Enemy.ContactType contactType)
	{
		switch(contactType)
		{
			case stopper:
				direction = direction == Direction.right ? Direction.left : Direction.right;
				break;
		}
	}
}
